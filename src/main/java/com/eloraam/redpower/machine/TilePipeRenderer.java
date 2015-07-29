package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.machine.TilePipe;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

public class TilePipeRenderer extends TileEntitySpecialRenderer {
	
	RenderContext context = new RenderContext();
	
	public TilePipeRenderer() {
		this.context.setDefaults();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		TilePipe tp = (TilePipe) te;
		int lvl = tp.pipebuf.getLevel();
		if (tp.pipebuf.Type != 0 && lvl > 0) {
			float lvn = Math.min(1.0F, (float) lvl / (float) tp.pipebuf.getMaxLevel());
			tp.cacheCon();
			int sides = tp.ConCache;
			Fluid fcl = FluidRegistry.getFluid(tp.pipebuf.Type);
			if (fcl != null) {
				Tessellator tessellator = Tessellator.instance;
				//this.bindTextureByName(fcl.getTextureFile());
				int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glDisable(2896);
				tessellator.startDrawingQuads();
				this.context.setBrightness(lv);
				this.context.setPos(x, y, z);
				this.context.setIcon(fcl.getIcon());
				float x1;
				float x2;
				float n;
				if ((sides & 3) > 0) {
					x1 = 0.5F;
					x2 = 0.5F;
					if ((sides & 1) > 0) {
						x1 = 0.0F;
					}
					
					if ((sides & 2) > 0) {
						x2 = 1.0F;
					}
					
					n = 0.124F * lvn;
					this.context.renderBox(60, 0.5F - n, x1, 0.5F - n,
							0.5F + n, x2, 0.5F + n);
				}
				
				if ((sides & 12) > 0) {
					x1 = 0.5F;
					x2 = 0.5F;
					if ((sides & 4) > 0) {
						x1 = 0.0F;
					}
					
					if ((sides & 8) > 0) {
						x2 = 1.0F;
					}
					
					n = 0.248F * lvn;
					this.context.renderBox(51, 0.37599998712539673D,
							0.37599998712539673D, x1, 0.6240000128746033D,
							0.376F + n, x2);
				}
				
				if ((sides & 48) > 0) {
					x1 = 0.5F;
					x2 = 0.5F;
					if ((sides & 16) > 0) {
						x1 = 0.0F;
					}
					
					if ((sides & 32) > 0) {
						x2 = 1.0F;
					}
					
					n = 0.248F * lvn;
					this.context.renderBox(15, x1, 0.37599998712539673D,
							0.37599998712539673D, x2, 0.376F + n,
							0.6240000128746033D);
				}
				
				tessellator.draw();
				GL11.glEnable(2896);
				GL11.glDisable(3042);
			}
		}
	}
}
