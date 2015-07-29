package com.eloraam.redpower.base;

import com.eloraam.redpower.core.ItemPartialCraft;

import net.minecraft.creativetab.CreativeTabs;

public class ItemHandsaw extends ItemPartialCraft {
	
	private int sharp;
	
	public ItemHandsaw(int sh) {
		this.sharp = sh;
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
	
	public int getSharpness() {
		return this.sharp;
	}
	
	public String getTextureFile() {
		return "/eloraam/base/items1.png";
	}
}
