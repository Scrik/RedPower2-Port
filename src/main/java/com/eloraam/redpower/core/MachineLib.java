package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MachineLib {
	
	public static IInventory getInventory(World world, WorldCoord wc) {
		IInventory inv = (IInventory) CoreLib.getTileEntity(world, wc, IInventory.class);
		if (!(inv instanceof TileEntityChest)) {
			return inv;
		} else {
			TileEntityChest tec = (TileEntityChest) CoreLib.getTileEntity(world, wc.x - 1, wc.y, wc.z, TileEntityChest.class);
			if (tec != null) {
				return new InventoryLargeChest("Large chest", tec, inv);
			} else {
				tec = (TileEntityChest) CoreLib.getTileEntity(world, wc.x + 1, wc.y, wc.z, TileEntityChest.class);
				if (tec != null) {
					return new InventoryLargeChest("Large chest", inv, tec);
				} else {
					tec = (TileEntityChest) CoreLib.getTileEntity(world, wc.x, wc.y, wc.z - 1, TileEntityChest.class);
					if (tec != null) {
						return new InventoryLargeChest("Large chest", tec, inv);
					} else {
						tec = (TileEntityChest) CoreLib.getTileEntity(world, wc.x, wc.y, wc.z + 1, TileEntityChest.class);
						return tec != null ? new InventoryLargeChest("Large chest", inv, tec) : inv;
					}
				}
			}
		}
	}
	
	public static IInventory getSideInventory(World world, WorldCoord wc, int side, boolean push) {
		IInventory inv = getInventory(world, wc);
		if (inv == null) {
			return null;
		} else if (inv instanceof ISidedInventory) {
			ISidedInventory isi = (ISidedInventory) inv;
			int[] slots = isi.getAccessibleSlotsFromSide(side);
			return new MachineLib.SubInventory(inv, slots);
		} else {
			return inv;
		}
	}
	
	public static boolean addToInventoryCore(World world, ItemStack ist, WorldCoord wc, int side, boolean act) {
		IInventory inv = getInventory(world, wc);
		if (inv == null) {
			return false;
		} else {
			int [] slots = new int[]{};
			if (inv instanceof ISidedInventory) {
				ISidedInventory isi = (ISidedInventory) inv;
				slots = isi.getAccessibleSlotsFromSide(side);
			}
			return addToInventoryCore(inv, ist, slots, act);
		}
	}
	
	public static boolean addToInventoryCore(IInventory inv, ItemStack ist, List<Integer> slots, boolean act) {
		int n;
		ItemStack invst;
		for (int i : slots) {
			n = i;
			invst = inv.getStackInSlot(n);
			if (invst == null) {
				if (!act) {
					return true;
				}
			} else if (ist.isItemEqual(invst) && ItemStack.areItemStackTagsEqual(ist, invst)) {
				int dfs = Math.min(invst.getMaxStackSize(), inv.getInventoryStackLimit());
				dfs -= invst.stackSize;
				if (dfs > 0) {
					int si = Math.min(dfs, ist.stackSize);
					if (!act) {
						return true;
					}
					
					invst.stackSize += si;
					inv.setInventorySlotContents(n, invst);
					ist.stackSize -= si;
					if (ist.stackSize == 0) {
						return true;
					}
				}
			}
		}
		if (!act) {
			return false;
		} else {
			for (int i : slots) {
				n = i;
				invst = inv.getStackInSlot(n);
				if (invst == null) {
					if (inv.getInventoryStackLimit() >= ist.stackSize) {
						inv.setInventorySlotContents(n, ist);
						return true;
					}
					
					inv.setInventorySlotContents(n,ist.splitStack(inv.getInventoryStackLimit()));
				}
			}
			return false;
		}
	}
	
	public static boolean addToInventoryCore(IInventory inv, ItemStack ist, int[] slots, boolean act) {
		int n;
		ItemStack invst;
		for (int i : slots) {
			n = i;
			invst = inv.getStackInSlot(n);
			if (invst == null) {
				if (!act) {
					return true;
				}
			} else if (ist.isItemEqual(invst) && ItemStack.areItemStackTagsEqual(ist, invst)) {
				int dfs = Math.min(invst.getMaxStackSize(), inv.getInventoryStackLimit());
				dfs -= invst.stackSize;
				if (dfs > 0) {
					int si = Math.min(dfs, ist.stackSize);
					if (!act) {
						return true;
					}
					
					invst.stackSize += si;
					inv.setInventorySlotContents(n, invst);
					ist.stackSize -= si;
					if (ist.stackSize == 0) {
						return true;
					}
				}
			}
		}
		if (!act) {
			return false;
		} else {
			for (int i : slots) {
				n = i;
				invst = inv.getStackInSlot(n);
				if (invst == null) {
					if (inv.getInventoryStackLimit() >= ist.stackSize) {
						inv.setInventorySlotContents(n, ist);
						return true;
					}
					
					inv.setInventorySlotContents(n,ist.splitStack(inv.getInventoryStackLimit()));
				}
			}
			return false;
		}
	}
	
	public static boolean addToInventory(World world, ItemStack ist, WorldCoord wc, int side) {
		return addToInventoryCore(world, ist, wc, side, true);
	}
	
	public static boolean canAddToInventory(World world, ItemStack ist, WorldCoord wc, int side) {
		return addToInventoryCore(world, ist, wc, side, false);
	}
	
	public static void ejectItem(World world, WorldCoord wc, ItemStack ist, int dir) {
		wc = wc.copy();
		wc.step(dir);
		EntityItem item = new EntityItem(world, wc.x + 0.5D, wc.y + 0.5D, wc.z + 0.5D, ist);
		item.motionX = 0.0D;
		item.motionY = 0.0D;
		item.motionZ = 0.0D;
		switch (dir) {
			case 0:
				item.motionY = -0.3D;
				break;
			case 1:
				item.motionY = 0.3D;
				break;
			case 2:
				item.motionZ = -0.3D;
				break;
			case 3:
				item.motionZ = 0.3D;
				break;
			case 4:
				item.motionX = -0.3D;
				break;
			default:
				item.motionX = 0.3D;
		}
		
		item.delayBeforeCanPickup = 10;
		world.spawnEntityInWorld(item);
	}
	
	public static boolean handleItem(World world, ItemStack ist, WorldCoord wc, int side) {
		WorldCoord dest = wc.copy();
		dest.step(side);
		if (ist.stackSize == 0) {
			return true;
		} else if (TubeLib.addToTubeRoute(world, ist, wc, dest, side ^ 1)) {
			return true;
		} else if (addToInventory(world, ist, dest, (side ^ 1) & 63)) {
			return true;
		} else {
			TileEntity te = (TileEntity) CoreLib.getTileEntity(world, dest, TileEntity.class);
			if (!(te instanceof IInventory) && !(te instanceof ITubeConnectable)) {
				if (world.getBlock(dest.x, dest.y, dest.z).isSideSolid(world, dest.x, dest.y, dest.z, ForgeDirection.getOrientation(side ^ 1))) {
					return false;
				} else {
					ejectItem(world, wc, ist, side);
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	public static boolean handleItem(World world, TubeItem ti, WorldCoord wc, int side) {
		WorldCoord dest = wc.copy();
		dest.step(side);
		if (ti.item.stackSize == 0) {
			return true;
		} else if (TubeLib.addToTubeRoute(world, ti, wc, dest, side ^ 1)) {
			return true;
		} else if (addToInventory(world, ti.item, dest, (side ^ 1) & 63)) {
			return true;
		} else {
			TileEntity te = (TileEntity) CoreLib.getTileEntity(world, dest, TileEntity.class);
			if (!(te instanceof IInventory) && !(te instanceof ITubeConnectable)) {
				if (world.getBlock(dest.x, dest.y, dest.z).isSideSolid(world, dest.x, dest.y, dest.z, ForgeDirection.getOrientation(side ^ 1))) {
					return false;
				} else {
					ejectItem(world, wc, ti.item, side);
					return true;
				}
			} else {
				return false;
			}
		}
	}
	
	public static boolean addToRandomInventory(World world, ItemStack ist,
			int i, int j, int k) {
		return false;
	}
	
	public static int compareItem(ItemStack a, ItemStack b) {
		if (Item.getIdFromItem(a.getItem()) != Item.getIdFromItem(b.getItem())) {
			return Item.getIdFromItem(a.getItem()) - Item.getIdFromItem(b.getItem());
		} else if (a.getItemDamage() == b.getItemDamage()) {
			return 0;
		} else if (a.getItem().getHasSubtypes()) {
			return a.getItemDamage() - b.getItemDamage();
		} else {
			int d1 = a.getItemDamage() <= 1 ? -1 : (a.getItemDamage() == a
					.getMaxDamage() - 1 ? 1 : 0);
			int d2 = b.getItemDamage() <= 1 ? -1 : (b.getItemDamage() == b
					.getMaxDamage() - 1 ? 1 : 0);
			return d1 - d2;
		}
	}
	
	public static MachineLib.FilterMap makeFilterMap(ItemStack[] ist) {
		return new MachineLib.FilterMap(ist);
	}
	
	public static MachineLib.FilterMap makeFilterMap(ItemStack[] ist, int st,
			int ln) {
		ItemStack[] it = new ItemStack[ln];
		System.arraycopy(ist, st, it, 0, ln);
		return new MachineLib.FilterMap(it);
	}
	
	public static int[] genMatchCounts(MachineLib.FilterMap map) {
		int[] tr = new int[map.filter.length];
		
		for (int n = 0; n < map.filter.length; ++n) {
			ItemStack ist = map.filter[n];
			if (ist != null && ist.stackSize != 0) {
				ArrayList<Integer> arl = (ArrayList<Integer>) map.map.get(ist);
				Integer m;
				if (arl != null && ((Integer) arl.get(0)).intValue() == n) {
					for (Iterator<Integer> i$ = arl.iterator(); i$.hasNext(); tr[n] += map.filter[m
							.intValue()].stackSize) {
						m = (Integer) i$.next();
					}
				}
			}
		}
		
		return tr;
	}
	
	public static int decMatchCount(MachineLib.FilterMap map, int[] mc, ItemStack ist) {
		ArrayList<Integer> arl = (ArrayList<Integer>)map.map.get(ist);
		if (arl == null) {
			return 0;
		} else {
			int n = ((Integer) arl.get(0)).intValue();
			int tr = Math.min(mc[n], ist.stackSize);
			mc[n] -= tr;
			return tr;
		}
	}
	
	public static int getMatchCount(MachineLib.FilterMap map, int[] mc, ItemStack ist) {
		ArrayList<Integer> arl = (ArrayList<Integer>)map.map.get(ist);
		if (arl == null) {
			return 0;
		} else {
			int n = ((Integer) arl.get(0)).intValue();
			int tr = Math.min(mc[n], ist.stackSize);
			return tr;
		}
	}
	
	public static boolean isMatchEmpty(int[] mc) {
		for (int i = 0; i < mc.length; ++i) {
			if (mc[i] > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void decMatchCounts(MachineLib.FilterMap map, int[] mc, IInventory inv, List<Integer> slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				decMatchCount(map, mc, ist);
			}
		}
	}
	
	public static void decMatchCounts(MachineLib.FilterMap map, int[] mc, IInventory inv, int[] slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				decMatchCount(map, mc, ist);
			}
		}
	}
	
	public static boolean matchOneStack(MachineLib.FilterMap map, IInventory inv, int[] slots, int pos) {
		ItemStack match = map.filter[pos];
		int fc = match == null ? 1 : match.stackSize;
		
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				if (match == null) {
					return true;
				}
				
				if (compareItem(match, ist) == 0) {
					int m = Math.min(ist.stackSize, fc);
					fc -= m;
					if (fc <= 0) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static int matchAnyStack(MachineLib.FilterMap map, IInventory inv, int[] slots) {
		int[] mc = new int[map.filter.length];
		
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				ArrayList<Integer> arl = (ArrayList<Integer>)map.map.get(ist);
				if (arl != null) {
					Iterator<Integer> i$ = arl.iterator();
					
					while (i$.hasNext()) {
						Integer m = (Integer) i$.next();
						int var10001 = m.intValue();
						mc[var10001] += ist.stackSize;
						if (mc[m.intValue()] >= map.filter[m.intValue()].stackSize) {
							return m.intValue();
						}
					}
				}
			}
		}
		
		return -1;
	}
	
	public static int matchAnyStackCol(MachineLib.FilterMap map, IInventory inv, int[] slots, int col) {
		int[] mc = new int[5];
		
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				ArrayList<Integer> arl = (ArrayList<Integer>)map.map.get(ist);
				if (arl != null) {
					Iterator<Integer> i$ = arl.iterator();
					
					while (i$.hasNext()) {
						Integer m = (Integer) i$.next();
						if ((m.intValue() & 7) == col) {
							int s = m.intValue() >> 3;
							mc[s] += ist.stackSize;
							if (mc[s] >= map.filter[m.intValue()].stackSize) {
								return m.intValue();
							}
						}
					}
				}
			}
		}
		
		return -1;
	}
	
	public static boolean matchAllCol(MachineLib.FilterMap map, IInventory inv, int[] slots, int col) {
		int[] mc = new int[5];
		
		for (int any : slots) {
			ItemStack n = inv.getStackInSlot(any);
			if (n != null && n.stackSize != 0) {
				ArrayList<Integer> ct = (ArrayList<Integer>) map.map.get(n);
				if (ct != null) {
					int ss = n.stackSize;
					Iterator<Integer> i$ = ct.iterator();
					
					while (i$.hasNext()) {
						Integer m = (Integer) i$.next();
						if ((m.intValue() & 7) == col) {
							int c = m.intValue() >> 3;
							int s1 = Math.min(ss,
									map.filter[m.intValue()].stackSize - mc[c]);
							mc[c] += s1;
							ss -= s1;
							if (ss == 0) {
								break;
							}
						}
					}
				}
			}
		}
		
		boolean var14 = false;
		
		for (int var15 = 0; var15 < 5; ++var15) {
			ItemStack var16 = map.filter[var15 * 8 + col];
			if (var16 != null && var16.stackSize != 0) {
				var14 = true;
				if (var16.stackSize > mc[var15]) {
					return false;
				}
			}
		}
		
		return var14;
	}
	
	public static boolean emptyInventory(IInventory inv, List<Integer> slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean emptyInventory(IInventory inv, int[] slots) {
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				return false;
			}
		}
		return true;
	}
	
	public static ItemStack collectOneStack(IInventory inv, int[] slots, ItemStack match) {
		ItemStack tr = null;
		int fc = match == null ? 1 : match.stackSize;
		
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				if (match == null) {
					inv.setInventorySlotContents(n, (ItemStack) null);
					return ist;
				}
				
				if (compareItem(match, ist) == 0) {
					int m = Math.min(ist.stackSize, fc);
					if (tr == null) {
						tr = inv.decrStackSize(n, m);
					} else {
						inv.decrStackSize(n, m);
						tr.stackSize += m;
					}
					
					fc -= m;
					if (fc <= 0) {
						break;
					}
				}
			}
		}
		
		return tr;
	}
	
	public static ItemStack collectOneStackFuzzy(IInventory inv, int[] slots, ItemStack match) {
		ItemStack tr = null;
		int fc = match == null ? 1 : match.getMaxStackSize();
		
		for (int n : slots) {
			ItemStack ist = inv.getStackInSlot(n);
			if (ist != null && ist.stackSize != 0) {
				if (match == null) {
					inv.setInventorySlotContents(n, (ItemStack) null);
					return ist;
				}
				
				if (compareItem(match, ist) == 0) {
					int m = Math.min(ist.stackSize, fc);
					if (tr == null) {
						tr = inv.decrStackSize(n, m);
					} else {
						inv.decrStackSize(n, m);
						tr.stackSize += m;
					}
					
					fc -= m;
					if (fc <= 0) {
						break;
					}
				}
			}
		}
		
		return tr;
	}
	
	public static class FilterMap {
		
		protected TreeMap<ItemStack, ArrayList<Integer>> map;
		protected ItemStack[] filter;
		
		public FilterMap(ItemStack[] filt) {
			this.filter = filt;
			this.map = new TreeMap<ItemStack, ArrayList<Integer>>(new Comparator<ItemStack>() {
				public int compare(ItemStack o1, ItemStack o2) {
					return MachineLib.compareItem(o1, o2);
				}
			});
			
			for (int i = 0; i < filt.length; ++i) {
				if (filt[i] != null && filt[i].stackSize != 0) {
					ArrayList<Integer> arl = this.map.get(filt[i]);
					if (arl == null) {
						arl = new ArrayList<Integer>();
						this.map.put(filt[i], arl);
					}
					arl.add(Integer.valueOf(i));
				}
			}
		}
		
		public int size() {
			return this.map.size();
		}
		
		public boolean containsKey(ItemStack ist) {
			return this.map.containsKey(ist);
		}
		
		public int firstMatch(ItemStack ist) {
			ArrayList<Integer> arl = (ArrayList<Integer>)this.map.get(ist);
			return arl == null ? -1 : ((Integer) arl.get(0)).intValue();
		}
	}
	
	public static class SubInventory implements IInventory {
		IInventory parent;
		int[] slots;
		
		SubInventory(IInventory par, int[] sl) {
			this.parent = par;
			this.slots=sl;
		}
		
		@Override
		public int getSizeInventory() {
			return this.slots.length;
		}
		
		@Override
		public ItemStack getStackInSlot(int idx) {
			for(int i : slots) {
				if(i == idx) {
					return this.parent.getStackInSlot(idx);
				}
			}
			return null;
		}
		
		@Override
		public ItemStack decrStackSize(int idx, int num) {
			for(int i : slots) {
				if(i == idx) {
					return this.parent.decrStackSize(idx, num);
				}
			}
			return null;
		}
		
		@Override
		public ItemStack getStackInSlotOnClosing(int idx) {
			for(int i : slots) {
				if(i == idx) {
					return this.parent.getStackInSlotOnClosing(idx);
				}
			}
			return null;
		}
		
		@Override
		public void setInventorySlotContents(int idx, ItemStack ist) {
			for(int i : slots) {
				if(i == idx) {
					this.parent.setInventorySlotContents(idx, ist);
				}
			}
		}
		
		@Override
		public String getInventoryName() {
			return this.parent.getInventoryName();
		}
		
		@Override
		public int getInventoryStackLimit() {
			return this.parent.getInventoryStackLimit();
		}
		
		@Override
		public void markDirty() {
			this.parent.markDirty();
		}
		
		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			return this.parent.isUseableByPlayer(var1);
		}
		
		@Override
		public void openInventory() {
		}
		
		@Override
		public void closeInventory() {
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true; //TODO: Maybe not
		}
		
		@Override
		public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
			return true; //TODO: Maybe not
		}
	}
}
