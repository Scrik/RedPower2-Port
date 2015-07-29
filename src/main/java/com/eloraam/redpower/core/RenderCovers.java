package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CoverRenderer;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public abstract class RenderCovers extends RenderCustomBlock {
	
	protected static IIcon[][] coverIcons = CoverLib.coverIcons;
	protected CoverRenderer coverRenderer;
	protected RenderContext context = new RenderContext();
	
	public RenderCovers(Block bl) {
		super(bl);
		this.coverRenderer = new CoverRenderer(this.context);
	}
	
	public void renderCovers(int uc, short[] covs) {
		this.coverRenderer.render(uc, covs);
	}
}
