package com.eloraam.redpower.logic;

import java.util.ArrayList;

import com.eloraam.redpower.core.DimCoord;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.network.IHandleGuiEvent;
import com.eloraam.redpower.network.PacketGuiEvent;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerTimer extends Container implements IHandleGuiEvent {
	
	long interval = 0L;
	private TileLogicPointer tileLogic;
	
	public ContainerTimer(IInventory inv, TileLogicPointer tf) {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		long iv = this.tileLogic.getInterval();
		
		for (int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting) super.crafters.get(i);
			if (iv != this.interval) {
				ArrayList data = new ArrayList();
				data.add(iv);
				CoreProxy.sendPacketToCrafting(ic, new PacketGuiEvent.GuiMessageEvent(1, super.windowId, data));
			}
		}
		this.interval = iv;
	}
	
	@Override
	public void updateProgressBar(int i, int j) {
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		try {
			switch (message.eventId) {
				case 1:
					long i = message.storedBuffer.readInt();
					this.tileLogic.setInterval(i);
					if (this.tileLogic.getWorldObj() != null) {
						this.tileLogic.updateBlock();
					}
			}
		} catch (Throwable thr) {}
	}
}
