package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.machine.RenderTube;
import com.eloraam.redpower.machine.TileFrameTube;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderFrameTube extends RenderTube {
	
	public RenderFrameTube(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		boolean cons = false;
		TileFrameTube tc = (TileFrameTube) CoreLib.getTileEntity(iba, i, j, k,
				TileFrameTube.class);
		if (tc != null) {
			super.context.setDefaults();
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.readGlobalLights(iba, i, j, k);
			int pc;
			int m;
			if (tc.CoverSides > 0) {
				short[] sides = new short[6];
				
				for (pc = 0; pc < 6; ++pc) {
					sides[pc] = tc.Covers[pc];
					m = sides[pc] >> 8;
					if (m == 1 || m == 4) {
						sides[pc] = (short) (sides[pc] - 256);
					}
				}
				
				super.coverRenderer.render(tc.CoverSides, sides);
			}
			
			int var13 = TubeLib.getConnections(iba, i, j, k);
			super.context.exactTextureCoordinates = true;
			//RenderLib.bindTexture("/eloraam/machine/machine1.png", 1);
			super.context.setIcon(getIcon(2, md));
			int var14 = tc.CoverSides | var13;
			
			for (pc = 0; pc < 6; ++pc) {
				m = 1 << pc;
				byte tx = 1;
				super.coverRenderer.start();
				if ((var14 & m) > 0) {
					if ((tc.StickySides & m) > 0) {
						tx = 4;
					} else {
						tx = 2;
					}
				} else {
					m |= 1 << (pc ^ 1);
					super.context.setIconNum(pc ^ 1, getIcon(1, md));
				}
				
				super.context.setIconNum(pc, getIcon(tx, md));
				super.coverRenderer.setSize(pc, 0.0625F);
				super.context.calcBoundsGlobal();
				super.context.renderGlobFaces(m);
			}
			
			super.context.exactTextureCoordinates = false;
			//RenderLib.unbindTexture();
			super.context.setBrightness(super.block.getMixedBrightnessForBlock( iba, i, j, k));
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			this.renderCenterBlock(var13, getIcon(64, md), getIcon(65, md));
			if (tc.paintColor > 0) {
				pc = super.paintColors[tc.paintColor - 1];
				super.context.setTint((pc >> 16) / 255.0F,
						(pc >> 8 & 255) / 255.0F, (pc & 255) / 255.0F);
				this.renderBlockPaint(var13, 66, md);
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
		super.context.setIcon(getIcon(2, md), getIcon(2, md), getIcon(1, md), getIcon(1, md), getIcon(1, md), getIcon(1, md));
		this.doubleBox(63, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.01F);
		IIcon iconVert = getIcon(65, md);
		IIcon iconSide = getIcon(64, md);
		super.context.setIcon(iconVert, iconVert, iconSide, iconSide, iconSide, iconSide);
		super.context.renderBox(63, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
		super.context.renderBox(63, 0.7400000095367432D, 0.9900000095367432D,
				0.7400000095367432D, 0.25999999046325684D,
				0.009999999776482582D, 0.25999999046325684D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		super.context.useNormal = false;
	}
	
	void doubleBox(int sides, float x1, float y1, float z1, float x2, float y2,
			float z2, float ino) {
		int s2 = sides << 1 & 42 | sides >> 1 & 21;
		super.context.renderBox(sides, x1, y1, z1, x2, y2, z2);
		super.context.renderBox(s2, x2 - ino, y2 - ino, z2 - ino, x1 + ino, y1
				+ ino, z1 + ino);
	}
}
