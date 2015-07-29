package com.eloraam.redpower.world;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemStorage extends ItemBlock {
	
	public ItemStorage(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public int getPlacedBlockMetadata(int i) {
		return i;
	}
	
	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		switch (itemstack.getItemDamage()) {
			case 0:
				return "tile.blockRuby";
			case 1:
				return "tile.blockGreenSapphire";
			case 2:
				return "tile.blockSapphire";
			case 3:
				return "tile.blockSilver";
			case 4:
				return "tile.blockTin";
			case 5:
				return "tile.blockCopper";
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
