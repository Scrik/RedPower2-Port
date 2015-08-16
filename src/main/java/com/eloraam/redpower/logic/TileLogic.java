package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCoverable;
import com.eloraam.redpower.logic.BlockLogic;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileLogic extends TileCoverable implements IHandlePackets, IRedPowerConnectable, IRotatable, IFrameSupport {
	
	public int SubId = 0;
	public int Rotation = 0;
	public boolean Powered = false;
	public boolean Disabled = false;
	public boolean Active = false;
	public int PowerState = 0;
	public int Deadmap = 0;
	public int Cover = 255;
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : (part != this.Rotation >> 2 ? 0 : 3);
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		return sec ? 0 : (part != this.Rotation >> 2 ? 0 : this.Rotation & 3);
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (!sec) {
			if (part == this.Rotation >> 2) {
				this.Rotation = rot & 3 | this.Rotation & -4;
				this.updateBlockChange();
			}
		}
	}
	
	@Override
	public int getConnectableMask() {
		return 15 << (this.Rotation & -4);
	}
	
	@Override
	public int getConnectClass(int side) {
		return 0;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public int getPoweringMask(int ch) {
		return ch != 0 ? 0 : (this.Powered ? RedPowerLib.mapRotToCon(8,
				this.Rotation) : 0);
	}
	
	@Override
	public boolean canAddCover(int side, int cover) {
		return this.Cover != 255 ? false : ((side ^ 1) != this.Rotation >> 2 ? false : cover <= 254);
	}
	
	@Override
	public boolean tryAddCover(int side, int cover) {
		if (!this.canAddCover(side, cover)) {
			return false;
		} else {
			this.Cover = cover;
			this.updateBlock();
			return true;
		}
	}
	
	@Override
	public int tryRemoveCover(int side) {
		if (this.Cover == 255) {
			return -1;
		} else if ((side ^ 1) != this.Rotation >> 2) {
			return -1;
		} else {
			int tr = this.Cover;
			this.Cover = 255;
			this.updateBlock();
			return tr;
		}
	}
	
	@Override
	public int getCover(int side) {
		return this.Cover == 255 ? -1 : ((side ^ 1) != this.Rotation >> 2 ? -1 : this.Cover);
	}
	
	@Override
	public int getCoverMask() {
		return this.Cover == 255 ? 0 : 1 << (this.Rotation >> 2 ^ 1);
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		super.addHarvestContents(ist);
		ist.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID() * 256 + this.SubId));
	}
	
	private void replaceWithCovers() {
		if (this.Cover != 255) {
			short[] t = new short[26];
			t[this.Rotation >> 2 ^ 1] = (short) this.Cover;
			CoverLib.replaceWithCovers(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 1 << (this.Rotation >> 2 ^ 1),t);
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord,
					new ItemStack(this.getBlockType(), 1, this.getExtendedID() * 256 + this.SubId));
		} else {
			this.breakBlock();
			//RedPowerLib.updateIndirectNeighbors(super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.getBlockType());
		}
	}
	
	public boolean tryDropBlock() {
		if (RedPowerLib.canSupportWire(super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.Rotation >> 2)) {
			return false;
		} else {
			this.replaceWithCovers();
			return true;
		}
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		if (part == this.Rotation >> 2) {
			this.replaceWithCovers();
		} else {
			super.onHarvestPart(player, part);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		BlockLogic bl = RedPowerLogic.blockLogic;
		return part == this.Rotation >> 2 ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F) : super.getPartStrength(player, part);
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		if (part != this.Rotation >> 2) {
			super.setPartBounds(bl, part);
		} else {
			switch (part) {
				case 0:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
					break;
				case 1:
					bl.setBlockBounds(0.0F, 0.875F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;
				case 2:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
					break;
				case 3:
					bl.setBlockBounds(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
					break;
				case 4:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
					break;
				case 5:
					bl.setBlockBounds(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
			
		}
	}
	
	@Override
	public int getPartsMask() {
		int pm = 1 << (this.Rotation >> 2);
		if (this.Cover != 255) {
			pm |= 1 << (this.Rotation >> 2 ^ 1);
		}
		
		return pm;
	}
	
	@Override
	public int getSolidPartsMask() {
		return this.getPartsMask();
	}
	
	@Override
	public boolean isBlockStrongPoweringTo(int l) {
		return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(l ^ 1)) > 0;
	}
	
	@Override
	public boolean isBlockWeakPoweringTo(int l) {
		return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(l ^ 1)) > 0;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerLogic.blockLogic;
	}
	
	@Override
	public int getExtendedMetadata() {
		return this.SubId;
	}
	
	@Override
	public void setExtendedMetadata(int md) {
		this.SubId = md;
	}
	
	public void playSound(String name, float f, float f2, boolean always) {
		if (always || RedPowerLogic.EnableSounds) {
			super.worldObj.playSoundEffect(super.xCoord + 0.5F,
					super.yCoord + 0.5F, super.zCoord + 0.5F, name, f, f2);
		}
	}
	
	public void initSubType(int st) {
		this.SubId = st;
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.getLightValue() != 9) {
				CoreLib.updateAllLightTypes(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			}
		}
	}
	
	public int getLightValue() {
		return 9;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<?> getFramePacket() {
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
		this.SubId = tag.getByte("sid") & 255;
		this.Rotation = tag.getByte("rot") & 255;
		int ps = tag.getByte("ps") & 255;
		this.Deadmap = tag.getByte("dm") & 255;
		this.Cover = tag.getByte("cov") & 255;
		this.PowerState = ps & 15;
		this.Powered = (ps & 16) > 0;
		this.Disabled = (ps & 32) > 0;
		this.Active = (ps & 64) > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("sid", (byte) this.SubId);
		tag.setByte("rot", (byte) this.Rotation);
		int ps = this.PowerState | (this.Powered ? 16 : 0)
				| (this.Disabled ? 32 : 0) | (this.Active ? 64 : 0);
		tag.setByte("ps", (byte) ps);
		tag.setByte("dm", (byte) this.Deadmap);
		tag.setByte("cov", (byte) this.Cover);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.SubId = buffer.readInt();
		this.Rotation = buffer.readInt();
		int ps = buffer.readInt();
		if (CoreLib.isClient(super.worldObj)) {
			this.PowerState = ps & 15;
			this.Powered = (ps & 16) > 0;
			this.Disabled = (ps & 32) > 0;
			this.Active = (ps & 64) > 0;
		}
		if ((ps & 128) > 0) {
			this.Deadmap = buffer.readInt();
		} else {
			this.Deadmap = 0;
		}
		this.Cover = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.SubId);
		data.add(this.Rotation);
		int ps = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0) | (this.Active ? 64 : 0) | (this.Deadmap > 0 ? 128 : 0);
		data.add(ps);
		if (this.Deadmap > 0) {
			data.add(this.Deadmap);
		}
		data.add(this.Cover);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		data.add(1);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		try {
			this.readFromPacket(buffer);
		} catch (Throwable thr) {}
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
	}
}
