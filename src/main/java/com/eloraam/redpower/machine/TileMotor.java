package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FrameLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileMotor;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class TileMotor extends TileExtended implements IHandlePackets, IBluePowerConnectable, IRotatable, IFrameLink, IFrameSupport {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileMotor.this;
		}
	};
	public int Rotation = 0;
	public int MoveDir = 4;
	public int MovePos = -1;
	public boolean Powered = false;
	public boolean Active = false;
	public boolean Charged = false;
	public int LinkSize = -1;
	public int ConMask = -1;
	
	@Override
	public int getConnectableMask() {
		return 1073741823 ^ RedPowerLib.getConDirMask(this.Rotation >> 2 ^ 1);
	}
	
	@Override
	public int getConnectClass(int side) {
		return 65;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public WorldCoord getFrameLinkset() {
		return null;
	}
	
	@Override
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return this.MovePos >= 0 ? 0 : (sec ? 5 : 3);
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		return sec ? this.Rotation >> 2 : this.Rotation & 3;
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (this.MovePos < 0) {
			if (sec) {
				this.Rotation = this.Rotation & 3 | rot << 2;
			} else {
				this.Rotation = this.Rotation & -4 | rot & 3;
			}
			
			this.updateBlockChange();
		}
	}
	
	@Override
	public boolean isFrameMoving() {
		return false;
	}
	
	@Override
	public boolean canFrameConnectIn(int dir) {
		return dir != (this.Rotation >> 2 ^ 1);
	}
	
	@Override
	public boolean canFrameConnectOut(int dir) {
		return dir == (this.Rotation >> 2 ^ 1);
	}
	
	@Override
	public int getExtendedID() {
		return 7;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.MovePos >= 0 && this.MovePos < 16) {
			++this.MovePos;
			this.markDirty();
		}
		
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.MovePos >= 0) {
				this.cond.drawPower(100 + 10 * this.LinkSize);
			}
			
			if (this.MovePos >= 16) {
				this.dropFrame(true);
				this.MovePos = -1;
				this.Active = false;
				this.updateBlock();
			}
			
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.MovePos < 0) {
				if (this.cond.getVoltage() < 60.0D) {
					if (this.Charged && this.cond.Flow == 0) {
						this.Charged = false;
						this.updateBlock();
					}
					
				} else {
					if (!this.Charged) {
						this.Charged = true;
						this.updateBlock();
					}
					
				}
			}
		}
	}
	
	private int getDriveSide() {
		short n;
		switch (this.Rotation >> 2) {
			case 0:
				n = 13604;
				break;
			case 1:
				n = 13349;
				break;
			case 2:
				n = 20800;
				break;
			case 3:
				n = 16720;
				break;
			case 4:
				n = 8496;
				break;
			default:
				n = 12576;
		}
		
		int n1 = n >> ((this.Rotation & 3) << 2);
		n1 &= 7;
		return n1;
	}
	
	void pickFrame() {
		this.MoveDir = this.getDriveSide();
		WorldCoord wc = new WorldCoord(this);
		FrameLib.FrameSolver fs = new FrameLib.FrameSolver(super.worldObj,
				wc.coordStep(this.Rotation >> 2 ^ 1), wc, this.MoveDir);
		if (fs.solveLimit(RedPowerMachine.FrameLinkSize)) {
			if (fs.addMoved()) {
				this.LinkSize = fs.getFrameSet().size();
				this.MovePos = 0;
				this.Active = true;
				this.updateBlock();
				Iterator<WorldCoord> i$ = fs.getClearSet().iterator();
				
				WorldCoord sp;
				while (i$.hasNext()) {
					sp = (WorldCoord) i$.next();
					super.worldObj.setBlockToAir(sp.x, sp.y, sp.z);
				}
				
				i$ = fs.getFrameSet().iterator();
				
				while (i$.hasNext()) {
					sp = (WorldCoord) i$.next();
					Block tfm = super.worldObj.getBlock(sp.x, sp.y, sp.z);
					int ifs = super.worldObj.getBlockMetadata(sp.x, sp.y, sp.z);
					TileEntity te = super.worldObj.getTileEntity(sp.x, sp.y, sp.z);
					if (te != null) {
						super.worldObj.removeTileEntity(sp.x, sp.y, sp.z);
					}
					
					boolean ir = super.worldObj.isRemote;
					super.worldObj.isRemote = true;
					super.worldObj.setBlock(sp.x, sp.y, sp.z, RedPowerMachine.blockFrame, 1, 2); //Maybe 3
					super.worldObj.isRemote = ir;
					TileFrameMoving tfm1 = (TileFrameMoving) CoreLib
							.getTileEntity(super.worldObj, sp,
									TileFrameMoving.class);
					if (tfm1 != null) {
						tfm1.setContents(tfm, ifs, super.xCoord, super.yCoord, super.zCoord, te);
					}
				}
				
				i$ = fs.getFrameSet().iterator();
				
				while (i$.hasNext()) {
					sp = (WorldCoord) i$.next();
					super.worldObj.markBlockForUpdate(sp.x, sp.y, sp.z);
					CoreLib.markBlockDirty(super.worldObj, sp.x, sp.y, sp.z);
					TileFrameMoving tfm2 = (TileFrameMoving) CoreLib
							.getTileEntity(super.worldObj, sp,
									TileFrameMoving.class);
					if (tfm2 != null
							&& tfm2.movingTileEntity instanceof IFrameSupport) {
						IFrameSupport ifs1 = (IFrameSupport) tfm2.movingTileEntity;
						ifs1.onFramePickup(tfm2.getFrameBlockAccess());
					}
				}
				
			}
		}
	}
	
	void dropFrame(boolean fw) {
		WorldCoord wc = new WorldCoord(this);
		FrameLib.FrameSolver fs = new FrameLib.FrameSolver(super.worldObj,wc.coordStep(this.Rotation >> 2 ^ 1), wc, -1);
		if (fs.solve()) {
			this.LinkSize = 0;
			fs.sort(this.MoveDir);
			Iterator<WorldCoord> i$ = fs.getFrameSet().iterator();
			WorldCoord sp;
			while (i$.hasNext()) {
				sp = (WorldCoord) i$.next();
				TileFrameMoving ifs = (TileFrameMoving) CoreLib.getTileEntity(
						super.worldObj, sp, TileFrameMoving.class);
				if (ifs != null) {
					ifs.pushEntities(this);
					WorldCoord s2 = sp.copy();
					if (fw) {
						s2.step(this.MoveDir);
					}
					
					if (ifs.movingBlockID != Blocks.air) {
						boolean ir = super.worldObj.isRemote;
						super.worldObj.isRemote = true;
						super.worldObj.setBlock(s2.x, s2.y, s2.z, ifs.movingBlockID, ifs.movingBlockMeta, 2); //TODO: Don't know
						super.worldObj.isRemote = ir;
						if (ifs.movingTileEntity != null) {
							ifs.movingTileEntity.xCoord = s2.x;
							ifs.movingTileEntity.yCoord = s2.y;
							ifs.movingTileEntity.zCoord = s2.z;
							ifs.movingTileEntity.validate();
							super.worldObj.setTileEntity(s2.x, s2.y, s2.z, ifs.movingTileEntity);
						}
					}
					
					if (fw) {
						super.worldObj.setBlockToAir(sp.x, sp.y, sp.z);
					}
				}
			}
			
			i$ = fs.getFrameSet().iterator();
			
			while (i$.hasNext()) {
				sp = (WorldCoord) i$.next();
				IFrameSupport ifs1 = (IFrameSupport) CoreLib.getTileEntity(
						super.worldObj, sp, IFrameSupport.class);
				if (ifs1 != null) {
					ifs1.onFrameDrop();
				}
				
				super.worldObj.markBlockForUpdate(sp.x, sp.y, sp.z);
				CoreLib.markBlockDirty(super.worldObj, sp.x, sp.y, sp.z);
				RedPowerLib.updateIndirectNeighbors(super.worldObj, sp.x, sp.y, sp.z, super.worldObj.getBlock(sp.x, sp.y, sp.z));
			}
			
		}
	}
	
	float getMoveScaled() {
		return this.MovePos / 16.0F;
	}
	
	@Override
	public void onBlockRemoval() {
		if (this.MovePos >= 0) {
			this.Active = false;
			this.dropFrame(false);
		}
		
		this.MovePos = -1;
	}
	
	@Override
	public void onBlockNeighborChange(Block l) {
		this.ConMask = -1;
		if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63)) {
			if (this.Charged) {
				if (!this.Powered) {
					if (this.MovePos < 0) {
						this.Powered = true;
						this.updateBlockChange();
						if (this.Powered) {
							this.pickFrame();
						}
					}
				}
			}
		} else if (this.Powered) {
			this.Powered = false;
			this.updateBlockChange();
		}
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
		this.Rotation = this.getFacing(ent) << 2;
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
		this.MoveDir = tag.getByte("mdir");
		this.MovePos = tag.getByte("mpos");
		this.LinkSize = tag.getInteger("links");
		this.cond.readFromNBT(tag);
		byte k = tag.getByte("ps");
		this.Powered = (k & 1) > 0;
		this.Active = (k & 2) > 0;
		this.Charged = (k & 4) > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("rot", (byte) this.Rotation);
		tag.setByte("mdir", (byte) this.MoveDir);
		tag.setByte("mpos", (byte) this.MovePos);
		tag.setInteger("links", this.LinkSize);
		this.cond.writeToNBT(tag);
		int ps = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0)
				| (this.Charged ? 4 : 0);
		tag.setByte("ps", (byte) ps);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
		this.MoveDir = buffer.readInt();
		this.MovePos = buffer.readInt() - 1;
		int flags = buffer.readInt();
		this.Powered = (flags & 1) > 0;
		this.Active = (flags & 2) > 0;
		this.Charged = (flags & 4) > 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
		data.add(this.MoveDir);
		data.add(this.MovePos + 1);
		int flags = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
		data.add(flags);
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
