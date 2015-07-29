package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.machine.TileMachinePanel;
import com.eloraam.redpower.machine.TileSolarPanel;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileSolarPanel extends TileMachinePanel implements
		IBluePowerConnectable {
	
	BluePowerConductor cond = new BluePowerConductor() {
		@Override
		public TileEntity getParent() {
			return TileSolarPanel.this;
		}
		
		@Override
		public double getInvCap() {
			return 4.0D;
		}
	};
	public int ConMask = -1;
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		if (!World.doesBlockHaveSolidTopSurface(super.worldObj, super.xCoord, super.yCoord - 1, super.zCoord)) {
			this.breakBlock();
		}
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
	}
	
	@Override
	public int getConnectableMask() {
		return 16777231;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 64;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	@Override
	public void updateEntity() {
		if (!CoreLib.isClient(super.worldObj)) {
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, 0);
			}
			this.cond.iterate();
			this.markDirty();
			if (this.cond.getVoltage() <= 100.0D) {
				if (super.worldObj.canBlockSeeTheSky(super.xCoord,
						super.yCoord, super.zCoord)) {
					if (super.worldObj.isDaytime()) {
						if (!super.worldObj.provider.hasNoSky) {
							this.cond.applyDirect(2.0D);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		this.cond.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		this.cond.writeToNBT(nbttagcompound);
	}
}
