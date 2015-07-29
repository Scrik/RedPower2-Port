package com.eloraam.redpower.core;

import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IWiring;

public interface IRedPowerWiring extends IRedPowerConnectable, IWiring {
	
	int scanPoweringStrength(int var1, int var2);
	
	int getCurrentStrength(int var1, int var2);
	
	void updateCurrentStrength();
}
