package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;

public class TileShapedLamp extends TileExtended implements IHandlePackets, IFrameSupport, IConnectable {
	
	public int Rotation = 0;
	public boolean Powered = false;
	public boolean Inverted = false;
	public int Style = 0;
	public int Color = 0;
	
	@Override
	public int getConnectableMask() {
		return 16777216 << this.Rotation | 15 << (this.Rotation << 2);
	}
	
	@Override
	public int getConnectClass(int side) {
		return 1;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.Rotation = side ^ 1;
		this.onBlockNeighborChange(Blocks.air);
		this.Inverted = (ist.getItemDamage() & 16) > 0;
		this.Color = ist.getItemDamage() & 15;
		this.Style = (ist.getItemDamage() & 1023) >> 5;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerLighting.blockShapedLamp;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void onBlockNeighborChange(Block block) {
		int mask = this.getConnectableMask();
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, mask & 16777215, mask >> 24)) {
			if (this.Powered) {
				return;
			}
			
			this.Powered = true;
			this.updateBlock();
			super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord); //TODO: Look on this
		} else {
			if (!this.Powered) {
				return;
			}
			
			this.Powered = false;
			this.updateBlock();
			super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord);
		}
		
	}
	
	public int getLightValue() {
		return this.Powered != this.Inverted ? 15 : 0;
	}
	
	@Override
	public void addHarvestContents(ArrayList<ItemStack> ist) {
		ItemStack is = new ItemStack(this.getBlockType(), 1, (this.getExtendedID() << 10) + (this.Style << 5) + (this.Inverted ? 16 : 0) + this.Color);
		ist.add(is);
	}
	
	public static AxisAlignedBB getRotatedBB(float x1, float y1, float z1,
			float x2, float y2, float z2, int rot) {
		switch (rot) {
			case 0:
				return AxisAlignedBB.getBoundingBox(x1, y1, z1, x2, y2, z2);
			case 1:
				return AxisAlignedBB.getBoundingBox(x1, 1.0F - y2, z1, x2,
						1.0F - y1, z2);
			case 2:
				return AxisAlignedBB.getBoundingBox(x1, z1, y1, x2, z2, y2);
			case 3:
				return AxisAlignedBB.getBoundingBox(x1, 1.0F - z2, 1.0F - y2,
						x2, 1.0F - z1, 1.0F - y1);
			case 4:
				return AxisAlignedBB.getBoundingBox(y1, x1, z1, y2, x2, z2);
			default:
				return AxisAlignedBB.getBoundingBox(1.0F - y2, 1.0F - x2, z1,
						1.0F - y1, 1.0F - x1, z2);
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		switch (this.Style) {
			case 0:
				return getRotatedBB(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F,
						this.Rotation);
			case 1:
				return getRotatedBB(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.75F,
						0.8125F, this.Rotation);
			default:
				return getRotatedBB(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F,
						this.Rotation);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(11);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 11) {
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
		this.Powered = (k & 1) > 0;
		this.Inverted = (k & 2) > 0;
		this.Color = tag.getByte("color");
		this.Style = tag.getByte("style");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
		tag.setByte("ps", (byte) ps);
		tag.setByte("rot", (byte) this.Rotation);
		tag.setByte("color", (byte) this.Color);
		tag.setByte("style", (byte) this.Style);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
		this.Color = buffer.readInt();
		this.Style = buffer.readInt();
		int ps = buffer.readInt();
		this.Powered = (ps & 1) > 0;
		this.Inverted = (ps & 2) > 0;
		super.worldObj.updateLightByType(EnumSkyBlock.Sky, super.xCoord, super.yCoord, super.zCoord);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
		data.add(this.Color);
		data.add(this.Style);
		int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
		data.add(ps);
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
