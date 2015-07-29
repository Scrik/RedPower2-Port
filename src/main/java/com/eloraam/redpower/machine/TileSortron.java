package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.machine.TileSortron;
import com.eloraam.redpower.machine.TileTranspose;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileSortron extends TileTranspose implements
		IBluePowerConnectable, IRedbusConnectable {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileSortron.this;
		}
	};
	public int ConMask = -1;
	int rbaddr = 4;
	private int cmdDelay = 0;
	private int command = 0;
	private int itemSlot = 0;
	private int itemType = 0;
	private int itemDamage = 0;
	private int itemDamageMax = 0;
	private int itemQty = 0;
	private int itemColor = 0;
	private int itemInColor = 0;
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 67;
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
				return this.command & 255;
			case 1:
				return this.itemQty & 255;
			case 2:
				return this.itemSlot & 255;
			case 3:
				return this.itemSlot >> 8 & 255;
			case 4:
				return this.itemType & 255;
			case 5:
				return this.itemType >> 8 & 255;
			case 6:
				return this.itemType >> 16 & 255;
			case 7:
				return this.itemType >> 24 & 255;
			case 8:
				return this.itemDamage & 255;
			case 9:
				return this.itemDamage >> 8 & 255;
			case 10:
				return this.itemDamageMax & 255;
			case 11:
				return this.itemDamageMax >> 8 & 255;
			case 12:
				return this.itemColor & 255;
			case 13:
				return this.itemInColor & 255;
			default:
				return 0;
		}
	}
	
	@Override
	public void rbWrite(int reg, int dat) {
		this.markDirty();
		switch (reg) {
			case 0:
				this.command = dat;
				this.cmdDelay = 2;
				break;
			case 1:
				this.itemQty = dat;
				break;
			case 2:
				this.itemSlot = this.itemSlot & '\uff00' | dat;
				break;
			case 3:
				this.itemSlot = this.itemSlot & 255 | dat << 8;
				break;
			case 4:
				this.itemType = this.itemType & -256 | dat;
				break;
			case 5:
				this.itemType = this.itemType & -65281 | dat << 8;
				break;
			case 6:
				this.itemType = this.itemType & -16711681 | dat << 16;
				break;
			case 7:
				this.itemType = this.itemType & 16777215 | dat << 24;
				break;
			case 8:
				this.itemDamage = this.itemDamage & '\uff00' | dat;
				break;
			case 9:
				this.itemDamage = this.itemDamage & 255 | dat << 8;
				break;
			case 10:
				this.itemDamageMax = this.itemDamageMax & '\uff00' | dat;
				break;
			case 11:
				this.itemDamageMax = this.itemDamageMax & 255 | dat << 8;
				break;
			case 12:
				this.itemColor = dat;
				break;
			case 13:
				this.itemInColor = dat;
		}
		
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.cmdDelay > 0 && --this.cmdDelay == 0) {
				this.processCommand();
			}
			
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
	public Block getBlockType() {
		return RedPowerMachine.blockMachine2;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
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
					return true;
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (super.Active) {
				if (!super.buffer.isEmpty()) {
					this.drainBuffer();
					if (!super.buffer.isEmpty()) {
						this.scheduleTick(10);
					} else {
						this.scheduleTick(5);
					}
					
				} else {
					super.Active = false;
					this.updateBlock();
				}
			}
		}
	}
	
	public static int hashItem(ItemStack ist) {
		String in = ist.getItem().getUnlocalizedName();
		int hc;
		if (in == null) {
			hc = ist.getItem().hashCode();
		} else {
			hc = in.hashCode();
		}
		if (ist.getHasSubtypes()) {
			hc = Integer.valueOf(Integer.valueOf(hc).hashCode() ^ ist.getItemDamage()).intValue();
		}
		return hc;
	}
	
	void processCommand() {
		if (this.cond.getVoltage() < 60.0D) {
			this.cmdDelay = 20;
		} else {
			IInventory inv;
			ItemStack ist;
			switch (this.command) {
				case 0:
					break;
				case 1:
					inv = this.getConnectedInventory(false);
					if (inv == null) {
						this.command = 255;
					} else {
						this.itemSlot = inv.getSizeInventory();
						this.command = 0;
					}
					break;
				case 2:
					inv = this.getConnectedInventory(false);
					if (inv == null) {
						this.command = 255;
					} else if (this.itemSlot >= inv.getSizeInventory()) {
						this.command = 255;
					} else {
						ist = inv.getStackInSlot(this.itemSlot);
						if (ist != null && ist.stackSize != 0) {
							this.itemQty = ist.stackSize;
							this.itemType = hashItem(ist);
							if (ist.isItemStackDamageable()) {
								this.itemDamage = ist.getItemDamage();
								this.itemDamageMax = ist.getMaxDamage();
							} else {
								this.itemDamage = 0;
								this.itemDamageMax = 0;
							}
							
							this.command = 0;
						} else {
							this.itemQty = 0;
							this.itemType = 0;
							this.itemDamage = 0;
							this.itemDamageMax = 0;
							this.command = 0;
						}
					}
					break;
				case 3:
					if (super.Active) {
						this.cmdDelay = 2;
						return;
					}
					
					inv = this.getConnectedInventory(false);
					if (inv == null) {
						this.command = 255;
					} else if (this.itemSlot >= inv.getSizeInventory()) {
						this.command = 255;
					} else {
						ist = inv.getStackInSlot(this.itemSlot);
						if (ist != null && ist.stackSize != 0) {
							int i = Math.min(this.itemQty, ist.stackSize);
							this.itemQty = i;
							if (this.itemColor > 16) {
								this.itemColor = 0;
							}
							
							super.buffer.addNewColor(
									inv.decrStackSize(this.itemSlot, i),
									this.itemColor);
							this.cond.drawPower(50 * ist.stackSize);
							this.drainBuffer();
							super.Active = true;
							this.command = 0;
							this.updateBlock();
							this.scheduleTick(5);
						} else {
							this.itemQty = 0;
							this.command = 0;
						}
					}
					break;
				case 4:
					if (this.itemQty == 0) {
						this.command = 0;
					}
					break;
				default:
					this.command = 255;
			}
			
		}
	}
	
	@Override
	protected boolean handleExtract(IInventory inv, int[] slots) {
		return false;
	}
	
	@Override
	protected void addToBuffer(ItemStack ist) {
		if (this.itemColor > 16) {
			this.itemColor = 0;
		}
		
		super.buffer.addNewColor(ist, this.itemColor);
	}
	
	@Override
	protected int suckEntity(Entity ent) {
		if (ent instanceof EntityItem) {
			EntityItem ei = (EntityItem) ent;
			ItemStack ist = ei.getEntityItem();
			if (ist.stackSize != 0 && !ei.isDead) {
				int st = ist.stackSize;
				if (!this.suckFilter(ist)) {
					return st == ist.stackSize ? 0 : 2;
				} else {
					this.addToBuffer(ist);
					ei.setDead();
					return 1;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	@Override
	protected boolean suckFilter(ItemStack ist) {
		if (this.command != 4) {
			return false;
		} else if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else if (this.itemType != 0 && this.itemType != hashItem(ist)) {
			return false;
		} else {
			boolean tr = true;
			if (this.itemQty < ist.stackSize) {
				tr = false;
				ist = ist.splitStack(this.itemQty);
				if (this.itemColor > 16) {
					this.itemColor = 0;
				}
				
				super.buffer.addNewColor(ist, this.itemColor);
			}
			
			this.itemQty -= ist.stackSize;
			if (this.itemQty == 0) {
				this.command = 0;
			}
			
			this.cond.drawPower(50 * ist.stackSize);
			return tr;
		}
	}
	
	@Override
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side == super.Rotation && state == 2) {
			return super.tubeItemEnter(side, state, ti);
		} else if (side == (super.Rotation ^ 1) && state == 1) {
			if (this.command != 4) {
				return false;
			} else if (this.cond.getVoltage() < 60.0D) {
				return false;
			} else if (this.itemType != 0 && this.itemType != hashItem(ti.item)) {
				return false;
			} else if (this.itemInColor != 0 && this.itemInColor != ti.color) {
				return false;
			} else {
				boolean tr = true;
				ItemStack ist = ti.item;
				if (this.itemQty < ist.stackSize) {
					tr = false;
					ist = ist.splitStack(this.itemQty);
				}
				
				this.itemQty -= ist.stackSize;
				if (this.itemQty == 0) {
					this.command = 0;
				}
				
				if (this.itemColor > 16) {
					this.itemColor = 0;
				}
				
				super.buffer.addNewColor(ist, this.itemColor);
				this.cond.drawPower(50 * ist.stackSize);
				this.drainBuffer();
				super.Active = true;
				this.updateBlock();
				this.scheduleTick(5);
				return tr;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		return side == super.Rotation && state == 2 ? true : (side == (super.Rotation ^ 1)
				&& state == 1 ? (this.command != 4 ? false : (this.cond
				.getVoltage() < 60.0D ? false : (this.itemType != 0
				&& this.itemType != hashItem(ti.item) ? false : this.itemInColor == 0
				|| this.itemInColor == ti.color))) : false);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cond.readFromNBT(tag);
		this.rbaddr = tag.getByte("rbaddr") & 255;
		this.cmdDelay = tag.getByte("cmddelay") & 255;
		this.command = tag.getByte("cmd") & 255;
		this.itemSlot = tag.getShort("itemslot") & '\uffff';
		this.itemType = tag.getInteger("itemtype");
		this.itemDamage = tag.getShort("itemdmg") & '\uffff';
		this.itemDamageMax = tag.getShort("itemdmgmax") & '\uffff';
		this.itemQty = tag.getByte("itemqty") & 255;
		this.itemInColor = tag.getByte("itemincolor") & 255;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.cond.writeToNBT(tag);
		tag.setByte("rbaddr", (byte) this.rbaddr);
		tag.setByte("cmddelay", (byte) this.cmdDelay);
		tag.setByte("cmd", (byte) this.command);
		tag.setShort("itemslot", (short) this.itemSlot);
		tag.setInteger("itemtype", this.itemType);
		tag.setShort("itemdmg", (short) this.itemDamage);
		tag.setShort("itemdmgmax", (short) this.itemDamageMax);
		tag.setByte("itemqty", (byte) this.itemQty);
		tag.setByte("itemcolor", (byte) this.itemColor);
		tag.setByte("itemincolor", (byte) this.itemInColor);
	}
}
