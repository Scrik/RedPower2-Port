package com.eloraam.redpower.control;

import com.eloraam.redpower.control.TileDiskDrive;
import com.eloraam.redpower.core.CoreLib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemDisk extends Item {
	
	private IIcon emptyIcon;
	private IIcon forthIcon;
	private IIcon forthExtIcon;
	
	public ItemDisk() {
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		this.emptyIcon = reg.registerIcon("rpcontrol:itemDisk");
		this.forthIcon = reg.registerIcon("rpcontrol:itemDiskForth");
		this.forthExtIcon = reg.registerIcon("rpcontrol:itemDiskForthExtended");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		switch (itemstack.getItemDamage()) {
			case 0:
				return "item.disk";
			case 1:
				return "item.disk.forth";
			case 2:
				return "item.disk.forthxp";
			default:
				return null;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack ist) {
		return ist.stackTagCompound == null ? super
				.getItemStackDisplayName(ist) : (!ist.stackTagCompound
				.hasKey("label") ? super.getItemStackDisplayName(ist) : ist.stackTagCompound
				.getString("label"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack ist) {
		return ist.getItemDamage() >= 1 ? EnumRarity.uncommon : EnumRarity.common;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		if (CoreLib.isClient(world)) {
			return false;
		} else {
			TileDiskDrive tdd = (TileDiskDrive) CoreLib.getTileEntity(world, i, j, k, TileDiskDrive.class);
			if (tdd == null) {
				return false;
			} else if (tdd.setDisk(ist.copy())) {
				ist.stackSize = 0;
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean getShareTag() {
		return true;
	}
	
	@Override
	public IIcon getIconFromDamage(int dmg) {
		switch(dmg) {
			default:
				return this.emptyIcon;
			case 1:
				return this.forthIcon;
			case 2: 
				return this.forthExtIcon;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List itemList) {
		for (int i = 0; i <= 1; ++i) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}
}
