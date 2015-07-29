package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.EnumSkyBlock;

public class TileAlloyFurnace extends TileAppliance implements IInventory, ISidedInventory {
	
	private ItemStack[] contents = new ItemStack[11];
	public int totalburn = 0;
	public int burntime = 0;
	public int cooktime = 0;
	
	void updateLight() {
		super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord); //TODO: Looks strange...
		this.sendPacket();
		this.markDirty();
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean btu = false;
		if (this.burntime > 0) {
			--this.burntime;
			if (this.burntime == 0) {
				btu = true;
				super.Active = false;
			}
		}
		
		if (!CoreLib.isClient(super.worldObj)) {
			boolean cs = this.canSmelt();
			if (this.burntime == 0 && cs && this.contents[9] != null) {
				this.burntime = this.totalburn = CoreLib.getBurnTime(this.contents[9]);
				if (this.burntime > 0) {
					super.Active = true;
					if (this.contents[9].getItem().getContainerItem() != null) {
						this.contents[9] = new ItemStack(this.contents[9].getItem().getContainerItem());
					} else {
						--this.contents[9].stackSize;
					}
					
					if (this.contents[9].stackSize == 0) {
						this.contents[9] = null;
					}
					
					if (!btu) {
						this.markDirty();
						this.updateBlock();
						this.updateLight();
					}
				}
			}
			
			if (this.burntime > 0 && cs) {
				++this.cooktime;
				if (this.cooktime == 200) {
					this.cooktime = 0;
					this.smeltItem();
					this.markDirty();
				}
			} else {
				this.cooktime = 0;
			}
			
			if (btu) {
				this.updateBlock();
				this.updateLight();
			}
		}
	}
	
	boolean canSmelt() {
		ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, false);
		if (ist == null) {
			return false;
		} else if (this.contents[10] == null) {
			return true;
		} else if (!this.contents[10].isItemEqual(ist)) {
			return false;
		} else {
			int st = this.contents[10].stackSize + ist.stackSize;
			return st <= this.getInventoryStackLimit() && st <= ist.getMaxStackSize();
		}
	}
	
	void smeltItem() {
		if (this.canSmelt()) {
			ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, true);
			if (this.contents[10] == null) {
				this.contents[10] = ist.copy();
			} else {
				this.contents[10].stackSize += ist.stackSize;
			}
			
		}
	}
	
	int getCookScaled(int i) {
		return this.cooktime * i / 200;
	}
	
	int getBurnScaled(int i) {
		return this.totalburn == 0 ? 0 : this.burntime * i / this.totalburn;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerBase.instance, 1, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
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
		for (int i = 0; i < 11; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
			}
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 11;
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
		return "AlloyFurnace";
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
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagList items = nbttagcompound.getTagList("Items", 10); //TODO: Arrgh! Check it out...
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		
		this.totalburn = nbttagcompound.getShort("TotalBurn");
		this.burntime = nbttagcompound.getShort("BurnTime");
		this.cooktime = nbttagcompound.getShort("CookTime");
		
		super.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
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
		nbttagcompound.setShort("TotalBurn", (short) this.totalburn);
		nbttagcompound.setShort("BurnTime", (short) this.burntime);
		nbttagcompound.setShort("CookTime", (short) this.cooktime);
		
		super.writeToNBT(nbttagcompound);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == 1) {
			return new int[]{};
		} else {
			int s = CoreLib.rotToSide(super.Rotation);
			return side == s ? new int[]{9} : (side == (s ^ 1) ? new int[]{1,2,3,4,5,6,7,8,9,10} : new int[]{});
		}
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		return slotID == 0;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return slotID >= 1 && slotID <= 10;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}
	
	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return false;
	}
}
