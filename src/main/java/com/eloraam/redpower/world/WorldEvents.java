package com.eloraam.redpower.world;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.world.ItemSeedBag;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class WorldEvents {
	
	@SubscribeEvent
	public void onBonemeal(BonemealEvent ev) {
		if (ev.block == RedPowerWorld.blockCrops) {
			int md = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);
			if (md == 4 || md == 5) {
				return;
			}
			if (CoreLib.isClient(ev.world)) {
				ev.setResult(Result.ALLOW);
				return;
			}
			if (RedPowerWorld.blockCrops.fertilize(ev.world, ev.x, ev.y, ev.z)) {
				ev.setResult(Result.ALLOW);
			}
		} else if(ev.block == RedPowerWorld.blockPlants && ev.world.getBlockMetadata(ev.x, ev.y, ev.z) >= 1) {
			if(RedPowerWorld.blockPlants.growTree(ev.world, ev.x, ev.y, ev.z)) {
				ev.setResult(Result.ALLOW);
			}
		}
	}
	
	@SubscribeEvent
	public void onDeath(LivingDeathEvent ev) {
		if (ev.source instanceof EntityDamageSource) {
			EntityDamageSource eds = (EntityDamageSource) ev.source;
			Entity ent = eds.getEntity();
			if (ent instanceof EntityPlayer) {
				EntityPlayer epl = (EntityPlayer) ent;
				ItemStack wpn = epl.getCurrentEquippedItem();
				if (EnchantmentHelper.getEnchantmentLevel(RedPowerWorld.enchantVorpal.effectId, wpn) != 0) {
					if (ev.entityLiving.getHealth() <= -20) {
						if (ev.entityLiving instanceof EntitySkeleton) {
							EntitySkeleton ist = (EntitySkeleton) ev.entityLiving;
							if (ist.getSkeletonType() == 1) {
								return;
							}
							ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 0), 0.0F);
						} else if (ev.entityLiving instanceof EntityZombie) {
							ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 2), 0.0F);
						} else if (ev.entityLiving instanceof EntityPlayer) {
							ItemStack ist1 = new ItemStack(Items.skull, 1, 3);
							ist1.setTagCompound(new NBTTagCompound());
							ist1.getTagCompound().setString("SkullOwner", ev.entityLiving.getCommandSenderName());
							ev.entityLiving.entityDropItem(ist1, 0.0F);
						} else if (ev.entityLiving instanceof EntityCreeper) {
							ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPickupItem(EntityItemPickupEvent ev) {
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = ev.entityPlayer.inventory.getStackInSlot(i);
			if (ist != null && ist.getItem() instanceof ItemSeedBag) {
				IInventory inv = ItemSeedBag.getBagInventory(ist);
				if (inv != null && ItemSeedBag.getPlant(inv) != null) {
					ItemStack tpi = ev.item.getEntityItem();
					List<Integer> list = new ArrayList<Integer>(inv.getSizeInventory());
					for(int j = 0; j < inv.getSizeInventory(); j ++) {
						list.add(j);
					}
					if (ItemSeedBag.canAdd(inv, tpi) && MachineLib.addToInventoryCore(inv, tpi, list, true)) {
						ev.item.setDead();
						ev.setResult(Result.ALLOW);
						return;
					}
				}
			}
		}
	}
}
