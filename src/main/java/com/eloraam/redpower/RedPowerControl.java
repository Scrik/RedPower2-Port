package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.control.BlockPeripheral;
import com.eloraam.redpower.control.ControlProxy;
import com.eloraam.redpower.control.ItemBackplane;
import com.eloraam.redpower.control.ItemDisk;
import com.eloraam.redpower.control.MicroPlacementRibbon;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.control.TileDiskDrive;
import com.eloraam.redpower.control.TileDisplay;
import com.eloraam.redpower.control.TileIOExpander;
import com.eloraam.redpower.control.TileRAM;
import com.eloraam.redpower.control.TileRibbon;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.ItemExtended;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

@Mod(
	modid = "RedPowerControl",
	name = "RedPower Control",
	version = "2.0pr6",
	certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b",
	dependencies = "required-after:RedPowerBase"
)
public class RedPowerControl {

	@Instance("RedPowerControl")
	public static RedPowerControl instance;
	@SidedProxy(
		clientSide = "com.eloraam.redpower.control.ControlProxyClient",
		serverSide = "com.eloraam.redpower.control.ControlProxy"
	)
	public static ControlProxy proxy;
	public static BlockExtended blockBackplane;
	public static BlockExtended blockPeripheral;
	public static BlockExtended blockFlatPeripheral;
	public static ItemDisk itemDisk;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		setupBlocks();
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	private static void setupBlocks() {
		blockBackplane = new BlockMultipart(CoreLib.materialRedpower);
		GameRegistry.registerBlock(blockBackplane, ItemBackplane.class, "backplane");
		blockBackplane.setHardness(1.0F);
		blockBackplane.setBlockName(0, "rpbackplane");
		blockBackplane.setBlockName(1, "rpram");
		blockPeripheral = new BlockPeripheral();
		GameRegistry.registerBlock(blockPeripheral, ItemExtended.class, "peripheral");
		blockPeripheral.setHardness(1.0F);
		blockPeripheral.setBlockName(0, "rpdisplay");
		blockPeripheral.setBlockName(1, "rpcpu");
		blockPeripheral.setBlockName(2, "rpdiskdrive");
		blockFlatPeripheral = new BlockMultipart(Material.rock);
		blockFlatPeripheral.setCreativeTab(CreativeExtraTabs.tabMachine);
		GameRegistry.registerBlock(blockFlatPeripheral, ItemExtended.class, "peripheralFlat");
		blockFlatPeripheral.setHardness(1.0F);
		blockFlatPeripheral.setBlockName(0, "rpioexp");
		GameRegistry.registerTileEntity(TileBackplane.class, "RPConBP");
		blockBackplane.addTileEntityMapping(0, TileBackplane.class);
		GameRegistry.registerTileEntity(TileRAM.class, "RPConRAM");
		blockBackplane.addTileEntityMapping(1, TileRAM.class);
		GameRegistry.registerTileEntity(TileDisplay.class, "RPConDisp");
		blockPeripheral.addTileEntityMapping(0, TileDisplay.class);
		GameRegistry.registerTileEntity(TileDiskDrive.class, "RPConDDrv");
		blockPeripheral.addTileEntityMapping(2, TileDiskDrive.class);
		GameRegistry.registerTileEntity(TileCPU.class, "RPConCPU");
		blockPeripheral.addTileEntityMapping(1, TileCPU.class);
		GameRegistry.registerTileEntity(TileIOExpander.class, "RPConIOX");
		blockFlatPeripheral.addTileEntityMapping(0, TileIOExpander.class);
		GameRegistry.registerTileEntity(TileRibbon.class, "RPConRibbon");
		RedPowerBase.blockMicro.addTileEntityMapping(12, TileRibbon.class);
		MicroPlacementRibbon imp = new MicroPlacementRibbon();
		RedPowerBase.blockMicro.registerPlacement(12, imp);
		itemDisk = new ItemDisk();
		CraftLib.addOreRecipe(new ItemStack(itemDisk, 1), new Object[]{"WWW", "W W", "WIW", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('W'), "plankWood"});
		GameRegistry.addShapelessRecipe(new ItemStack(itemDisk, 1, 1), new Object[]{new ItemStack(itemDisk, 1, 0), Items.redstone});
		GameRegistry.registerItem(itemDisk, "diskette");
		GameRegistry.addShapelessRecipe(new ItemStack(itemDisk, 1, 2), new Object[]{new ItemStack(itemDisk, 1, 1), Items.redstone});
		GameRegistry.addRecipe(new ItemStack(blockBackplane, 1, 0), new Object[]{"ICI", "IGI", "ICI", Character.valueOf('C'), RedPowerBase.itemFineCopper, Character.valueOf('I'), Blocks.iron_bars, Character.valueOf('G'), Items.gold_ingot});
		GameRegistry.addRecipe(new ItemStack(blockBackplane, 1, 1), new Object[]{"IRI", "RDR", "IRI", Character.valueOf('I'), Blocks.iron_bars, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('D'), Items.diamond});
		CraftLib.addOreRecipe(new ItemStack(blockPeripheral, 1, 0), new Object[]{"GWW", "GPR", "GBW", Character.valueOf('P'), new ItemStack(RedPowerBase.itemLumar, 1, 5), Character.valueOf('G'), Blocks.glass, Character.valueOf('W'), "plankWood", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack(RedPowerBase.blockMicro, 1, 3072)});
		CraftLib.addOreRecipe(new ItemStack(blockPeripheral, 1, 1), new Object[]{"WWW", "RDR", "WBW", Character.valueOf('W'), "plankWood", Character.valueOf('D'), Blocks.diamond_block, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack(RedPowerBase.blockMicro, 1, 3072)});
		CraftLib.addOreRecipe(new ItemStack(blockPeripheral, 1, 2), new Object[]{"WWW", "WMR", "WBW", Character.valueOf('G'), Blocks.glass, Character.valueOf('W'), "plankWood", Character.valueOf('M'), RedPowerBase.itemMotor, Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('B'), new ItemStack(RedPowerBase.blockMicro, 1, 3072)});
		CraftLib.addOreRecipe(new ItemStack(blockFlatPeripheral, 1, 0), new Object[]{"WCW", "WRW", "WBW", Character.valueOf('W'), "plankWood", Character.valueOf('R'), RedPowerBase.itemWaferRed, Character.valueOf('C'), new ItemStack(RedPowerBase.blockMicro, 1, 768), Character.valueOf('B'), new ItemStack(RedPowerBase.blockMicro, 1, 3072)});
		GameRegistry.addRecipe(new ItemStack(RedPowerBase.blockMicro, 8, 3072), new Object[]{"C", "C", "C", Character.valueOf('C'), RedPowerBase.itemFineCopper});
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemDisk, 1, 1), 0, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemDisk, 1, 2), 0, 1, 1));
	}
}
