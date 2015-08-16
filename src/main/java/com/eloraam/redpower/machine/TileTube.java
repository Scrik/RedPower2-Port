package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IPaintable;
import com.eloraam.redpower.core.ITubeFlow;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.machine.BlockMachine;
import com.eloraam.redpower.machine.TileTube;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileTube extends TileCovered implements ITubeFlow, IPaintable {
	
	protected TubeFlow flow = new TubeFlow() {
		@Override
		public TileEntity getParent() {
			return TileTube.this;
		}
		
		@Override
		public boolean schedule(TubeItem ti, TubeFlow.TubeScheduleContext tsc) {
			ti.scheduled = true;
			ti.progress = 0;
			int i = tsc.cons & ~(1 << ti.side);
			if (i == 0) {
				return true;
			} else if (Integer.bitCount(i) == 1) {
				ti.side = (byte) Integer.numberOfTrailingZeros(i);
				return true;
			} else if (CoreLib.isClient(TileTube.super.worldObj)) {
				return false;
			} else {
				if (ti.mode != 3) {
					ti.mode = 1;
				}
				
				ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc, ti, i,
						ti.mode, TileTube.this.lastDir);
				int m;
				if (ti.side >= 0) {
					m = i & ~((2 << TileTube.this.lastDir) - 1);
					if (m == 0) {
						m = i;
					}
					
					if (m == 0) {
						TileTube.this.lastDir = 0;
					} else {
						TileTube.this.lastDir = (byte) Integer
								.numberOfTrailingZeros(m);
					}
				} else {
					if (ti.mode == 1 && ti.priority > 0) {
						ti.priority = 0;
						ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc,
								ti, tsc.cons, 1);
						if (ti.side >= 0) {
							return true;
						}
					}
					
					ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc, ti,
							tsc.cons, 2);
					if (ti.side >= 0) {
						ti.mode = 2;
						return true;
					}
					
					if (ti.mode == 3) {
						ti.side = (byte) TubeLib.findRoute(tsc.world, tsc.wc,
								ti, tsc.cons, 1);
						ti.mode = 1;
					}
					
					if (ti.side < 0) {
						ti.side = TileTube.this.lastDir;
						m = i & ~((2 << TileTube.this.lastDir) - 1);
						if (m == 0) {
							m = i;
						}
						
						if (m == 0) {
							TileTube.this.lastDir = 0;
						} else {
							TileTube.this.lastDir = (byte) Integer
									.numberOfTrailingZeros(m);
						}
					}
				}
				
				return true;
			}
		}
		
		@Override
		public boolean handleItem(TubeItem ti, TubeFlow.TubeScheduleContext tsc) {
			return MachineLib.addToInventory(TileTube.super.worldObj, ti.item,
					tsc.dest, (ti.side ^ 1) & 63);
		}
	};
	public byte lastDir = 0;
	public byte paintColor = 0;
	private boolean hasChanged = false;
	
	@Override
	public int getTubeConnectableSides() {
		int tr = 63;
		
		for (int i = 0; i < 6; ++i) {
			if ((super.CoverSides & 1 << i) > 0 && super.Covers[i] >> 8 < 3) {
				tr &= ~(1 << i);
			}
		}
		
		return tr;
	}
	
	@Override
	public int getTubeConClass() {
		return this.paintColor;
	}
	
	@Override
	public boolean canRouteItems() {
		return true;
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (state != 0) {
			return false;
		} else if (ti.color != 0 && this.paintColor != 0
				&& ti.color != this.paintColor) {
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
		return ti.color != 0 && this.paintColor != 0
				&& ti.color != this.paintColor ? false : state == 0;
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
	public boolean tryPaint(int part, int side, int color) {
		if (part == 29) {
			if (this.paintColor == color) {
				return false;
			} else {
				this.paintColor = (byte) color;
				this.updateBlockChange();
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
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
		
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerBase.blockMicro;
	}
	
	@Override
	public int getExtendedID() {
		return 8;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl ) {
	}
	
	@Override
	public int getPartsMask() {
		return super.CoverSides | 536870912;
	}
	
	@Override
	public int getSolidPartsMask() {
		return super.CoverSides | 536870912;
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
		if (part == 29) {
			CoreLib.dropItem(
					super.worldObj,
					super.xCoord,
					super.yCoord,
					super.zCoord,
					new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
			this.flow.onRemove();
			if (super.CoverSides > 0) {
				this.replaceWithCovers();
			} else {
				this.deleteBlock();
			}
			
		} else {
			super.onHarvestPart(player, part);
		}
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		super.addHarvestContents(ist);
		ist.add(new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		BlockMachine bl = RedPowerMachine.blockMachine;
		return part == 29 ? player.getBreakSpeed(bl, false, 0)
				/ (bl.getHardness() * 30.0F) : super.getPartStrength(player,
				part);
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		if (part == 29) {
			bl.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
		} else {
			super.setPartBounds(bl, part);
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.flow.readFromNBT(tag);
		this.lastDir = tag.getByte("lDir");
		this.paintColor = tag.getByte("pCol");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.flow.writeToNBT(tag);
		tag.setByte("lDir", this.lastDir);
		tag.setByte("pCol", this.paintColor);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		if (buffer.readInt() == 10) {
			this.flow.contents.clear();
			int cs = buffer.readInt();
			
			for (int i = 0; i < cs; ++i) {
				this.flow.contents.add(TubeItem.newFromPacket(buffer));
			}
		} else {
			super.readFromPacket(buffer);
			this.paintColor = buffer.readByte();
			this.updateBlock();
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		data.add(this.paintColor);
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
			TubeItem ti = tii.next();
			ti.writeToPacket(data);
		}
		super.markDirty(); //TODO: It will send packet
		//CoreProxy.sendPacketToPosition(super.worldObj, pkt, super.xCoord, super.zCoord);
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		this.readFromPacket(buffer);
	}
}
