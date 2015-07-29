package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileDeployBase;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileAssemble extends TileDeployBase implements IInventory, ISidedInventory, IRedPowerWiring {
	
	private ItemStack[] contents = new ItemStack[34];
	public byte select = 0;
	public byte mode = 0;
	public int skipSlots = '\ufffe';
	public int ConMask = -1;
	public int PowerState = 0;
	
	@Override
	public int getExtendedID() {
		return 13;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 11, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 34; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
		
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		if (this.mode == 0) {
			super.onBlockNeighborChange(bl);
		}
		RedPowerLib.updateCurrent(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
	}
	
	@Override
	public int getConnectionMask() {
		if (this.ConMask >= 0) {
			return this.ConMask;
		} else {
			this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
					super.xCoord, super.yCoord, super.zCoord);
			return this.ConMask;
		}
	}
	
	@Override
	public int getExtConnectionMask() {
		return 0;
	}
	
	@Override
	public int getPoweringMask(int ch) {
		return 0;
	}
	
	@Override
	public int scanPoweringStrength(int cons, int ch) {
		return 0;
	}
	
	@Override
	public int getCurrentStrength(int cons, int ch) {
		return -1;
	}
	
	@Override
	public void updateCurrentStrength() {
		if (this.mode == 1) {
			int slot;
			for (slot = 0; slot < 16; ++slot) {
				short wc = (short) RedPowerLib.getMaxCurrentStrength(
						super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, 1073741823, 0, slot + 1);
				if (wc > 0) {
					this.PowerState |= 1 << slot;
				} else {
					this.PowerState &= ~(1 << slot);
				}
			}
			
			CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord,
					super.zCoord);
			if (this.PowerState == 0) {
				if (super.Active) {
					this.scheduleTick(5);
				}
			} else if (!super.Active) {
				super.Active = true;
				this.updateBlock();
				slot = Integer.numberOfTrailingZeros(this.PowerState);
				if (this.contents[slot] != null) {
					WorldCoord var4 = new WorldCoord(this);
					var4.step(super.Rotation ^ 1);
					int ms = this.getMatchingStack(slot);
					if (ms >= 0) {
						this.enableTowardsActive(var4, ms);
					}
				}
				
			}
		}
	}
	
	@Override
	public int getConnectClass(int side) {
		return this.mode == 0 ? 0 : 18;
	}
	
	protected void packInv(ItemStack[] bkup, int act) {
		int i;
		for (i = 0; i < 36; ++i) {
			bkup[i] = TileDeployBase.fakePlayer.inventory.getStackInSlot(i);
			TileDeployBase.fakePlayer.inventory.setInventorySlotContents(i,
					(ItemStack) null);
		}
		
		for (i = 0; i < 18; ++i) {
			if (act == i) {
				TileDeployBase.fakePlayer.inventory.setInventorySlotContents(0,
						this.contents[16 + i]);
			} else {
				TileDeployBase.fakePlayer.inventory.setInventorySlotContents(
						i + 9, this.contents[16 + i]);
			}
		}
		
	}
	
	protected void unpackInv(ItemStack[] bkup, int act) {
		int i;
		for (i = 0; i < 18; ++i) {
			if (act == i) {
				this.contents[16 + i] = TileDeployBase.fakePlayer.inventory
						.getStackInSlot(0);
			} else {
				this.contents[16 + i] = TileDeployBase.fakePlayer.inventory
						.getStackInSlot(i + 9);
			}
		}
		
		for (i = 0; i < 36; ++i) {
			TileDeployBase.fakePlayer.inventory.setInventorySlotContents(i,
					bkup[i]);
		}
		
	}
	
	protected int getMatchingStack(int stack) {
		for (int i = 0; i < 18; ++i) {
			ItemStack compareStack = this.contents[16 + i];
			if (this.contents[16 + i] != null && CoreLib.compareItemStack(compareStack, this.contents[stack]) == 0) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void enableTowards(WorldCoord wc) {
		int i;
		if (this.contents[this.select] != null) {
			i = this.getMatchingStack(this.select);
			if (i >= 0) {
				this.enableTowardsActive(wc, i);
			}
		}
		
		for (i = 0; i < 16; ++i) {
			this.select = (byte) (this.select + 1 & 15);
			if ((this.skipSlots & 1 << this.select) == 0 || this.select == 0) {
				break;
			}
		}
		
	}
	
	protected void enableTowardsActive(WorldCoord wc, int act) {
		ItemStack[] bkup = new ItemStack[36];
		this.initPlayer();
		this.packInv(bkup, act);
		ItemStack ist = this.contents[16 + act];
		if (ist != null && ist.stackSize > 0
				&& this.tryUseItemStack(ist, wc.x, wc.y, wc.z, 0)) {
			if (TileDeployBase.fakePlayer.isUsingItem()) {
				TileDeployBase.fakePlayer.stopUsingItem();
			}
			
			this.unpackInv(bkup, act);
			if (this.contents[16 + act].stackSize == 0) {
				this.contents[16 + act] = null;
			}
			
			this.markDirty();
		} else {
			this.unpackInv(bkup, act);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 34;
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
		
		if (ist != null && i < 16) {
			this.skipSlots &= ~(1 << i);
		}
		this.markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return "Assembler";
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
	
	/*public int getStartInventorySide(ForgeDirection fd) {
		int side = fd.ordinal();
		return (side ^ 1) == super.Rotation ? 0 : 16;
	}
	
	public int getSizeInventorySide(ForgeDirection fd) {
		int side = fd.ordinal();
		return (side ^ 1) == super.Rotation ? 0 : 18;
	}*/
	
	public int[] getAccessibleSlotsFromSide(int side) {
		return (side ^ 1) == super.Rotation ? new int[]{} : new int[]{16, 17 /*, 18 MAYBE*/};
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
		
		this.mode = tag.getByte("mode");
		this.select = tag.getByte("sel");
		this.skipSlots = tag.getShort("ssl") & '\uffff';
		this.PowerState = tag.getInteger("psex");
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
		tag.setByte("mode", this.mode);
		tag.setByte("sel", this.select);
		tag.setShort("ssl", (short) this.skipSlots);
		tag.setInteger("psex", this.PowerState);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		this.mode = buffer.readByte();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add(this.mode);
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
		return true; //TODO: Maybe not
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}
}
