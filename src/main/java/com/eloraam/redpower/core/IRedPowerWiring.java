package com.eloraam.redpower.core;

import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IWiring;

public interface IRedPowerWiring extends IRedPowerConnectable, IWiring {
	
	int scanPoweringStrength(int cons, int ch);
	
	int getCurrentStrength(int cons, int ch);
	
	void updateCurrentStrength();
}
