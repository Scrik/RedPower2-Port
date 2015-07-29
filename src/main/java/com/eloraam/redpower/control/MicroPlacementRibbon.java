package com.eloraam.redpower.control;

import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.wiring.MicroPlacementWire;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MicroPlacementRibbon extends MicroPlacementWire {
	
	@Override
	public String getMicroName(int hb, int lb) {
		return hb != 12 && lb != 0 ? null : "tile.ribbon";
	}
	
	@Override
	public void addCreativeItems(int hb, CreativeTabs tab, List itemList) {
		if (tab == CreativeExtraTabs.tabWires) {
			if (hb == 12) {
				itemList.add(new ItemStack(CoverLib.blockCoverPlate, 1, 3072));
			}
		}
	}
}
