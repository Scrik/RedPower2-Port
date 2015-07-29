package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.wiring.TileWiring;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

public class TileInsulatedWire extends TileWiring implements IRedPowerWiring {
	
	public short PowerState = 0;
	
	@Override
	public float getWireHeight() {
		return 0.188F;
	}
	
	@Override
	public int getExtendedID() {
		return 2;
	}
	
	@Override
	public boolean isBlockWeakPoweringTo(int side) {
		if (RedPowerLib.isSearching()) {
			return false;
		} else {
			int dir = RedPowerLib.getConDirMask(side ^ 1);
			dir &= this.getConnectableMask();
			return dir == 0 ? false : (RedPowerLib.isBlockRedstone(
					super.worldObj, super.xCoord, super.yCoord, super.zCoord,
					side ^ 1) ? this.PowerState > 15 : this.PowerState > 0);
		}
	}
	
	@Override
	public int getConnectClass(int side) {
		return 2 + super.Metadata;
	}
	
	@Override
	public int scanPoweringStrength(int cons, int ch) {
		return RedPowerLib.isPowered(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, cons, 0) ? 255 : 0;
	}
	
	@Override
	public int getCurrentStrength(int cons, int ch) {
		return ch != 0 && ch != super.Metadata + 1 ? -1 : ((cons & this
				.getConnectableMask()) == 0 ? -1 : this.PowerState);
	}
	
	@Override
	public void updateCurrentStrength() {
		this.PowerState = (short) RedPowerLib.updateBlockCurrentStrength(
				super.worldObj, this, super.xCoord, super.yCoord, super.zCoord,
				16777215, 1 | 2 << super.Metadata);
		CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord);
	}
	
	@Override
	public int getPoweringMask(int ch) {
		return this.PowerState == 0 ? 0 : (ch != 0 && ch != super.Metadata + 1 ? 0 : this
				.getConnectableMask());
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add((int)this.PowerState);
	}
}
