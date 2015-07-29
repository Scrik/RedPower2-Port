package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;

import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

public class ItemCustomHoe extends ItemHoe {
	
	public ItemCustomHoe(ToolMaterial mat) {
		super(mat);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
		return this.theToolMaterial == RedPowerWorld.toolMaterialRuby
				&& ist2.isItemEqual(RedPowerBase.itemRuby) ? true : (this.theToolMaterial == RedPowerWorld.toolMaterialSapphire
				&& ist2.isItemEqual(RedPowerBase.itemSapphire) ? true : (this.theToolMaterial == RedPowerWorld.toolMaterialGreenSapphire
				&& ist2.isItemEqual(RedPowerBase.itemGreenSapphire) ? true : super
				.getIsRepairable(ist1, ist2)));
	}
}
