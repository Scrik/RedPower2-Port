package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.CoreProxyClient;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderHighlight;
import com.eloraam.redpower.core.RenderLib;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

public class CoreProxyClient extends CoreProxy {
	
	@Override
	public IIcon getItemIcon(Item it, int dmg) {
		return it.getIconFromDamage(dmg);
	}
	
	@Override
	public void setupRenderers() {
		RedPowerCore.customBlockModel = RenderingRegistry.getNextAvailableRenderId();
		RedPowerCore.nullBlockModel = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(RedPowerCore.customBlockModel, new CoreProxyClient.RenderHandler());
		MinecraftForge.EVENT_BUS.register(new RenderHighlight());
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	protected void pxySendPacketToServer(Packet pkt) {
		NetHandlerPlayClient nch = Minecraft.getMinecraft().thePlayer.sendQueue;
		nch.addToSendQueue(pkt);
	}
	
	// public void processPacket211(S35PacketUpdateTileEntity pkt,
	// NetHandlerPlayClient nh) {
	// if (nh instanceof NetHandlerPlayClient) {
	// NetHandlerPlayClient nch = (NetHandlerPlayClient) nh;
	// World world =
	// Minecraft.getMinecraft().theWorld;/*nch.getPlayer().worldObj;*/ //TODO:
	// we are in client, right?
	// if (world.blockExists(pkt.xCoord, pkt.yCoord, pkt.zCoord)) {
	// TileEntity tile = world.getTileEntity(pkt.xCoord, pkt.yCoord,
	// pkt.zCoord);
	// if (tile instanceof IHandlePackets) {
	// ((IHandlePackets) tile).handlePacket(pkt);
	// return;
	// }
	// }
	// } else {
	// super.processPacket211(pkt, nh);
	// }
	// }
	
	// public void processPacket212(PacketGuiEvent pkt, NetHandlerPlayClient nh)
	// {
	// if (nh instanceof NetHandlerPlayClient) {
	// //NetHandlerPlayClient nch = (NetHandlerPlayClient) nh;
	// EntityPlayer pl = Minecraft.getMinecraft().thePlayer;/*nch.getPlayer();*/
	// //TODO: we are in client, right?
	// if (pl.openContainer != null && pl.openContainer.windowId ==
	// pkt.windowId) {
	// if (pl.openContainer instanceof IHandleGuiEvent) {
	// IHandleGuiEvent ihge = (IHandleGuiEvent) pl.openContainer;
	// ihge.handleGuiEvent(pkt);
	// }
	// }
	// } else {
	// super.processPacket212(pkt, nh);
	// }
	// }
	
	public static class RenderHandler implements ISimpleBlockRenderingHandler {
		
		@Override
		public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
			if (modelID == RedPowerCore.customBlockModel) {
				RenderCustomBlock rcb = RenderLib.getInvRenderer(block, metadata);
				if (rcb == null) {
					System.out.printf("Bad Render at %d:%d\n",
						new Object[] { Integer.valueOf(Integer.valueOf(Block.getIdFromBlock(block))), 
							Integer.valueOf(metadata) });
				} else {
					rcb.renderInvBlock(renderer, metadata);
				}
			}
		}

		@Override
		public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
			if (renderer.overrideBlockTexture != null) {
				return true;
			} else if (modelId != RedPowerCore.customBlockModel) {
				return false;
			} else {
				int md = world.getBlockMetadata(x, y, z);
				RenderCustomBlock rcb = RenderLib.getRenderer(block, md);
				if (rcb == null) {
					System.out.printf("Bad Render at %d:%d\n", new Object[] { Integer.valueOf(Block.getIdFromBlock(block)), Integer.valueOf(md) });
					return true;
				} else {
					rcb.renderWorldBlock(renderer, world, x, y, z, md);
					//Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					return true;
				}
			}
		}
		
		@Override
		public boolean shouldRender3DInInventory(int modelId) {
			return true;
		}
		
		@Override
		public int getRenderId() {
			return RedPowerCore.customBlockModel;
		}
	}
}
