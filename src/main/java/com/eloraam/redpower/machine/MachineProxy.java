package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.machine.ContainerAssemble;
import com.eloraam.redpower.machine.ContainerBatteryBox;
import com.eloraam.redpower.machine.ContainerBlueAlloyFurnace;
import com.eloraam.redpower.machine.ContainerBlueFurnace;
import com.eloraam.redpower.machine.ContainerBufferChest;
import com.eloraam.redpower.machine.ContainerChargingBench;
import com.eloraam.redpower.machine.ContainerDeploy;
import com.eloraam.redpower.machine.ContainerEject;
import com.eloraam.redpower.machine.ContainerFilter;
import com.eloraam.redpower.machine.ContainerItemDetect;
import com.eloraam.redpower.machine.ContainerManager;
import com.eloraam.redpower.machine.ContainerRegulator;
import com.eloraam.redpower.machine.ContainerRetriever;
import com.eloraam.redpower.machine.ContainerSorter;
import com.eloraam.redpower.machine.ContainerWindTurbine;
import com.eloraam.redpower.machine.GuiAssemble;
import com.eloraam.redpower.machine.GuiBatteryBox;
import com.eloraam.redpower.machine.GuiBlueAlloyFurnace;
import com.eloraam.redpower.machine.GuiBlueFurnace;
import com.eloraam.redpower.machine.GuiBufferChest;
import com.eloraam.redpower.machine.GuiChargingBench;
import com.eloraam.redpower.machine.GuiDeploy;
import com.eloraam.redpower.machine.GuiEject;
import com.eloraam.redpower.machine.GuiFilter;
import com.eloraam.redpower.machine.GuiItemDetect;
import com.eloraam.redpower.machine.GuiManager;
import com.eloraam.redpower.machine.GuiRegulator;
import com.eloraam.redpower.machine.GuiRetriever;
import com.eloraam.redpower.machine.GuiSorter;
import com.eloraam.redpower.machine.GuiWindTurbine;
import com.eloraam.redpower.machine.TileAssemble;
import com.eloraam.redpower.machine.TileBatteryBox;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;
import com.eloraam.redpower.machine.TileBlueFurnace;
import com.eloraam.redpower.machine.TileBufferChest;
import com.eloraam.redpower.machine.TileChargingBench;
import com.eloraam.redpower.machine.TileDeploy;
import com.eloraam.redpower.machine.TileEjectBase;
import com.eloraam.redpower.machine.TileFilter;
import com.eloraam.redpower.machine.TileItemDetect;
import com.eloraam.redpower.machine.TileManager;
import com.eloraam.redpower.machine.TileRegulator;
import com.eloraam.redpower.machine.TileRelay;
import com.eloraam.redpower.machine.TileRetriever;
import com.eloraam.redpower.machine.TileSorter;
import com.eloraam.redpower.machine.TileWindTurbine;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class MachineProxy implements IGuiHandler {

	public void registerRenderers() {}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
		switch(ID) {
		case 1:
			return new GuiDeploy(player.inventory, (TileDeploy)CoreLib.getGuiTileEntity(world, X, Y, Z, TileDeploy.class));
		case 2:
			return new GuiFilter(player.inventory, (TileFilter)CoreLib.getGuiTileEntity(world, X, Y, Z, TileFilter.class));
		case 3:
			return new GuiBlueFurnace(player.inventory, (TileBlueFurnace)CoreLib.getGuiTileEntity(world, X, Y, Z, TileBlueFurnace.class));
		case 4:
			return new GuiBufferChest(player.inventory, (TileBufferChest)CoreLib.getGuiTileEntity(world, X, Y, Z, TileBufferChest.class));
		case 5:
			return new GuiSorter(player.inventory, (TileSorter)CoreLib.getGuiTileEntity(world, X, Y, Z, TileSorter.class));
		case 6:
			return new GuiItemDetect(player.inventory, (TileItemDetect)CoreLib.getGuiTileEntity(world, X, Y, Z, TileItemDetect.class));
		case 7:
			return new GuiRetriever(player.inventory, (TileRetriever)CoreLib.getGuiTileEntity(world, X, Y, Z, TileRetriever.class));
		case 8:
			return new GuiBatteryBox(player.inventory, (TileBatteryBox)CoreLib.getGuiTileEntity(world, X, Y, Z, TileBatteryBox.class));
		case 9:
			return new GuiRegulator(player.inventory, (TileRegulator)CoreLib.getGuiTileEntity(world, X, Y, Z, TileRegulator.class));
		case 10:
			return new GuiBlueAlloyFurnace(player.inventory, (TileBlueAlloyFurnace)CoreLib.getGuiTileEntity(world, X, Y, Z, TileBlueAlloyFurnace.class));
		case 11:
			return new GuiAssemble(player.inventory, (TileAssemble)CoreLib.getGuiTileEntity(world, X, Y, Z, TileAssemble.class));
		case 12:
			return new GuiEject(player.inventory, (TileEjectBase)CoreLib.getGuiTileEntity(world, X, Y, Z, TileEjectBase.class));
		case 13:
			return new GuiEject(player.inventory, (TileEjectBase)CoreLib.getGuiTileEntity(world, X, Y, Z, TileRelay.class));
		case 14:
			return new GuiChargingBench(player.inventory, (TileChargingBench)CoreLib.getGuiTileEntity(world, X, Y, Z, TileChargingBench.class));
		case 15:
			return new GuiWindTurbine(player.inventory, (TileWindTurbine)CoreLib.getGuiTileEntity(world, X, Y, Z, TileWindTurbine.class));
		case 16:
			return new GuiManager(player.inventory, (TileManager)CoreLib.getGuiTileEntity(world, X, Y, Z, TileManager.class));
		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
		switch(ID) {
		case 1:
			return new ContainerDeploy(player.inventory, (TileDeploy)CoreLib.getTileEntity(world, X, Y, Z, TileDeploy.class));
		case 2:
			return new ContainerFilter(player.inventory, (TileFilter)CoreLib.getTileEntity(world, X, Y, Z, TileFilter.class));
		case 3:
			return new ContainerBlueFurnace(player.inventory, (TileBlueFurnace)CoreLib.getTileEntity(world, X, Y, Z, TileBlueFurnace.class));
		case 4:
			return new ContainerBufferChest(player.inventory, (TileBufferChest)CoreLib.getTileEntity(world, X, Y, Z, TileBufferChest.class));
		case 5:
			return new ContainerSorter(player.inventory, (TileSorter)CoreLib.getTileEntity(world, X, Y, Z, TileSorter.class));
		case 6:
			return new ContainerItemDetect(player.inventory, (TileItemDetect)CoreLib.getTileEntity(world, X, Y, Z, TileItemDetect.class));
		case 7:
			return new ContainerRetriever(player.inventory, (TileRetriever)CoreLib.getTileEntity(world, X, Y, Z, TileRetriever.class));
		case 8:
			return new ContainerBatteryBox(player.inventory, (TileBatteryBox)CoreLib.getTileEntity(world, X, Y, Z, TileBatteryBox.class));
		case 9:
			return new ContainerRegulator(player.inventory, (TileRegulator)CoreLib.getTileEntity(world, X, Y, Z, TileRegulator.class));
		case 10:
			return new ContainerBlueAlloyFurnace(player.inventory, (TileBlueAlloyFurnace)CoreLib.getTileEntity(world, X, Y, Z, TileBlueAlloyFurnace.class));
		case 11:
			return new ContainerAssemble(player.inventory, (TileAssemble)CoreLib.getTileEntity(world, X, Y, Z, TileAssemble.class));
		case 12:
			return new ContainerEject(player.inventory, (TileEjectBase)CoreLib.getTileEntity(world, X, Y, Z, TileEjectBase.class));
		case 13:
			return new ContainerEject(player.inventory, (TileEjectBase)CoreLib.getTileEntity(world, X, Y, Z, TileRelay.class));
		case 14:
			return new ContainerChargingBench(player.inventory, (TileChargingBench)CoreLib.getTileEntity(world, X, Y, Z, TileChargingBench.class));
		case 15:
			return new ContainerWindTurbine(player.inventory, (TileWindTurbine)CoreLib.getTileEntity(world, X, Y, Z, TileWindTurbine.class));
		case 16:
			return new ContainerManager(player.inventory, (TileManager)CoreLib.getTileEntity(world, X, Y, Z, TileManager.class));
		default:
			return null;
		}
	}
}
