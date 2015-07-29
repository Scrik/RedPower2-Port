package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.wiring.TileWiring;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MicroPlacementJacket implements IMicroPlacement {
	
	private void blockUsed(World world, WorldCoord wc, ItemStack ist) {
		--ist.stackSize;
		CoreLib.placeNoise(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
		world.markBlockForUpdate(wc.x, wc.y, wc.z);
		RedPowerLib.updateIndirectNeighbors(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
	}
	
	private int getWireMeta(int md) {
		switch (md) {
			case 64:
				return 1;
			case 65:
				return 3;
			case 66:
				return 5;
			default:
				return 0;
		}
	}
	
	private boolean initialPlace(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
		int md = ist.getItemDamage() >> 8;
		Block bid = Block.getBlockFromItem(ist.getItem());
		md = this.getWireMeta(md);
		if (!world.canPlaceEntityOnSide(bid, wc.x, wc.y, wc.z, false, l, player, null)) {
			return false;
		} else if (!world.setBlock(wc.x, wc.y, wc.z, bid, md, 3)) {
			return true;
		} else {
			TileWiring tw = (TileWiring) CoreLib.getTileEntity(world, wc,
					TileWiring.class);
			if (tw == null) {
				return false;
			} else {
				tw.CenterPost = (short) (ist.getItemDamage() & 255);
				tw.ConSides |= 64;
				this.blockUsed(world, wc, ist);
				return true;
			}
		}
	}
	
	private boolean tryAddingJacket(World world, WorldCoord wc, ItemStack ist) {
		TileWiring tw = (TileWiring) CoreLib.getTileEntity(world, wc, TileWiring.class);
		if (tw == null) {
			return false;
		} else if ((tw.ConSides & 64) > 0) {
			return false;
		} else if (!CoverLib.checkPlacement(tw.CoverSides, tw.Covers,
				tw.ConSides, true)) {
			return false;
		} else {
			tw.CenterPost = (short) (ist.getItemDamage() & 255);
			tw.ConSides |= 64;
			tw.uncache();
			this.blockUsed(world, wc, ist);
			return true;
		}
	}
	
	@Override
	public boolean onPlaceMicro(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
		int hb = ist.getItemDamage();
		//int lb = hb & 255;
		hb >>= 8;
		hb = this.getWireMeta(hb);
		int dmg = hb << 8;
		if (CoverLib.tryMakeCompatible(world, wc, Block.getBlockFromItem(ist.getItem()), dmg)
				&& this.tryAddingJacket(world, wc, ist)) {
			return true;
		} else {
			wc.step(l);
			Block bid = world.getBlock(wc.x, wc.y, wc.z);
			return bid != Block.getBlockFromItem(ist.getItem()) ? this.initialPlace(ist, player, world, wc, l) : 
				(!CoverLib.tryMakeCompatible(world, wc, Block.getBlockFromItem(ist.getItem()), dmg) ? false : 
					this.tryAddingJacket(world, wc, ist));
		}
	}
	
	@Override
	public String getMicroName(int hb, int lb) {
		String nm;
		switch (hb) {
			case 64:
				nm = CoverLib.getName(lb);
				if (nm == null) {
					return null;
				} else {
					if (CoverLib.isTransparent(lb)) {
						return null;
					}
					
					return "tile.rparmwire." + nm;
				}
			case 65:
				nm = CoverLib.getName(lb);
				if (nm == null) {
					return null;
				} else {
					if (CoverLib.isTransparent(lb)) {
						return null;
					}
					
					return "tile.rparmcable." + nm;
				}
			case 66:
				nm = CoverLib.getName(lb);
				if (nm == null) {
					return null;
				} else {
					if (CoverLib.isTransparent(lb)) {
						return null;
					}
					
					return "tile.rparmbwire." + nm;
				}
			default:
				return null;
		}
	}
	
	@Override
	public void addCreativeItems(int hb, CreativeTabs tab, List<ItemStack> itemList) {
		if (tab == CreativeExtraTabs.tabWires) {
			switch (hb) {
				case 64:
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 16386));
					break;
				case 65:
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 16666));
					break;
				case 66:
					itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 16902));
			}
		}
	}
}
