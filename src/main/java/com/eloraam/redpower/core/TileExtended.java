package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.network.INetworkDataProvider;
import com.eloraam.redpower.network.PacketTileEntityUpdate;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public abstract class TileExtended extends TileEntity implements INetworkDataProvider {
	
	protected long timeSched = -1L;
	protected int updateRange = 16;
	protected int updateDelay = 20;
	
	public void onBlockNeighborChange(Block block) {
	}
	
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.sendPacket();
		this.markDirty();
	}
	
	@SuppressWarnings("rawtypes")
	public void sendPacket() {
		if(this != null && !this.worldObj.isRemote) {
			RedPowerCore.packetHandler.sendToReceivers(new PacketTileEntityUpdate.TileEntityMessage(DimCoord.get(this), getNetworkedData(new ArrayList())), Range4D.getChunkRange(this));
		}
	}
	
	public void onBlockRemoval() {
	}
	
	public boolean isBlockStrongPoweringTo(int side) {
		return false;
	}
	
	public boolean isBlockWeakPoweringTo(int side) {
		return this.isBlockStrongPoweringTo(side);
	}
	
	public boolean onBlockActivated(EntityPlayer player) {
		this.sendPacket();
		return false;
	}
	
	public void onEntityCollidedWithBlock(Entity ent) {
	}
	
	public AxisAlignedBB getCollisionBoundingBox() {
		return null;
	}
	
	public void onTileTick() {
	}
	
	public int getExtendedID() {
		return 0;
	}
	
	public int getExtendedMetadata() {
		return 0;
	}
	
	public void setExtendedMetadata(int md) {
	}
	
	public void addHarvestContents(ArrayList<ItemStack> ist) {
		ist.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID()));
	}
	
	public void scheduleTick(int time) {
		long tn = super.worldObj.getWorldTime() + time;
		if (this.timeSched <= 0L || this.timeSched >= tn) {
			this.timeSched = tn;
			this.markDirty();
		}
	}
	
	public boolean isTickRunnable() {
		return this.timeSched >= 0L && this.timeSched <= super.worldObj.getWorldTime();
	}
	
	public boolean isTickScheduled() {
		return this.timeSched >= 0L;
	}
	
	public void updateBlockChange() {
		RedPowerLib.updateIndirectNeighbors(super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.getBlockType());
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		//CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
		this.markDirty();
		this.sendPacket();
	}
	
	public void updateBlock() {
		//int md = super.worldObj.getBlockMetadata(super.xCoord, super.yCoord, super.zCoord);
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		//CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
		this.markDirty();
	}
	
	/*public void dirtyBlock() {
		CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
	}*/
	
	public void breakBlock() {
		ArrayList<ItemStack> il = new ArrayList<ItemStack>();
		this.addHarvestContents(il);
		Iterator<ItemStack> iter = il.iterator();
		while (iter.hasNext()) {
			ItemStack it = iter.next();
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, it);
		}
		super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
	}
	
	@Override
	public void updateEntity() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.timeSched >= 0L) {
				long wtime = super.worldObj.getWorldTime();
				if (this.timeSched > wtime + 1200L) {
					this.timeSched = wtime + 1200L;
				} else if (this.timeSched <= wtime) {
					this.timeSched = -1L;
					this.onTileTick();
					this.markDirty();
				}
			}
			
			updateDelay--;
			if(updateDelay == 0) {
				this.sendPacket();
				updateDelay = 20;
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.timeSched = nbttagcompound.getLong("sched");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setLong("sched", this.timeSched);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		return data;
	}
}
