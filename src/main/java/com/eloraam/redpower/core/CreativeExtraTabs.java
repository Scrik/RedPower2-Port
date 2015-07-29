package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeExtraTabs {
	
	public static CreativeTabs tabWires = new CreativeTabs(
			CreativeTabs.getNextID(), "RPWires") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(RedPowerBase.blockMicro, 1, 768);
		}

		@Override
		public Item getTabIconItem() {
			return null;
		}
	};
	public static CreativeTabs tabMicros = new CreativeTabs( CreativeTabs.getNextID(), "RPMicroblocks") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(RedPowerBase.blockMicro, 1, 23);
		}
		
		@Override
		public Item getTabIconItem() {
			return null;
		}
	};
	public static CreativeTabs tabMachine = new CreativeTabs( CreativeTabs.getNextID(), "RPMachines") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(RedPowerBase.blockAppliance, 1, 3);
		}
		
		@Override
		public Item getTabIconItem() {
			return null;
		}
	};
	
}
