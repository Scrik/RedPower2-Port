package com.eloraam.redpower.world;

import com.eloraam.redpower.world.ItemSeedBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSeedBag extends Container {
	
	ItemStack itemBag;
	IInventory baginv;
	
	public ContainerSeedBag(InventoryPlayer inv, IInventory bag, ItemStack ist) {
		this.baginv = bag;
		
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new ContainerSeedBag.SlotSeeds(bag, j
						+ i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
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
			if (!ItemSeedBag.canAdd(this.baginv, itemstack1)) {
				return null;
			}
			
			itemstack = itemstack1.copy();
			if (i < 9) {
				if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
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
	
	public static class SlotSeeds extends Slot {
		
		public SlotSeeds(IInventory inv, int par2, int par3, int par4) {
			super(inv, par2, par3, par4);
		}
		
		@Override
		public boolean isItemValid(ItemStack ist) {
			return ItemSeedBag.canAdd(super.inventory, ist);
		}
	}
}
