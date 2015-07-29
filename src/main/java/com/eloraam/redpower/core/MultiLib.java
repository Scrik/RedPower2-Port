package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TileMultiblock;
import com.eloraam.redpower.core.WorldCoord;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;

public class MultiLib {
	
	public static boolean isClear(World world, WorldCoord parent, List<WorldCoord> coords) {
		Iterator<WorldCoord> iter = coords.iterator();
		
		TileMultiblock tmb;
		do {
			WorldCoord wc;
			do {
				if (!iter.hasNext()) {
					return true;
				}
				wc = iter.next();
			} while (RedPowerBase.blockMultiblock.canPlaceBlockAt(world, wc.x, wc.y, wc.z));
			
			tmb = (TileMultiblock) CoreLib.getTileEntity(world, wc, TileMultiblock.class);
			if (tmb == null) {
				return false;
			}
		} while (tmb.relayX == parent.x && tmb.relayY == parent.y && tmb.relayZ == parent.z);
		
		return false;
	}
	
	public static void addRelays(World world, WorldCoord parent, int md, List<WorldCoord> coords) {
		int num = 0;
		Iterator<WorldCoord> iter = coords.iterator();
		
		while (iter.hasNext()) {
			WorldCoord wc = iter.next();
			world.setBlock(wc.x, wc.y, wc.z,RedPowerBase.blockMultiblock, md, 3);
			TileMultiblock tmb = (TileMultiblock) CoreLib.getTileEntity(world, wc, TileMultiblock.class);
			if (tmb != null) {
				tmb.relayX = parent.x;
				tmb.relayY = parent.y;
				tmb.relayZ = parent.z;
				tmb.relayNum = num++;
			}
		}
	}
	
	public static void removeRelays(World world, WorldCoord parent, List<WorldCoord> coords) {
		Iterator<WorldCoord> iter = coords.iterator();
		
		while (iter.hasNext()) {
			WorldCoord wc = (WorldCoord) iter.next();
			TileMultiblock tmb = (TileMultiblock) CoreLib.getTileEntity(world, wc, TileMultiblock.class);
			if (tmb != null && tmb.relayX == parent.x && tmb.relayY == parent.y && tmb.relayZ == parent.z) {
				world.setBlockToAir(wc.x, wc.y, wc.z);
			}
		}
	}
}
