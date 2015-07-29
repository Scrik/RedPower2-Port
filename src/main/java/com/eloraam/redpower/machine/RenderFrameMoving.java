package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RenderCustomBlock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderFrameMoving extends RenderCustomBlock {
	
	public RenderFrameMoving(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World worldObj, int x, int y, int z, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int metadata) {
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int metadata) {
	}
}
