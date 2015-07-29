package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.ItemExtended;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMachinePanel extends ItemExtended {
	
	public ItemMachinePanel(Block block) {
		super(block);
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xp, float yp, float zp) {
		Block bid = world.getBlock(x, y, z);
		Block block = Block.getBlockFromItem(this);
		//int blockID = this.getBlockID();
		if (bid == Blocks.snow) {
			side = 1;
		} else if (bid != Blocks.vine && bid != Blocks.tallgrass && bid != Blocks.deadbush) {
			switch (side) {
				case 0:
					--y;
					break;
				case 1:
					++y;
					break;
				case 2:
					--z;
					break;
				case 3:
					++z;
					break;
				case 4:
					--x;
					break;
				default:
					++x;
			}
		}
		
		if (ist.stackSize == 0) {
			return false;
		} else if (!player.canPlayerEdit(x, y, z, side, ist)) {
			return false;
		} else if (y >= world.getHeight() - 1) {
			return false;
		} else if (!world.canPlaceEntityOnSide(world.getBlock(x, y, z), x, y, z, false, side, player, ist)) { //Replace block to itself
			return false;
		} else if (ist.getItemDamage() == 0
				&& !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
			return false;
		} else {
			if (world.setBlock(x, y, z, block, this.getMetadata(ist.getItemDamage()), 3)) {
				if (world.getBlock(x, y, z) == block) {
					BlockExtended bex = (BlockExtended)block;
					bex.onBlockPlacedBy(world, x, y, z/*, side*/, player, ist); //TODO: Removed sibe becuse of its calculating from player yaw
				}
				
				world.playSoundEffect((double) (x + 0.5F), (double) (y + 0.5F),
						(double) (z + 0.5F), bid.stepSound.getStepResourcePath(), //TODO: Or place sound
						(bid.stepSound.getVolume() + 1.0F) / 2.0F,
						bid.stepSound.getPitch() * 0.8F);
				--ist.stackSize;
			}
			
			return true;
		}
	}
}
