package com.eloraam.redpower.core;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.world.IBlockAccess;

public interface IFrameSupport {
	
	ArrayList<?> getFramePacket();
	
	void handleFramePacket(ByteBuf buffer);
	
	void onFrameRefresh(IBlockAccess iba);
	
	void onFramePickup(IBlockAccess iba);
	
	void onFrameDrop();
}
