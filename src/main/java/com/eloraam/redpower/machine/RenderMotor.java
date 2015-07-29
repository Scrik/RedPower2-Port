package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileMotor;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderMotor extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderMotor(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileMotor tb = (TileMotor) CoreLib.getTileEntity(iba, i, j, k, TileMotor.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setTexFlags(64);
			if (tb.Active) {
				this.context.setIcon(super.block.getIcon(150, md), super.block.getIcon(255, md), super.block.getIcon(239, md), super.block.getIcon(239, md), super.block.getIcon(155, md), super.block.getIcon(155, md));
			} else {
				IIcon tx = super.block.getIcon(144 + (tb.Charged ? 1 : 0), md);
				this.context.setIcon(super.block.getIcon(150, md), super.block.getIcon(151, md), tx, tx, super.block.getIcon(155, md), super.block.getIcon(155, md));
			}
			
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.orientTextureNew(tb.Rotation);
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
		this.context.setIcon(super.block.getIcon(150, md), super.block.getIcon(151, md), super.block.getIcon(144, md), super.block.getIcon(144, md), super.block.getIcon(155, md), super.block.getIcon(155, md));
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
