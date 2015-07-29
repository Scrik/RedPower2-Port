package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.wiring.TileWiring;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MicroPlacementWire implements IMicroPlacement {
	
	private void blockUsed(World world, WorldCoord wc, ItemStack ist) {
		--ist.stackSize;
		CoreLib.placeNoise(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
		world.markBlockForUpdate(wc.x, wc.y, wc.z);
		RedPowerLib.updateIndirectNeighbors(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
	}
	
	private boolean initialPlace(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
		int md = ist.getItemDamage() >> 8;
		Block bid = Block.getBlockFromItem(ist.getItem());
		if (!world.canPlaceEntityOnSide(bid, wc.x, wc.y, wc.z, false, l, player, null)) {
			return false;
		} else if (!RedPowerLib.canSupportWire(world, wc.x, wc.y, wc.z, l ^ 1)) {
			return false;
		} else if (!world.setBlock(wc.x, wc.y, wc.z, bid, md, 3)) {
			return true;
		} else {
			TileWiring tw = (TileWiring) CoreLib.getTileEntity(world, wc, TileWiring.class);
			if (tw == null) {
				return false;
			} else {
				tw.ConSides = 1 << (l ^ 1);
				tw.Metadata = ist.getItemDamage() & 255;
				this.blockUsed(world, wc, ist);
				return true;
			}
		}
	}
	
	@Override
	public boolean onPlaceMicro(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
		wc.step(l);
		Block bid = world.getBlock(wc.x, wc.y, wc.z);
		if (bid != Block.getBlockFromItem(ist.getItem())) {
			return this.initialPlace(ist, player, world, wc, l);
		} else {
			TileCovered tc = (TileCovered) CoreLib.getTileEntity(world, wc,
					TileCovered.class);
			if (tc == null) {
				return false;
			} else {
				int d = 1 << (l ^ 1);
				if ((tc.CoverSides & d) > 0) {
					return false;
				} else {
					int hb = ist.getItemDamage();
					//int lb = hb & 255;
					//hb >>= 8; //TODO Look on this!!!
					if (!CoverLib.tryMakeCompatible(world, wc, Block.getBlockFromItem(ist.getItem()), hb)) {
						return false;
					} else {
						TileWiring tw = (TileWiring) CoreLib.getTileEntity(world, wc, TileWiring.class);
						if (tw == null) {
							return false;
						} else if (!RedPowerLib.canSupportWire(world, wc.x, wc.y, wc.z, l ^ 1)) {
							return false;
						} else if (((tw.ConSides | tw.CoverSides) & d) > 0) {
							return false;
						} else {
							d |= tw.ConSides;
							int t = d & 63;
							if (t != 3 && t != 12 && t != 48) {
								if (!CoverLib.checkPlacement(tw.CoverSides, tw.Covers, t, (tw.ConSides & 64) > 0)) {
									return false;
								} else {
									tw.ConSides = d;
									tw.uncache();
									this.blockUsed(world, wc, ist);
									return true;
								}
							} else {
								return false;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public String getMicroName(int hb, int lb) {
		switch (hb) {
			case 1:
				if (lb == 0) {
					return "tile.rpwire";
				}
				break;
			case 2:
				return "tile.rpinsulated." + CoreLib.rawColorNames[lb];
			case 3:
				if (lb == 0) {
					return "tile.rpcable";
				}
				
				return "tile.rpcable." + CoreLib.rawColorNames[lb - 1];
			case 4:
			default:
				break;
			case 5:
				if (lb == 0) {
					return "tile.bluewire";
				}
				
				if (lb == 1) {
					return "tile.bluewire10";
				}
				
				if (lb == 2) {
					return "tile.bluewire1M";
				}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(int hb, CreativeTabs tab, List itemList) {
		if (tab == CreativeExtraTabs.tabWires) {
			int i;
			switch (hb) {
				case 1:
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 256));
					break;
				case 2:
					for (i = 0; i < 16; ++i) {
						itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 512 + i));
					}
					
					return;
				case 3:
					for (i = 0; i < 17; ++i) {
						itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 768 + i));
					}
				case 4:
				default:
					break;
				case 5:
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1280));
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1281));
			}
		}
	}
}
