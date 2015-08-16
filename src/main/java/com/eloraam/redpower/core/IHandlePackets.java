package com.eloraam.redpower.core;

import io.netty.buffer.ByteBuf;

public interface IHandlePackets {
	void handlePacketData(ByteBuf data);
}
