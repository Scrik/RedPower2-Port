package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.TileAdvBench;
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

public class RenderAdvBench extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderAdvBench(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileAdvBench tb = (TileAdvBench) CoreLib.getTileEntity(iba, x, y, z, TileAdvBench.class);
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
		IIcon frontIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.NORTH.ordinal(), 3);
		IIcon topIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.UP.ordinal(), 3);
		IIcon bottomIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.DOWN.ordinal(), 3);
		IIcon sideIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.UNKNOWN.ordinal(), 3);
		this.context.setIcon(bottomIcon, topIcon, sideIcon, sideIcon, sideIcon, frontIcon);
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		this.context.useNormal = false;
	}
}
