package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemCustomSeeds extends Item implements IPlantable {
	
	public ItemCustomSeeds() {
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setUnlocalizedName("seedFlax");
		this.setTextureName("rpworld:itemSeedsFlax");
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		if (l != 1) {
			return false;
		} else {
			Block soil = world.getBlock(i, j, k);
			if (soil == null) {
				return false;
			} else if (soil.canSustainPlant(world, i, j, k, ForgeDirection.UP, this) && world.getBlockMetadata(i, j, k) >= 1 && world.isAirBlock(i, j + 1, k)) {
				world.setBlock(i, j + 1, k, RedPowerWorld.blockCrops, 0, 3);
				--ist.stackSize;
				return true;
			} else {
				return false;
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i <= 0; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Crop;
	}
	
	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return RedPowerWorld.blockCrops;
	}
	
	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return 0;
	}
}
