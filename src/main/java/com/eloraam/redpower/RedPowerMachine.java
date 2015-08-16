package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.AchieveLib;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.ItemTextured;
import com.eloraam.redpower.machine.BlockFrame;
import com.eloraam.redpower.machine.BlockMachine;
import com.eloraam.redpower.machine.BlockMachinePanel;
import com.eloraam.redpower.machine.ItemBattery;
import com.eloraam.redpower.machine.ItemMachinePanel;
import com.eloraam.redpower.machine.ItemSonicDriver;
import com.eloraam.redpower.machine.ItemVoltmeter;
import com.eloraam.redpower.machine.ItemWindmill;
import com.eloraam.redpower.machine.MachineProxy;
import com.eloraam.redpower.machine.MicroPlacementTube;
import com.eloraam.redpower.machine.TileAccel;
import com.eloraam.redpower.machine.TileAssemble;
import com.eloraam.redpower.machine.TileBatteryBox;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;
import com.eloraam.redpower.machine.TileBlueFurnace;
import com.eloraam.redpower.machine.TileBreaker;
import com.eloraam.redpower.machine.TileBufferChest;
import com.eloraam.redpower.machine.TileChargingBench;
import com.eloraam.redpower.machine.TileDeploy;
import com.eloraam.redpower.machine.TileEject;
import com.eloraam.redpower.machine.TileFilter;
import com.eloraam.redpower.machine.TileFrame;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileFrameRedstoneTube;
import com.eloraam.redpower.machine.TileFrameTube;
import com.eloraam.redpower.machine.TileGrate;
import com.eloraam.redpower.machine.TileIgniter;
import com.eloraam.redpower.machine.TileItemDetect;
import com.eloraam.redpower.machine.TileMagTube;
import com.eloraam.redpower.machine.TileManager;
import com.eloraam.redpower.machine.TileMotor;
import com.eloraam.redpower.machine.TilePipe;
import com.eloraam.redpower.machine.TilePump;
import com.eloraam.redpower.machine.TileRedstoneTube;
import com.eloraam.redpower.machine.TileRegulator;
import com.eloraam.redpower.machine.TileRelay;
import com.eloraam.redpower.machine.TileRestrictTube;
import com.eloraam.redpower.machine.TileRetriever;
import com.eloraam.redpower.machine.TileSolarPanel;
import com.eloraam.redpower.machine.TileSorter;
import com.eloraam.redpower.machine.TileSortron;
import com.eloraam.redpower.machine.TileThermopile;
import com.eloraam.redpower.machine.TileTransformer;
import com.eloraam.redpower.machine.TileTranspose;
import com.eloraam.redpower.machine.TileTube;
import com.eloraam.redpower.machine.TileWindTurbine;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

