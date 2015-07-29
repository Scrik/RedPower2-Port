package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.WorldCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCustomLeaves extends BlockLeaves {
	
	private String opaque;
	private IIcon opaqueIcon;
	private String transparent;
	private IIcon transparentIcon;
	
	public BlockCustomLeaves(String opaque, String transparent) {
		super();
		this.opaque=opaque;
		this.transparent=transparent;
		this.setTickRandomly(true);
		this.setHardness(0.2F);
		this.setStepSound(soundTypeGrass);
		this.setLightOpacity(1);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister registerer) {
        this.opaqueIcon = registerer.registerIcon(opaque);
        this.transparentIcon = registerer.registerIcon(transparent);
    }
	
	@Override
	public boolean isOpaqueCube() {
		super.field_150121_P = !Blocks.leaves.isOpaqueCube();
		return !super.field_150121_P; //MAYBE INVERTED
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		super.field_150121_P = !Blocks.leaves.isOpaqueCube();
		return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
	}
	
	@Override
	public IIcon getIcon(int i, int j) {
		super.field_150121_P = !Blocks.leaves.isOpaqueCube();
		return super.field_150121_P ? this.transparentIcon : this.opaqueIcon;
	}
	
	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		updateLeaves(world, i, j, k, 1);
	}
	
	public static void updateLeaves(World world, int i, int j, int k, int r) {
		if (world.checkChunksExist(i - r - 1, j - r - 1, k - r - 1, i + r + 1,
				j + r + 1, k + r + 1)) {
			for (int x = -r; x <= r; ++x) {
				for (int y = -r; y <= r; ++y) {
					for (int z = -r; z <= r; ++z) {
						if (world.getBlock(i + x, j + y, k + z) == RedPowerWorld.blockLeaves) {
							int md = world.getBlockMetadata(i + x, j + y, k + z);
							world.setBlock(i + x, j + y, k + z, world.getBlock(i + x, j + y, k + z), md | 8, 3);
						}
					}
				}
			}	
		}
	}
	
	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!CoreLib.isClient(world)) {
			int md = world.getBlockMetadata(i, j, k);
			if ((md & 8) != 0 && (md & 4) <= 0) {
				HashMap<WorldCoord, Integer> wch = new HashMap<WorldCoord, Integer>();
				LinkedList<WorldCoord> fifo = new LinkedList<WorldCoord>();
				WorldCoord wc = new WorldCoord(i, j, k);
				WorldCoord wcp = wc.copy();
				fifo.addLast(wc);
				wch.put(wc, Integer.valueOf(4));
				
				while (fifo.size() > 0) {
					wc = (WorldCoord) fifo.removeFirst();
					Integer stp = (Integer) wch.get(wc);
					if (stp != null) {
						for (int n = 0; n < 6; ++n) {
							wcp.set(wc);
							wcp.step(n);
							if (!wch.containsKey(wcp)) {
								Block block = world.getBlock(wcp.x, wcp.y, wcp.z);
								if (block == RedPowerWorld.blockLogs) {
									world.setBlock(i, j, k, block, md & -9, 3);
									return;
								}
								if (stp.intValue() != 0 && block == this) {
									wch.put(wcp, Integer.valueOf(stp.intValue() - 1));
									fifo.addLast(wcp);
								}
							}
						}
					}
				}
				this.dropBlockAsItem(world, i, j, k, md, 0);
				world.setBlockToAir(i, j, k);
			}
		}
	}
	
	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(RedPowerWorld.blockPlants);
	}
	
	@Override
	public int quantityDropped(int i, int fortune, Random random) {
		return random.nextInt(20) != 0 ? 0 : 1;
	}
	
	@Override
	public int damageDropped(int i) {
		return 1;
	}
	
	@Override
	public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public String[] func_150125_e() {
		return new String[]{this.getUnlocalizedName()};
	}
	
	/*public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}*/
}
