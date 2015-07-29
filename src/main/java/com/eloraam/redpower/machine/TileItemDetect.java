package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.machine.TileMachine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileItemDetect extends TileMachine implements ITubeConnectable, IInventory, ISidedInventory {
	
	TubeBuffer buffer = new TubeBuffer();
	int count = 0;
	public byte mode = 0;
	protected ItemStack[] contents = new ItemStack[9];
	protected MachineLib.FilterMap filterMap = null;
	
	void regenFilterMap() {
		this.filterMap = MachineLib.makeFilterMap(this.contents);
	}
	
	@Override
	public int getTubeConnectableSides() {
		return 3 << (super.Rotation & -2);
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
		} else if (side == (super.Rotation ^ 1) && state == 1) {
			if (!this.buffer.isEmpty()) {
				return false;
			} else {
				this.buffer.add(ti);
				if (this.filterMap == null) {
					this.regenFilterMap();
				}
				
				if (this.filterMap.size() == 0
						|| this.filterMap.containsKey(ti.item)) {
					if (this.mode == 0) {
						this.count += ti.item.stackSize;
					} else if (this.mode == 1) {
						++this.count;
					}
				}
				
				super.Active = true;
				this.updateBlock();
				this.scheduleTick(5);
				this.drainBuffer();
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return side == super.Rotation && state == 2 ? true : (side == (super.Rotation ^ 1)
				&& state == 1 ? this.buffer.isEmpty() : false);
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1) ? new int[]{0,1,2,3,4,5,6,7,8} /*TODO: Maybe 9*/ : new int[]{};
	}
	
	public void drainBuffer() {
		while (true) {
			if (!this.buffer.isEmpty()) {
				TubeItem ti = this.buffer.getLast();
				if (!this.handleItem(ti)) {
					this.buffer.plugged = true;
					if (this.mode == 2 && !super.Powered) {
						super.Delay = false;
						super.Powered = true;
						this.count = 0;
						this.updateBlockChange();
					}
					
					return;
				}
				
				this.buffer.pop();
				if (!this.buffer.plugged) {
					continue;
				}
				
				if (this.mode == 2 && !super.Powered) {
					super.Delay = false;
					super.Powered = true;
					this.count = 0;
					this.updateBlockChange();
				}
				
				return;
			}
			
			if (this.mode == 2 && super.Powered) {
				super.Powered = false;
				this.updateBlockChange();
			}
			
			return;
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.mode != 2) {
				if (super.Powered) {
					if (super.Delay) {
						super.Delay = false;
						this.markDirty();
					} else {
						super.Powered = false;
						if (this.count > 0) {
							super.Delay = true;
						}
						
						this.updateBlockChange();
					}
				} else if (this.count != 0) {
					if (super.Delay) {
						super.Delay = false;
						this.markDirty();
					} else {
						--this.count;
						super.Powered = true;
						super.Delay = true;
						this.updateBlockChange();
					}
				}
			}
		}
	}
	
	@Override
	public boolean isPoweringTo(int side) {
		return side == (super.Rotation ^ 1) ? false : super.Powered;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 6, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public int getExtendedID() {
		return 4;
	}
	
	@Override
	public void onBlockRemoval() {
		this.buffer.onRemove(this);
		
		for (int i = 0; i < 9; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
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
				super.Active = false;
				this.updateBlock();
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
		return "Item Detector";
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
		
		this.buffer.readFromNBT(tag);
		this.count = tag.getInteger("cnt");
		this.mode = tag.getByte("mode");
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
		tag.setInteger("cnt", this.count);
		tag.setByte("mode", this.mode);
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
		return true; //TODO: May be not
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}
}
