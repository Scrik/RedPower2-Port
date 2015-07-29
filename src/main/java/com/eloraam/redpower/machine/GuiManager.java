package com.eloraam.redpower.machine;

import java.util.ArrayList;

import com.eloraam.redpower.core.DimCoord;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.machine.ContainerManager;
import com.eloraam.redpower.machine.TileManager;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiManager extends GuiContainer {
	
	static int[] paintColors = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, '\uff00', 16737408, 5460819, 9671571, '\uffff', 8388863, 255, 5187328, '\u8000', 16711680, 2039583 };
	TileManager manager;
	
	public GuiManager(InventoryPlayer pli, TileManager td) {
		super(new ContainerManager(pli, td));
		this.manager = td;
		super.ySize = 186;
	}
	
	public GuiManager(Container cn) {
		super(cn);
		super.ySize = 186;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Manager", 68, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpmachine", "textures/gui/manager.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int s = this.manager.cond.getChargeScaled(48);
		this.drawTexturedModalRect(j + 17, k + 76 - s, 176, 48 - s, 5, s);
		s = this.manager.cond.getFlowScaled(48);
		this.drawTexturedModalRect(j + 24, k + 76 - s, 176, 48 - s, 5, s);
		if (this.manager.cond.Charge > 600) {
			this.drawTexturedModalRect(j + 18, k + 20, 181, 0, 3, 6);
		}
		
		if (this.manager.cond.Flow == -1) {
			this.drawTexturedModalRect(j + 25, k + 20, 184, 0, 3, 6);
		}
		
		this.drawTexturedModalRect(j + 153, k + 37, 191,
				14 * this.manager.mode, 14, 14);
		if (this.manager.color > 0) {
			this.rect(j + 158, k + 78, 4, 4,
					paintColors[this.manager.color - 1]);
		} else {
			this.drawTexturedModalRect(j + 158, k + 78, 187, 0, 4, 4);
		}
		
		String nm = String.format("%d", new Object[] { Integer.valueOf(this.manager.priority) });
		super.fontRendererObj.drawStringWithShadow(nm, j + 160 - super.fontRendererObj.getStringWidth(nm) / 2, k + 58, 16777215);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendMode() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.manager.mode);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendColor() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.manager.color);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendPriority() {
		if (super.mc.theWorld.isRemote) {
			ArrayList data = new ArrayList();
			data.add(this.manager.priority);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(3, super.inventorySlots.windowId, data));
		}
	}
	
	protected void changeColor(boolean incdec) {
		if (incdec) {
			++this.manager.color;
			if (this.manager.color > 16) {
				this.manager.color = 0;
			}
		} else {
			--this.manager.color;
			if (this.manager.color < 0) {
				this.manager.color = 16;
			}
		}
		
		this.sendColor();
	}
	
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		if (x >= 154 && x <= 165) {
			if (y >= 38 && y <= 50) {
				if (k == 0) {
					++this.manager.mode;
					if (this.manager.mode > 1) {
						this.manager.mode = 0;
					}
				} else {
					--this.manager.mode;
					if (this.manager.mode < 0) {
						this.manager.mode = 1;
					}
				}
				
				this.sendMode();
			}
			
			if (y >= 56 && y <= 68) {
				if (k == 0) {
					++this.manager.priority;
					if (this.manager.priority > 9) {
						this.manager.priority = 0;
					}
				} else {
					--this.manager.priority;
					if (this.manager.priority < 0) {
						this.manager.priority = 9;
					}
				}
				this.sendPriority();
			}
			
			if (y >= 74 && y <= 86) {
				this.changeColor(k == 0);
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
