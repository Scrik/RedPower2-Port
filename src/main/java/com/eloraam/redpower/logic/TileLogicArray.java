package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public class TileLogicArray extends TileLogic implements IRedPowerWiring {
	
	public short PowerVal1 = 0;
	public short PowerVal2 = 0;
	
	@Override
	public int getPoweringMask(int ch) {
		if (ch != 0) {
			return 0;
		} else {
			int tr = 0;
			if (this.PowerVal1 > 0) {
				tr |= RedPowerLib.mapRotToCon(10, super.Rotation);
			}
			
			if (this.PowerVal2 > 0) {
				tr |= RedPowerLib.mapRotToCon(5, super.Rotation);
			}
			
			return tr;
		}
	}
	
	@Override
	public void updateCurrentStrength() {
		this.PowerVal2 = (short) RedPowerLib.updateBlockCurrentStrength(
				super.worldObj, this, super.xCoord, super.yCoord, super.zCoord,
				RedPowerLib.mapRotToCon(5, super.Rotation), 1);
		this.PowerVal1 = (short) RedPowerLib.updateBlockCurrentStrength(
				super.worldObj, this, super.xCoord, super.yCoord, super.zCoord,
				RedPowerLib.mapRotToCon(10, super.Rotation), 1);
		CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord);
	}
	
	@Override
	public int getCurrentStrength(int cons, int ch) {
		return ch != 0 ? -1 : ((RedPowerLib.mapRotToCon(5, super.Rotation) & cons) > 0 ? this.PowerVal2 : ((RedPowerLib
				.mapRotToCon(10, super.Rotation) & cons) > 0 ? this.PowerVal1 : -1));
	}
	
	@Override
	public int scanPoweringStrength(int cons, int ch) {
		if (ch != 0) {
			return 0;
		} else {
			int r1 = RedPowerLib.mapRotToCon(5, super.Rotation);
			int r2 = RedPowerLib.mapRotToCon(10, super.Rotation);
			return (r1 & cons) > 0 ? (super.Powered ? 255 : (RedPowerLib
					.isPowered(super.worldObj, super.xCoord, super.yCoord,
							super.zCoord, r1 & cons, 0) ? 255 : 0)) : ((r2 & cons) > 0 ? (RedPowerLib
					.isPowered(super.worldObj, super.xCoord, super.yCoord,
							super.zCoord, r2 & cons, 0) ? 255 : 0) : 0);
		}
	}
	
	@Override
	public int getConnectionMask() {
		return RedPowerLib.mapRotToCon(15, super.Rotation);
	}
	
	@Override
	public int getExtConnectionMask() {
		return 0;
	}
	
	public int getTopwireMask() {
		return RedPowerLib.mapRotToCon(5, super.Rotation);
	}
	
	private boolean cellWantsPower() {
		return super.SubId == 1 ? super.PowerState == 0 : super.PowerState != 0;
	}
	
	private void updatePowerState() {
		super.PowerState = this.PowerVal1 > 0 ? 1 : 0;
		if (this.cellWantsPower() != super.Powered) {
			this.scheduleTick(2);
		}
		
	}
	
	@Override
	public int getExtendedID() {
		return 2;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (!this.tryDropBlock()) {
			RedPowerLib.updateCurrent(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord);
			if (super.SubId != 0) {
				if (!this.isTickRunnable()) {
					this.updatePowerState();
				}
			}
		}
	}
	
	@Override
	public boolean isBlockStrongPoweringTo(int l) {
		return RedPowerLib.isSearching() ? false : (this.getPoweringMask(0) & RedPowerLib
				.getConDirMask(l ^ 1)) > 0;
	}
	
	@Override
	public boolean isBlockWeakPoweringTo(int l) {
		return RedPowerLib.isSearching() ? false : (this.getPoweringMask(0) & RedPowerLib
				.getConDirMask(l ^ 1)) > 0;
	}
	
	@Override
	public void onTileTick() {
		if (super.Powered != this.cellWantsPower()) {
			super.Powered = !super.Powered;
			this.updateBlockChange();
			this.updatePowerState();
		}
		
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		if (part != super.Rotation >> 2) {
			super.setPartBounds(bl, part);
		} else {
			switch (part) {
				case 0:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
					break;
				case 1:
					bl.setBlockBounds(0.0F, 0.15F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;
				case 2:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
					break;
				case 3:
					bl.setBlockBounds(0.0F, 0.0F, 0.15F, 1.0F, 1.0F, 1.0F);
					break;
				case 4:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
					break;
				case 5:
					bl.setBlockBounds(0.15F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.PowerVal1 = (short) (tag.getByte("pv1") & 255);
		this.PowerVal2 = (short) (tag.getByte("pv2") & 255);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("pv1", (byte) this.PowerVal1);
		tag.setByte("pv2", (byte) this.PowerVal2);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		if (buffer.readInt() == 6) {
			this.PowerVal1 = buffer.readShort();
			this.PowerVal2 = buffer.readShort();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add(6);
		data.add(this.PowerVal1);
		data.add(this.PowerVal2);
	}
}
