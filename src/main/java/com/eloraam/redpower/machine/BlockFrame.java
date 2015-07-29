package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileMotor;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockFrame extends BlockCoverable {
	
	public BlockFrame() {
		super(CoreLib.materialRedpower);
		this.setHardness(0.5F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
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
