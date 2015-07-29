package com.eloraam.redpower.core;

import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IRedbusConnectable;

public interface IRedbusConnectable extends IConnectable {
	
	int rbGetAddr();
	
	void rbSetAddr(int var1);
	
	int rbRead(int var1);
	
	void rbWrite(int var1, int var2);
	
	public static class Dummy implements IRedbusConnectable {
		
		private int address;
		
		@Override
		public int getConnectableMask() {
			return 0;
		}
		
		@Override
		public int getConnectClass(int side) {
			return 0;
		}
		
		@Override
		public int getCornerPowerMode() {
			return 0;
		}
		
		@Override
		public int rbGetAddr() {
			return this.address;
		}
		
		@Override
		public void rbSetAddr(int addr) {
			this.address = addr;
		}
		
		@Override
		public int rbRead(int reg) {
			return 0;
		}
		
		@Override
		public void rbWrite(int reg, int dat) {
		}
	}
}
