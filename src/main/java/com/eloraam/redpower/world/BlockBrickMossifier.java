package com.eloraam.redpower.world;

import com.eloraam.redpower.core.WorldCoord;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockBrickMossifier extends BlockStoneBrick {
	
	public BlockBrickMossifier() {
		this.setTickRandomly(true);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setStepSound(soundTypeStone);
		this.setBlockName("stonebricksmooth");
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		switch (world.getBlockMetadata(i, j, k)) {
			case 0:
				this.crackBrick(world, i, j, k, random);
				break;
			case 1:
				this.spreadMoss(world, i, j, k, random);
		}
		
	}
	
	private void crackBrick(World world, int i, int j, int k, Random random) {
		WorldCoord wc1 = new WorldCoord(i, j, k);
		boolean lava = false;
		boolean water = false;
		
		for (int n = 0; n < 6; ++n) {
			WorldCoord wc2 = wc1.coordStep(n);
			Block block = world.getBlock(wc2.x, wc2.y, wc2.z);
			if (block != Blocks.water && block != Blocks.flowing_water) {
				if (block == Blocks.lava || block == Blocks.flowing_lava) {
					lava = true;
				}
			} else {
				water = true;
			}
		}
		
		if (lava && water) {
			if (random.nextInt(2) == 0) {
				world.setBlock(i, j, k, this, 2, 3);
			}
		}
	}
	
	private void spreadMoss(World world, int i, int j, int k, Random random) {
		if (world.isAirBlock(i, j + 1, k)) {
			if (!world.canBlockSeeTheSky(i, j + 1, k)) {
				WorldCoord wc1 = new WorldCoord(i, j, k);
				
				for (int n = 0; n < 4; ++n) {
					WorldCoord wc2 = wc1.coordStep(2 + n);
					Block block = world.getBlock(wc2.x, wc2.y, wc2.z);
					Block rpb = block;
					byte rpmd = 0;
					if (block == Blocks.cobblestone) {
						rpb = Blocks.mossy_cobblestone;
					} else {
						if (block != Blocks.stonebrick || world.getBlockMetadata(wc2.x, wc2.y, wc2.z) != 0) {
							continue;
						}
						
						rpmd = 1;
					}
					
					if (world.isAirBlock(wc2.x, wc2.y + 1, wc2.z)) {
						if (world.canBlockSeeTheSky(wc2.x, wc2.y + 1, wc2.z)) {
							return;
						}
						
						boolean wet = false;
						
						for (int m = 0; m < 4; ++m) {
							WorldCoord wc3 = wc2.coordStep(2 + m);
							Block bd2 = world.getBlock(wc3.x, wc3.y, wc3.z);
							if (bd2 == Blocks.water || bd2 == Blocks.flowing_water) {
								wet = true;
								break;
							}
						}
						
						if (wet && random.nextInt(2) == 0) {
							world.setBlock(wc2.x, wc2.y,wc2.z, rpb, rpmd, 3);
						}
					}
				}
			}
		}
	}
}
