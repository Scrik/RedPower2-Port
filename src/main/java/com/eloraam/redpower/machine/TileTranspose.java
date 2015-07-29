package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileMachine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTranspose extends TileMachine implements ITubeConnectable {
	
	TubeBuffer buffer = new TubeBuffer();
	
	@Override
	public int getTubeConnectableSides() {
		return 3 << (super.Rotation & -2);
	}
	
	@Override
	public int getTubeConClass() {
		return 0;
	}
	
	@Override
	public boolean canRouteItems() {
		return false;
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side == super.Rotation && state == 2) {
			this.buffer.addBounce(ti);
			super.Active = true;
			this.updateBlock();
			this.scheduleTick(5);
			return true;
		} else if (side == (super.Rotation ^ 1) && state == 1) {
			if (super.Powered) {
				return false;
			} else if (!this.buffer.isEmpty()) {
				return false;
			} else {
				this.addToBuffer(ti.item);
				super.Active = true;
				this.updateBlock();
				this.scheduleTick(5);
				this.drainBuffer();
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return side == super.Rotation && state == 2 ? true : (side == (super.Rotation ^ 1)
				&& state == 1 ? this.buffer.isEmpty() && !super.Powered : false);
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
	}
	
	protected void addToBuffer(ItemStack ist) {
		this.buffer.addNew(ist);
	}
	
	public boolean canSuck(int i, int j, int k) {
		if (super.worldObj.getBlock(i, j, k).isSideSolid(this.worldObj, i, j, k, ForgeDirection.getOrientation(super.Rotation))) {
			return false;
		} else {
			TileEntity te = super.worldObj.getTileEntity(i, j, k);
			return te == null ? true : !(te instanceof IInventory)
					&& !(te instanceof ITubeConnectable);
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63)) {
			if (!super.Powered) {
				super.Powered = true;
				this.markDirty();
				if (!super.Active) {
					super.Active = true;
					WorldCoord wc = new WorldCoord(super.xCoord, super.yCoord, super.zCoord);
					wc.step(super.Rotation ^ 1);
					if (this.canSuck(wc.x, wc.y, wc.z)) {
						this.doSuck();
						this.updateBlock();
					} else {
						if (this.handleExtract(wc)) {
							this.updateBlock();
						}
						
					}
				}
			}
		} else {
			if (super.Active && !this.isTickScheduled()) {
				this.scheduleTick(5);
			}
			
			super.Powered = false;
			this.markDirty();
		}
	}
	
	protected IInventory getConnectedInventory(boolean push) {
		WorldCoord pos = new WorldCoord(this);
		pos.step(super.Rotation ^ 1);
		return MachineLib.getSideInventory(super.worldObj, pos, super.Rotation, push);
	}
	
	protected boolean handleExtract(WorldCoord wc) {
		IInventory inv = MachineLib.getInventory(super.worldObj, wc);
		if (inv == null) {
			return false;
		} else {
			int[] slots = new int[]{};
			if (inv instanceof ISidedInventory) {
				ISidedInventory isi = (ISidedInventory) inv;
				slots = isi.getAccessibleSlotsFromSide(super.Rotation);
			}
			
			return this.handleExtract(inv, slots);
		}
	}
	
	protected boolean handleExtract(IInventory inv, int[] slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				this.addToBuffer(inv.decrStackSize(n, 1));
				this.drainBuffer();
				return true;
			}
		}
		return false;
	}
	
	protected boolean handleExtract(IInventory inv, List<Integer> slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				this.addToBuffer(inv.decrStackSize(n, 1));
				this.drainBuffer();
				return true;
			}
		}
		return false;
	}
	
	protected AxisAlignedBB getSizeBox(double bw, double bf, double bb) {
		double fx = super.xCoord + 0.5D;
		double fy = super.yCoord + 0.5D;
		double fz = super.zCoord + 0.5D;
		switch (super.Rotation) {
			case 0:
				return AxisAlignedBB.getBoundingBox(fx - bw, super.yCoord - bb,
						fz - bw, fx + bw, super.yCoord + bf, fz + bw);
			case 1:
				return AxisAlignedBB.getBoundingBox(fx - bw, super.yCoord + 1
						- bf, fz - bw, fx + bw, super.yCoord + 1 + bb, fz + bw);
			case 2:
				return AxisAlignedBB.getBoundingBox(fx - bw, fy - bw,
						super.zCoord - bb, fx + bw, fy + bw, super.zCoord + bf);
			case 3:
				return AxisAlignedBB.getBoundingBox(fx - bw, fy - bw,
						super.zCoord + 1 - bf, fx + bw, fy + bw, super.zCoord
								+ 1 + bb);
			case 4:
				return AxisAlignedBB.getBoundingBox(super.xCoord - bb, fy - bw,
						fz - bw, super.xCoord + bf, fy + bw, fz + bw);
			default:
				return AxisAlignedBB.getBoundingBox(super.xCoord + 1 - bf, fy
						- bw, fz - bw, super.xCoord + 1 + bb, fy + bw, fz + bw);
		}
	}
	
	protected void doSuck() {
		this.suckEntities(this.getSizeBox(1.55D, 3.05D, -0.95D));
	}
	
	protected boolean suckFilter(ItemStack ist) {
		return true;
	}
	
	protected int suckEntity(Entity ent) {
		if (ent instanceof EntityItem) {
			EntityItem em1 = (EntityItem) ent;
			ItemStack ist = em1.getEntityItem();
			if (ist.stackSize != 0 && !em1.isDead) {
				if (!this.suckFilter(ist)) {
					return 0;
				} else {
					this.addToBuffer(ist);
					em1.setDead();
					return 1;
				}
			} else {
				return 0;
			}
		} else {
			if (ent instanceof EntityMinecartContainer) {
				if (super.Active) {
					return 0;
				}
				EntityMinecartContainer em = (EntityMinecartContainer) ent;
				List<Integer> slots = new ArrayList<Integer>(em.getSizeInventory());
				for(int i = 0; i < em.getSizeInventory(); i ++) {
					slots.add(i);
				}
				if (this.handleExtract(em, slots)) {
					return 2;
				}
			}
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void suckEntities(AxisAlignedBB bb) {
		boolean trig = false;
		List<Entity> el = super.worldObj.getEntitiesWithinAABB(Entity.class, bb);
		Iterator<Entity> iter = el.iterator();
		
		while (iter.hasNext()) {
			Entity ent = iter.next();
			int i = this.suckEntity(ent);
			if (i != 0) {
				trig = true;
				if (i == 2) {
					break;
				}
			}
		}
		if (trig) {
			if (!super.Active) {
				super.Active = true;
				this.updateBlock();
			}
			
			this.drainBuffer();
			this.scheduleTick(5);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean stuffCart(ItemStack ist) {
		WorldCoord wc = new WorldCoord(this);
		wc.step(super.Rotation);
		Block bl = super.worldObj.getBlock(wc.x, wc.y, wc.z);
		if (!(bl instanceof BlockRail)) {
			return false;
		} else {
			List<EntityMinecartContainer> el = super.worldObj.getEntitiesWithinAABB(EntityMinecartContainer.class, this.getSizeBox(0.8D, 0.05D, 1.05D));
			Iterator<EntityMinecartContainer> iter = el.iterator();
			
			while (iter.hasNext()) {
				Object ent = iter.next();
				if (ent instanceof EntityMinecart) {
					EntityMinecartContainer em = (EntityMinecartContainer) ent;
					List<Integer> list = new ArrayList<Integer>(em.getSizeInventory());
					for(int i = 0; i < em.getSizeInventory(); i ++) {
						list.add(i);
					}
					if (MachineLib.addToInventoryCore(em, ist, list, true)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public void drainBuffer() {
		while (true) {
			if (!this.buffer.isEmpty()) {
				TubeItem ti = this.buffer.getLast();
				if (this.stuffCart(ti.item)) {
					this.buffer.pop();
					continue;
				}
				
				if (!this.handleItem(ti)) {
					this.buffer.plugged = true;
					return;
				}
				
				this.buffer.pop();
				if (!this.buffer.plugged) {
					continue;
				}
				
				return;
			}
			
			return;
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getSizeBox(0.5D, 0.95D, 0.0D);
	}
	
	@Override
	public void onEntityCollidedWithBlock(Entity ent) {
		if (!CoreLib.isClient(super.worldObj)) {
			if (!super.Powered) {
				if (this.buffer.isEmpty()) {
					this.suckEntities(this.getSizeBox(0.55D, 1.05D, -0.95D));
				}
				
			}
		}
	}
	
	@Override
	public void onBlockRemoval() {
		this.buffer.onRemove(this);
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (!this.buffer.isEmpty()) {
				this.drainBuffer();
				if (!this.buffer.isEmpty()) {
					this.scheduleTick(10);
				} else {
					this.scheduleTick(5);
				}
				
			} else {
				if (!super.Powered) {
					super.Active = false;
					this.updateBlock();
				}
				
			}
		}
	}
	
	@Override
	public int getExtendedID() {
		return 2;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.buffer.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		this.buffer.writeToNBT(nbttagcompound);
	}
}
