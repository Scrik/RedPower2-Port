package com.eloraam.redpower.machine;

import java.util.ArrayList;

import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.machine.ContainerAssemble;
import com.eloraam.redpower.machine.TileAssemble;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiAssemble extends GuiContainer {
	
	TileAssemble assemble;
	
	public GuiAssemble(InventoryPlayer pli, TileAssemble td) {
		super(new ContainerAssemble(pli, td));
		this.assemble = td;
		super.ySize = 195;
	}
	
	public GuiAssemble(Container cn) {
		super(cn);
		super.ySize = 195;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Assembler", 65, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation tex;
		if (this.assemble.mode == 0) {
			tex = new ResourceLocation("rpmachine", "textures/gui/assembler.png");
		} else {
			tex = new ResourceLocation("rpmachine", "textures/gui/assembler2.png");
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(tex);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		this.drawTexturedModalRect(j + 152, k + 37, 196,
				14 * this.assemble.mode, 14, 14);
		if (this.assemble.mode == 0) {
			this.drawTexturedModalRect(j + 6 + 18 * (this.assemble.select & 7),
					k + 16 + 18 * (this.assemble.select >> 3), 176, 0, 20, 20);
			
			for (int i = 1; i < 16; ++i) {
				if ((this.assemble.skipSlots & 1 << i) != 0) {
					this.drawTexturedModalRect(j + 8 + 18 * (i & 7), k + 18
							+ 18 * (i >> 3), 176, 20, 16, 16);
				}
			}
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendMode() {
		if (!super.mc.theWorld.isRemote) {
			this.assemble.updateBlockChange();
		} else {
			ArrayList data = new ArrayList();
			data.add(this.assemble.mode);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void sendSkip() {
		if (!super.mc.theWorld.isRemote) {
			this.assemble.updateBlockChange();
		} else {
			ArrayList data = new ArrayList();
			data.add(this.assemble.skipSlots);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, data));
		}
	}
	
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		if (x >= 152 && y >= 37 && x <= 166 && y <= 51) {
			if (k == 0) {
				++this.assemble.mode;
				if (this.assemble.mode > 1) {
					this.assemble.mode = 0;
				}
			} else {
				--this.assemble.mode;
				if (this.assemble.mode < 0) {
					this.assemble.mode = 1;
				}
			}
			
			this.sendMode();
		} else {
			if (this.assemble.mode == 0
					&& super.mc.thePlayer.inventory.getItemStack() == null) {
				boolean send = false;
				
				for (int v = 1; v < 16; ++v) {
					int x2 = 8 + 18 * (v & 7);
					int y2 = 18 + 18 * (v >> 3);
					if (x >= x2 && x < x2 + 16 && y >= y2 && y < y2 + 16) {
						if (super.inventorySlots.getSlot(v).getHasStack()) {
							break;
						}
						
						this.assemble.skipSlots ^= 1 << v;
						send = true;
					}
				}
				
				if (send) {
					this.sendSkip();
					return;
				}
			}
			
			super.mouseClicked(i, j, k);
		}
	}
}
