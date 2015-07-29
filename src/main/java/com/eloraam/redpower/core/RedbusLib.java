package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.IWiring;
import com.eloraam.redpower.core.RedbusLib;
import com.eloraam.redpower.core.WirePathfinder;
import com.eloraam.redpower.core.WorldCoord;

import net.minecraft.world.IBlockAccess;

public class RedbusLib {

   public static IRedbusConnectable getAddr(IBlockAccess iba, WorldCoord pos, int addr) {
      RedbusLib.RedbusPathfinder pf = new RedbusLib.RedbusPathfinder(iba, addr);
      pf.addSearchBlocks(pos, 16777215, 0);

      while(pf.iterate()) {
         ;
      }

      return pf.result;
   }

   private static class RedbusPathfinder extends WirePathfinder {

      public IRedbusConnectable result = null;
      IBlockAccess iba;
      int addr;


      public RedbusPathfinder(IBlockAccess ib, int ad) {
         this.iba = ib;
         this.addr = ad;
         this.init();
      }

      @Override
	public boolean step(WorldCoord wc) {
         IRedbusConnectable irb = (IRedbusConnectable)CoreLib.getTileEntity(this.iba, wc, IRedbusConnectable.class);
         if(irb != null && irb.rbGetAddr() == this.addr) {
            this.result = irb;
            return false;
         } else {
            IWiring iw = (IWiring)CoreLib.getTileEntity(this.iba, wc, IWiring.class);
            if(iw == null) {
               return true;
            } else {
               this.addSearchBlocks(wc, iw.getConnectionMask(), iw.getExtConnectionMask());
               return true;
            }
         }
      }
   }
}
