package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;
import com.eloraam.redpower.logic.TileLogicAdv;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileLogicAdv extends TileLogic implements IRedPowerWiring {
	
	TileLogicAdv.LogicAdvModule storage = null;
	
	@Override
	public void updateCurrentStrength() {
		this.initStorage();
		this.storage.updateCurrentStrength();
	}
	
	@Override
	public int getCurrentStrength(int cons, int ch) {
		this.initStorage();
		return (this.storage.getPoweringMask(ch) & cons) > 0 ? 255 : -1;
	}
	
	@Override
	public int scanPoweringStrength(int cons, int ch) {
		return 0;
	}
	
	@Override
	public int getConnectionMask() {
		return RedPowerLib.mapRotToCon(15, super.Rotation);
	}
	
	@Override
	public int getExtConnectionMask() {
		return 0;
	}
	
	@Override
	public int getConnectClass(int side) {
		int s = RedPowerLib.mapRotToCon(10, super.Rotation);
		return (s & RedPowerLib.getConDirMask(side)) > 0 ? 18 : 0;
	}
	
	@Override
	public int getExtendedID() {
		return 4;
	}
	
	@Override
	public void initSubType(int st) {
		super.SubId = st;
		this.initStorage();
	}
	
	@SuppressWarnings("rawtypes")
	public TileLogicAdv.LogicAdvModule getLogicStorage(Class cl) {
		if (!cl.isInstance(this.storage)) {
			this.initStorage();
		}
		return this.storage;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					return 1;
			}
		}
		
		return super.getPartMaxRotation(part, sec);
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					return super.Deadmap;
			}
		}
		
		return super.getPartRotation(part, sec);
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					super.Deadmap = rot;
					this.updateBlockChange();
					return;
			}
		}
		
		super.setPartRotation(part, sec, rot);
	}
	
	void initStorage() {
		if (this.storage == null || this.storage.getSubType() != super.SubId) {
			switch (super.SubId) {
				case 0:
					this.storage = new TileLogicAdv.LogicAdvXcvr();
					break;
				default:
					this.storage = null;
			}
			
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (!this.tryDropBlock()) {
			this.initStorage();
			switch (super.SubId) {
				case 0:
					if (this.isTickRunnable()) {
						return;
					} else {
						this.storage.updatePowerState();
					}
				default:
			}
		}
	}
	
	@Override
	public void onTileTick() {
		this.initStorage();
		this.storage.tileTick();
	}
	
	@Override
	public int getPoweringMask(int ch) {
		this.initStorage();
		return this.storage.getPoweringMask(ch);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.initStorage();
		this.storage.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.storage.writeToNBT(tag);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		this.initStorage();
		this.storage.readFromPacket(buffer);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		this.storage.writeToPacket(data);
	}
	
	public abstract class LogicAdvModule {
		
		public abstract void updatePowerState();
		
		public abstract void tileTick();
		
		public abstract int getSubType();
		
		public abstract int getPoweringMask(int var1);
		
		public void updateCurrentStrength() {
		}
		
		public abstract void readFromNBT(NBTTagCompound var1);
		
		public abstract void writeToNBT(NBTTagCompound var1);
		
		public void readFromPacket(ByteBuf buffer) {
		}
		
		@SuppressWarnings("rawtypes")
		public void writeToPacket(ArrayList data) {
		}
	}
	
	public class LogicAdvXcvr extends TileLogicAdv.LogicAdvModule {
		
		public int State1 = 0;
		public int State2 = 0;
		public int State1N = 0;
		public int State2N = 0;
		
		public LogicAdvXcvr() {
			super();
		}
		
		@Override
		public void updatePowerState() {
			int ps = RedPowerLib.getRotPowerState(TileLogicAdv.super.worldObj,
					TileLogicAdv.super.xCoord, TileLogicAdv.super.yCoord,
					TileLogicAdv.super.zCoord, 5, TileLogicAdv.super.Rotation,
					0);
			if (ps != TileLogicAdv.super.PowerState) {
				TileLogicAdv.super.PowerState = ps;
				TileLogicAdv.this.updateBlock();
				TileLogicAdv.this.scheduleTick(2);
			}
			
		}
		
		@Override
		public void tileTick() {
			TileLogicAdv.super.Powered = (TileLogicAdv.super.PowerState & 1) > 0;
			TileLogicAdv.super.Active = (TileLogicAdv.super.PowerState & 4) > 0;
			int sd1 = this.State1N;
			int sd2 = this.State2N;
			if (TileLogicAdv.super.Deadmap == 0) {
				if (!TileLogicAdv.super.Powered) {
					sd1 = 0;
				}
				
				if (!TileLogicAdv.super.Active) {
					sd2 = 0;
				}
			} else {
				if (!TileLogicAdv.super.Powered) {
					sd2 = 0;
				}
				
				if (!TileLogicAdv.super.Active) {
					sd1 = 0;
				}
			}
			
			boolean ch = this.State1 != sd1 || this.State2 != sd2;
			this.State1 = sd1;
			this.State2 = sd2;
			if (ch) {
				TileLogicAdv.this.updateBlock();
				RedPowerLib.updateCurrent(TileLogicAdv.super.worldObj,
						TileLogicAdv.super.xCoord, TileLogicAdv.super.yCoord,
						TileLogicAdv.super.zCoord);
			}
			
			this.updatePowerState();
			this.updateCurrentStrength();
		}
		
		@Override
		public int getSubType() {
			return 0;
		}
		
		@Override
		public int getPoweringMask(int ch) {
			int ps = 0;
			if (ch >= 1 && ch <= 16) {
				--ch;
				if ((this.State1 >> ch & 1) > 0) {
					ps |= 8;
				}
				
				if ((this.State2 >> ch & 1) > 0) {
					ps |= 2;
				}
				
				return RedPowerLib.mapRotToCon(ps, TileLogicAdv.super.Rotation);
			} else {
				return 0;
			}
		}
		
		@Override
		public void updateCurrentStrength() {
			if (!TileLogicAdv.this.isTickRunnable()) {
				this.State1N = this.State2;
				this.State2N = this.State1;
				
				for (int ch = 0; ch < 16; ++ch) {
					short p1 = (short) RedPowerLib.updateBlockCurrentStrength(
							TileLogicAdv.super.worldObj, TileLogicAdv.this,
							TileLogicAdv.super.xCoord,
							TileLogicAdv.super.yCoord,
							TileLogicAdv.super.zCoord, RedPowerLib.mapRotToCon(
									2, TileLogicAdv.super.Rotation), 2 << ch);
					short p2 = (short) RedPowerLib.updateBlockCurrentStrength(
							TileLogicAdv.super.worldObj, TileLogicAdv.this,
							TileLogicAdv.super.xCoord,
							TileLogicAdv.super.yCoord,
							TileLogicAdv.super.zCoord, RedPowerLib.mapRotToCon(
									8, TileLogicAdv.super.Rotation), 2 << ch);
					if (p1 > 0) {
						this.State1N |= 1 << ch;
					}
					
					if (p2 > 0) {
						this.State2N |= 1 << ch;
					}
				}
				
				TileLogicAdv.this.markDirty();
				if (this.State1N != this.State1 || this.State2N != this.State2) {
					TileLogicAdv.this.scheduleTick(2);
				}
				
			}
		}
		
		@Override
		public void readFromNBT(NBTTagCompound tag) {
			this.State1 = tag.getInteger("s1");
			this.State2 = tag.getInteger("s2");
			this.State1N = tag.getInteger("s1n");
			this.State2N = tag.getInteger("s2n");
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("s1", this.State1);
			tag.setInteger("s2", this.State2);
			tag.setInteger("s1n", this.State1N);
			tag.setInteger("s2n", this.State2N);
		}
		
		@Override
		public void readFromPacket(ByteBuf buffer) {
			this.State1 = buffer.readInt();
			this.State2 = buffer.readInt();
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void writeToPacket(ArrayList data) {
			data.add(this.State1);
			data.add(this.State2);
		}
	}
}
