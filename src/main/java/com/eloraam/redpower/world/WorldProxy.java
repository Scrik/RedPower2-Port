package com.eloraam.redpower.world;

import com.eloraam.redpower.world.ContainerSeedBag;
import com.eloraam.redpower.world.GuiSeedBag;
import com.eloraam.redpower.world.ItemSeedBag;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.World;

public class WorldProxy implements IGuiHandler {
	
	public void registerRenderers() {
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,int x, int y, int z) {
		switch (ID) {
			case 1:
				return new GuiSeedBag(player.inventory, new InventoryBasic("", true, 9));
			default:
				return null;
		}
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
			case 1:
				return new ContainerSeedBag(player.inventory, ItemSeedBag.getBagInventory(player.inventory .getCurrentItem()), player.inventory.getCurrentItem());
			default:
				return null;
		}
	}
}
