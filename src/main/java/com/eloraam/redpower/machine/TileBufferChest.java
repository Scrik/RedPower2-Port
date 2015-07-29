package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRotatable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileBufferChest extends TileAppliance implements IInventory, ISidedInventory, IRotatable {
	
	private ItemStack[] contents = new ItemStack[20];
	
	@Override
	public int getExtendedID() {
		return 2;
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 4, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	public int getFacing(EntityLivingBase ent) {
		int yawrx = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (Math.abs(ent.posX - super.xCoord) < 2.0D
				&& Math.abs(ent.posZ - super.zCoord) < 2.0D) {
			double p = ent.posY + 1.82D - ent.yOffset - super.yCoord;
			if (p > 2.0D) {
				return 0;
			}
			
			if (p < 0.0D) {
				return 1;
			}
		}
		
		switch (yawrx) {
			case 0:
				return 3;
			case 1:
				return 4;
			case 2:
				return 2;
			default:
				return 5;
		}
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = this.getFacing(ent);
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 20; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
			}
		}
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : 5;
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		return sec ? 0 : super.Rotation;
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (!sec) {
			super.Rotation = rot;
			this.updateBlockChange();
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 20;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.contents[i];
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack tr;
			if (this.contents[i].stackSize <= j) {
				tr = this.contents[i];
				this.contents[i] = null;
				this.markDirty();
				return tr;
			} else {
				tr = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize == 0) {
					this.contents[i] = null;
				}
				this.markDirty();
				return tr;
			}
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack ist = this.contents[i];
			this.contents[i] = null;
			return ist;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack ist) {
		this.contents[i] = ist;
		if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
			ist.stackSize = this.getInventoryStackLimit();
		}
		this.markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return "Buffer";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void closeInventory() {
	}
	
	@Override
	public void openInventory() {
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = (side ^ 1) == super.Rotation ? 0 : 4 * ((5 + (side ^ 1) - super.Rotation) % 6); i < ((side ^ 1) == super.Rotation ? 20 : 4); i ++) {
			list.add(i);
		}
		return CoreLib.toIntArray(list);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList items = nbttagcompound.getTagList("Items", 10); //TODO:
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				this.contents[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		nbttagcompound.setTag("Items", items);
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		for(int i : this.getAccessibleSlotsFromSide(side)) {
			if(i == slotID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		for(int i : this.getAccessibleSlotsFromSide(side)) {
			if(i == slotID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}
}
