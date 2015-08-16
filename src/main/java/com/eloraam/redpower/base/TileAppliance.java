package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.TileExtended;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class TileAppliance extends TileExtended implements IHandlePackets, IFrameSupport {
	
	public int Rotation = 0;
	public boolean Active = false;
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.Rotation = MathHelper.floor_double((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		this.sendPacket();
		this.markDirty();
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerBase.blockAppliance;
	}
	
	public int getLightValue() {
		return this.Active ? 13 : 0;
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
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		byte k = nbttagcompound.getByte("ps");
		this.Rotation = nbttagcompound.getByte("rot");
		this.Active = k > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("ps", (byte) (this.Active ? 1 : 0));
		nbttagcompound.setByte("rot", (byte) this.Rotation);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
		int ps = buffer.readInt();
		this.Active = ps > 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
		int ps = this.Active ? 1 : 0;
		data.add(ps);
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
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		this.markDirty();
	}
}
