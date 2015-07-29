package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileSorter;
import com.eloraam.redpower.machine.TileTranspose;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileSorter extends TileTranspose implements IInventory, ISidedInventory, IBluePowerConnectable {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileSorter.this;
		}
	};
	public int ConMask = -1;
	private ItemStack[] contents = new ItemStack[40];
	public byte[] colors = new byte[8];
	public byte mode = 0;
	public byte automode = 0;
	public byte defcolor = 0;
	public byte draining = -1;
	public byte column = 0;
	public int pulses = 0;
	protected MachineLib.FilterMap filterMap = null;
	TubeBuffer[] channelBuffers = new TubeBuffer[8];
	
	public TileSorter() {
		for (int i = 0; i < 8; ++i) {
			this.channelBuffers[i] = new TubeBuffer();
		}
		
	}
	
	void regenFilterMap() {
		this.filterMap = MachineLib.makeFilterMap(this.contents);
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
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[]{};
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (!super.Powered) {
				super.Delay = false;
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
			
			if ((this.automode == 1 || this.automode == 2 && this.pulses > 0)
					&& !this.isTickScheduled()) {
				this.scheduleTick(10);
			}
			
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 5, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public int getExtendedID() {
		return 5;
	}
	
	@Override
	public void onBlockRemoval() {
		super.onBlockRemoval();
		
		int i;
		for (i = 0; i < 8; ++i) {
			this.channelBuffers[i].onRemove(this);
		}
		
		for (i = 0; i < 40; ++i) {
			ItemStack ist = this.contents[i];
			if (ist != null && ist.stackSize > 0) {
				CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
						super.zCoord, ist);
			}
		}
		
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		if (this.automode == 0) {
			super.onBlockNeighborChange(bl);
		}
		
		if (this.automode == 2) {
			if (!RedPowerLib.isPowered(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 16777215, 63)) {
				super.Powered = false;
				this.markDirty();
				return;
			}
			
			if (super.Powered) {
				return;
			}
			
			super.Powered = true;
			this.markDirty();
			if (super.Delay) {
				return;
			}
			
			super.Delay = true;
			++this.pulses;
		}
		
	}
	
	protected int getColumnMatch(ItemStack ist) {
		if (this.filterMap == null) {
			this.regenFilterMap();
		}
		
		if (this.filterMap.size() == 0) {
			return -2;
		} else {
			int i = this.filterMap.firstMatch(ist);
			return i < 0 ? i : i & 7;
		}
	}
	
	protected void fireMatch() {
		super.Active = true;
		this.updateBlock();
		this.scheduleTick(5);
	}
	
	protected boolean tryDrainBuffer(TubeBuffer buf) {
		if (buf.isEmpty()) {
			return false;
		} else {
			while (!buf.isEmpty()) {
				TubeItem ti = buf.getLast();
				if (this.stuffCart(ti.item)) {
					buf.pop();
				} else {
					if (!this.handleItem(ti)) {
						buf.plugged = true;
						return true;
					}
					
					buf.pop();
					if (buf.plugged) {
						return true;
					}
				}
			}
			
			return true;
		}
	}
	
	protected boolean tryDrainBuffer() {
		for (int i = 0; i < 9; ++i) {
			++this.draining;
			TubeBuffer buf;
			if (this.draining > 7) {
				this.draining = -1;
				buf = super.buffer;
			} else {
				buf = this.channelBuffers[this.draining];
			}
			
			if (this.tryDrainBuffer(buf)) {
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean isBufferEmpty() {
		if (!super.buffer.isEmpty()) {
			return false;
		} else {
			for (int i = 0; i < 8; ++i) {
				if (!this.channelBuffers[i].isEmpty()) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	@Override
	public void drainBuffer() {
		this.tryDrainBuffer();
	}
	
	private boolean autoTick() {
		if (super.Active) {
			return false;
		} else if (this.automode == 2 && this.pulses == 0) {
			return false;
		} else {
			WorldCoord wc = new WorldCoord(this);
			wc.step(super.Rotation ^ 1);
			if (this.handleExtract(wc)) {
				super.Active = true;
				this.updateBlock();
				this.scheduleTick(5);
			} else {
				this.scheduleTick(10);
			}
			
			return true;
		}
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.automode == 1 && super.Powered) {
				super.Powered = false;
				this.updateBlock();
			}
			
			if (this.automode <= 0 || !this.autoTick()) {
				if (super.Active) {
					if (!this.tryDrainBuffer()) {
						if (this.isBufferEmpty()) {
							this.scheduleTick(5);
						} else {
							this.scheduleTick(10);
						}
						
					} else {
						if (!super.Powered || this.automode == 2) {
							super.Active = false;
							this.updateBlock();
						}
						
						if (this.automode == 1 || this.automode == 2
								&& this.pulses > 0) {
							this.scheduleTick(5);
						}
						
					}
				}
			}
		}
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		int cm;
		TubeBuffer buf;
		if (side == super.Rotation && state == 2) {
			cm = this.getColumnMatch(ti.item);
			buf = super.buffer;
			if (cm >= 0 && this.mode > 1) {
				buf = this.channelBuffers[cm];
			}
			
			buf.addBounce(ti);
			this.fireMatch();
			return true;
		} else if (side == (super.Rotation ^ 1) && state == 1) {
			if (ti.priority > 0) {
				return false;
			} else if (this.automode == 0 && super.Powered) {
				return false;
			} else if (this.cond.getVoltage() < 60.0D) {
				return false;
			} else {
				cm = this.getColumnMatch(ti.item);
				buf = super.buffer;
				if (cm >= 0 && this.mode > 1) {
					buf = this.channelBuffers[cm];
				}
				
				if (!buf.isEmpty()) {
					return false;
				} else if (cm < 0) {
					if (this.mode != 4 && this.mode != 6) {
						if (cm == -2) {
							this.cond.drawPower(25 * ti.item.stackSize);
							buf.addNewColor(ti.item, 0);
							this.fireMatch();
							this.tryDrainBuffer(buf);
							return true;
						} else {
							return false;
						}
					} else {
						this.cond.drawPower(25 * ti.item.stackSize);
						buf.addNewColor(ti.item, this.defcolor);
						this.fireMatch();
						this.tryDrainBuffer(buf);
						return true;
					}
				} else {
					this.cond.drawPower(25 * ti.item.stackSize);
					buf.addNewColor(ti.item, this.colors[cm]);
					this.fireMatch();
					this.tryDrainBuffer(buf);
					return true;
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		if (side == super.Rotation && state == 2) {
			return true;
		} else if (side == (super.Rotation ^ 1) && state == 1) {
			if (ti.priority > 0) {
				return false;
			} else if (this.automode == 0 && super.Powered) {
				return false;
			} else if (this.cond.getVoltage() < 60.0D) {
				return false;
			} else {
				int cm = this.getColumnMatch(ti.item);
				TubeBuffer buf = super.buffer;
				if (cm >= 0 && this.mode > 1) {
					buf = this.channelBuffers[cm];
				}
				
				return !buf.isEmpty() ? false : (cm < 0 ? (this.mode != 4
						&& this.mode != 6 ? cm == -2 : true) : true);
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void addToBuffer(ItemStack ist) {
		int cm = this.getColumnMatch(ist);
		TubeBuffer buf = super.buffer;
		if (cm >= 0 && this.mode > 1) {
			buf = this.channelBuffers[cm];
		}
		
		if (cm < 0) {
			if (this.mode != 4 && this.mode != 6) {
				buf.addNewColor(ist, 0);
			} else {
				buf.addNewColor(ist, this.defcolor);
			}
		} else {
			buf.addNewColor(ist, this.colors[cm]);
		}
	}
	
	private void stepColumn() {
		for (int i = 0; i < 8; ++i) {
			++this.column;
			if (this.column > 7) {
				if (this.pulses > 0) {
					--this.pulses;
				}
				
				this.column = 0;
			}
			
			for (int a = 0; a < 5; ++a) {
				ItemStack ct = this.contents[a * 8 + this.column];
				if (ct != null && ct.stackSize != 0) {
					return;
				}
			}
		}
		
		this.column = 0;
	}
	
	private void checkColumn() {
		for (int a = 0; a < 5; ++a) {
			ItemStack ct = this.contents[a * 8 + this.column];
			if (ct != null && ct.stackSize != 0) {
				return;
			}
		}
		
		this.stepColumn();
		this.markDirty();
	}
	
	@Override
	protected boolean handleExtract(IInventory inv, int[] slots) {
		if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			if (this.filterMap.size() == 0) {
				ItemStack var8 = MachineLib.collectOneStack(inv, slots, (ItemStack) null);
				if (var8 == null) {
					return false;
				} else {
					if (this.mode != 4 && this.mode != 6) {
						super.buffer.addNew(var8);
					} else {
						super.buffer.addNewColor(var8, this.defcolor);
					}
					
					this.cond.drawPower(25 * var8.stackSize);
					this.drainBuffer();
					return true;
				}
			} else {
				int sm;
				ItemStack coll;
				int n;
				ItemStack match;
				switch (this.mode) {
					case 0:
						this.checkColumn();
						sm = MachineLib.matchAnyStackCol(this.filterMap, inv, slots, this.column);
						if (sm < 0) {
							return false;
						}
						
						coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
						super.buffer.addNewColor(coll, this.colors[sm & 7]);
						this.cond.drawPower(25 * coll.stackSize);
						this.stepColumn();
						this.drainBuffer();
						return true;
					case 1:
						this.checkColumn();
						if (!MachineLib.matchAllCol(this.filterMap, inv, slots, this.column)) {
							return false;
						}
						
						for (n = 0; n < 5; ++n) {
							match = this.contents[n * 8 + this.column];
							if (match != null && match.stackSize != 0) {
								coll = MachineLib.collectOneStack(inv, slots, match);
								super.buffer.addNewColor(coll, this.colors[this.column]);
								this.cond.drawPower(25 * coll.stackSize);
							}
						}
						
						this.stepColumn();
						this.drainBuffer();
						return true;
					case 2:
						for (sm = 0; sm < 8
								&& !MachineLib.matchAllCol(this.filterMap, inv, slots, sm); ++sm) {
							;
						}
						
						if (sm == 8) {
							return false;
						} else {
							for (n = 0; n < 5; ++n) {
								match = this.contents[n * 8 + sm];
								if (match != null && match.stackSize != 0) {
									coll = MachineLib.collectOneStack(inv, slots, match);
									this.channelBuffers[sm].addNewColor(coll, this.colors[sm]);
									this.cond.drawPower(25 * coll.stackSize);
								}
							}
							
							if (this.pulses > 0) {
								--this.pulses;
							}
							
							this.drainBuffer();
							return true;
						}
					case 3:
						sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
						if (sm < 0) {
							return false;
						}
						
						coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
						this.channelBuffers[sm & 7].addNewColor(coll,
								this.colors[sm & 7]);
						this.cond.drawPower(25 * coll.stackSize);
						if (this.pulses > 0) {
							--this.pulses;
						}
						
						this.drainBuffer();
						return true;
					case 4:
						sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
						if (sm < 0) {
							coll = MachineLib.collectOneStack(inv, slots, (ItemStack) null);
							if (coll == null) {
								return false;
							}
							
							super.buffer.addNewColor(coll, this.defcolor);
						} else {
							coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
							this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7]);
						}
						
						this.cond.drawPower(25 * coll.stackSize);
						if (this.pulses > 0) {
							--this.pulses;
						}
						
						this.drainBuffer();
						return true;
					case 5:
						sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
						if (sm < 0) {
							return false;
						}
						
						coll = MachineLib.collectOneStackFuzzy(inv, slots, this.contents[sm]);
						this.channelBuffers[sm & 7].addNewColor(coll,
								this.colors[sm & 7]);
						this.cond.drawPower(25 * coll.stackSize);
						if (this.pulses > 0) {
							--this.pulses;
						}
						
						this.drainBuffer();
						return true;
					case 6:
						sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
						if (sm < 0) {
							coll = MachineLib.collectOneStack(inv, slots, (ItemStack) null);
							if (coll == null) {
								return false;
							}
							
							super.buffer.addNewColor(coll, this.defcolor);
						} else {
							coll = MachineLib.collectOneStackFuzzy(inv, slots, this.contents[sm]);
							this.channelBuffers[sm & 7].addNewColor(coll,
									this.colors[sm & 7]);
						}
						
						this.cond.drawPower(25 * coll.stackSize);
						if (this.pulses > 0) {
							--this.pulses;
						}
						
						this.drainBuffer();
						return true;
					default:
						return false;
				}
			}
		}
	}
	
	@Override
	protected boolean suckFilter(ItemStack ist) {
		if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else {
			if (this.filterMap == null) {
				this.regenFilterMap();
			}
			
			int cm = this.getColumnMatch(ist);
			TubeBuffer buf = super.buffer;
			if (cm >= 0 && this.mode > 1) {
				buf = this.channelBuffers[cm];
			}
			
			if (buf.plugged) {
				return false;
			} else if (cm < 0) {
				if (this.mode != 4 && this.mode != 6 && cm != -2) {
					return false;
				} else {
					this.cond.drawPower(25 * ist.stackSize);
					return true;
				}
			} else {
				this.cond.drawPower(25 * ist.stackSize);
				return true;
			}
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 40;
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
		return "Sorter";
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
	
	@Override
	public void markDirty() {
		this.filterMap = null;
		super.markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10); //TODO:
		this.contents = new ItemStack[this.getSizeInventory()];
		
		int i;
		for (int cols = 0; cols < items.tagCount(); ++cols) {
			NBTTagCompound bufs = (NBTTagCompound) items.getCompoundTagAt(cols);
			i = bufs.getByte("Slot") & 255;
			if (i >= 0 && i < this.contents.length) {
				this.contents[i] = ItemStack.loadItemStackFromNBT(bufs);
			}
		}
		
		this.column = tag.getByte("coln");
		byte[] var7 = tag.getByteArray("cols");
		if (var7.length >= 8) {
			for (int var8 = 0; var8 < 8; ++var8) {
				this.colors[var8] = var7[var8];
			}
		}
		
		this.mode = tag.getByte("mode");
		this.automode = tag.getByte("amode");
		this.draining = tag.getByte("drain");
		if (this.mode == 4 || this.mode == 6) {
			this.defcolor = tag.getByte("defc");
		}
		
		this.pulses = tag.getInteger("pulses");
		this.cond.readFromNBT(tag);
		NBTTagList var9 = tag.getTagList("buffers", 10); //TODO:
		
		for (i = 0; i < var9.tagCount(); ++i) {
			NBTTagCompound buf = (NBTTagCompound) var9.getCompoundTagAt(i);
			this.channelBuffers[i].readFromNBT(buf);
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList items = new NBTTagList();
		
		for (int bufs = 0; bufs < this.contents.length; ++bufs) {
			if (this.contents[bufs] != null) {
				NBTTagCompound i = new NBTTagCompound();
				i.setByte("Slot", (byte) bufs);
				this.contents[bufs].writeToNBT(i);
				items.appendTag(i);
			}
		}
		
		tag.setByte("coln", this.column);
		tag.setTag("Items", items);
		tag.setByteArray("cols", this.colors);
		tag.setByte("mode", this.mode);
		tag.setByte("amode", this.automode);
		tag.setByte("drain", this.draining);
		tag.setInteger("pulses", this.pulses);
		if (this.mode == 4 || this.mode == 6) {
			tag.setByte("defc", this.defcolor);
		}
		
		this.cond.writeToNBT(tag);
		NBTTagList var6 = new NBTTagList();
		
		for (int var7 = 0; var7 < 8; ++var7) {
			NBTTagCompound buf = new NBTTagCompound();
			this.channelBuffers[var7].writeToNBT(buf);
			var6.appendTag(buf);
		}
		
		tag.setTag("buffers", var6);
	}

	@Override
	public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
		return false;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return true; //TODO: Maybe not
	}
}
