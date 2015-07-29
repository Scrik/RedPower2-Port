package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.machine.RenderTube;
import com.eloraam.redpower.machine.TileRedstoneTube;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderRedstoneTube extends RenderTube {
	
	public RenderRedstoneTube(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		//boolean cons = false;
		TileRedstoneTube tt = (TileRedstoneTube) CoreLib.getTileEntity(iba, i, j, k, TileRedstoneTube.class);
		if (tt != null) {
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			if (tt.CoverSides > 0) {
				super.context.readGlobalLights(iba, i, j, k);
				this.renderCovers(tt.CoverSides, tt.Covers);
			}
			
			int cons1 = TubeLib.getConnections(iba, i, j, k) | tt.getConnectionMask() >> 24;
			super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.setPos(i, j, k);
			int ps = (tt.PowerState + 84) / 85;
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			this.renderCenterBlock(cons1, super.block.getIcon(68 + ps, md), super.block.getIcon(72 + ps, md));
			if (tt.paintColor > 0) {
				int tc = super.paintColors[tt.paintColor - 1];
				super.context.setTint((tc >> 16) / 255.0F, (tc >> 8 & 255) / 255.0F, (tc & 255) / 255.0F);
				this.renderBlockPaint(cons1, 66, md);
			}
			//RenderLib.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		super.context.setDefaults();
		super.context.setPos(-0.5D, -0.5D, -0.5D);
		super.context.useNormal = true;
		super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		//RenderLib.bindTexture("/eloraam/machine/machine1.png");
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		super.context.useNormal = true;
		IIcon vertIcon = super.block.getIcon(72, md);
		IIcon sideIcon = super.block.getIcon(68, md);
		super.context.setIcon(vertIcon, vertIcon, sideIcon, sideIcon, sideIcon, sideIcon);
		super.context.renderBox(63, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
		super.context.renderBox(63, 0.7400000095367432D, 0.9900000095367432D,
				0.7400000095367432D, 0.25999999046325684D,
				0.009999999776482582D, 0.25999999046325684D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		super.context.useNormal = false;
	}
}
