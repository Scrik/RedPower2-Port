package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ContainerAlloyFurnace;
import com.eloraam.redpower.base.TileAlloyFurnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiAlloyFurnace extends GuiContainer {
	
	TileAlloyFurnace furnace;
	
	public GuiAlloyFurnace(InventoryPlayer pli, TileAlloyFurnace td) {
		super(new ContainerAlloyFurnace(pli, td));
		this.furnace = td;
	}
	
	public GuiAlloyFurnace(Container cn) {
		super(cn);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Alloy Furnace", 60, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/afurnacegui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int i1;
		if (this.furnace.burntime > 0) {
			i1 = this.furnace.getBurnScaled(12);
			this.drawTexturedModalRect(j + 17, k + 25 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
		}
		i1 = this.furnace.getCookScaled(24);
		this.drawTexturedModalRect(j + 107, k + 34, 176, 14, i1 + 1, 16);
	}
}
