package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.machine.TileTranspose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileFilter extends TileTranspose implements IInventory, ISidedInventory {
	
	protected ItemStack[] contents = new ItemStack[9];
	protected MachineLib.FilterMap filterMap = null;
	public byte color = 0;
	
	void regenFilterMap() {
		this.filterMap = MachineLib.makeFilterMap(this.contents);
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side == (super.Rotation ^ 1) && state == 1) {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			return this.filterMap.size() == 0 ? super.tubeItemEnter(side,
					state, ti) : (!this.filterMap.containsKey(ti.item) ? false : super.tubeItemEnter(side, state, ti));
		} else {
			return super.tubeItemEnter(side, state, ti);
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		if (side == (super.Rotation ^ 1) && state == 1) {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			return this.filterMap.size() == 0 ? super.tubeItemCanEnter(side,
					state, ti) : (!this.filterMap.containsKey(ti.item) ? false : super
					.tubeItemCanEnter(side, state, ti));
		} else {
			return super.tubeItemCanEnter(side, state, ti);
		}
	}
	
	@Override
	protected void addToBuffer(ItemStack ist) {
		super.buffer.addNewColor(ist, this.color);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1) ? new int[]{0,1,2,3,4,5,6,7,8} : new int[]{};
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 2, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public int getExtendedID() {
		return 3;
	}
	
	@Override
	public void onBlockRemoval() {
		super.onBlockRemoval();
		
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
	}
	
	@Override
	protected boolean handleExtract(IInventory inv, int[] slots) {
		if (this.filterMap == null) {
			this.regenFilterMap();
		}
		if (this.filterMap.size() == 0) {
			ItemStack sm1 = MachineLib.collectOneStack(inv, slots, (ItemStack) null);
			if (sm1 == null) {
				return false;
			} else {
				super.buffer.addNewColor(sm1, this.color);
				this.drainBuffer();
				return true;
			}
		} else {
			int sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
			if (sm < 0) {
				return false;
			} else {
				ItemStack coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
				super.buffer.addNewColor(coll, this.color);
				this.drainBuffer();
				return true;
			}
		}
	}
	
	@Override
	protected boolean suckFilter(ItemStack ist) {
		if (this.filterMap == null) {
			this.regenFilterMap();
		}
		
		return this.filterMap.size() == 0 ? true : this.filterMap
				.containsKey(ist);
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
		return "Filter";
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
	public void markDirty() {
		this.filterMap = null;
		super.markDirty();
	}
	
	@Override
	public void closeInventory() {
	}
	
	@Override
	public void openInventory() {
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
		
		this.color = tag.getByte("color");
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
		tag.setByte("color", this.color);
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1);
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true; //TODO: Check...
	}
}
