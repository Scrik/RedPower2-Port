package com.eloraam.redpower.base;

import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.network.IHandleGuiEvent;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerBusId extends Container implements IHandleGuiEvent {
	
	private IRedbusConnectable rbConn;
	int addr = 0;
	
	public ContainerBusId(IInventory inv, IRedbusConnectable irc) {
		this.rbConn = irc;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		return null;
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting) super.crafters.get(i);
			if (this.rbConn.rbGetAddr() != this.addr) {
				ic.sendProgressBarUpdate(this, 0, this.rbConn.rbGetAddr());
			}
		}
		
		this.addr = this.rbConn.rbGetAddr();
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.rbConn.rbSetAddr(j);
				return;
			default:
		}
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			if (message.eventId != 1) {
				return;
			}
			this.rbConn.rbSetAddr(message.storedBuffer.readByte());
		} catch (Throwable thr) {}
	}
}
