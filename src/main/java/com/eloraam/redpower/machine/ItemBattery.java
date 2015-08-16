package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.IChargeable;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBattery extends Item {
	
	public ItemBattery() {
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setMaxDamage(1500);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setTextureName("rpmachine:itemBattery");
		this.setUnlocalizedName("btbattery");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		for (int i = 0; i < 9; ++i) {
			ItemStack i1 = player.inventory.getStackInSlot(i);
			if (i1 != null && i1.getItem() instanceof IChargeable && i1.getItemDamage() > 1) {
				int d = Math.min(i1.getItemDamage() - 1, ist.getMaxDamage() - ist.getItemDamage());
				d = Math.min(d, 25);
				ist.setItemDamage(ist.getItemDamage() + d);
				i1.setItemDamage(i1.getItemDamage() - d);
				player.inventory.markDirty();
				if (ist.getItemDamage() == ist.getMaxDamage()) {
					return new ItemStack(RedPowerMachine.itemBatteryEmpty, 1);
				}
				break;
			}
		}
		return ist;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		itemList.add(new ItemStack(this, 1, 1));
	}
}
