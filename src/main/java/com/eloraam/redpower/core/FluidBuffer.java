package com.eloraam.redpower.core;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public abstract class FluidBuffer {
	
	public int Type = 0;
	public int Level = 0;
	public int Delta = 0;
	private int lastTick = 0;
	
	public abstract TileEntity getParent();
	
	public abstract void onChange();
	
	public int getMaxLevel() {
		return 1000;
	}
	
	public int getLevel() {
		long lt = this.getParent().getWorldObj().getWorldTime();
		if ((lt & 65535L) == this.lastTick) {
			return this.Level;
		} else {
			this.lastTick = (int) (lt & 65535L);
			this.Level += this.Delta;
			this.Delta = 0;
			if (this.Level == 0) {
				this.Type = 0;
			}
			
			return this.Level;
		}
	}
	
	public void addLevel(int type, int lvl) {
		this.Type = type;
		this.Delta += lvl;
		this.onChange();
	}
	
	public Fluid getFluidClass() {
		return this.Type != 0 && this.Level != 0 ? FluidRegistry.getFluid(this.Type) : null;
	}
	
	public void readFromNBT(NBTTagCompound tag, String name) {
		NBTTagCompound t2 = tag.getCompoundTag(name);
		this.Type = t2.getInteger("type");
		this.Level = t2.getInteger("lvl");
		this.Delta = t2.getInteger("del");
		this.lastTick = t2.getInteger("ltk");
	}
	
	public void writeToNBT(NBTTagCompound tag, String name) {
		NBTTagCompound t2 = new NBTTagCompound();
		t2.setInteger("type", (short) this.Type);
		t2.setInteger("lvl", (short) this.Level);
		t2.setInteger("del", (short) this.Delta);
		t2.setShort("lck", (short) this.lastTick);
		tag.setTag(name, t2);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeToPacket(ArrayList data) {
		data.add((int)this.Type);
		data.add((int)this.Level);
	}
	
	public void readFromPacket(ByteBuf buffer) {
		this.Type = buffer.readInt();
		this.Level = buffer.readInt();
	}
}
