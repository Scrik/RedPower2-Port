package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ContainerAdvBench;
import com.eloraam.redpower.base.ContainerAlloyFurnace;
import com.eloraam.redpower.base.ContainerBag;
import com.eloraam.redpower.base.ContainerBusId;
import com.eloraam.redpower.base.GuiAdvBench;
import com.eloraam.redpower.base.GuiAlloyFurnace;
import com.eloraam.redpower.base.GuiBag;
import com.eloraam.redpower.base.GuiBusId;
import com.eloraam.redpower.base.ItemBag;
import com.eloraam.redpower.base.TileAdvBench;
import com.eloraam.redpower.base.TileAlloyFurnace;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedbusConnectable;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BaseProxy implements IGuiHandler {
	
	public void registerRenderers() {
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
			case 1:
				return new GuiAlloyFurnace(player.inventory, (TileAlloyFurnace) CoreLib.getGuiTileEntity(world, x, y, z, TileAlloyFurnace.class));
			case 2:
				return new GuiAdvBench(player.inventory, (TileAdvBench) CoreLib.getGuiTileEntity(world, x, y, z, TileAdvBench.class));
			case 3:
				return new GuiBusId(player.inventory, new IRedbusConnectable.Dummy(), (TileEntity) CoreLib.getGuiTileEntity(world, x, y, z, TileEntity.class));
			case 4:
				return new GuiBag(player.inventory, new InventoryBasic("", true, 27));
			default:
				return null;
		}
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
			case 1:
				return new ContainerAlloyFurnace(player.inventory,
						(TileAlloyFurnace) CoreLib.getTileEntity(world, x, y,
								z, TileAlloyFurnace.class));
			case 2:
				return new ContainerAdvBench(player.inventory,
						(TileAdvBench) CoreLib.getTileEntity(world, x, y, z,
								TileAdvBench.class));
			case 3:
				return new ContainerBusId(player.inventory,
						(IRedbusConnectable) CoreLib.getTileEntity(world, x, y,
								z, IRedbusConnectable.class));
			case 4:
				return new ContainerBag(player.inventory, ItemBag.getBagInventory(player.inventory.getCurrentItem(), player), player.inventory.getCurrentItem());
			default:
				return null;
		}
	}
}
