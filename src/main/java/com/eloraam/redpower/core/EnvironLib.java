package com.eloraam.redpower.core;

import com.eloraam.redpower.core.FractalLib;
import com.eloraam.redpower.core.WorldCoord;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class EnvironLib {
	
	public static double getWindSpeed(World world, WorldCoord wc) {
		if (world.provider.isHellWorld) {
			return 0.5D;
		} else {
			double nv = FractalLib.noise1D(2576710L,
					world.getWorldTime() * 1.0E-4D, 0.6F, 5);
			nv = Math.max(0.0D, 1.6D * (nv - 0.5D) + 0.5D);
			if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
				nv *= Math.sqrt(wc.y) / 16.0D;
			}
			
			BiomeGenBase bgb = world.getBiomeGenForCoords(wc.x, wc.z);
			if (bgb.canSpawnLightningBolt()) {
				if (world.isThundering()) {
					return 4.0D * nv;
				}
				
				if (world.isRaining()) {
					return 0.5D + 0.5D * nv;
				}
			}
			return nv;
		}
	}
}
