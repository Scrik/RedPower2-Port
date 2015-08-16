package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileCPU;
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

public class RenderCPU extends RenderCustomBlock {
	
	RenderContext context = new RenderContext();
	
	public RenderCPU(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileCPU cpu = (TileCPU) CoreLib.getTileEntity(iba, x, y, z, TileCPU.class);
		if (cpu != null) {
			this.context.setTexRotation(renderblocks, CoreLib.getFacing(cpu.Rotation), false);
			renderblocks.renderStandardBlock(block, x, y, z);
			renderblocks.setRenderBoundsFromBlock(block);
			this.context.resetTexRotation(renderblocks);
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.useNormal = true;
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		IIcon topIcon = RedPowerControl.blockPeripheral.getIcon(ForgeDirection.UP.ordinal(), md);
		IIcon bottomIcon = RedPowerControl.blockPeripheral.getIcon(ForgeDirection.DOWN.ordinal(), md);
		IIcon frontIcon = RedPowerControl.blockPeripheral.getIcon(ForgeDirection.NORTH.ordinal(), md);
		IIcon backIcon = RedPowerControl.blockPeripheral.getIcon(ForgeDirection.SOUTH.ordinal(), md);
		IIcon sideIcon = RedPowerControl.blockPeripheral.getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
		this.context.setIcon(bottomIcon, topIcon, sideIcon, sideIcon, backIcon, frontIcon);
		tessellator.startDrawingQuads();
		this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		this.context.useNormal = false;
	}
}
