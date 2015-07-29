package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileThermopile;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderThermopile extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderThermopile(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileThermopile tb = (TileThermopile) CoreLib.getTileEntity(iba, i, j, k, TileThermopile.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setIcon(super.block.getIcon(140, md), super.block.getIcon(140, md), super.block.getIcon(138, md), super.block.getIcon(138, md), super.block.getIcon(139, md), super.block.getIcon(139, md));
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			this.context.renderGlobFaces(63);
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
		this.context.setIcon(super.block.getIcon(140, md), super.block.getIcon(140, md), super.block.getIcon(138, md), super.block.getIcon(138, md), super.block.getIcon(139, md), super.block.getIcon(139, md));
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
