package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.machine.RenderTube;
import com.eloraam.redpower.machine.TileFrameRedstoneTube;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderFrameRedstoneTube extends RenderTube {
	
	public RenderFrameRedstoneTube(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba,
			int i, int j, int k, int md) {
		//boolean cons = false;
		TileFrameRedstoneTube tc = (TileFrameRedstoneTube) CoreLib
				.getTileEntity(iba, i, j, k, TileFrameRedstoneTube.class);
		if (tc != null) {
			super.context.setDefaults();
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.readGlobalLights(iba, i, j, k);
			int ps;
			int pc;
			if (tc.CoverSides > 0) {
				short[] sides = new short[6];
				
				for (ps = 0; ps < 6; ++ps) {
					sides[ps] = tc.Covers[ps];
					pc = sides[ps] >> 8;
					if (pc == 1 || pc == 4) {
						sides[ps] = (short) (sides[ps] - 256);
					}
				}
				
				super.coverRenderer.render(tc.CoverSides, sides);
			}
			
			int var13 = TubeLib.getConnections(iba, i, j, k) | tc.getConnectionMask() >> 24;
			super.context.exactTextureCoordinates = true;
			//RenderLib.bindTexture("/eloraam/machine/machine1.png", 1);
			super.context.setIcon(getIcon(ForgeDirection.NORTH.ordinal(), md));
			int var14 = tc.CoverSides | var13;
			
			for (ps = 0; ps < 6; ++ps) {
				pc = 1 << ps;
				byte tx = 1;
				super.coverRenderer.start();
				if ((var14 & pc) > 0) {
					if ((tc.StickySides & pc) > 0) {
						tx = 4;
					} else {
						tx = 2;
					}
				} else {
					pc |= 1 << (ps ^ 1);
					super.context.setIconNum(ps ^ 1, getIcon(1, md));
				}
				
				super.context.setIconNum(ps, getIcon(tx, md));
				super.coverRenderer.setSize(ps, 0.0625F);
				super.context.calcBoundsGlobal();
				super.context.renderGlobFaces(pc);
			}
			
			super.context.exactTextureCoordinates = false;
			//RenderLib.unbindTexture();
			super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			ps = (tc.PowerState + 84) / 85;
			this.renderCenterBlock(var13, BlockMachine.redstoneTubeSideIcons[ps], BlockMachine.redstoneTubeFaceIcons[ps]);
			if (tc.paintColor > 0) {
				pc = super.paintColors[tc.paintColor - 1];
				super.context.setTint((pc >> 16) / 255.0F, (pc >> 8 & 255) / 255.0F, (pc & 255) / 255.0F);
				this.renderBlockPaint(var13, BlockMachine.baseTubeFaceColorIcon, BlockMachine.baseTubeSideColorIcon, md);
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
		IIcon iconVert = getIcon(72, md);
		IIcon iconSide = getIcon(68, md);
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
