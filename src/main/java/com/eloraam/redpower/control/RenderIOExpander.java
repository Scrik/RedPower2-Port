package com.eloraam.redpower.control;

import com.eloraam.redpower.control.TileIOExpander;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderIOExpander extends RenderCustomBlock {
	
	RenderContext context = new RenderContext();
	protected RenderModel modelModem = RenderModel.loadModel("/assets/rpcontrol/models/modem.obj");
	protected ResourceLocation modelRes = new ResourceLocation("rpcontrol", "models/modem.png");
	
	public RenderIOExpander(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileIOExpander iox = (TileIOExpander) CoreLib.getTileEntity(iba, x, y, z, TileIOExpander.class);
		if (iox != null) {
			Tessellator tess = Tessellator.instance;
			tess.draw();
			
			this.context.setDefaults();
			this.context.setPos(x, y, z);
			this.context.setOrientation(0, iox.Rotation);
			this.context.readGlobalLights(iba, x, y, z);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, x, y, z));
			
			Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
			
			tess.startDrawingQuads();
			
			this.context.bindModelOffset(this.modelModem, 0.5D, 0.5D, 0.5D);
			this.context.renderModelGroup(1, 1 + (CoreLib.rotToSide(iox.Rotation) & 1));
			this.context.renderModelGroup(2, 1 + (iox.WBuf & 15));
			this.context.renderModelGroup(3, 1 + (iox.WBuf >> 4 & 15));
			this.context.renderModelGroup(4, 1 + (iox.WBuf >> 8 & 15));
			this.context.renderModelGroup(5, 1 + (iox.WBuf >> 12 & 15));
			
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
		
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		this.context.setOrientation(0, 3);
		this.context.bindModelOffset(this.modelModem, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(1, 1);
		this.context.renderModelGroup(2, 1);
		this.context.renderModelGroup(3, 1);
		this.context.renderModelGroup(4, 1);
		this.context.renderModelGroup(5, 1);
		this.context.useNormal = false;
		tessellator.draw();
	}
}
