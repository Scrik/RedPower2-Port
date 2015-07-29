package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;

import java.io.File;

import net.minecraft.world.World;

public class DiskLib {

   public static File getSaveDir(World world) {
      File tr = new File(RedPowerCore.getSaveDir(world), "redpower");
      tr.mkdirs();
      return tr;
   }

   public static String generateSerialNumber(World world) {
      String tr = "";

      for(int i = 0; i < 16; ++i) {
         tr = tr + String.format("%01x", new Object[]{Integer.valueOf(world.rand.nextInt(16))});
      }

      return tr;
   }

   public static File getDiskFile(File dir, String serno) {
      return new File(dir, String.format("disk_%s.img", new Object[]{serno}));
   }
}
