package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;

import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class AchieveLib {
	
	private static HashMap<String, Achievement> achievelist = new HashMap<String, Achievement>();
	public static AchievementPage achievepage = new AchievementPage("RedPower", new Achievement[0]);
	private static TreeMap<ItemStack, Achievement> achievebycraft = new TreeMap<ItemStack, Achievement>(CoreLib.itemStackComparator);
	private static TreeMap<ItemStack, Achievement> achievebyfurnace = new TreeMap<ItemStack, Achievement>(CoreLib.itemStackComparator);
	private static TreeMap<ItemStack, Achievement> achievebyalloy = new TreeMap<ItemStack, Achievement>(CoreLib.itemStackComparator);
	
	public static void registerAchievement(String id, String name, int x, int y, ItemStack icon, Object require, boolean special) {
		Achievement acreq = null;
		if (require instanceof Achievement) {
			acreq = (Achievement) require;
		} else if (require instanceof String) {
			acreq = (Achievement) achievelist.get(require);
		}
		
		Achievement ac = new Achievement(id, name, x, y, icon, acreq);
		ac.registerStat();
		if (special) {
			ac.setSpecial();
		}
		
		achievelist.put(name, ac);
		achievepage.getAchievements().add(ac);
	}
	
	public static void registerAchievement(String id, String name, int x, int y, ItemStack icon, Object require) {
		registerAchievement(id, name, x, y, icon, require, false);
	}
	
	public static void addCraftingAchievement(ItemStack target, String id) {
		Achievement ac = (Achievement) achievelist.get(id);
		if (ac != null) {
			achievebycraft.put(target, ac);
		}
	}
	
	public static void addAlloyAchievement(ItemStack target, String id) {
		Achievement ac = (Achievement) achievelist.get(id);
		if (ac != null) {
			achievebyalloy.put(target, ac);
		}
	}
	
	public static void addFurnaceAchievement(ItemStack target, String id) {
		Achievement ac = (Achievement) achievelist.get(id);
		if (ac != null) {
			achievebyfurnace.put(target, ac);
		}
	}
	
	public static void triggerAchievement(EntityPlayer player, String id) {
		Achievement ac = (Achievement) achievelist.get(id);
		if (ac != null) {
			player.triggerAchievement(ac);
		}
	}
	
	public static void onCrafting(EntityPlayer player, ItemStack ist) {
		Achievement ac = (Achievement) achievebycraft.get(ist);
		if (ac != null) {
			player.triggerAchievement(ac);
		}
	}
	
	public static void onFurnace(EntityPlayer player, ItemStack ist) {
		Achievement ac = (Achievement) achievebyfurnace.get(ist);
		if (ac != null) {
			player.triggerAchievement(ac);
		}
	}
	
	public static void onAlloy(EntityPlayer player, ItemStack ist) {
		Achievement ac = (Achievement) achievebyalloy.get(ist);
		if (ac != null) {
			player.triggerAchievement(ac);
		}
	}
	
}
