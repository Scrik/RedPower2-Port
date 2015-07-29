package com.eloraam.redpower.core;

import com.eloraam.redpower.core.IConnectable;

public interface IWiring extends IConnectable {
	
	int getConnectionMask();
	
	int getExtConnectionMask();
}
