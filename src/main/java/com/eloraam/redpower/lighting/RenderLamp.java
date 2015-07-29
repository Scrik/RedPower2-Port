package com.eloraam.redpower.lighting;

import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.lighting.BlockLamp;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class RenderLamp extends RenderCustomBlock {
	
	int[] lightColors = new int[] { 16777215, 12608256, 11868853, 7308529, 12566272, 7074048, 15812213, 5460819, 9671571, '\u8787', 6160576, 1250240, 5187328, 558848, 10620678, 2039583 };
	RenderContext context = new RenderContext();
	
	public RenderLamp(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		boolean lit = ((BlockLamp) super.block).lit;
		this.context.setPos(i, j, k);
		this.context.setOrientation(0, 0);
		this.context.readGlobalLights(iba, i, j, k);
		if (MinecraftForgeClient.getRenderPass() != 1) {
			System.out.println("UNLIT");
			
			float tc1 = super.block.getMixedBrightnessForBlock(iba, i, j, k);
			if (lit) {
				tc1 = 1.0F;
			}
			
			this.context.startWorldRender(renderblocks);
			//this.context.bindTexture("/eloraam/lighting/lighting1.png");
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setTexFlags(0);
			this.context.setupBox();
			this.context.transform();
			if (lit) {
				System.out.println("LIT2");
				this.context.setTint(tc1, tc1, tc1);
				this.context.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
				this.context.setIcon(super.block.getIcon(0, md));
				this.context.doMappingBox(63);
				this.context.doLightLocal(63);
				this.context.renderFlat(63);
			} else {
				System.out.println("UNLIT2");
				this.context.setTint(1.0F, 1.0F, 1.0F);
				this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
				this.context.setIcon(super.block.getIcon(1, md));
				this.context.renderGlobFaces(63);
			}
			
			//this.context.unbindTexture();
			this.context.endWorldRender();
		} else if (lit) {
			System.out.println("LIT");
			//RenderLib.bindTexture("/eloraam/lighting/lighting1.png", 1);
			int tc = this.lightColors[md];
			this.context.setTint((tc >> 16) / 255.0F, (tc >> 8 & 255) / 255.0F, (tc & 255) / 255.0F);
			this.context.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
			this.context.setSize(-0.05D, -0.05D, -0.05D, 1.05D, 1.05D, 1.05D);
			this.context.setupBox();
			this.context.transform();
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.doMappingBox(63);
			this.context.doLightLocal(63);
			this.context.renderAlpha(63, 0.5F);
			//RenderLib.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		boolean lit = ((BlockLamp) super.block).lit;
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.setTint(1.0F, 1.0F, 1.0F);
		this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		if (lit) {
			this.context.setIcon(super.block.getIcon(0, md));
		} else {
			this.context.setIcon(super.block.getIcon(1, md));
		}
		
		this.context.setOrientation(0, 0);
		this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		this.context.calcBounds();
		//this.context.bindTexture("/eloraam/lighting/lighting1.png");
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		this.context.renderFaces(63);
		this.context.useNormal = false;
		tessellator.draw();
		//this.context.unbindTexture();
	}
}
