package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ItemCustomPickaxe extends ItemPickaxe {
	
	public ItemCustomPickaxe(ToolMaterial mat) {
		super(mat);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
		return this.toolMaterial == RedPowerWorld.toolMaterialRuby
				&& ist2.isItemEqual(RedPowerBase.itemRuby) ? true : (this.toolMaterial == RedPowerWorld.toolMaterialSapphire
				&& ist2.isItemEqual(RedPowerBase.itemSapphire) ? true : (this.toolMaterial == RedPowerWorld.toolMaterialGreenSapphire
				&& ist2.isItemEqual(RedPowerBase.itemGreenSapphire) ? true : super
				.getIsRepairable(ist1, ist2)));
	}
}
