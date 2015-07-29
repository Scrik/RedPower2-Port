package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.TileCoverable;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.BlockMachine;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileFrame extends TileCoverable implements IHandlePackets, IFrameLink, IFrameSupport {
	
	public int CoverSides = 0;
	public int StickySides = 63;
	public short[] Covers = new short[6];
	
	@Override
	public boolean isFrameMoving() {
		return false;
	}
	
	@Override
	public boolean canFrameConnectIn(int dir) {
		return (this.StickySides & 1 << dir) > 0;
	}
	
	@Override
	public boolean canFrameConnectOut(int dir) {
		return (this.StickySides & 1 << dir) > 0;
	}
	
	@Override
	public WorldCoord getFrameLinkset() {
		return null;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockFrame;
	}
	
	@Override
	public int getPartsMask() {
		return this.CoverSides | 536870912;
	}
	
	@Override
	public int getSolidPartsMask() {
		return this.CoverSides | 536870912;
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		//boolean change = false;
		if (part == 29) {
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
					super.zCoord, new ItemStack(RedPowerMachine.blockFrame, 1));
			if (this.CoverSides > 0) {
				this.replaceWithCovers();
				this.updateBlockChange();
			} else {
				this.deleteBlock();
			}
			
		} else {
			super.onHarvestPart(player, part);
		}
	}
	
	@Override
	public void addHarvestContents(ArrayList<ItemStack> ist) {
		super.addHarvestContents(ist);
		ist.add(new ItemStack(RedPowerMachine.blockFrame, 1));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		BlockMachine bl = RedPowerMachine.blockMachine;
		return part == 29 ? player.getBreakSpeed(bl, false, 0)
				/ (bl.getHardness() * 30.0F) : super.getPartStrength(player, part);
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		if (part == 29) {
			bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			super.setPartBounds(bl, part);
		}
		
	}
	
	@Override
	public boolean canAddCover(int side, int cover) {
		if (side > 5) {
			return false;
		} else {
			int n = cover >> 8;
			return n != 0 && n != 1 && n != 3 && n != 4 ? false : (this.CoverSides & 1 << side) <= 0;
		}
	}
	
	void rebuildSticky() {
		int ss = 0;
		
		for (int i = 0; i < 6; ++i) {
			int m = 1 << i;
			if ((this.CoverSides & m) == 0) {
				ss |= m;
			} else {
				int n = this.Covers[i] >> 8;
				if (n == 1 || n == 4) {
					ss |= m;
				}
			}
		}
		
		this.StickySides = ss;
	}
	
	@Override
	public boolean tryAddCover(int side, int cover) {
		if (!this.canAddCover(side, cover)) {
			return false;
		} else {
			this.CoverSides |= 1 << side;
			this.Covers[side] = (short) cover;
			this.rebuildSticky();
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
			this.rebuildSticky();
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
	
	public void replaceWithCovers() {
		short[] covs = Arrays.copyOf(this.Covers, 29);
		CoverLib.replaceWithCovers(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, this.CoverSides, covs);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(9);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 9) {
			this.readFromPacket(buffer);
		}
	}
	
	@Override
	public void onFramePickup(IBlockAccess iba) {
	}
	
	@Override
	public void onFrameRefresh(IBlockAccess iba) {
	}
	
	@Override
	public void onFrameDrop() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		int cs2 = nbttagcompound.getInteger("cvm") & 63;
		this.CoverSides |= cs2;
		byte[] cov = nbttagcompound.getByteArray("cvs");
		if (cov != null && cs2 > 0) {
			int sp = 0;
			
			for (int i = 0; i < 6; ++i) {
				if ((cs2 & 1 << i) != 0) {
					this.Covers[i] = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
					sp += 2;
				}
			}
		}
		
		this.rebuildSticky();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("cvm", this.CoverSides);
		byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
		int dp = 0;
		
		for (int i = 0; i < 6; ++i) {
			if ((this.CoverSides & 1 << i) != 0) {
				cov[dp] = (byte) (this.Covers[i] & 255);
				cov[dp + 1] = (byte) (this.Covers[i] >> 8);
				dp += 2;
			}
		}
		nbttagcompound.setByteArray("cvs", cov);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		if (buffer.readInt() == 9) {
			this.CoverSides = buffer.readInt();
			for (int i = 0; i < 6; ++i) {
				if ((this.CoverSides & 1 << i) > 0) {
					this.Covers[i] = (short)buffer.readInt();
				}
			}
			this.rebuildSticky();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.CoverSides);
		for (int i = 0; i < 6; ++i) {
			if ((this.CoverSides & 1 << i) > 0) {
				data.add((int)this.Covers[i]);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		data.add(9);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		this.readFromPacket(buffer);
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
	}
}
