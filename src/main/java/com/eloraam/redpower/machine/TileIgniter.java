package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileMachine;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class TileIgniter extends TileMachine {
	
	@Override
	public int getExtendedID() {
		return 12;
	}
	
	private void fireAction() {
		WorldCoord wc = new WorldCoord(this);
		wc.step(super.Rotation ^ 1);
		if (super.Active) {
			if (super.worldObj.isAirBlock(wc.x, wc.y, wc.z)) {
				super.worldObj.setBlock(wc.x, wc.y, wc.z, Blocks.fire);
			}
		} else {
			Block block = super.worldObj.getBlock(wc.x, wc.y, wc.z);
			if (block == Blocks.fire || block == Blocks.portal) {
				super.worldObj.setBlockToAir(wc.x, wc.y, wc.z);
			}
		}
		
	}
	
	@Override
	public void onBlockNeighborChange(Block block) {
		if (!RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord,
				super.zCoord, 16777215, 63)) {
			if (!super.Powered) {
				return;
			}
			
			super.Powered = false;
			if (super.Delay) {
				return;
			}
			
			super.Active = false;
			super.Delay = true;
			this.fireAction();
		} else {
			if (super.Powered) {
				return;
			}
			
			super.Powered = true;
			if (super.Delay) {
				return;
			}
			
			if (super.Active) {
				return;
			}
			
			super.Active = true;
			super.Delay = true;
			this.fireAction();
		}
		
		this.scheduleTick(5);
		this.updateBlock();
	}
	
	public boolean isOnFire(ForgeDirection face) {
		return super.Rotation != 0 ? false : super.Active;
	}
	
	@Override
	public void onTileTick() {
		super.Delay = false;
		if (super.Active != super.Powered) {
			super.Active = super.Powered;
			this.fireAction();
			this.updateBlock();
		}
	}
}