@Mod(modid = "RedPowerMachine", name = "RedPower Machine", version = "2.0pr6", certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b", dependencies = "required-after:RedPowerBase")
public class RedPowerMachine {
	
	@Instance("RedPowerMachine")
	public static RedPowerMachine instance;
	@SidedProxy(clientSide = "com.eloraam.redpower.machine.MachineProxyClient", serverSide = "com.eloraam.redpower.machine.MachineProxy")
	public static MachineProxy proxy;
	public static BlockMachine blockMachine;
	public static BlockMachine blockMachine2;
	public static BlockMachinePanel blockMachinePanel;
	public static BlockFrame blockFrame;
	public static ItemVoltmeter itemVoltmeter;
	public static ItemSonicDriver itemSonicDriver;
	public static Item itemBatteryEmpty;
	public static Item itemBatteryPowered;
	public static ItemParts itemMachineParts;
	public static ItemStack itemWoodSail;
	public static Item itemWoodTurbine;
	public static Item itemWoodWindmill;
	public static boolean FrameAlwaysCrate;
	public static int FrameLinkSize;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		FrameAlwaysCrate = Config.getInt("settings.machine.frame.alwayscrate") > 0;
		FrameLinkSize = Config.getInt("settings.machine.frame.linksize");
		setupItems();
		setupBlocks();
		initAchievements();
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	private static void setupItems() {
		itemVoltmeter = new ItemVoltmeter();
		itemBatteryEmpty = new ItemTextured("rpmachine:itemBattery").setUnlocalizedName("btbattery").setCreativeTab(CreativeTabs.tabRedstone);
		itemBatteryPowered = new ItemBattery();
		CraftLib.addOreRecipe(
				new ItemStack(itemVoltmeter),
				new Object[] { "WWW", "WNW", "CCC", Character.valueOf('W'), "plankWood", Character
						.valueOf('N'), RedPowerBase.itemNikolite, Character.valueOf('C'), "ingotCopper" });
		GameRegistry.registerItem(itemVoltmeter, "voltmeter");
		CraftLib.addOreRecipe(
				new ItemStack(itemBatteryEmpty, 1),
				new Object[] { "NCN", "NTN", "NCN", Character.valueOf('N'), RedPowerBase.itemNikolite, Character
						.valueOf('C'), "ingotCopper", Character.valueOf('T'), "ingotTin" });
		GameRegistry.registerItem(itemBatteryEmpty, "batteryEmpty");
		GameRegistry.registerItem(itemBatteryPowered, "batteryPowered");
		itemSonicDriver = new ItemSonicDriver();
		itemSonicDriver.setUnlocalizedName("sonicDriver").setTextureName("rpmachine:itemSonicScrewdriver");
		GameRegistry
				.addRecipe(
						new ItemStack(itemSonicDriver, 1, itemSonicDriver.getMaxDamage()),
						new Object[] { "E  ", " R ", "  B", Character
								.valueOf('R'), RedPowerBase.itemIngotBrass, Character
								.valueOf('E'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('B'), itemBatteryEmpty });
		GameRegistry.registerItem(itemSonicDriver, "sonicDriver");
		itemWoodTurbine = new ItemWindmill(1);
		itemWoodWindmill = new ItemWindmill(2).setUnlocalizedName("windmillWood").setTextureName("rpmachine:itemWindmill");
		itemMachineParts = new ItemParts();
		itemMachineParts.addItem(0, "rpmachine:itemWindSailWood", "item.windSailWood");
		itemWoodSail = new ItemStack(itemMachineParts, 1, 0);
		GameRegistry.registerItem(itemMachineParts, "machineParts");
		CraftLib.addOreRecipe(itemWoodSail,
				new Object[] { "CCS", "CCW", "CCS", Character.valueOf('C'), RedPowerBase.itemCanvas, Character
						.valueOf('W'), "plankWood", Character.valueOf('S'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemWoodTurbine),
						new Object[] { "SAS", "SAS", "SAS", Character
								.valueOf('S'), itemWoodSail, Character
								.valueOf('A'), new ItemStack(
								RedPowerBase.blockMicro, 1, 5905) });
		GameRegistry
				.addRecipe(
						new ItemStack(itemWoodWindmill),
						new Object[] { " S ", "SAS", " S ", Character
								.valueOf('S'), itemWoodSail, Character
								.valueOf('A'), new ItemStack(
								RedPowerBase.blockMicro, 1, 5905) });
		GameRegistry.registerItem(itemWoodTurbine, "woodTurbine");
		GameRegistry.registerItem(itemWoodWindmill, "woodWindmill");
	}
	
	private static void setupBlocks() {
		blockMachine = new BlockMachine();
		blockMachine.setBlockName("rpmachine");
		GameRegistry.registerBlock(blockMachine, ItemExtended.class, "machine");
		blockMachine.setBlockName(0, "rpdeploy");
		blockMachine.setBlockName(1, "rpbreaker");
		blockMachine.setBlockName(2, "rptranspose");
		blockMachine.setBlockName(3, "rpfilter");
		blockMachine.setBlockName(4, "rpitemdet");
		blockMachine.setBlockName(5, "rpsorter");
		blockMachine.setBlockName(6, "rpbatbox");
		blockMachine.setBlockName(7, "rpmotor");
		blockMachine.setBlockName(8, "rpretriever");
		blockMachine.setBlockName(9, "rpkgen");
		blockMachine.setBlockName(10, "rpregulate");
		blockMachine.setBlockName(11, "rpthermo");
		blockMachine.setBlockName(12, "rpignite");
		blockMachine.setBlockName(13, "rpassemble");
		blockMachine.setBlockName(14, "rpeject");
		blockMachine.setBlockName(15 ,"rprelay");
		GameRegistry.registerTileEntity(TileWindTurbine.class, "RPWind");
		GameRegistry.registerTileEntity(TilePipe.class, "RPPipe");
		GameRegistry.registerTileEntity(TilePump.class, "RPPump");
		GameRegistry.registerTileEntity(TileTube.class, "RPTube");
		GameRegistry.registerTileEntity(TileRedstoneTube.class, "RPRSTube");
		GameRegistry.registerTileEntity(TileRestrictTube.class, "RPRTube");
		GameRegistry.registerTileEntity(TileMagTube.class, "RPMTube");
		GameRegistry.registerTileEntity(TileAccel.class, "RPAccel");
		GameRegistry.registerTileEntity(TileDeploy.class, "RPDeploy");
		GameRegistry.registerTileEntity(TileBreaker.class, "RPBreaker");
		GameRegistry.registerTileEntity(TileTranspose.class, "RPTranspose");
		GameRegistry.registerTileEntity(TileFilter.class, "RPFilter");
		GameRegistry.registerTileEntity(TileItemDetect.class, "RPItemDet");
		GameRegistry.registerTileEntity(TileSorter.class, "RPSorter");
		GameRegistry.registerTileEntity(TileBatteryBox.class, "RPBatBox");
		GameRegistry.registerTileEntity(TileMotor.class, "RPMotor");
		GameRegistry.registerTileEntity(TileRetriever.class, "RPRetrieve");
		GameRegistry.registerTileEntity(TileRegulator.class, "RPRegulate");
		GameRegistry.registerTileEntity(TileThermopile.class, "RPThermo");
		GameRegistry.registerTileEntity(TileIgniter.class, "RPIgnite");
		GameRegistry.registerTileEntity(TileAssemble.class, "RPAssemble");
		GameRegistry.registerTileEntity(TileEject.class, "RPEject");
		GameRegistry.registerTileEntity(TileRelay.class, "RPRelay");
		blockMachine.addTileEntityMapping(0, TileDeploy.class);
		blockMachine.addTileEntityMapping(1, TileBreaker.class);
		blockMachine.addTileEntityMapping(2, TileTranspose.class);
		blockMachine.addTileEntityMapping(3, TileFilter.class);
		blockMachine.addTileEntityMapping(4, TileItemDetect.class);
		blockMachine.addTileEntityMapping(5, TileSorter.class);
		blockMachine.addTileEntityMapping(6, TileBatteryBox.class);
		blockMachine.addTileEntityMapping(7, TileMotor.class);
		blockMachine.addTileEntityMapping(8, TileRetriever.class);
		blockMachine.addTileEntityMapping(9, TileWindTurbine.class);
		blockMachine.addTileEntityMapping(10, TileRegulator.class);
		blockMachine.addTileEntityMapping(11, TileThermopile.class);
		blockMachine.addTileEntityMapping(12, TileIgniter.class);
		blockMachine.addTileEntityMapping(13, TileAssemble.class);
		blockMachine.addTileEntityMapping(14, TileEject.class);
		blockMachine.addTileEntityMapping(15, TileRelay.class);
		blockMachine2 = new BlockMachine();
		blockMachine.setBlockName("rpmachine2");
		GameRegistry.registerBlock(blockMachine2, ItemExtended.class, "machine2");
		blockMachine2.setBlockName(0, "rpsortron");
		blockMachine2.setBlockName(1, "rpmanager");
		GameRegistry.registerTileEntity(TileSortron.class, "RPSortron");
		GameRegistry.registerTileEntity(TileManager.class, "RPManager");
		blockMachine2.addTileEntityMapping(0, TileSortron.class);
		blockMachine2.addTileEntityMapping(1, TileManager.class);
		blockMachinePanel = new BlockMachinePanel();
		GameRegistry.registerBlock(blockMachinePanel, ItemMachinePanel.class, "machinePanel");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "RPSolar");
		GameRegistry.registerTileEntity(TileGrate.class, "RPGrate");
		GameRegistry.registerTileEntity(TileTransformer.class, "RPXfmr");
		blockMachinePanel.addTileEntityMapping(0, TileSolarPanel.class);
		blockMachinePanel.addTileEntityMapping(1, TilePump.class);
		blockMachinePanel.addTileEntityMapping(2, TileAccel.class);
		blockMachinePanel.addTileEntityMapping(3, TileGrate.class);
		blockMachinePanel.addTileEntityMapping(4, TileTransformer.class);
		blockMachinePanel.setBlockName(0, "rpsolar");
		blockMachinePanel.setBlockName(1, "rppump");
		blockMachinePanel.setBlockName(2, "rpaccel");
		blockMachinePanel.setBlockName(3, "rpgrate");
		blockMachinePanel.setBlockName(4, "rptransformer");
		GameRegistry.registerTileEntity(TileBlueFurnace.class, "RPBFurnace");
		GameRegistry.registerTileEntity(TileBufferChest.class, "RPBuffer");
		GameRegistry.registerTileEntity(TileBlueAlloyFurnace.class, "RPBAFurnace");
		GameRegistry.registerTileEntity(TileChargingBench.class, "RPCharge");
		RedPowerBase.blockAppliance.setBlockName(1, "rpbfurnace");
		RedPowerBase.blockAppliance.addTileEntityMapping(1, TileBlueFurnace.class);
		RedPowerBase.blockAppliance.setBlockName(2, "rpbuffer");
		RedPowerBase.blockAppliance.addTileEntityMapping(2, TileBufferChest.class);
		RedPowerBase.blockAppliance.setBlockName(4, "rpbafurnace");
		RedPowerBase.blockAppliance.addTileEntityMapping(4, TileBlueAlloyFurnace.class);
		RedPowerBase.blockAppliance.setBlockName(5, "rpcharge");
		RedPowerBase.blockAppliance.addTileEntityMapping(5, TileChargingBench.class);
		blockFrame = new BlockFrame();
		GameRegistry.registerBlock(blockFrame, ItemExtended.class, "frame");
		blockFrame.setBlockName("rpframe");
		blockFrame.setBlockName(0, "rpframe");
		blockFrame.setBlockName(2, "rptframe");
		blockFrame.setBlockName(3, "rprtframe");
		GameRegistry.registerTileEntity(TileFrame.class, "RPFrame");
		GameRegistry.registerTileEntity(TileFrameMoving.class, "RPMFrame");
		GameRegistry.registerTileEntity(TileFrameTube.class, "RPTFrame");
		GameRegistry.registerTileEntity(TileFrameRedstoneTube.class, "RPRTFrame");
		blockFrame.addTileEntityMapping(0, TileFrame.class);
		blockFrame.addTileEntityMapping(1, TileFrameMoving.class);
		blockFrame.addTileEntityMapping(2, TileFrameTube.class);
		blockFrame.addTileEntityMapping(3, TileFrameRedstoneTube.class);
		MicroPlacementTube imp = new MicroPlacementTube();
		RedPowerBase.blockMicro.registerPlacement(7, imp);
		RedPowerBase.blockMicro.registerPlacement(8, imp);
		RedPowerBase.blockMicro.registerPlacement(9, imp);
		RedPowerBase.blockMicro.registerPlacement(10, imp);
		RedPowerBase.blockMicro.registerPlacement(11, imp);
		RedPowerBase.blockMicro.addTileEntityMapping(7, TilePipe.class);
		RedPowerBase.blockMicro.addTileEntityMapping(8, TileTube.class);
		RedPowerBase.blockMicro.addTileEntityMapping(9, TileRedstoneTube.class);
		RedPowerBase.blockMicro.addTileEntityMapping(10, TileRestrictTube.class);
		RedPowerBase.blockMicro.addTileEntityMapping(11, TileMagTube.class);
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 0),
						new Object[] { "SCS", "SPS", "SRS", Character
								.valueOf('S'), Blocks.cobblestone, Character
								.valueOf('C'), Blocks.chest, Character
								.valueOf('R'), Items.redstone, Character
								.valueOf('P'), Blocks.piston });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 1),
						new Object[] { "SAS", "SPS", "SRS", Character
								.valueOf('S'), Blocks.cobblestone, Character
								.valueOf('A'), Items.iron_pickaxe, Character
								.valueOf('R'), Items.redstone, Character
								.valueOf('P'), Blocks.piston });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 2),
				new Object[] { "SSS", "WPW", "SRS", Character.valueOf('S'), Blocks.cobblestone, Character
						.valueOf('R'), Items.redstone, Character.valueOf('P'), Blocks.piston, Character
						.valueOf('W'), "plankWood" });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 3),
						new Object[] { "SSS", "GPG", "SRS", Character
								.valueOf('S'), Blocks.cobblestone, Character
								.valueOf('R'), RedPowerBase.itemWaferRed, Character
								.valueOf('P'), Blocks.piston, Character
								.valueOf('G'), Items.gold_ingot });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 4),
				new Object[] { "BTB", "RPR", "WTW", Character.valueOf('B'), "ingotBrass", Character
						.valueOf('T'), new ItemStack(RedPowerBase.blockMicro,
						1, 2048), Character.valueOf('R'), RedPowerBase.itemWaferRed, Character
						.valueOf('W'), "plankWood", Character.valueOf('P'), Blocks.wooden_pressure_plate });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 5),
						new Object[] { "III", "RFR", "IBI", Character
								.valueOf('B'), RedPowerBase.itemIngotBlue, Character
								.valueOf('R'), RedPowerBase.itemWaferRed, Character
								.valueOf('F'), new ItemStack(blockMachine, 1, 3), Character
								.valueOf('I'), Items.iron_ingot });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 8),
						new Object[] { "BLB", "EFE", "INI", Character
								.valueOf('N'), RedPowerBase.itemIngotBlue, Character
								.valueOf('B'), RedPowerBase.itemIngotBrass, Character
								.valueOf('E'), Items.ender_pearl, Character
								.valueOf('L'), Items.leather, Character
								.valueOf('F'), new ItemStack(blockMachine, 1, 3), Character
								.valueOf('I'), Items.iron_ingot });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 9),
						new Object[] { "IBI", "IMI", "IUI", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('B'), RedPowerBase.itemIngotBrass, Character
								.valueOf('M'), RedPowerBase.itemMotor, Character
								.valueOf('U'), RedPowerBase.itemIngotBlue });
		CraftLib.addOreRecipe(
				new ItemStack(RedPowerBase.blockAppliance, 1, 2),
				new Object[] { "BWB", "W W", "BWB", Character.valueOf('B'), Blocks.iron_bars, Character
						.valueOf('W'), "plankWood" });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 10),
				new Object[] { "BCB", "RDR", "WCW", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character
						.valueOf('B'), "ingotBrass", Character.valueOf('D'), new ItemStack(
						blockMachine, 1, 4), Character.valueOf('W'), "plankWood", Character
						.valueOf('C'), new ItemStack(
						RedPowerBase.blockAppliance, 1, 2) });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 11),
				new Object[] { "CIC", "WBW", "CIC", Character.valueOf('I'), Items.iron_ingot, Character
						.valueOf('B'), RedPowerBase.itemIngotBlue, Character
						.valueOf('W'), RedPowerBase.itemWaferBlue, Character
						.valueOf('C'), "ingotCopper" });
		CraftLib.addOreRecipe(
				new ItemStack(RedPowerBase.blockMicro, 8, 2048),
				new Object[] { "BGB", Character.valueOf('G'), Blocks.glass, Character
						.valueOf('B'), "ingotBrass" });
		GameRegistry.addShapelessRecipe(new ItemStack(RedPowerBase.blockMicro,
				1, 2304), new Object[] { Items.redstone, new ItemStack(
				RedPowerBase.blockMicro, 1, 2048) });
		GameRegistry.addShapelessRecipe(new ItemStack(RedPowerBase.blockMicro,
				1, 2560), new Object[] { Items.iron_ingot, new ItemStack(
				RedPowerBase.blockMicro, 1, 2048) });
		GameRegistry
				.addRecipe(
						new ItemStack(RedPowerBase.blockMicro, 8, 2816),
						new Object[] { "CCC", "OGO", "CCC", Character
								.valueOf('G'), Blocks.glass, Character
								.valueOf('O'), Blocks.obsidian, Character
								.valueOf('C'), RedPowerBase.itemFineCopper });
		GameRegistry
				.addRecipe(
						new ItemStack(RedPowerBase.blockAppliance, 1, 1),
						new Object[] { "CCC", "C C", "IBI", Character
								.valueOf('C'), Blocks.clay, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue, Character
								.valueOf('I'), Items.iron_ingot });
		GameRegistry
				.addRecipe(
						new ItemStack(RedPowerBase.blockAppliance, 1, 4),
						new Object[] { "CCC", "C C", "IBI", Character
								.valueOf('C'), Blocks.brick_block, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue, Character
								.valueOf('I'), Items.iron_ingot });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachinePanel, 1, 0),
						new Object[] { "WWW", "WBW", "WWW", Character
								.valueOf('W'), RedPowerBase.itemWaferBlue, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachinePanel, 1, 2),
						new Object[] { "BOB", "O O", "BOB", Character
								.valueOf('O'), Blocks.obsidian, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 6),
				new Object[] { "BWB", "BIB", "IAI", Character.valueOf('I'), Items.iron_ingot, Character
						.valueOf('W'), "plankWood", Character.valueOf('A'), RedPowerBase.itemIngotBlue, Character
						.valueOf('B'), itemBatteryEmpty });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachinePanel, 1, 4),
						new Object[] { "III", "CIC", "BIB", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('C'), RedPowerBase.itemCopperCoil, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine2, 1, 0),
						new Object[] { "IDI", "RSR", "IWI", Character
								.valueOf('D'), Items.diamond, Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('R'), RedPowerBase.itemWaferRed, Character
								.valueOf('W'), new ItemStack(
								RedPowerBase.blockMicro, 1, 3072), Character
								.valueOf('S'), new ItemStack(blockMachine, 1, 5) });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine2, 1, 1),
				new Object[] { "IMI", "RSR", "WBW", Character.valueOf('I'), Items.iron_ingot, Character
						.valueOf('R'), RedPowerBase.itemWaferRed, Character
						.valueOf('S'), new ItemStack(blockMachine, 1, 5), Character
						.valueOf('M'), new ItemStack(blockMachine, 1, 10), Character
						.valueOf('W'), "plankWood", Character.valueOf('B'), RedPowerBase.itemIngotBlue });
		CraftLib.addOreRecipe(
				new ItemStack(RedPowerBase.blockAppliance, 1, 5),
				new Object[] { "OQO", "BCB", "WUW", Character.valueOf('O'), Blocks.obsidian, Character
						.valueOf('W'), "plankWood", Character.valueOf('U'), RedPowerBase.itemIngotBlue, Character
						.valueOf('C'), Blocks.chest, Character.valueOf('Q'), RedPowerBase.itemCopperCoil, Character
						.valueOf('B'), itemBatteryEmpty });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 12),
						new Object[] { "NFN", "SDS", "SRS", Character
								.valueOf('N'), Blocks.netherrack, Character
								.valueOf('F'), Items.flint_and_steel, Character
								.valueOf('D'), new ItemStack(blockMachine, 1, 0), Character
								.valueOf('S'), Blocks.cobblestone, Character
								.valueOf('R'), Items.redstone });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachine, 1, 13),
						new Object[] { "BIB", "CDC", "IRI", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('D'), new ItemStack(blockMachine, 1, 0), Character
								.valueOf('C'), new ItemStack(
								RedPowerBase.blockMicro, 1, 768), Character
								.valueOf('R'), RedPowerBase.itemWaferRed, Character
								.valueOf('B'), RedPowerBase.itemIngotBrass });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 14),
				new Object[] { "WBW", "WTW", "SRS", Character.valueOf('R'), Items.redstone, Character
						.valueOf('T'), new ItemStack(blockMachine, 1, 2), Character
						.valueOf('W'), "plankWood", Character.valueOf('B'), new ItemStack(
						RedPowerBase.blockAppliance, 1, 2), Character
						.valueOf('S'), Blocks.cobblestone });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 15),
				new Object[] { "WBW", "WTW", "SRS", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character
						.valueOf('T'), new ItemStack(blockMachine, 1, 2), Character
						.valueOf('W'), "plankWood", Character.valueOf('B'), new ItemStack(
						RedPowerBase.blockAppliance, 1, 2), Character
						.valueOf('S'), Blocks.cobblestone });
		GameRegistry
				.addRecipe(
						RedPowerBase.itemCopperCoil,
						new Object[] { "FBF", "BIB", "FBF", Character
								.valueOf('F'), RedPowerBase.itemFineCopper, Character
								.valueOf('B'), Blocks.iron_bars, Character
								.valueOf('I'), Items.iron_ingot });
		GameRegistry
				.addRecipe(
						RedPowerBase.itemMotor,
						new Object[] { "ICI", "ICI", "IBI", Character
								.valueOf('C'), RedPowerBase.itemCopperCoil, Character
								.valueOf('B'), RedPowerBase.itemIngotBlue, Character
								.valueOf('I'), Items.iron_ingot });
		CraftLib.addOreRecipe(
				new ItemStack(blockFrame, 1),
				new Object[] { "SSS", "SBS", "SSS", Character.valueOf('S'), Items.stick, Character
						.valueOf('B'), "ingotBrass" });
		GameRegistry.addShapelessRecipe(new ItemStack(blockFrame, 1, 2),
				new Object[] { new ItemStack(blockFrame, 1), new ItemStack(
						RedPowerBase.blockMicro, 1, 2048) });
		GameRegistry.addShapelessRecipe(new ItemStack(blockFrame, 1, 3),
				new Object[] { new ItemStack(blockFrame, 1), new ItemStack(
						RedPowerBase.blockMicro, 1, 2304) });
		GameRegistry
				.addShapelessRecipe(
						new ItemStack(blockFrame, 1, 3),
						new Object[] { new ItemStack(blockFrame, 1, 2), Items.redstone });
		CraftLib.addOreRecipe(
				new ItemStack(blockMachine, 1, 7),
				new Object[] { "III", "BMB", "IAI", Character.valueOf('I'), Items.iron_ingot, Character
						.valueOf('A'), RedPowerBase.itemIngotBlue, Character
						.valueOf('B'), "ingotBrass", Character.valueOf('M'), RedPowerBase.itemMotor });
		CraftLib.addOreRecipe(
				new ItemStack(RedPowerBase.blockMicro, 16, 1792),
				new Object[] { "B B", "BGB", "B B", Character.valueOf('G'), Blocks.glass, Character
						.valueOf('B'), "ingotBrass" });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachinePanel, 1, 3),
						new Object[] { "III", "I I", "IPI", Character
								.valueOf('P'), new ItemStack(
								RedPowerBase.blockMicro, 1, 1792), Character
								.valueOf('I'), Blocks.iron_bars });
		GameRegistry
				.addRecipe(
						new ItemStack(blockMachinePanel, 1, 1),
						new Object[] { "III", "PMP", "IAI", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('A'), RedPowerBase.itemIngotBlue, Character
								.valueOf('P'), new ItemStack(
								RedPowerBase.blockMicro, 1, 1792), Character
								.valueOf('M'), RedPowerBase.itemMotor });
	}
	
	public static void initAchievements() {
		AchieveLib.registerAchievement("0x1CA23", "rpTranspose", -2, 2, new ItemStack(blockMachine, 1, 2), AchievementList.acquireIron);
		AchieveLib.registerAchievement("0x1CA24", "rpBreaker", -2, 4, new ItemStack(blockMachine, 1, 1), AchievementList.acquireIron);
		AchieveLib.registerAchievement("0x1CA25", "rpDeploy", -2, 6, new ItemStack(blockMachine, 1, 0), AchievementList.acquireIron);
		AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 2), "rpTranspose");
		AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 1), "rpBreaker");
		AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 0), "rpDeploy");
		AchieveLib.registerAchievement("0x1CA26", "rpFrames", 4, 4, new ItemStack(blockMachine, 1, 7), "rpIngotBlue");
		AchieveLib.registerAchievement("0x1CA27", "rpPump", 4, 5, new ItemStack(blockMachinePanel, 1, 1), "rpIngotBlue");
		AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 7), "rpFrames");
		AchieveLib.addCraftingAchievement(new ItemStack(blockMachinePanel, 1, 1), "rpPump");
	}
	
	public static int blockDamageDropped(Block bl, int md) {
		return bl.damageDropped(md);
	}
}
