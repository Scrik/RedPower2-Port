package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCustomOre extends Block {
	
	private IIcon[] icons = new IIcon[8];
	
	public BlockCustomOre() {
		super(Material.rock);
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setBlockName("rpores");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister register) {
		for(int i = 0; i < this.icons.length; i++) {
			this.icons[i] = register.registerIcon("rpworld:blockOre"+i);
		}
    }
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return 3.0F;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[meta];
	}
	
	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return meta >= 3 && meta != 7 ? Item.getItemFromBlock(this) : RedPowerBase.itemResource;
	}
	
	@Override
	public int quantityDropped(int i, int fortune, Random random) {
		if (i == 7) {
			return 4 + random.nextInt(2) + random.nextInt(fortune + 1);
		} else if (i < 3) {
			int b = random.nextInt(fortune + 2) - 1;
			if (b < 0) {
				b = 0;
			}
			
			return b + 1;
		} else {
			return 1;
		}
	}
	
	@Override
	public int damageDropped(int i) {
		return i == 7 ? 6 : i;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i <= 7; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int md, float chance, int fortune) {
		super.dropBlockAsItemWithChance(world, x, y, z, md, chance, fortune);
		byte min = 0;
		byte max = 0;
		switch (md) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				min = 3;
				max = 7;
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			default:
				break;
			case 7:
				min = 1;
				max = 5;
				break;
		}
		if (max > 0) {
			this.dropXpOnBlockBreak(world, x, y, z,
					MathHelper.getRandomIntegerInRange(world.rand, min, max));
		}
	}
}
