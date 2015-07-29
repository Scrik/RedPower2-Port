package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ContainerBag;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiBag extends GuiContainer {
	
	public GuiBag(InventoryPlayer pli, IInventory td) {
		super(new ContainerBag(pli, td, (ItemStack) null));
		super.ySize = 167;
	}
	
	public GuiBag(Container cn) {
		super(cn);
		super.ySize = 167;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Canvas Bag", 8, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 94 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/baggui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(res);
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
	}
}
