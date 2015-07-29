package com.eloraam.redpower.core;

import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.Vector3;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class RenderLib {
	
	private static RenderLib.RenderListEntry[] renderers = new RenderLib.RenderListEntry[4096];
	
	public static void renderSpecialLever(Vector3 pos, Quat rot, IIcon tex1, IIcon tex2) {
		//int k1 = (tex & 15) << 4;
		//int l1 = tex & 240;
		Vector3[] pl = new Vector3[8];
		float f8 = 0.0625F;
		float f9 = 0.0625F;
		float f10 = 0.375F;
		pl[0] = new Vector3((double) (-f8), 0.0D, (double) (-f9));
		pl[1] = new Vector3((double) f8, 0.0D, (double) (-f9));
		pl[2] = new Vector3((double) f8, 0.0D, (double) f9);
		pl[3] = new Vector3((double) (-f8), 0.0D, (double) f9);
		pl[4] = new Vector3((double) (-f8), (double) f10, (double) (-f9));
		pl[5] = new Vector3((double) f8, (double) f10, (double) (-f9));
		pl[6] = new Vector3((double) f8, (double) f10, (double) f9);
		pl[7] = new Vector3((double) (-f8), (double) f10, (double) f9);
		
		for (int i = 0; i < 8; ++i) {
			rot.rotate(pl[i]);
			pl[i].add(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D);
		}
		
		float u1 = /*(float) (k1 + 7) / 256.0F;*/ tex1.getMinU();
		float u2 = /*((float) (k1 + 9) - 0.01F) / 256.0F;*/ tex1.getMaxU();
		float v1 = /*(float) (l1 + 6) / 256.0F;*/ tex1.getMinV();
		float v2 = /*((float) (l1 + 8) - 0.01F) / 256.0F;*/ tex1.getMaxV();
		addVectWithUV(pl[0], (double) u1, (double) v2);
		addVectWithUV(pl[1], (double) u2, (double) v2);
		addVectWithUV(pl[2], (double) u2, (double) v1);
		addVectWithUV(pl[3], (double) u1, (double) v1);
		addVectWithUV(pl[7], (double) u1, (double) v2);
		addVectWithUV(pl[6], (double) u2, (double) v2);
		addVectWithUV(pl[5], (double) u2, (double) v1);
		addVectWithUV(pl[4], (double) u1, (double) v1);
		u1 = /*(float) (k1 + 7) / 256.0F;*/ tex2.getMinU();
		u2 = /*((float) (k1 + 9) - 0.01F) / 256.0F;*/ tex2.getMaxU();
		v1 = /*(float) (l1 + 6) / 256.0F;*/ tex2.getMinV();
		v2 = /*((float) (l1 + 12) - 0.01F) / 256.0F;*/ tex2.getMaxV();
		addVectWithUV(pl[1], (double) u1, (double) v2);
		addVectWithUV(pl[0], (double) u2, (double) v2);
		addVectWithUV(pl[4], (double) u2, (double) v1);
		addVectWithUV(pl[5], (double) u1, (double) v1);
		addVectWithUV(pl[2], (double) u1, (double) v2);
		addVectWithUV(pl[1], (double) u2, (double) v2);
		addVectWithUV(pl[5], (double) u2, (double) v1);
		addVectWithUV(pl[6], (double) u1, (double) v1);
		addVectWithUV(pl[3], (double) u1, (double) v2);
		addVectWithUV(pl[2], (double) u2, (double) v2);
		addVectWithUV(pl[6], (double) u2, (double) v1);
		addVectWithUV(pl[7], (double) u1, (double) v1);
		addVectWithUV(pl[0], (double) u1, (double) v2);
		addVectWithUV(pl[3], (double) u2, (double) v2);
		addVectWithUV(pl[7], (double) u2, (double) v1);
		addVectWithUV(pl[4], (double) u1, (double) v1);
	}
	
	public static void addVectWithUV(Vector3 vect, double u, double v) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.addVertexWithUV(vect.x, vect.y, vect.z, u, v);
	}
	
	public static void renderPointer(Vector3 pos, Quat rot) {
		Tessellator tessellator = Tessellator.instance;
		
		boolean isDrawing = false;
		try {
			tessellator.startDrawingQuads();
			tessellator.draw();
		} catch(IllegalStateException exc) {
			isDrawing = true;
		}
		
		if(isDrawing) {
			tessellator.draw();
		}

		tessellator.setColorOpaque_F(0.9F, 0.9F, 0.9F);
		Vector3[] pl = new Vector3[] { new Vector3(0.4D, 0.0D, 0.0D), new Vector3(
				0.0D, 0.0D, 0.2D), new Vector3(-0.2D, 0.0D, 0.0D), new Vector3(
				0.0D, 0.0D, -0.2D), new Vector3(0.4D, 0.1D, 0.0D), new Vector3(
				0.0D, 0.1D, 0.2D), new Vector3(-0.2D, 0.1D, 0.0D), new Vector3(
				0.0D, 0.1D, -0.2D) };
		
		for (int i = 0; i < 8; ++i) {
			rot.rotate(pl[i]);
			pl[i].add(pos);
		}
		
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/blocks/stone.png"));
		
		tessellator.startDrawingQuads();
		
		addVectWithUV(pl[0], 0.5, 1D / 16D);
		addVectWithUV(pl[1], 0.5 + 1D / 8D, 0.5);
		addVectWithUV(pl[2], 0.5, 0.5 + 1D / 8D);
		addVectWithUV(pl[3], 0.5 - 1D / 8D, 0.5);
		
		addVectWithUV(pl[4], 0.5, 1D / 16D);
		addVectWithUV(pl[7], 0.5 - 1D / 8D, 0.5);
		addVectWithUV(pl[6], 0.5, 0.5 + 1D / 8D);
		addVectWithUV(pl[5], 0.5 + 1D / 8D, 0.5);
		
		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		addVectWithUV(pl[0], 0.5, 1D / 16D);
		addVectWithUV(pl[4], 0.5, 1D / 16D);
		addVectWithUV(pl[5], 0.5 - 1D / 8D, 0.5);
		addVectWithUV(pl[1], 0.5 - 1D / 8D, 0.5);
		
		addVectWithUV(pl[0], 0.5 - 1D / 8D, 0.5);
		addVectWithUV(pl[3], 0.5 - 1D / 8D, 0.5);
		addVectWithUV(pl[7], 0.5, 0.5 + 1D / 8D);
		addVectWithUV(pl[4], 0.5, 0.5 + 1D / 8D);
		
		addVectWithUV(pl[2], 0.5, 0.5 + 1D / 8D);
		addVectWithUV(pl[6], 0.5, 0.5 + 1D / 8D);
		addVectWithUV(pl[7], 0.5 + 1D / 8D, 0.5);
		addVectWithUV(pl[3], 0.5 + 1D / 8D, 0.5);
		
		addVectWithUV(pl[2], 0.5 + 1D / 8D, 0.5);
		addVectWithUV(pl[1], 0.5 + 1D / 8D, 0.5);
		addVectWithUV(pl[5], 0.5, 1D / 16D);
		addVectWithUV(pl[6], 0.5, 1D / 16D);
		
		tessellator.draw();
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		if(isDrawing) {
			tessellator.startDrawingQuads();
		}
	}
	
	public static RenderCustomBlock getRenderer(Block bid, int md) {
		RenderLib.RenderListEntry rle = renderers[Block.getIdFromBlock(bid)];
		return rle == null ? null : rle.metaRenders[md];
	}
	
	public static RenderCustomBlock getInvRenderer(Block bid, int md) {
		RenderLib.RenderListEntry rle = renderers[Block.getIdFromBlock(bid)];
		if (rle == null) {
			return null;
		} else {
			int mdv = rle.mapDamageValue(md);
			return mdv > 15 ? rle.defaultRender : rle.metaRenders[rle.mapDamageValue(md)];
		}
	}
	
	private static RenderCustomBlock makeRenderer(Block bl, Class<? extends RenderCustomBlock> rcl) {
		try {
			RenderCustomBlock rnd = rcl.getDeclaredConstructor(new Class[] { Block.class }).newInstance(new Object[] { bl });
			return rnd;
		} catch (Throwable var4) {
			var4.printStackTrace();
			return null;
		}
	}
	
	public static void setRenderer(Block bl, Class<? extends RenderCustomBlock> rcl) {
		RenderCustomBlock rnd = makeRenderer(bl, rcl);
		if (renderers[Block.getIdFromBlock(bl)] == null) {
			renderers[Block.getIdFromBlock(bl)] = new RenderLib.RenderListEntry();
		}
		for (int i = 0; i < 16; ++i) {
			renderers[Block.getIdFromBlock(bl)].metaRenders[i] = rnd;
		}
	}
	
	public static void setRenderer(Block bl, int md, Class<? extends RenderCustomBlock> rcl) {
		RenderCustomBlock rnd = makeRenderer(bl, rcl);
		if (renderers[Block.getIdFromBlock(bl)] == null) {
			renderers[Block.getIdFromBlock(bl)] = new RenderLib.RenderListEntry();
		}
		renderers[Block.getIdFromBlock(bl)].metaRenders[md] = rnd;
	}
	
	public static void setHighRenderer(Block bl, int md, Class<? extends RenderCustomBlock> rcl) {
		RenderCustomBlock rnd = makeRenderer(bl, rcl);
		if (renderers[Block.getIdFromBlock(bl)] == null) {
			renderers[Block.getIdFromBlock(bl)] = new RenderLib.RenderShiftedEntry(8);
		}
		renderers[Block.getIdFromBlock(bl)].metaRenders[md] = rnd;
	}
	
	public static void setDefaultRenderer(Block bl, int shift, Class<? extends RenderCustomBlock> rcl) {
		RenderCustomBlock rnd = makeRenderer(bl, rcl);
		if (renderers[Block.getIdFromBlock(bl)] == null) {
			renderers[Block.getIdFromBlock(bl)] = new RenderLib.RenderShiftedEntry(shift);
		}
		for (int i = 0; i < 16; ++i) {
			if (renderers[Block.getIdFromBlock(bl)].metaRenders[i] == null) {
				renderers[Block.getIdFromBlock(bl)].metaRenders[i] = rnd;
			}
		}
		renderers[Block.getIdFromBlock(bl)].defaultRender = rnd;
	}
	
	public static void setShiftedRenderer(Block bl, int md, int shift, Class<? extends RenderCustomBlock> rcl) {
		RenderCustomBlock rnd = makeRenderer(bl, rcl);
		if (renderers[Block.getIdFromBlock(bl)] == null) {
			renderers[Block.getIdFromBlock(bl)] = new RenderLib.RenderShiftedEntry(shift);
		}
		renderers[Block.getIdFromBlock(bl)].metaRenders[md] = rnd;
	}
	
	private static class RenderListEntry {
		public RenderCustomBlock[] metaRenders = new RenderCustomBlock[16];
		RenderCustomBlock defaultRender;
		
		public int mapDamageValue(int dmg) {
			return dmg;
		}
	}
	
	private static class RenderShiftedEntry extends RenderLib.RenderListEntry {
		
		public int shift;
		
		public RenderShiftedEntry(int sh) {
			this.shift = sh;
		}
		
		public int mapDamageValue(int dmg) {
			return dmg >> this.shift;
		}
	}
}
