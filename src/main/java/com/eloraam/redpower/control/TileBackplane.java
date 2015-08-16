package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.TileMultipart;
import com.eloraam.redpower.core.WorldCoord;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class TileBackplane extends TileMultipart implements IHandlePackets, IFrameSupport {
	
	public int Rotation = 0;
	
	public int readBackplane(int addr) {
		return 255;
	}
	
	public void writeBackplane(int addr, int val) {
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerControl.blockBackplane;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void onBlockNeighborChange(Block l) {
		if (!super.worldObj.getBlock(super.xCoord, super.yCoord - 1, super.zCoord).isSideSolid(super.worldObj, super.xCoord, super.yCoord - 1, super.zCoord, ForgeDirection.UP)) {
			this.breakBlock();
		} else {
			WorldCoord wc = new WorldCoord(this);
			wc.step(CoreLib.rotToSide(this.Rotation) ^ 1);
			Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
			int md = super.worldObj.getBlockMetadata(wc.x, wc.y, wc.z);
			if (bid != RedPowerControl.blockBackplane) {
				if (bid != RedPowerControl.blockPeripheral || md != 1) {
					this.breakBlock();
				}
			}
		}
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		//super.addHarvestContents(ist);
		ist.add(new ItemStack(RedPowerControl.blockBackplane, 1, 0));
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		this.breakBlock();
	}
	
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		return 0.1F;
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}
	
	@Override
	public int getSolidPartsMask() {
		return 1;
	}
	
	@Override
	public int getPartsMask() {
		return 1;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getFramePacket() {
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
		this.Rotation = nbttagcompound.getByte("rot");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("rot", (byte) this.Rotation);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
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
	}
}
