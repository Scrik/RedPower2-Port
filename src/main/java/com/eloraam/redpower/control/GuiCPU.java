package com.eloraam.redpower.control;

import java.util.ArrayList;

import com.eloraam.redpower.control.ContainerCPU;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.core.DimCoord;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCPU extends GuiContainer {
	
	TileCPU tileCPU;
	
	public GuiCPU(InventoryPlayer pli, TileCPU cpu) {
		super(new ContainerCPU(pli, cpu));
		this.tileCPU = cpu;
		super.ySize = 145;
		super.xSize = 227;
	}
	
	public GuiCPU(Container cn) {
		super(cn);
		super.ySize = 145;
		super.xSize = 227;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation res = new ResourceLocation("rpcontrol", "textures/gui/cpugui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int bits = this.tileCPU.byte0;
		
		int n;
		for (n = 0; n < 8; ++n) {
			if ((bits & 1 << n) != 0) {
				this.drawTexturedModalRect(j + 14 + n * 12, k + 57,
						227 + (n >> 2) * 12, 0, 12, 32);
			}
		}
		
		bits = this.tileCPU.byte1;
		
		for (n = 0; n < 8; ++n) {
			if ((bits & 1 << n) != 0) {
				this.drawTexturedModalRect(j + 118 + n * 12, k + 57,
						227 + (n >> 2) * 12, 0, 12, 32);
			}
		}
		
		bits = this.tileCPU.rbaddr;
		
		for (n = 0; n < 8; ++n) {
			if ((bits & 1 << n) != 0) {
				this.drawTexturedModalRect(j + 118 + n * 12, k + 101,
						227 + (n >> 2) * 12, 0, 12, 32);
			}
		}
		
		if (this.tileCPU.isRunning()) {
			this.drawTexturedModalRect(j + 102, k + 99, 227, 32, 8, 8);
		} else {
			this.drawTexturedModalRect(j + 102, k + 112, 227, 32, 8, 8);
		}
		
		this.drawString(
				super.fontRendererObj,
				String.format("Disk: %d",
						new Object[] { Integer.valueOf(this.tileCPU.byte0) }),
				j + 14, k + 47, -1);
		this.drawString(
				super.fontRendererObj,
				String.format("Console: %d",
						new Object[] { Integer.valueOf(this.tileCPU.byte1) }),
				j + 118, k + 47, -1);
		this.drawString(
				super.fontRendererObj,
				String.format("ID: %d",
						new Object[] { Integer.valueOf(this.tileCPU.rbaddr) }),
				j + 118, k + 91, -1);
		this.drawString(
				super.fontRendererObj,
				String.format("START",
						new Object[] { Integer.valueOf(this.tileCPU.rbaddr) }),
				j + 50, k + 99, -1);
		this.drawString(
				super.fontRendererObj,
				String.format("HALT",
						new Object[] { Integer.valueOf(this.tileCPU.rbaddr) }),
				j + 50, k + 112, -1);
		this.drawString(
				super.fontRendererObj,
				String.format("RESET",
						new Object[] { Integer.valueOf(this.tileCPU.rbaddr) }),
				j + 50, k + 125, -1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendSimple(int n, int m) {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(m);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(n, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	boolean sendEvent(int n) {
		if (!super.mc.theWorld.isRemote) {
			return true;
		} else {
			ArrayList data = new ArrayList();
			data.add(0);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(n, super.inventorySlots.windowId, data));
			return false;
		}
	}
	
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		int n;
		if (y >= 57 && y <= 89) {
			for (n = 0; n < 8; ++n) {
				if (x >= 14 + n * 12 && x <= 26 + n * 12) {
					this.tileCPU.byte0 ^= 1 << n;
					this.sendSimple(1, this.tileCPU.byte0);
					return;
				}
			}
			
			for (n = 0; n < 8; ++n) {
				if (x >= 118 + n * 12 && x <= 130 + n * 12) {
					this.tileCPU.byte1 ^= 1 << n;
					this.sendSimple(2, this.tileCPU.byte1);
					return;
				}
			}
		}
		
		if (y >= 101 && y <= 133) {
			for (n = 0; n < 8; ++n) {
				if (x >= 118 + n * 12 && x <= 130 + n * 12) {
					this.tileCPU.rbaddr ^= 1 << n;
					this.sendSimple(3, this.tileCPU.rbaddr);
					return;
				}
			}
		}
		
		if (x >= 87 && x <= 96) {
			if (y >= 98 && y <= 107) {
				if (this.sendEvent(4)) {
					this.tileCPU.warmBootCPU();
				}
				
				return;
			}
			
			if (y >= 111 && y <= 120) {
				if (this.sendEvent(5)) {
					this.tileCPU.haltCPU();
				}
				
				return;
			}
			
			if (y >= 124 && y <= 133) {
				if (this.sendEvent(6)) {
					this.tileCPU.coldBootCPU();
				}
				
				return;
			}
		}
		
		super.mouseClicked(i, j, k);
	}
}
