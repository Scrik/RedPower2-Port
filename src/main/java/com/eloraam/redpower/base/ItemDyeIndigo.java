package com.eloraam.redpower.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDyeIndigo extends Item {
	
	public ItemDyeIndigo() {
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("dyeIndigo");
		this.setTextureName("rpbase:itemDyeIndigo");
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	public void useItemOnEntity(ItemStack itemstack, EntityLiving entityliving) {
		if (itemstack.getItemDamage() == 0) {
			if (entityliving instanceof EntitySheep) {
				EntitySheep entitysheep = (EntitySheep) entityliving;
				if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != 11) {
					entitysheep.setFleeceColor(11);
					--itemstack.stackSize;
				}
			}
		}
	}
}
