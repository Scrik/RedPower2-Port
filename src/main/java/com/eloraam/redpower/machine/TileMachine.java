package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileMachine extends TileExtended implements IHandlePackets, IRotatable, IFrameSupport {
	
	public int Rotation = 0;
	public boolean Active = false;
	public boolean Powered = false;
	public boolean Delay = false;
	public boolean Charged = false;
	
	public int getFacing(EntityLiving ent) {
		int yawrx = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if (Math.abs(ent.posX - (double) super.xCoord) < 2.0D && Math.abs(ent.posZ - (double) super.zCoord) < 2.0D) {
			double p = ent.posY + 1.82D - (double) ent.yOffset - (double) super.yCoord;
			if (p > 2.0D) {
				return 0;
			}
			if (p < 0.0D) {
				return 1;
			}
		}
		switch (yawrx) {
			case 0:
				return 3;
			case 1:
				return 4;
			case 2:
				return 2;
			default:
				return 5;
		}
	}
	
	protected boolean handleItem(TubeItem ti) {
		return MachineLib.handleItem(super.worldObj, ti, new WorldCoord(super.xCoord, super.yCoord, super.zCoord), this.Rotation);
	}
	
	public boolean isPoweringTo(int side) {
		return false;
	}
	
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : 5;
	}
	
	public int getPartRotation(int part, boolean sec) {
		return sec ? 0 : this.Rotation;
	}
	
	public void setPartRotation(int part, boolean sec, int rot) {
		if (!sec) {
			this.Rotation = rot;
			this.updateBlockChange();
		}
	}
	
	public void onBlockPlaced(ItemStack ist, int side, EntityLiving ent) {
		this.Rotation = this.getFacing(ent);
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
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
	
	public void onFrameRefresh(IBlockAccess iba) {
	}
	
	public void onFramePickup(IBlockAccess iba) {
	}
	
	public void onFrameDrop() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		byte k = nbttagcompound.getByte("ps");
		this.Rotation = nbttagcompound.getByte("rot");
		this.Active = (k & 1) > 0;
		this.Powered = (k & 2) > 0;
		this.Delay = (k & 4) > 0;
		this.Charged = (k & 8) > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
		nbttagcompound.setByte("ps", (byte) ps);
		nbttagcompound.setByte("rot", (byte) this.Rotation);
	}
	
	protected void readFromPacket(ByteBuf pkt) {
		this.Rotation = pkt.readInt();
		int ps = pkt.readInt();
		this.Active = (ps & 1) > 0;
		this.Powered = (ps & 2) > 0;
		this.Delay = (ps & 4) > 0;
		this.Charged = (ps & 8) > 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList pkt) {
		pkt.add(this.Rotation);
		int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
		pkt.add(ps);
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
		if(buffer.readInt() == 7) {
			this.readFromPacket(buffer);
			super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		}
	}
}
