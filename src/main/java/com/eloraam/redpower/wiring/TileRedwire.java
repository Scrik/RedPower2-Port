package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.wiring.TileWiring;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

public class TileRedwire extends TileWiring implements IRedPowerWiring {
	
	public short PowerState = 0;
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
	@Override
	public boolean isBlockStrongPoweringTo(int side) {
		if (RedPowerLib.isSearching()) {
			return false;
		} else {
			int dir = 15 << ((side ^ 1) << 2);
			dir &= this.getConnectableMask();
			return dir == 0 ? false : this.PowerState > 0;
		}
	}
	
	@Override
	public boolean isBlockWeakPoweringTo(int side) {
		if (RedPowerLib.isSearching()) {
			return false;
		} else {
			int dir = 15 << ((side ^ 1) << 2);
			dir |= RedPowerLib.getConDirMask(side ^ 1);
			dir &= this.getConnectableMask();
			return dir == 0 ? false : (RedPowerLib.isBlockRedstone(
					super.worldObj, super.xCoord, super.yCoord, super.zCoord,
					side ^ 1) ? this.PowerState > 15 : this.PowerState > 0);
		}
	}
	
	@Override
	public int getConnectClass(int side) {
		return 1;
	}
	
	@Override
	public int getConnectableMask() {
		if (super.ConaMask >= 0) {
			return super.ConaMask;
		} else {
			int tr = super.getConnectableMask();
			if ((super.ConSides & 1) > 0) {
				tr |= 16777216;
			}
			if ((super.ConSides & 2) > 0) {
				tr |= 33554432;
			}
			if ((super.ConSides & 4) > 0) {
				tr |= 67108864;
			}
			if ((super.ConSides & 8) > 0) {
				tr |= 134217728;
			}
			if ((super.ConSides & 16) > 0) {
				tr |= 268435456;
			}
			if ((super.ConSides & 32) > 0) {
				tr |= 536870912;
			}
			super.ConaMask = tr;
			return tr;
		}
	}
	
	@Override
	public int getCurrentStrength(int cons, int ch) {
		return ch != 0 ? -1 : ((cons & this.getConnectableMask()) == 0 ? -1 : this.PowerState);
	}
	
	@Override
	public int scanPoweringStrength(int cons, int ch) {
		return ch != 0 ? 0 : (RedPowerLib.isPowered(super.worldObj,
				super.xCoord, super.yCoord, super.zCoord, cons, super.ConSides) ? 255 : 0);
	}
	
	@Override
	public void updateCurrentStrength() {
		this.PowerState = (short) RedPowerLib.updateBlockCurrentStrength(
				super.worldObj, this, super.xCoord, super.yCoord, super.zCoord,
				1073741823, 1);
		CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord);
	}
	
	@Override
	public int getPoweringMask(int ch) {
		return ch == 0 && this.PowerState != 0 ? this.getConnectableMask() : 0;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.PowerState = (short) (nbttagcompound.getByte("pwr") & 255);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("pwr", (byte) this.PowerState);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		this.PowerState = (short)buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add((int)this.PowerState);
	}
}
