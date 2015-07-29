package com.eloraam.redpower.core;

import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class WirePathfinder {

   HashSet scanmap;
   LinkedList scanpos;


   public void init() {
      this.scanmap = new HashSet();
      this.scanpos = new LinkedList();
   }

   public void addSearchBlock(WorldCoord wc) {
      if(!this.scanmap.contains(wc)) {
         this.scanmap.add(wc);
         this.scanpos.addLast(wc);
      }
   }

   private void addIndBl(WorldCoord wc, int d1, int d2) {
      wc = wc.coordStep(d1);
      int d3;
      switch(d1) {
      case 0:
         d3 = d2 + 2;
         break;
      case 1:
         d3 = d2 + 2;
         break;
      case 2:
         d3 = d2 + (d2 & 2);
         break;
      case 3:
         d3 = d2 + (d2 & 2);
         break;
      case 4:
         d3 = d2;
         break;
      default:
         d3 = d2;
      }

      wc.step(d3);
      this.addSearchBlock(wc);
   }

   public void addSearchBlocks(WorldCoord wc, int cons, int indcon) {
      int a;
      for(a = 0; a < 6; ++a) {
         if((cons & RedPowerLib.getConDirMask(a)) > 0) {
            this.addSearchBlock(wc.coordStep(a));
         }
      }

      for(a = 0; a < 6; ++a) {
         for(int b = 0; b < 4; ++b) {
            if((indcon & 1 << a * 4 + b) > 0) {
               this.addIndBl(wc, a, b);
            }
         }
      }

   }

   public boolean step(WorldCoord coord) {
      return false;
   }

   public boolean iterate() {
      if(this.scanpos.size() == 0) {
         return false;
      } else {
         WorldCoord wc = (WorldCoord)this.scanpos.removeFirst();
         return this.step(wc);
      }
   }
}
