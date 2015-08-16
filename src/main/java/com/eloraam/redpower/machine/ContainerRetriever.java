package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.TileRetriever;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRetriever extends Container implements IHandleGuiEvent {
	
	private TileRetriever tileRetriever;
	public int charge = 0;
	public int flow = 0;
	public int color = 0;
	public int select = 0;
	public int mode = 0;
	
	public ContainerRetriever(IInventory inv, TileRetriever tr) {
		this.tileRetriever = tr;
		
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(tr, j + i * 3, 62 + j * 18,
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
		return this.tileRetriever.isUseableByPlayer(player);
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
			if (this.charge != this.tileRetriever.cond.Charge) {
				ic.sendProgressBarUpdate(this, 0,
						this.tileRetriever.cond.Charge);
			}
			
			if (this.flow != this.tileRetriever.cond.Flow) {
				ic.sendProgressBarUpdate(this, 1, this.tileRetriever.cond.Flow);
			}
			
			if (this.color != this.tileRetriever.color) {
				ic.sendProgressBarUpdate(this, 2, this.tileRetriever.color);
			}
			
			if (this.select != this.tileRetriever.select) {
				ic.sendProgressBarUpdate(this, 3, this.tileRetriever.select);
			}
			
			if (this.mode != this.tileRetriever.mode) {
				ic.sendProgressBarUpdate(this, 4, this.tileRetriever.mode);
			}
		}
		
		this.flow = this.tileRetriever.cond.Flow;
		this.charge = this.tileRetriever.cond.Charge;
		this.color = this.tileRetriever.color;
		this.select = this.tileRetriever.select;
		this.mode = this.tileRetriever.mode;
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileRetriever.cond.Charge = j;
				break;
			case 1:
				this.tileRetriever.cond.Flow = j;
				break;
			case 2:
				this.tileRetriever.color = (byte) j;
				break;
			case 3:
				this.tileRetriever.select = (byte) j;
				break;
			case 4:
				this.tileRetriever.mode = (byte) j;
		}
		
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					this.tileRetriever.color = message.storedBuffer.readByte();
					this.tileRetriever.markDirty();
					break;
				case 2:
					this.tileRetriever.mode = message.storedBuffer.readByte();
					this.tileRetriever.markDirty();
			}
		} catch (Throwable thr) {}
	}
}
