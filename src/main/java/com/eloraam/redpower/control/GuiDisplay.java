package com.eloraam.redpower.control;

import java.util.ArrayList;

import com.eloraam.redpower.control.ContainerDisplay;
import com.eloraam.redpower.control.TileDisplay;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiDisplay extends GuiContainer {
	
	TileDisplay disp;
	
	public GuiDisplay(IInventory inv, TileDisplay td) {
		super(new ContainerDisplay(inv, td));
		super.xSize = 350;
		super.ySize = 230;
		this.disp = td;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendKey(int b) {
		ArrayList data = new ArrayList();
		data.add((byte) b);
		CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
	}
	
	@Override
	protected void keyTyped(char c, int i) {
		if (i == 1) {
			super.mc.thePlayer.closeScreen();
		} else {
			if (c == 10) {
				c = 13;
			}
			
			int m = 0;
			if (isShiftKeyDown()) {
				m |= 64;
			}
			
			if (isCtrlKeyDown()) {
				m |= 32;
			}
			
			switch (i) {
				case 199:
					this.sendKey(132 | m);
					break;
				case 200:
					this.sendKey(128 | m);
					break;
				/*case 201:
				case 202:
				case 204:
				case 206:
				case 209:*/
				default:
					if (c > 0 && c <= 127) {
						this.sendKey(c);
					}
					break;
				case 203:
					this.sendKey(130 | m);
					break;
				case 205:
					this.sendKey(131 | m);
					break;
				case 207:
					this.sendKey(133 | m);
					break;
				case 208:
					this.sendKey(129 | m);
					break;
				case 210:
					this.sendKey(134 | m);
			}
		}
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		ResourceLocation res = new ResourceLocation("rpcontrol", "textures/gui/displaygui.png");
		//FontRenderer fontrenderer = super.mc.fontRenderer;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int l = (super.width - super.xSize) / 2;
		int m = (super.height - super.ySize) / 2;
		this.drawDoubledRect(l, m, super.xSize, super.ySize, 0, 0, super.xSize, super.ySize);
		GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
		
		for (int y = 0; y < 50; ++y) {
			for (int x = 0; x < 80; ++x) {
				int b = this.disp.screen[y * 80 + x] & 255;
				if (x == this.disp.cursX && y == this.disp.cursY) {
					if (this.disp.cursMode == 1) {
						b ^= 128;
					}
					
					if (this.disp.cursMode == 2) {
						long tm = super.mc.theWorld.getWorldTime();
						if ((tm >> 2 & 1L) > 0L) {
							b ^= 128;
						}
					}
				}
				if (b != 32) {
					this.drawDoubledRect(l + 15 + x * 4, m + 15 + y * 4, 4, 4, 350 + (b & 15) * 8, (b >> 4) * 8, 8, 8);
				}
			}
		}
	}
	
	public void drawDoubledRect(int xd, int yd, int wd, int hd, int xs, int ys, int ws, int hs) {
		float xm = 0.001953125F;
		float ym = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(xd, yd + hd, super.zLevel, xs * xm, (ys + hs) * ym);
		tessellator.addVertexWithUV(xd + wd, yd + hd, super.zLevel, (xs + ws) * xm, (ys + hs) * ym);
		tessellator.addVertexWithUV(xd + wd, yd, super.zLevel, (xs + ws) * xm, ys * ym);
		tessellator.addVertexWithUV(xd, yd, super.zLevel, xs * xm, ys * ym);
		tessellator.draw();
	}
}
