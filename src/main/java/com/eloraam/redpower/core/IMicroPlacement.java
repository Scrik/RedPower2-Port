package com.eloraam.redpower.core;

import com.eloraam.redpower.core.WorldCoord;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMicroPlacement {
	boolean onPlaceMicro(ItemStack itemStack, EntityPlayer player, World worldObj, WorldCoord wc, int var5);
	
	String getMicroName(int var1, int var2);
	
	void addCreativeItems(int hb, CreativeTabs tab, List<ItemStack> itemList);
}
