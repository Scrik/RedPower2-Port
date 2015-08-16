package com.eloraam.redpower.base;

import java.util.ArrayList;

import com.eloraam.redpower.base.ContainerBusId;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiBusId extends GuiContainer {
	
	IRedbusConnectable rbConn;
	TileEntity tile;
	
	public GuiBusId(InventoryPlayer pli, IRedbusConnectable irc, TileEntity tile) {
		super(new ContainerBusId(pli, irc));
		this.rbConn = irc;
		this.tile=tile;
		super.ySize = 81;
		super.xSize = 123;
	}
	
	public GuiBusId(Container cn) {
		super(cn);
		super.ySize = 81;
		super.xSize = 123;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Set Bus Id", 32, 6, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/idgui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int bits = this.rbConn.rbGetAddr();
		
		for (int n = 0; n < 8; ++n) {
			if ((bits & 1 << n) != 0) {
				this.drawTexturedModalRect(j + 16 + n * 12, k + 25, 123, 0, 8,
						16);
			}
		}
		
		this.drawCenteredString(
				super.fontRendererObj,
				String.format("ID: %d", new Object[] { Integer.valueOf(bits) }),
				super.width / 2, k + 60, -1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendAddr() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.rbConn.rbGetAddr());
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		if (y >= 25 && y <= 41) {
			for (int n = 0; n < 8; ++n) {
				if (x >= 16 + n * 12 && x <= 24 + n * 12) {
					this.rbConn.rbSetAddr(this.rbConn.rbGetAddr() ^ 1 << n);
					this.sendAddr();
					return;
				}
			}
		}
		super.mouseClicked(i, j, k);
	}
}
