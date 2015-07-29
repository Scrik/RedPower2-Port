package com.eloraam.redpower.core;


public class PowerLib {

   public static int cutBits(int bits, int cut) {
      int i = 1;

      while(i <= cut) {
         if((cut & i) == 0) {
            i <<= 1;
         } else {
            bits = bits & i - 1 | bits >> 1 & ~(i - 1);
            cut >>= 1;
         }
      }

      return bits;
   }
}
