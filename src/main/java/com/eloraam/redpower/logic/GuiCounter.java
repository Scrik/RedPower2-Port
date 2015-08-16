package com.eloraam.redpower.logic;

import java.util.ArrayList;

import com.eloraam.redpower.core.CoreProxy;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.eloraam.redpower.logic.ContainerCounter;
import com.eloraam.redpower.logic.TileLogicStorage;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCounter extends GuiContainer {
	
	private TileLogicStorage tileLogic;
	private GuiButton[] buttons = new GuiButton[18];
	private ResourceLocation guiRes = new ResourceLocation("rplogic", "textures/gui/countergui.png");
	
	public GuiCounter(InventoryPlayer pli, TileLogicStorage te) {
		super(new ContainerCounter(pli, te));
		super.xSize = 228;
		super.ySize = 117;
		this.tileLogic = te;
	}
	
	public GuiCounter(Container cn) {
		super(cn);
		super.xSize = 228;
		super.ySize = 117;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		int bw = super.xSize - 20;
		int l = (super.width - super.xSize) / 2;
		int m = (super.height - super.ySize) / 2;
		super.buttonList.add(this.buttons[0] = new GuiButton(1, l + 10,
				m + 20, bw / 6, 20, "-25"));
		super.buttonList.add(this.buttons[1] = new GuiButton(2, l + 10 + bw
				/ 6, m + 20, bw / 6, 20, "-5"));
		super.buttonList.add(this.buttons[2] = new GuiButton(3, l + 10 + bw
				* 2 / 6, m + 20, bw / 6, 20, "-1"));
		super.buttonList.add(this.buttons[3] = new GuiButton(4, l + 10 + bw
				* 3 / 6, m + 20, bw / 6, 20, "+1"));
		super.buttonList.add(this.buttons[4] = new GuiButton(5, l + 10 + bw
				* 4 / 6, m + 20, bw / 6, 20, "+5"));
		super.buttonList.add(this.buttons[5] = new GuiButton(6, l + 10 + bw
				* 5 / 6, m + 20, bw / 6, 20, "+25"));
		super.buttonList.add(this.buttons[6] = new GuiButton(7, l + 10,
				m + 55, bw / 6, 20, "-25"));
		super.buttonList.add(this.buttons[7] = new GuiButton(8, l + 10 + bw
				/ 6, m + 55, bw / 6, 20, "-5"));
		super.buttonList.add(this.buttons[8] = new GuiButton(9, l + 10 + bw
				* 2 / 6, m + 55, bw / 6, 20, "-1"));
		super.buttonList.add(this.buttons[9] = new GuiButton(10, l + 10 + bw
				* 3 / 6, m + 55, bw / 6, 20, "+1"));
		super.buttonList.add(this.buttons[10] = new GuiButton(11, l + 10 + bw
				* 4 / 6, m + 55, bw / 6, 20, "+5"));
		super.buttonList.add(this.buttons[11] = new GuiButton(12, l + 10 + bw
				* 5 / 6, m + 55, bw / 6, 20, "+25"));
		super.buttonList.add(this.buttons[12] = new GuiButton(13, l + 10,
				m + 90, bw / 6, 20, "-25"));
		super.buttonList.add(this.buttons[13] = new GuiButton(14, l + 10 + bw
				/ 6, m + 90, bw / 6, 20, "-5"));
		super.buttonList.add(this.buttons[14] = new GuiButton(15, l + 10 + bw
				* 2 / 6, m + 90, bw / 6, 20, "-1"));
		super.buttonList.add(this.buttons[15] = new GuiButton(16, l + 10 + bw
				* 3 / 6, m + 90, bw / 6, 20, "+1"));
		super.buttonList.add(this.buttons[16] = new GuiButton(17, l + 10 + bw
				* 4 / 6, m + 90, bw / 6, 20, "+5"));
		super.buttonList.add(this.buttons[17] = new GuiButton(18, l + 10 + bw
				* 5 / 6, m + 90, bw / 6, 20, "+25"));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		FontRenderer fontrenderer = super.mc.fontRenderer;
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		//int k = super.mc.renderEngine.getTexture("/eloraam/logic/countergui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.mc.renderEngine.bindTexture(guiRes);
		int l = (super.width - super.xSize) / 2;
		int m = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(l, m, 0, 0, super.xSize, super.ySize);
		String str = String.format("Maximum Count: %d",
				new Object[] { Integer.valueOf(lsc.CountMax) });
		this.drawCenteredString(fontrenderer, str, super.width / 2, m + 10, -1);
		str = String.format("Increment: %d",
				new Object[] { Integer.valueOf(lsc.Inc) });
		this.drawCenteredString(fontrenderer, str, super.width / 2, m + 45, -1);
		str = String.format("Decrement: %d",
				new Object[] { Integer.valueOf(lsc.Dec) });
		this.drawCenteredString(fontrenderer, str, super.width / 2, m + 80, -1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void changeCountMax(int cc) {
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic
				.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		lsc.CountMax += cc;
		if (lsc.CountMax < 1) {
			lsc.CountMax = 1;
		}
		
		if (!super.mc.theWorld.isRemote) {
			this.tileLogic.updateBlock();
		} else {
			ArrayList data = new ArrayList();
			data.add(lsc.CountMax);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void changeInc(int cc) {
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		lsc.Inc += cc;
		if (lsc.Inc < 1) {
			lsc.Inc = 1;
		}
		
		if (!super.mc.theWorld.isRemote) {
			this.tileLogic.updateBlock();
		} else {
			ArrayList data = new ArrayList();
			data.add(lsc.Inc);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, data));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void changeDec(int cc) {
		TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) this.tileLogic.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
		lsc.Dec += cc;
		if (lsc.Dec < 1) {
			lsc.Dec = 1;
		}
		if (!super.mc.theWorld.isRemote) {
			this.tileLogic.updateBlock();
		} else {
			ArrayList data = new ArrayList();
			data.add(lsc.Dec);
			CoreProxy.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(3, super.inventorySlots.windowId, data));
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.enabled) {
			switch (guibutton.id) {
				case 1:
					this.changeCountMax(-25);
					break;
				case 2:
					this.changeCountMax(-5);
					break;
				case 3:
					this.changeCountMax(-1);
					break;
				case 4:
					this.changeCountMax(1);
					break;
				case 5:
					this.changeCountMax(5);
					break;
				case 6:
					this.changeCountMax(25);
					break;
				case 7:
					this.changeInc(-25);
					break;
				case 8:
					this.changeInc(-5);
					break;
				case 9:
					this.changeInc(-1);
					break;
				case 10:
					this.changeInc(1);
					break;
				case 11:
					this.changeInc(5);
					break;
				case 12:
					this.changeInc(25);
					break;
				case 13:
					this.changeDec(-25);
					break;
				case 14:
					this.changeDec(-5);
					break;
				case 15:
					this.changeDec(-1);
					break;
				case 16:
					this.changeDec(1);
					break;
				case 17:
					this.changeDec(5);
					break;
				case 18:
					this.changeDec(25);
			}
			
		}
	}
}
