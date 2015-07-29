package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFilter;
import com.eloraam.redpower.machine.TileRetriever;
import com.eloraam.redpower.machine.TileTube;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileRetriever extends TileFilter implements IBluePowerConnectable {
	
	BluePowerEndpoint cond = new BluePowerEndpoint() {
		@Override
		public TileEntity getParent() {
			return TileRetriever.this;
		}
	};
	public int ConMask = -1;
	public byte select = 0;
	public byte mode = 0;
	
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
	public boolean tubeItemEnter(int side, int state, TubeItem ti) {
		if (side == (super.Rotation ^ 1) && state == 3) {
			if (!super.buffer.isEmpty()) {
				return false;
			} else {
				if (super.filterMap == null) {
					this.regenFilterMap();
				}
				
				if (super.filterMap.size() > 0
						&& !super.filterMap.containsKey(ti.item)) {
					return false;
				} else {
					super.buffer.addNewColor(ti.item, super.color);
					super.Delay = true;
					this.updateBlock();
					this.scheduleTick(5);
					this.drainBuffer();
					return true;
				}
			}
		} else {
			return side == super.Rotation && state == 2 ? super.tubeItemEnter(
					side, state, ti) : false;
		}
	}
	
	@Override
	public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
		if (side == (super.Rotation ^ 1) && state == 3) {
			if (!super.buffer.isEmpty()) {
				return false;
			} else {
				if (super.filterMap == null) {
					this.regenFilterMap();
				}
				
				return super.filterMap.size() == 0 ? true : super.filterMap
						.containsKey(ti.item);
			}
		} else {
			return side == super.Rotation && state == 2 ? super
					.tubeItemCanEnter(side, state, ti) : false;
		}
	}
	
	private void stepSelect() {
		for (int i = 0; i < 9; ++i) {
			++this.select;
			if (this.select > 8) {
				this.select = 0;
			}
			
			ItemStack ct = super.contents[this.select];
			if (ct != null && ct.stackSize > 0) {
				return;
			}
		}
		
		this.select = 0;
	}
	
	@Override
	protected boolean handleExtract(WorldCoord wc) {
		ITubeConnectable itc = (ITubeConnectable) CoreLib.getTileEntity(super.getWorldObj(), wc, ITubeConnectable.class);
		if (itc != null && itc.canRouteItems()) {
			if (this.cond.getVoltage() < 60.0D) {
				return false;
			} else {
				if (super.filterMap == null) {
					this.regenFilterMap();
				}
				
				TubeLib.InRouteFinder irf = new TubeLib.InRouteFinder(
						super.worldObj, super.filterMap);
				if (this.mode == 0) {
					irf.setSubFilt(this.select);
				}
				
				int sm = irf.find(new WorldCoord(this),
						1 << (super.Rotation ^ 1));
				if (sm < 0) {
					return false;
				} else {
					WorldCoord dest = irf.getResultPoint();
					IInventory inv = MachineLib.getInventory(super.worldObj, dest);
					if (inv == null) {
						return false;
					} else {
						int side = irf.getResultSide();
						int[] slots = new int[]{};
						if (inv instanceof ISidedInventory) {
							ISidedInventory tt = (ISidedInventory) inv;
							slots = tt.getAccessibleSlotsFromSide(side);
						}
						
						dest.step(side);
						TileTube tt1 = (TileTube) CoreLib.getTileEntity(
								super.worldObj, dest, TileTube.class);
						if (tt1 == null) {
							return false;
						} else {
							ItemStack ist = MachineLib.collectOneStack(inv, slots, super.contents[sm]);
							if (ist == null) {
								return false;
							} else {
								TubeItem ti = new TubeItem(side, ist);
								this.cond.drawPower(25 * ist.stackSize);
								ti.mode = 3;
								tt1.addTubeItem(ti);
								if (this.mode == 0) {
									this.stepSelect();
								}
								
								return true;
							}
						}
					}
				}
			}
		} else {
			return super.handleExtract(wc);
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
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		super.onBlockNeighborChange(bl);
	}
	
	@Override
	public void onTileTick() {
		super.onTileTick();
		if (super.Delay) {
			super.Delay = false;
			this.updateBlock();
		}
		
	}
	
	@Override
	protected void doSuck() {
		this.suckEntities(this.getSizeBox(2.55D, 5.05D, -0.95D));
	}
	
	@Override
	protected boolean suckFilter(ItemStack ist) {
		if (this.cond.getVoltage() < 60.0D) {
			return false;
		} else if (!super.suckFilter(ist)) {
			return false;
		} else {
			this.cond.drawPower(25 * ist.stackSize);
			return true;
		}
	}
	
	@Override
	protected int suckEntity(Entity ent) {
		if (!(ent instanceof EntityMinecartContainer)) {
			return super.suckEntity(ent);
		} else if (this.cond.getVoltage() < 60.0D) {
			return 0;
		} else {
			if (super.filterMap == null) {
				this.regenFilterMap();
			}
			
			EntityMinecartContainer em = (EntityMinecartContainer) ent;
			List<Integer> list = new ArrayList<Integer>(em.getSizeInventory());
			for(int i = 0; i < em.getSizeInventory(); i ++) {
				list.add(i);
			}
			if (!MachineLib.emptyInventory(em, list)) {
				return super.suckEntity(ent);
			} else {
				List<ItemStack> items = new ArrayList<ItemStack>();
				items.add(new ItemStack(Items.minecart, 1));
				if(em.func_145820_n().getMaterial() != Material.air) {
					items.add(new ItemStack(em.func_145820_n(), 1, em.getDisplayTileData()));
				}
				Iterator<ItemStack> iter = items.iterator();
				
				while (iter.hasNext()) {
					ItemStack ist = em.getCartItem();
					super.buffer.addNewColor(ist, super.color);
				}
				
				em.setDead();
				this.cond.drawPower(200.0D);
				return 2;
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
			player.openGui(RedPowerMachine.instance, 7, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public int getExtendedID() {
		return 8;
	}
	
	@Override
	public String getInventoryName() {
		return "Retriever";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cond.readFromNBT(tag);
		this.mode = tag.getByte("mode");
		this.select = tag.getByte("sel");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.cond.writeToNBT(tag);
		tag.setByte("mode", this.mode);
		tag.setByte("sel", this.select);
	}
}
