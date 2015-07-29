package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TubeItem;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TubeBuffer {
	
	LinkedList<TubeItem> buffer = null;
	public boolean plugged = false;
	
	public boolean isEmpty() {
		return this.buffer == null ? true : this.buffer.size() == 0;
	}
	
	public TubeItem getLast() {
		return this.buffer == null ? null : (TubeItem) this.buffer.getLast();
	}
	
	public void add(TubeItem ti) {
		if (this.buffer == null) {
			this.buffer = new LinkedList<TubeItem>();
		}
		
		this.buffer.addFirst(ti);
	}
	
	public void addNew(ItemStack ist) {
		if (this.buffer == null) {
			this.buffer = new LinkedList<TubeItem>();
		}
		
		this.buffer.addFirst(new TubeItem(0, ist));
	}
	
	public void addNewColor(ItemStack ist, int col) {
		if (this.buffer == null) {
			this.buffer = new LinkedList<TubeItem>();
		}
		
		TubeItem ti = new TubeItem(0, ist);
		ti.color = (byte) col;
		this.buffer.addFirst(ti);
	}
	
	public void addAll(Collection<?> col) {
		if (this.buffer == null) {
			this.buffer = new LinkedList<TubeItem>();
		}
		
		Iterator<?> i$ = col.iterator();
		
		while (i$.hasNext()) {
			ItemStack ist = (ItemStack) i$.next();
			this.buffer.add(new TubeItem(0, ist));
		}
		
	}
	
	public void addBounce(TubeItem ti) {
		if (this.buffer == null) {
			this.buffer = new LinkedList<TubeItem>();
		}
		
		this.buffer.addLast(ti);
		this.plugged = true;
	}
	
	public void pop() {
		this.buffer.removeLast();
		if (this.buffer.size() == 0) {
			this.plugged = false;
		}
	}
	
	public int size() {
		return this.buffer == null ? 0 : this.buffer.size();
	}
	
	public void onRemove(TileEntity te) {
		if (this.buffer != null) {
			Iterator<TubeItem> iter = this.buffer.iterator();
			
			while (iter.hasNext()) {
				TubeItem ti = (TubeItem) iter.next();
				if (ti != null && ti.item.stackSize > 0) {
					CoreLib.dropItem(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, ti.item);
				}
			}
		}
	}
	
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagList items = nbttagcompound.getTagList("Buffer", 10); // TODO: Care
		if (items.tagCount() > 0) {
			this.buffer = new LinkedList<TubeItem>();
			
			for (int b = 0; b < items.tagCount(); ++b) {
				NBTTagCompound item = (NBTTagCompound) items
						.getCompoundTagAt(b);
				this.buffer.add(TubeItem.newFromNBT(item));
			}
		}
		
		byte var5 = nbttagcompound.getByte("Plug");
		this.plugged = var5 > 0;
	}
	
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList items = new NBTTagList();
		if (this.buffer != null) {
			Iterator<TubeItem> i$ = this.buffer.iterator();
			
			while (i$.hasNext()) {
				TubeItem ti = (TubeItem) i$.next();
				NBTTagCompound item = new NBTTagCompound();
				ti.writeToNBT(item);
				items.appendTag(item);
			}
		}
		nbttagcompound.setTag("Buffer", items);
		nbttagcompound.setByte("Plug", (byte) (this.plugged ? 1 : 0));
	}
}
