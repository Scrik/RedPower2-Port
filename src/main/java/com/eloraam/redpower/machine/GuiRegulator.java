package com.eloraam.redpower.machine;

import java.util.ArrayList;

import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.machine.ContainerRegulator;
import com.eloraam.redpower.machine.TileRegulator;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiRegulator extends GuiContainer {
	
	static int[] paintColors = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, '\uff00', 16737408, 5460819, 9671571, '\uffff', 8388863, 255, 5187328, '\u8000', 16711680, 2039583 };
	TileRegulator tileRegulator;
	
	public GuiRegulator(InventoryPlayer pli, TileRegulator reg) {
		super(new ContainerRegulator(pli, reg));
		this.tileRegulator = reg;
		super.xSize = 211;
		super.ySize = 167;
	}
	
	public GuiRegulator(Container cn) {
		super(cn);
		super.xSize = 211;
		super.ySize = 167;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString(this.tileRegulator.getInventoryName(), 79, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 25, super.ySize - 96 + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpmachine", "textures/gui/regulator.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		if (this.tileRegulator.color > 0) {
			this.rect(j + 140, k + 60, 4, 4,paintColors[this.tileRegulator.color - 1]);
		} else {
			this.drawTexturedModalRect(j + 140, k + 60, 212, 0, 4, 4);
		}
		
		this.drawTexturedModalRect(j + 135, k + 19, 216, 14 * this.tileRegulator.mode, 14, 14);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendColor() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.tileRegulator.color);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendMode() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.tileRegulator.mode);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, data));
		}
	}
	
	protected void changeColor(boolean incdec) {
		if (incdec) {
			++this.tileRegulator.color;
			if (this.tileRegulator.color > 16) {
				this.tileRegulator.color = 0;
			}
		} else {
			--this.tileRegulator.color;
			if (this.tileRegulator.color < 0) {
				this.tileRegulator.color = 16;
			}
		}
		this.sendColor();
	}
	
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		if (x >= 136 && x <= 147) {
			if (y >= 56 && y <= 67) {
				this.changeColor(k == 0);
				return;
			}
			if (y >= 19 && y <= 32) {
				if (k == 0) {
					++this.tileRegulator.mode;
					if (this.tileRegulator.mode > 1) {
						this.tileRegulator.mode = 0;
					}
				} else {
					--this.tileRegulator.mode;
					if (this.tileRegulator.mode < 0) {
						this.tileRegulator.mode = 1;
					}
				}
				this.sendMode();
			}
		}
		
		super.mouseClicked(i, j, k);
	}
	
	private void rect(int x, int y, int w, int h, int c) {
		w += x;
		h += y;
		float r = (c >> 16 & 255) / 255.0F;
		float g = (c >> 8 & 255) / 255.0F;
		float b = (c & 255) / 255.0F;
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(3553);
		GL11.glColor4f(r, g, b, 1.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertex(x, h, 0.0D);
		tessellator.addVertex(w, h, 0.0D);
		tessellator.addVertex(w, y, 0.0D);
		tessellator.addVertex(x, y, 0.0D);
		tessellator.draw();
		GL11.glEnable(3553);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}
	
}
