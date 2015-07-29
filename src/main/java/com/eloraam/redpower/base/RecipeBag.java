package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.ItemBag;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeBag implements IRecipe {
	
	@Override
	public int getRecipeSize() {
		return 2;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(RedPowerBase.itemBag, 1, 0);
	}
	
	private ItemStack findResult(InventoryCrafting inv) {
		ItemStack bag = null;
		int color = -1;
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				ItemStack ist = inv.getStackInRowAndColumn(i, j);
				if (ist != null) {
					if (ist.getItem() instanceof ItemBag) {
						if (bag != null) {
							return null;
						}
						
						bag = ist;
					} else {
						if (ist.getItem() != Items.dye) {
							return null;
						}
						
						if (color >= 0) {
							return null;
						}
						
						color = 15 - ist.getItemDamage();
					}
				}
			}
		}
		
		if (bag != null && color >= 0) {
			if (bag.getItemDamage() == color) {
				return null;
			} else {
				bag = bag.copy();
				bag.setItemDamage(color);
				return bag;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return this.findResult(inv).copy();
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		return this.findResult(inv) != null;
	}
}
