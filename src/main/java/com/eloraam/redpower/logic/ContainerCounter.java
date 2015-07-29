package com.eloraam.redpower.logic;

import com.eloraam.redpower.logic.TileLogicStorage;
import com.eloraam.redpower.network.IHandleGuiEvent;
import com.eloraam.redpower.network.PacketGuiEvent;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerCounter extends Container implements IHandleGuiEvent {
	
	int Count = 0;
	int CountMax = 0;
	int Inc = 0;
	int Dec = 0;
	private TileLogicStorage tileLogic;
	
	public ContainerCounter(IInventory inv, TileLogicStorage tf) {
		this.tileLogic = tf;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileLogic.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		return null;
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic
				.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		
		for (int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting) super.crafters.get(i);
			if (this.Count != lsc.Count) {
				ic.sendProgressBarUpdate(this, 0, lsc.Count);
			}
			
			if (this.CountMax != lsc.CountMax) {
				ic.sendProgressBarUpdate(this, 1, lsc.CountMax);
			}
			
			if (this.Inc != lsc.Inc) {
				ic.sendProgressBarUpdate(this, 2, lsc.Inc);
			}
			
			if (this.Dec != lsc.Dec) {
				ic.sendProgressBarUpdate(this, 3, lsc.Dec);
			}
		}
		
		this.Count = lsc.Count;
		this.CountMax = lsc.CountMax;
		this.Inc = lsc.Inc;
		this.Dec = lsc.Dec;
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic
				.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		switch (i) {
			case 0:
				lsc.Count = j;
				break;
			case 1:
				lsc.CountMax = j;
				break;
			case 2:
				lsc.Inc = j;
				break;
			case 3:
				lsc.Dec = j;
		}
		
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		
		try {
			switch (message.eventId) {
				case 0:
					lsc.Count = message.storedBuffer.readInt();
					this.tileLogic.updateBlock();
					break;
				case 1:
					lsc.CountMax = message.storedBuffer.readInt();
					this.tileLogic.updateBlock();
					break;
				case 2:
					lsc.Inc = message.storedBuffer.readInt();
					this.tileLogic.markDirty();
					break;
				case 3:
					lsc.Dec = message.storedBuffer.readInt();
					this.tileLogic.markDirty();
			}
		} catch (Throwable thr) {}
		
	}
}
