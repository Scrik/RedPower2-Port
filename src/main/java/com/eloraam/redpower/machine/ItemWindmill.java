package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemWindmill extends Item {
	
	public int windmillType;
	
	public ItemWindmill(int tp) {
		this.setTextureName("rpmachine:itemWindTurbine");
		this.setMaxStackSize(1);
		this.setMaxDamage(1000);
		this.setUnlocalizedName("windTurbineWood");
		this.windmillType = tp;
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		list.add(new ItemStack(this, 1, 0));
	}
	
	public boolean canFaceDirection(int dir) {
		switch (this.windmillType) {
			case 1:
				return dir == 0;
			case 2:
				return dir > 1;
			default:
				return false;
		}
	}
	
	public ItemStack getBrokenItem() {
		switch (this.windmillType) {
			case 1:
				return new ItemStack(RedPowerBase.blockMicro, 3, 5905);
			case 2:
				return new ItemStack(RedPowerBase.blockMicro, 1, 5905);
			default:
				return null;
		}
	}
}
