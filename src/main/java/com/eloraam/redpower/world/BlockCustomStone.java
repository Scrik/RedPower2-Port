package com.eloraam.redpower.world;

import com.eloraam.redpower.core.IBlockHardness;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockCustomStone extends Block implements IBlockHardness {
	
	private String[] textures = new String[16];
	private IIcon[] icons = new IIcon[16];
	
	public BlockCustomStone() {
		super(Material.rock);
		this.setHardness(3.0F);
		this.setResistance(10.0F);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for(int i = 0; i < this.textures.length; i ++) {
        	if(this.textures[i] != null && !this.textures[i].trim().isEmpty()) {
        		this.icons[i] = register.registerIcon(this.textures[i]);
        	} else {
        		this.icons[i] = null;
        	}
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icons[meta];
    }
	
	public BlockCustomStone setBlockTexture(int meta, String textureName) {
		this.textures[meta] = textureName;
		return this;
	}
	
	@Override
	public float getPrototypicalHardness(int md) {
		switch (md) {
			case 0:
				return 1.0F;
			case 1:
				return 2.5F;
			case 2:
				return 1.0F;
			case 3:
				return 2.5F;
			case 4:
				return 2.5F;
			case 5:
				return 2.5F;
			case 6:
				return 2.5F;
			default:
				return 3.0F;
		}
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		int md = world.getBlockMetadata(x, y, z);
		return this.getPrototypicalHardness(md);
	}
	
	@Override
	public float getExplosionResistance(Entity exploder, World world, int X, int Y, int Z, double srcX, double srcY, double srcZ) {
		int md = world.getBlockMetadata(X, Y, Z);
		switch (md) {
			case 1:
			case 3:
			case 4:
			case 5:
			case 6:
				return 12.0F;
			case 2:
			default:
				return 6.0F;
		}
	}
	
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return 16 + j;
	}
	
	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	@Override
	public int damageDropped(int i) {
		return i == 1 ? 3 : (i == 6 ? 3 : i);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i <= 6; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
    }
}
