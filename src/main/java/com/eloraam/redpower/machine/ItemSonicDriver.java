package com.eloraam.redpower.machine;

import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IChargeable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSonicDriver extends ItemScrewdriver implements IChargeable {
	
	public ItemSonicDriver() {
		this.setMaxDamage(400);
		this.setNoRepair();
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		return CoreLib.isClient(world) ? false : (ist.getItemDamage() == ist .getMaxDamage() ? false : super.onItemUseFirst(ist, player, world, i, j, k, l, xp, yp, zp));
	}
	
	@Override
	public int getDamageVsEntity(Entity entity) {
		return 1;
	}
	
	@Override
	public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving player) {
		return false;
	}
}
