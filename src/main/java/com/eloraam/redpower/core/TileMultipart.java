package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.IMultipart;
import com.eloraam.redpower.core.TileExtended;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class TileMultipart extends TileExtended implements IMultipart {
	
	@Override
	public boolean isSideSolid(int side) {
		return false;
	}
	
	@Override
	public boolean isSideNormal(int side) {
		return false;
	}
	
	@Override
	public List<ItemStack> harvestMultipart() {
		ArrayList<ItemStack> ist = new ArrayList<ItemStack>();
		this.addHarvestContents(ist);
		this.deleteBlock();
		return ist;
	}
	
	public void onHarvestPart(EntityPlayer player, int part) {
	}
	
	public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
		return false;
	}
	
	public float getPartStrength(EntityPlayer player, int part) {
		return 0.0F;
	}
	
	public abstract boolean blockEmpty();
	
	public abstract void setPartBounds(BlockMultipart var1, int var2);
	
	public abstract int getSolidPartsMask();
	
	public abstract int getPartsMask();
	
	public void deleteBlock() {
		BlockMultipart.removeMultipartWithNotify(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
	}
}
