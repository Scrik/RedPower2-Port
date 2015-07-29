package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.machine.TilePump;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TilePumpRenderer extends TileEntitySpecialRenderer {
	
	RenderContext context = new RenderContext();
	protected RenderModel modelSlide = RenderModel.loadModel("/assets/rpmachine/models/pump2.obj");
	protected ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/machine1.png");
	
	public TilePumpRenderer() {
		this.context.setDefaults();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		TilePump tp = (TilePump) te;
		Tessellator tessellator = Tessellator.instance;
		//this.bindTextureByName("/eloraam/machine/machine1.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(lv);
		if (tp.Active) {
			f += tp.PumpTick;
			if (f > 8.0F) {
				f = 16.0F - f;
			}
			
			f = (float) (f / 8.0D);
		} else {
			f = 0.0F;
		}
		
		this.context.useNormal = true;
		this.context.setPos(x, y, z);
		this.context.setOrientation(0, tp.Rotation);
		this.context.setRelPos(0.375D + 0.3125D * f, 0.0D, 0.0D);
		this.context.bindModelOffset(this.modelSlide, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(0, 0);
		tessellator.draw();
	}
}
