package com.eloraam.redpower.world;

import com.eloraam.redpower.core.ItemPartialCraft;

import net.minecraft.creativetab.CreativeTabs;

public class ItemWoolCard extends ItemPartialCraft {
	
	public ItemWoolCard() {
		this.setUnlocalizedName("woolcard");
		this.setTextureName("rpworld:itemWoolCard");
		this.setMaxDamage(63);
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}
}
