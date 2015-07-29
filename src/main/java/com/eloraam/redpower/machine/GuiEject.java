package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.ContainerEject;
import com.eloraam.redpower.machine.TileEjectBase;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiEject extends GuiContainer {
	
	TileEjectBase tileEject;
	private int inventoryRows = 3;
	
	public GuiEject(InventoryPlayer pli, TileEjectBase td) {
		super(new ContainerEject(pli, td));
		this.tileEject = td;
	}
	
	public GuiEject(Container cn) {
		super(cn);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString(this.tileEject.getInventoryName(), 60, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/generic_54.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(j, k + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}
}
