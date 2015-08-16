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
import net.minecraftforge.common.util.ForgeDirection;

public class RenderFrame extends RenderCovers {
	
	public RenderFrame(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		//boolean cons = false;
		TileFrame tc = (TileFrame) CoreLib.getTileEntity(iba, z, y, z, TileFrame.class);
		if (tc != null) {
			super.context.setDefaults();
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(x, y, z);
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.readGlobalLights(iba, x, y, z);
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
			//RenderLib.bindTexture("/eloraam/machine/machine1.png", 1); //TODO: Strange int param
			super.context.setIcon(getIcon(ForgeDirection.NORTH.ordinal(), md));
			
			for (int side = 0; side < 6; ++side) {
				m = 1 << side;
				int index = ForgeDirection.UNKNOWN.ordinal();
				super.coverRenderer.start();
				if ((tc.CoverSides & m) > 0) {
					if ((tc.StickySides & m) > 0) {
						index = ForgeDirection.SOUTH.ordinal();
					} else {
						index = ForgeDirection.NORTH.ordinal();
					}
				} else {
					m |= 1 << (side ^ 1);
					super.context.setIconNum(side ^ 1, getIcon(ForgeDirection.UNKNOWN.ordinal(), md));
				}
				
				super.context.setIconNum(side, getIcon(index, md));
				super.coverRenderer.setSize(side, 0.0625F);
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
		super.context.setIcon(getIcon(ForgeDirection.UNKNOWN.ordinal(), md));
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
