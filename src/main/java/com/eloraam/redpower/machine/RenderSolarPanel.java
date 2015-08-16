package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.machine.TileMachinePanel;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderSolarPanel extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderSolarPanel(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileMachinePanel tm = (TileMachinePanel) CoreLib.getTileEntity(iba, i, j, k, TileMachinePanel.class);
		if (tm != null) {
			this.context.setDefaults();
			this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			this.context.setPos(i, j, k);
			this.context.readGlobalLights(iba, i, j, k);
			IIcon topIcon = getIcon(ForgeDirection.UP.ordinal(), md);
			IIcon bottomIcon = getIcon(ForgeDirection.UP.ordinal(), md);
			IIcon sideIcon = getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
			this.context.setIcon(bottomIcon, topIcon, sideIcon, sideIcon, sideIcon, sideIcon);
			this.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
			this.context.setupBox();
			this.context.transform();
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			this.context.renderGlobFaces(62);
			//RenderLib.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.useNormal = true;
		//RenderLib.bindTexture("/eloraam/machine/machine1.png");
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		IIcon topIcon = getIcon(ForgeDirection.UP.ordinal(), md);
		IIcon bottomIcon = getIcon(ForgeDirection.UP.ordinal(), md);
		IIcon sideIcon = getIcon(ForgeDirection.UNKNOWN.ordinal(), md);
		this.context.setIcon(bottomIcon, topIcon, sideIcon, sideIcon, sideIcon, sideIcon);
		this.context.renderBox(62, 0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
		tessellator.draw();
		//RenderLib.unbindTexture();
		this.context.useNormal = false;
	}
}
