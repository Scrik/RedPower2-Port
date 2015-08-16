package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.TileSorter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSorter extends Container implements IHandleGuiEvent {
	
	public byte[] colors = new byte[8];
	public int column;
	public int charge = 0;
	public int flow = 0;
	public int mode = 0;
	public int defcolor = 0;
	public int automode = 0;
	private TileSorter tileSorter;
	
	public ContainerSorter(IInventory inv, TileSorter tf) {
		this.tileSorter = tf;
		
		int i;
		int j;
		for (i = 0; i < 5; ++i) {
			for (j = 0; j < 8; ++j) {
				this.addSlotToContainer(new Slot(tf, j + i * 8, 26 + 18 * j,
						18 + 18 * i));
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						8 + j * 18, 140 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 198));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileSorter.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < 40) {
				if (!this.mergeItemStack(itemstack1, 40, 76, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 40, false)) {
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
		
		int j;
		for (j = 0; j < super.crafters.size(); ++j) {
			ICrafting ic = (ICrafting) super.crafters.get(j);
			
			for (int j1 = 0; j1 < 8; ++j1) {
				if (this.colors[j1] != this.tileSorter.colors[j1]) {
					ic.sendProgressBarUpdate(this, j1,
							this.tileSorter.colors[j1]);
				}
			}
			
			if (this.column != this.tileSorter.column) {
				ic.sendProgressBarUpdate(this, 8, this.tileSorter.column);
			}
			
			if (this.charge != this.tileSorter.cond.Charge) {
				ic.sendProgressBarUpdate(this, 9, this.tileSorter.cond.Charge);
			}
			
			if (this.flow != this.tileSorter.cond.Flow) {
				ic.sendProgressBarUpdate(this, 10, this.tileSorter.cond.Flow);
			}
			
			if (this.mode != this.tileSorter.mode) {
				ic.sendProgressBarUpdate(this, 11, this.tileSorter.mode);
			}
			
			if (this.defcolor != this.tileSorter.defcolor) {
				ic.sendProgressBarUpdate(this, 12, this.tileSorter.defcolor);
			}
			
			if (this.automode != this.tileSorter.automode) {
				ic.sendProgressBarUpdate(this, 13, this.tileSorter.automode);
			}
		}
		
		for (j = 0; j < 8; ++j) {
			this.colors[j] = this.tileSorter.colors[j];
		}
		
		this.column = this.tileSorter.column;
		this.charge = this.tileSorter.cond.Charge;
		this.flow = this.tileSorter.cond.Flow;
		this.mode = this.tileSorter.mode;
		this.defcolor = this.tileSorter.defcolor;
		this.automode = this.tileSorter.automode;
	}
	
	public void func_20112_a(int i, int j) {
		this.updateProgressBar(i, j);
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		if (i < 8) {
			this.tileSorter.colors[i] = (byte) j;
		}
		
		switch (i) {
			case 8:
				this.tileSorter.column = (byte) j;
				break;
			case 9:
				this.tileSorter.cond.Charge = j;
				break;
			case 10:
				this.tileSorter.cond.Flow = j;
				break;
			case 11:
				this.tileSorter.mode = (byte) j;
				break;
			case 12:
				this.tileSorter.defcolor = (byte) j;
				break;
			case 13:
				this.tileSorter.automode = (byte) j;
		}
		
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					this.tileSorter.mode = message.storedBuffer.readByte();
					this.tileSorter.markDirty();
					break;
				case 2:
					byte i = message.storedBuffer.readByte();
					if (i >= 0 && i <= 8) {
						this.tileSorter.colors[i] = message.storedBuffer.readByte();
						this.tileSorter.markDirty();
					}
					break;
				case 3:
					this.tileSorter.defcolor = message.storedBuffer.readByte();
					this.tileSorter.markDirty();
					break;
				case 4:
					this.tileSorter.automode = message.storedBuffer.readByte();
					this.tileSorter.pulses = 0;
					this.tileSorter.markDirty();
			}
		} catch (Throwable thr) {}
	}
}
