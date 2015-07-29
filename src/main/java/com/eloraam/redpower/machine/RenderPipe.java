package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.machine.TilePipe;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderPipe extends RenderCovers {
	
	public RenderPipe(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		//boolean cons = false;
		TilePipe tt = (TilePipe) CoreLib.getTileEntity(iba, i, j, k, TilePipe.class);
		if (tt != null) {
			super.context.exactTextureCoordinates = true;
			super.context.setTexFlags(55);
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			if (tt.CoverSides > 0) {
				super.context.readGlobalLights(iba, i, j, k);
				this.renderCovers(tt.CoverSides, tt.Covers);
			}
			
			int cons1 = PipeLib.getConnections(iba, i, j, k);
			super.context.setBrightness(super.block.getMixedBrightnessForBlock(
					iba, i, j, k));
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.setPos(i, j, k);
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			IIcon vertIcon = super.block.getIcon(28, md);
			IIcon sideIcon = super.block.getIcon(26, md);
			this.renderCenterBlock(cons1, sideIcon, vertIcon);
			tt.cacheFlange();
			this.renderFlanges(tt.Flanges, super.block.getIcon(27, md));
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
		IIcon vertIcon = super.block.getIcon(28, md);
		IIcon sideIcon = super.block.getIcon(26, md);
		super.context.setIcon(vertIcon, vertIcon, sideIcon, sideIcon, sideIcon, sideIcon);
		super.context.renderBox(60, 0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
		super.context.renderBox(60, 0.6240000128746033D, 0.9990000128746033D,
				0.6240000128746033D, 0.37599998712539673D,
				0.0010000000474974513D, 0.37599998712539673D);
		this.renderFlanges(3, super.block.getIcon(27, md));
		tessellator.draw();
		//RenderLib.unbindTexture();
		super.context.useNormal = false;
	}
	
	void doubleBox(int sides, float x1, float y1, float z1, float x2, float y2,
			float z2) {
		int s2 = sides << 1 & 42 | sides >> 1 & 21;
		super.context.renderBox(sides, x1, y1, z1, x2, y2, z2);
		super.context.renderBox(s2, x2, y2, z2, x1, y1, z1);
	}
	
	public void renderFlanges(int cons, IIcon tex) {
		super.context.setIcon(tex);
		if ((cons & 1) > 0) {
			super.context.setTexFlags(0);
			super.context.renderBox(63, 0.25D, 0.0D, 0.25D, 0.75D, 0.125D,
					0.75D);
		}
		
		if ((cons & 2) > 0) {
			super.context.setTexFlags(112320);
			super.context.renderBox(63, 0.25D, 0.875D, 0.25D, 0.75D, 1.0D,
					0.75D);
		}
		
		if ((cons & 4) > 0) {
			super.context.setTexFlags(217134);
			super.context.renderBox(63, 0.25D, 0.25D, 0.0D, 0.75D, 0.75D,
					0.125D);
		}
		
		if ((cons & 8) > 0) {
			super.context.setTexFlags(188469);
			super.context.renderBox(63, 0.25D, 0.25D, 0.875D, 0.75D, 0.75D,
					1.0D);
		}
		
		if ((cons & 16) > 0) {
			super.context.setTexFlags(2944);
			super.context.renderBox(63, 0.0D, 0.25D, 0.25D, 0.125D, 0.75D,
					0.75D);
		}
		
		if ((cons & 32) > 0) {
			super.context.setTexFlags(3419);
			super.context.renderBox(63, 0.875D, 0.25D, 0.25D, 1.0D, 0.75D,
					0.75D);
		}
		
	}
	
	public void renderCenterBlock(int cons, IIcon side, IIcon end) {
		if (cons == 0) {
			super.context.setIcon(end);
			this.doubleBox(63, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
		} else if (cons == 3) {
			super.context.setTexFlags(1773);
			super.context.setIcon(end, end, side, side, side, side);
			this.doubleBox(60, 0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
		} else if (cons == 12) {
			super.context.setTexFlags(184365);
			super.context.setIcon(side, side, end, end, side, side);
			this.doubleBox(51, 0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
		} else if (cons == 48) {
			super.context.setTexFlags(187200);
			super.context.setIcon(side, side, side, side, end, end);
			this.doubleBox(15, 0.0F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
		} else {
			super.context.setIcon(end);
			this.doubleBox(63 ^ cons, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F,
					0.625F);
			if ((cons & 1) > 0) {
				super.context.setTexFlags(1773);
				super.context.setIcon(end, end, side, side, side, side);
				this.doubleBox(60, 0.375F, 0.0F, 0.375F, 0.625F, 0.375F, 0.625F);
			}
			
			if ((cons & 2) > 0) {
				super.context.setTexFlags(1773);
				super.context.setIcon(end, end, side, side, side, side);
				this.doubleBox(60, 0.375F, 0.625F, 0.375F, 0.625F, 1.0F, 0.625F);
			}
			
			if ((cons & 4) > 0) {
				super.context.setTexFlags(184365);
				super.context.setIcon(side, side, end, end, side, side);
				this.doubleBox(51, 0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 0.375F);
			}
			
			if ((cons & 8) > 0) {
				super.context.setTexFlags(184365);
				super.context.setIcon(side, side, end, end, side, side);
				this.doubleBox(51, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F, 1.0F);
			}
			
			if ((cons & 16) > 0) {
				super.context.setTexFlags(187200);
				super.context.setIcon(side, side, side, side, end, end);
				this.doubleBox(15, 0.0F, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F);
			}
			
			if ((cons & 32) > 0) {
				super.context.setTexFlags(187200);
				super.context.setIcon(side, side, side, side, end, end);
				this.doubleBox(15, 0.625F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
			}
			
		}
	}
}
