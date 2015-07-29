package com.eloraam.redpower.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStorage extends Block {
	
	private IIcon[] icons = new IIcon[6];
	
	public BlockStorage() {
		super(Material.iron);
		this.setHardness(5.0F);
		this.setResistance(10.0F);
		this.setStepSound(soundTypeMetal);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister register) {
		for(int i = 0; i < icons.length; i++) {
			this.icons[i] = register.registerIcon("rpworld:blockStorage"+i);
		}
    }
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[meta];
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i <= 5; ++i) {
			list.add(new ItemStack(this, 1, i));
		}	
	}
}
