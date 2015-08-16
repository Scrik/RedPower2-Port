package com.eloraam.redpower.core;

import com.eloraam.redpower.core.IConnectable;

public interface IRedPowerConnectable extends IConnectable {
	
	int getPoweringMask(int ch);
}
