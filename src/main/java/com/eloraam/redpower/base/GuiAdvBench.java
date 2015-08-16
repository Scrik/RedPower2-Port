package com.eloraam.redpower.base;

import java.util.ArrayList;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.ContainerAdvBench;
import com.eloraam.redpower.base.TileAdvBench;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.PacketGuiEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiAdvBench extends GuiContainer {
	
	TileAdvBench bench;
	
	public GuiAdvBench(InventoryPlayer pli, TileAdvBench td) {
		super(new ContainerAdvBench(pli, td));
		this.bench = td;
		super.ySize = 222;
	}
	
	public GuiAdvBench(Container cn) {
		super(cn);
		super.ySize = 222;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Project Table", 60, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2,
				4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/advbench.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		ItemStack plan = super.inventorySlots.getSlot(9).getStack();
		ItemStack craft = super.inventorySlots.getSlot(10).getStack();
		if (plan != null && craft != null
				&& plan.getItem() == RedPowerBase.itemPlanBlank) {
			this.drawTexturedModalRect(j + 18, k + 55, 176, 0, 14, 14);
		}
		
		if (plan != null && plan.getItem() == RedPowerBase.itemPlanFull) {
			ContainerAdvBench cont = (ContainerAdvBench) super.inventorySlots;
			ItemStack[] ist = ContainerAdvBench.getShadowItems(plan);
			RenderHelper.enableGUIStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
					240.0F, 240.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable('\u803a');
			GL11.glEnable(2896);
			GL11.glEnable(2929);
			
			int n;
			Slot sl;
			int slx;
			int sly;
			for (n = 0; n < 9; ++n) {
				if (ist[n] != null) {
					sl = super.inventorySlots.getSlot(n);
					if (sl.getStack() == null) {
						slx = sl.xDisplayPosition + j;
						sly = sl.yDisplayPosition + k;
						GuiContainer.itemRender.renderItemIntoGUI(
								super.fontRendererObj, super.mc.renderEngine,
								ist[n], slx, sly);
						GuiContainer.itemRender.renderItemOverlayIntoGUI(
								super.fontRendererObj, super.mc.renderEngine,
								ist[n], slx, sly);
					}
				}
			}
			
			GL11.glDisable(2896);
			GL11.glDisable(2929);
			GL11.glEnable(3042);
			super.mc.renderEngine.bindTexture(res);
			
			for (n = 0; n < 9; ++n) {
				if (ist[n] != null) {
					sl = super.inventorySlots.getSlot(n);
					if (sl.getStack() == null) {
						slx = sl.xDisplayPosition;
						sly = sl.yDisplayPosition;
						if ((cont.satisfyMask & 1 << n) > 0) {
							GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
						} else {
							GL11.glColor4f(1.0F, 0.1F, 0.1F, 0.6F);
						}
						
						this.drawTexturedModalRect(j + slx, k + sly, slx, sly,
								16, 16);
					}
				}
			}
			GL11.glDisable(3042);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected void mouseClicked(int i, int j, int k) {
		int x = i - (super.width - super.xSize) / 2;
		int y = j - (super.height - super.ySize) / 2;
		if (x >= 18 && y >= 55 && x <= 32 && y <= 69) {
			ItemStack plan = super.inventorySlots.getSlot(9).getStack();
			ItemStack craft = super.inventorySlots.getSlot(10).getStack();
			if (plan == null || craft == null || plan.getItem() != RedPowerBase.itemPlanBlank) {
				return;
			}
			ArrayList data = new ArrayList();
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
		super.mouseClicked(i, j, k);
	}
}
