package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.TileItemDetect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerItemDetect extends Container implements IHandleGuiEvent {
	
	private TileItemDetect tileDetect;
	byte mode;
	
	public ContainerItemDetect(IInventory inv, TileItemDetect tid) {
		this.tileDetect = tid;
		
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(tid, j + i * 3, 62 + j * 18,
						17 + i * 18));
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
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileDetect.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
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
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting) super.crafters.get(i);
			if (this.mode != this.tileDetect.mode) {
				ic.sendProgressBarUpdate(this, 0, this.tileDetect.mode);
			}
		}
		
		this.mode = this.tileDetect.mode;
	}
	
	public void func_20112_a(int i, int j) {
		this.updateProgressBar(i, j);
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		if (i == 0) {
			this.tileDetect.mode = (byte) j;
		}
		
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		if (message.eventId == 1) {
			try {
				this.tileDetect.mode = message.storedBuffer.readByte();
				this.tileDetect.markDirty();
			} catch (Throwable thr) {}
			this.detectAndSendChanges();
		}
	}
}
