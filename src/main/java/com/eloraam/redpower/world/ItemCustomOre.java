package com.eloraam.redpower.world;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCustomOre extends ItemBlock {
	
	public ItemCustomOre(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	/*public int getPlacedBlockMetadata(int i) { //TODO: CHECK IT OUT
		return i;
	}*/
	
	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		switch (itemstack.getItemDamage()) {
			case 0:
				return "tile.oreRuby";
			case 1:
				return "tile.oreGreenSapphire";
			case 2:
				return "tile.oreSapphire";
			case 3:
				return "tile.oreSilver";
			case 4:
				return "tile.oreTin";
			case 5:
				return "tile.oreCopper";
			case 6:
				return "tile.oreTungsten";
			case 7:
				return "tile.oreNikolite";
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
