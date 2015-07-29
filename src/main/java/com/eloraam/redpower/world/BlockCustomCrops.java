package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCustomCrops extends BlockFlower {
	
	private IIcon[] icons = new IIcon[6];
	
	public BlockCustomCrops() {
		super(0);
		this.setHardness(0.0F);
		this.setStepSound(soundTypeGrass);
		this.setTickRandomly(true);
		// this.setRequiresSelfNotify(); TODO: meh..
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if(meta > 6) meta = 6;
		return this.icons[meta];
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
		for(int i = 0; i < this.icons.length; i++) {
			this.icons[i] = register.registerIcon("rpworld:blockFlaxCrop"+i);
		}
    }
	
	protected boolean canThisPlantGrowOnThisBlock(Block bl) {
		return bl == Blocks.farmland;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int i, int j, int k) {
		int md = iba.getBlockMetadata(i, j, k);
		float h = Math.min(1.0F, 0.1F + 0.25F * md);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, h, 1.0F);
	}
	
	@Override
	public int getRenderType() {
		return 6;
	}
	
	@Override
	public Item getItemDropped(int meta, Random random, int j) {
		return null;
	}
	
	public boolean fertilize(World world, int i, int j, int k) {
		Random random = world.rand;
		if (world.getBlockLightValue(i, j + 1, k) < 9) {
			return false;
		} else {
			int md = world.getBlockMetadata(i, j, k);
			if (md != 4 && md != 5) {
				if (world.getBlock(i, j - 1, k) == Blocks.farmland && world.getBlockMetadata(i, j - 1, k) != 0 && world.isAirBlock(i, j + 1, k)) {
					if (random.nextBoolean()) {
						world.setBlockMetadataWithNotify(i, j, k, md + 1, 3);
						if (md == 3) {
							world.setBlock(i, j + 1, k, this, 1, 3);
						}
						return true;
					}
				} else if(world.getBlock(i, j - 2, k) == Blocks.farmland && world.getBlockMetadata(i, j - 2, k) != 0 && world.isAirBlock(i, j + 1, k)) {
					if (random.nextBoolean()) {
						if(md + 1 < 4) {
							world.setBlock(i, j, k, this, md + 1, 3);
							return true;	
						} else if(world.getBlockMetadata(i, j, k) != 5) {
							world.setBlock(i, j, k, this, 5, 3);
							return true;	
						}
						return false;
					}
				}
			} else if(md == 5 || md == 4) {
				if (world.getBlock(i, j - 1, k) == Blocks.farmland && world.getBlockMetadata(i, j - 1, k) != 0 && world.isAirBlock(i, j + 2, k)) {
					if(world.getBlock(i, j + 1, k) == this && world.getBlockMetadata(i, j + 1, k) <= 3) {
						if (random.nextBoolean()) {
							System.out.println("BONE KOKO");
							int mdup = world.getBlockMetadata(i, j + 1, k);
							if(mdup + 1 <= 3) {
								world.setBlock(i, j + 1, k, this, mdup + 1, 3);
								return true;	
							} else if(world.getBlockMetadata(i, j + 1, k) != 5) {
								world.setBlock(i, j + 1, k, this, 5, 3);
								return true;	
							}
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> tr = new ArrayList<ItemStack>();
		int n;
		if (metadata == 4 || metadata == 5) {
			n = 1 + world.rand.nextInt(3) + world.rand.nextInt(1 + fortune);
			while (n-- > 0) {
				tr.add(new ItemStack(Items.string));
			}
		}
		for (n = 0; n < 3 + fortune; ++n) {
			if (metadata == 5) {
				metadata = 4;
			}	
			if (world.rand.nextInt(8) <= metadata) {
				tr.add(new ItemStack(RedPowerWorld.itemSeeds, 1, 0));
			}
		}
		return tr;
    }
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		super.updateTick(world, i, j, k, random);
		if (world.getBlockLightValue(i, j + 1, k) >= 9) {
			int md = world.getBlockMetadata(i, j, k);
			if (md != 4 && md != 5) {
				if (world.getBlock(i, j - 1, k) == Blocks.farmland && world.getBlockMetadata(i, j - 1, k) != 0 && world.isAirBlock(i, j + 1, k)) {
					if (random.nextBoolean()) {
						world.setBlockMetadataWithNotify(i, j, k, md + 1, 3);
						if (md == 3) {
							world.setBlock(i, j + 1, k, this, 1, 3);
						}
					}
				} else if(world.getBlock(i, j - 2, k) == Blocks.farmland && world.getBlockMetadata(i, j - 2, k) != 0 && world.isAirBlock(i, j + 1, k)) {
					if (random.nextBoolean()) {
						if(md + 1 < 4) {
							world.setBlock(i, j, k, this, md + 1, 3);
						} else if(world.getBlockMetadata(i, j, k) != 5) {
							world.setBlock(i, j, k, this, 5, 3);
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean canBlockStay(World world, int i, int j, int k) {
		int md = world.getBlockMetadata(i, j, k);
		return md == 5 ? (world.getBlock(i, j - 1, k) != this ? false : world
				.getBlockMetadata(i, j - 1, k) == 4) : (world.getBlock(i,
				j - 1, k) != Blocks.farmland ? false : (md == 4 ? world.getBlock(i, j + 1, k) == this && world.getBlockMetadata(i, j + 1, k) != 4 : 
					world.isAirBlock(i, j + 1, k)));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List itemList){
		;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(RedPowerWorld.itemSeeds, 1, 0);
    }
}
