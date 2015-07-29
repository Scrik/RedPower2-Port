package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.wiring.RenderWiring;
import com.eloraam.redpower.wiring.TileWiring;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderRibbon extends RenderWiring {
	
	public RenderRibbon(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k,
			Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
		TileCovered tc = (TileCovered) iba.getTileEntity(i, j, k);
		if (tc != null) {
			super.context.setPos(i, j, k);
			if (tc.CoverSides > 0) {
				super.context.setTint(1.0F, 1.0F, 1.0F);
				super.context.readGlobalLights(iba, i, j, k);
				this.renderCovers(tc.CoverSides, tc.Covers);
			}
			
			TileWiring tw = (TileWiring) tc;
			int cons = tw.getConnectionMask();
			int indcon = tw.getExtConnectionMask();
			int indconex = tw.EConEMask;
			cons |= indcon;
			super.context.setTint(1.0F, 1.0F, 1.0F);
			IIcon topIcon = RedPowerBase.blockMicro.getIcon(1, md);
			IIcon centIcon = RedPowerBase.blockMicro.getIcon(2, md);
			this.setSideTex(topIcon, centIcon, topIcon);
			this.setWireSize(0.5F, 0.0625F);
			//RenderLib.bindTexture("/eloraam/control/control1.png");
			this.renderWireBlock(tw.ConSides, cons, indcon, indconex);
			//RenderLib.unbindTexture();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		Block bid = Block.getBlockById(md >> 8);
		md &= 255;
		super.context.setDefaults();
		super.context.setTexFlags(55);
		super.context.setPos(-0.5D, -0.20000000298023224D, -0.5D);
		IIcon topIcon = RedPowerBase.blockMicro.getIcon(1, md);
		IIcon centIcon = RedPowerBase.blockMicro.getIcon(2, md);
		this.setSideTex(topIcon, centIcon, topIcon);
		this.setWireSize(0.5F, 0.0625F);
		super.context.useNormal = true;
		//RenderLib.bindTexture("/eloraam/control/control1.png");
		tessellator.startDrawingQuads();
		this.renderSideWires(127, 0, 0);
		tessellator.draw();
		//RenderLib.unbindTexture();
		super.context.useNormal = false;
	}
}
