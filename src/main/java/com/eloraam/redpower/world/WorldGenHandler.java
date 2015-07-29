package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.world.WorldGenCustomOre;
import com.eloraam.redpower.world.WorldGenMarble;
import com.eloraam.redpower.world.WorldGenRubberTree;
import com.eloraam.redpower.world.WorldGenVolcano;

import cpw.mods.fml.common.IWorldGenerator;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenFlowers;

public class WorldGenHandler implements IWorldGenerator {
	
	@Override
	public void generate(Random rin, int chunkX, int chunkZ, World world,
			IChunkProvider generator, IChunkProvider provider) {
		if (!(generator instanceof ChunkProviderHell)
				&& !(generator instanceof ChunkProviderEnd)) {
			Random rand = new Random(Integer.valueOf(chunkX).hashCode() * 31
					+ Integer.valueOf(chunkZ).hashCode());
			
			int a;
			int vc;
			int bgb;
			int n;
			for (a = 0; a < 2; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = rand.nextInt(48);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenCustomOre(RedPowerWorld.blockOres, 0, 7))
						.generate(world, rand, vc, bgb, n);
			}
			
			for (a = 0; a < 2; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = rand.nextInt(48);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenCustomOre(RedPowerWorld.blockOres, 1, 7))
						.generate(world, rand, vc, bgb, n);
			}
			
			for (a = 0; a < 2; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = rand.nextInt(48);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenCustomOre(RedPowerWorld.blockOres, 2, 7))
						.generate(world, rand, vc, bgb, n);
			}
			
			if (Config.getInt("settings.world.generate.silver") > 0) {
				for (a = 0; a < 4; ++a) {
					vc = chunkX * 16 + rand.nextInt(16);
					bgb = rand.nextInt(32);
					n = chunkZ * 16 + rand.nextInt(16);
					(new WorldGenCustomOre(RedPowerWorld.blockOres, 3, 8))
							.generate(world, rand, vc, bgb, n);
				}
			}
			
			if (Config.getInt("settings.world.generate.tin") > 0) {
				for (a = 0; a < 10; ++a) {
					vc = chunkX * 16 + rand.nextInt(16);
					bgb = rand.nextInt(48);
					n = chunkZ * 16 + rand.nextInt(16);
					(new WorldGenCustomOre(RedPowerWorld.blockOres, 4, 8))
							.generate(world, rand, vc, bgb, n);
				}
			}
			
			if (Config.getInt("settings.world.generate.copper") > 0) {
				for (a = 0; a < 20; ++a) {
					vc = chunkX * 16 + rand.nextInt(16);
					bgb = rand.nextInt(64);
					n = chunkZ * 16 + rand.nextInt(16);
					(new WorldGenCustomOre(RedPowerWorld.blockOres, 5, 8))
							.generate(world, rand, vc, bgb, n);
				}
			}
			
			for (a = 0; a < 1; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = rand.nextInt(16);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenCustomOre(RedPowerWorld.blockOres, 6, 4))
						.generate(world, rand, vc, bgb, n);
			}
			
			for (a = 0; a < 4; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = rand.nextInt(16);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenCustomOre(RedPowerWorld.blockOres, 7, 10))
						.generate(world, rand, vc, bgb, n);
			}
			
			for (a = 0; a < 4; ++a) {
				vc = chunkX * 16 + rand.nextInt(16);
				bgb = 32 + rand.nextInt(32);
				n = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenMarble(RedPowerWorld.blockStone, 0,
						rand.nextInt(4096))).generate(world, rand, vc, bgb, n);
			}
			
			vc = Math.max(1, rand.nextInt(10) - 6);
			vc *= vc;
			
			int x;
			for (a = 0; a < vc; ++a) {
				bgb = chunkX * 16 + rand.nextInt(16);
				n = rand.nextInt(32);
				x = chunkZ * 16 + rand.nextInt(16);
				(new WorldGenVolcano(RedPowerWorld.blockStone, 1,
						rand.nextInt(65536))).generate(world, rand, bgb, n, x);
			}
			
			BiomeGenBase var15 = world.getWorldChunkManager().getBiomeGenAt(
					chunkX * 16 + 16, chunkZ * 16 + 16);
			byte var16 = 0;
			if (var15 == BiomeGenBase.jungle) {
				var16 = 1;
			} else if (var15 == BiomeGenBase.jungleHills) {
				var16 = 1;
			} else if (var15 == BiomeGenBase.forest) {
				var16 = 1;
			} else if (var15 == BiomeGenBase.plains) {
				var16 = 4;
			}
			
			int z;
			int y;
			for (a = 0; a < var16; ++a) {
				x = chunkX * 16 + rand.nextInt(16) + 8;
				z = rand.nextInt(128);
				y = chunkZ * 16 + rand.nextInt(16) + 8;
				(new WorldGenFlowers(RedPowerWorld.blockPlants)).generate(
						world, rand, x, z, y);
			}
			
			if (var15 == BiomeGenBase.jungle
					|| var15 == BiomeGenBase.jungleHills) {
				for (a = 0; a < 6; ++a) {
					x = chunkX * 16 + rand.nextInt(16) + 8;
					z = chunkZ * 16 + rand.nextInt(16) + 8;
					y = world.getHeightValue(x, z);
					(new WorldGenRubberTree()).generate(world, world.rand, x,
							y, z);
				}
			}
			
		}
	}
}
