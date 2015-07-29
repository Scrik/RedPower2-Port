package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemParts extends Item {
	
	HashMap<Integer, String> names = new HashMap<Integer, String>();
	HashMap<Integer, IIcon> icons = new HashMap<Integer, IIcon>();
	HashMap<Integer, String> iconstrings = new HashMap<Integer, String>();
	ArrayList<Integer> valid = new ArrayList<Integer>();
	
	public ItemParts() {
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public void addItem(int dmg, String icon, String name) {
		this.iconstrings.put(dmg, icon);
		this.names.put(dmg, name);
		this.valid.add(dmg);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer) {
        for(int i = 0; i < this.iconstrings.size(); i++) {
        	if(this.iconstrings.get(i) != null && !this.iconstrings.get(i).trim().isEmpty()) {
        		this.icons.put(i, registerer.registerIcon(this.iconstrings.get(i)));
        	} else {
        		this.icons.put(i, null);
        	}
        }
    }
	
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		String tr = (String) this.names.get(ist.getItemDamage());
		if (tr == null) {
			throw new IndexOutOfBoundsException();
		} else {
			return tr;
		}
	}
	
	@Override
	public IIcon getIconFromDamage(int i) {
		if(i >= this.icons.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return this.icons.get(i);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for(int i = 0; i < this.valid.size(); i ++) {
			list.add(new ItemStack(this, 1, i));
		}
	}
}
