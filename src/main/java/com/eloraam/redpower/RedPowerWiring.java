package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.wiring.MicroPlacementJacket;
import com.eloraam.redpower.wiring.MicroPlacementWire;
import com.eloraam.redpower.wiring.TileBluewire;
import com.eloraam.redpower.wiring.TileCable;
import com.eloraam.redpower.wiring.TileInsulatedWire;
import com.eloraam.redpower.wiring.TileRedwire;
import com.eloraam.redpower.wiring.WiringProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Mod(modid = "RedPowerWiring", name = "RedPower Wiring", version = "2.0pr6", certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b", dependencies = "required-after:RedPowerBase")
public class RedPowerWiring {
	
	@Instance("RedPowerWiring")
	public static RedPowerWiring instance;
	@SidedProxy(clientSide = "com.eloraam.redpower.wiring.WiringProxyClient", serverSide = "com.eloraam.redpower.wiring.WiringProxy")
	public static WiringProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		initJacketRecipes();
		setupWires();
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	private static void initJacketRecipes() {
		CoverLib.addMaterialHandler(new CoverLib.IMaterialHandler() {
			@Override
			public void addMaterial(int n) {
				if (!CoverLib.isTransparent(n)) {
					String name = CoverLib.getName(n);
					String desc = CoverLib.getDesc(n);
					Config.addName("tile.rparmwire." + name + ".name", desc + " Jacketed Wire");
					Config.addName("tile.rparmcable." + name + ".name", desc + " Jacketed Cable");
					Config.addName("tile.rparmbwire." + name + ".name", desc + " Jacketed Bluewire");
					GameRegistry
							.addRecipe(
									new ItemStack(RedPowerBase.blockMicro, 4, 16384 + n),
									new Object[] { "SSS", "SRS", "SSS", Character
											.valueOf('S'), new ItemStack(
											RedPowerBase.blockMicro, 1, n), Character
											.valueOf('R'), RedPowerBase.itemIngotRed });
					GameRegistry
							.addRecipe(
									new ItemStack(RedPowerBase.blockMicro, 1, 16640 + n),
									new Object[] { "SSS", "SCS", "SSS", Character
											.valueOf('S'), new ItemStack(
											RedPowerBase.blockMicro, 1, n), Character
											.valueOf('C'), new ItemStack(
											RedPowerBase.blockMicro, 1, 768) });
					GameRegistry
							.addRecipe(
									new ItemStack(RedPowerBase.blockMicro, 4,
											16896 + n),
									new Object[] { "SSS", "SBS", "SSS", Character
											.valueOf('S'), new ItemStack(
											RedPowerBase.blockMicro, 1, n), Character
											.valueOf('B'), RedPowerBase.itemIngotBlue });
					CraftLib.addAlloyResult(CoreLib.copyStack(
							RedPowerBase.itemIngotRed, 1),
							new Object[] { new ItemStack(
									RedPowerBase.blockMicro, 4, 16384 + n) });
					CraftLib.addAlloyResult(CoreLib.copyStack(
							RedPowerBase.itemIngotRed, 5),
							new Object[] { new ItemStack(
									RedPowerBase.blockMicro, 8, 16640 + n) });
					CraftLib.addAlloyResult(CoreLib.copyStack(
							RedPowerBase.itemIngotBlue, 1),
							new Object[] { new ItemStack(
									RedPowerBase.blockMicro, 4, 16896 + n) });
				}
			}
		});
	}
	
	public static void setupWires() {
		GameRegistry.registerTileEntity(TileRedwire.class, "Redwire");
		GameRegistry.registerTileEntity(TileInsulatedWire.class, "InsRedwire");
		GameRegistry.registerTileEntity(TileCable.class, "RedCable");
		GameRegistry.registerTileEntity(TileCovered.class, "Covers");
		GameRegistry.registerTileEntity(TileBluewire.class, "Bluewire");
		MicroPlacementWire imp = new MicroPlacementWire();
		RedPowerBase.blockMicro.registerPlacement(1, imp);
		RedPowerBase.blockMicro.registerPlacement(2, imp);
		RedPowerBase.blockMicro.registerPlacement(3, imp);
		RedPowerBase.blockMicro.registerPlacement(5, imp);
		MicroPlacementJacket var3 = new MicroPlacementJacket();
		RedPowerBase.blockMicro.registerPlacement(64, var3);
		RedPowerBase.blockMicro.registerPlacement(65, var3);
		RedPowerBase.blockMicro.registerPlacement(66, var3);
		RedPowerBase.blockMicro.addTileEntityMapping(1, TileRedwire.class);
		RedPowerBase.blockMicro.addTileEntityMapping(2, TileInsulatedWire.class);
		RedPowerBase.blockMicro.addTileEntityMapping(3, TileCable.class);
		RedPowerBase.blockMicro.addTileEntityMapping(5, TileBluewire.class);
		GameRegistry
				.addRecipe(
						new ItemStack(RedPowerBase.blockMicro, 12, 256),
						new Object[] { "R", "R", "R", Character.valueOf('R'), RedPowerBase.itemIngotRed });
		CraftLib.addAlloyResult(RedPowerBase.itemIngotRed,
				new Object[] { new ItemStack(RedPowerBase.blockMicro, 4, 256) });
		CraftLib.addAlloyResult(
				CoreLib.copyStack(RedPowerBase.itemIngotRed, 5),
				new Object[] { new ItemStack(RedPowerBase.blockMicro, 8, 768) });
		GameRegistry
				.addRecipe(
						new ItemStack(RedPowerBase.blockMicro, 12, 1280),
						new Object[] { "WBW", "WBW", "WBW", Character
								.valueOf('B'), RedPowerBase.itemIngotBlue, Character
								.valueOf('W'), Blocks.wool });
		CraftLib.addAlloyResult(
				RedPowerBase.itemIngotBlue,
				new Object[] { new ItemStack(RedPowerBase.blockMicro, 4, 1280) });
		GameRegistry.addShapelessRecipe(new ItemStack(RedPowerBase.blockMicro,
				1, 1281), new Object[] { new ItemStack(RedPowerBase.blockMicro,
				1, 1280), Blocks.wool });
		CraftLib.addAlloyResult(
				RedPowerBase.itemIngotBlue,
				new Object[] { new ItemStack(RedPowerBase.blockMicro, 4, 1281) });
		
		int i;
		int j;
		for (i = 0; i < 16; ++i) {
			Config.addName("tile.rpinsulated." + CoreLib.rawColorNames[i]
					+ ".name", CoreLib.enColorNames[i] + " Insulated Wire");
			Config.addName(
					"tile.rpcable." + CoreLib.rawColorNames[i] + ".name",
					CoreLib.enColorNames[i] + " Bundled Cable");
			GameRegistry
					.addRecipe(
							new ItemStack(RedPowerBase.blockMicro, 12, 512 + i),
							new Object[] { "WRW", "WRW", "WRW", Character
									.valueOf('R'), RedPowerBase.itemIngotRed, Character
									.valueOf('W'), new ItemStack(Blocks.wool,
									1, i) });
			
			for (j = 0; j < 16; ++j) {
				if (i != j) {
					GameRegistry
							.addShapelessRecipe(
									new ItemStack(RedPowerBase.blockMicro, 1,
											512 + i),
									new Object[] { new ItemStack(
											RedPowerBase.blockMicro, 1, 512 + j), new ItemStack(
											Items.dye, 1, 15 - i) });
					GameRegistry
							.addShapelessRecipe(
									new ItemStack(RedPowerBase.blockMicro, 1,
											769 + i),
									new Object[] { new ItemStack(
											RedPowerBase.blockMicro, 1, 769 + j), new ItemStack(
											Items.dye, 1, 15 - i) });
				}
			}
			
			CraftLib.addAlloyResult(RedPowerBase.itemIngotRed,
					new Object[] { new ItemStack(RedPowerBase.blockMicro, 4,
							512 + i) });
			GameRegistry
					.addRecipe(
							new ItemStack(RedPowerBase.blockMicro, 2, 768),
							new Object[] { "SWS", "WWW", "SWS", Character
									.valueOf('W'), new ItemStack(
									RedPowerBase.blockMicro, 1, 512 + i), Character
									.valueOf('S'), Items.string });
			GameRegistry
					.addShapelessRecipe(new ItemStack(RedPowerBase.blockMicro,
							1, 769 + i), new Object[] { new ItemStack(
							RedPowerBase.blockMicro, 1, 768), new ItemStack(
							Items.dye, 1, 15 - i), Items.paper });
			CraftLib.addAlloyResult(CoreLib.copyStack(
					RedPowerBase.itemIngotRed, 5),
					new Object[] { new ItemStack(RedPowerBase.blockMicro, 8,
							769 + i) });
		}
		
		for (i = 0; i < 16; ++i) {
			if (i != 11) {
				CraftLib.addShapelessOreRecipe(new ItemStack(
						RedPowerBase.blockMicro, 1, 523),
						new Object[] { new ItemStack(RedPowerBase.blockMicro,
								1, 512 + i), "dyeBlue" });
				CraftLib.addShapelessOreRecipe(new ItemStack(
						RedPowerBase.blockMicro, 1, 780),
						new Object[] { new ItemStack(RedPowerBase.blockMicro,
								1, 769 + i), "dyeBlue" });
			}
		}
		
		CraftLib.addShapelessOreRecipe(new ItemStack(RedPowerBase.blockMicro,
				1, 780), new Object[] { new ItemStack(RedPowerBase.blockMicro,
				1, 768), "dyeBlue", Items.paper });
		RedPowerLib.addCompatibleMapping(0, 1);
		
		for (i = 0; i < 16; ++i) {
			RedPowerLib.addCompatibleMapping(0, 2 + i);
			RedPowerLib.addCompatibleMapping(1, 2 + i);
			RedPowerLib.addCompatibleMapping(65, 2 + i);
			
			for (j = 0; j < 16; ++j) {
				RedPowerLib.addCompatibleMapping(19 + j, 2 + i);
			}
			
			RedPowerLib.addCompatibleMapping(18, 2 + i);
			RedPowerLib.addCompatibleMapping(18, 19 + i);
		}
		
		RedPowerLib.addCompatibleMapping(0, 65);
		RedPowerLib.addCompatibleMapping(1, 65);
		RedPowerLib.addCompatibleMapping(64, 65);
		RedPowerLib.addCompatibleMapping(64, 67);
		RedPowerLib.addCompatibleMapping(65, 67);
		RedPowerLib.addCompatibleMapping(66, 67);
	}
}
