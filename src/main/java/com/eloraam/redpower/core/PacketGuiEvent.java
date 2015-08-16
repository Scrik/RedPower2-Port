package com.eloraam.redpower.core;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiEvent implements IMessageHandler<PacketGuiEvent.GuiMessageEvent, IMessage> {
	
	@Override
	public IMessage onMessage(PacketGuiEvent.GuiMessageEvent message, MessageContext context)  {
		if(context.netHandler instanceof NetHandlerPlayServer) {
			EntityPlayerMP player = ((NetHandlerPlayServer)context.netHandler).playerEntity;
			if(player.openContainer != null && player.openContainer.windowId == message.windowId) {
				if(player.openContainer instanceof IHandleGuiEvent) {
		               IHandleGuiEvent ihge = (IHandleGuiEvent)player.openContainer;
		               ihge.handleGuiEvent(message);
		        }
			}
		}
		message.storedBuffer.release();
		return null;
	}
	
	public static class GuiMessageEvent implements IMessage {
		
		public int eventId = -1;
		public int windowId = -1;
		@SuppressWarnings("rawtypes")
		public ArrayList parameters = new ArrayList();
		public ByteBuf storedBuffer = null;
		
		public GuiMessageEvent() {
		}
		
		@SuppressWarnings("rawtypes")
		public GuiMessageEvent(int eventId, int windowId, ArrayList params) {
			this.eventId=eventId;
			this.windowId=windowId;
			this.parameters=params;
		}

		@Override
		public void fromBytes(ByteBuf dataStream) {
			this.eventId=dataStream.readInt();
			this.windowId=dataStream.readInt();
			this.storedBuffer=dataStream.copy();
		}

		@Override
		public void toBytes(ByteBuf dataStream) {
			dataStream.writeInt(this.eventId);
			dataStream.writeInt(this.windowId);
			PacketHandler.encode(new Object[] {parameters}, dataStream);
		}
	}
}
