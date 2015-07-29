package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CoverRenderer;
import com.eloraam.redpower.core.RenderContext;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import org.lwjgl.opengl.GL11;

public class RenderHighlight {
	
	RenderContext context = new RenderContext();
	CoverRenderer coverRenderer;
	
	public RenderHighlight() {
		this.coverRenderer = new CoverRenderer(this.context);
	}
	
	@SubscribeEvent
	public void highlightEvent(DrawBlockHighlightEvent ev) {
		this.onBlockHighlight(ev.context, ev.player, ev.target, ev.subID,
				ev.currentItem, ev.partialTicks);
	}
	
	@SuppressWarnings("rawtypes") //TODO: Костыль...
	public boolean onBlockHighlight(RenderGlobal renderglobal, EntityPlayer player, MovingObjectPosition mop, int i, ItemStack ist, float f) {
		World world = player.worldObj;
		Block bid = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		Map dmgBlocks = (HashMap)ReflectionHelper.getPrivateValue(RenderGlobal.class, renderglobal, new String[]{"field_72738_E", "damagedBlocks"});
		if (!dmgBlocks.isEmpty()) {
			Iterator plp = dmgBlocks.values().iterator();
			
			while (plp.hasNext()) {
				Object o = plp.next();
				DestroyBlockProgress drb = (DestroyBlockProgress) o;
				if (drb.getPartialBlockX() == mop.blockX
						&& drb.getPartialBlockY() == mop.blockY
						&& drb.getPartialBlockZ() == mop.blockZ) {
					if (bid instanceof BlockExtended) {
						this.drawBreaking(player.worldObj, renderglobal,
								(BlockExtended) bid, player,
								mop, f, drb.getPartialBlockDamage());
						renderglobal.drawSelectionBox(player, mop, i/*, ist*/, f);
						return true;
					}
					break;
				}
			}
		}
		
		if (ist != null && CoverLib.blockCoverPlate != null && Block.getBlockFromItem(ist.getItem()) == CoverLib.blockCoverPlate) {
			if (mop.typeOfHit != MovingObjectType.BLOCK) {
				return false;
			} else {
				MovingObjectPosition plp1;
				switch (ist.getItemDamage() >> 8) {
					case 45: //TODO: Может напортачил...
						this.drawSideBox(world, player, mop, f);
						plp1 = CoverLib.getPlacement(world, mop,
								ist.getItemDamage());
						if (plp1 != null) {
							this.drawPreview(player, plp1, f,
									ist.getItemDamage());
						}
						break;
					default:
						return false;
					case 38:
						this.drawCornerBox(world, player, mop, f);
						plp1 = CoverLib.getPlacement(world, mop, ist.getItemDamage());
						if (plp1 != null) {
							this.drawPreview(player, plp1, f, ist.getItemDamage());
						}
				}
				return true;
			}
		} else {
			return false;
		}
	}
	
	private void setRawPos(EntityPlayer player, MovingObjectPosition mop, float f) {
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * f;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * f;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * f;
		this.context.setPos(mop.blockX - x, mop.blockY - y, mop.blockZ - z);
	}
	
	private void setCollPos(EntityPlayer player, MovingObjectPosition mop, float f) {
		this.setRawPos(player, mop, f);
		switch (mop.sideHit) {
			case 0:
				this.context.setRelPos(0.0D, mop.hitVec.yCoord - mop.blockY, 0.0D);
				break;
			case 1:
				this.context.setRelPos(0.0D, mop.blockY - mop.hitVec.yCoord + 1.0D, 0.0D);
				break;
			case 2:
				this.context.setRelPos(0.0D, mop.hitVec.zCoord - mop.blockZ, 0.0D);
				break;
			case 3:
				this.context.setRelPos(0.0D, mop.blockZ - mop.hitVec.zCoord + 1.0D, 0.0D);
				break;
			case 4:
				this.context.setRelPos(0.0D, mop.hitVec.xCoord - mop.blockX, 0.0D);
				break;
			default:
				this.context.setRelPos(0.0D, mop.blockX - mop.hitVec.xCoord + 1.0D, 0.0D);
		}
		
	}
	
	public void drawCornerBox(World world, EntityPlayer player, MovingObjectPosition mop, float f) {
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
		GL11.glLineWidth(3.0F);
		GL11.glDisable(3553);
		GL11.glDepthMask(false);
		float sx = 0.002F;
		float sbs = 0.25F;
		Block bid = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		if (bid != Blocks.air) {
			this.context.setSize(0.0D, (-sx), 0.0D, 1.0D, (-sx), 1.0D);
			this.context.setupBox();
			this.context.vertexList[4].set(0.0D, (-sx), 0.5D);
			this.context.vertexList[5].set(1.0D, (-sx), 0.5D);
			this.context.vertexList[6].set(0.5D, (-sx), 0.0D);
			this.context.vertexList[7].set(0.5D, (-sx), 1.0D);
			this.context.setOrientation(mop.sideHit, 0);
			this.setCollPos(player, mop, f);
			this.context.transformRotate();
			Tessellator.instance.startDrawing(3);
			this.context.drawPoints(new int[] { 0, 1, 2, 3, 0 });
			Tessellator.instance.draw();
			Tessellator.instance.startDrawing(1);
			this.context.drawPoints(new int[] { 4, 5, 6, 7 });
			Tessellator.instance.draw();
		}
		
		GL11.glDepthMask(true);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		this.context.setRelPos(0.0D, 0.0D, 0.0D);
	}
	
