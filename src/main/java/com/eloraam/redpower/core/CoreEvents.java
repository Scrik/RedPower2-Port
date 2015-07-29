package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class CoreEvents {
	
	@SubscribeEvent
	public void toolDestroyed(PlayerDestroyItemEvent ev) {
		EntityPlayer player = ev.entityPlayer;
		ItemStack orig = ev.original;
		int ci = player.inventory.currentItem;
		Item oid = orig.getItem();
		int odmg = orig.getItemDamage();
		ItemStack in2 = player.inventory.getStackInSlot(ci + 27);
		ItemStack ist = player.inventory.getStackInSlot(ci);
		if (ist != null && ist.stackSize <= 0) {
			if (in2 != null) {
				if (in2.getItem() == oid) {
					if (!in2.getHasSubtypes() || in2.getItemDamage() == odmg) {
						player.inventory.setInventorySlotContents(ci, in2);
						player.inventory.setInventorySlotContents(ci + 27,
								(ItemStack) null);
						
						for (int i = 2; i > 0; --i) {
							ist = player.inventory.getStackInSlot(ci + 9 * i);
							if (ist == null) {
								return;
							}
							
							if (ist.getItem() != oid) {
								return;
							}
							
							if (ist.getHasSubtypes()
									&& ist.getItemDamage() != odmg) {
								return;
							}
							
							player.inventory.setInventorySlotContents(ci + 9
									* i + 9, ist);
							player.inventory.setInventorySlotContents(ci + 9
									* i, (ItemStack) null);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void oreRegister(OreRegisterEvent ev) {
		CoreLib.registerOre(ev.Name, ev.Ore);
	}
	
	/*@SubscribeEvent //TODO: KOKOKO
	public void liquidRegister(LiquidRegisterEvent ev) {
		PipeLib.registerForgeFluid(ev.Name, ev.Liquid);
	}*/
}
