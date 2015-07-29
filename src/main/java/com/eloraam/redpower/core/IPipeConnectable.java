package com.eloraam.redpower.core;

import com.eloraam.redpower.core.FluidBuffer;

public interface IPipeConnectable {

   int getPipeConnectableSides();

   int getPipeFlangeSides();

   int getPipePressure(int var1);

   FluidBuffer getPipeBuffer(int var1);
}
