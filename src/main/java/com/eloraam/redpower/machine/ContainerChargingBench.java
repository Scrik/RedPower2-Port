package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.TileChargingBench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChargingBench extends Container {

   private TileChargingBench tileCB;
   public int charge;
   public int storage;


   public ContainerChargingBench(IInventory inv, TileChargingBench tf) {
      this.tileCB = tf;

      int i;
      int j;
      for(i = 0; i < 4; ++i) {
         for(j = 0; j < 4; ++j) {
            this.addSlotToContainer(new Slot(tf, j + i * 4, 80 + j * 18, 18 + i * 18));
         }
      }

      for(i = 0; i < 3; ++i) {
         for(j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
         }
      }

      for(i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 162));
      }

   }

   @Override
public boolean canInteractWith(EntityPlayer player) {
      return this.tileCB.isUseableByPlayer(player);
   }

   @Override
public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if(slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if(i < 16) {
            if(!this.mergeItemStack(itemstack1, 16, 52, true)) {
               return null;
            }
         } else if(!this.mergeItemStack(itemstack1, 0, 16, false)) {
            return null;
         }

         if(itemstack1.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if(itemstack1.stackSize == itemstack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(player, itemstack1);
      }

      return itemstack;
   }

   @Override
public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(int i = 0; i < super.crafters.size(); ++i) {
         ICrafting ic = (ICrafting)super.crafters.get(i);
         if(this.charge != this.tileCB.cond.Charge) {
            ic.sendProgressBarUpdate(this, 0, this.tileCB.cond.Charge);
         }

         if(this.storage != this.tileCB.Storage) {
            ic.sendProgressBarUpdate(this, 1, this.tileCB.Storage);
         }
      }

      this.charge = this.tileCB.cond.Charge;
      this.storage = this.tileCB.Storage;
   }

   @Override
public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileCB.cond.Charge = j;
         break;
      case 1:
         this.tileCB.Storage = j;
      }

   }
}
