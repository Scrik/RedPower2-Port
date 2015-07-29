package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.ItemPartialCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemPaintCan extends ItemPartialCraft {
	
	int color;
	private IIcon[] icons = new IIcon[16];
	
	public ItemPaintCan(int col) {
		this.color = col;
		this.setMaxDamage(15);
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.icons[meta];
    }
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer) {
        for(int i = 0; i < 16; i ++) {
        	this.icons[i] = registerer.registerIcon("rpworld:itemPaintBrush"+i);
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		for (int n = 0; n < 9; ++n) {
			ItemStack isl = player.inventory.getStackInSlot(n);
			if (isl != null && isl.getItem() == RedPowerWorld.itemBrushDry && isl.stackSize == 1) {
				player.inventory.setInventorySlotContents(n, new ItemStack(
						RedPowerWorld.itemBrushPaint[this.color]));
				ist.damageItem(1, player);
				if (ist.stackSize == 0) {
					return new ItemStack(RedPowerWorld.itemPaintCanEmpty);
				}
				return ist;
			}
		}
		return ist;
	}
}
