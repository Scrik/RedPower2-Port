package com.eloraam.redpower.wiring;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.wiring.RenderRedwire;
import com.eloraam.redpower.wiring.WiringProxy;

public class WiringProxyClient extends WiringProxy {
	
	@Override
	public void registerRenderers() {
		RenderLib.setDefaultRenderer(RedPowerBase.blockMicro, 8, RenderRedwire.class);
		//MinecraftForgeClient.preloadTexture("/eloraam/wiring/redpower1.png");
	}
}
