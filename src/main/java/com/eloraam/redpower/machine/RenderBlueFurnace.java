package com.eloraam.redpower.machine;

import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderBlueFurnace extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderBlueFurnace(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileAppliance tb = (TileAppliance) CoreLib.getTileEntity(iba, x, y, z, TileAppliance.class);
		if (tb != null) {
			renderblocks.renderStandardBlock(block, x, y, z);
			renderblocks.setRenderBoundsFromBlock(block);
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
		IIcon bottomIcon = getIcon(ForgeDirection.DOWN.ordinal(), md);
		IIcon topIcon = getIcon(ForgeDirection.UP.ordinal(), md);
		IIcon frontIcon = getIcon(ForgeDirection.NORTH.ordinal(), md);
		IIcon sideIcon = getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
		this.context.setIcon(bottomIcon, topIcon, sideIcon, sideIcon, sideIcon, frontIcon);
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		this.context.useNormal = false;
	}
}
