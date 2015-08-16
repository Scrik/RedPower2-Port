package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileMotor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrame extends BlockCoverable {
	
	public IIcon crossedFrameIcon;
	public IIcon coveredFrameIcon;
	public IIcon paneledFrameIcon;
	
	public BlockFrame() {
		super(CoreLib.materialRedpower);
		this.setHardness(0.5F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
		this.setBlockTextureName("rpmachine:blockFrameCrossed");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
		this.crossedFrameIcon = reg.registerIcon("rpmachine:blockFrameCrossed");
		this.coveredFrameIcon = reg.registerIcon("rpmachine:blockFrameCovered");
		this.paneledFrameIcon = reg.registerIcon("rpmachine:blockFramePaneled");
		
		this.blockIcon = reg.registerIcon(this.getTextureName());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileExtended tile = (TileExtended)CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
		if(tile != null) {
			//switch(meta) {
			//	case 0: {
					//if(tile instanceof TileFrame) {
						switch(ForgeDirection.getOrientation(side)) {
							case NORTH:
								return this.coveredFrameIcon;
							case SOUTH:
								return this.paneledFrameIcon;
							default:
								return this.crossedFrameIcon;
						}
					//}
			//	}
			//}
		}
		return this.blockIcon;
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
		switch(meta) {
			case 0: {
				switch(ForgeDirection.getOrientation(side)) {
					default:
						return this.crossedFrameIcon;
				}
			}
		}
        return this.blockIcon;
    }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB box, List list, Entity ent) {
		TileFrameMoving tl = (TileFrameMoving) CoreLib.getTileEntity(world, i, j, k, TileFrameMoving.class);
		if (tl == null) {
			super.addCollisionBoxesToList(world, i, j, k, box, list, ent);
		} else {
			this.computeCollidingBoxes(world, i, j, k, box, list, tl);
			TileMotor tm = (TileMotor) CoreLib.getTileEntity(world, tl.motorX, tl.motorY, tl.motorZ, TileMotor.class);
			if (tm != null) {
				WorldCoord wc = new WorldCoord(i, j, k);
				wc.step(tm.MoveDir ^ 1);
				tl = (TileFrameMoving) CoreLib.getTileEntity(world, wc, TileFrameMoving.class);
				if (tl != null) {
					this.computeCollidingBoxes(world, wc.x, wc.y, wc.z, box, list, tl);
				}
			}
		}
	}
}
