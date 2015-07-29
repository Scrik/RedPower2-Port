package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IMultiblock;
import com.eloraam.redpower.core.TileMultiblock;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMultiblock extends BlockContainer {
	
	public BlockMultiblock() {
		super(CoreLib.materialRedpower);
	}
	
	@Override
	public int getRenderType() {
		return RedPowerCore.nullBlockModel;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldObj, int metadata) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World worldObj, int metadata) {
		switch (metadata) {
			case 0:
				return new TileMultiblock();
			default:
				return null;
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int md) {
		TileMultiblock tmb = (TileMultiblock) CoreLib.getTileEntity(world, x, y, z, TileMultiblock.class);
		if (tmb != null) {
			IMultiblock imb = (IMultiblock) CoreLib.getTileEntity(world, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class);
			if (imb != null) {
				imb.onMultiRemoval(tmb.relayNum);
			}
		}
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		TileMultiblock tmb = (TileMultiblock) CoreLib.getTileEntity(iba, x, y, z, TileMultiblock.class);
		if (tmb == null) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			IMultiblock imb = (IMultiblock) CoreLib.getTileEntity(iba, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class);
			if (imb != null) {
				AxisAlignedBB aabb = imb.getMultiBounds(tmb.relayNum);
				int xa = tmb.relayX - x;
				int ya = tmb.relayY - y;
				int za = tmb.relayZ - z;
				this.setBlockBounds((float) aabb.minX + xa, (float) aabb.minY + ya, (float) aabb.minZ + za, (float) aabb.maxX + xa, (float) aabb.maxY + ya, (float) aabb.maxZ + za);
			}
		}
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		TileMultiblock tmb = (TileMultiblock) CoreLib.getTileEntity(world, x, y, z, TileMultiblock.class);
		if (tmb == null) {
			return 0.0F;
		} else {
			IMultiblock imb = (IMultiblock) CoreLib.getTileEntity(world, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class);
			return imb == null ? 0.0F : imb.getMultiBlockStrength(tmb.relayNum, player);
		}
	}
}
