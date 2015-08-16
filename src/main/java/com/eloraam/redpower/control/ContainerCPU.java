package com.eloraam.redpower.control;

import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerCPU extends Container implements IHandleGuiEvent {
	
	private TileCPU tileCPU;
	int byte0 = 0;
	int byte1 = 0;
	int rbaddr = 0;
	boolean isrun = false;
	
	public ContainerCPU(IInventory inv, TileCPU cpu) {
		this.tileCPU = cpu;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileCPU.isUseableByPlayer(player);
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
			if (this.tileCPU.byte0 != this.byte0) {
				ic.sendProgressBarUpdate(this, 0, this.tileCPU.byte0);
			}
			
			if (this.tileCPU.byte1 != this.byte1) {
				ic.sendProgressBarUpdate(this, 1, this.tileCPU.byte1);
			}
			
			if (this.tileCPU.rbaddr != this.rbaddr) {
				ic.sendProgressBarUpdate(this, 2, this.tileCPU.rbaddr);
			}
			
			if (this.tileCPU.isRunning() != this.isrun) {
				ic.sendProgressBarUpdate(this, 3,
						this.tileCPU.isRunning() ? 1 : 0);
			}
		}
		
		this.byte0 = this.tileCPU.byte0;
		this.byte1 = this.tileCPU.byte1;
		this.rbaddr = this.tileCPU.rbaddr;
		this.isrun = this.tileCPU.isRunning();
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileCPU.byte0 = j;
				break;
			case 1:
				this.tileCPU.byte1 = j;
				break;
			case 2:
				this.tileCPU.rbaddr = j;
				break;
			case 3:
				this.tileCPU.sliceCycles = j > 0 ? 0 : -1;
		}
		
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					this.tileCPU.byte0 = message.storedBuffer.readInt(); //TODO Можно заменить на байты
					break;
				case 2:
					this.tileCPU.byte1 = message.storedBuffer.readInt();
					break;
				case 3:
					this.tileCPU.rbaddr = message.storedBuffer.readInt();
					break;
				case 4:
					this.tileCPU.warmBootCPU();
					break;
				case 5:
					this.tileCPU.haltCPU();
					break;
				case 6:
					this.tileCPU.coldBootCPU();
			}
		} catch (Throwable thr) {}
	}
}
