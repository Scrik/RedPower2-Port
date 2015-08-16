package com.eloraam.redpower.core;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

	public SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("Redpower2");
	
		public void init() {
			netHandler.registerMessage(PacketTileEntityUpdate.class, PacketTileEntityUpdate.TileEntityMessage.class, 0, Side.CLIENT);
			netHandler.registerMessage(PacketTileEntityUpdate.class, PacketTileEntityUpdate.TileEntityMessage.class, 0, Side.SERVER);
			netHandler.registerMessage(PacketGuiEvent.class, PacketGuiEvent.GuiMessageEvent.class, 1, Side.CLIENT);
			netHandler.registerMessage(PacketGuiEvent.class, PacketGuiEvent.GuiMessageEvent.class, 1, Side.SERVER);
		}
		
		@SuppressWarnings("rawtypes")
		public static void encode(Object[] dataValues, ByteBuf output) {
			try {
			for(Object data : dataValues) {
				if (data instanceof Integer) {
					output.writeInt((Integer) data);
				} else if (data instanceof Boolean) {
					output.writeBoolean((Boolean) data);
				} else if (data instanceof Double) {
					output.writeDouble((Double) data);
				} else if (data instanceof Float) {
					output.writeFloat((Float) data);
				} else if (data instanceof String) {
					writeString(output, (String) data);
				} else if (data instanceof Byte) {
					output.writeByte((Byte) data);
				} else if (data instanceof ItemStack) {
					writeItemStack(output, (ItemStack) data);
				} else if (data instanceof NBTTagCompound) {
					writeNBTCompound(output, (NBTTagCompound) data);
				} else if (data instanceof int[]) {
					for (int i : (int[]) data) {
						output.writeInt(i);
					}
				} else if (data instanceof byte[]) {
					for (byte b : (byte[]) data) {
						output.writeByte(b);
					}
				} else if (data instanceof ArrayList) {
					encode(((ArrayList) data).toArray(), output);
				}
			}
		} catch(Exception e) {
			System.out.println("Error while encoding packet data.");
			e.printStackTrace();
		}
	}
	
	public static void writeString(ByteBuf output, String s) {
		output.writeInt(s.getBytes().length);
		output.writeBytes(s.getBytes());
	}
	
	public static String readString(ByteBuf input) {
		return new String(input.readBytes(input.readInt()).array());
	}
	
	public static void writeItemStack(ByteBuf output, ItemStack stack) {
		output.writeInt(stack != null ? Item.getIdFromItem(stack.getItem()) : -1);
		if(stack != null) {
			output.writeInt(stack.stackSize);
			output.writeInt(stack.getItemDamage());
			
			if(stack.getTagCompound() != null && stack.getItem().getShareTag()) {
				output.writeBoolean(true);
				writeNBTCompound(output, stack.getTagCompound());
			} else {
				output.writeBoolean(false);
			}
		}
	}
	
	public static ItemStack readItemStack(ByteBuf input) {
		int id = input.readInt();
		if(id >= 0) {
			ItemStack stack = new ItemStack(Item.getItemById(id), input.readInt(), input.readInt());
			if(input.readBoolean()) {
				stack.setTagCompound(readNBTCompound(input));
			}
			return stack;
		}
		return null;
	}
	
	public static void writeNBTCompound(ByteBuf output, NBTTagCompound nbtTags) {
		try {
			byte[] buffer = CompressedStreamTools.compress(nbtTags);
			
			output.writeInt(buffer.length);
			output.writeBytes(buffer);
		} catch(Exception e) {}
	}
	
	public static NBTTagCompound readNBTCompound(ByteBuf input) {
		try {
			byte[] buffer = new byte[input.readInt()];
			input.readBytes(buffer);
			
			return CompressedStreamTools.func_152457_a(buffer, new NBTSizeTracker(2097152L));
		} catch(Exception e) {
			return null;
		}
	}
	
	public static EntityPlayer getPlayer(MessageContext context) {
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return context.getServerHandler().playerEntity;
		}
		else {
			return Minecraft.getMinecraft().thePlayer;
		}
	}

	public void sendTo(IMessage message, EntityPlayerMP player) {
		netHandler.sendTo(message, player);
	}

	public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		netHandler.sendToAllAround(message, point);
	}
	
	@SuppressWarnings("unchecked")
	public void sendToReceivers(IMessage message, Range4D range) {
		MinecraftServer server = MinecraftServer.getServer();

		if(server != null) {
			Iterator<EntityPlayerMP> iter = server.getConfigurationManager().playerEntityList.iterator();
			while(iter.hasNext()) {
				EntityPlayerMP player = iter.next();
				if(player.dimension == range.dimensionId && Range4D.getChunkRange(player).intersects(range)) {
					this.sendTo(message, player);
				}
			}
		}
	}
		

	public void sendToDimension(IMessage message, int dimensionId) {
		netHandler.sendToDimension(message, dimensionId);
	}


	public void sendToServer(IMessage message) {
		netHandler.sendToServer(message);
	}	
}
