package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.machine.TileBatteryBox;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class TileBatteryBox extends TileExtended implements IHandlePackets, IInventory, IBluePowerConnectable, ISidedInventory, IFrameSupport {
	
	BluePowerConductor cond = new BluePowerConductor() {
		@Override
		public TileEntity getParent() {
			return TileBatteryBox.this;
		}
		
		@Override
		public double getInvCap() {
			return 0.25D;
		}
	};
	protected ItemStack[] contents = new ItemStack[2];
	public int Charge = 0;
	public int Storage = 0;
	public int ConMask = -1;
	public boolean Powered = false;
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 65;
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
	public int[] getAccessibleSlotsFromSide(int side) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = (side == 0 ? 1 : 0); i < (side >= 2 ? 0 : 1); i ++) {
			list.add(i);
		}
		return CoreLib.toIntArray(list);
	}
	
	@Override
	public void addHarvestContents(ArrayList<ItemStack> ist) {
		ItemStack is = new ItemStack(this.getBlockType(), 1, this.getExtendedID());
		if (this.Storage > 0) {
			is.setTagCompound(new NBTTagCompound());
			is.stackTagCompound.setShort("batLevel", (short) this.Storage);
		}
		ist.add(is);
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		if (ist.stackTagCompound != null) {
			this.Storage = ist.stackTagCompound.getShort("batLevel");
		}
	}
	
	@Override
	public int getExtendedID() {
		return 6;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
	}
	
	public int getMaxStorage() {
		return 6000;
	}
	
	public int getStorageForRender() {
		return this.Storage * 8 / this.getMaxStorage();
	}
	
	public int getChargeScaled(int i) {
		return Math.min(i, i * this.Charge / 1000);
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
			this.Charge = (int) (this.cond.getVoltage() * 10.0D);
			int rs = this.getStorageForRender();
			int n;
			if (this.contents[0] != null && this.Storage > 0) {
				if (this.contents[0].getItem() == RedPowerMachine.itemBatteryEmpty) {
					this.contents[0] = new ItemStack(
							RedPowerMachine.itemBatteryPowered, 1,
							RedPowerMachine.itemBatteryPowered.getMaxDamage());
					this.markDirty();
				}
				
				if (this.contents[0].getItem() == RedPowerMachine.itemBatteryPowered) {
					n = Math.min(this.contents[0].getItemDamage() - 1, this.Storage);
					n = Math.min(n, 25);
					this.Storage -= n;
					this.contents[0].setItemDamage(this.contents[0].getItemDamage() - n);
					this.markDirty();
				}
			}
			
			if (this.contents[1] != null && this.contents[1].getItem() == RedPowerMachine.itemBatteryPowered) {
				n = Math.min(
						this.contents[1].getMaxDamage() - this.contents[1].getItemDamage(),
						this.getMaxStorage() - this.Storage);
				n = Math.min(n, 25);
				this.Storage += n;
				this.contents[1].setItemDamage(this.contents[1].getItemDamage() + n);
				if (this.contents[1].getItemDamage() == this.contents[1].getMaxDamage()) {
					this.contents[1] = new ItemStack(
							RedPowerMachine.itemBatteryEmpty, 1);
				}
				
				this.markDirty();
			}
			
			if (this.Charge > 900 && this.Storage < this.getMaxStorage()) {
				n = Math.min((this.Charge - 900) / 10, 10);
				n = Math.min(n, this.getMaxStorage() - this.Storage);
				this.cond.drawPower(n * 1000);
				this.Storage += n;
			} else if (this.Charge < 800 && this.Storage > 0 && !this.Powered) {
				n = Math.min((800 - this.Charge) / 10, 10);
				n = Math.min(n, this.Storage);
				this.cond.applyPower(n * 1000);
				this.Storage -= n;
			}
			
			if (rs != this.getStorageForRender()) {
				this.updateBlock();
			}
			
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 2;
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
		return "Battery Box";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
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
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, 16777215, 63)) {
			if (!this.Powered) {
				this.Powered = true;
				this.markDirty();
			}
		} else if (this.Powered) {
			this.Powered = false;
			this.markDirty();
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 8, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockRemoval() {
		super.onBlockRemoval();
		
		for (int i = 0; i < 2; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(7);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 7) {
			this.readFromPacket(buffer);
		}
	}
	
	@Override
	public void onFrameRefresh(IBlockAccess iba) {
	}
	
	@Override
	public void onFramePickup(IBlockAccess iba) {
	}
	
	@Override
	public void onFrameDrop() {
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
		this.Charge = tag.getShort("chg");
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
		tag.setShort("chg", (short) this.Charge);
		tag.setShort("stor", (short) this.Storage);
		ps = this.Powered ? 1 : 0;
		tag.setByte("ps", (byte) ps);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Storage = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Storage);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		data.add(7);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		try {
			if (buffer.readInt() != 7) {
				return;
			}
			this.readFromPacket(buffer);
		} catch (Throwable thr) {}
		this.updateBlock();
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
