package com.eloraam.redpower.world;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCustomStone extends ItemBlock {
	
	public ItemCustomStone(Block block) {
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
				return "tile.marble";
			case 1:
				return "tile.basalt";
			case 2:
				return "tile.marbleBrick";
			case 3:
				return "tile.basaltCobble";
			case 4:
				return "tile.basaltBrick";
			case 5:
				return "tile.basaltCircle";
			case 6:
				return "tile.basaltPaver";
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