	public void drawSideBox(World world, EntityPlayer player,
			MovingObjectPosition mop, float f) {
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
		GL11.glLineWidth(3.0F);
		GL11.glDisable(3553);
		GL11.glDepthMask(false);
		float sx = 0.002F;
		float sbs = 0.25F;
		Block bid = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		if (bid != Blocks.air) {
			this.context.setSize(0.0D, (-sx), 0.0D, 1.0D, (-sx), 1.0D);
			this.context.setupBox();
			this.context.vertexList[4].set(1.0F - sbs, (-sx), sbs);
			this.context.vertexList[5].set(sbs, (-sx), sbs);
			this.context.vertexList[6].set(sbs, (-sx), 1.0F - sbs);
			this.context.vertexList[7].set(1.0F - sbs, (-sx), 1.0F - sbs);
			this.context.setOrientation(mop.sideHit, 0);
			this.setCollPos(player, mop, f);
			this.context.transformRotate();
			Tessellator.instance.startDrawing(3);
			this.context.drawPoints(new int[] { 0, 1, 2, 3, 0 });
			Tessellator.instance.draw();
			Tessellator.instance.startDrawing(3);
			this.context.drawPoints(new int[] { 4, 5, 6, 7, 4 });
			Tessellator.instance.draw();
			Tessellator.instance.startDrawing(1);
			this.context.drawPoints(new int[] { 0, 4, 1, 5, 2, 6, 3, 7 });
			Tessellator.instance.draw();
		}
		
		GL11.glDepthMask(true);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		this.context.setRelPos(0.0D, 0.0D, 0.0D);
	}
	
	public void drawBreaking(World world, RenderGlobal rg, BlockExtended bex,
			EntityPlayer player, MovingObjectPosition mop, float f, int dmg) {
		if (bex instanceof BlockMultipart) {
			BlockMultipart j = (BlockMultipart) bex;
			j.setPartBounds(world, mop.blockX, mop.blockY, mop.blockZ,
					mop.subHit);
		}
		
		GL11.glEnable(3042);
		GL11.glBlendFunc(774, 768);
		//int j1 = rg.renderEngine.getTexture("/terrain.png"); //TODO: Figure this out
		//GL11.glBindTexture(3553, j1);
		//Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/atlas/blocks.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		GL11.glPolygonOffset(-3.0F, -3.0F);
		GL11.glEnable('\u8037');
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX)
				* f;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY)
				* f;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ)
				* f;
		GL11.glEnable(3008);
		this.context.setPos(mop.blockX - x, mop.blockY - y, mop.blockZ - z);
		//this.context.setTex(240 + dmg);
		Tessellator.instance.startDrawingQuads();
		this.context.setSize(bex.getBlockBoundsMinX(),
				bex.getBlockBoundsMinY(), bex.getBlockBoundsMinZ(),
				bex.getBlockBoundsMaxX(), bex.getBlockBoundsMaxY(),
				bex.getBlockBoundsMaxZ());
		this.context.setupBox();
		this.context.transform();
		this.context.renderFaces(63);
		Tessellator.instance.draw();
		GL11.glPolygonOffset(0.0F, 0.0F);
		GL11.glDisable('\u8037');
	}
	
	public void drawPreview(EntityPlayer player, MovingObjectPosition mop, float f, int item) {
		this.setRawPos(player, mop, f);
		this.coverRenderer.start();
		this.coverRenderer.setupCorners();
		this.coverRenderer.setSize(mop.subHit, CoverLib.getThickness(mop.subHit, CoverLib.damageToCoverValue(item)));
		//this.context.setTexFile(CoverLib.coverTextureFiles[item & 255]); //TODO: Это тоже проверить...
		this.context.setIcon(CoverLib.coverIcons[item & 255]);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 1);
		GL11.glDepthMask(false);
		GL11.glPolygonOffset(-3.0F, -3.0F);
		GL11.glEnable('\u8037');
		Tessellator.instance.startDrawingQuads();
		this.context.setupBox();
		this.context.transform();
		this.context.doMappingBox(63);
		this.context.doLightLocal(63);
		this.context.renderAlpha(63, 0.8F);
		Tessellator.instance.draw();
		GL11.glDisable('\u8037');
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
	}
}
