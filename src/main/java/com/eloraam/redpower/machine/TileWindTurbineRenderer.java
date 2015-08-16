package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.Matrix3;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileWindTurbine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileWindTurbineRenderer extends TileEntitySpecialRenderer {
	
	RenderModel modelWoodTurbine = RenderModel.loadModel("/assets/rpmachine/models/vawt.obj");
	RenderModel modelWoodWindmill = RenderModel.loadModel("/assets/rpmachine/models/windmill.obj");
	ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/vawt.png");
	
	RenderContext context = new RenderContext();
	
	public TileWindTurbineRenderer() {
		this.modelWoodTurbine.scale(0.0625D);
		this.modelWoodWindmill.scale(0.0625D);
		this.context.setDefaults();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		if (te instanceof TileWindTurbine) {
			TileWindTurbine twt = (TileWindTurbine) te;
			if (twt.hasBlades) {
				byte wtt = twt.windmillType;
				Tessellator tessellator = Tessellator.instance;
				Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
				tessellator.startDrawingQuads();
				WorldCoord wc = new WorldCoord(te);
				wc.step(twt.Rotation ^ 1);
				int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(wc.x, wc.y, wc.z, 0);
				tessellator.setBrightness(lv);
				this.context.useNormal = true;
				if (twt.hasBrakes) {
					f = (float) (f * 0.1D);
				}
				double tm = f * twt.speed + twt.phase;
				if (wtt == 2) {
					tm = -tm;
				}
				this.context.setOrientation(twt.Rotation, 0);
				this.context.basis = Matrix3.getRotY(-4.0E-6D * tm).multiply(this.context.basis);
				this.context.setPos(x, y, z);
				this.context.setRelPos(0.5D, 0.875D, 0.5D);
				switch (wtt) {
					case 1:
						this.context.bindModelOffset(this.modelWoodTurbine,
								0.5D, 0.5D, 0.5D);
						break;
					case 2:
						this.context.bindModelOffset(this.modelWoodWindmill,
								0.5D, 0.5D, 0.5D);
						break;
					default:
						return;
				}
				this.context.setTint(1.0F, 1.0F, 1.0F);
				this.context.renderModelGroup(0, 0);
				if (wtt == 1) {
					this.context.setTint(1.0F, 1.0F, 1.0F);
					this.context.renderModelGroup(1, 1);
					this.context.renderModelGroup(1, 3);
					this.context.renderModelGroup(1, 5);
					this.context.setTint(1.0F, 0.1F, 0.1F);
					this.context.renderModelGroup(1, 2);
					this.context.renderModelGroup(1, 4);
					this.context.renderModelGroup(1, 6);
				} else {
					this.context.setTint(1.0F, 1.0F, 1.0F);
					this.context.renderModelGroup(1, 1);
					this.context.renderModelGroup(1, 3);
					this.context.setTint(1.0F, 0.1F, 0.1F);
					this.context.renderModelGroup(1, 2);
					this.context.renderModelGroup(1, 4);
				}
				tessellator.draw();
			}
		}
	}
}
