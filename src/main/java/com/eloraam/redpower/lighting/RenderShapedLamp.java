package com.eloraam.redpower.lighting;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.lighting.TileShapedLamp;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class RenderShapedLamp extends RenderCustomBlock {
	
	int[] lightColors = new int[] { 16777215, 12608256, 11868853, 7308529, 12566272, 7074048, 15812213, 5460819, 9671571, '\u8787', 6160576, 1250240, 5187328, 558848, 10620678, 2039583 };
	int[] lightColorsOff = new int[16];
	RenderContext context = new RenderContext();
	protected RenderModel modelLamp1 = RenderModel.loadModel("/assets/rplighting/models/shlamp1.obj");
	protected RenderModel modelLamp2 = RenderModel.loadModel("/assets/rplighting/models/shlamp2.obj");
	protected ResourceLocation lampRes = new ResourceLocation("rplighting", "models/shlamp.png");
	
	public RenderShapedLamp(Block bl) {
		super(bl);
		for (int i = 0; i < 16; ++i) {
			int r = this.lightColors[i] & 255;
			int g = this.lightColors[i] >> 8 & 255;
			int b = this.lightColors[i] >> 16 & 255;
			int v = (r + g + b) / 3;
			r = (r + 2 * v) / 5;
			g = (g + 2 * v) / 5;
			b = (b + 2 * v) / 5;
			this.lightColorsOff[i] = r | g << 8 | b << 16;
		}
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileShapedLamp tsl = (TileShapedLamp) CoreLib.getTileEntity(iba, i, j, k, TileShapedLamp.class);
		if (tsl != null) {
			//Tessellator tess = Tessellator.instance;
			//tess.draw();
			
			boolean lit = tsl.Powered != tsl.Inverted;
			this.context.setDefaults();
			this.context.setPos(i, j, k);
			this.context.setOrientation(tsl.Rotation, 0);
			this.context.readGlobalLights(iba, i, j, k);
			switch (tsl.Style) {
				case 0:
					this.context.bindModelOffset(this.modelLamp1, 0.5D, 0.5D, 0.5D);
					break;
				case 1:
					this.context.bindModelOffset(this.modelLamp2, 0.5D, 0.5D, 0.5D);
			}
			
			int tc;
			
			if (MinecraftForgeClient.getRenderPass() != 1) {
				System.out.println("UNLIT");
				tc = super.block.getMixedBrightnessForBlock(iba, i, j, k);
				//this.context.bindTexture("/eloraam/lighting/lighting1.png");
				Minecraft.getMinecraft().renderEngine.bindTexture(lampRes);
				
				//tess.startDrawingQuads();
				this.context.setBrightness(tc);
				this.context.renderModelGroup(0, 0);
				if (lit) {
					this.context.setTintHex(this.lightColors[tsl.Color & 15]);
					this.context.setBrightness(15728880);
				} else {
					this.context.setTintHex(this.lightColorsOff[tsl.Color & 15]);
				}
				this.context.renderModelGroup(1, 0);
				//tess.draw();
				//this.context.unbindTexture();
			} else if (lit) {
				System.out.println("LIT");
				
				Minecraft.getMinecraft().renderEngine.bindTexture(lampRes); //TODO: Strange int param...
				
				//tess.startDrawingQuads();
				
				tc = this.lightColors[tsl.Color & 15];
				this.context.setTint((tc >> 16) / 255.0F, (tc >> 8 & 255) / 255.0F, (tc & 255) / 255.0F);
				this.context.setAlpha(0.3F);
				this.context.renderModelGroup(2, 0);
				
				//tess.draw();
			}
			
			//this.context.bindBlockTexture();
			//tess.startDrawingQuads();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		boolean lit = false;
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		//this.context.bindTexture("/eloraam/lighting/lighting1.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(lampRes);
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		switch (md >> 5) {
			case 0:
				this.context.bindModelOffset(this.modelLamp1, 0.5D, 0.5D, 0.5D);
				break;
			case 1:
				this.context.bindModelOffset(this.modelLamp2, 0.5D, 0.5D, 0.5D);
		}
		
		this.context.renderModelGroup(0, 0);
		if ((md & 16) > 0) {
			this.context.setTintHex(this.lightColors[md & 15]);
		} else {
			this.context.setTintHex(this.lightColorsOff[md & 15]);
		}
		
		this.context.renderModelGroup(1, 0);
		this.context.useNormal = false;
		tessellator.draw();
		//this.context.unbindTexture();
	}
}
