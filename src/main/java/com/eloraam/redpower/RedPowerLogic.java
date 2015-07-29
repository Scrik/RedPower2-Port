package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.logic.BlockLogic;
import com.eloraam.redpower.logic.ItemLogic;
import com.eloraam.redpower.logic.LogicProxy;
import com.eloraam.redpower.logic.TileLogicAdv;
import com.eloraam.redpower.logic.TileLogicArray;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.logic.TileLogicSimple;
import com.eloraam.redpower.logic.TileLogicStorage;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

@Mod(
   modid = "RedPowerLogic",
   name = "RedPower Logic",
   version = "2.0pr6",
   certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b",
   dependencies = "required-after:RedPowerBase"
)
public class RedPowerLogic {

   @Instance("RedPowerLogic")
   public static RedPowerLogic instance;
   @SidedProxy(
      clientSide = "com.eloraam.redpower.logic.LogicProxyClient",
      serverSide = "com.eloraam.redpower.logic.LogicProxy"
   )
   public static LogicProxy proxy;
   public static BlockLogic blockLogic;
   public static ItemParts itemParts;
   public static ItemStack itemAnode;
   public static ItemStack itemCathode;
   public static ItemStack itemWire;
   public static ItemStack itemWafer;
   public static ItemStack itemPointer;
   public static ItemStack itemPlate;
   public static ItemStack itemWaferRedwire;
   public static ItemStack itemChip;
   public static ItemStack itemTaintedChip;
   public static ItemStack itemWaferBundle;
   public static boolean EnableSounds;


   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {}

