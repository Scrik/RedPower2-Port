package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.IConnectable;

public interface IBluePowerConnectable extends IConnectable {
	BluePowerConductor getBlueConductor(int side);
}
