package com.eloraam.redpower.logic;

import java.util.ArrayList;

import com.eloraam.redpower.core.DimCoord;
import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.logic.ContainerTimer;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiTimer extends GuiContainer {
	
	private TileLogicPointer tileLogic;
	private GuiButton[] buttons = new GuiButton[6];
	private ResourceLocation guiRes = new ResourceLocation("rplogic", "textures/gui/timersgui.png");
	
	public GuiTimer(InventoryPlayer pli, TileLogicPointer te) {
		super(new ContainerTimer(pli, te));
		super.xSize = 228;
		super.ySize = 82;
		this.tileLogic = te;
	}
	
	public GuiTimer(Container cn) {
		super(cn);
		super.xSize = 228;
		super.ySize = 82;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		int bw = super.xSize - 20;
		int l = (super.width - super.xSize) / 2;
		int m = (super.height - super.ySize) / 2;
		super.buttonList.add(this.buttons[0] = new GuiButton(1, l + 10,
				m + 50, bw / 6, 20, "-10s"));
		super.buttonList.add(this.buttons[1] = new GuiButton(2, l + 10 + bw
				/ 6, m + 50, bw / 6, 20, "-1s"));
		super.buttonList.add(this.buttons[2] = new GuiButton(3, l + 10 + bw
				* 2 / 6, m + 50, bw / 6, 20, "-50ms"));
		super.buttonList.add(this.buttons[3] = new GuiButton(4, l + 10 + bw
				* 3 / 6, m + 50, bw / 6, 20, "+50ms"));
		super.buttonList.add(this.buttons[4] = new GuiButton(5, l + 10 + bw
				* 4 / 6, m + 50, bw / 6, 20, "+1s"));
		super.buttonList.add(this.buttons[5] = new GuiButton(6, l + 10 + bw
				* 5 / 6, m + 50, bw / 6, 20, "+10s"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		FontRenderer fontrenderer = super.mc.fontRenderer;
		//int k = super.mc.renderEngine.getTexture("/eloraam/logic/timersgui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(guiRes);
		int l = (super.width - super.xSize) / 2;
		int m = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(l, m, 0, 0, super.xSize, super.ySize);
		String str = String.format("Timer Interval: %.3fs", new Object[] { Double.valueOf(this.tileLogic.getInterval() / 20.0D) });
		this.drawCenteredString(fontrenderer, str, super.width / 2, m + 10, -1);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void changeInterval(int cc) {
		long iv = this.tileLogic.getInterval() + cc;
		if (iv < 4L) {
			iv = 4L;
		}
		
		this.tileLogic.setInterval(iv);
		if (!super.mc.theWorld.isRemote) {
			this.tileLogic.updateBlock();
		} else {
			ArrayList data = new ArrayList();
			data.add(iv);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.enabled) {
			switch (guibutton.id) {
				case 1:
					this.changeInterval(-200);
					break;
				case 2:
					this.changeInterval(-20);
					break;
				case 3:
					this.changeInterval(-1);
					break;
				case 4:
					this.changeInterval(1);
					break;
				case 5:
					this.changeInterval(20);
					break;
				case 6:
					this.changeInterval(200);
			}
		}
	}
}
