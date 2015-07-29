package com.eloraam.redpower.core;

import com.eloraam.redpower.core.OreStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftLib {
	
	static List alloyRecipes = new ArrayList();
	public static HashSet damageOnCraft = new HashSet();
	public static HashMap damageContainer = new HashMap();
	
	public static void addAlloyResult(ItemStack output, Object... input) {
		alloyRecipes.add(Arrays.asList(new Object[] { input, output }));
	}
	
	public static void addOreRecipe(ItemStack output, Object... input) {
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(output, new Object[] { Boolean
						.valueOf(true), input }));
	}
	
	public static void addShapelessOreRecipe(ItemStack output, Object... input) {
		CraftingManager.getInstance().getRecipeList()
				.add(new ShapelessOreRecipe(output, input));
	}
	
	public static boolean isOreClass(ItemStack ist, String ore) {
		ArrayList ores = OreDictionary.getOres(ore);
		Iterator i$ = ores.iterator();
		
		ItemStack ois;
		do {
			if (!i$.hasNext()) {
				return false;
			}
			
			ois = (ItemStack) i$.next();
		} while (!ois.isItemEqual(ist));
		
		return true;
	}
	
	public static ItemStack getAlloyResult(ItemStack[] input, int ofs, int len, boolean decr) {
		Iterator i$ = alloyRecipes.iterator();
		
		label136: while (i$.hasNext()) {
			List l = (List) i$.next();
			Object[] ob = l.toArray();
			Object[] ipt = ((Object[]) ob[0]);
			Object[] arr$ = ipt;
			int len$ = ipt.length;
			
			int i$1;
			Object iso1;
			ItemStack os1;
			int rc;
			int i;
			OreStack var15;
			for (i$1 = 0; i$1 < len$; ++i$1) {
				iso1 = arr$[i$1];
				if (iso1 instanceof ItemStack) {
					os1 = (ItemStack) iso1;
					rc = os1.stackSize;
					
					for (i = ofs; i < len; ++i) {
						if (input[i] != null) {
							if (input[i].isItemEqual(os1)) {
								rc -= input[i].stackSize;
							}
							
							if (rc <= 0) {
								break;
							}
						}
					}
					
					if (rc > 0) {
						continue label136;
					}
				} else if (iso1 instanceof OreStack) {
					var15 = (OreStack) iso1;
					rc = var15.quantity;
					
					for (i = ofs; i < len; ++i) {
						if (input[i] != null) {
							if (isOreClass(input[i], var15.material)) {
								rc -= input[i].stackSize;
							}
							
							if (rc <= 0) {
								break;
							}
						}
					}
					
					if (rc > 0) {
						continue label136;
					}
				}
			}
			
			if (decr) {
				arr$ = ipt;
				len$ = ipt.length;
				
				for (i$1 = 0; i$1 < len$; ++i$1) {
					iso1 = arr$[i$1];
					if (iso1 instanceof ItemStack) {
						os1 = (ItemStack) iso1;
						rc = os1.stackSize;
						
						for (i = ofs; i < len; ++i) {
							if (input[i] != null && input[i].isItemEqual(os1)) {
								rc -= input[i].stackSize;
								if (rc < 0) {
									input[i].stackSize = -rc;
								} else if (input[i].getItem()
										.hasContainerItem()) {
									input[i] = new ItemStack(input[i].getItem()
											.getContainerItem());
								} else {
									input[i] = null;
								}
								
								if (rc <= 0) {
									break;
								}
							}
						}
					} else if (iso1 instanceof OreStack) {
						var15 = (OreStack) iso1;
						rc = var15.quantity;
						
						for (i = ofs; i < len; ++i) {
							if (input[i] != null
									&& isOreClass(input[i], var15.material)) {
								rc -= input[i].stackSize;
								if (rc < 0) {
									input[i].stackSize = -rc;
								} else {
									input[i] = null;
								}
								
								if (rc <= 0) {
									break;
								}
							}
						}
					}
				}
			}
			
			return (ItemStack) ob[1];
		}
		
		return null;
	}
	
}
