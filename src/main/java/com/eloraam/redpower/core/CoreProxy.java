package com.eloraam.redpower.core;

import java.util.List;

import com.eloraam.redpower.RedPowerCore;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CoreProxy {
	
	@SidedProxy(clientSide = "com.eloraam.redpower.core.CoreProxyClient", serverSide = "com.eloraam.redpower.core.CoreProxy")
	public static CoreProxy instance;
	
	public static void sendPacketToServer(IMessage msg) {
		RedPowerCore.packetHandler.sendToServer(msg);
	}
	
	public IIcon getItemIcon(Item it, int dmg) {
		return null;
	}
	
	public static void sendPacketToCrafting(ICrafting icr, IMessage msg) {
		if (icr instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) icr;
			RedPowerCore.packetHandler.sendTo(msg, player);
			//player.playerNetServerHandler.sendPacket(pkt); //TODO: sendPacketToPlayer is you?
		}
	}
	
	public void setupRenderers() {
	}
	
	protected void pxySendPacketToServer(Packet pkt) {
	}
	
	/*public void processPacket31(S35PacketUpdateTileEntity pkt, INetHandler nh) {
		if (nh instanceof NetHandlerPlayServer) {
			NetHandlerPlayServer nsh = (NetHandlerPlayServer) nh;
			EntityPlayerMP player = nsh.playerEntity;
			World world = player.worldObj;
			if (world.blockExists(pkt.func_148856_c(), pkt.func_148855_d(), pkt.func_148854_e())) {
				TileEntity tile = world.getTileEntity(pkt.func_148856_c(), pkt.func_148855_d(), pkt.func_148854_e());
				if (tile instanceof IHandlePackets) {
					((IHandlePackets) tile).handlePacket(pkt);
					return;
				}
			}
		}
	}
	
	public void processPacket32(S32PacketGuiEvent pkt, INetHandler nh) {
		if (nh instanceof NetHandlerPlayServer) {
			NetHandlerPlayServer nsh = (NetHandlerPlayServer) nh;
			EntityPlayerMP pl = nsh.playerEntity;
			if (pl.openContainer != null && pl.openContainer.windowId == pkt.windowId) {
				if (pl.openContainer instanceof IHandleGuiEvent) {
					IHandleGuiEvent ihge = (IHandleGuiEvent) pl.openContainer;
					ihge.handleGuiEvent(pkt);
				}
			}
		}
	}*/
}
