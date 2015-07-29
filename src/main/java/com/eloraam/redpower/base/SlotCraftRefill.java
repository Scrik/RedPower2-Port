package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ContainerAdvBench;
import com.eloraam.redpower.core.CoreLib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotCraftRefill extends SlotCrafting {
	
	IInventory allSlots;
	IInventory craftingMatrix;
	ContainerAdvBench eventHandler;
	
	public SlotCraftRefill(EntityPlayer player, IInventory matrix, IInventory result, IInventory all, ContainerAdvBench evh, int i, int j, int k) {
		super(player, matrix, result, i, j, k);
		this.allSlots = all;
		this.craftingMatrix = matrix;
		this.eventHandler = evh;
	}
	
	private int findMatch(ItemStack a) {
		for (int i = 0; i < 18; ++i) {
			ItemStack test = this.allSlots.getStackInSlot(10 + i);
			if (test != null && test.stackSize != 0
					&& CoreLib.matchItemStackOre(a, test)) {
				return 10 + i;
			}
		}
		return -1;
	}
	
	public boolean isLastUse() {
		int bits = 0;
		int i;
		ItemStack test;
		for (i = 0; i < 9; ++i) {
			test = this.allSlots.getStackInSlot(i);
			if (test == null) {
				bits |= 1 << i;
			} else if (!test.isStackable()) {
				bits |= 1 << i;
			} else if (test.stackSize > 1) {
				bits |= 1 << i;
			}
		}
		
		if (bits == 511) {
			return false;
		} else {
			for (i = 0; i < 18; ++i) {
				test = this.allSlots.getStackInSlot(10 + i);
				if (test != null && test.stackSize != 0) {
					int sc = test.stackSize;
					for (int j = 0; j < 9; ++j) {
						if ((bits & 1 << j) <= 0) {
							ItemStack st = this.allSlots.getStackInSlot(j);
							if (st != null && CoreLib.matchItemStackOre(st, test)) {
								bits |= 1 << j;
								--sc;
								if (sc == 0) {
									break;
								}
							}
						}
					}
				}
			}
			return bits != 511;
		}
	}
	
	public void a(EntityPlayer player, ItemStack ist) {
		ItemStack[] plan = this.eventHandler.getPlanItems();
		ItemStack[] cur = new ItemStack[9];
		
		for (int last = 0; last < 9; ++last) {
			ItemStack idx = this.allSlots.getStackInSlot(last);
			if (idx == null) {
				cur[last] = null;
			} else {
				cur[last] = idx.copy();
			}
		}
		
		boolean var12 = this.isLastUse();
		int var13;
		if (plan != null) {
			for (var13 = 0; var13 < 9; ++var13) {
				if (cur[var13] == null && plan[var13] != null) {
					int i1 = this.findMatch(plan[var13]);
					if (i1 >= 0) {
						ItemStack ch = this.allSlots.getStackInSlot(i1);
						if (ch != null) {
							this.allSlots.decrStackSize(i1, 1);
							if (ch.getItem().getContainerItem() != null) {
								ItemStack i = ch.getItem().getContainerItem(ch);
								this.allSlots.setInventorySlotContents(i1, i);
							}
						}
					}
				}
			}
		}
		
		super.onPickupFromSlot(player, ist);
		if (var12) {
			this.eventHandler.onCraftMatrixChanged(this.craftingMatrix);
		} else {
			for (int var16 = 0; var16 < 9; ++var16) {
				if (cur[var16] != null) {
					ItemStack nsl = this.allSlots.getStackInSlot(var16);
					if (plan == null || plan[var16] == null) {
						if (nsl != null) {
							if (!CoreLib.matchItemStackOre(nsl, cur[var16]) && cur[var16].getItem().getContainerItem() != null) {
								ItemStack ctr = cur[var16].getItem() .getContainerItem(cur[var16]);
								if (ctr != null && ctr.getItem() == nsl.getItem()) {
									var13 = this.findMatch(cur[var16]);
									if (var13 >= 0) {
										ItemStack var14 = this.allSlots.getStackInSlot(var13);
										this.allSlots.setInventorySlotContents(var13, nsl);
										this.allSlots.setInventorySlotContents(var16, var14);
									}
								}
							}
						} else {
							var13 = this.findMatch(cur[var16]);
							if (var13 >= 0) {
								this.allSlots.getStackInSlot(var13);
								this.allSlots.setInventorySlotContents(var16, this.allSlots.decrStackSize(var13, 1));
							}
						}
					}
				}
			}
			this.eventHandler.onCraftMatrixChanged(this.craftingMatrix);
		}
	}
}
