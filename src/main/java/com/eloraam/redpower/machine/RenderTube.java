package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.machine.TileTube;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderTube extends RenderCovers {
	
	int[] paintColors = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, '\uff00', 16737408, 5460819, 9671571, '\uffff', 8388863, 255, 5187328, '\u8000', 16711680, 2039583 };
	
	public RenderTube(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		boolean cons = false;
		TileTube tt = (TileTube) CoreLib.getTileEntity(iba, i, j, k, TileTube.class);
		if (tt != null) {
			super.context.exactTextureCoordinates = true;
			super.context.setTexFlags(55);
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.setPos(i, j, k);
			if (tt.CoverSides > 0) {
				super.context.readGlobalLights(iba, i, j, k);
				this.renderCovers(tt.CoverSides, tt.Covers);
			}
			
			int cons1 = TubeLib.getConnections(iba, i, j, k);
			super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.setPos(i, j, k);
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			if (md == 10) {
				this.renderCenterBlock(cons1, super.block.getIcon(76, md), super.block.getIcon(77,md));
			} else if (md == 11) {
				if (this.renderMagFins(cons1, md)) {
					this.renderCenterBlock(cons1, super.block.getIcon(23, md), super.block.getIcon(22, md));
				} else {
					this.renderCenterBlock(cons1, super.block.getIcon(21, md), super.block.getIcon(22, md));
				}
			} else {
				this.renderCenterBlock(cons1, super.block.getIcon(64, md), super.block.getIcon(65, md));
			}
			
			if (tt.paintColor > 0) {
				int tc = this.paintColors[tt.paintColor - 1];
				super.context.setTint((tc >> 16) / 255.0F,
						(tc >> 8 & 255) / 255.0F, (tc & 255) / 255.0F);
				if (md == 10) {
					this.renderBlockPaint(cons1, 78, md);
				} else {
					this.renderBlockPaint(cons1, 66, md);
				}
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
		IIcon icon65 = super.block.getIcon(65, md);
		if (md >> 8 == 10) {
			IIcon icon76 = super.block.getIcon(76, md);
			super.context.setIcon(icon65, icon65, icon76, icon76, icon76, icon76);
		} else if (md >> 8 == 11) {
			IIcon icon21 = super.block.getIcon(21, md);
			IIcon icon22 = super.block.getIcon(22, md);
			this.renderMagFins(3, md);
			super.context.setIcon(icon22, icon22, icon21, icon21, icon21, icon21);
		} else {
			IIcon icon64 = super.block.getIcon(64, md);
			super.context.setIcon(icon65, icon65, icon64, icon64, icon64, icon64);
		}
		
		super.context.renderBox(63, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
		super.context.renderBox(63, 0.7400000095367432D, 0.9900000095367432D,
				0.7400000095367432D, 0.25999999046325684D,
				0.009999999776482582D, 0.25999999046325684D);
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
	
	public boolean renderMagFins(int cons, int md) {
		IIcon icon24 = super.block.getIcon(24, md);
		IIcon icon25 = super.block.getIcon(25, md);
		if (cons == 3) {
			super.context.setTexFlags(0);
			super.context.setIcon(icon25, icon25, icon24, icon24, icon24, icon24);
			super.context.renderBox(63, 0.125D, 0.125D, 0.125D, 0.875D, 0.375D, 0.875D);
			super.context.renderBox(63, 0.125D, 0.625D, 0.125D, 0.875D, 0.875D, 0.875D);
			return true;
		} else if (cons == 12) {
			super.context.setTexFlags(147492);
			super.context.setIcon(icon24, icon24, icon25, icon25, icon24, icon24);
			super.context.renderBox(63, 0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.375D);
			super.context.renderBox(63, 0.125D, 0.125D, 0.625D, 0.875D, 0.875D, 0.875D);
			return true;
		} else if (cons == 48) {
			super.context.setTexFlags(2304);
			super.context.setIcon(icon24, icon24, icon24, icon24, icon25, icon25);
			super.context.renderBox(63, 0.125D, 0.125D, 0.125D, 0.375D, 0.875D, 0.875D);
			super.context.renderBox(63, 0.625D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D);
			return true;
		} else {
			return false;
		}
	}
	
	public void renderCenterBlock(int cons, IIcon side, IIcon end) {
		if (cons == 0) {
			super.context.setIcon(end);
			this.doubleBox(63, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
		} else if (cons == 3) {
			super.context.setTexFlags(1773);
			super.context.setIcon(end, end, side, side, side, side);
			this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
		} else if (cons == 12) {
			super.context.setTexFlags(184365);
			super.context.setIcon(side, side, end, end, side, side);
			this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
		} else if (cons == 48) {
			super.context.setTexFlags(187200);
			super.context.setIcon(side, side, side, side, end, end);
			this.doubleBox(15, 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
		} else {
			super.context.setIcon(end);
			this.doubleBox(63 ^ cons, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
			if ((cons & 1) > 0) {
				super.context.setTexFlags(1773);
				super.context.setIcon(end, end, side, side, side, side);
				this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 0.25F, 0.75F);
			}
			
			if ((cons & 2) > 0) {
				super.context.setTexFlags(1773);
				super.context.setIcon(end, end, side, side, side, side);
				this.doubleBox(60, 0.25F, 0.75F, 0.25F, 0.75F, 1.0F, 0.75F);
			}
			
			if ((cons & 4) > 0) {
				super.context.setTexFlags(184365);
				super.context.setIcon(side, side, end, end, side, side);
				this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.25F);
			}
			
			if ((cons & 8) > 0) {
				super.context.setTexFlags(184365);
				super.context.setIcon(side, side, end, end, side, side);
				this.doubleBox(51, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, 1.0F);
			}
			
			if ((cons & 16) > 0) {
				super.context.setTexFlags(187200);
				super.context.setIcon(side, side, side, side, end, end);
				this.doubleBox(15, 0.0F, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F);
			}
			
			if ((cons & 32) > 0) {
				super.context.setTexFlags(187200);
				super.context.setIcon(side, side, side, side, end, end);
				this.doubleBox(15, 0.75F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
			}
			
		}
	}
	
	public void renderBlockPaint(int cons, int tx1, int meta) {
		IIcon tex1 = super.block.getIcon(tx1, meta);
		IIcon tex2 = super.block.getIcon(tx1 + 1, meta);
		if (cons != 0) {
			if (cons == 3) {
				super.context.setTexFlags(1773);
				super.context.setIcon(null, null, tex2, tex2, tex2, tex2);
				this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
			} else if (cons == 12) {
				super.context.setTexFlags(184365);
				super.context.setIcon(tex2, tex2, null, null, tex2, tex2);
				this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
			} else if (cons == 48) {
				super.context.setTexFlags(187200);
				super.context.setIcon(tex2, tex2, tex2, tex2, null, null);
				this.doubleBox(15, 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
			} else {
				super.context.setIcon(tex1);
				this.doubleBox(63 ^ cons, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F,
						0.75F);
				if ((cons & 1) > 0) {
					super.context.setTexFlags(1773);
					super.context.setIcon(tex1, tex1, tex2, tex2, tex2,
							tex2);
					this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 0.25F, 0.75F);
				}
				
				if ((cons & 2) > 0) {
					super.context.setTexFlags(1773);
					super.context.setIcon(tex1, tex1, tex2, tex2, tex2, tex2);
					this.doubleBox(60, 0.25F, 0.75F, 0.25F, 0.75F, 1.0F, 0.75F);
				}
				
				if ((cons & 4) > 0) {
					super.context.setTexFlags(184365);
					super.context.setIcon(tex2, tex2, tex1, tex1, tex2,
							tex2);
					this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.25F);
				}
				
				if ((cons & 8) > 0) {
					super.context.setTexFlags(184365);
					super.context.setIcon(tex2, tex2, tex1, tex1, tex2,
							tex2);
					this.doubleBox(51, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, 1.0F);
				}
				
				if ((cons & 16) > 0) {
					super.context.setTexFlags(187200);
					super.context.setIcon(tex2, tex2, tex2, tex2,
							tex1, tex1);
					this.doubleBox(15, 0.0F, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F);
				}
				
				if ((cons & 32) > 0) {
					super.context.setTexFlags(187200);
					super.context.setIcon(tex2, tex2, tex2, tex2,
							tex1, tex1);
					this.doubleBox(15, 0.75F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
				}
				
			}
		}
	}
}
