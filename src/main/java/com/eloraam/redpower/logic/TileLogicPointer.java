package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.IPointerTile;
import com.eloraam.redpower.logic.TileLogic;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileLogicPointer extends TileLogic implements IPointerTile {
	
	private long timestart = 0L;
	public long interval = 40L;
	
	@Override
	public void initSubType(int st) {
		super.initSubType(st);
		switch (st) {
			case 0:
				this.interval = 38L;
				break;
			case 2:
				super.Disabled = true;
		}
		
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec && (super.SubId == 1 || super.SubId == 2) ? 1 : super
				.getPartMaxRotation(part, sec);
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		return sec && (super.SubId == 1 || super.SubId == 2) ? super.Deadmap : super
				.getPartRotation(part, sec);
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (sec && (super.SubId == 1 || super.SubId == 2)) {
			super.Deadmap = rot;
			this.updateBlockChange();
		} else {
			super.setPartRotation(part, sec, rot);
		}
	}
	
	private void timerChange() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 7, super.Rotation, 0);
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		if (super.Powered) {
			if (!super.Disabled) {
				return;
			}
			
			if (ps > 0) {
				return;
			}
			
			super.Powered = false;
			super.Disabled = false;
			this.timestart = super.worldObj.getWorldTime();
			this.updateBlock();
		} else if (super.Disabled) {
			if (ps > 0) {
				return;
			}
			
			this.timestart = super.worldObj.getWorldTime();
			super.Disabled = false;
			this.updateBlock();
		} else {
			if (ps == 0) {
				return;
			}
			
			super.Disabled = true;
			this.updateBlock();
		}
		
	}
	
	private void timerTick() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 7, super.Rotation, 0);
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		if (super.Powered) {
			if (super.Disabled) {
				if (ps > 0) {
					super.Powered = false;
					this.updateBlock();
					return;
				}
				
				super.Disabled = false;
				super.Powered = false;
				this.timestart = super.worldObj.getWorldTime();
				this.updateBlock();
				return;
			}
			
			if (ps == 0) {
				super.Powered = false;
			} else {
				super.Disabled = true;
				this.scheduleTick(2);
			}
			
			this.timestart = super.worldObj.getWorldTime();
			this.updateBlockChange();
		} else if (super.Disabled) {
			if (ps > 0) {
				return;
			}
			
			this.timestart = super.worldObj.getWorldTime();
			super.Disabled = false;
			this.updateBlock();
		} else {
			if (ps == 0) {
				return;
			}
			
			super.Disabled = true;
			this.updateBlock();
		}
		
	}
	
	private void timerUpdate() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (!super.Powered && !super.Disabled) {
				long wt = super.worldObj.getWorldTime();
				if (this.interval < 2L) {
					this.interval = 2L;
				}
				
				if (this.timestart > wt) {
					this.timestart = wt;
				}
				
				if (this.timestart + this.interval <= wt) {
					this.playSound("random.click", 0.3F, 0.5F, false);
					super.Powered = true;
					this.scheduleTick(2);
					this.updateBlockChange();
				}
				
			}
		}
	}
	
	private void sequencerUpdate() {
		long wt = super.worldObj.getWorldTime() + 6000L;
		float f = (float) wt / (float) (this.interval * 4L);
		int i = (int) Math.floor(f * 4.0F);
		if (super.Deadmap == 1) {
			i = 3 - i & 3;
		} else {
			i = i + 3 & 3;
		}
		
		if (super.PowerState != i && !CoreLib.isClient(super.worldObj)) {
			this.playSound("random.click", 0.3F, 0.5F, false);
			super.PowerState = i;
			this.updateBlockChange();
		}
		
	}
	
	private void stateCellChange() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 7, super.Rotation, 0);
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		boolean ps3 = super.Deadmap == 0 ? (ps & 3) > 0 : (ps & 6) > 0;
		if (super.Disabled && !ps3) {
			super.Disabled = false;
			this.timestart = super.worldObj.getWorldTime();
			this.updateBlock();
		} else if (!super.Disabled && ps3) {
			super.Disabled = true;
			this.updateBlock();
		}
		
		if (!super.Active && !super.Powered && (ps & 2) > 0) {
			super.Powered = true;
			this.updateBlock();
			this.scheduleTick(2);
		}
		
	}
	
	private void stateCellTick() {
		if (!super.Active && super.Powered) {
			super.Powered = false;
			super.Active = true;
			this.timestart = super.worldObj.getWorldTime();
			this.updateBlockChange();
		} else if (super.Active && super.Powered) {
			super.Powered = false;
			super.Active = false;
			this.updateBlockChange();
		}
		
	}
	
	private void stateCellUpdate() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (super.Active && !super.Powered && !super.Disabled) {
				long wt = super.worldObj.getWorldTime();
				if (this.interval < 2L) {
					this.interval = 2L;
				}
				
				if (this.timestart > wt) {
					this.timestart = wt;
				}
				
				if (this.timestart + this.interval <= wt) {
					this.playSound("random.click", 0.3F, 0.5F, false);
					super.Powered = true;
					this.scheduleTick(2);
					this.updateBlockChange();
				}
				
			}
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (!this.tryDropBlock()) {
			switch (super.SubId) {
				case 0:
					this.timerChange();
					break;
				case 2:
					this.stateCellChange();
			}
			
		}
	}
	
	@Override
	public void onTileTick() {
		switch (super.SubId) {
			case 0:
				this.timerTick();
				break;
			case 2:
				this.stateCellTick();
		}
		
	}
	
	@Override
	public int getPoweringMask(int ch) {
		if (ch != 0) {
			return 0;
		} else {
			switch (super.SubId) {
				case 0:
					if (!super.Disabled && super.Powered) {
						return RedPowerLib.mapRotToCon(13, super.Rotation);
					}
					
					return 0;
				case 1:
					return RedPowerLib.mapRotToCon(1 << super.PowerState,
							super.Rotation);
				case 2:
					int ps = (super.Active && super.Powered ? 8 : 0)
							| (super.Active && !super.Powered ? (super.Deadmap == 0 ? 4 : 1) : 0);
					return RedPowerLib.mapRotToCon(ps, super.Rotation);
				default:
					return 0;
			}
		}
	}
	
	@Override
	public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
		if (part != super.Rotation >> 2) {
			return false;
		} else if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return false;
		} else {
			player.openGui(RedPowerLogic.instance, 2, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		switch (super.SubId) {
			case 0:
				this.timerUpdate();
				break;
			case 1:
				this.sequencerUpdate();
				break;
			case 2:
				this.stateCellUpdate();
		}
		
	}
	
	@Override
	public float getPointerDirection(float f) {
		long wt;
		float ivt;
		if (super.SubId == 0) {
			if (!super.Powered && !super.Disabled) {
				wt = super.worldObj.getWorldTime();
				ivt = (wt + f - this.timestart) / this.interval;
				if (ivt > 1.0F) {
					ivt = 1.0F;
				}
				
				return ivt + 0.75F;
			} else {
				return 0.75F;
			}
		} else if (super.SubId == 1) {
			wt = super.worldObj.getWorldTime() + 6000L;
			ivt = (wt + f) / (this.interval * 4L);
			if (super.Deadmap == 1) {
				ivt = 0.75F - ivt;
			} else {
				ivt += 0.75F;
			}
			
			return ivt;
		} else if (super.SubId != 2) {
			return 0.0F;
		} else {
			if (super.Deadmap > 0) {
				if (!super.Active || super.Disabled) {
					return 1.0F;
				}
				
				if (super.Active && super.Powered) {
					return 0.8F;
				}
			} else {
				if (!super.Active || super.Disabled) {
					return 0.5F;
				}
				
				if (super.Active && super.Powered) {
					return 0.7F;
				}
			}
			
			wt = super.worldObj.getWorldTime();
			ivt = (wt + f - this.timestart) / this.interval;
			return super.Deadmap > 0 ? 1.0F - 0.2F * ivt : 0.5F + 0.2F * ivt;
		}
	}
	
	@Override
	public Quat getOrientationBasis() {
		return MathLib.orientQuat(super.Rotation >> 2, super.Rotation & 3);
	}
	
	public Vector3 getPointerOrigin() {
		return super.SubId == 2 ? (super.Deadmap > 0 ? new Vector3(0.0D, -0.1D,
				-0.25D) : new Vector3(0.0D, -0.1D, 0.25D)) : new Vector3(0.0D,
				-0.1D, 0.0D);
	}
	
	public void setInterval(long iv) {
		if (super.SubId == 0) {
			this.interval = iv - 2L;
		} else {
			this.interval = iv;
		}
		
	}
	
	public long getInterval() {
		return super.SubId == 0 ? this.interval + 2L : this.interval;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.interval = tag.getLong("iv");
		if (super.SubId == 0 || super.SubId == 2) {
			this.timestart = tag.getLong("ts");
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("iv", this.interval);
		if (super.SubId == 0 || super.SubId == 2) {
			tag.setLong("ts", this.timestart);
		}
		
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		if (buffer.readInt() == 2) {
			this.interval = buffer.readInt();
			if (super.SubId == 0 || super.SubId == 2) {
				this.timestart = buffer.readInt();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add(2);
		data.add(this.interval);
		if (super.SubId == 0 || super.SubId == 2) {
			data.add(this.timestart);
		}
	}
}
