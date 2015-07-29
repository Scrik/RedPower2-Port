package com.eloraam.redpower.world;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class EnchantmentVorpal extends EnchantmentDamage {
	
	public EnchantmentVorpal(int i, int j) {
		super(i, j, 0 /*all mobs*/);
	}
	
	@Override
	public int getMinEnchantability(int i) {
		return 20 + 10 * (i - 1);
	}
	
	@Override
	public int getMaxEnchantability(int i) {
		return this.getMinEnchantability(i) + 50;
	}
	
	@Override
	public int getMaxLevel() {
		return 4;
	}
	
	@Override //TODO Костыль
	public void func_151368_a(EntityLivingBase attacker, Entity target, int damage) {
        if (target instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase)target;
            if(target.worldObj.rand.nextInt(100) < 2 * damage * damage) {
            	entitylivingbase1.attackEntityFrom(DamageSource.magic, 100);
            }
        }
    }
	
	@Override
	public String getName() {
		return "enchantment.damage.vorpal";
	}
	
	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return enchantment != this;
	}
}
