package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IPipeConnectable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemVoltmeter extends Item {

   public ItemVoltmeter() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setTextureName("rpmachine:itemVoltmeter");
      this.setUnlocalizedName("voltmeter");
   }

   private boolean measureBlue(EntityPlayer player, World world, int i, int j, int k, int l) {
      IBluePowerConnectable ibc = (IBluePowerConnectable)CoreLib.getTileEntity(world, i, j, k, IBluePowerConnectable.class);
      if(ibc == null) {
         return false;
      } else {
         BluePowerConductor bpc = ibc.getBlueConductor(l);
         double v = bpc.getVoltage();
         CoreLib.writeChat(player, String.format("Reading %.2fV %.2fA (%.2fW)", new Object[]{Double.valueOf(v), Double.valueOf(bpc.Itot), Double.valueOf(v * bpc.Itot)}));
         return true;
      }
   }

   private boolean measurePressure(EntityPlayer player, World world, int i, int j, int k, int l) {
      IPipeConnectable ipc = (IPipeConnectable)CoreLib.getTileEntity(world, i, j, k, IPipeConnectable.class);
      if(ipc == null) {
         return false;
      } else {
         int psi = ipc.getPipePressure(l);
         CoreLib.writeChat(player, String.format("Reading %d psi", new Object[]{Integer.valueOf(psi)}));
         return true;
      }
   }

   private boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
      return this.measureBlue(player, world, i, j, k, l)?true:this.measurePressure(player, world, i, j, k, l);
   }

   @Override
public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
      return player.isSneaking()?false:this.itemUseShared(ist, player, world, i, j, k, l);
   }

   @Override
public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
      return CoreLib.isClient(world)?false:(!player.isSneaking()?false:this.itemUseShared(ist, player, world, i, j, k, l));
   }
}
