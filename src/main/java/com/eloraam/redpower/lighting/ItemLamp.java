package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.CoreLib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemLamp extends ItemBlock {
	
	public ItemLamp(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	public int getPlacedBlockMetadata(int i) {
		return i;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return ist.getItem() == Item.getItemFromBlock(RedPowerLighting.blockInvLampOn) ? "tile.rpilamp."
				+ CoreLib.rawColorNames[ist.getItemDamage()] : "tile.rplamp."
				+ CoreLib.rawColorNames[ist.getItemDamage()];
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i <= 15; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}
}
