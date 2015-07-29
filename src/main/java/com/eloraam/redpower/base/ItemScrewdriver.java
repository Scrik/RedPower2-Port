package com.eloraam.redpower.base;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRotatable;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemScrewdriver extends Item {
	
	public ItemScrewdriver() {
		this.setMaxDamage(200);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setUnlocalizedName("screwdriver");
		this.setTextureName("rpbase:itemScrewdriver");
	}
	
	public boolean hitEntity(ItemStack itemstack, EntityLiving entityliving, EntityLiving player) {
		itemstack.damageItem(8, player);
		return true;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		if (CoreLib.isClient(world)) {
			return false;
		} else {
			boolean sec = false;
			if (player != null && player.isSneaking()) {
				sec = true;
			}
			
			Block bid = world.getBlock(i, j, k);
			int md = world.getBlockMetadata(i, j, k);
			if (bid != Blocks.unpowered_repeater && bid != Blocks.powered_repeater) {
				if (bid == Blocks.dispenser) {
					md = md & 3 ^ md >> 2;
					md += 2;
					world.setBlock(i, j, k, bid, md, 3); //TODO: Look on this
					ist.damageItem(1, player);
					return true;
				} else if (bid != Blocks.piston && bid != Blocks.sticky_piston) {
					IRotatable ir = (IRotatable) CoreLib.getTileEntity(world, i, j, k, IRotatable.class);
					if (ir == null) {
						return false;
					} else {
						MovingObjectPosition mop = CoreLib.retraceBlock(world,
								player, i, j, k);
						if (mop == null) {
							return false;
						} else {
							int rm = ir.getPartMaxRotation(mop.subHit, sec);
							if (rm == 0) {
								return false;
							} else {
								int r = ir.getPartRotation(mop.subHit, sec);
								++r;
								if (r > rm) {
									r = 0;
								}
								
								ir.setPartRotation(mop.subHit, sec, r);
								ist.damageItem(1, player);
								return true;
							}
						}
					}
				} else {
					++md;
					if (md > 5) {
						md = 0;
					}
					
					world.setBlock(i, j, k, bid, md, 3);
					ist.damageItem(1, player);
					return true;
				}
			} else {
				world.setBlock(i, j, k, bid, md & 12 | md + 1 & 3, 3);
				ist.damageItem(1, player);
				return true;
			}
		}
	}
	
	public int getDamageVsEntity(Entity entity) {
		return 6;
	}
	
	public String getTextureFile() {
		return "/eloraam/base/items1.png";
	}
}
