package com.eloraam.redpower.world;

import com.eloraam.redpower.world.WorldGenCustomOre;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenMarble extends WorldGenCustomOre {
	
	LinkedList<List<Integer>> fillStack = new LinkedList<List<Integer>>();
	HashSet<List<Integer>> fillStackTest = new HashSet<List<Integer>>();
	
	public WorldGenMarble(Block block, int meta, int num) {
		super(block, meta, num);
	}
	
	private void addBlock(int i, int j, int k, int p) {
		List<Integer> sb = Arrays.asList(new Integer[] { Integer.valueOf(i), Integer
				.valueOf(j), Integer.valueOf(k) });
		if (!this.fillStackTest.contains(sb)) {
			this.fillStack
					.addLast(Arrays.asList(new Integer[] { Integer.valueOf(i), Integer
							.valueOf(j), Integer.valueOf(k), Integer.valueOf(p) }));
			this.fillStackTest.add(sb);
		}
	}
	
	private void searchBlock(World world, int i, int j, int k, int p) {
		if (world.isAirBlock(i - 1, j, k)
				|| world.isAirBlock(i + 1, j, k)
				|| world.isAirBlock(i, j - 1, k)
				|| world.isAirBlock(i, j + 1, k)
				|| world.isAirBlock(i, j, k - 1)
				|| world.isAirBlock(i, j, k + 1)) {
			p = 6;
		}
		
		this.addBlock(i - 1, j, k, p);
		this.addBlock(i + 1, j, k, p);
		this.addBlock(i, j - 1, k, p);
		this.addBlock(i, j + 1, k, p);
		this.addBlock(i, j, k - 1, p);
		this.addBlock(i, j, k + 1, p);
	}
	
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		if (!world.isAirBlock(i, j, k)) {
			return false;
		} else {
			int l;
			for (l = j; world.getBlock(i, l, k) != Blocks.stone; ++l) {
				if (l > 96) {
					return false;
				}
			}
			
			this.addBlock(i, l, k, 6);
			
			while (this.fillStack.size() > 0 && super.numberOfBlocks > 0) {
				System.out.println("KOKO LOOP FREEZE MARBLE");
				List<Integer> sp1 = this.fillStack.removeFirst();
				Integer[] sp = ((Integer[]) sp1.toArray());
				if (world.getBlock(sp[0].intValue(), sp[1].intValue(), sp[2].intValue()) == Blocks.stone) {
					world.setBlock(sp[0].intValue(),
							sp[1].intValue(), sp[2].intValue(),
							super.minableBlock, super.minableBlockMeta, 3);
					if (sp[3].intValue() > 0) {
						this.searchBlock(world, sp[0].intValue(),
								sp[1].intValue(), sp[2].intValue(),
								sp[3].intValue() - 1);
					}
					--super.numberOfBlocks;
				}
			}
			return true;
		}
	}
}
