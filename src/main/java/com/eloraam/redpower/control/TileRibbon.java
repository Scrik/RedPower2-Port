package com.eloraam.redpower.control;

import net.minecraft.block.Block;

import com.eloraam.redpower.wiring.TileWiring;

public class TileRibbon extends TileWiring {
	
	@Override
	public int getExtendedID() {
		return 12;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 66;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		super.onBlockNeighborChange(bl);
		this.getConnectionMask();
		this.getExtConnectionMask();
	}
}
