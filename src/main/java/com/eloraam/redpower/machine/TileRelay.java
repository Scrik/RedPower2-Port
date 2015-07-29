package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.machine.TileEjectBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileRelay extends TileEjectBase {
	
	@Override
	public int getExtendedID() {
		return 15;
	}
	
	@Override
	public void onTileTick() {
		super.onTileTick();
		if (!CoreLib.isClient(super.worldObj)) {
			if (!super.Active) {
				if (this.handleExtract()) {
					super.Active = true;
					this.updateBlock();
					this.scheduleTick(5);
				}
				
			}
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!this.isTickScheduled()) {
			this.scheduleTick(10);
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 13, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	protected boolean handleExtract() {
		for (int n = 0; n < this.getSizeInventory(); ++n) {
			ItemStack ist = this.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				this.addToBuffer(super.contents[n]);
				this.setInventorySlotContents(n, (ItemStack) null);
				this.drainBuffer();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getInventoryName() {
		return "Relay";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}
}
