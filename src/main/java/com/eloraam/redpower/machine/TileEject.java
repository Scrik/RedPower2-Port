package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.machine.TileEjectBase;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEject extends TileEjectBase {
	
	@Override
	public int getExtendedID() {
		return 14;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, 16777215, 63)) {
			if (!super.Powered) {
				super.Powered = true;
				this.markDirty();
				if (!super.Active) {
					super.Active = true;
					if (this.handleExtract()) {
						this.updateBlock();
					}
					
				}
			}
		} else {
			if (super.Active && !this.isTickScheduled()) {
				this.scheduleTick(5);
			}
			
			super.Powered = false;
			this.markDirty();
		}
	}
	
	protected boolean handleExtract() {
		for (int n = 0; n < this.getSizeInventory(); ++n) {
			ItemStack ist = this.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				this.addToBuffer(this.decrStackSize(n, 1));
				this.drainBuffer();
				return true;
			}
		}
		
		return false;
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
