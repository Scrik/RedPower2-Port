package com.eloraam.redpower.network;

import io.netty.buffer.ByteBuf;

public interface IHandlePackets {
	void handlePacketData(ByteBuf data);
}
