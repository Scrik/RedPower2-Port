package com.eloraam.redpower.lighting;

import net.minecraft.block.Block;

import com.eloraam.redpower.core.ItemExtended;

public class ItemShapedLamp extends ItemExtended {
	
	public ItemShapedLamp(Block block) {
		super(block);
	}
	
	@Override
	public int getMetadata(int i) {
		return i >> 10;
	}
}
