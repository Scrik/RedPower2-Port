package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemCustomSword extends ItemSword {
	
	protected ToolMaterial toolMaterial2;
	
	public ItemCustomSword(ToolMaterial mat) {
		super(mat);
		this.toolMaterial2 = mat;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
		return this.toolMaterial2 == RedPowerWorld.toolMaterialRuby
				&& ist2.isItemEqual(RedPowerBase.itemRuby) ? true : (this.toolMaterial2 == RedPowerWorld.toolMaterialSapphire
				&& ist2.isItemEqual(RedPowerBase.itemSapphire) ? true : (this.toolMaterial2 == RedPowerWorld.toolMaterialGreenSapphire
				&& ist2.isItemEqual(RedPowerBase.itemGreenSapphire) ? true : super
				.getIsRepairable(ist1, ist2)));
	}
}
