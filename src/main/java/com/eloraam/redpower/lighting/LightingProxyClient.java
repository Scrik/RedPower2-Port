package com.eloraam.redpower.lighting;

import net.minecraftforge.client.MinecraftForgeClient;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.lighting.LightingProxy;
import com.eloraam.redpower.lighting.RenderLamp;
import com.eloraam.redpower.lighting.RenderShapedLamp;

public class LightingProxyClient extends LightingProxy {
	
	@Override
	public void registerRenderers() {
		
		/*MinecraftForgeClient.registerRenderContextHandler( //TODO: Figure it out
				"/eloraam/lighting/lighting1.png", 1,
				new IRenderContextHandler() {
					@Override
					public void beforeRenderContext() {
						GL11.glBlendFunc(770, 1);
						GL11.glDisable(3553);
						GL11.glDepthMask(false);
					}
					
					@Override
					public void afterRenderContext() {
						GL11.glDepthMask(true);
						GL11.glEnable(3553);
						GL11.glBlendFunc(770, 771);
					}
				});*/
		RenderLib.setRenderer(RedPowerLighting.blockLampOff, RenderLamp.class);
		RenderLib.setRenderer(RedPowerLighting.blockLampOn, RenderLamp.class);
		RenderLib.setRenderer(RedPowerLighting.blockInvLampOff, RenderLamp.class);
		RenderLib.setRenderer(RedPowerLighting.blockInvLampOn, RenderLamp.class);
		RenderLib.setDefaultRenderer(RedPowerLighting.blockShapedLamp, 10, RenderShapedLamp.class);
		//MinecraftForgeClient.preloadTexture("/eloraam/lighting/lighting1.png");
	}
}
