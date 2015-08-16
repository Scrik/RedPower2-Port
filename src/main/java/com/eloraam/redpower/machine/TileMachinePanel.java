package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;
import com.eloraam.redpower.machine.BlockMachinePanel;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;

public class TileMachinePanel extends TileMultipart implements IHandlePackets, IRotatable, IFrameSupport {
	
	public int Rotation = 0;
	public boolean Active = false;
	public boolean Powered = false;
	public boolean Delay = false;
	public boolean Charged = false;
	
	public int getLightValue() {
		return 0;
	}
	
	void updateLight() {
		super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord); //TODO: Костыль
	}
	
	public int getFacing(EntityLivingBase ent) {
		int yawrx = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (Math.abs(ent.posX - super.xCoord) < 2.0D
				&& Math.abs(ent.posZ - super.zCoord) < 2.0D) {
			double p = ent.posY + 1.82D - ent.yOffset - super.yCoord;
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
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		RedPowerLib.updateIndirectNeighbors(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.blockType);
		this.sendPacket();
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachinePanel;
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		ist.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID()));
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		this.breakBlock();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		BlockMachinePanel bl = RedPowerMachine.blockMachinePanel;
		return player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F);
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public int getSolidPartsMask() {
		return 0x1;
	}
	
	@Override
	public int getPartsMask() {
		return 0x1;
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : 3;
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		return sec ? 0 : this.Rotation;
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (!sec) {
			this.Rotation = rot;
			this.updateBlockChange();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList<?> getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(8);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 8) {
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		byte k = tag.getByte("ps");
		this.Rotation = tag.getByte("rot");
		this.Active = (k & 1) > 0;
		this.Powered = (k & 2) > 0;
		this.Delay = (k & 4) > 0;
		this.Charged = (k & 8) > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
		tag.setByte("ps", (byte) ps);
		tag.setByte("rot", (byte) this.Rotation);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
		int ps = buffer.readByte();
		this.Active = (ps & 1) > 0;
		this.Powered = (ps & 2) > 0;
		this.Delay = (ps & 4) > 0;
		this.Charged = (ps & 8) > 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add((int)this.Rotation);
		int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0) | (this.Charged ? 8 : 0);
		data.add((byte)ps);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		data.add(8);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		if(buffer.readInt() == 8) {
			this.readFromPacket(buffer);
			super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		}
	}
}
