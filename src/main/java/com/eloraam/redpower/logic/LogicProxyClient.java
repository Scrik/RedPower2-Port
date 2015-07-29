package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.logic.LogicProxy;
import com.eloraam.redpower.logic.RenderLogicAdv;
import com.eloraam.redpower.logic.RenderLogicArray;
import com.eloraam.redpower.logic.RenderLogicPointer;
import com.eloraam.redpower.logic.RenderLogicSimple;
import com.eloraam.redpower.logic.RenderLogicStorage;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.logic.TilePointerRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;

public class LogicProxyClient extends LogicProxy {
	
	@Override
	public void registerRenderers() {
		RenderLib.setHighRenderer(RedPowerLogic.blockLogic, 0, RenderLogicPointer.class);
		RenderLib.setHighRenderer(RedPowerLogic.blockLogic, 1, RenderLogicSimple.class);
		RenderLib.setHighRenderer(RedPowerLogic.blockLogic, 2, RenderLogicArray.class);
		RenderLib.setHighRenderer(RedPowerLogic.blockLogic, 3, RenderLogicStorage.class);
		RenderLib.setHighRenderer(RedPowerLogic.blockLogic, 4, RenderLogicAdv.class);
		ClientRegistry.bindTileEntitySpecialRenderer(TileLogicPointer.class, new TilePointerRenderer());
		//MinecraftForgeClient.preloadTexture("/eloraam/logic/logic1.png");
		//MinecraftForgeClient.preloadTexture("/eloraam/logic/logic2.png");
		//MinecraftForgeClient.preloadTexture("/eloraam/logic/array1.png");
		//MinecraftForgeClient.preloadTexture("/eloraam/logic/sensor1.png");
	}
}
