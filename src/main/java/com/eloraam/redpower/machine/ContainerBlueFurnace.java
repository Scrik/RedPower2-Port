package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.TileBlueFurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerBlueFurnace extends Container {

   public int cooktime = 0;
   private TileBlueFurnace tileFurnace;
   public int charge = 0;
   public int flow = 0;


   public ContainerBlueFurnace(InventoryPlayer inv, TileBlueFurnace td) {
      this.tileFurnace = td;
      this.addSlotToContainer(new Slot(td, 0, 62, 35));
      this.addSlotToContainer(new SlotFurnace(inv.player, td, 1, 126, 35));

      int i;
      for(i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
      }

   }

   @Override
public boolean canInteractWith(EntityPlayer player) {
      return this.tileFurnace.isUseableByPlayer(player);
   }

   @Override
public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if(slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if(i < 2) {
            if(!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return null;
            }
         } else if(!this.mergeItemStack(itemstack1, 0, 1, false)) {
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
         if(this.cooktime != this.tileFurnace.cooktime) {
            ic.sendProgressBarUpdate(this, 0, this.tileFurnace.cooktime);
         }

         if(this.charge != this.tileFurnace.cond.Charge) {
            ic.sendProgressBarUpdate(this, 1, this.tileFurnace.cond.Charge);
         }

         if(this.flow != this.tileFurnace.cond.Flow) {
            ic.sendProgressBarUpdate(this, 2, this.tileFurnace.cond.Flow & '\uffff');
            ic.sendProgressBarUpdate(this, 3, this.tileFurnace.cond.Flow >> 16 & '\uffff');
         }
      }

      this.cooktime = this.tileFurnace.cooktime;
      this.charge = this.tileFurnace.cond.Charge;
      this.flow = this.tileFurnace.cond.Flow;
   }

   public void func_20112_a(int i, int j) {
      this.updateProgressBar(i, j);
   }

   @Override
public void updateProgressBar(int i, int j) {
      switch(i) {
      case 0:
         this.tileFurnace.cooktime = j;
         break;
      case 1:
         this.tileFurnace.cond.Charge = j;
         break;
      case 2:
         this.tileFurnace.cond.Flow = this.tileFurnace.cond.Flow & -65536 | j & '\uffff';
         break;
      case 3:
         this.tileFurnace.cond.Flow = this.tileFurnace.cond.Flow & '\uffff' | (j & '\uffff') << 16;
      }

   }
}
