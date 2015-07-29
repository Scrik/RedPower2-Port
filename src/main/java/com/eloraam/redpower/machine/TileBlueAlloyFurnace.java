package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;

public class TileBlueAlloyFurnace extends TileAppliance implements IInventory, ISidedInventory, IBluePowerConnectable {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileBlueAlloyFurnace.this;
		}
	};
	private ItemStack[] contents = new ItemStack[10];
	public int cooktime = 0;
	public int ConMask = -1;
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 64;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	void updateLight() {
		super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord);
	}
	
	@Override
	public int getExtendedID() {
		return 4;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.cond.getVoltage() < 60.0D) {
				if (super.Active && this.cond.Flow == 0) {
					super.Active = false;
					this.updateBlock();
					this.updateLight();
				}
				
			} else {
				boolean cs = this.canSmelt();
				if (cs) {
					if (!super.Active) {
						super.Active = true;
						this.updateBlock();
						this.updateLight();
					}
					
					++this.cooktime;
					this.cond.drawPower(1000.0D);
					if (this.cooktime >= 100) {
						this.cooktime = 0;
						this.smeltItem();
						this.markDirty();
					}
				} else {
					if (super.Active) {
						super.Active = false;
						this.updateBlock();
						this.updateLight();
					}
					this.cooktime = 0;
				}
			}
		}
	}
	
	boolean canSmelt() {
		ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, false);
		if (ist == null) {
			return false;
		} else if (this.contents[9] == null) {
			return true;
		} else if (!this.contents[9].isItemEqual(ist)) {
			return false;
		} else {
			int st = this.contents[9].stackSize + ist.stackSize;
			return st <= this.getInventoryStackLimit()
					&& st <= ist.getMaxStackSize();
		}
	}
	
	void smeltItem() {
		if (this.canSmelt()) {
			ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, true);
			if (this.contents[9] == null) {
				this.contents[9] = ist.copy();
			} else {
				this.contents[9].stackSize += ist.stackSize;
			}
			
		}
	}
	
	int getCookScaled(int i) {
		return this.cooktime * i / 100;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 10, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 10; ++i) {
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
	}
	
	@Override
	public int getSizeInventory() {
		return 10;
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
		return "Blulectric Alloy Furnace";
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
	
	public int[] getAccessibleSlotsFromSide(int side) {
		int s = CoreLib.rotToSide(super.Rotation);
		List<Integer> list = new ArrayList<Integer>();
		for(int i = (side == 1 ? 0 : (side == (s ^ 1) ? 9 : 0)); i < (side == 1 ? 9 : (side == (s ^ 1) ? 1 : 0)); i ++) {
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
		
		this.cooktime = nbttagcompound.getShort("CookTime");
		this.cond.readFromNBT(nbttagcompound);
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
		nbttagcompound.setShort("CookTime", (short) this.cooktime);
		this.cond.writeToNBT(nbttagcompound);
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
