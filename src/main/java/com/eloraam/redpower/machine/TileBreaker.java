package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileMachine;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class TileBreaker extends TileMachine implements ITubeConnectable,
		IFrameLink, IConnectable {
	
	TubeBuffer buffer = new TubeBuffer();
	
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
	public int getTubeConnectableSides() {
		return 1 << super.Rotation;
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
			this.scheduleTick(5);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return side == super.Rotation && state == 2;
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		int cm = this.getConnectableMask();
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, cm, cm >> 24)) {
			if (!super.Powered) {
				super.Powered = true;
				this.markDirty();
				if (!super.Active) {
					WorldCoord wc = new WorldCoord(super.xCoord, super.yCoord, super.zCoord);
					wc.step(super.Rotation ^ 1);
					Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
					if (bid != Blocks.air && bid.getBlockHardness(super.worldObj, wc.x, wc.y, wc.z) != -1.0F && bid != Blocks.bedrock) {
						if (bl.getBlockHardness(super.worldObj, wc.x, wc.y, wc.z) >= 0.0F) {
							super.Active = true;
							this.updateBlock();
							int md = super.worldObj.getBlockMetadata(wc.x, wc.y, wc.z);
							this.buffer.addAll(bl.getDrops(super.worldObj, wc.x, wc.y, wc.z, md, 0));
							super.worldObj.setBlockToAir(wc.x, wc.y, wc.z);
							this.drainBuffer();
							if (!this.buffer.isEmpty()) {
								this.scheduleTick(5);
							}
						}
					}
				}
			}
		} else {
			if (super.Active && !this.isTickScheduled()) {
				this.scheduleTick(5);
			}
			
			if (super.Powered) {
				super.Powered = false;
			}
		}
	}
	
	public void drainBuffer() {
		while (true) {
			if (!this.buffer.isEmpty()) {
				TubeItem ti = this.buffer.getLast();
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
	public void onBlockRemoval() {
		this.buffer.onRemove(this);
	}
	
	@Override
	public void onTileTick() {
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
	
	@Override
	public int getExtendedID() {
		return 1;
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
