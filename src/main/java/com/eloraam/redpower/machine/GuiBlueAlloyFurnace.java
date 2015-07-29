package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.ContainerBlueAlloyFurnace;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiBlueAlloyFurnace extends GuiContainer {
	
	TileBlueAlloyFurnace furnace;
	
	public GuiBlueAlloyFurnace(InventoryPlayer pli, TileBlueAlloyFurnace td) {
		super(new ContainerBlueAlloyFurnace(pli, td));
		this.furnace = td;
	}
	
	public GuiBlueAlloyFurnace(Container cn) {
		super(cn);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.fontRendererObj.drawString("Blulectric Alloy Furnace", 38, 6, 4210752);
		super.fontRendererObj.drawString("Inventory", 8, super.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(new ResourceLocation("rpmachine", "textures/gui/btafurnace.png"));
		int j = (super.width - super.xSize) / 2;
		int k = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
		int s = this.furnace.getCookScaled(24);
		this.drawTexturedModalRect(j + 107, k + 34, 176, 0, s + 1, 16);
		s = this.furnace.cond.getChargeScaled(48);
		this.drawTexturedModalRect(j + 19, k + 69 - s, 176, 65 - s, 5, s);
		s = this.furnace.cond.getFlowScaled(48);
		this.drawTexturedModalRect(j + 26, k + 69 - s, 176, 65 - s, 5, s);
		if (this.furnace.cond.Charge > 600) {
			this.drawTexturedModalRect(j + 20, k + 13, 181, 17, 3, 6);
		}
		
		if (this.furnace.cond.Flow == -1) {
			this.drawTexturedModalRect(j + 27, k + 13, 184, 17, 3, 6);
		}
	}
}
