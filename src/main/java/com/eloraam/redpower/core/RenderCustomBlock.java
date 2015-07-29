package com.eloraam.redpower.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class RenderCustomBlock {
	
	protected Block block;
	
	public RenderCustomBlock(Block bl) {
		this.block = bl;
	}
	
	public abstract void randomDisplayTick(World worldObj, int x, int y, int z, Random random);
	
	public abstract void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int metadata);
	
	public abstract void renderInvBlock(RenderBlocks renderblocks, int metadata);
	
	public IIcon getIcon(int id, int meta) {
		return block.getIcon(id, meta);
	}
}
