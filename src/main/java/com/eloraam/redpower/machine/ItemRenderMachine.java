package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.machine.ItemRenderMachine;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class ItemRenderMachine implements IItemRenderer {
	
	protected RenderContext context = new RenderContext();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return item.getItem() == Item.getItemFromBlock(RedPowerMachine.blockMachine) && item.getItemDamage() == 6 ? item.stackTagCompound != null : false;
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		switch (helper) {
			case BLOCK_3D:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (item.getItemDamage() == 6) {
			this.context.setDefaults();
			this.context.setPos(-0.5D, -0.5D, -0.5D);
			this.context.useNormal = true;
			//RenderLib.bindTexture("/eloraam/machine/machine1.png");
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			short bat = 0;
			if (item.stackTagCompound != null) {
				bat = item.stackTagCompound.getShort("batLevel");
			}
			Item parent = item.getItem();
			IIcon tx = parent.getIconFromDamage(129 + bat * 8 / 6000); //TODO: Жирный костыль
			this.context.setIcon(parent.getIconFromDamage(84), parent.getIconFromDamage(128), tx, tx, tx, tx);
			this.context.renderBox(63, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
			tessellator.draw();
			//RenderLib.unbindTexture();
			this.context.useNormal = false;
		}
		
	}
	
	// $FF: synthetic class
	static class NamelessClass31344489 {
		
		// $FF: synthetic field
		static final int[] $SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper = new int[ItemRendererHelper
				.values().length];
		
		static {
			try {
				$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper[ItemRendererHelper.INVENTORY_BLOCK
						.ordinal()] = 1;
			} catch (NoSuchFieldError var5) {
				;
			}
			
			try {
				$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper[ItemRendererHelper.EQUIPPED_BLOCK
						.ordinal()] = 2;
			} catch (NoSuchFieldError var4) {
				;
			}
			
			try {
				$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper[ItemRendererHelper.ENTITY_ROTATION
						.ordinal()] = 3;
			} catch (NoSuchFieldError var3) {
				;
			}
			
			try {
				$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper[ItemRendererHelper.ENTITY_BOBBING
						.ordinal()] = 4;
			} catch (NoSuchFieldError var2) {
				;
			}
			
			try {
				$SwitchMap$net$minecraftforge$client$IItemRenderer$ItemRendererHelper[ItemRendererHelper.BLOCK_3D
						.ordinal()] = 5;
			} catch (NoSuchFieldError var1) {
				;
			}
			
		}
	}
}
