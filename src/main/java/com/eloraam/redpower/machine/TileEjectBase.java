package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.machine.TileMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEjectBase extends TileMachine implements IInventory, ISidedInventory, ITubeConnectable {
	
	TubeBuffer buffer = new TubeBuffer();
	protected ItemStack[] contents = new ItemStack[9];
	
	@Override
	public int getTubeConnectableSides() {
		return 1 << super.Rotation;
	}
	
	@Override
	public int getTubeConClass() {
		return 0;
	}
	
	@Override
	public boolean canRouteItems() {
		return false;
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side == super.Rotation && state == 2) {
			this.buffer.addBounce(ti);
			super.Active = true;
			this.updateBlock();
			this.scheduleTick(5);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return side == super.Rotation && state == 2;
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
	}
	
	protected void addToBuffer(ItemStack ist) {
		this.buffer.addNew(ist);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 12, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
			}
		}
		this.buffer.onRemove(this);
	}
	
	public void drainBuffer() {
		while (true) {
			if (!this.buffer.isEmpty()) {
				TubeItem ti = this.buffer.getLast();
				if (!this.handleItem(ti)) {
					this.buffer.plugged = true;
					return;
				}
				this.buffer.pop();
				if (!this.buffer.plugged) {
					continue;
				}
				
				return;
			}
			
			return;
		}
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (!this.buffer.isEmpty()) {
				this.drainBuffer();
				if (!this.buffer.isEmpty()) {
					this.scheduleTick(10);
				} else {
					this.scheduleTick(5);
				}
				
			} else {
				if (!super.Powered) {
					super.Active = false;
					this.updateBlock();
				}
				
			}
		}
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
		return "Ejector";
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
		return side == super.Rotation ? new int[]{} : new int[]{0,1,2,3,4,5,6,7,8};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10); //TODO:
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		
		this.buffer.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				this.contents[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		tag.setTag("Items", items);
		this.buffer.writeToNBT(tag);
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true; //TODO: Maybe not
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true; //TODO: Maybe not
	}
}
