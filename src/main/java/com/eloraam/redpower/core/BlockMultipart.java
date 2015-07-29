package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TileMultipart;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockMultipart extends BlockExtended {
	
	public BlockMultipart(Material m) {
		super(m);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
		if (tl == null) {
			world.setBlockToAir(i, j, k);
		} else {
			tl.onBlockNeighborChange(block);
		}
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int i, int j, int k) {
		if (CoreLib.isClient(world)) {
			return true;
		} else {
			MovingObjectPosition pos = CoreLib.retraceBlock(world, player, i, j, k);
			if (pos == null) {
				return false;
			} else if (pos.typeOfHit != MovingObjectType.BLOCK) {
				return false;
			} else {
				TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
				if (tl == null) {
					return false;
				} else {
					tl.onHarvestPart(player, pos.subHit);
					return false;
				}
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float xp, float yp, float zp) {
		MovingObjectPosition pos = CoreLib.retraceBlock(world, player, i, j, k);
		if (pos == null) {
			return false;
		} else if (pos.typeOfHit != MovingObjectType.BLOCK) {
			return false;
		} else {
			TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
			return tl == null ? false : tl.onPartActivateSide(player, pos.subHit, pos.sideHit);
		}
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		MovingObjectPosition pos = CoreLib.retraceBlock(world, player, x, y, z);
		if (pos == null) {
			return 0.0F;
		} else if (pos.typeOfHit != MovingObjectType.BLOCK) {
			return 0.0F;
		} else {
			TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(player.worldObj, x, y, z, TileMultipart.class);
			return tl == null ? 0.0F : tl.getPartStrength(player, pos.subHit);
		}
	}
	
	public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
		TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
		if (tl != null) {
			tl.breakBlock();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB box, List list, Entity ent) {
		TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
		if (tl != null) {
			int pm = tl.getSolidPartsMask();
			
			while (pm > 0) {
				int pt = Integer.numberOfTrailingZeros(pm);
				pm &= ~(1 << pt);
				tl.setPartBounds(this, pt);
				super.addCollisionBoxesToList(world, i, j, k, box, list, ent);
			}
			
		}
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 vec3d, Vec3 vec3d1) {
		TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
		if (tl == null) {
			return null;
		} else {
			int pm = tl.getPartsMask();
			MovingObjectPosition p1 = null;
			int cpt = -1;
			double d1 = 0.0D;
			
			while (pm > 0) {
				int pt = Integer.numberOfTrailingZeros(pm);
				pm &= ~(1 << pt);
				tl.setPartBounds(this, pt);
				MovingObjectPosition p2 = super.collisionRayTrace(world, i, j,
						k, vec3d, vec3d1);
				if (p2 != null) {
					double d2 = p2.hitVec.squareDistanceTo(vec3d);
					if (p1 == null || d2 < d1) {
						d1 = d2;
						p1 = p2;
						cpt = pt;
					}
				}
			}
			
			if (p1 == null) {
				return null;
			} else {
				tl.setPartBounds(this, cpt);
				p1.subHit = cpt;
				return p1;
			}
		}
	}
	
	public static void removeMultipart(World world, int i, int j, int k) {
		world.setBlockToAir(i, j, k);
	}
	
	public static void removeMultipartWithNotify(World world, int i, int j, int k) {
		world.setBlockToAir(i, j, k);
	}
	
	protected MovingObjectPosition traceCurrentBlock(World world, int i, int j, int k, Vec3 src, Vec3 dest) {
		return super.collisionRayTrace(world, i, j, k, src, dest);
	}
	
	
	public void setPartBounds(World world, int i, int j, int k, int part) {
		TileMultipart tl = (TileMultipart) CoreLib.getTileEntity(world, i, j, k, TileMultipart.class);
		if (tl == null) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			tl.setPartBounds(this, part);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void computeCollidingBoxes(World world, int i, int j, int k, AxisAlignedBB box, List list, TileMultipart tl) {
		int pm = tl.getSolidPartsMask();
		while (pm > 0) {
			int pt = Integer.numberOfTrailingZeros(pm);
			pm &= ~(1 << pt);
			tl.setPartBounds(this, pt);
			super.addCollisionBoxesToList(world, i, j, k, box, list, (Entity) null);
		}
	}
	
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		TileMultipart tile = (TileMultipart)CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
		if(tile != null) {
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
			tile.addHarvestContents(drops);
			if(drops.size() >= 1) {
				return drops.get(0);
			}
		}
        return null;
    }
}
