package com.eloraam.redpower.base;

import com.eloraam.redpower.core.AchieveLib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAlloyFurnace extends Slot {

   private EntityPlayer thePlayer;
   int totalCrafted;


   public SlotAlloyFurnace(EntityPlayer player, IInventory inv, int i, int j, int k) {
      super(inv, i, j, k);
      this.thePlayer = player;
   }

   @Override
public boolean isItemValid(ItemStack ist) {
      return false;
   }

   @Override
public ItemStack decrStackSize(int num) {
      if(this.getHasStack()) {
         this.totalCrafted += Math.min(num, this.getStack().stackSize);
      }

      return super.decrStackSize(num);
   }

   @Override
public void onPickupFromSlot(EntityPlayer player, ItemStack ist) {
      this.onCrafting(ist);
      super.onPickupFromSlot(player, ist);
   }

   @Override
protected void onCrafting(ItemStack ist, int num) {
      this.totalCrafted += num;
      this.onCrafting(ist);
   }

   @Override
protected void onCrafting(ItemStack ist) {
      ist.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.totalCrafted);
      this.totalCrafted = 0;
      AchieveLib.onAlloy(this.thePlayer, ist);
   }
}
