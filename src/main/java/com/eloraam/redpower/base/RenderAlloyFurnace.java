package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.TileAlloyFurnace;
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
import net.minecraftforge.common.util.ForgeDirection;

public class RenderAlloyFurnace extends RenderCustomBlock {
	
	protected RenderContext context = new RenderContext();
	
	public RenderAlloyFurnace(Block bl) {
		super(bl);
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		TileAlloyFurnace tb = (TileAlloyFurnace) CoreLib.getTileEntity(world, i, j, k, TileAlloyFurnace.class);
		if (tb != null) {
			if (tb.Active) {
				float f = i + 0.5F;
				float f1 = j + 0.0F + random.nextFloat() * 6.0F / 16.0F;
				float f2 = k + 0.5F;
				float f3 = 0.52F;
				float f4 = random.nextFloat() * 0.6F - 0.3F;
				switch (tb.Rotation) {
					case 0:
						world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
						world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
						break;
					case 1:
						world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
						break;
					case 2:
						world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
						world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
						break;
					case 3:
						world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
						break;
				}
			}
		}
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int x, int y, int z, int md) {
		TileAlloyFurnace tb = (TileAlloyFurnace) CoreLib.getTileEntity(iba, x, y, z, TileAlloyFurnace.class);
		if (tb != null) {
			renderblocks.renderStandardBlock(block, x, y, z);
			renderblocks.setRenderBoundsFromBlock(block);
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		this.context.setDefaults();
		this.context.setPos(-0.5D, -0.5D, -0.5D);
		this.context.useNormal = true;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		IIcon frontIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.NORTH.ordinal(), 0);
		IIcon vertIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.UP.ordinal(), 0);
		IIcon sideIcon = RedPowerBase.blockAppliance.getIcon(ForgeDirection.UNKNOWN.ordinal(), 0);
		this.context.setIcon(vertIcon, vertIcon, sideIcon, sideIcon, sideIcon, frontIcon);
		this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.draw();
		this.context.useNormal = false;
	}
}
