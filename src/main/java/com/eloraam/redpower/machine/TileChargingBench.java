package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IChargeable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.machine.TileChargingBench;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileChargingBench extends TileAppliance implements IInventory, IBluePowerConnectable {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileChargingBench.this;
		}
	};
	public boolean Powered = false;
	public int Storage = 0;
	private ItemStack[] contents = new ItemStack[16];
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
	
	@Override
	public int getLightValue() {
		return 0;
	}
	
	@Override
	public int getExtendedID() {
		return 5;
	}
	
	public int getMaxStorage() {
		return 3000;
	}
	
	public int getStorageForRender() {
		return this.Storage * 4 / this.getMaxStorage();
	}
	
	public int getChargeScaled(int i) {
		return Math.min(i, i * this.cond.Charge / 1000);
	}
	
	public int getStorageScaled(int i) {
		return Math.min(i, i * this.Storage / this.getMaxStorage());
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.cond.Flow == 0) {
				if (this.Powered) {
					this.Powered = false;
					this.updateBlock();
				}
			} else if (!this.Powered) {
				this.Powered = true;
				this.updateBlock();
			}
			
			int rs = this.getStorageForRender();
			if (this.cond.Charge > 600 && this.Storage < this.getMaxStorage()) {
				int lastact = Math.min((this.cond.Charge - 600) / 40, 5);
				lastact = Math.min(lastact, this.getMaxStorage() - this.Storage);
				this.cond.drawPower(lastact * 1000);
				this.Storage += lastact;
			}
			
			boolean var5 = super.Active;
			super.Active = false;
			if (this.Storage > 0) {
				for (int i = 0; i < 16; ++i) {
					if (this.contents[i] != null
							&& this.contents[i].getItem() instanceof IChargeable
							&& this.contents[i].getItemDamage() > 1) {
						int d = Math.min(this.contents[i].getItemDamage() - 1, this.Storage);
						d = Math.min(d, 25);
						this.contents[i].setItemDamage(this.contents[i].getItemDamage() - d);
						this.Storage -= d;
						this.markDirty();
						super.Active = true;
					}
				}
			}
			
			if (rs != this.getStorageForRender() || var5 != super.Active) {
				this.updateBlock();
			}
			
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 14, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	}
	
	@Override
	public void onBlockRemoval() {
		for (int i = 0; i < 2; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
			}
		}
		
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
	}
	
	@Override
	public int getSizeInventory() {
		return 16;
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
		return "Charging Bench";
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10); //TODO: 
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int k = 0; k < items.tagCount(); ++k) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(k);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		
		this.cond.readFromNBT(tag);
		this.Storage = tag.getShort("stor");
		byte var6 = tag.getByte("ps");
		this.Powered = (var6 & 1) > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList items = new NBTTagList();
		
		int ps;
		for (ps = 0; ps < this.contents.length; ++ps) {
			if (this.contents[ps] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) ps);
				this.contents[ps].writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		tag.setTag("Items", items);
		this.cond.writeToNBT(tag);
		tag.setShort("stor", (short) this.Storage);
		ps = this.Powered ? 1 : 0;
		tag.setByte("ps2", (byte) ps);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.Rotation = buffer.readInt();
		int ps = buffer.readInt();
		super.Active = (ps & 1) > 0;
		this.Powered = (ps & 2) > 0;
		this.Storage = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		data.add(super.Rotation);
		int ps = (super.Active ? 1 : 0) | (this.Powered ? 2 : 0);
		data.add(ps);
		data.add(this.Storage);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return itemStack.getItem() instanceof IChargeable;
	}
}
