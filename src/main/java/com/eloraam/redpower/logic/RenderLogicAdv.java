package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;
import com.eloraam.redpower.logic.TileLogicAdv;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class RenderLogicAdv extends RenderLogic {
	
	RenderModel modelXcvr = RenderModel.loadModel("/assets/rplogic/models/busxcvr.obj");
	ResourceLocation modelRes = new ResourceLocation("rplogic", "models/arraytex.png");
	
	public RenderLogicAdv(Block bl) {
		super(bl);
	}
	
	@Override
	protected int getTorchState(TileLogic tl) {
		//int md = tl.getExtendedMetadata();
		return 0;
	}
	
	@Override
	protected int getInvTorchState(int md) {
		return 0;
	}
	
	@Override
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		//int md = tl.getExtendedMetadata();
		return null;
	}
	
	@Override
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		return null;
	}
	
	@Override
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
		int md = tl.getExtendedMetadata();
		TileLogicAdv tls = (TileLogicAdv) tl;
		switch (md) {
			case 0:
				TileLogicAdv.LogicAdvXcvr lsc = (TileLogicAdv.LogicAdvXcvr) tls.getLogicStorage(TileLogicAdv.LogicAdvXcvr.class);
				//super.context.bindTexture("/eloraam/logic/array1.png");
				Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
				
				super.context.bindModelOffset(this.modelXcvr, 0.5D, 0.5D, 0.5D);
				super.context.setTint(1.0F, 1.0F, 1.0F);
				boolean b = (3552867 >> tl.Rotation & 1) == 0;
				super.context.renderModelGroup(1, 1 + (b ? 1 : 0) + (tl.Deadmap == 0 ? 2 : 0));
				super.context.renderModelGroup(2, 1 + ((tl.PowerState & 1) > 0 ? 1 : 0) + ((tl.PowerState & 4) > 0 ? 2 : 0));
				
				for (int i = 0; i < 4; ++i) {
					if (tl.Deadmap == 0) {
						super.context.renderModelGroup(3 + i, 1 + (lsc.State2 >> 4 * i & 15));
						super.context.renderModelGroup(7 + i, 1 + (lsc.State1 >> 4 * i & 15));
					} else {
						super.context.renderModelGroup(3 + i, 1 + (lsc.State1 >> 4 * i & 15));
						super.context.renderModelGroup(7 + i, 1 + (lsc.State2 >> 4 * i & 15));
					}
				}
				return;
			default:
		}
	}
	
	@Override
	protected void renderInvPart(int md) {
		switch (md) {
			case 1024:
				Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.bindModelOffset(this.modelXcvr, 0.5D, 0.5D, 0.5D);
				super.context.setTint(1.0F, 1.0F, 1.0F);
				super.context.renderModelGroup(1, 1);
				super.context.renderModelGroup(2, 1);
				
				for (int i = 0; i < 8; ++i) {
					super.context.renderModelGroup(3 + i, 1);
				}
				
				super.context.useNormal = false;
				tessellator.draw();
			default:
				break;
		}
	}
}
