package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.FractalLib;
import com.eloraam.redpower.core.Vector3;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRubberTree extends WorldGenerator {
	
	public void putLeaves(World world, int i, int j, int k) {
		if (world.isAirBlock(i, j, k)) {
			world.setBlock(i, j, k, RedPowerWorld.blockLeaves, 0, 3);
		}
	}
	
	public boolean fillBlock(World world, int i, int j, int k) {
		if (j >= 0 && j <= 126) {
			Block bl = world.getBlock(i, j, k);
			if (bl != null && bl.isWood(world, i, j, k)) {
				return true;
			} else if (bl != null && !bl.isLeaves(world, i, j, k) && bl != Blocks.tallgrass
					&& bl != Blocks.grass && bl != Blocks.vine) {
				return false;
			} else {
				world.setBlock(i, j, k, RedPowerWorld.blockLogs, 0, 3);
				this.putLeaves(world, i, j - 1, k);
				this.putLeaves(world, i, j + 1, k);
				this.putLeaves(world, i, j, k - 1);
				this.putLeaves(world, i, j, k + 1);
				this.putLeaves(world, i - 1, j, k);
				this.putLeaves(world, i + 1, j, k);
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		int trh = random.nextInt(6) + 25;
		if (j >= 1 && j + trh + 2 <= world.getHeight()) {
			Block bid;
			int x;
			int z;
			for (x = -1; x <= 1; ++x) {
				for (z = -1; z <= 1; ++z) {
					bid = world.getBlock(i + x, j - 1, k + z);
					if (bid != Blocks.grass && bid != Blocks.dirt) {
						return false;
					}
				}
			}
			
			byte rw = 1;
			
			int org;
			for (org = j; org < j + trh; ++org) {
				if (org > j + 3) {
					rw = 5;
				}
				
				for (x = i - rw; x <= i + rw; ++x) {
					for (z = k - rw; z <= k + rw; ++z) {
						Block dest = world.getBlock(x, org, z);
						if (dest != null && !dest.isLeaves(world, x, org, z)
								&& !dest.isWood(world, x, org, z)
								&& dest != Blocks.tallgrass
								&& dest != Blocks.grass
								&& dest != Blocks.vine) {
							return false;
						}
					}
				}
			}
			
			for (x = -1; x <= 1; ++x) {
				for (z = -1; z <= 1; ++z) {
					world.setBlock(i + x, j - 1, k + z, Blocks.dirt);
				}
			}
			
			for (org = 0; org <= 6; ++org) {
				for (x = -1; x <= 1; ++x) {
					for (z = -1; z <= 1; ++z) {
						world.setBlock(i + x, j + org, k
								+ z, RedPowerWorld.blockLogs, 1, 3);
					}
				}
				
				for (x = -1; x <= 1; ++x) {
					if (random.nextInt(5) == 1
							&& world.isAirBlock(i + x, j + org, k - 2)) {
						world.setBlock(i + x, j + org,
								k - 2, Blocks.vine, 1, 3);
					}
					
					if (random.nextInt(5) == 1
							&& world.isAirBlock(i + x, j + org, k + 2)) {
						world.setBlock(i + x, j + org,
								k + 2, Blocks.vine, 4, 3);
					}
				}
				
				for (z = -1; z <= 1; ++z) {
					if (random.nextInt(5) == 1
							&& world.isAirBlock(i - 2, j + org, k + z)) {
						world.setBlock(i - 2, j + org, k
								+ z, Blocks.vine, 8, 3);
					}
					
					if (random.nextInt(5) == 1
							&& world.isAirBlock(i + 2, j + org, k + z)) {
						world.setBlock(i + 2, j + org, k
								+ z, Blocks.vine, 2, 3);
					}
				}
			}
			
			Vector3 var23 = new Vector3();
			Vector3 var24 = new Vector3();
			int nbr = random.nextInt(100) + 10;
			int br = 0;
			
			while (br < nbr) {
				var24.set(random.nextFloat() - 0.5D, random.nextFloat(), random.nextFloat() - 0.5D);
				var24.normalize();
				double m = (nbr / 10.0D + 4.0D) * (1.0F + 1.0F * random.nextFloat());
				var24.x *= m;
				var24.z *= m;
				var24.y = var24.y * (trh - 15) + nbr / 10.0D;
				if (nbr < 8) {
					switch (nbr) {
						case 0:
							var23.set(i - 1, j + 6, k - 1);
							break;
						case 1:
							var23.set(i - 1, j + 6, k);
							break;
						case 2:
							var23.set(i - 1, j + 6, k + 1);
							break;
						case 3:
							var23.set(i, j + 6, k + 1);
							break;
						case 4:
							var23.set(i + 1, j + 6, k + 1);
							break;
						case 5:
							var23.set(i + 1, j + 6, k);
							break;
						case 6:
							var23.set(i + 1, j + 6, k - 1);
							break;
						default:
							var23.set(i, j + 6, k - 1);
					}
				} else {
					var23.set(i + random.nextInt(3) - 1, j + 6,
							k + random.nextInt(3) - 1);
				}
				
				long brseed = random.nextLong();
				FractalLib.BlockSnake bsn = new FractalLib.BlockSnake(var23, var24, brseed);
				
				while (true) {
					System.out.println("KOKO LOOP FREEZE");
					if (bsn.iterate()) {
						Vector3 v = bsn.get();
						if (this.fillBlock(world, (int) Math.floor(v.x), (int) Math.floor(v.y), (int) Math.floor(v.z))) {
							continue;
						}
					}
					++br;
					break;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
}
