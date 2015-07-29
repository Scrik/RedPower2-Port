package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileDeployBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileDeploy extends TileDeployBase implements IInventory, ISidedInventory {
	
	private ItemStack[] contents = new ItemStack[9];
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 1, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
		
	}
	
	protected void packInv(ItemStack[] bkup) {
		for (int i = 0; i < 9; ++i) {
			bkup[i] = TileDeployBase.fakePlayer.inventory.getStackInSlot(i);
			TileDeployBase.fakePlayer.inventory.setInventorySlotContents(i,
					this.contents[i]);
		}
		
	}
	
	protected void unpackInv(ItemStack[] bkup) {
		for (int i = 0; i < 9; ++i) {
			this.contents[i] = TileDeployBase.fakePlayer.inventory
					.getStackInSlot(i);
			TileDeployBase.fakePlayer.inventory.setInventorySlotContents(i,
					bkup[i]);
		}
		
	}
	
	@Override
	public void enableTowards(WorldCoord wc) {
		ItemStack[] bkup = new ItemStack[9];
		this.initPlayer();
		this.packInv(bkup);
		
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0
					&& this.tryUseItemStack(ist, wc.x, wc.y, wc.z, i)) {
				if (TileDeployBase.fakePlayer.isUsingItem()) {
					TileDeployBase.fakePlayer.stopUsingItem();
				}
				
				this.unpackInv(bkup);
				if (this.contents[i].stackSize == 0) {
					this.contents[i] = null;
				}
				
				this.markDirty();
				return;
			}
		}
		this.unpackInv(bkup);
	}
	
	@Override
	public int getSizeInventory() {
		return 9;
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
		return "Deployer";
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
		return (side ^ 1) == super.Rotation ? new int[]{} : new int[]{0,1,2,3,4,5,6,7,8};
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
		return (side ^ 1) != super.Rotation;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return (side ^ 1) != super.Rotation;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true; //TODO: Maybe not
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}
}
