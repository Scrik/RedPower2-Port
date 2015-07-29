package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ItemMicro;
import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;

import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

public class BlockMicro extends BlockCoverable {
	
	public BlockMicro() {
		super(CoreLib.materialRedpower);
		this.setHardness(0.1F);
		this.setCreativeTab(CreativeExtraTabs.tabWires);
	}
	
	@Override
	public boolean canProvidePower() {
		return !RedPowerLib.isSearching();
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess iba, int i, int j, int k, int dir) {
		if (RedPowerLib.isSearching()) {
			return false;
		} else {
			int md = iba.getBlockMetadata(i, j, k);
			return md == 1 || md == 2;
		}
	}
	
	public void registerPlacement(int md, IMicroPlacement imp) {
		((ItemMicro) Item.getItemFromBlock(this)).registerPlacement(md, imp);
	}
}
