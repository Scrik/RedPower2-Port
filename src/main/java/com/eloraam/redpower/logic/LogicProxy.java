package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.logic.ContainerCounter;
import com.eloraam.redpower.logic.ContainerTimer;
import com.eloraam.redpower.logic.GuiCounter;
import com.eloraam.redpower.logic.GuiTimer;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.logic.TileLogicStorage;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class LogicProxy implements IGuiHandler {

   public void registerRenderers() {}

   @Override
public Object getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      switch(ID) {
      case 1:
         return new GuiCounter(player.inventory, (TileLogicStorage)CoreLib.getGuiTileEntity(world, X, Y, Z, TileLogicStorage.class));
      case 2:
         return new GuiTimer(player.inventory, (TileLogicPointer)CoreLib.getGuiTileEntity(world, X, Y, Z, TileLogicPointer.class));
      default:
         return null;
      }
   }

   @Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      switch(ID) {
      case 1:
         return new ContainerCounter(player.inventory, (TileLogicStorage)CoreLib.getTileEntity(world, X, Y, Z, TileLogicStorage.class));
      case 2:
         return new ContainerTimer(player.inventory, (TileLogicPointer)CoreLib.getTileEntity(world, X, Y, Z, TileLogicPointer.class));
      default:
         return null;
      }
   }
}
