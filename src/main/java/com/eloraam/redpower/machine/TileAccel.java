package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.ITubeFlow;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileAccel;
import com.eloraam.redpower.machine.TileMachinePanel;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileAccel extends TileMachinePanel implements IBluePowerConnectable, ITubeFlow {
	
	TubeFlow flow = new TubeFlow() {
		@Override
		public TileEntity getParent() {
			return TileAccel.this;
		}
		
		@Override
		public boolean schedule(TubeItem ti, TubeFlow.TubeScheduleContext tsc) {
			ti.scheduled = true;
			ti.progress = 0;
			ti.side = (byte) (ti.side ^ 1);
			TileAccel.this.recache();
			ti.power = 0;
			if ((ti.side == TileAccel.super.Rotation
					&& (TileAccel.this.conCache & 2) > 0 || ti.side == (TileAccel.super.Rotation ^ 1)
					&& (TileAccel.this.conCache & 8) > 0)
					&& TileAccel.this.cond.getVoltage() >= 60.0D) {
				TileAccel.this.cond.drawPower(100 * ti.item.stackSize);
				ti.power = 255;
			}
			
			return true;
		}
	};
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileAccel.this;
		}
	};
	private boolean hasChanged = false;
	public int ConMask = -1;
	public int conCache = -1;
	
	@Override
	public int getTubeConnectableSides() {
		return 3 << (super.Rotation & 6);
	}
	
	@Override
	public int getTubeConClass() {
		return 17;
	}
	
	@Override
	public boolean canRouteItems() {
		return true;
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (state != 0) {
			return false;
		} else if (side != super.Rotation && side != (super.Rotation ^ 1)) {
			return false;
		} else {
			ti.side = (byte) side;
			this.flow.add(ti);
			this.hasChanged = true;
			this.markDirty();
			return true;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return state == 0;
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return 0;
	}
	
	@Override
	public void addTubeItem(TubeItem ti) {
		ti.side = (byte) (ti.side ^ 1);
		this.flow.add(ti);
		this.hasChanged = true;
		this.markDirty();
	}
	
	@Override
	public TubeFlow getTubeFlow() {
		return this.flow;
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : 5;
	}
	
	@Override
	public int getLightValue() {
		return super.Charged ? 6 : 0;
	}
	
	public void recache() {
		if (this.conCache < 0) {
			WorldCoord wc = new WorldCoord(this);
			ITubeConnectable fw = (ITubeConnectable) CoreLib.getTileEntity(
					super.worldObj, wc.coordStep(super.Rotation),
					ITubeConnectable.class);
			ITubeConnectable bw = (ITubeConnectable) CoreLib.getTileEntity(
					super.worldObj, wc.coordStep(super.Rotation ^ 1),
					ITubeConnectable.class);
			this.conCache = 0;
			int mcl;
			if (fw != null) {
				mcl = fw.getTubeConClass();
				if (mcl < 17) {
					this.conCache |= 1;
				} else if (mcl >= 17) {
					this.conCache |= 2;
				}
			}
			
			if (bw != null) {
				mcl = bw.getTubeConClass();
				if (mcl < 17) {
					this.conCache |= 4;
				} else if (mcl >= 17) {
					this.conCache |= 8;
				}
			}
			
		}
	}
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
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
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		this.flow.onRemove();
		this.breakBlock();
	}
	
	@Override
	public int getExtendedID() {
		return 2;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.flow.update()) {
			this.hasChanged = true;
		}
		
		if (this.hasChanged) {
			this.hasChanged = false;
			if (!CoreLib.isClient(super.worldObj)) {
				this.sendItemUpdate();
			}
			
			this.markDirty();
		}
		
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.cond.Flow == 0) {
				if (super.Charged) {
					super.Charged = false;
					this.updateBlock();
					this.updateLight();
				}
			} else if (!super.Charged) {
				super.Charged = true;
				this.updateBlock();
				this.updateLight();
			}
			
		}
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = this.getFacing(ent);
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		this.conCache = -1;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cond.readFromNBT(tag);
		this.flow.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.cond.writeToNBT(tag);
		this.flow.writeToNBT(tag);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void sendItemUpdate() {
		ArrayList data = new ArrayList();
		data.add(10);
		int cs = this.flow.contents.size();
		if (cs > 6) {
			cs = 6;
		}
		data.add(cs);
		Iterator<TubeItem> tii = this.flow.contents.iterator();
		for (int i = 0; i < cs; ++i) {
			TubeItem ti = (TubeItem) tii.next();
			ti.writeToPacket(data);
		}
		this.markDirty(); //TODO Needs to send packet ot location
		//CoreProxy.sendPacketToPosition(super.worldObj, pkt, super.xCoord, super.zCoord);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		int subId = 0;
		try {
			subId = buffer.readInt();
		} catch(Throwable t) {}
		if (subId == 10) {
			this.flow.contents.clear();
			int cs = buffer.readInt();
			for (int i = 0; i < cs; ++i) {
				this.flow.contents.add(TubeItem.newFromPacket(buffer));
			}
		} else {
			super.readFromPacket(buffer);
			this.updateBlock();
		}
		
	}
}
