package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileThermopile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileThermopile extends TileExtended implements IBluePowerConnectable {
	
	BluePowerConductor cond = new BluePowerConductor() {
		@Override
		public TileEntity getParent() {
			return TileThermopile.this;
		}
		
		@Override
		public double getInvCap() {
			return 4.0D;
		}
	};
	public int tempHot = 0;
	public int tempCold = 0;
	public int ticks = 0;
	public int ConMask = -1;
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 64;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	@Override
	public int getExtendedID() {
		return 11;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
	}
	
	private void updateTemps() {
		int up = 0;
		int down = 0;
		
		int i;
		WorldCoord wc;
		Block bid;
		for (i = 0; i < 6; ++i) {
			wc = new WorldCoord(this);
			wc.step(i);
			bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
			if (super.worldObj.isAirBlock(wc.x, wc.y, wc.z)) {
				if (super.worldObj.provider.isHellWorld) {
					++up;
				} else {
					++down;
				}
			} else if (bid == Blocks.snow) {
				down += 100;
			} else if (bid == Blocks.ice) {
				down += 100;
			} else if (bid == Blocks.snow_layer) {
				down += 50;
			} else if (bid == Blocks.torch) {
				up += 5;
			} else if (bid == Blocks.lit_pumpkin) {
				up += 3;
			} else if (bid != Blocks.flowing_water
					&& bid != Blocks.water) {
				if (bid != Blocks.flowing_lava
						&& bid != Blocks.lava) {
					if (bid == Blocks.fire) {
						up += 25;
					}
				} else {
					up += 100;
				}
			} else {
				down += 25;
			}
		}
		
		if (this.tempHot >= 100 && this.tempCold >= 200) {
			for (i = 0; i < 6; ++i) {
				wc = new WorldCoord(this);
				wc.step(i);
				bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
				if ((bid == Blocks.flowing_lava || bid == Blocks.lava) && super.worldObj.rand.nextInt(300) == 0) {
					int md = super.worldObj.getBlockMetadata(wc.x, wc.y, wc.z);
					super.worldObj.setBlock(wc.x, wc.y, wc.z, md == 0 ? Blocks.obsidian : RedPowerWorld.blockStone, md > 0 ? 1 : 0, 3);
					break;
				}
			}
		}
		
		if (this.tempHot >= 100) {
			for (i = 0; i < 6; ++i) {
				if (super.worldObj.rand.nextInt(300) == 0) {
					wc = new WorldCoord(this);
					wc.step(i);
					bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
					if (bid == Blocks.snow_layer) {
						super.worldObj.setBlockToAir(wc.x, wc.y, wc.z);
						break;
					}
					
					if (bid == Blocks.ice || bid == Blocks.snow) {
						super.worldObj.setBlock(wc.x, wc.y, wc.z, super.worldObj.provider.isHellWorld ? Blocks.air : Blocks.flowing_water, 0, 3);
						break;
					}
				}
			}
		}
		
		this.tempHot = up;
		this.tempCold = down;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.cond.getVoltage() <= 100.0D) {
				++this.ticks;
				if (this.ticks > 20) {
					this.ticks = 0;
					this.updateTemps();
				}
				
				int t = Math.min(this.tempHot, this.tempCold);
				this.cond.applyDirect(0.005D * t);
			}
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cond.readFromNBT(tag);
		this.tempHot = tag.getShort("hot");
		this.tempCold = tag.getShort("cold");
		this.ticks = tag.getByte("ticks");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.cond.writeToNBT(tag);
		tag.setShort("hot", (short) this.tempHot);
		tag.setShort("cold", (short) this.tempCold);
		tag.setByte("ticks", (byte) this.ticks);
	}
}
