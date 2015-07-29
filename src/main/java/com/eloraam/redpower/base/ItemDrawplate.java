package com.eloraam.redpower.base;

import com.eloraam.redpower.core.ItemPartialCraft;

import net.minecraft.creativetab.CreativeTabs;

public class ItemDrawplate extends ItemPartialCraft {
	
	public ItemDrawplate() {
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
