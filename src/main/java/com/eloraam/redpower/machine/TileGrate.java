package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FluidBuffer;
import com.eloraam.redpower.core.IPipeConnectable;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileGrate;
import com.eloraam.redpower.machine.TileMachinePanel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class TileGrate extends TileMachinePanel implements IPipeConnectable {
	
	FluidBuffer gratebuf = new FluidBuffer() {
		@Override
		public TileEntity getParent() {
			return TileGrate.this;
		}
		
		@Override
		public void onChange() {
			TileGrate.this.markDirty();
		}
		
		@Override
		public int getMaxLevel() {
			return 1000;
		}
	};
	TileGrate.GratePathfinder searchPath = null;
	int searchState = 0;
	int pressure;
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		return sec ? 0 : 5;
	}
	
	@Override
	public int getPipeConnectableSides() {
		return 1 << super.Rotation;
	}
	
	@Override
	public int getPipeFlangeSides() {
		return 1 << super.Rotation;
	}
	
	@Override
	public int getPipePressure(int side) {
		return this.pressure;
	}
	
	@Override
	public FluidBuffer getPipeBuffer(int side) {
		return this.gratebuf;
	}
	
	@Override
	public void onFramePickup(IBlockAccess iba) {
		this.restartPath();
	}
	
	@Override
	public int getExtendedID() {
		return 3;
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		super.Rotation = this.getFacing(ent);
		this.updateBlockChange();
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!CoreLib.isClient(super.worldObj)) {
			if (!this.isTickScheduled()) {
				this.scheduleTick(5);
			}
			
			WorldCoord wc = new WorldCoord(this);
			wc.step(super.Rotation);
			Integer pr = PipeLib.getPressure(super.worldObj, wc,
					super.Rotation ^ 1);
			if (pr != null) {
				this.pressure = pr.intValue() - Integer.signum(pr.intValue());
			}
			
			if (this.searchState == 1) {
				this.searchPath.tryMapFluid(400);
			}
			
			PipeLib.movePipeLiquid(super.worldObj, this, new WorldCoord(this),
					1 << super.Rotation);
		}
	}
	
	public void restartPath() {
		this.searchPath = null;
		this.searchState = 0;
	}
	
	@Override
	public void onTileTick() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.pressure == 0) {
				this.restartPath();
			} else if (this.pressure < -100) {
				if (this.gratebuf.getLevel() >= this.gratebuf.getMaxLevel()) {
					return;
				}
				
				if (this.searchState == 2) {
					this.restartPath();
				}
				
				if (this.searchState == 0) {
					this.searchState = 1;
					this.searchPath = new TileGrate.GratePathfinder(false);
					if (this.gratebuf.Type == 0) {
						if (!this.searchPath.startSuck(new WorldCoord(this),
								63 ^ 1 << super.Rotation)) {
							this.restartPath();
							return;
						}
					} else {
						this.searchPath.start(new WorldCoord(this),
								this.gratebuf.Type, 63 ^ 1 << super.Rotation);
					}
				}
				
				if (this.searchState == 1) {
					if (!this.searchPath.tryMapFluid(400)) {
						return;
					}
					
					int fluid = this.searchPath.trySuckFluid(/*this.searchPath.fluidClass.getFluidQuanta()*/1000); //TODO: amount
					if (fluid == 0) {
						return;
					}
					
					this.gratebuf.addLevel(this.searchPath.fluidID, fluid);
				}
			} else if (this.pressure > 100) {
				Fluid fluid1 = this.gratebuf.getFluidClass();
				if (fluid1 == null) {
					return;
				}
				
				int fq = /*fluid1.getFluidQuanta();*/1000; //TODO: Check it out...
				/*if (fq == 0) {
					return;
				}*/
				
				if (this.gratebuf.getLevel() < fq) {
					return;
				}
				
				if (this.gratebuf.Type == 0) {
					return;
				}
				
				if (this.searchState == 1) {
					this.restartPath();
				}
				
				if (this.searchState == 0) {
					this.searchState = 2;
					this.searchPath = new TileGrate.GratePathfinder(true);
					this.searchPath.start(new WorldCoord(this),
							this.gratebuf.Type, 63 ^ 1 << super.Rotation);
				}
				
				if (this.searchState == 2) {
					int fr = this.searchPath.tryDumpFluid(fq, 2000);
					if (fr != fq) {
						this.gratebuf.addLevel(this.gratebuf.Type, -fq);
					}
				}
			}
			
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.gratebuf.readFromNBT(tag, "buf");
		this.pressure = tag.getShort("pres");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.gratebuf.writeToNBT(tag, "buf");
		tag.setShort("pres", (short) this.pressure);
	}
	
	public static class FluidCoord implements Comparable<FluidCoord> {
		public WorldCoord wc;
		public int dist;
		
		public FluidCoord(WorldCoord w, int d) {
			this.wc = w;
			this.dist = d;
		}
		@Override
		public int compareTo(FluidCoord wr) {
			return this.wc.y == wr.wc.y ? this.dist - wr.dist : this.wc.y - wr.wc.y;
		}
	}
	
	public class GratePathfinder {
		WorldCoord startPos;
		HashMap<WorldCoord, WorldCoord> backlink = new HashMap<WorldCoord, WorldCoord>();
		PriorityQueue<FluidCoord> workset;
		PriorityQueue<FluidCoord> allset = new PriorityQueue<FluidCoord>(1024, Collections.reverseOrder());
		public int fluidID;
		public Fluid fluidClass;
		
		public GratePathfinder(boolean checkVertical) {
			if (checkVertical) {
				this.workset = new PriorityQueue<FluidCoord>();
			} else {
				this.workset = new PriorityQueue<FluidCoord>(1024, new TileGrate.SimpleComparator());
			}
		}
		
		public void start(WorldCoord wc, int tp, int sides) {
			this.fluidID = tp;
			this.fluidClass = FluidRegistry.getFluid(this.fluidID);
			this.startPos = wc;
			
			for (int i = 0; i < 6; ++i) {
				if ((sides & 1 << i) != 0) {
					WorldCoord wc2 = wc.coordStep(i);
					this.backlink.put(wc2, wc);
					this.workset.add(new TileGrate.FluidCoord(wc2, 0));
				}
			}
		}
		
		public boolean startSuck(WorldCoord wc, int sides) {
			this.fluidID = 0;
			this.startPos = wc;
			
			for (int i = 0; i < 6; ++i) {
				if ((sides & 1 << i) != 0) {
					WorldCoord wc2 = wc.coordStep(i);
					this.backlink.put(wc2, wc);
					this.workset.add(new TileGrate.FluidCoord(wc2, 0));
					int fl = PipeLib.getFluidId(TileGrate.super.worldObj, wc2);
					if (fl != 0) {
						this.fluidID = fl;
					}
				}
			}
			
			if (this.fluidID == 0) {
				return false;
			} else {
				this.fluidClass = FluidRegistry.getFluid(this.fluidID);
				return true;
			}
		}
		
		public boolean isConnected(WorldCoord wc) {
			if (wc.compareTo(this.startPos) == 0) {
				return true;
			} else {
				do {
					wc = (WorldCoord) this.backlink.get(wc);
					if (wc == null) {
						return false;
					}
					if (wc.compareTo(this.startPos) == 0) {
						return true;
					}
				} while (PipeLib.getFluidId(TileGrate.super.worldObj, wc) == this.fluidID);
				
				return false;
			}
		}
		
		public void stepAdd(TileGrate.FluidCoord nc) {
			for (int i = 0; i < 6; ++i) {
				WorldCoord wc2 = nc.wc.coordStep(i);
				if (!this.backlink.containsKey(wc2)) {
					this.backlink.put(wc2, nc.wc);
					this.workset
							.add(new TileGrate.FluidCoord(wc2, nc.dist + 1));
				}
			}
			
		}
		
		public void stepMap(TileGrate.FluidCoord nc) {
			for (int i = 0; i < 6; ++i) {
				WorldCoord wc2 = nc.wc.coordStep(i);
				if (PipeLib.getFluidId(TileGrate.super.worldObj, wc2) == this.fluidID && !this.backlink.containsKey(wc2)) {
					this.backlink.put(wc2, nc.wc);
					this.workset
							.add(new TileGrate.FluidCoord(wc2, nc.dist + 1));
				}
			}
			
		}
		
		public int tryDumpFluid(int level, int tries) {
			level = 1000; //TODO: Костыль
			for (int i = 0; i < tries; ++i) {
				TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.workset
						.poll();
				if (nc == null) {
					TileGrate.this.restartPath();
					return level;
				}
				
				if (!this.isConnected(nc.wc)) {
					TileGrate.this.restartPath();
					return level;
				}
				
				if (TileGrate.super.worldObj.getBlock(nc.wc.x, nc.wc.y, nc.wc.z) == Blocks.air) {
					//if (this.fluidClass.setFluidLevel(TileGrate.super.worldObj, nc.wc, level)) {
					this.stepAdd(nc);
					return 0;
					//}
				} else if (PipeLib.getFluidId(TileGrate.super.worldObj, nc.wc) == this.fluidID) {
					this.stepAdd(nc); //TODO: Костыльненько...
					/*int lv1 = this.fluidClass.getFluidLevel(TileGrate.super.worldObj, nc.wc);
					if (lv1 < 1000) {
						int lv2 = Math.min(lv1 + level, 1000);
						if (this.fluidClass.setFluidLevel(TileGrate.super.worldObj, nc.wc, lv2)) {
							level -= lv2 - lv1;
							if (level == 0) {
								return 0;
							}
						}
					}*/
				}
			}
			
			return level;
		}
		
		public boolean tryMapFluid(int tries) {
			if (this.allset.size() > '\u8000') {
				return true;
			} else {
				for (int i = 0; i < tries; ++i) {
					TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.workset.poll();
					if (nc == null) {
						return true;
					}
					
					if (PipeLib.getFluidId(TileGrate.super.worldObj, nc.wc) == this.fluidID) {
						this.stepMap(nc);
						//int lv1 = this.fluidClass.getFluidLevel( TileGrate.super.worldObj, nc.wc); //TODO: Опять костыль
						//if (lv1 > 0) {
						this.allset.add(nc);
						//}
					}
				}
				
				return false;
			}
		}
		
		public int trySuckFluid(int level) {
			int tr = 0;
			
			while (!this.allset.isEmpty()) {
				TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.allset.peek();
				if (!this.isConnected(nc.wc)) {
					TileGrate.this.restartPath();
					return tr;
				}
				
				if (PipeLib.getFluidId(TileGrate.super.worldObj, nc.wc) != this.fluidID) {
					this.allset.poll();
				} else { //TODO: КоСтЫлИ...
					/*int lv1 = this.fluidClass.getFluidLevel(TileGrate.super.worldObj, nc.wc);
					if (lv1 == 0) {
						this.allset.poll();
					} else {*/
						/*if (tr + lv1 <= level) {
							tr += lv1;
							TileGrate.super.worldObj.setBlockToAir(nc.wc.x, nc.wc.y, nc.wc.z);
							this.allset.poll();
							if (tr == level) {
								return level;
							}
						}*/
						
						//if (this.fluidClass.setFluidLevel(TileGrate.super.worldObj, nc.wc, level - tr)) {
						return level;
						//}
					//}
				}
			}
			TileGrate.this.restartPath();
			return tr;
		}
	}
	
	public static class SimpleComparator implements Comparator<FluidCoord> {
		int dir;
		@Override
		public int compare(FluidCoord wa, FluidCoord wb) {
			return wa.dist - wb.dist;
		}
	}
}
