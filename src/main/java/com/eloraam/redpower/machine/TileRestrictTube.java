package com.eloraam.redpower.machine;

import com.eloraam.redpower.machine.TileTube;

public class TileRestrictTube extends TileTube {

   @Override
public int tubeWeight(int side, int state) {
      return 1000000;
   }

   @Override
public int getExtendedID() {
      return 10;
   }
}
