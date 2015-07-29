package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.machine.TileFrame;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderFrame extends RenderCovers {
	
	public RenderFrame(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		//boolean cons = false;
		TileFrame tc = (TileFrame) CoreLib.getTileEntity(iba, i, j, k, TileFrame.class);
		if (tc != null) {
			super.context.setDefaults();
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.readGlobalLights(iba, i, j, k);
			int m;
			if (tc.CoverSides > 0) {
				short[] n = new short[6];
				
				for (m = 0; m < 6; ++m) {
					n[m] = tc.Covers[m];
					int tx = n[m] >> 8;
					if (tx == 1 || tx == 4) {
						n[m] = (short) (n[m] - 256);
					}
				}
				super.coverRenderer.render(tc.CoverSides, n);
			}
			
			super.context.exactTextureCoordinates = true;
			//RenderLib.bindTexture("/eloraam/machine/machine1.png", 1);
			super.context.setIcon(getIcon(2, md));
			
			for (int var12 = 0; var12 < 6; ++var12) {
				m = 1 << var12;
				byte var13 = 1;
				super.coverRenderer.start();
				if ((tc.CoverSides & m) > 0) {
					if ((tc.StickySides & m) > 0) {
						var13 = 4;
					} else {
						var13 = 2;
					}
				} else {
					m |= 1 << (var12 ^ 1);
					super.context.setIconNum(var12 ^ 1, getIcon(1, md));
				}
				
				super.context.setIconNum(var12, getIcon(var13, md));
				super.coverRenderer.setSize(var12, 0.0625F);
				super.context.calcBoundsGlobal();
				super.context.renderGlobFaces(m);
			}
			
			super.context.exactTextureCoordinates = false;
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
		super.context.setIcon(getIcon(1, md));
		this.doubleBox(63, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.01F);
		tessellator.draw();
		//RenderLib.unbindTexture();
		super.context.useNormal = false;
	}
	
	void doubleBox(int sides, float x1, float y1, float z1, float x2, float y2, float z2, float ino) {
		int s2 = sides << 1 & 42 | sides >> 1 & 21;
		super.context.renderBox(sides, x1, y1, z1, x2, y2, z2);
		super.context.renderBox(s2, x2 - ino, y2 - ino, z2 - ino, x1 + ino, y1 + ino, z1 + ino);
	}
}
