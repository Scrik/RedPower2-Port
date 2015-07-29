package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.ContainerChargingBench;
import com.eloraam.redpower.machine.TileChargingBench;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiChargingBench extends GuiContainer {
	
	TileChargingBench tileCB;
	
	public GuiChargingBench(InventoryPlayer pli, TileChargingBench cb) {
		super(new ContainerChargingBench(pli, cb));
		this.tileCB = cb;
		super.ySize = 186;
	}
	
	public GuiChargingBench(Container cn) {
		super(cn);
		super.ySize = 186;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Charging Bench", 60, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpmachine", "textures/gui/charging.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int mx = this.tileCB.getMaxStorage();
		int s = this.tileCB.getChargeScaled(48);
		this.drawTexturedModalRect(j + 21, k + 78 - s, 176, 48 - s, 5, s);
		if (this.tileCB.cond.Charge > 600) {
			this.drawTexturedModalRect(j + 22, k + 22, 197, 8, 3, 6);
		}
		
		if (this.tileCB.cond.Charge > 600 && this.tileCB.Storage < mx) {
			this.drawTexturedModalRect(j + 32, k + 51, 197, 0, 10, 8);
		}
		
		s = this.tileCB.getStorageScaled(48);
		this.drawTexturedModalRect(j + 48, k + 78 - s, 181, 48 - s, 16, s);
		if (this.tileCB.Storage == mx) {
			this.drawTexturedModalRect(j + 53, k + 22, 200, 8, 6, 6);
		}
		
	}
}
