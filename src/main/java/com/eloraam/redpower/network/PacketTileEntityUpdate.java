package com.eloraam.redpower.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import com.eloraam.redpower.core.DimCoord;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTileEntityUpdate implements IMessageHandler<PacketTileEntityUpdate.TileEntityMessage, IMessage> {
	@Override
	public IMessage onMessage(PacketTileEntityUpdate.TileEntityMessage message, MessageContext context)  {
		TileEntity tileEntity = message.coord4D.getTileEntity(PacketHandler.getPlayer(context).worldObj);
		
		if(tileEntity instanceof IHandlePackets) {
			try {
				((IHandlePackets)tileEntity).handlePacketData(message.storedBuffer);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		message.storedBuffer.release();
		return null;
	}
	
	public static class TileEntityMessage implements IMessage {
		public DimCoord coord4D;
	
		public ArrayList<?> parameters;
		
		public ByteBuf storedBuffer = null;
		
		public TileEntityMessage() {}
	
		public TileEntityMessage(DimCoord coord, ArrayList<?> params) {
			coord4D = coord;
			parameters = params;
		}
	
		@Override
		public void toBytes(ByteBuf dataStream) {
			dataStream.writeInt(coord4D.xCoord);
			dataStream.writeInt(coord4D.yCoord);
			dataStream.writeInt(coord4D.zCoord);
			dataStream.writeInt(coord4D.dimensionId);
			
			/*MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			
			if(server != null) {
				World world = server.worldServerForDimension(coord4D.dimensionId);
				System.out.println("Sending TileEntityUpdatePacket from coords " + coord4D + " (" + coord4D.getTileEntity(world) + ")");
			}*/
			PacketHandler.encode(new Object[] {parameters}, dataStream);
		}
	
		@Override
		public void fromBytes(ByteBuf dataStream) {
			coord4D = new DimCoord(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
			storedBuffer = dataStream.copy();
		}
	}
}
