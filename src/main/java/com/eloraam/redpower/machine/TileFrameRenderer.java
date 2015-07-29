package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileMotor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class TileFrameRenderer extends TileEntitySpecialRenderer {
	
	private RenderBlocks rblocks;
	RenderContext context = new RenderContext();
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		if (!te.isInvalid()) {
			TileFrameMoving tfm = (TileFrameMoving) te;
			Block block = tfm.movingBlockID;
			if (block != null) {
				Tessellator tessellator = Tessellator.instance;
				//this.bindTextureByName("/terrain.png");
				//Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/atlas/blocks.png"));
				int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
				tessellator.setBrightness(lv);
				RenderHelper.disableStandardItemLighting();
				GL11.glBlendFunc(770, 771);
				GL11.glEnable(3042);
				GL11.glEnable(2884);
				if (Minecraft.isAmbientOcclusionEnabled()) {
					GL11.glShadeModel(7425);
				} else {
					GL11.glShadeModel(7424);
				}
				
				IBlockAccess wba = this.rblocks.blockAccess;
				this.rblocks.blockAccess = tfm.getFrameBlockAccess();
				//ForgeHooksClient.beforeBlockRender(block, this.rblocks);
				TileMotor tm = (TileMotor) CoreLib.getTileEntity(tfm.getWorldObj(), tfm.motorX, tfm.motorY, tfm.motorZ, TileMotor.class);
				GL11.glPushMatrix();
				if (tm != null) {
					WorldCoord wc = new WorldCoord(0, 0, 0);
					wc.step(tm.MoveDir);
					float ms = tm.getMoveScaled();
					GL11.glTranslatef(wc.x * ms, wc.y * ms, wc.z * ms);
				}
				
				tessellator.startDrawingQuads();
				tessellator.setTranslation(x - tfm.xCoord, y - tfm.yCoord, z - tfm.zCoord);
				tessellator.setColorOpaque(1, 1, 1);
				if (tfm.movingCrate) {
					this.context.setDefaults();
					this.context.setBrightness(lv);
					this.context.setPos(tfm.xCoord, tfm.yCoord, tfm.zCoord);
					//this.context.setTexFile("/eloraam/machine/machine1.png");
					this.context.setIcon(te.blockType.getIcon(5, te.blockMetadata));
					this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
				} else {
					tfm.doRefresh(tfm.getFrameBlockAccess());
					this.rblocks.renderAllFaces = true;
					this.rblocks.renderBlockByRenderType(block, tfm.xCoord, tfm.yCoord, tfm.zCoord);
					this.rblocks.renderAllFaces = false;
				}
				
				tessellator.setTranslation(0.0D, 0.0D, 0.0D);
				tessellator.draw();
				GL11.glPopMatrix();
				//ForgeHooksClient.afterBlockRender(block, this.rblocks);
				this.rblocks.blockAccess = wba;
				RenderHelper.enableStandardItemLighting();
			}
		}
	}
	
	@Override
	public void func_147496_a(World world) {
		this.rblocks = new RenderBlocks(world);
	}
}
