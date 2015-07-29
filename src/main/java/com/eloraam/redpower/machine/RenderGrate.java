package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileGrate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderGrate extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderGrate(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileGrate tb = (TileGrate) CoreLib.getTileEntity(iba, i, j, k, TileGrate.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setIcon(super.block.getIcon(tb.Rotation == 0 ? 45 : 43, md),
					super.block.getIcon(tb.Rotation == 1 ? 45 : 43, md), 
					super.block.getIcon(tb.Rotation == 2 ? 45 : 44, md),
					super.block.getIcon(tb.Rotation == 3 ? 45 : 44, md), 
					super.block.getIcon(tb.Rotation == 4 ? 45 : 44, md),
					super.block.getIcon(tb.Rotation == 5 ? 45 : 44, md));
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.renderGlobFaces(63);
			this.context.setIcon(super.block.getIcon(tb.Rotation == 1 ? 46 : 43, md),
					super.block.getIcon(tb.Rotation == 0 ? 46 : 43, md), 
					super.block.getIcon(tb.Rotation == 3 ? 46 : 43, md),
					super.block.getIcon(tb.Rotation == 2 ? 46 : 43, md), 
					super.block.getIcon(tb.Rotation == 5 ? 46 : 43, md),
					super.block.getIcon(tb.Rotation == 4 ? 46 : 43, md));
			this.context.setLocalLights(0.3F);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(
					iba, i, j, k));
			this.context
					.renderBox(63, 0.99D, 0.99D, 0.99D, 0.01D, 0.01D, 0.01D);
			//RenderLib.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.useNormal = true;
		//RenderLib.bindTexture("/eloraam/machine/machine1.png");
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		this.context.setIcon(super.block.getIcon(43, md), super.block.getIcon(45, md), super.block.getIcon(44, md), super.block.getIcon(44, md), super.block.getIcon(44, md), super.block.getIcon(44, md));
		this.context.doubleBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, 0.01D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
