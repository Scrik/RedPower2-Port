package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBackplane extends ItemExtended {
	
	public ItemBackplane(Block block) {
		super(block);
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world,
			int i, int j, int k, int l, float xp, float yp, float zp) {
		return player.isSneaking() ? false : this.itemUseShared(ist, player,
				world, i, j, k, l);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player,
			World world, int i, int j, int k, int l, float xp, float yp,
			float zp) {
		return CoreLib.isClient(world) ? false : (!player.isSneaking() ? false : this
				.itemUseShared(ist, player, world, i, j, k, l));
	}
	
	protected boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
		Block bid = world.getBlock(i, j, k);
		int md = world.getBlockMetadata(i, j, k);
		int dmg = ist.getItemDamage();
		int rx;
		if (bid == Block.getBlockFromItem(ist.getItem()) && md == 0 && dmg != 0) {
			TileBackplane var19 = (TileBackplane) CoreLib.getTileEntity(world,
					i, j, k, TileBackplane.class);
			if (var19 == null) {
				return false;
			} else {
				rx = var19.Rotation;
				BlockMultipart.removeMultipart(world, i, j, k);
				if (!world.setBlock(i, j, k, bid, dmg, 3)) {
					return false;
				} else {
					var19 = (TileBackplane) CoreLib.getTileEntity(world, i, j,
							k, TileBackplane.class);
					if (var19 != null) {
						var19.Rotation = rx;
					}
					
					world.markBlockForUpdate(i, j, k);
					CoreLib.placeNoise(world, i, j, k, Block.getBlockFromItem(ist.getItem()));
					--ist.stackSize;
					RedPowerLib.updateIndirectNeighbors(world, i, j, k, Block.getBlockFromItem(ist.getItem()));
					return true;
				}
			}
		} else if (dmg != 0) {
			return false;
		} else {
			WorldCoord wc = new WorldCoord(i, j, k);
			wc.step(l);
			if (!world.canPlaceEntityOnSide(world.getBlock(wc.x, wc.y, wc.z), wc.x, wc.y, wc.z, false, 1, player, ist)) { //Maybe replace first param to Block.getBlockFromItem(ist.getItem())
				return false;
			} else if (!RedPowerLib.isSideNormal(world, wc.x, wc.y, wc.z, 0)) {
				return false;
			} else {
				rx = -1;
				
				label77: for (int tb = 0; tb < 4; ++tb) {
					WorldCoord wc2 = wc.copy();
					int dir = CoreLib.rotToSide(tb) ^ 1;
					wc2.step(dir);
					TileCPU tcpu = (TileCPU) CoreLib.getTileEntity(world, wc2,
							TileCPU.class);
					if (tcpu != null && tcpu.Rotation == tb) {
						rx = tb;
						break;
					}
					
					TileBackplane tb1 = (TileBackplane) CoreLib.getTileEntity(
							world, wc2, TileBackplane.class);
					if (tb1 != null && tb1.Rotation == tb) {
						for (int pb = 0; pb < 6; ++pb) {
							wc2.step(dir);
							if (world.getBlock(wc2.x, wc2.y, wc2.z) == RedPowerControl.blockPeripheral
									&& world.getBlockMetadata(wc2.x, wc2.y,
											wc2.z) == 1) {
								rx = tb;
								break label77;
							}
						}
					}
				}
				
				if (rx < 0) {
					return false;
				} else if (!world.setBlock(wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()), dmg, 3)) {
					return true;
				} else {
					TileBackplane var20 = (TileBackplane) CoreLib
							.getTileEntity(world, wc, TileBackplane.class);
					var20.Rotation = rx;
					CoreLib.placeNoise(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
					--ist.stackSize;
					world.markBlockForUpdate(wc.x, wc.y, wc.z);
					RedPowerLib.updateIndirectNeighbors(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
					return true;
				}
			}
		}
	}
}
