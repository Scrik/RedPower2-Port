package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.machine.TilePump;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderPump extends RenderCustomBlock {
	
	protected RenderModel modelBase = RenderModel.loadModel("/assets/rpmachine/models/pump1.obj");
	protected RenderModel modelSlide = RenderModel.loadModel("/assets/rpmachine/models/pump2.obj");
	protected ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/machine1.png");
	protected RenderContext context = new RenderContext();
	
	public RenderPump(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TilePump tb = (TilePump) CoreLib.getTileEntity(iba, x, y, z, TilePump.class);
		if (tb != null) {
			Tessellator tessellator = Tessellator.instance;
			tessellator.draw();
			
			//Лоадим наш блок
			this.context.setDefaults();
			this.context.setPos(x, y, z);
			this.context.setOrientation(0, tb.Rotation);
			this.context.readGlobalLights(iba, x, y, z);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, x, y, z));
			//this.context.bindTexture("/eloraam/machine/machine1.png");
			Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
			
			//Рендерим наш блок
			tessellator.startDrawingQuads();
			
			this.context.bindModelOffset(this.modelBase, 0.5D, 0.5D, 0.5D);
			this.context.renderModelGroup(0, 0);
			this.context.renderModelGroup(1, tb.Charged ? (tb.Active ? 3 : 2) : 1);
			
			tessellator.draw();
			
			//Восстанавливаем дефолтные блоки
			this.context.bindBlockTexture();
			tessellator.startDrawingQuads();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		//this.context.bindTexture("/eloraam/machine/machine1.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(modelRes);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		this.context.useNormal = true;
		this.context.bindModelOffset(this.modelBase, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(0, 0);
		this.context.renderModelGroup(1, 1);
		this.context.setRelPos(0.375D, 0.0D, 0.0D);
		this.context.bindModelOffset(this.modelSlide, 0.5D, 0.5D, 0.5D);
		this.context.renderModelGroup(0, 0);
		this.context.useNormal = false;
		tessellator.draw();
		//this.context.unbindTexture();
	}
}
