package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;

public class ItemAthame extends ItemSword {
	
	private float entityDamage;
	
	public ItemAthame() {
		super(ToolMaterial.EMERALD);
		this.setMaxDamage(100);
		this.setTextureName("rpworld:itemAthame");
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.entityDamage=(float)1;
	}
	
	@Override
	public float func_150893_a(ItemStack itemstack, Block block) {
		return 1.0F;
	}
	
	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase hitEntity, EntityLivingBase host) {
		itemStack.damageItem(1, host);
		if((hitEntity instanceof EntityEnderman) || (hitEntity instanceof EntityDragon)) {
			hitEntity.attackEntityFrom(DamageSource.causeMobDamage(host), (float)25);
		}
        return true;
    }
	
	@Override
	public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
		return ist2.isItemEqual(RedPowerBase.itemIngotSilver);
	}
	
	@Override
	public int getItemEnchantability() {
		return 30;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.entityDamage, 0));
        return multimap;
    }
}
