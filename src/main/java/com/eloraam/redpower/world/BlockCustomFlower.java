package com.eloraam.redpower.world;

import com.eloraam.redpower.world.WorldGenRubberTree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockCustomFlower extends BlockFlower {
	
	public String[] names = new String[2];
	public IIcon[] icons = new IIcon[2];
	
	public BlockCustomFlower(String... names) {
		super(0);
		this.names=names;
		this.setHardness(0.0F);
		this.setStepSound(soundTypeGrass);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registerer) {
		for(int i = 0; i < 2; i ++) {
			this.icons[i] = registerer.registerIcon(this.names[i]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return icons[meta >= 1 ? 1 : 0];
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		int md = world.getBlockMetadata(i, j, k);
		if (md == 1 || md == 2) {
			if (world.getBlockLightValue(i, j + 1, k) >= 9
					&& random.nextInt(300) == 0) {
				if (md == 1) {
					Chunk chunk = new Chunk(world, i >> 4, k >> 4);
					chunk.setBlockMetadata(i, j, k, 2);
				} else if (md == 2) {
					this.growTree(world, i, j, k);
				}
			}
		}
	}
	
	public boolean growTree(World world, int i, int j, int k) {
		world.setBlockToAir(i, j, k);
		WorldGenRubberTree wg = new WorldGenRubberTree();
		if (!wg.generate(world, world.rand, i, j, k)) {
			world.setBlock(i, j, k, this, 1, 3);
			return false;
		}
		return true;
	}
	
	@Override
	public int damageDropped(int i) {
		return i == 2 ? 1 : i;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}
}
