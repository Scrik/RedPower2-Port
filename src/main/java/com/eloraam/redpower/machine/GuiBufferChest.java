package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.ContainerBufferChest;
import com.eloraam.redpower.machine.TileBufferChest;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiBufferChest extends GuiContainer {
	
	public GuiBufferChest(InventoryPlayer pli, TileBufferChest td) {
		super(new ContainerBufferChest(pli, td));
		super.ySize = 186;
	}
	
	public GuiBufferChest(Container cn) {
		super(cn);
		super.ySize = 186;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Buffer", 70, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpmachine", "textures/gui/buffer.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
	}
}
