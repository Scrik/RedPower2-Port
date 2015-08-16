package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileIOExpander extends TileMultipart implements IRedbusConnectable, IRedPowerConnectable, IHandlePackets, IFrameSupport {
	
	public int Rotation = 0;
	public int WBuf = 0;
	public int WBufNew = 0;
	public int RBuf = 0;
	private int rbaddr = 3;
	
	@Override
	public int rbGetAddr() {
		return this.rbaddr;
	}
	
	@Override
	public void rbSetAddr(int addr) {
		this.rbaddr = addr;
	}
	
	@Override
	public int rbRead(int reg) {
		switch (reg) {
			case 0:
				return this.RBuf & 255;
			case 1:
				return this.RBuf >> 8;
			case 2:
				return this.WBufNew & 255;
			case 3:
				return this.WBufNew >> 8;
			default:
				return 0;
		}
	}
	
	@Override
	public void rbWrite(int reg, int dat) {
		this.markDirty();
		switch (reg) {
			case 0:
			case 2:
				this.WBufNew = this.WBufNew & '\uff00' | dat;
				this.scheduleTick(2);
				break;
			case 1:
			case 3:
				this.WBufNew = this.WBufNew & 255 | dat << 8;
				this.scheduleTick(2);
		}
	}
	
	@Override
	public int getConnectableMask() {
		return 15;
	}
	
	@Override
	public int getConnectClass(int side) {
		return side == CoreLib.rotToSide(this.Rotation) ? 18 : 66;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public int getPoweringMask(int ch) {
		return ch == 0 ? 0 : ((this.WBuf & 1 << ch - 1) > 0 ? RedPowerLib.mapRotToCon(8, this.Rotation) : 0);
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) + 1 & 3;
		this.sendPacket();
		this.markDirty();
	}
	
	@Override
	public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
		if (player.isSneaking()) {
			if (CoreLib.isClient(super.worldObj)) {
				return false;
			} else {
				ItemStack ist = player.inventory.getCurrentItem();
				if (ist == null) {
					return false;
				} else if (!(ist.getItem() instanceof ItemScrewdriver)) {
					return false;
				} else {
					player.openGui(RedPowerBase.instance, 3, super.worldObj,
							super.xCoord, super.yCoord, super.zCoord);
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void onTileTick() {
		if (this.WBuf != this.WBufNew) {
			this.WBuf = this.WBufNew;
			this.onBlockNeighborChange(Blocks.air);
			this.updateBlockChange();
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block l) {
		boolean ch = false;
		
		for (int n = 0; n < 16; ++n) {
			int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 8, this.Rotation, n + 1);
			if (ps == 0) {
				if ((this.RBuf & 1 << n) > 0) {
					this.RBuf &= ~(1 << n);
					ch = true;
				}
			} else if ((this.RBuf & 1 << n) == 0) {
				this.RBuf |= 1 << n;
				ch = true;
			}
		}
		if (ch) {
			this.updateBlock();
		}
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerControl.blockFlatPeripheral;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		//super.addHarvestContents(ist);
		ist.add(new ItemStack(this.getBlockType(), 1, 0));
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
		bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.Rotation = tag.getByte("rot");
		this.WBuf = tag.getShort("wb") & '\uffff';
		this.WBufNew = tag.getShort("wbn") & '\uffff';
		this.RBuf = tag.getShort("rb") & '\uffff';
		this.rbaddr = tag.getByte("rbaddr") & 255;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("rot", (byte) this.Rotation);
		tag.setShort("wb", (short) this.WBuf);
		tag.setShort("wbn", (short) this.WBufNew);
		tag.setShort("rb", (short) this.RBuf);
		tag.setByte("rbaddr", (byte) this.rbaddr);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
		this.WBuf = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
		data.add(this.WBuf);
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
