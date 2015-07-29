package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileMachine;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderMachine extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderMachine(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileMachine tb = (TileMachine) CoreLib.getTileEntity(iba, i, j, k, TileMachine.class);
		if (tb != null) {
			Tessellator tess = Tessellator.instance;
			tess.draw();
			
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			int tex;
			if (md == 0) {
				tex = tb.Active ? 1 : 0;
				this.context.setIcon(getIcon(48, md), getIcon(53 + tex, md), getIcon(56, md), getIcon(56, md), getIcon(55, md), getIcon(55,md));
			} else {
				int t2;
				if (md == 4) {
					tex = 96 + (tb.Active ? 1 : 0);
					t2 = tex + 2 + (tb.Powered ? 2 : 0);
					this.context.setIcon(getIcon(102, md), getIcon(103, md), getIcon(tex, md), getIcon(tex, md), getIcon(t2, md), getIcon(t2, md));
				} else if (md == 5) {
					tex = tb.Charged ? (tb.Active ? 2 : 1) : 0;
					t2 = 116 + (tb.Charged ? 1 : 0) + (tb.Active ? 2 : 0);
					this.context.setIcon(getIcon(113 + tex, md), getIcon(112, md), getIcon(t2, md), getIcon(t2, md), getIcon(t2, md), getIcon(t2, md));
				} else if (md == 8) {
					tex = 120 + (tb.Charged ? 1 : 0) + (tb.Delay | tb.Active ? 2 : 0);
					this.context.setIcon(getIcon(124, md), getIcon(125, md), getIcon(tex, md), getIcon(tex, md), getIcon(tex, md), getIcon(tex, md));
				} else if (md == 10) {
					tex = 104 + (tb.Active ? 1 : 0);
					t2 = tex + 2 + (tb.Powered ? 2 : 0);
					this.context.setIcon(getIcon(102, md), getIcon(103, md), getIcon(tex, md), getIcon(tex, md), getIcon(t2, md), getIcon(t2, md));
				} else if (md == 12) {
					tex = tb.Active ? 1 : 0;
					this.context.setIcon(getIcon(48, md), getIcon(164 + tex, md), getIcon(167, md), getIcon(167, md), getIcon(166, md), getIcon(166, md));
				} else if (md == 13) {
					tex = tb.Active ? 1 : 0;
					this.context.setIcon(getIcon(172 + tex, md), getIcon(168 + tex, md), getIcon(171, md), getIcon(171, md), getIcon(170, md), getIcon(170, md));
				} else if (md == 14) {
					tex = tb.Active ? 1 : 0;
					this.context.setIcon(getIcon(58, md), getIcon(89, md), getIcon(91 + tex, md), getIcon(91 + tex, md), getIcon(90, md), getIcon(90, md));
				} else if (md == 15) {
					tex = tb.Active ? 1 : 0;
					this.context.setIcon(getIcon(58, md), getIcon(89, md), getIcon(93 + tex, md), getIcon(93 + tex, md), getIcon(90, md), getIcon(90, md));
				} else {
					tex = 59 + (tb.Active ? 1 : 0) + (md == 3 ? 2 : 0);
					this.context.setIcon(getIcon(58, md), getIcon(57, md), getIcon(tex, md), getIcon(tex, md), getIcon(tex, md), getIcon(tex, md));
				}
			}
			
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.orientTextures(tb.Rotation);
			
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			tess.startDrawingQuads();
			this.context.renderGlobFaces(63);
			tess.draw();
			//RenderLib.unbindTexture();
			
			this.context.bindBlockTexture();
			tess.startDrawingQuads();
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
			this.context.setIcon(getIcon(48, md), getIcon(53, md), getIcon(56, md), getIcon(56, md), getIcon(55, md), getIcon(55, md));
		} else if (md == 2) {
			this.context.setIcon(getIcon(58, md), getIcon(57, md), getIcon(59, md), getIcon(59, md), getIcon(59, md), getIcon(59, md));
		} else if (md == 4) {
			this.context.setIcon(getIcon(102, md), getIcon(103, md), getIcon(98, md), getIcon(98, md), getIcon(96, md), getIcon(96, md));
		} else if (md == 5) {
			this.context.setIcon(getIcon(113, md), getIcon(112, md), getIcon(117, md), getIcon(117, md), getIcon(117, md), getIcon(117, md));
		} else if (md == 8) {
			this.context.setIcon(getIcon(124, md), getIcon(125, md), getIcon(120, md), getIcon(120, md), getIcon(120, md), getIcon(120, md));
		} else if (md == 10) {
			this.context.setIcon(getIcon(102, md), getIcon(103, md), getIcon(106, md), getIcon(106, md), getIcon(104, md), getIcon(104, md));
		} else if (md == 12) {
			this.context.setIcon(getIcon(48, md), getIcon(164, md), getIcon(167, md), getIcon(167, md), getIcon(166, md), getIcon(166, md));
		} else if (md == 13) {
			this.context.setIcon(getIcon(172, md), getIcon(168, md), getIcon(171, md), getIcon(171, md), getIcon(170, md), getIcon(170, md));
		} else if (md == 14) {
			this.context.setIcon(getIcon(58, md), getIcon(89, md), getIcon(91, md), getIcon(91, md), getIcon(90, md), getIcon(90, md));
		} else if (md == 15) {
			this.context.setIcon(getIcon(58, md), getIcon(89, md), getIcon(93, md), getIcon(93, md), getIcon(90, md), getIcon(90, md));
		} else {
			this.context.setIcon(getIcon(58, md), getIcon(57, md), getIcon(61, md), getIcon(61, md), getIcon(61, md), getIcon(61, md));
		}
		
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
