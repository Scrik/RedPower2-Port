package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;
import com.eloraam.redpower.logic.TileLogicArray;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class RenderLogicArray extends RenderLogic {
	
	RenderModel model = RenderModel.loadModel("/assets/rplogic/models/arraycells.obj");
	ResourceLocation modelRes = new ResourceLocation("rplogic", "models/arraytex.png");
	private static RenderLogic.TorchPos[] torchMapInvert = new RenderLogic.TorchPos[] { new RenderLogic.TorchPos( 0.0D, -0.25D, 0.0D, 0.7D) };
	private static RenderLogic.TorchPos[] torchMapNonInv = new RenderLogic.TorchPos[] { new RenderLogic.TorchPos( 0.0D, -0.25D, 0.0D, 0.7D), new RenderLogic.TorchPos(-0.188D, -0.25D, 0.219D, 0.7D) };
	
	public RenderLogicArray(Block bl) {
		super(bl);
	}
	
	@Override
	protected int getTorchState(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 1:
				return tl.Powered ? 1 : 0;
			case 2:
				return tl.Powered ? 1 : 2;
			default:
				return 0;
		}
	}
	
	@Override
	protected int getInvTorchState(int md) {
		return md == 514 ? 2 : 0;
	}
	
	@Override
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 1:
				return torchMapInvert;
			case 2:
				return torchMapNonInv;
			default:
				return null;
		}
	}
	
	@Override
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		switch (md) {
			case 513:
				return torchMapInvert;
			case 514:
				return torchMapNonInv;
			default:
				return null;
		}
	}
	
	public static int getFacingDir(int rot, int rel) {
		short n;
		switch (rot >> 2) {
			case 0:
				n = 13604;
				break;
			case 1:
				n = 13349;
				break;
			case 2:
				n = 20800;
				break;
			case 3:
				n = 16720;
				break;
			case 4:
				n = 8496;
				break;
			default:
				n = 12576;
		}
		
		int n1 = n >> ((rot + rel & 3) << 2);
		n1 &= 7;
		return n1;
	}
	
	private boolean isArrayTopwire(IBlockAccess iba, WorldCoord wc, int mask, int dir) {
		wc = wc.coordStep(dir);
		TileLogicArray tla = (TileLogicArray) CoreLib.getTileEntity(iba, wc,
				TileLogicArray.class);
		if (tla == null) {
			return false;
		} else {
			int m = tla.getTopwireMask();
			m &= RedPowerLib.getConDirMask(dir);
			m = (m & 1431655765) << 1 | (m & 715827882) >> 1;
			m &= mask;
			return m > 0;
		}
	}
	
	@Override
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
		if (tl instanceof TileLogicArray) {
			Tessellator tess = Tessellator.instance;
			boolean isDrawing = false;
			try {
				tess.startDrawingQuads();
				tess.draw();
			} catch(IllegalStateException exc) {
				isDrawing = true;
			}
			
			if(isDrawing) {
				tess.draw();
			}
			
			TileLogicArray tla = (TileLogicArray) tl;
			int md = tl.getExtendedMetadata();
			Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
			
			tess.startDrawingQuads();
			
			super.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
			super.context.setTint(1.0F, 1.0F, 1.0F);
			super.context.renderModelGroup(0, 0);
			switch (md) {
				case 0:
					super.context.renderModelGroup(1, 1);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal1 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(2, 1);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal2 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(3, 1);
					break;
				case 1:
					super.context.renderModelGroup(1, 2 + (tla.PowerVal1 > 0 ? 1 : 0));
					super.context.renderModelGroup(5, 0);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal1 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(2, 2);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal2 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(3, 2);
					break;
				case 2:
					super.context.renderModelGroup(1, 4 + (tla.PowerVal1 > 0 ? 1 : 0) + (tla.Powered ? 0 : 2));
					super.context.renderModelGroup(5, 0);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal1 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(2, 2);
					super.context.setTint(0.3F + 0.7F * (tla.PowerVal2 / 255.0F), 0.0F, 0.0F);
					super.context.renderModelGroup(3, 2);
			}
			
			int fd = getFacingDir(tla.Rotation, 1);
			int fm = tla.getTopwireMask();
			WorldCoord wc = new WorldCoord(tl);
			super.context.renderModelGroup(4, (this.isArrayTopwire(iba, wc, fm, fd) ? 0 : 1) + (this.isArrayTopwire(iba, wc, fm, fd ^ 1) ? 0 : 2));
			
			tess.draw();
			
			super.context.bindBlockTexture();
			if(isDrawing) {
				tess.startDrawingQuads();
			}
		}
	}
	
	@Override
	protected void renderInvPart(int md) {
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		super.context.useNormal = true;
		super.context.bindModelOffset(this.model, 0.5D, 0.5D, 0.5D);
		super.context.setTint(1.0F, 1.0F, 1.0F);
		super.context.renderModelGroup(0, 0);
		switch (md) {
			case 512:
				super.context.renderModelGroup(1, 1);
				super.context.setTint(0.3F, 0.0F, 0.0F);
				super.context.renderModelGroup(2, 1);
				super.context.renderModelGroup(3, 1);
				super.context.renderModelGroup(4, 3);
				break;
			case 513:
				super.context.renderModelGroup(1, 2);
				super.context.renderModelGroup(5, 0);
				super.context.setTint(0.3F, 0.0F, 0.0F);
				super.context.renderModelGroup(2, 2);
				super.context.renderModelGroup(3, 2);
				super.context.renderModelGroup(4, 3);
				break;
			case 514:
				super.context.renderModelGroup(1, 6);
				super.context.renderModelGroup(5, 0);
				super.context.setTint(0.3F, 0.0F, 0.0F);
				super.context.renderModelGroup(2, 2);
				super.context.renderModelGroup(3, 2);
				super.context.renderModelGroup(4, 3);
		}
		
		super.context.useNormal = false;
		tessellator.draw();
	}
	
}
