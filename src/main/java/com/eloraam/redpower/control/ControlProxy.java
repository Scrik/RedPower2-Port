package com.eloraam.redpower.control;

import com.eloraam.redpower.control.ContainerCPU;
import com.eloraam.redpower.control.ContainerDisplay;
import com.eloraam.redpower.control.GuiCPU;
import com.eloraam.redpower.control.GuiDisplay;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.control.TileDisplay;
import com.eloraam.redpower.core.CoreLib;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ControlProxy implements IGuiHandler {

   public void registerRenderers() {}

   @Override
public Object getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      switch(ID) {
      case 1:
         return new GuiDisplay(player.inventory, (TileDisplay)CoreLib.getGuiTileEntity(world, X, Y, Z, TileDisplay.class));
      case 2:
         return new GuiCPU(player.inventory, (TileCPU)CoreLib.getGuiTileEntity(world, X, Y, Z, TileCPU.class));
      default:
         return null;
      }
   }

   @Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
      switch(ID) {
      case 1:
         return new ContainerDisplay(player.inventory, (TileDisplay)CoreLib.getTileEntity(world, X, Y, Z, TileDisplay.class));
      case 2:
         return new ContainerCPU(player.inventory, (TileCPU)CoreLib.getTileEntity(world, X, Y, Z, TileCPU.class));
      default:
         return null;
      }
   }
}
