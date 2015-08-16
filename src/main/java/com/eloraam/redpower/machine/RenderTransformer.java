package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.machine.TileTransformer;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderTransformer extends RenderCustomBlock {
	
	protected RenderModel model;
	protected ResourceLocation modelRes;
	protected RenderContext context = new RenderContext();
	
	public RenderTransformer(Block bl) {
		super(bl);
		this.model = RenderModel.loadModel("/assets/rpmachine/models/transform.obj");
		this.modelRes = new ResourceLocation("rpmachine", "models/machine2.png");
		
		this.model.scale(0.0625D);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileTransformer tb = (TileTransformer) CoreLib.getTileEntity(iba, i, j, k, TileTransformer.class);
		if (tb != null) {
			Tessellator tess = Tessellator.instance;
			tess.draw();
			
			this.context.setDefaults();
			this.context.setPos(i, j, k);
			this.context.setOrientation(tb.Rotation >> 2, tb.Rotation + 3 & 3);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));

			Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
			
			tess.startDrawingQuads();
			
			this.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
			this.context.renderModelGroup(0, 0);
			
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
		//this.context.bindTexture("/eloraam/machine/machine2.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		this.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(0, 0);
		this.context.useNormal = false;
		tessellator.draw();
		//this.context.unbindTexture();
	}
}
