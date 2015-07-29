package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BluePowerConductor;

import net.minecraft.nbt.NBTTagCompound;

public abstract class BluePowerEndpoint extends BluePowerConductor {
	
	public int Charge = 0;
	public int Flow = 0;
	
	@Override
	public double getInvCap() {
		return 0.25D;
	}
	
	@Override
	public int getChargeScaled(int i) {
		return Math.min(i, i * this.Charge / 1000);
	}
	
	@Override
	public int getFlowScaled(int i) {
		return Integer.bitCount(this.Flow) * i / 32;
	}
	
	@Override
	public void iterate() {
		super.iterate();
		this.Charge = (int) (this.getVoltage() * 10.0D);
		this.Flow = this.Flow << 1 | (this.Charge >= 600 ? 1 : 0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.Charge = tag.getShort("chg");
		this.Flow = tag.getInteger("flw");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("chg", (short) this.Charge);
		tag.setInteger("flw", this.Flow);
	}
}
