package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileRAM extends TileBackplane {
	
	public byte[] memory = new byte[8192];
	
	@Override
	public int readBackplane(int addr) {
		return this.memory[addr] & 255;
	}
	
	@Override
	public void writeBackplane(int addr, int val) {
		this.memory[addr] = (byte) val;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerControl.blockBackplane;
	}
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
	@Override
	public void addHarvestContents(ArrayList<ItemStack> ist) {
		super.addHarvestContents(ist);
		ist.add(new ItemStack(RedPowerControl.blockBackplane, 1, 1));
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, new ItemStack(RedPowerControl.blockBackplane, 1,
						1));
		BlockMultipart.removeMultipart(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord);
		super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, RedPowerControl.blockBackplane);
		TileBackplane tb = (TileBackplane) CoreLib.getTileEntity(
				super.worldObj, super.xCoord, super.yCoord, super.zCoord,
				TileBackplane.class);
		if (tb != null) {
			tb.Rotation = super.Rotation;
		}
		
		this.updateBlockChange();
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		if (part == 0) {
			super.setPartBounds(bl, part);
		} else {
			bl.setBlockBounds(0.0F, 0.125F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		
	}
	
	@Override
	public int getSolidPartsMask() {
		return 3;
	}
	
	@Override
	public int getPartsMask() {
		return 3;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.memory = nbttagcompound.getByteArray("ram");
		if (this.memory.length != 8192) {
			this.memory = new byte[8192];
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByteArray("ram", this.memory);
	}
}
