package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileTube;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileFrameTube extends TileTube implements IFrameLink {
	
	public int StickySides = 63;
	
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
		return 2;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockFrame;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		//boolean change = false;
		if (part == 29) {
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, new ItemStack(this.getBlockType(), 1, 2));
			super.flow.onRemove();
			if (super.CoverSides > 0) {
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
	public void addHarvestContents(List<ItemStack> ist) {
		this.addCoverableHarvestContents(ist);
		ist.add(new ItemStack(this.getBlockType(), 1, 2));
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
			return n != 0 && n != 1 && n != 3 && n != 4 ? false : (super.CoverSides & 1 << side) <= 0;
		}
	}
	
	void rebuildSticky() {
		int ss = 0;
		for (int i = 0; i < 6; ++i) {
			int m = 1 << i;
			if ((super.CoverSides & m) == 0) {
				ss |= m;
			} else {
				int n = super.Covers[i] >> 8;
				if (n == 1 || n == 4) {
					ss |= m;
				}
			}
		}
		this.StickySides = ss;
	}
	
	@Override
	public boolean tryAddCover(int side, int cover) {
		if (!super.tryAddCover(side, cover)) {
			return false;
		} else {
			this.rebuildSticky();
			return true;
		}
	}
	
	@Override
	public int tryRemoveCover(int side) {
		int tr = super.tryRemoveCover(side);
		if (tr < 0) {
			return tr;
		} else {
			this.rebuildSticky();
			return tr;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.rebuildSticky();
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		this.rebuildSticky();
	}
}
