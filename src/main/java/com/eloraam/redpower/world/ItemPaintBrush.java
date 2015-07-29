package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IPaintable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemPaintBrush extends Item {
	
	int color;
	private IIcon[] icons = new IIcon[16];
	
	public ItemPaintBrush(int col) {
		this.color = col;
		this.setMaxStackSize(1);
		this.setMaxDamage(15);
		this.setNoRepair();
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

	private boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
		IPaintable ip = (IPaintable) CoreLib.getTileEntity(world, i, j, k, IPaintable.class);
		if (ip == null) {
			return false;
		} else {
			MovingObjectPosition mop = CoreLib.retraceBlock(world, player, i,
					j, k);
			if (mop == null) {
				return false;
			} else if (!ip.tryPaint(mop.subHit, mop.sideHit, this.color + 1)) {
				return false;
			} else {
				ist.damageItem(1, player);
				if (ist.stackSize == 0) {
					ist = new ItemStack(RedPowerWorld.itemBrushDry);
				}
				return true;
			}
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		return CoreLib.isClient(world) ? false : (player.isSneaking() ? false : this
				.itemUseShared(ist, player, world, i, j, k, l));
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player,
			World world, int i, int j, int k, int l, float xp, float yp,
			float zp) {
		return CoreLib.isClient(world) ? false : (!player.isSneaking() ? false : this
				.itemUseShared(ist, player, world, i, j, k, l));
	}
}
