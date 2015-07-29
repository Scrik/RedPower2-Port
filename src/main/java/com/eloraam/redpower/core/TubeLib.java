package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.ITubeRequest;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TubeLib {
	
	private static HashSet tubeClassMapping = new HashSet();
	
	public static void addCompatibleMapping(int a, int b) {
		tubeClassMapping
				.add(Arrays.asList(new Integer[] { Integer.valueOf(a), Integer
						.valueOf(b) }));
		tubeClassMapping
				.add(Arrays.asList(new Integer[] { Integer.valueOf(b), Integer
						.valueOf(a) }));
	}
	
	public static boolean isCompatible(int a, int b) {
		return a == b
				|| tubeClassMapping.contains(Arrays
						.asList(new Integer[] { Integer.valueOf(a), Integer
								.valueOf(b) }));
	}
	
	private static boolean isConSide(IBlockAccess iba, int i, int j, int k, int col, int side) {
		TileEntity te = iba.getTileEntity(i, j, k);
		if (isCompatible(col, 0) && te instanceof IInventory) {
			if (!(te instanceof ISidedInventory)) {
				return true;
			}
			
			ISidedInventory inventory = (ISidedInventory) te;
			if(inventory.getSizeInventory() > 0) {
				int[] slots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(ForgeDirection.getOrientation(side).getOpposite().ordinal()); //TODO: May be opposite

				return (slots != null && slots.length > 0);
			}
		}
		
		if (te instanceof ITubeConnectable) {
			ITubeConnectable itc1 = (ITubeConnectable) te;
			if (!isCompatible(col, itc1.getTubeConClass())) {
				return false;
			} else {
				int s = itc1.getTubeConnectableSides();
				return (s & 1 << side) > 0;
			}
		} else {
			return false;
		}
	}
	
	public static int getConnections(IBlockAccess iba, int i, int j, int k) {
		ITubeConnectable itc = (ITubeConnectable) CoreLib.getTileEntity(iba, i,
				j, k, ITubeConnectable.class);
		if (itc == null) {
			return 0;
		} else {
			int trs = 0;
			int col = itc.getTubeConClass();
			int sides = itc.getTubeConnectableSides();
			if ((sides & 1) > 0 && isConSide(iba, i, j - 1, k, col, 1)) {
				trs |= 1;
			}
			
			if ((sides & 2) > 0 && isConSide(iba, i, j + 1, k, col, 0)) {
				trs |= 2;
			}
			
			if ((sides & 4) > 0 && isConSide(iba, i, j, k - 1, col, 3)) {
				trs |= 4;
			}
			
			if ((sides & 8) > 0 && isConSide(iba, i, j, k + 1, col, 2)) {
				trs |= 8;
			}
			
			if ((sides & 16) > 0 && isConSide(iba, i - 1, j, k, col, 5)) {
				trs |= 16;
			}
			
			if ((sides & 32) > 0 && isConSide(iba, i + 1, j, k, col, 4)) {
				trs |= 32;
			}
			
			return trs;
		}
	}
	
	public static int findRoute(World world, WorldCoord wc, TubeItem te,
			int sides, int state) {
		TubeLib.OutRouteFinder rf = new TubeLib.OutRouteFinder(world, te, state);
		return rf.find(wc, sides);
	}
	
	public static int findRoute(World world, WorldCoord wc, TubeItem te,
			int sides, int state, int start) {
		TubeLib.OutRouteFinder rf = new TubeLib.OutRouteFinder(world, te, state);
		rf.startDir = start;
		return rf.find(wc, sides);
	}
	
	public static boolean addToTubeRoute(World world, ItemStack ist,
			WorldCoord src, WorldCoord wc, int side) {
		return addToTubeRoute(world, new TubeItem(0, ist), src, wc, side);
	}
	
	public static boolean addToTubeRoute(World world, TubeItem ti,
			WorldCoord src, WorldCoord wc, int side) {
		ITubeConnectable ite = (ITubeConnectable) CoreLib.getTileEntity(world,
				wc, ITubeConnectable.class);
		if (ite == null) {
			return false;
		} else {
			ti.mode = 1;
			int s = findRoute(world, src, ti, 1 << (side ^ 1), 1);
			return s < 0 ? false : ite.tubeItemEnter(side, 0, ti);
		}
	}
	
	static {
		addCompatibleMapping(0, 17);
		addCompatibleMapping(17, 18);
		
		for (int i = 0; i < 16; ++i) {
			addCompatibleMapping(0, 1 + i);
			addCompatibleMapping(17, 1 + i);
			addCompatibleMapping(17, 19 + i);
			addCompatibleMapping(18, 19 + i);
		}
		
	}
	
	public static class InRouteFinder extends TubeLib.RouteFinder {
		
		MachineLib.FilterMap filterMap;
		int subFilt = -1;
		
		public InRouteFinder(World world, MachineLib.FilterMap map) {
			super(world);
			this.filterMap = map;
		}
		
		@Override
		public void addPoint(WorldCoord wc, int st, int side, int weight) {
			IInventory inv = MachineLib.getInventory(super.worldObj, wc);
			if (inv == null) {
				super.addPoint(wc, st, side, weight);
			} else {
				int side2 = (side ^ 1) & 63;
				int[] slots = new int[]{};
				if (inv instanceof ISidedInventory) {
					ISidedInventory sm = (ISidedInventory) inv;
					slots = sm.getAccessibleSlotsFromSide(side2);
				}
				
				if (this.filterMap.size() == 0) {
					if (!MachineLib.emptyInventory(inv, slots)) {
						TubeLib.WorldRoute sm2 = new TubeLib.WorldRoute(wc, 0, side2, weight);
						sm2.solved = true;
						super.scanpos.add(sm2);
					} else {
						super.addPoint(wc, st, side, weight);
					}
				} else {
					int sm1 = -1;
					if (this.subFilt < 0) {
						sm1 = MachineLib.matchAnyStack(this.filterMap, inv, slots);
					} else if (MachineLib.matchOneStack(this.filterMap, inv, slots, this.subFilt)) {
						sm1 = this.subFilt;
					}
					
					if (sm1 < 0) {
						super.addPoint(wc, st, side, weight);
					} else {
						TubeLib.WorldRoute nr = new TubeLib.WorldRoute(wc, sm1,
								side2, weight);
						nr.solved = true;
						super.scanpos.add(nr);
					}
				}
			}
		}
		
		public void setSubFilt(int sf) {
			this.subFilt = sf;
		}
		
		public int getResultSide() {
			return super.result.side;
		}
	}
	
	private static class OutRouteFinder extends TubeLib.RouteFinder {
		
		int state;
		TubeItem tubeItem;
		
		public OutRouteFinder(World world, TubeItem ti, int st) {
			super(world);
			this.state = st;
			this.tubeItem = ti;
		}
		
		@Override
		public void addPoint(WorldCoord wc, int start, int side, int weight) {
			int side1 = (side ^ 1) & 255;
			if (this.state != 3
					&& this.tubeItem.priority == 0
					&& MachineLib.canAddToInventory(super.worldObj,
							this.tubeItem.item, wc, side1)) {
				TubeLib.WorldRoute itc1 = new TubeLib.WorldRoute(wc, start,
						side, weight);
				itc1.solved = true;
				super.scanpos.add(itc1);
			} else {
				ITubeConnectable itc = (ITubeConnectable) CoreLib
						.getTileEntity(super.worldObj, wc,
								ITubeConnectable.class);
				if (itc != null) {
					if (itc.tubeItemCanEnter(side1, this.state, this.tubeItem)) {
						TubeLib.WorldRoute nr = new TubeLib.WorldRoute(wc,
								start, side1, weight
										+ itc.tubeWeight(side1, this.state));
						nr.solved = true;
						super.scanpos.add(nr);
					} else if (itc.tubeItemCanEnter(side1, 0, this.tubeItem)) {
						if (itc.canRouteItems()) {
							if (!super.scanmap.contains(wc)) {
								super.scanmap.add(wc);
								super.scanpos.add(new TubeLib.WorldRoute(wc,
										start, side1, weight
												+ itc.tubeWeight(side1,
														this.state)));
							}
						}
					}
				}
			}
		}
	}
	
	public static class RequestRouteFinder extends TubeLib.RouteFinder {
		
		TubeItem tubeItem;
		
		public RequestRouteFinder(World world, TubeItem item) {
			super(world);
			this.tubeItem = item;
		}
		
		@Override
		public void addPoint(WorldCoord wc, int st, int side, int weight) {
			ITubeRequest itr = (ITubeRequest) CoreLib.getTileEntity(
					super.worldObj, wc, ITubeRequest.class);
			if (itr != null) {
				if (itr.requestTubeItem(this.tubeItem, false)) {
					TubeLib.WorldRoute itc1 = new TubeLib.WorldRoute(wc, 0,
							side, weight);
					itc1.solved = true;
					super.scanpos.add(itc1);
				}
				
			} else {
				ITubeConnectable itc = (ITubeConnectable) CoreLib
						.getTileEntity(super.worldObj, wc,
								ITubeConnectable.class);
				if (itc != null) {
					int side1 = (side ^ 1) & 255;
					if (itc.tubeItemCanEnter(side1, 0, this.tubeItem)) {
						if (itc.canRouteItems()) {
							if (!super.scanmap.contains(wc)) {
								super.scanmap.add(wc);
								super.scanpos.add(new TubeLib.WorldRoute(wc,
										st, side1, weight
												+ itc.tubeWeight(side1, 0)));
							}
						}
					}
				}
			}
		}
	}
	
	private static class RouteFinder {
		
		int startDir = 0;
		TubeLib.WorldRoute result;
		World worldObj;
		HashSet scanmap = new HashSet();
		PriorityQueue scanpos = new PriorityQueue();
		
		public RouteFinder(World world) {
			this.worldObj = world;
		}
		
		public void addPoint(WorldCoord wc, int start, int side, int weight) {
			ITubeConnectable itc = (ITubeConnectable) CoreLib.getTileEntity(
					this.worldObj, wc, ITubeConnectable.class);
			if (itc != null) {
				if (itc.canRouteItems()) {
					if (!this.scanmap.contains(wc)) {
						this.scanmap.add(wc);
						this.scanpos.add(new TubeLib.WorldRoute(wc, start,
								side ^ 1, weight));
					}
				}
			}
		}
		
		public int find(WorldCoord wc, int sides) {
			for (int wr = 0; wr < 6; ++wr) {
				if ((sides & 1 << wr) != 0) {
					WorldCoord cons = wc.copy();
					cons.step(wr);
					this.addPoint(cons, wr, wr, wr == this.startDir ? 0 : 1);
				}
			}
			
			while (this.scanpos.size() > 0) {
				TubeLib.WorldRoute var7 = (TubeLib.WorldRoute) this.scanpos
						.poll();
				if (var7.solved) {
					this.result = var7;
					return var7.start;
				}
				
				int var8 = TubeLib.getConnections(this.worldObj, var7.wc.x,
						var7.wc.y, var7.wc.z);
				
				for (int n = 0; n < 6; ++n) {
					if (n != var7.side && (var8 & 1 << n) != 0) {
						WorldCoord wcp = var7.wc.copy();
						wcp.step(n);
						this.addPoint(wcp, var7.start, n, var7.weight + 2);
					}
				}
			}
			
			return -1;
		}
		
		public WorldCoord getResultPoint() {
			return this.result.wc;
		}
	}
	
	private static class WorldRoute implements Comparable {
		
		public WorldCoord wc;
		public int start;
		public int side;
		public int weight;
		public boolean solved = false;
		
		public WorldRoute(WorldCoord w, int st, int s, int wt) {
			this.wc = w;
			this.start = st;
			this.side = s;
			this.weight = wt;
		}
		
		@Override
		public int compareTo(Object obj) {
			TubeLib.WorldRoute wr = (TubeLib.WorldRoute) obj;
			return this.weight - wr.weight;
		}
	}
}
