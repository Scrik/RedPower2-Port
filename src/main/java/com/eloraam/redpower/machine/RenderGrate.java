package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileGrate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderGrate extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderGrate(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileGrate tb = (TileGrate) CoreLib.getTileEntity(iba, x, y, z, TileGrate.class);
		if (tb != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(x, y, z);
			this.context.readGlobalLights(iba, x, y, z);
			IIcon iconSide = getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
			IIcon iconMossySide = getIcon(ForgeDirection.WEST.ordinal(), md);
			IIcon iconBack = getIcon(ForgeDirection.NORTH.ordinal(), md);
			IIcon iconEmptyBack = getIcon(ForgeDirection.SOUTH.ordinal(), md);
			
			this.context.setIcon(tb.Rotation == 0 ? iconBack : iconSide,
					tb.Rotation == 1 ? iconBack : iconSide, 
					tb.Rotation == 2 ? iconBack : iconMossySide,
					tb.Rotation == 3 ? iconBack : iconMossySide, 
					tb.Rotation == 4 ? iconBack : iconMossySide,
					tb.Rotation == 5 ? iconBack : iconMossySide);
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.renderGlobFaces(63);
			this.context.setIcon(tb.Rotation == 1 ? iconEmptyBack : iconSide,
					tb.Rotation == 0 ? iconEmptyBack : iconSide, 
					tb.Rotation == 3 ? iconEmptyBack : iconSide,
					tb.Rotation == 2 ? iconEmptyBack : iconSide, 
					tb.Rotation == 5 ? iconEmptyBack : iconSide,
					tb.Rotation == 4 ? iconEmptyBack : iconSide);
			this.context.setLocalLights(0.3F);
			this.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, x, y, z));
			this.context.renderBox(63, 0.99D, 0.99D, 0.99D, 0.01D, 0.01D, 0.01D);
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.useNormal = true;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		IIcon iconSide = getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
		IIcon iconMossySide = getIcon(ForgeDirection.WEST.ordinal(), md);
		IIcon iconBack = getIcon(ForgeDirection.NORTH.ordinal(), md);
		this.context.setIcon(iconSide, iconBack, iconMossySide, iconMossySide, iconMossySide, iconMossySide);
		this.context.doubleBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, 0.01D);
		tessellator.draw();
		this.context.useNormal = false;
	}
}
