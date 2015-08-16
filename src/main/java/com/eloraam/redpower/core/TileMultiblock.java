package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerCore;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileMultiblock extends TileEntity implements IHandlePackets, INetworkDataProvider {
	
	public int relayX;
	public int relayY;
	public int relayZ;
	public int relayNum;
	protected int updateRange = 25;
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerBase.blockMultiblock;
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
	}
	
	@SuppressWarnings("rawtypes")
	public void sendPacket() {
		RedPowerCore.packetHandler.sendToAllAround(
				new PacketTileEntityUpdate.TileEntityMessage(DimCoord.get(this),
						getNetworkedData(new ArrayList())), new TargetPoint(
						super.worldObj.provider.dimensionId, super.xCoord,
						super.yCoord, super.zCoord, this.updateRange));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.relayX = tag.getInteger("rlx");
		this.relayY = tag.getInteger("rly");
		this.relayZ = tag.getInteger("rlz");
		this.relayNum = tag.getInteger("rln");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("rlx", this.relayX);
		tag.setInteger("rly", this.relayY);
		tag.setInteger("rlz", this.relayZ);
		tag.setInteger("rln", this.relayNum);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.relayX = buffer.readInt();
		this.relayY = buffer.readInt();
		this.relayZ = buffer.readInt();
		this.relayNum = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.relayX);
		data.add(this.relayY);
		data.add(this.relayZ);
		data.add(this.relayNum);
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
	}
}
