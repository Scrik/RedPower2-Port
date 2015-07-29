package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ContainerBag;
import com.eloraam.redpower.base.ItemBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBag extends Container {
	
	ItemStack itemBag;
	
	public ContainerBag(InventoryPlayer inv, IInventory bag, ItemStack ist) {
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new ContainerBag.SlotBag(bag,
						j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						8 + j * 18, 86 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 144));
		}
		
		this.itemBag = ist;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.inventory.getCurrentItem() == this.itemBag;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1.getItem() instanceof ItemBag) {
				return null;
			}
			
			itemstack = itemstack1.copy();
			if (i < 27) {
				if (!this.mergeItemStack(itemstack1, 27, 63, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
				return null;
			}
			
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			
			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			
			slot.onPickupFromSlot(player, itemstack1);
		}
		
		return itemstack;
	}
	
	public static class SlotBag extends Slot {
		public SlotBag(IInventory inv, int par2, int par3, int par4) {
			super(inv, par2, par3, par4);
		}
		
		@Override
		public boolean isItemValid(ItemStack ist) {
			return !(ist.getItem() instanceof ItemBag);
		}
	}
}