   @EventHandler
   public void load(FMLInitializationEvent event) {
      EnableSounds = Config.getInt("settings.logic.enableSounds") > 0;
      setupLogic();
      proxy.registerRenderers();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {}

   private static void setupLogic() {
      GameRegistry.registerTileEntity(TileLogicSimple.class, "RPLgSmp");
      GameRegistry.registerTileEntity(TileLogicArray.class, "RPLgAr");
      GameRegistry.registerTileEntity(TileLogicStorage.class, "RPLgStor");
      GameRegistry.registerTileEntity(TileLogicAdv.class, "RPLgAdv");
      GameRegistry.registerTileEntity(TileLogicPointer.class, "RPLgPtr");
      itemParts = new ItemParts();
      itemParts.addItem(0, "rplogic:itemWafer", "item.irwafer");
      itemParts.addItem(1, "rplogic:itemWire", "item.irwire");
      itemParts.addItem(2, "rplogic:itemAnode", "item.iranode");
      itemParts.addItem(3, "rplogic:itemCatode", "item.ircathode");
      itemParts.addItem(4, "rplogic:itemPointer", "item.irpointer");
      itemParts.addItem(5, "rplogic:itemRedWire", "item.irredwire");
      itemParts.addItem(6, "rplogic:itemPlate", "item.irplate");
      itemParts.addItem(7, "rplogic:itemChip", "item.irchip");
      itemParts.addItem(8, "rplogic:itemChip", "item.irtchip");
      itemParts.addItem(9, "rplogic:itemBundle", "item.irbundle");
      itemWafer = new ItemStack(itemParts, 1, 0);
      itemWire = new ItemStack(itemParts, 1, 1);
      itemAnode = new ItemStack(itemParts, 1, 2);
      itemCathode = new ItemStack(itemParts, 1, 3);
      itemPointer = new ItemStack(itemParts, 1, 4);
      itemWaferRedwire = new ItemStack(itemParts, 1, 5);
      itemPlate = new ItemStack(itemParts, 1, 6);
      itemChip = new ItemStack(itemParts, 1, 7);
      itemTaintedChip = new ItemStack(itemParts, 1, 8);
      itemWaferBundle = new ItemStack(itemParts, 1, 9);
      FurnaceRecipes.smelting().func_151393_a(Blocks.stone, new ItemStack(itemParts, 2, 0), 0.1F);
      GameRegistry.addRecipe(itemWire, new Object[]{"R", "B", Character.valueOf('B'), itemWafer, Character.valueOf('R'), Items.redstone});
      GameRegistry.addRecipe(new ItemStack(itemParts, 3, 2), new Object[]{" R ", "RRR", "BBB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), Items.redstone});
      GameRegistry.addRecipe(itemCathode, new Object[]{"T", "B", Character.valueOf('B'), itemWafer, Character.valueOf('T'), Blocks.redstone_torch});
      GameRegistry.addRecipe(itemPointer, new Object[]{"S", "T", "B", Character.valueOf('B'), itemWafer, Character.valueOf('S'), Blocks.stone, Character.valueOf('T'), Blocks.redstone_torch});
      GameRegistry.addRecipe(itemWaferRedwire, new Object[]{"W", "B", Character.valueOf('B'), itemWafer, Character.valueOf('W'), new ItemStack(RedPowerBase.blockMicro, 1, 256)});
      GameRegistry.addRecipe(itemPlate, new Object[]{" B ", "SRS", "BCB", Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('R'), RedPowerBase.itemIngotRed, Character.valueOf('S'), Items.stick});
      GameRegistry.addRecipe(CoreLib.copyStack(itemChip, 3), new Object[]{" R ", "BBB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), RedPowerBase.itemWaferRed});
      GameRegistry.addShapelessRecipe(CoreLib.copyStack(itemTaintedChip, 1), new Object[]{itemChip, Items.glowstone_dust});
      GameRegistry.addRecipe(itemWaferBundle, new Object[]{"W", "B", Character.valueOf('B'), itemWafer, Character.valueOf('W'), new ItemStack(RedPowerBase.blockMicro, 1, 768)});
      blockLogic = new BlockLogic();
      GameRegistry.registerBlock(blockLogic, ItemLogic.class, "logic");
      blockLogic.addTileEntityMapping(0, TileLogicPointer.class);
      blockLogic.addTileEntityMapping(1, TileLogicSimple.class);
      blockLogic.addTileEntityMapping(2, TileLogicArray.class);
      blockLogic.addTileEntityMapping(3, TileLogicStorage.class);
      blockLogic.addTileEntityMapping(4, TileLogicAdv.class);
      blockLogic.setBlockName(0, "irtimer");
      blockLogic.setBlockName(1, "irseq");
      blockLogic.setBlockName(2, "irstate");
      blockLogic.setBlockName(256, "irlatch");
      blockLogic.setBlockName(257, "irnor");
      blockLogic.setBlockName(258, "iror");
      blockLogic.setBlockName(259, "irnand");
      blockLogic.setBlockName(260, "irand");
      blockLogic.setBlockName(261, "irxnor");
      blockLogic.setBlockName(262, "irxor");
      blockLogic.setBlockName(263, "irpulse");
      blockLogic.setBlockName(264, "irtoggle");
      blockLogic.setBlockName(265, "irnot");
      blockLogic.setBlockName(266, "irbuf");
      blockLogic.setBlockName(267, "irmux");
      blockLogic.setBlockName(268, "irrepeater");
      blockLogic.setBlockName(269, "irsync");
      blockLogic.setBlockName(270, "irrand");
      blockLogic.setBlockName(271, "irdlatch");
      blockLogic.setBlockName(272, "rplightsensor");
      blockLogic.setBlockName(512, "rpanc");
      blockLogic.setBlockName(513, "rpainv");
      blockLogic.setBlockName(514, "rpaninv");
      blockLogic.setBlockName(768, "ircounter");
      blockLogic.setBlockName(1024, "irbusxcvr");
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 0), new Object[]{"BWB", "WPW", "ACA", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 1), new Object[]{"BCB", "CPC", "BCB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 2), new Object[]{"BAC", "WSP", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode, Character.valueOf('P'), itemPointer, Character.valueOf('S'), itemChip});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 256), new Object[]{"WWA", "CBC", "AWW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 257), new Object[]{"BAB", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 258), new Object[]{"BCB", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 259), new Object[]{"AAA", "CCC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 260), new Object[]{"ACA", "CCC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 261), new Object[]{"ACA", "CAC", "WCW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 262), new Object[]{"AWA", "CAC", "WCW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 263), new Object[]{"ACA", "CAC", "WWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 264), new Object[]{"BCB", "WLW", "BCB", Character.valueOf('L'), Blocks.lever, Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 265), new Object[]{"BAB", "ACA", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 266), new Object[]{"ACA", "WCW", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 267), new Object[]{"ACA", "CBC", "ACW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 268), new Object[]{"BCW", "BAW", "BWC", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('A'), itemAnode, Character.valueOf('C'), itemCathode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 269), new Object[]{"WCW", "SAS", "WWW", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('A'), itemAnode, Character.valueOf('C'), itemCathode, Character.valueOf('S'), itemChip});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 270), new Object[]{"BSB", "WWW", "SWS", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('S'), itemTaintedChip});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 271), new Object[]{"ACW", "CCC", "CWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('A'), itemAnode});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 272), new Object[]{"BWB", "BSB", "BBB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('S'), RedPowerBase.itemWaferBlue});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 768), new Object[]{"BWB", "CPC", "BWB", Character.valueOf('W'), itemWire, Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('P'), itemPointer});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 512), new Object[]{"BRB", "RRR", "BRB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), itemWaferRedwire});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 513), new Object[]{"BRB", "RPR", "BRB", Character.valueOf('B'), itemWafer, Character.valueOf('R'), itemWaferRedwire, Character.valueOf('P'), itemPlate});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 514), new Object[]{"BRB", "RPR", "BRC", Character.valueOf('B'), itemWafer, Character.valueOf('C'), itemCathode, Character.valueOf('R'), itemWaferRedwire, Character.valueOf('P'), itemPlate});
      GameRegistry.addRecipe(new ItemStack(blockLogic, 1, 1024), new Object[]{"CCC", "WBW", "CCC", Character.valueOf('B'), itemWafer, Character.valueOf('W'), RedPowerBase.itemWaferRed, Character.valueOf('C'), itemWaferBundle});
   }
}
