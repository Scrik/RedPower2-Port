package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPartialCraft extends Item {
	
	private ItemStack emptyItem = null;
	
	public ItemPartialCraft() {
		this.setMaxStackSize(1);
		this.setNoRepair();
	}
	
	public void setEmptyItem(ItemStack ei) {
		this.emptyItem = ei;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack ist) {
		int dmg = ist.getItemDamage();
		if (dmg == ist.getMaxDamage() && this.emptyItem != null) {
			return CoreLib.copyStack(this.emptyItem, 1);
		} else {
			ItemStack tr = CoreLib.copyStack(ist, 1);
			tr.setItemDamage(dmg + 1);
			return tr;
		}
	}
	
	@Override
	public boolean hasContainerItem() {
		return true;
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
		return false;
	}
}
