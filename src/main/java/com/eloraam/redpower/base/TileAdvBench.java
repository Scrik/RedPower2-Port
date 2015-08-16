package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.CoreLib;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class TileAdvBench extends TileAppliance implements IInventory, ISidedInventory {
	
	private ItemStack[] contents = new ItemStack[28];
	
	@Override
	public int getExtendedID() {
		return 3;
	}
	
	@Override
	public boolean canUpdate() { //TODO IN MASS AMOUNT CAN CAUSE TPS DROP :(
		return true;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerBase.instance, 2, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		this.sendPacket();
		this.markDirty();
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 27; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
			}
		}
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{10, 11, 12, 13, 14, 15, 16, 17, 18}; //TODO x2 maybe if needed
	}
	
	@Override
	public int getSizeInventory() {
		return 28;
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
		return StatCollector.translateToLocal("tile.ProjectTable.name");
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		TileEntity tile = super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord);
		return tile != this || tile == null || tile.isInvalid() ? false : 
			player.getDistanceSq(super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void closeInventory() {
	}
	
	@Override
	public void openInventory() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList items = nbttagcompound.getTagList("Items", 10); //TODO:  Look on this
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		this.markDirty();
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
		return slotID >= 10 && slotID <= 18;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return slotID >= 10 && slotID <= 18;
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
