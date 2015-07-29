package com.eloraam.redpower.world;

import java.util.HashSet;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class ItemSickle extends ItemTool {
	
	public int cropRadius = 2;
	public int leafRadius = 1;
	
	public ItemSickle(ToolMaterial mat) {
		super(3.0f, mat, new HashSet<Block>());
		this.setMaxStackSize(1);
	}
	
	@Override
	public float func_150893_a(ItemStack ist, Block bl) {
		return bl instanceof BlockLeaves ? this.efficiencyOnProperMaterial : super.func_150893_a(ist, bl);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack ist, World world, Block iblock, int i, int j, int k, EntityLivingBase entity) {
		boolean used = false;
		if (!(entity instanceof EntityPlayer)) {
			return false;
		} else {
			EntityPlayer player = (EntityPlayer) entity;
			Block block;
			int md;
			int q;
			int r;
			if (iblock != null && iblock.isLeaves(world, i, j, k)) {
				for (q = -this.leafRadius; q <= this.leafRadius; ++q) {
					for (r = -this.leafRadius; r <= this.leafRadius; ++r) {
						for (int s = -this.leafRadius; s <= this.leafRadius; ++s) {
							block = world.getBlock(i + q, j + r, k + s);
							md = world.getBlockMetadata(i + q, j + r, k + s);
							iblock = block;
							if (iblock != null && iblock.isLeaves(world, i + q, j + r, k + s)) {
								if (iblock.canHarvestBlock(player, md)) {
									iblock.harvestBlock(world, player, i + q,
											j + r, k + s, md);
								}
								
								world.setBlockToAir(i + q, j + r, k + s);
								used = true;
							}
						}
					}
				}
				
				if (used) {
					ist.damageItem(1, entity);
				}
				
				return used;
			} else {
				for (q = -this.cropRadius; q <= this.cropRadius; ++q) {
					for (r = -this.cropRadius; r <= this.cropRadius; ++r) {
						block = world.getBlock(i + q, j, k + r);
						md = world.getBlockMetadata(i + q, j, k + r);
						if (block.getMaterial() != Material.air) { //TODO: Modify this
							iblock = block;
							if (iblock != Blocks.waterlily && iblock instanceof BlockFlower) {
								if (iblock.canHarvestBlock(player, md)) {
									iblock.harvestBlock(world, player, i + q, j, k + r, md);
								}
								world.setBlockToAir(i + q, j, k + r);
								used = true;
							}
						}
					}
				}
				if (used) {
					ist.damageItem(1, entity);
				}
				return used;
			}
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
		return this.toolMaterial == RedPowerWorld.toolMaterialRuby
				&& ist2.isItemEqual(RedPowerBase.itemRuby) ? true : (this.toolMaterial == RedPowerWorld.toolMaterialSapphire
				&& ist2.isItemEqual(RedPowerBase.itemSapphire) ? true : (this.toolMaterial == RedPowerWorld.toolMaterialGreenSapphire
				&& ist2.isItemEqual(RedPowerBase.itemGreenSapphire) ? true : super.getIsRepairable(ist1, ist2)));
	}
	
	@Override
	public int getItemEnchantability() {
		return 20;
	}
}
