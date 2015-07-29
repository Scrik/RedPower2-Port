package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.WorldCoord;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MicroPlacementTube implements IMicroPlacement {
	
	private void blockUsed(World world, WorldCoord wc, ItemStack ist) {
		--ist.stackSize;
		CoreLib.placeNoise(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
		world.markBlockForUpdate(wc.x, wc.y, wc.z);
		RedPowerLib.updateIndirectNeighbors(world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem()));
	}
	
	private boolean initialPlace(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
		int md = ist.getItemDamage() >> 8;
		Block bid = Block.getBlockFromItem(ist.getItem());
		if (!world.canPlaceEntityOnSide(world.getBlock(wc.x, wc.y, wc.z), wc.x, wc.y, wc.z, false, l, player, ist)) { //TODO: Replace block to itself
			return false;
		} else if (!world.setBlock(wc.x, wc.y, wc.z, bid, md, 3)) {
			return true;
		} else {
			this.blockUsed(world, wc, ist);
			return true;
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
				int eid = tc.getExtendedID();
				if (eid != 7 && eid != 8 && eid != 9 && eid != 10 && eid != 11) {
					if (!CoverLib.tryMakeCompatible(world, wc, Block.getBlockFromItem(ist.getItem()), ist.getItemDamage())) {
						return false;
					} else {
						this.blockUsed(world, wc, ist);
						return true;
					}
				} else {
					return false;
				}
			}
		}
	}
	
	@Override
	public String getMicroName(int hb, int lb) {
		return hb == 7 ? "tile.rppipe" : (hb == 8 ? "tile.rptube" : (hb == 9 ? "tile.rprstube" : (hb == 10 ? "tile.rprtube" : (hb == 11 ? "tile.rpmtube" : null))));
	}
	
	@Override
	public void addCreativeItems(int hb, CreativeTabs tab, List<ItemStack> itemList) {
		if (tab == CreativeExtraTabs.tabMachine) {
			if (hb == 7) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1792));
			} else if (hb == 8) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2048));
			} else if (hb == 9) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2304));
			} else if (hb == 10) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2560));
			} else if (hb == 11) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 2816));
			}
		}
	}
}
