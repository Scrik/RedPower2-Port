package com.eloraam.redpower.world;

import com.eloraam.redpower.world.ContainerSeedBag;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiSeedBag extends GuiContainer {
	
	public GuiSeedBag(InventoryPlayer pli, IInventory td) {
		super(new ContainerSeedBag(pli, td, (ItemStack) null));
		super.ySize = 167;
	}
	
	public GuiSeedBag(Container cn) {
		super(cn);
		super.ySize = 167;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Seed Bag", 65, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 94 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpworld", "/gui/trap.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
	}
}
