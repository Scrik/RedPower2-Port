package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.wiring.TileWiring;

import net.minecraft.nbt.NBTTagCompound;

public class TileCable extends TileWiring implements IRedPowerWiring {

   public short[] PowerState = new short[16];


   @Override
public float getWireHeight() {
      return 0.25F;
   }

   @Override
public int getExtendedID() {
      return 3;
   }

   @Override
public int getConnectClass(int side) {
      return 18 + super.Metadata;
   }

   @Override
public int scanPoweringStrength(int cons, int ch) {
      return 0;
   }

   @Override
public int getCurrentStrength(int cons, int ch) {
      return ch >= 1 && ch <= 16?((cons & this.getConnectableMask()) == 0?-1:this.PowerState[ch - 1]):-1;
   }

   @Override
public void updateCurrentStrength() {
      for(int ch = 0; ch < 16; ++ch) {
         this.PowerState[ch] = (short)RedPowerLib.updateBlockCurrentStrength(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord, 1073741823, 2 << ch);
      }

      CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
public int getPoweringMask(int ch) {
      return ch >= 1 && ch <= 16?(this.PowerState[ch - 1] == 0?0:this.getConnectableMask()):0;
   }

   @Override
public void readFromNBT(NBTTagCompound nbttagcompound) {
      super.readFromNBT(nbttagcompound);
      byte[] pwr = nbttagcompound.getByteArray("pwrs");
      if(pwr != null) {
         for(int i = 0; i < 16; ++i) {
            this.PowerState[i] = (short)(pwr[i] & 255);
         }

      }
   }

   @Override
public void writeToNBT(NBTTagCompound nbttagcompound) {
      super.writeToNBT(nbttagcompound);
      byte[] pwr = new byte[16];

      for(int i = 0; i < 16; ++i) {
         pwr[i] = (byte)this.PowerState[i];
      }

      nbttagcompound.setByteArray("pwrs", pwr);
   }
}
