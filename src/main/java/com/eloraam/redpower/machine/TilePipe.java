package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FluidBuffer;
import com.eloraam.redpower.core.IPipeConnectable;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.BlockMachine;
import com.eloraam.redpower.machine.TilePipe;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class TilePipe extends TileCovered implements IPipeConnectable {
	
	public FluidBuffer pipebuf = new FluidBuffer() {
		@Override
		public TileEntity getParent() {
			return TilePipe.this;
		}
		
		@Override
		public void onChange() {
			TilePipe.this.markDirty();
		}
	};
	public int Pressure = 0;
	public int ConCache = -1;
	public int Flanges = -1;
	//private boolean hasChanged = false;
	
	@Override
	public int getPipeConnectableSides() {
		int tr = 63;
		
		for (int i = 0; i < 6; ++i) {
			if ((super.CoverSides & 1 << i) > 0 && super.Covers[i] >> 8 < 3) {
				tr &= ~(1 << i);
			}
		}
		
		return tr;
	}
	
	@Override
	public int getPipeFlangeSides() {
		this.cacheCon();
		return this.ConCache != 3 && this.ConCache != 12 && this.ConCache != 48 ? (Integer
				.bitCount(this.ConCache) == 1 ? 0 : this.ConCache) : 0;
	}
	
	@Override
	public int getPipePressure(int side) {
		return this.Pressure;
	}
	
	@Override
	public FluidBuffer getPipeBuffer(int side) {
		return this.pipebuf;
	}
	
	@Override
	public boolean tryAddCover(int side, int cover) {
		if (!super.tryAddCover(side, cover)) {
			return false;
		} else {
			this.uncache();
			this.updateBlockChange();
			return true;
		}
	}
	
	@Override
	public int tryRemoveCover(int side) {
		int tr = super.tryRemoveCover(side);
		if (tr < 0) {
			return -1;
		} else {
			this.uncache();
			this.updateBlockChange();
			return tr;
		}
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			int pr = 0;
			int d = 0;
			int min = 0;
			int max = 0;
			this.cacheCon();
			
			for (int i = 0; i < 6; ++i) {
				if ((this.ConCache & 1 << i) != 0) {
					WorldCoord wc = new WorldCoord(this);
					wc.step(i);
					Integer p = PipeLib.getPressure(super.worldObj, wc, i ^ 1);
					if (p != null) {
						min = Math.min(p.intValue(), min);
						max = Math.max(p.intValue(), max);
						pr += p.intValue();
						++d;
					}
				}
			}
			
			if (d == 0) {
				this.Pressure = 0;
			} else {
				if (min < 0) {
					++min;
				}
				
				if (max > 0) {
					--max;
				}
				
				this.Pressure = Math.max(min,
						Math.min(max, pr / d + Integer.signum(pr)));
			}
			
			PipeLib.movePipeLiquid(super.worldObj, this, new WorldCoord(this),
					this.ConCache);
			this.markDirty();
			if (!CoreLib.isClient(super.worldObj)
					&& (super.worldObj.getWorldTime() & 16L) == 0L) {
				this.sendItemUpdate();
			}
			
		}
	}
	
	public void uncache() {
		this.ConCache = -1;
		this.Flanges = -1;
	}
	
	public void cacheCon() {
		if (this.ConCache < 0) {
			this.ConCache = PipeLib.getConnections(super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
		}
		
	}
	
	public void cacheFlange() {
		if (this.Flanges < 0) {
			this.cacheCon();
			this.Flanges = this.getPipeFlangeSides();
			this.Flanges |= PipeLib.getFlanges(super.worldObj, new WorldCoord(
					this), this.ConCache);
		}
	}
	
	@Override
	public void onFrameRefresh(IBlockAccess iba) {
		if (this.ConCache < 0) {
			this.ConCache = PipeLib.getConnections(iba, super.xCoord,
					super.yCoord, super.zCoord);
		}
		
		this.Flanges = 0;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerBase.blockMicro;
	}
	
	@Override
	public int getExtendedID() {
		return 7;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		int cfl = this.Flanges;
		int ccc = this.ConCache;
		this.uncache();
		this.cacheFlange();
		if (this.Flanges != cfl || ccc != this.ConCache) {
			this.updateBlock();
		}
		
	}
	
	@Override
	public int getPartsMask() {
		return super.CoverSides | 536870912;
	}
	
	@Override
	public int getSolidPartsMask() {
		return super.CoverSides | 536870912;
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		if (part == 29) {
			CoreLib.dropItem(
					super.worldObj,
					super.xCoord,
					super.yCoord,
					super.zCoord,
					new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
			if (super.CoverSides > 0) {
				this.replaceWithCovers();
			} else {
				this.deleteBlock();
			}
			
			this.uncache();
			this.updateBlockChange();
		} else {
			super.onHarvestPart(player, part);
		}
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		super.addHarvestContents(ist);
		ist.add(new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
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
			bl.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
		} else {
			super.setPartBounds(bl, part);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.Pressure = tag.getInteger("psi");
		this.pipebuf.readFromNBT(tag, "buf");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("psi", this.Pressure);
		this.pipebuf.writeToNBT(tag, "buf");
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) { //TODO: Catch this
		int i = 0;
		try {
			i = buffer.readInt();
		} catch (Throwable thr)  {}
		if (i == 11) {
			this.pipebuf.readFromPacket(buffer);
			this.updateBlock();
		} else {
			super.readFromPacket(buffer);
			this.pipebuf.readFromPacket(buffer);
			this.ConCache = -1;
			this.Flanges = -1;
			this.updateBlock();
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		this.pipebuf.writeToPacket(data);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sendItemUpdate() {
		ArrayList data = new ArrayList();
		data.add(11);
		this.pipebuf.writeToPacket(data);
		this.markDirty(); //TODO: This sends packet to around
		//CoreProxy.sendPacketToPosition(super.worldObj, pkt, super.xCoord, super.zCoord);
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		this.readFromPacket(buffer);
	}
}
