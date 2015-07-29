package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.BaseProxy;
import com.eloraam.redpower.base.RenderAdvBench;
import com.eloraam.redpower.base.RenderAlloyFurnace;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.RenderLib;

public class BaseProxyClient extends BaseProxy {
	
	@Override
	public void registerRenderers() {
		RenderLib.setRenderer(RedPowerBase.blockAppliance, 0, RenderAlloyFurnace.class);
		RenderLib.setRenderer(RedPowerBase.blockAppliance, 3, RenderAdvBench.class);
		RenderLib.setRenderer(RedPowerBase.blockMicro, RenderCovers.class);
		//MinecraftForgeClient.preloadTexture("/eloraam/base/base1.png");
	}
}
