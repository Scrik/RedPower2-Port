package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentDisjunction extends Enchantment {
	
	public EnchantmentDisjunction(int i, int j) {
		super(i, j, EnumEnchantmentType.weapon);
	}
	
	@Override
	public int getMinEnchantability(int i) {
		return 5 + 8 * i;
	}
	
	@Override
	public int getMaxEnchantability(int i) {
		return this.getMinEnchantability(i) + 20;
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}
	
	/*@Override  //TODO: FIX THIS
	public int calcModifierLiving(int i, EntityLivingBase ent) {
		return !(ent instanceof EntityEnderman)
				&& !(ent instanceof EntityDragon) ? 0 : i * 6;
	}
	
	public int calcModifierDamage(int i, DamageSource damageSource) {
        return 0;
    }*/
	
	@Override
	public String getName() {
		return "enchantment.damage.disjunction";
	}
	
	@Override
	public boolean canApply(ItemStack ist) {
		return ist.getItem() == RedPowerWorld.itemAthame;
    }
	
	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return enchantment == this ? false : !(enchantment instanceof EnchantmentDamage);
	}
}
