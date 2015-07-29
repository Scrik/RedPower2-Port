package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileDisplay;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderDisplay extends RenderCustomBlock {
	
	RenderContext context = new RenderContext();
	
	public RenderDisplay(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileDisplay disp = (TileDisplay) CoreLib.getTileEntity(iba, i, j, k, TileDisplay.class);
		if (disp != null) {
			this.context.setDefaults();
			this.context.readGlobalLights(iba, i, j, k);
			//this.context.bindTexture("/eloraam/control/control1.png");
			IIcon frontIcon = RedPowerControl.blockPeripheral.getIcon(0, md);
			IIcon sideIcon = RedPowerControl.blockPeripheral.getIcon(1, md);
			IIcon backIcon = RedPowerControl.blockPeripheral.getIcon(2, md);
			IIcon topIcon = RedPowerControl.blockPeripheral.getIcon(3, md);
			IIcon bottomIcon = RedPowerControl.blockPeripheral.getIcon(4, md);
			this.context.setIcon(bottomIcon, backIcon, sideIcon, sideIcon, frontIcon, topIcon);
			this.context.rotateTextures(disp.Rotation);
			this.context.setPos(i, j, k);
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			this.context.renderGlobFaces(63);
			//this.context.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.useNormal = true;
		this.context.setOrientation(0, 3);
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		//this.context.bindTexture("/eloraam/control/control1.png");
		IIcon frontIcon = RedPowerControl.blockPeripheral.getIcon(0, md);
		IIcon sideIcon = RedPowerControl.blockPeripheral.getIcon(1, md);
		IIcon backIcon = RedPowerControl.blockPeripheral.getIcon(2, md);
		IIcon topIcon = RedPowerControl.blockPeripheral.getIcon(3, md);
		IIcon bottomIcon = RedPowerControl.blockPeripheral.getIcon(4, md);
		this.context.setIcon(bottomIcon, backIcon, sideIcon, sideIcon, frontIcon, topIcon);
		this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		tessellator.startDrawingQuads();
		this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		this.context.useNormal = false;
		//this.context.unbindTexture();
	}
}
