package com.eloraam.redpower.core;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IMultipart {
	
	boolean isSideSolid(int side);
	
	boolean isSideNormal(int side);
	
	List<ItemStack> harvestMultipart();
}
