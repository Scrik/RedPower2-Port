package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.TileCoverable;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileCovered extends TileCoverable implements IHandlePackets, IFrameSupport {
	
	public int CoverSides = 0;
	public short[] Covers = new short[29];
	
	public void replaceWithCovers() {
		CoverLib.replaceWithCovers(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, this.CoverSides, this.Covers);
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void onBlockNeighborChange(Block l) {
		if (this.CoverSides == 0) {
			this.deleteBlock();
		}
		
	}
	
	@Override
	public Block getBlockType() {
		return CoverLib.blockCoverPlate;
	}
	
	@Override
	public boolean canAddCover(int side, int cover) {
		if ((this.CoverSides & 1 << side) > 0) {
			return false;
		} else {
			short[] test = Arrays.copyOf(this.Covers, 29);
			test[side] = (short) cover;
			return CoverLib.checkPlacement(this.CoverSides | 1 << side, test,
					0, false);
		}
	}
	
	@Override
	public boolean tryAddCover(int side, int cover) {
		if (!this.canAddCover(side, cover)) {
			return false;
		} else {
			this.CoverSides |= 1 << side;
			this.Covers[side] = (short) cover;
			this.updateBlockChange();
			return true;
		}
	}
	
	@Override
	public int tryRemoveCover(int side) {
		if ((this.CoverSides & 1 << side) == 0) {
			return -1;
		} else {
			this.CoverSides &= ~(1 << side);
			short tr = this.Covers[side];
			this.Covers[side] = 0;
			this.updateBlockChange();
			return tr;
		}
	}
	
	@Override
	public int getCover(int side) {
		return (this.CoverSides & 1 << side) == 0 ? -1 : this.Covers[side];
	}
	
	@Override
	public int getCoverMask() {
		return this.CoverSides;
	}
	
	@Override
	public boolean blockEmpty() {
		return this.CoverSides == 0;
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
		int cs2 = nbttagcompound.getInteger("cvm") & 536870911;
		this.CoverSides |= cs2;
		byte[] cov = nbttagcompound.getByteArray("cvs");
		if (cov != null && cs2 > 0) {
			int sp = 0;
			
			for (int i = 0; i < 29; ++i) {
				if ((cs2 & 1 << i) != 0) {
					this.Covers[i] = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
					sp += 2;
				}
			}
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("cvm", this.CoverSides);
		byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
		int dp = 0;
		
		for (int i = 0; i < 29; ++i) {
			if ((this.CoverSides & 1 << i) != 0) {
				cov[dp] = (byte) (this.Covers[i] & 255);
				cov[dp + 1] = (byte) (this.Covers[i] >> 8);
				dp += 2;
			}
		}
		
		nbttagcompound.setByteArray("cvs", cov);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<?> getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(5);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 5) {
			this.readFromPacket(buffer);
		}
	}
	
	protected void readFromPacket(ByteBuf pkt) {
		if (pkt.readInt() == 5) {
			this.CoverSides = pkt.readInt();
			for (int i = 0; i < 29; ++i) {
				if ((this.CoverSides & 1 << i) > 0) {
					this.Covers[i] = (short)pkt.readInt();
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void writeToPacket(ArrayList data) {
		data.add((int)5);
		data.add(this.CoverSides);
		for (int i = 0; i < 29; ++i) {
			if ((this.CoverSides & 1 << i) > 0) {
				data.add((int)this.Covers[i]);
			}
		}
	}
	
	@Override
	public void handlePacketData(ByteBuf data) {
		this.readFromPacket(data);
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		this.writeToPacket(data);
		return data;
	}
}
