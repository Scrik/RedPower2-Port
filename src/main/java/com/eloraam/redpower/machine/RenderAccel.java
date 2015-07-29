package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.machine.TileAccel;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderAccel extends RenderCustomBlock {
	
	protected RenderModel model = RenderModel.loadModel("/assets/rpmachine/models/accel.obj");
	protected ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/machine1.png"); 
	protected RenderContext context = new RenderContext();
	
	public RenderAccel(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileAccel tb = (TileAccel) CoreLib.getTileEntity(iba, i, j, k, TileAccel.class);
		if (tb != null) {
			Tessellator tess = Tessellator.instance;
			tess.draw();
			
			this.context.setDefaults();
			this.context.setPos(i, j, k);
			this.context.setOrientation(tb.Rotation, 0);
			this.context.readGlobalLights(iba, i, j, k);
			if (tb.Charged) {
				this.context.setBrightness(15728880);
			} else {
				this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			}
			Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
			
			tess.startDrawingQuads();
			this.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
			this.context.renderModelGroup(0, 0);
			this.context.renderModelGroup(1, 1 + (tb.Charged ? 1 : 0));
			if (tb.Charged) {
				this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			}
			tb.recache();
			if ((tb.conCache & 1) > 0) {
				this.context.renderModelGroup(2, 2);
			}
			
			if ((tb.conCache & 2) > 0) {
				this.context.renderModelGroup(2, 1);
			}
			
			if ((tb.conCache & 4) > 0) {
				this.context.renderModelGroup(3, 2);
			}
			
			if ((tb.conCache & 8) > 0) {
				this.context.renderModelGroup(3, 1);
			}
			
			tess.draw();
			
			this.context.bindBlockTexture();
			tess.startDrawingQuads();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.setOrientation(2, 0);
		//this.context.bindTexture("/eloraam/machine/machine1.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		this.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(0, 0);
		this.context.renderModelGroup(1, 1);
		this.context.useNormal = false;
		tessellator.draw();
		//this.context.unbindTexture();
	}
}
