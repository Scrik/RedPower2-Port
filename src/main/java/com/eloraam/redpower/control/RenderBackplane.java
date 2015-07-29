package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderBackplane extends RenderCustomBlock {
	
	RenderContext context = new RenderContext();
	
	public RenderBackplane(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileBackplane tb = (TileBackplane) CoreLib.getTileEntity(iba, i, j, k, TileBackplane.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			//this.context.bindTexture("/eloraam/control/control1.png");
			this.context.setOrientation(0, tb.Rotation);
			this.context.setPos(i, j, k);
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			IIcon fs0 = RedPowerControl.blockBackplane.getIcon(0, md);
			IIcon fs1 = RedPowerControl.blockBackplane.getIcon(1, md);
			IIcon fs2 = RedPowerControl.blockBackplane.getIcon(2, md);
			IIcon sd0 = RedPowerControl.blockBackplane.getIcon(3, md);
			IIcon sd1 = RedPowerControl.blockBackplane.getIcon(4, md);
			IIcon sd2 = RedPowerControl.blockBackplane.getIcon(5, md);
			if (md == 0) {
				this.context.setIcon(null, fs2, fs1, fs1, fs0, fs0);
				this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
			} else if (md == 1) {
				this.context.setIcon(null, sd2, sd1, sd1, sd0, sd0);
				this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			}
			//this.context.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.useNormal = true;
		this.context.setOrientation(0, 3);
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		//this.context.bindTexture("/eloraam/control/control1.png");
		this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		tessellator.startDrawingQuads();
		IIcon fs0 = RedPowerControl.blockBackplane.getIcon(0, md);
		IIcon fs1 = RedPowerControl.blockBackplane.getIcon(1, md);
		IIcon fs2 = RedPowerControl.blockBackplane.getIcon(2, md);
		IIcon sd0 = RedPowerControl.blockBackplane.getIcon(3, md);
		IIcon sd1 = RedPowerControl.blockBackplane.getIcon(4, md);
		IIcon sd2 = RedPowerControl.blockBackplane.getIcon(5, md);
		if (md == 0) {
			this.context.setIcon(null, fs2, fs1, fs1, fs0, fs0);
			this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
		} else if (md == 1) {
			this.context.setIcon(null, sd2, sd1, sd1, sd0, sd0);
			this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		}
		
		tessellator.draw();
		this.context.useNormal = false;
		//this.context.unbindTexture();
	}
}
