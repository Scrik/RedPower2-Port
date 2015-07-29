package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BlockExtended;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemExtended extends ItemBlock {
	
	HashMap<Integer, String> names = new HashMap<Integer, String>();
	ArrayList<Integer> valid = new ArrayList<Integer>();
	
	public ItemExtended(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	public void setMetaName(int dmg, String name) {
		this.names.put(Integer.valueOf(dmg), name);
		this.valid.add(Integer.valueOf(dmg));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		String tr = (String) this.names.get(Integer.valueOf(ist.getItemDamage()));
		/*if (tr == null) {
			throw new IndexOutOfBoundsException();*/
		if (tr != null){
			return tr;
		}
		return "noname";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		Iterator<Integer> iter = this.valid.iterator();
		while (iter.hasNext()) {
			int i = iter.next();
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	public void placeNoise(World world, int i, int j, int k, Block block) {
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!world.setBlock(x, y, z, Block.getBlockFromItem(this), metadata, 3)) {
			return false;
		} else {
			if (world.getBlock(x, y, z) == Block.getBlockFromItem(this)) {
				BlockExtended bex = (BlockExtended) Block.getBlockFromItem(this);
				bex.onBlockPlacedBy(world, x, y, z/*, side*/, player, stack);
				this.placeNoise(world, x, y, z, Block.getBlockFromItem(this));
			}
			return true;
		}
	}
}
