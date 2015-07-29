package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.lighting.TileShapedLamp;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;

public class BlockShapedLamp extends BlockExtended {
	
	public BlockShapedLamp() {
		super(CoreLib.materialRedpower);
		this.setHardness(1.0F);
		this.setCreativeTab(RedPowerLighting.tabLamp);
	}
	
	@Override
	public boolean canRenderInPass(int n) {
		return true;
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
	public boolean isACube() {
		return false;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	
	@Override
	public int getLightValue(IBlockAccess iba, int i, int j, int k) {
		TileShapedLamp taf = (TileShapedLamp) CoreLib.getTileEntity(iba, i, j, k, TileShapedLamp.class);
		return taf == null ? 0 : taf.getLightValue();
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		TileShapedLamp tsl = (TileShapedLamp) CoreLib.getTileEntity(iba, x, y, z, TileShapedLamp.class);
		if (tsl == null) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			AxisAlignedBB aabb = tsl.getCollisionBoundingBox();
			this.setBlockBounds((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
		}
	}
}
