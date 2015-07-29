package com.eloraam.redpower.core;

import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.core.CoverLib;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CoverRecipe implements IRecipe {

	private static ItemStack newCover(int num, int type, int mat) {
		return new ItemStack(CoverLib.blockCoverPlate, num, type << 8 | mat);
	}

	private ItemStack getSawRecipe(InventoryCrafting inv, ItemStack saw, int sawpos, ItemStack mat, int matpos) {
		int sp1 = sawpos & 15;
		int sp2 = sawpos >> 4;
		int mp1 = matpos & 15;
		int mp2 = matpos >> 4;
		//boolean mn = true;
		int dmg = -1;
		int mn1;
		if(mat.getItem() == Item.getItemFromBlock(CoverLib.blockCoverPlate)) {
			dmg = mat.getItemDamage();
			mn1 = dmg & 255;
			dmg >>= 8;
		} else {
			Integer ihs = CoverLib.getMaterial(mat);
			if(ihs == null) {
				return null;
			}

			mn1 = ihs.intValue();
		}

		ItemHandsaw ihs1 = (ItemHandsaw)saw.getItem();
		if(ihs1.getSharpness() < CoverLib.getHardness(mn1)) {
			return null;
		} else if(sp1 == mp1 && (sp2 == mp2 + 1 || sp2 == mp2 - 1)) {
			switch(dmg) {
			case -1:
				return newCover(2, 17, mn1);
			case 16:
				return newCover(2, 0, mn1);
			case 17:
				return newCover(2, 16, mn1);
			case 25:
				return newCover(2, 24, mn1);
			case 26:
				return newCover(2, 25, mn1);
			case 29:
				return newCover(2, 27, mn1);
			case 33:
				return newCover(2, 31, mn1);
			default:
				return null;
			}
		} else if(sp2 == mp2 && (sp1 == mp1 + 1 || sp1 == mp1 - 1)) {
			switch(dmg) {
				case 0:
					return newCover(2, 21, mn1);
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 18:
				case 19:
				case 20:
				case 24:
				case 25:
				case 26:
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
				default:
					return null;
				case 16:
					return newCover(2, 22, mn1);
				case 17:
					return newCover(2, 23, mn1);
				case 21:
					return newCover(2, 18, mn1);
				case 22:
					return newCover(2, 19, mn1);
				case 23:
					return newCover(2, 20, mn1);
				case 27:
					return newCover(2, 39, mn1);
				case 28:
					return newCover(2, 40, mn1);
				case 29:
					return newCover(2, 41, mn1);
				case 30:
					return newCover(2, 42, mn1);
				case 39:
					return newCover(2, 35, mn1);
				case 40:
					return newCover(2, 36, mn1);
				case 41:
					return newCover(2, 37, mn1);
				case 42:
					return newCover(2, 38, mn1);
				}
		} else {
			return null;
		}
	}

	private ItemStack getColumnRecipe(ItemStack mat) {
		if(mat.getItem() != Item.getItemFromBlock(CoverLib.blockCoverPlate)) {
			return null;
		} else {
			int dmg = mat.getItemDamage();
			int mn = dmg & 255;
			dmg >>= 8;
			switch(dmg) {
				case 22:
					return newCover(1, 43, mn);
				case 23:
					return newCover(1, 44, mn);
				case 41:
					return newCover(1, 45, mn);
				case 43:
					return newCover(1, 22, mn);
				case 44:
					return newCover(1, 23, mn);
				case 45:
					return newCover(1, 41, mn);
				default:
					return null;
			}
		}
	}

	private ItemStack getMergeRecipe(int mn, int tth, int ic) {
		int mc = mn >> 20;
		mn &= 255;
		switch(mc) {
		case 0:
			switch(tth) {
			case 2:
				return newCover(1, 16, mn);
			case 3:
				return newCover(1, 27, mn);
			case 4:
				return newCover(1, 17, mn);
			case 5:
				return newCover(1, 28, mn);
			case 6:
				return newCover(1, 29, mn);
			case 7:
				return newCover(1, 30, mn);
			case 8:
				return CoverLib.getItemStack(mn);
			default:
				return null;
			}
		case 1:
			switch(tth) {
			case 2:
				return newCover(1, 25, mn);
			case 3:
				return newCover(1, 31, mn);
			case 4:
				return newCover(1, 26, mn);
			case 5:
				return newCover(1, 32, mn);
			case 6:
				return newCover(1, 33, mn);
			case 7:
				return newCover(1, 34, mn);
			case 8:
				return CoverLib.getItemStack(mn);
			default:
				return null;
			}
		case 16:
			switch(tth) {
			case 2:
				return newCover(1, 0, mn);
			case 4:
				return newCover(1, 16, mn);
			case 8:
				return newCover(1, 17, mn);
			case 16:
				return CoverLib.getItemStack(mn);
			default:
				return null;
			}
		case 32:
			if(ic == 2) {
				switch(tth) {
				case 2:
					return newCover(1, 21, mn);
				case 4:
					return newCover(1, 22, mn);
				case 8:
					return newCover(1, 23, mn);
				}
			} else {
				switch(tth) {
				case 4:
					return newCover(1, 0, mn);
				case 8:
					return newCover(1, 16, mn);
				case 16:
					return newCover(1, 17, mn);
				case 32:
					return CoverLib.getItemStack(mn);
				}
			}
		}

		return null;
	}

	private ItemStack getHollowRecipe(int mn) {
		int mc = mn >> 8 & 255;
		mn &= 255;
		switch(mc) {
		case 0:
			return newCover(8, 24, mn);
		case 16:
			return newCover(8, 25, mn);
		case 17:
			return newCover(8, 26, mn);
		case 27:
			return newCover(8, 31, mn);
		case 28:
			return newCover(8, 32, mn);
		case 29:
			return newCover(8, 33, mn);
		case 30:
			return newCover(8, 34, mn);
		default:
			return null;
		}
	}

	private int getMicroClass(ItemStack ist) {
		if(ist.getItem() != Item.getItemFromBlock(CoverLib.blockCoverPlate)) {
			return -1;
		} else {
			int dmg = ist.getItemDamage();
			return CoverLib.damageToCoverData(dmg);
		}
	}

	private ItemStack findResult(InventoryCrafting inv) {
		ItemStack saw = null;
		ItemStack mat = null;
		boolean bad = false;
		boolean allmicro = true;
		boolean strict = true;
		int sp = 0;
		int mp = 0;
		int mn = -1;
		int tth = 0;
		int ic = 0;

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				ItemStack ist = inv.getStackInRowAndColumn(i, j);
				if(ist != null) {
					if(ist.getItem() instanceof ItemHandsaw) {
						if(saw != null) {
							bad = true;
						} else {
							saw = ist;
							sp = i + j * 16;
						}
					} else if(mat == null) {
						mat = ist;
						mp = i + j * 16;
						mn = this.getMicroClass(ist);
						if(mn >= 0) {
							tth += mn >> 16 & 15;
						} else {
							allmicro = false;
						}

						ic = 1;
					} else {
						bad = true;
						if(allmicro) {
							int t = this.getMicroClass(ist);
							if(((t ^ mn) & -1048321) != 0) {
								allmicro = false;
							} else {
								if(t != mn) {
									strict = false;
								}

								tth += t >> 16 & 15;
								++ic;
							}
						}
					}
				}
			}
		}

		if(saw != null && mat != null && !bad) {
			return this.getSawRecipe(inv, saw, sp, mat, mp);
		} else if(saw == null && mat != null && !bad) {
			return this.getColumnRecipe(mat);
		} else if(allmicro && bad && saw == null) {
			if(ic == 8 && strict && inv.getStackInRowAndColumn(1, 1) == null && mn >> 20 == 0) {
				return this.getHollowRecipe(mn);
			} else {
				return this.getMergeRecipe(mn, tth, ic);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		return this.findResult(inv) != null;
	}
	
	@Override
	public int getRecipeSize() {
		return 9;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return this.findResult(inv).copy();
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(CoverLib.blockCoverPlate, 1, 0);
	}
}
