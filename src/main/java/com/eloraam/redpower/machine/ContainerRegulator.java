package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.TileRegulator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRegulator extends Container implements IHandleGuiEvent {
	
	private TileRegulator tileRegulator;
	public int color = 0;
	public int mode = 0;
	
	public ContainerRegulator(IInventory inv, TileRegulator tf) {
		this.tileRegulator = tf;
		
		int i;
		int j;
		for (int k = 0; k < 3; ++k) {
			for (i = 0; i < 3; ++i) {
				for (j = 0; j < 3; ++j) {
					this.addSlotToContainer(new Slot(tf, j + i * 3 + k * 9, 8
							+ j * 18 + k * 72, 18 + i * 18));
				}
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						26 + j * 18, 86 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 26 + i * 18, 144));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileRegulator.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < 27) {
				if (!this.mergeItemStack(itemstack1, 27, 63, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 9, 18, false)) {
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
			if (this.color != this.tileRegulator.color) {
				ic.sendProgressBarUpdate(this, 0, this.tileRegulator.color);
			}
			
			if (this.mode != this.tileRegulator.mode) {
				ic.sendProgressBarUpdate(this, 1, this.tileRegulator.mode);
			}
		}
		
		this.color = this.tileRegulator.color;
		this.mode = this.tileRegulator.mode;
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileRegulator.color = (byte) j;
				break;
			case 1:
				this.tileRegulator.mode = (byte) j;
		}
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					this.tileRegulator.color = message.storedBuffer.readByte();
					this.tileRegulator.markDirty();
					break;
				case 2:
					this.tileRegulator.mode = message.storedBuffer.readByte();
					this.tileRegulator.markDirty();
					break;
				default:
					return;
			}
		} catch (Throwable thr) {}
	}
}
