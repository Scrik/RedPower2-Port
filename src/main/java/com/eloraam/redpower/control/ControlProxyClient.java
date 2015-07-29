package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.ControlProxy;
import com.eloraam.redpower.control.RenderBackplane;
import com.eloraam.redpower.control.RenderCPU;
import com.eloraam.redpower.control.RenderDiskDrive;
import com.eloraam.redpower.control.RenderDisplay;
import com.eloraam.redpower.control.RenderRibbon;
import com.eloraam.redpower.core.RenderLib;

public class ControlProxyClient extends ControlProxy {
	
	@Override
	public void registerRenderers() {
		RenderLib.setRenderer(RedPowerControl.blockBackplane, 0, RenderBackplane.class);
		RenderLib.setRenderer(RedPowerControl.blockBackplane, 1, RenderBackplane.class);
		RenderLib.setRenderer(RedPowerControl.blockPeripheral, 0, RenderDisplay.class);
		RenderLib.setRenderer(RedPowerControl.blockPeripheral, 1, RenderCPU.class);
		RenderLib.setRenderer(RedPowerControl.blockPeripheral, 2, RenderDiskDrive.class);
		RenderLib.setRenderer(RedPowerControl.blockFlatPeripheral, 0, RenderIOExpander.class);
		//ClientRegistry.bindTileEntitySpecialRenderer(TileIOExpander.class, new RenderIOExpander());
		// MinecraftForgeClient.preloadTexture("/eloraam/control/control1.png");
		RenderLib.setHighRenderer(RedPowerBase.blockMicro, 12, RenderRibbon.class);
	}
}
