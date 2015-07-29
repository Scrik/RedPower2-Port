package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLogic extends ItemExtended {
	
	public ItemLogic(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
	}
	
	@Override
	public void placeNoise(World world, int i, int j, int k, Block block) {
		//Block block = Block.blocksList[bid];
		world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, "step.stone",
				(block.stepSound.getVolume() + 1.0F) / 2.0F,
				block.stepSound.getPitch() * 0.8F);
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
	
	protected boolean tryPlace(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, int down, int rot) {
		int md = ist.getItemDamage();
		Block bid = Block.getBlockFromItem(ist.getItem());
		if (!world.setBlock(i, j, k, bid, md >> 8, 3)) {
			return false;
		} else {
			TileLogic tl = (TileLogic) CoreLib.getTileEntity(world, i, j, k,
					TileLogic.class);
			if (tl == null) {
				return false;
			} else {
				tl.Rotation = down << 2 | rot;
				tl.initSubType(md & 255);
				return true;
			}
		}
	}
	
	protected boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
		switch (l) {
			case 0:
				--j;
				break;
			case 1:
				++j;
				break;
			case 2:
				--k;
				break;
			case 3:
				++k;
				break;
			case 4:
				--i;
				break;
			case 5:
				++i;
		}
		
		Block bid = Block.getBlockFromItem(ist.getItem());
		if (!world.canPlaceEntityOnSide(world.getBlock(i, j, k), i, j, k, false, l, player, ist)) { //TODO: Maybe replace block to it's own
			return false;
		} else if (!RedPowerLib.isSideNormal(world, i, j, k, l ^ 1)) {
			return false;
		} else {
			int yaw = (int) Math.floor(player.rotationYaw / 90.0F + 0.5F);
			int pitch = (int) Math.floor(player.rotationPitch / 90.0F + 0.5F);
			yaw = yaw + 1 & 3;
			int down = l ^ 1;
			int rot;
			switch (down) {
				case 0:
					rot = yaw;
					break;
				case 1:
					rot = yaw ^ (yaw & 1) << 1;
					break;
				case 2:
					rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : 1 - yaw & 3;
					break;
				case 3:
					rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : yaw - 1 & 3;
					break;
				case 4:
					rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : yaw - 2 & 3;
					break;
				case 5:
					rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : 2 - yaw & 3;
					break;
				default:
					rot = 0;
			}
			
			if (!this.tryPlace(ist, player, world, i, j, k, l, down, rot)) {
				return true;
			} else {
				this.placeNoise(world, i, j, k, bid);
				--ist.stackSize;
				world.markBlockForUpdate(i, j, k);
				//RedPowerLib.updateIndirectNeighbors(world, i, j, k, bid);
				return true;
			}
		}
	}
}
