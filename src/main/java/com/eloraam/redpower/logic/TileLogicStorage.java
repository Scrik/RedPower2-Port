package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;
import com.eloraam.redpower.logic.TileLogicStorage;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileLogicStorage extends TileLogic {
	
	TileLogicStorage.LogicStorageModule storage = null;
	
	@Override
	public int getExtendedID() {
		return 3;
	}
	
	@Override
	public void initSubType(int st) {
		super.initSubType(st);
		this.initStorage();
	}
	
	@SuppressWarnings("rawtypes")
	public TileLogicStorage.LogicStorageModule getLogicStorage(Class cl) {
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
					this.storage = new TileLogicStorage.LogicStorageCounter();
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
	public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
		if (part != super.Rotation >> 2) {
			return false;
		} else if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			switch (super.SubId) {
				case 0:
					player.openGui(RedPowerLogic.instance, 1, super.worldObj,
							super.xCoord, super.yCoord, super.zCoord);
					return true;
				default:
					return true;
			}
		}
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
	
	public class LogicStorageCounter extends
			TileLogicStorage.LogicStorageModule {
		
		public int Count = 0;
		public int CountMax = 10;
		public int Inc = 1;
		public int Dec = 1;
		
		public LogicStorageCounter() {
			super();
		}
		
		@Override
		public void updatePowerState() {
			int ps = RedPowerLib.getRotPowerState(
					TileLogicStorage.super.worldObj,
					TileLogicStorage.super.xCoord,
					TileLogicStorage.super.yCoord,
					TileLogicStorage.super.zCoord, 5,
					TileLogicStorage.super.Rotation, 0);
			if (ps != TileLogicStorage.super.PowerState) {
				if ((ps & ~TileLogicStorage.super.PowerState & 1) > 0) {
					TileLogicStorage.super.Active = true;
				}
				
				if ((ps & ~TileLogicStorage.super.PowerState & 4) > 0) {
					TileLogicStorage.super.Disabled = true;
				}
				
				TileLogicStorage.super.PowerState = ps;
				TileLogicStorage.this.updateBlock();
				if (TileLogicStorage.super.Active
						|| TileLogicStorage.super.Disabled) {
					TileLogicStorage.this.scheduleTick(2);
				}
			}
			
		}
		
		@Override
		public void tileTick() {
			int co = this.Count;
			if (TileLogicStorage.super.Deadmap > 0) {
				if (TileLogicStorage.super.Active) {
					this.Count -= this.Dec;
					TileLogicStorage.super.Active = false;
				}
				
				if (TileLogicStorage.super.Disabled) {
					this.Count += this.Inc;
					TileLogicStorage.super.Disabled = false;
				}
			} else {
				if (TileLogicStorage.super.Active) {
					this.Count += this.Inc;
					TileLogicStorage.super.Active = false;
				}
				
				if (TileLogicStorage.super.Disabled) {
					this.Count -= this.Dec;
					TileLogicStorage.super.Disabled = false;
				}
			}
			
			if (this.Count < 0) {
				this.Count = 0;
			}
			
			if (this.Count > this.CountMax) {
				this.Count = this.CountMax;
			}
			
			if (co != this.Count) {
				TileLogicStorage.this.updateBlockChange();
				TileLogicStorage.this.playSound("random.click", 0.3F, 0.5F,
						false);
			}
			
			this.updatePowerState();
		}
		
		@Override
		public int getSubType() {
			return 0;
		}
		
		@Override
		public int getPoweringMask(int ch) {
			int ps = 0;
			if (ch != 0) {
				return 0;
			} else {
				if (this.Count == 0) {
					ps |= 2;
				}
				
				if (this.Count == this.CountMax) {
					ps |= 8;
				}
				
				return RedPowerLib.mapRotToCon(ps,
						TileLogicStorage.super.Rotation);
			}
		}
		
		@Override
		public void readFromNBT(NBTTagCompound tag) {
			this.Count = tag.getInteger("cnt");
			this.CountMax = tag.getInteger("max");
			this.Inc = tag.getInteger("inc");
			this.Dec = tag.getInteger("dec");
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("cnt", this.Count);
			tag.setInteger("max", this.CountMax);
			tag.setInteger("inc", this.Inc);
			tag.setInteger("dec", this.Dec);
		}
		
		@Override
		public void readFromPacket(ByteBuf buffer) {
			this.Count = buffer.readInt();
			this.CountMax = buffer.readInt();
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void writeToPacket(ArrayList data) {
			data.add(this.Count);
			data.add(this.CountMax);
		}
	}
	
	public abstract class LogicStorageModule {
		
		public abstract void updatePowerState();
		
		public abstract void tileTick();
		
		public abstract int getSubType();
		
		public abstract int getPoweringMask(int var1);
		
		public abstract void readFromNBT(NBTTagCompound var1);
		
		public abstract void writeToNBT(NBTTagCompound var1);
		
		public void readFromPacket(ByteBuf buffer) {
		}
		
		@SuppressWarnings("rawtypes")
		public void writeToPacket(ArrayList data) {
		}
	}
}
