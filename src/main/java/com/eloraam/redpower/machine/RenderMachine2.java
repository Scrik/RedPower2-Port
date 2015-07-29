package com.eloraam.redpower.machine;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;

public class RenderMachine2 extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderMachine2(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileMachine tb = (TileMachine) CoreLib.getTileEntity(iba, i, j, k, TileMachine.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			if (md == 0) {
				IIcon tx = super.block.getIcon(201 + (tb.Charged ? 1 : 0) + (tb.Active ? 2 : 0), md);
				IIcon t2 = super.block.getIcon(199 + (tb.Charged ? 1 : 0), md);
				this.context.setIcon(super.block.getIcon(198, md), super.block.getIcon(197, md), t2, t2, tx, tx);
			} else if (md == 1) {
				IIcon tx = super.block.getIcon(210 + (tb.Charged ? 4 : 0) + (tb.Active ? 1 : 0) + (!tb.Delay && !tb.Powered ? 0 : 2), md);
				this.context.setIcon(super.block.getIcon(209, md), super.block.getIcon(208, md), tx, tx, tx, tx);
			}
			
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.orientTextures(tb.Rotation);
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
		if (md == 0) {
			this.context.setIcon(super.block.getIcon(198, md), super.block.getIcon(197, md), super.block.getIcon(199, md), super.block.getIcon(199, md), super.block.getIcon(201, md), super.block.getIcon(201, md));
		} else if (md == 1) {
			this.context.setIcon(super.block.getIcon(209, md), super.block.getIcon(208, md), super.block.getIcon(210, md), super.block.getIcon(210, md), super.block.getIcon(210, md), super.block.getIcon(210, md));
		}
		
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
