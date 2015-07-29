package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.WorldCoord;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FrameLib {
	
	public static class FrameSolver {
		
		HashSet<WorldCoord> scanmap;
		LinkedList<WorldCoord> scanpos;
		HashSet<WorldCoord> framemap;
		LinkedList<WorldCoord> frameset;
		LinkedList<WorldCoord> clearset;
		int movedir;
		WorldCoord motorpos;
		public boolean valid = true;
		World worldObj;
		
		public FrameSolver(World world, WorldCoord wc, WorldCoord motorp, int movdir) {
			this.movedir = movdir;
			this.motorpos = motorp;
			this.worldObj = world;
			this.scanmap = new HashSet<WorldCoord>();
			this.scanpos = new LinkedList<WorldCoord>();
			this.framemap = new HashSet<WorldCoord>();
			this.frameset = new LinkedList<WorldCoord>();
			this.clearset = new LinkedList<WorldCoord>();
			this.scanmap.add(motorp);
			this.scanmap.add(wc);
			this.scanpos.addLast(wc);
		}
		
		boolean step() {
			WorldCoord wc = (WorldCoord) this.scanpos.removeFirst();
			if (wc.y >= 0 && wc.y < this.worldObj.getHeight() - 1) {
				Block block = this.worldObj.getBlock(wc.x, wc.y, wc.z);
				if (this.movedir >= 0
						&& !this.worldObj.blockExists(wc.x, wc.y, wc.z)) {
					this.valid = false;
					return false;
				} else if (this.worldObj.isAirBlock(wc.x, wc.y, wc.z)) {
					return false;
				} else if (this.movedir >= 0
						&& block.getBlockHardness(this.worldObj, wc.x, wc.y,
								wc.z) < 0.0F) {
					this.valid = false;
					return false;
				} else {
					this.framemap.add(wc);
					this.frameset.addLast(wc);
					IFrameLink ifl = (IFrameLink) CoreLib.getTileEntity(
							this.worldObj, wc, IFrameLink.class);
					if (ifl == null) {
						return true;
					} else if (ifl.isFrameMoving() && this.movedir >= 0) {
						this.valid = false;
						return true;
					} else {
						for (int i = 0; i < 6; ++i) {
							if (ifl.canFrameConnectOut(i)) {
								WorldCoord sp = wc.coordStep(i);
								if (!this.scanmap.contains(sp)) {
									IFrameLink if2 = (IFrameLink) CoreLib
											.getTileEntity(this.worldObj, sp,
													IFrameLink.class);
									if (if2 != null) {
										if (!if2.canFrameConnectIn((i ^ 1) & 255)) {
											continue;
										}
										
										if (this.movedir < 0) {
											WorldCoord wcls = if2
													.getFrameLinkset();
											if (wcls == null
													|| !wcls.equals(this.motorpos)) {
												continue;
											}
										}
									}
									
									this.scanmap.add(sp);
									this.scanpos.addLast(sp);
								}
							}
						}
						
						return true;
					}
				}
			} else {
				return false;
			}
		}
		
		public boolean solve() {
			while (this.valid && this.scanpos.size() > 0) {
				this.step();
			}
			
			return this.valid;
		}
		
		public boolean solveLimit(int limit) {
			while (true) {
				if (this.valid && this.scanpos.size() > 0) {
					if (this.step()) {
						--limit;
					}
					
					if (limit != 0) {
						continue;
					}
					
					return false;
				}
				
				return this.valid;
			}
		}
		
		@SuppressWarnings("unchecked")
		public boolean addMoved() {
			LinkedList<WorldCoord> fsstp = (LinkedList<WorldCoord>)this.frameset.clone();
			Iterator<WorldCoord> i$ = fsstp.iterator();
			
			while (i$.hasNext()) {
				WorldCoord wc = (WorldCoord) i$.next();
				WorldCoord sp = wc.coordStep(this.movedir);
				if (!this.worldObj.blockExists(sp.x, sp.y, sp.z)) {
					this.valid = false;
					return false;
				}
				
				if (!this.framemap.contains(sp)) {
					if (!this.worldObj.isAirBlock(wc.x, wc.y, wc.z)) {
						if (!this.worldObj.canPlaceEntityOnSide(Blocks.stone, sp.x, sp.y, sp.z, true, 0, (Entity) null, (ItemStack)null)) { //TODO: MAYBE TOO BAD
							this.valid = false;
							return false;
						}
						this.clearset.add(sp);
					}
					this.framemap.add(sp);
					this.frameset.addLast(sp);
				}
			}
			
			return this.valid;
		}
		
		public void sort(int dir) {
			Collections.sort(this.frameset, WorldCoord.getCompareDir(dir));
		}
		
		public LinkedList<WorldCoord> getFrameSet() {
			return this.frameset;
		}
		
		public LinkedList<WorldCoord> getClearSet() {
			return this.clearset;
		}
	}
}
