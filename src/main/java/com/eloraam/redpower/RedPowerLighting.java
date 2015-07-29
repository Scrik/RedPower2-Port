package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.lighting.BlockLamp;
import com.eloraam.redpower.lighting.BlockShapedLamp;
import com.eloraam.redpower.lighting.ItemLamp;
import com.eloraam.redpower.lighting.ItemShapedLamp;
import com.eloraam.redpower.lighting.LightingProxy;
import com.eloraam.redpower.lighting.TileShapedLamp;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = "RedPowerLighting", name = "RedPower Lighting", version = "2.0pr6", certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b", dependencies = "required-after:RedPowerBase")
public class RedPowerLighting {
	
	@Instance("RedPowerLighting")
	public static RedPowerLighting instance;
	@SidedProxy(clientSide = "com.eloraam.redpower.lighting.LightingProxyClient", serverSide = "com.eloraam.redpower.lighting.LightingProxy")
	public static LightingProxy proxy;
	public static BlockLamp blockLampOff;
	public static BlockLamp blockLampOn;
	public static BlockLamp blockInvLampOff;
	public static BlockLamp blockInvLampOn;
	public static BlockShapedLamp blockShapedLamp;
	public static CreativeTabs tabLamp = new CreativeTabs(
			CreativeTabs.getNextID(), "RPLights") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(RedPowerLighting.blockLampOn, 1, 0);
		}
		
		@Override
		public Item getTabIconItem() {
			return null;
		}
	};
	public static final String textureFile = "/eloraam/lighting/lighting1.png";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		setupLighting();
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public static void setupLighting() {
		blockLampOff = (BlockLamp) new BlockLamp(false, false).setCreativeTab(RedPowerLighting.tabLamp);
		blockLampOn = new BlockLamp(true, true);
		blockLampOn.setLightLevel(1.0F);
		blockLampOff.setBlockName("rplampoff");
		blockLampOff.setBlockTextureName("rplighting:blockLampOff");
		blockLampOn.setBlockName("rplampon");
		blockLampOn.setBlockTextureName("rplighting:blockLampOn");
		GameRegistry.registerBlock(blockLampOn, "lampon");
		GameRegistry.registerBlock(blockLampOff, ItemLamp.class, "lampoff");
		blockLampOn.onBlock = blockLampOff;
		blockLampOn.offBlock = blockLampOn;
		blockLampOff.onBlock = blockLampOff;
		blockLampOff.offBlock = blockLampOn;
		
		blockInvLampOff = (BlockLamp) new BlockLamp(false, true).setCreativeTab(RedPowerLighting.tabLamp);
		blockInvLampOn = new BlockLamp(true, false);
		blockInvLampOn.setLightLevel(1.0F);
		blockInvLampOff.setBlockName("rplampoff");
		blockInvLampOn.setBlockName("rplampon");
		GameRegistry.registerBlock(blockInvLampOn, ItemLamp.class, "ilampon");
		GameRegistry.registerBlock(blockInvLampOff, "ilampoff");
		blockInvLampOn.onBlock = blockInvLampOff;
		blockInvLampOn.offBlock = blockInvLampOn;
		blockInvLampOff.onBlock = blockInvLampOff;
		blockInvLampOff.offBlock = blockInvLampOn;
		
		int i;
		for (i = 0; i < 16; ++i) {
			Config.addName("tile.rplamp." + CoreLib.rawColorNames[i] + ".name",
					CoreLib.enColorNames[i] + " Lamp");
			Config.addName(
					"tile.rpilamp." + CoreLib.rawColorNames[i] + ".name",
					"Inverted " + CoreLib.enColorNames[i] + " Lamp");
			GameRegistry
					.addRecipe(
							new ItemStack(blockLampOff, 1, i),
							new Object[] { "GLG", "GLG", "GRG", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Items.redstone });
			GameRegistry
					.addRecipe(
							new ItemStack(blockInvLampOn, 1, i),
							new Object[] { "GLG", "GLG", "GRG", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Blocks.redstone_torch });
		}
		
		blockShapedLamp = new BlockShapedLamp();
		GameRegistry.registerBlock(blockShapedLamp, ItemShapedLamp.class, "shlamp");
		GameRegistry.registerTileEntity(TileShapedLamp.class, "RPShLamp");
		blockShapedLamp.addTileEntityMapping(0, TileShapedLamp.class);
		
		String nm;
		for (i = 0; i < 16; ++i) {
			nm = "rpshlamp." + CoreLib.rawColorNames[i];
			blockShapedLamp.setBlockName(i, nm);
			Config.addName("tile." + nm + ".name", CoreLib.enColorNames[i]
					+ " Fixture");
			GameRegistry
					.addRecipe(
							new ItemStack(blockShapedLamp, 1, i),
							new Object[] { "GLG", "GLG", "SRS", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Items.redstone, Character
									.valueOf('S'), Blocks.stone_slab });
		}
		
		for (i = 0; i < 16; ++i) {
			nm = "rpishlamp." + CoreLib.rawColorNames[i];
			blockShapedLamp.setBlockName(i + 16, nm);
			Config.addName("tile." + nm + ".name", "Inverted "
					+ CoreLib.enColorNames[i] + " Fixture");
			GameRegistry
					.addRecipe(
							new ItemStack(blockShapedLamp, 1, 16 + i),
							new Object[] { "GLG", "GLG", "SRS", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Blocks.redstone_torch, Character
									.valueOf('S'), new ItemStack(
									Blocks.stone_slab, 1, 0) });
		}
		
		for (i = 0; i < 16; ++i) {
			nm = "rpshlamp2." + CoreLib.rawColorNames[i];
			blockShapedLamp.setBlockName(i + 32, nm);
			Config.addName("tile." + nm + ".name", CoreLib.enColorNames[i]
					+ " Cage Lamp");
			GameRegistry
					.addRecipe(
							new ItemStack(blockShapedLamp, 1, 32 + i),
							new Object[] { "ILI", "GLG", "SRS", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Items.redstone, Character
									.valueOf('I'), Blocks.iron_bars, Character
									.valueOf('S'), new ItemStack(
									Blocks.stone_slab, 1, 0) });
		}
		
		for (i = 0; i < 16; ++i) {
			nm = "rpishlamp2." + CoreLib.rawColorNames[i];
			blockShapedLamp.setBlockName(i + 48, nm);
			Config.addName("tile." + nm + ".name", "Inverted " + CoreLib.enColorNames[i] + " Cage Lamp");
			GameRegistry
					.addRecipe(
							new ItemStack(blockShapedLamp, 1, 48 + i),
							new Object[] { "ILI", "GLG", "SRS", Character
									.valueOf('G'), Blocks.glass_pane, Character
									.valueOf('L'), new ItemStack(
									RedPowerBase.itemLumar, 1, i), Character
									.valueOf('R'), Blocks.redstone_torch, Character
									.valueOf('I'), Blocks.iron_bars, Character
									.valueOf('S'), Blocks.stone_slab });
		}	
	}
}
