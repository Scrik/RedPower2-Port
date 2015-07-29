package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.world.WorldGenCustomOre;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockVine;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenVolcano extends WorldGenCustomOre {
	
	LinkedList<List<Integer>> fillStack = new LinkedList<List<Integer>>();
	HashMap<List<Integer>, Integer> fillStackTest = new HashMap<List<Integer>, Integer>();
	
	public WorldGenVolcano(Block block, int meta, int num) {
		super(block, meta, num);
	}
	
	private void addBlock(int i, int j, int k, int p) {
		if (p > 0) {
			List<Integer> sb = Arrays.asList(new Integer[] { Integer.valueOf(i), Integer.valueOf(k) });
			Integer o = (Integer) this.fillStackTest.get(sb);
			if (o == null || p > o.intValue()) {
				this.fillStack.addLast(Arrays.asList(new Integer[] { Integer .valueOf(i), Integer.valueOf(j), Integer.valueOf(k) }));
				this.fillStackTest.put(sb, Integer.valueOf(p));
			}
		}
	}
	
	private void searchBlock(int i, int j, int k, int p, Random random) {
		int rp = random.nextInt(16);
		this.addBlock(i - 1, j, k, (rp & 1) > 0 ? p - 1 : p);
		this.addBlock(i + 1, j, k, (rp & 2) > 0 ? p - 1 : p);
		this.addBlock(i, j, k - 1, (rp & 4) > 0 ? p - 1 : p);
		this.addBlock(i, j, k + 1, (rp & 8) > 0 ? p - 1 : p);
	}
	
	public boolean canReplace(Block block) {
		return block == Blocks.air ? true : (block != Blocks.flowing_water
				&& block != Blocks.water && block != Blocks.log && block != Blocks.log2
				&& (block instanceof BlockLeavesBase) && (block instanceof BlockVine)
				&& block != Blocks.snow && block != Blocks.ice && block != Blocks.packed_ice ? (block != RedPowerWorld.blockLogs
				&& block != RedPowerWorld.blockLeaves ? block instanceof BlockFlower : true) : true);
	}
	
	public void eatTree(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);
		if (block == Blocks.snow) {
			world.setBlockToAir(i, j, k);
		} else if (block instanceof BlockLog || block instanceof BlockLeavesBase || block instanceof BlockVine) {
			world.setBlockToAir(i, j, k);
			this.eatTree(world, i, j + 1, k);
		}
	}
	
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		if (world.getBlock(i, j, k) != Blocks.lava) {
			return false;
		} else {
			int swh = world.getHeightValue(i, k);
			
			for (;this.canReplace(world.getBlock(i, swh - 1, k)); --swh) {
				;
			}
			int n;
			for (n = j; n < swh; ++n) {
				world.setBlock(i, n, k, Blocks.flowing_lava);
				world.setBlock(i - 1, n, k, super.minableBlock, super.minableBlockMeta, 3);
				world.setBlock(i + 1, n, k, super.minableBlock, super.minableBlockMeta, 3);
				world.setBlock(i, n, k - 1, super.minableBlock, super.minableBlockMeta, 3);
				world.setBlock(i, n, k + 1, super.minableBlock, super.minableBlockMeta, 3);
			}
			
			int head = 3 + random.nextInt(4);
			int spread = random.nextInt(3);
			
			label69: while (super.numberOfBlocks > 0) {
				while (this.fillStack.size() == 0) {
					System.out.println("KOKO LOOP FREEZE VOLCANO");
					world.setBlock(i, n, k, Blocks.lava);
					this.fillStackTest.clear();
					this.searchBlock(i, n, k, head, random);
					++n;
					if (n > 125) {
						break label69;
					}
				}
				
				List<Integer> sp1 = this.fillStack.removeFirst();
				Integer[] sp = ((Integer[]) sp1.toArray());
				world.getBlock(sp[0].intValue(), 64, sp[2].intValue());
				if (world.blockExists(sp[0].intValue(), 64, sp[2].intValue())) {
					int pow = ((Integer) this.fillStackTest.get(Arrays
							.asList(new Integer[] { sp[0], sp[2] })))
							.intValue();
					
					int hm;
					for (hm = world.getHeightValue(sp[0].intValue(),
							sp[2].intValue()) + 1; hm > 0
							&& this.canReplace(world.getBlock(
									sp[0].intValue(), hm - 1, sp[2].intValue())); --hm) {
						;
					}
					
					if (hm <= sp[1].intValue()) {
						Block block = world.getBlock(sp[0].intValue(), hm,
								sp[2].intValue());
						if (this.canReplace(block)) {
							this.eatTree(world, sp[0].intValue(), hm,
									sp[2].intValue());
							world.setBlock(sp[0].intValue(), hm, sp[2].intValue(), super.minableBlock, super.minableBlockMeta, 3);
							if (sp[1].intValue() > hm) {
								pow = Math.max(pow, spread);
							}
							
							this.searchBlock(sp[0].intValue(), hm, sp[2].intValue(), pow, random);
							--super.numberOfBlocks;
						}
					}
				}
			}
			
			world.setBlock(i, n, k, Blocks.lava);
			
			while (n > swh && world.getBlock(i, n, k) == Blocks.lava) {
				world.markBlockForUpdate(i, n, k);
				world.notifyBlocksOfNeighborChange(i, n, k, Blocks.lava);
				world.scheduledUpdatesAreImmediate = true;
				Blocks.lava.updateTick(world, i, n, k, random);
				world.scheduledUpdatesAreImmediate = false;
				--n;
			}
			
			return true;
		}
	}
}
