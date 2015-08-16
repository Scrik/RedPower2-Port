package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.TileAssemble;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAssemble extends Container implements IHandleGuiEvent {
	
	private TileAssemble tileAssemble;
	public int mode = 0;
	public int select = 0;
	public int skipSlots = 0;
	
	public ContainerAssemble(IInventory inv, TileAssemble tf) {
		this.tileAssemble = tf;
		
		int i;
		int j;
		for (i = 0; i < 2; ++i) {
			for (j = 0; j < 8; ++j) {
				this.addSlotToContainer(new Slot(tf, j + i * 8, 8 + j * 18,
						18 + i * 18));
			}
		}
		
		for (i = 0; i < 2; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(tf, j + i * 9 + 16,
						8 + j * 18, 63 + i * 18));
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						8 + j * 18, 113 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 171));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileAssemble.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < 34) {
				if (!this.mergeItemStack(itemstack1, 34, 70, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 16, 34, false)) {
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
			if (this.mode != this.tileAssemble.mode) {
				ic.sendProgressBarUpdate(this, 0, this.tileAssemble.mode);
			}
			
			if (this.select != this.tileAssemble.select) {
				ic.sendProgressBarUpdate(this, 1, this.tileAssemble.select);
			}
			
			if (this.skipSlots != this.tileAssemble.skipSlots) {
				ic.sendProgressBarUpdate(this, 2, this.tileAssemble.skipSlots);
			}
		}
		
		this.mode = this.tileAssemble.mode;
		this.select = this.tileAssemble.select;
		this.skipSlots = this.tileAssemble.skipSlots;
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileAssemble.mode = (byte) j;
				break;
			case 1:
				this.tileAssemble.select = (byte) j;
				break;
			case 2:
				this.tileAssemble.skipSlots = j & '\uffff';
		}
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					this.tileAssemble.mode = message.storedBuffer.readByte();
					this.tileAssemble.updateBlockChange();
					break;
				case 2:
					this.tileAssemble.skipSlots = message.storedBuffer.readInt();
					this.tileAssemble.markDirty();
			}
		} catch (Throwable thr) {}
	}
}
