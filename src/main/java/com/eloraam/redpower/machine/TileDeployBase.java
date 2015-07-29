package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.EntityPlayerFake;
import com.eloraam.redpower.machine.TileMachine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class TileDeployBase extends TileMachine implements IFrameLink, IConnectable {
	
	protected static EntityPlayer fakePlayer = null;
	
	@Override
	public boolean isFrameMoving() {
		return false;
	}
	
	@Override
	public boolean canFrameConnectIn(int dir) {
		return dir != (super.Rotation ^ 1);
	}
	
	@Override
	public boolean canFrameConnectOut(int dir) {
		return false;
	}
	
	@Override
	public WorldCoord getFrameLinkset() {
		return null;
	}
	
	@Override
	public int getConnectableMask() {
		return 1073741823 ^ RedPowerLib.getConDirMask(super.Rotation ^ 1);
	}
	
	@Override
	public int getConnectClass(int side) {
		return 0;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
	}
	
	protected void initPlayer() {
		if (fakePlayer == null) {
			fakePlayer = new EntityPlayerFake(super.worldObj);
		}
		
		double x = super.xCoord + 0.5D;
		double y = super.yCoord - 1.1D;
		double z = super.zCoord + 0.5D;
		float pitch;
		float yaw;
		switch (super.Rotation) {
			case 0:
				pitch = -90.0F;
				yaw = 0.0F;
				y += 0.51D;
				break;
			case 1:
				pitch = 90.0F;
				yaw = 0.0F;
				y -= 0.51D;
				break;
			case 2:
				pitch = 0.0F;
				yaw = 0.0F;
				z += 0.51D;
				break;
			case 3:
				pitch = 0.0F;
				yaw = 180.0F;
				z -= 0.51D;
				break;
			case 4:
				pitch = 0.0F;
				yaw = 270.0F;
				x += 0.51D;
				break;
			default:
				pitch = 0.0F;
				yaw = 90.0F;
				x -= 0.51D;
		}
		
		fakePlayer.worldObj = super.worldObj;
		fakePlayer.setLocationAndAngles(x, y, z, yaw, pitch);
	}
	
	@SuppressWarnings("rawtypes")
	protected static Entity traceEntities(World world, Entity exclude, Vec3 vs, Vec3 vlook) {
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(vs.xCoord, vs.yCoord, vs.zCoord, vs.xCoord, vs.yCoord, vs.zCoord);
		List elist = world.getEntitiesWithinAABBExcludingEntity(exclude,
				aabb.addCoord(vlook.xCoord, vlook.yCoord, vlook.zCoord).expand(1.0D, 1.0D, 1.0D));
		Vec3 v2 = vs.addVector(vlook.xCoord, vlook.yCoord, vlook.zCoord);
		Entity entHit = null;
		double edis = 0.0D;
		
		for (int i = 0; i < elist.size(); ++i) {
			Entity ent = (Entity) elist.get(i);
			if (ent.canBeCollidedWith()) {
				float cbs = ent.getCollisionBorderSize();
				AxisAlignedBB ab2 = ent.boundingBox.expand(cbs, cbs, cbs);
				if (ab2.isVecInside(vs)) {
					entHit = ent;
					edis = 0.0D;
					break;
				}
				MovingObjectPosition mop = ab2.calculateIntercept(vs, v2);
				if (mop != null) {
					double d = vs.distanceTo(mop.hitVec);
					if (d < edis || edis == 0.0D) {
						entHit = ent;
						edis = d;
					}
				}
			}
		}
		
		return entHit;
	}
	
	protected boolean useOnEntity(Entity ent) { //TODO: внимательно протестить
		if (ent.interactFirst(fakePlayer)) {
			return true;
		} else {
			ItemStack ist = fakePlayer.getCurrentEquippedItem();
			if (ist != null && ent instanceof EntityLiving) {
				int iss = ist.stackSize;
				ist.interactWithEntity(fakePlayer, (EntityLivingBase)ent);
				if (ist.stackSize != iss) {
					return true;
				}
			}
			return false;
		}
	}
	
	protected boolean tryUseItemStack(ItemStack ist, int x, int y, int z, int slot) {
		fakePlayer.inventory.currentItem = slot;
		if (ist.getItem() != Items.dye
				&& ist.getItem() != Items.minecart
				&& ist.getItem() != Items.furnace_minecart
				&& ist.getItem() != Items.chest_minecart) {
			if (ist.getItem().onItemUseFirst(ist, fakePlayer, super.worldObj,
					x, y, z, 1, 0.5F, 0.5F, 0.5F)) {
				return true;
			}
			
			if (ist.getItem().onItemUse(ist, fakePlayer, super.worldObj, x,
					y - 1, z, 1, 0.5F, 0.5F, 0.5F)) {
				return true;
			}
		} else if (ist.getItem().onItemUse(ist, fakePlayer, super.worldObj, x,
				y, z, 1, 0.5F, 0.5F, 0.5F)) {
			return true;
		}
		
		int iss = ist.stackSize;
		ItemStack ost = ist.useItemRightClick(super.worldObj, fakePlayer);
		if (ost == ist && (ost == null || ost.stackSize == iss)) {
			Vec3 lv = fakePlayer.getLook(1.0F);
			lv.xCoord *= 2.5D;
			lv.yCoord *= 2.5D;
			lv.zCoord *= 2.5D;
			Vec3 sv = Vec3.createVectorHelper(super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D);
			Entity ent = traceEntities(super.worldObj, fakePlayer, sv, lv);
			return ent != null && this.useOnEntity(ent);
		} else {
			fakePlayer.inventory.setInventorySlotContents(slot, ost);
			return true;
		}
	}
	
	public abstract void enableTowards(WorldCoord var1);
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		int cm = this.getConnectableMask();
		if (!RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, cm, cm >> 24)) {
			if (super.Active) {
				this.scheduleTick(5);
			}
		} else if (!super.Active) {
			super.Active = true;
			this.updateBlock();
			WorldCoord wc = new WorldCoord(this);
			wc.step(super.Rotation ^ 1);
			this.enableTowards(wc);
		}
	}
	
	@Override
	public void onTileTick() {
		super.Active = false;
		this.updateBlock();
	}
}
