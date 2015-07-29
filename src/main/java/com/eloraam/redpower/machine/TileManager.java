package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.ITubeRequest;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileMachine;
import com.eloraam.redpower.machine.TileManager;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileManager extends TileMachine implements IBluePowerConnectable,IInventory, ISidedInventory, ITubeConnectable, ITubeRequest {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileManager.this;
		}
	};
	TubeBuffer buffer = new TubeBuffer();
	protected ItemStack[] contents = new ItemStack[24];
	public int ConMask = -1;
	public byte color = 0;
	public byte mode = 0;
	public int priority = 0;
	public byte rqnum = 0;
	protected MachineLib.FilterMap filterMap = null;
	
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
	public int[] getAccessibleSlotsFromSide(int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1) ? new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20} /*TODO: OR 24*/ : new int[]{};
	}
	
	protected IInventory getConnectedInventory(boolean push) {
		WorldCoord pos = new WorldCoord(this);
		pos.step(super.Rotation ^ 1);
		return MachineLib.getSideInventory(super.worldObj, pos, super.Rotation, push);
	}
	
	protected void regenFilterMap() {
		this.filterMap = MachineLib.makeFilterMap(this.contents, 0, 24);
	}
	
	protected int[] getAcceptCounts() {
		if (this.filterMap == null) {
			this.regenFilterMap();
		}
		if (this.filterMap.size() == 0) {
			return null;
		} else {
			IInventory inv = this.getConnectedInventory(true);
			if (inv == null) {
				return null;
			} else {
				int[] tr = MachineLib.genMatchCounts(this.filterMap);
				List<Integer> list = new ArrayList<Integer>(inv.getSizeInventory());
				for(int i = 0; i < inv.getSizeInventory(); i ++) {
					list.add(i);
				}
				MachineLib.decMatchCounts(this.filterMap, tr, inv, list);
				return tr;
			}
		}
	}
	
	protected int acceptCount(ItemStack ist) {
		if (this.filterMap == null) {
			this.regenFilterMap();
		}
		
		if (this.filterMap.size() == 0) {
			return 0;
		} else if (!this.filterMap.containsKey(ist)) {
			return 0;
		} else {
			int[] match = this.getAcceptCounts();
			return match == null ? 0 : MachineLib.getMatchCount(this.filterMap, match, ist);
		}
	}
	
	protected void doRequest(int slot, int num) {
		ItemStack rq = CoreLib.copyStack(this.contents[slot], Math.min(64, num));
		TubeItem tir = new TubeItem(0, rq);
		tir.priority = (short) this.priority;
		tir.color = this.color;
		TubeLib.RequestRouteFinder rrf = new TubeLib.RequestRouteFinder(super.worldObj, tir);
		if (rrf.find(new WorldCoord(this), 63) >= 0) {
			WorldCoord wc = rrf.getResultPoint();
			ITubeRequest itr = (ITubeRequest) CoreLib.getTileEntity(super.worldObj, wc, ITubeRequest.class);
			itr.requestTubeItem(tir, true);
			this.cond.drawPower(100.0D);
			this.scheduleTick(20);
		}
	}
	
	protected void scanInventory() {
		IInventory inv = this.getConnectedInventory(false);
		if (inv != null) {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			int[] ac = MachineLib.genMatchCounts(this.filterMap);
			if (ac != null) {
				for (int hrs = 0; hrs < inv.getSizeInventory(); ++hrs) {
					ItemStack n = inv.getStackInSlot(hrs);
					if (n != null && n.stackSize != 0) {
						if (this.mode == 0) {
							int mc = MachineLib.decMatchCount(this.filterMap,
									ac, n);
							if (mc < n.stackSize) {
								ItemStack rem = inv.decrStackSize(hrs,
										n.stackSize - mc);
								this.cond.drawPower(25 * n.stackSize);
								this.buffer.addNewColor(rem, this.color);
								super.Active = true;
								this.scheduleTick(5);
								this.updateBlock();
								return;
							}
						} else if (this.mode == 1
								&& !this.filterMap.containsKey(n)) {
							inv.setInventorySlotContents(hrs, (ItemStack) null);
							this.cond.drawPower(25 * n.stackSize);
							this.buffer.addNewColor(n, this.color);
							super.Active = true;
							this.scheduleTick(5);
							this.updateBlock();
							return;
						}
					}
				}
				
				boolean var7 = false;
				if (this.mode == 0) {
					ac = this.getAcceptCounts();
					if (ac != null) {
						var7 = true;
						++this.rqnum;
						if (this.rqnum >= 24) {
							this.rqnum = 0;
						}
						
						int var8;
						for (var8 = this.rqnum; var8 < ac.length; ++var8) {
							if (ac[var8] != 0) {
								var7 = false;
								this.doRequest(var8, ac[var8]);
								break;
							}
						}
						
						for (var8 = 0; var8 < this.rqnum; ++var8) {
							if (ac[var8] != 0) {
								var7 = false;
								this.doRequest(var8, ac[var8]);
								break;
							}
						}
					}
				}
				
				if (super.Powered != var7) {
					super.Powered = var7;
					this.updateBlockChange();
				}
				
			}
		}
	}
	
	@Override
	public int getTubeConnectableSides() {
		return 1 << super.Rotation;
	}
	
	@Override
	public int getTubeConClass() {
		return 0;
	}
	
	@Override
	public boolean canRouteItems() {
		return false;
	}
	
	private boolean handleTubeItem(TubeItem ti) {
		if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else if (ti.priority > this.priority) {
			return false;
		} else if (ti.color != this.color && this.color != 0 && ti.color != 0) {
			return false;
		} else if (this.mode == 1) {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			if (this.filterMap.size() == 0) {
				return false;
			} else if (!this.filterMap.containsKey(ti.item)) {
				return false;
			} else {
				IInventory mc1 = this.getConnectedInventory(true);
				List<Integer> list = new ArrayList<Integer>(mc1.getSizeInventory());
				for(int i = 0; i < mc1.getSizeInventory(); i ++) {
					list.add(i);
				}
				if (MachineLib.addToInventoryCore(mc1, ti.item, list, true)) {
					super.Delay = true;
					this.scheduleTick(5);
					this.updateBlock();
					return true;
				} else {
					return false;
				}
			}
		} else {
			int mc = this.acceptCount(ti.item);
			if (mc == 0) {
				return false;
			} else {
				boolean tr = true;
				ItemStack ist = ti.item;
				if (mc < ist.stackSize) {
					tr = false;
					ist = ist.splitStack(mc);
				}
				
				IInventory dest = this.getConnectedInventory(true);
				List<Integer> list = new ArrayList<Integer>(dest.getSizeInventory());
				for(int i = 0; i < dest.getSizeInventory(); i ++) {
					list.add(i);
				}
				if (MachineLib.addToInventoryCore(dest, ist, list, true)) {
					super.Delay = true;
					this.scheduleTick(5);
					this.updateBlock();
					return tr;
				} else {
					return false;
				}
			}
		}
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side != super.Rotation) {
			return false;
		} else if (state == 2) {
			if (this.handleTubeItem(ti)) {
				return true;
			} else {
				this.buffer.addBounce(ti);
				super.Active = true;
				this.updateBlock();
				this.scheduleTick(5);
				return true;
			}
		} else {
			return this.handleTubeItem(ti);
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		if (side != super.Rotation) {
			return false;
		} else if (state == 2) {
			return true;
		} else if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else if (ti.priority > this.priority) {
			return false;
		} else if (ti.color != this.color && this.color != 0 && ti.color != 0) {
			return false;
		} else {
			switch (this.mode) {
				case 0:
					return this.acceptCount(ti.item) > 0;
				case 1:
					if (this.filterMap == null) {
						this.regenFilterMap();
					}
					
					if (this.filterMap.size() == 0) {
						return false;
					}
					
					return this.filterMap.containsKey(ti.item);
				default:
					return false;
			}
		}
	}
	
	@Override
	public int tubeWeight(int side, int state) {
		return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
	}
	
	@Override
	public boolean requestTubeItem(TubeItem rq, boolean act) {
		if (super.Active) {
			return false;
		} else if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			if (this.filterMap.size() == 0) {
				return false;
			} else if (!this.filterMap.containsKey(rq.item)) {
				return false;
			} else if (rq.priority <= this.priority) {
				return false;
			} else if (rq.color != this.color && this.color > 0) {
				return false;
			} else {
				IInventory inv = this.getConnectedInventory(false);
				if (inv == null) {
					return false;
				} else {
					for (int i = 0; i < inv.getSizeInventory(); ++i) {
						ItemStack is2 = inv.getStackInSlot(i);
						if (is2 != null && is2.stackSize != 0
								&& CoreLib.compareItemStack(rq.item, is2) == 0) {
							if (act) {
								ItemStack pull = inv.decrStackSize(i, Math.min(
										rq.item.stackSize, is2.stackSize));
								TubeItem ti = new TubeItem(0, pull);
								this.cond.drawPower(25 * ti.item.stackSize);
								ti.priority = rq.priority;
								ti.color = this.color;
								this.buffer.add(ti);
								super.Active = true;
								this.scheduleTick(5);
								this.updateBlock();
							}
							
							return true;
						}
					}
					
					return false;
				}
			}
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (!this.isTickScheduled()) {
				this.scheduleTick(10);
			}
			
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
				}
			} else if (!super.Charged) {
				super.Charged = true;
				this.updateBlock();
			}
			
		}
	}
	
	@Override
	public boolean isPoweringTo(int side) {
		return side == (super.Rotation ^ 1) ? false : super.Powered;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine2;
	}
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 16, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
	}
	
	public void drainBuffer() {
		while (true) {
			if (!this.buffer.isEmpty()) {
				TubeItem ti = this.buffer.getLast();
				if (this.handleTubeItem(ti)) {
					this.buffer.pop();
					if (!this.buffer.plugged) {
						continue;
					}
					
					return;
				}
				
				if (!this.handleItem(ti)) {
					this.buffer.plugged = true;
					return;
				}
				
				this.buffer.pop();
				if (!this.buffer.plugged) {
					continue;
				}
				
				return;
			}
			
			return;
		}
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			boolean r = false;
			if (super.Delay) {
				super.Delay = false;
				r = true;
			}
			
			if (super.Active) {
				if (!this.buffer.isEmpty()) {
					this.drainBuffer();
					if (!this.buffer.isEmpty()) {
						this.scheduleTick(10);
					} else {
						this.scheduleTick(5);
					}
					
				} else {
					super.Active = false;
					this.updateBlock();
				}
			} else if (r) {
				this.updateBlock();
			} else if (this.cond.getVoltage() >= 60.0D) {
				this.scanInventory();
			}
		}
	}
	
	@Override
	public void onBlockRemoval() {
		super.onBlockRemoval();
		
		for (int i = 0; i < this.contents.length; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
		
	}
	
	@Override
	public int getSizeInventory() {
		return 24;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.contents[i];
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack tr;
			if (this.contents[i].stackSize <= j) {
				tr = this.contents[i];
				this.contents[i] = null;
				this.markDirty();
				return tr;
			} else {
				tr = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize == 0) {
					this.contents[i] = null;
				}
				
				this.markDirty();
				return tr;
			}
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack ist = this.contents[i];
			this.contents[i] = null;
			return ist;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack ist) {
		this.contents[i] = ist;
		if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
			ist.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return "Manager";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	public void markDirty() {
		this.filterMap = null;
		super.markDirty();
	}
	
	@Override
	public void closeInventory() {
	}
	
	@Override
	public void openInventory() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cond.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10); //TODO: 
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int j = item.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(item);
			}
		}
		
		this.color = tag.getByte("color");
		this.mode = tag.getByte("mode");
		this.priority = tag.getInteger("prio");
		this.rqnum = tag.getByte("rqnum");
		this.buffer.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.cond.writeToNBT(tag);
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				this.contents[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		tag.setTag("Items", items);
		tag.setByte("color", this.color);
		tag.setByte("mode", this.mode);
		tag.setInteger("prio", this.priority);
		tag.setByte("rqnum", this.rqnum);
		this.buffer.writeToNBT(tag);
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1);
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return side != super.Rotation && side != (super.Rotation ^ 1);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true;
	}
}
