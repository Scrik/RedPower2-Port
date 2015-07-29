package com.eloraam.redpower.core;

import java.util.Comparator;

import net.minecraft.tileentity.TileEntity;

public class WorldCoord implements Comparable<WorldCoord> {
	
	public int x;
	public int y;
	public int z;
	
	public WorldCoord(int xi, int yi, int zi) {
		this.x = xi;
		this.y = yi;
		this.z = zi;
	}
	
	public WorldCoord(TileEntity te) {
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
	}
	
	public WorldCoord copy() {
		return new WorldCoord(this.x, this.y, this.z);
	}
	
	public WorldCoord coordStep(int dir) {
		switch (dir) {
			case 0:
				return new WorldCoord(this.x, this.y - 1, this.z);
			case 1:
				return new WorldCoord(this.x, this.y + 1, this.z);
			case 2:
				return new WorldCoord(this.x, this.y, this.z - 1);
			case 3:
				return new WorldCoord(this.x, this.y, this.z + 1);
			case 4:
				return new WorldCoord(this.x - 1, this.y, this.z);
			default:
				return new WorldCoord(this.x + 1, this.y, this.z);
		}
	}
	
	public void set(WorldCoord wc) {
		this.x = wc.x;
		this.y = wc.y;
		this.z = wc.z;
	}
	
	public int squareDist(int xi, int yi, int zi) {
		return (xi - this.x) * (xi - this.x) + (yi - this.y) * (yi - this.y)
				+ (zi - this.z) * (zi - this.z);
	}
	
	public void step(int dir) {
		switch (dir) {
			case 0:
				--this.y;
				break;
			case 1:
				++this.y;
				break;
			case 2:
				--this.z;
				break;
			case 3:
				++this.z;
				break;
			case 4:
				--this.x;
				break;
			default:
				++this.x;
		}
		
	}
	
	public void step(int dir, int dist) {
		switch (dir) {
			case 0:
				this.y -= dist;
				break;
			case 1:
				this.y += dist;
				break;
			case 2:
				this.z -= dist;
				break;
			case 3:
				this.z += dist;
				break;
			case 4:
				this.x -= dist;
				break;
			default:
				this.x += dist;
		}
		
	}
	
	public static int getRightDir(int dir) {
		if (dir < 2) {
			return dir;
		} else {
			switch (dir) {
				case 0:
					return 0;
				case 1:
					return 1;
				case 2:
					return 4;
				case 3:
					return 5;
				case 4:
					return 3;
				default:
					return 2;
			}
		}
	}
	
	public static int getIndStepDir(int d1, int d2) {
		switch (d1) {
			case 0:
				return d2 + 2;
			case 1:
				return d2 + 2;
			case 2:
				return d2 + (d2 & 2);
			case 3:
				return d2 + (d2 & 2);
			case 4:
				return d2;
			default:
				return d2;
		}
	}
	
	public void indStep(int d1, int d2) {
		this.step(d1);
		this.step(getIndStepDir(d1, d2));
	}
	
	@Override
	public int hashCode() {
		int c1 = Integer.valueOf(this.x).hashCode();
		int c2 = Integer.valueOf(this.y).hashCode();
		int c3 = Integer.valueOf(this.z).hashCode();
		return c1 + 31 * (c2 + 31 * c3);
	}
	
	@Override
	public int compareTo(WorldCoord wc) {
		return this.x == wc.x ? (this.y == wc.y ? this.z - wc.z : this.y - wc.y) : this.x - wc.x;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorldCoord)) {
			return false;
		} else {
			WorldCoord wc = (WorldCoord) obj;
			return this.x == wc.x && this.y == wc.y && this.z == wc.z;
		}
	}
	
	public static Comparator<WorldCoord> getCompareDir(int dir) {
		return new WorldCoord.WCComparator(dir);
	}
	
	public static class WCComparator implements Comparator<WorldCoord> {
		
		int dir;
		
		private WCComparator(int d) {
			this.dir = d;
		}
		
		@Override
		public int compare(WorldCoord wa, WorldCoord wb) {
			switch (this.dir) {
				case 0:
					return wa.y - wb.y;
				case 1:
					return wb.y - wa.y;
				case 2:
					return wa.z - wb.z;
				case 3:
					return wb.z - wa.z;
				case 4:
					return wa.x - wb.x;
				default:
					return wb.x - wa.x;
			}
		}
	}
}
