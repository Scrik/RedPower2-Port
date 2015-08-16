package com.eloraam.redpower;

import com.eloraam.redpower.base.BaseProxy;
import com.eloraam.redpower.base.BlockAppliance;
import com.eloraam.redpower.base.BlockMicro;
import com.eloraam.redpower.base.ItemBag;
import com.eloraam.redpower.base.ItemDrawplate;
import com.eloraam.redpower.base.ItemDyeIndigo;
import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.base.ItemMicro;
import com.eloraam.redpower.base.ItemPlan;
import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.base.RecipeBag;
import com.eloraam.redpower.base.TileAdvBench;
import com.eloraam.redpower.base.TileAlloyFurnace;
import com.eloraam.redpower.core.AchieveLib;
import com.eloraam.redpower.core.BlockMultiblock;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.OreStack;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.TileMultiblock;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "RedPowerBase", name = "RedPower Base", version = "2.0pr6", certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b", dependencies = "required-after:RedPowerCore")
public class RedPowerBase {
	
	@Instance("RedPowerBase")
	public static RedPowerBase instance;
	@SidedProxy(clientSide = "com.eloraam.redpower.base.BaseProxyClient", serverSide = "com.eloraam.redpower.base.BaseProxy")
	public static BaseProxy proxy;
	public static BlockAppliance blockAppliance;
	public static Item itemHandsawIron;
	public static Item itemHandsawDiamond;
	public static ItemParts itemLumar;
	public static ItemParts itemResource;
	public static ItemStack itemRuby;
	public static ItemStack itemGreenSapphire;
	public static ItemStack itemSapphire;
	public static ItemStack itemIngotSilver;
	public static ItemStack itemIngotTin;
	public static ItemStack itemIngotCopper;
	public static ItemStack itemNikolite;
	public static ItemParts itemAlloy;
	public static ItemStack itemIngotRed;
	public static ItemStack itemIngotBlue;
	public static ItemStack itemIngotBrass;
	public static ItemStack itemBouleSilicon;
	public static ItemStack itemWaferSilicon;
	public static ItemStack itemWaferBlue;
	public static ItemStack itemWaferRed;
	public static ItemStack itemTinplate;
	public static ItemStack itemFineCopper;
	public static ItemStack itemFineIron;
	public static ItemStack itemCopperCoil;
	public static ItemStack itemMotor;
	public static ItemStack itemCanvas;
	public static ItemParts itemNugget;
	public static ItemStack itemNuggetIron;
	public static ItemStack itemNuggetSilver;
	public static ItemStack itemNuggetTin;
	public static ItemStack itemNuggetCopper;
	public static Item itemDyeIndigo;
	public static BlockMicro blockMicro;
	public static BlockMultiblock blockMultiblock;
	public static ItemScrewdriver itemScrewdriver;
	public static Item itemDrawplateDiamond;
	public static Item itemPlanBlank;
	public static Item itemPlanFull;
	public static Item itemBag;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void load(FMLInitializationEvent event) {
		initBaseItems();
		initAlloys();
		initIndigo();
		initMicroblocks();
		initCoverMaterials();
		initBlocks();
		initAchievements();
		CraftingManager.getInstance().getRecipeList().add(new RecipeBag());
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public static void initBaseItems() {
		itemLumar = new ItemParts();
		itemLumar.setCreativeTab(CreativeTabs.tabMaterials);
		
		int i;
		for (i = 0; i < 16; ++i) {
			itemLumar.addItem(i, "rpbase:itemLumar" + i, "item.rplumar." + CoreLib.rawColorNames[i]);
			Config.addName("item.rplumar." + CoreLib.rawColorNames[i] + ".name", CoreLib.enColorNames[i] + " Lumar");
			ItemStack dye = new ItemStack(Items.dye, 1, 15 - i);
			GameRegistry.addShapelessRecipe(new ItemStack(itemLumar, 2, i), new Object[] { Items.redstone, dye, dye, Items.glowstone_dust });
		}
		
		itemResource = new ItemParts();
		itemAlloy = new ItemParts();
		itemResource.setCreativeTab(CreativeTabs.tabMaterials);
		itemAlloy.setCreativeTab(CreativeTabs.tabMaterials);
		itemResource.addItem(0, "rpbase:itemRuby", "item.ruby");
		itemResource.addItem(1, "rpbase:itemGreenSapphire", "item.greenSapphire");
		itemResource.addItem(2, "rpbase:itemSapphire", "item.sapphire");
		itemResource.addItem(3, "rpbase:itemSilverIngot", "item.ingotSilver");
		itemResource.addItem(4, "rpbase:itemTinIngot", "item.ingotTin");
		itemResource.addItem(5, "rpbase:itemCopperIngot", "item.ingotCopper");
		itemResource.addItem(6, "rpbase:itemNikolite", "item.nikolite");
		itemAlloy.addItem(0, "rpbase:itemIngotRed", "item.ingotRed");
		itemAlloy.addItem(1, "rpbase:itemIngotBlue", "item.ingotBlue");
		itemAlloy.addItem(2, "rpbase:itemIngotBrass", "item.ingotBrass");
		itemAlloy.addItem(3, "rpbase:itemBouleSilicon", "item.bouleSilicon");
		itemAlloy.addItem(4, "rpbase:itemWaferSilicon", "item.waferSilicon");
		itemAlloy.addItem(5, "rpbase:itemWaferBlue", "item.waferBlue");
		itemAlloy.addItem(6, "rpbase:itemWaferRed", "item.waferRed");
		itemAlloy.addItem(7, "rpbase:itemTinPlate", "item.tinplate");
		itemAlloy.addItem(8, "rpbase:itemFineCopper", "item.finecopper");
		itemAlloy.addItem(9, "rpbase:itemFineIron", "item.fineiron");
		itemAlloy.addItem(10, "rpbase:itemCopperCoil", "item.coppercoil");
		itemAlloy.addItem(11, "rpbase:itemBTMotor", "item.btmotor");
		itemAlloy.addItem(12, "rpbase:itemCanvas", "item.rpcanvas");
		itemRuby = new ItemStack(itemResource, 1, 0);
		itemGreenSapphire = new ItemStack(itemResource, 1, 1);
		itemSapphire = new ItemStack(itemResource, 1, 2);
		itemIngotSilver = new ItemStack(itemResource, 1, 3);
		itemIngotTin = new ItemStack(itemResource, 1, 4);
		itemIngotCopper = new ItemStack(itemResource, 1, 5);
		itemNikolite = new ItemStack(itemResource, 1, 6);
		itemIngotRed = new ItemStack(itemAlloy, 1, 0);
		itemIngotBlue = new ItemStack(itemAlloy, 1, 1);
		itemIngotBrass = new ItemStack(itemAlloy, 1, 2);
		itemBouleSilicon = new ItemStack(itemAlloy, 1, 3);
		itemWaferSilicon = new ItemStack(itemAlloy, 1, 4);
		itemWaferBlue = new ItemStack(itemAlloy, 1, 5);
		itemWaferRed = new ItemStack(itemAlloy, 1, 6);
		itemTinplate = new ItemStack(itemAlloy, 1, 7);
		itemFineCopper = new ItemStack(itemAlloy, 1, 8);
		itemFineIron = new ItemStack(itemAlloy, 1, 9);
		itemCopperCoil = new ItemStack(itemAlloy, 1, 10);
		itemMotor = new ItemStack(itemAlloy, 1, 11);
		itemCanvas = new ItemStack(itemAlloy, 1, 12);
		OreDictionary.registerOre("gemRuby", itemRuby);
		OreDictionary.registerOre("gemGreenSapphire", itemGreenSapphire);
		OreDictionary.registerOre("gemSapphire", itemSapphire);
		OreDictionary.registerOre("ingotTin", itemIngotTin);
		OreDictionary.registerOre("ingotCopper", itemIngotCopper);
		OreDictionary.registerOre("ingotSilver", itemIngotSilver);
		OreDictionary.registerOre("ingotBrass", itemIngotBrass);
		OreDictionary.registerOre("dustNikolite", itemNikolite);
		itemNugget = new ItemParts();
		itemNugget.setCreativeTab(CreativeTabs.tabMaterials);
		itemNugget.addItem(0, "rpbase:itemNuggetIron", "item.nuggetIron");
		itemNugget.addItem(1, "rpbase:itemNuggetSilver", "item.nuggetSilver");
		itemNugget.addItem(2, "rpbase:itemNuggetTin", "item.nuggetTin");
		itemNugget.addItem(3, "rpbase:itemNuggetCopper", "item.nuggetCopper");
		itemNuggetIron = new ItemStack(itemNugget, 1, 0);
		itemNuggetSilver = new ItemStack(itemNugget, 1, 1);
		itemNuggetTin = new ItemStack(itemNugget, 1, 2);
		itemNuggetCopper = new ItemStack(itemNugget, 1, 3);
		OreDictionary.registerOre("nuggetIron", itemNuggetIron);
		OreDictionary.registerOre("nuggetSilver", itemNuggetSilver);
		OreDictionary.registerOre("nuggetTin", itemNuggetTin);
		OreDictionary.registerOre("nuggetCopper", itemNuggetCopper);
		itemDrawplateDiamond = new ItemDrawplate();
		itemDrawplateDiamond.setUnlocalizedName("drawplateDiamond").setMaxDamage(255).setTextureName("rpbase:itemDiamondDrawplate");
		GameRegistry.registerItem(itemDrawplateDiamond, "drawplateDiamond");
		itemBag = new ItemBag();
		GameRegistry.addRecipe(new ItemStack(itemBag, 1, 0), new Object[] { "CCC", "C C", "CCC", Character.valueOf('C'), itemCanvas });
		
		for (i = 1; i < 16; ++i) {
			GameRegistry.addRecipe(new ItemStack(itemBag, 1, i), new Object[] { "CCC", "CDC", "CCC", Character.valueOf('C'), itemCanvas, Character.valueOf('D'), new ItemStack(Items.dye, 1, 15 - i) });
		}
		GameRegistry.registerItem(itemLumar, "lumar");
		GameRegistry.registerItem(itemResource, "resource");
		GameRegistry.registerItem(itemAlloy, "alloy");
		GameRegistry.registerItem(itemNugget, "nugget");
		GameRegistry.registerItem(itemBag, "canvasBag");
	}
	
	public static void initIndigo() {
		itemDyeIndigo = new ItemDyeIndigo();
		OreDictionary.registerOre("dyeBlue", new ItemStack(itemDyeIndigo));
		GameRegistry.registerItem(itemDyeIndigo, "dyeIndigo");
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.wool, 1, 11), new Object[] { itemDyeIndigo, Blocks.wool });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 12), new Object[] { itemDyeIndigo, new ItemStack(Items.dye,1, 15) });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 6), new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 2) });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 5), new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 1) });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 3, 13), new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 9) });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 13), new Object[] { itemDyeIndigo, new ItemStack(Items.dye,1, 1), new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 15) });
		CraftLib.addShapelessOreRecipe(new ItemStack(itemLumar, 2, 11), new Object[] { Items.redstone, "dyeBlue", "dyeBlue", Items.glowstone_dust });
		CraftLib.addOreRecipe(new ItemStack(itemBag, 1, 11), new Object[] { "CCC", "CDC", "CCC", Character.valueOf('C'), itemCanvas, Character.valueOf('D'), "dyeBlue" });
		itemPlanBlank = (new Item()).setTextureName("rpbase:itemPlanBlank");
		itemPlanBlank.setUnlocalizedName("planBlank");
		itemPlanBlank.setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.addShapelessRecipe(new ItemStack(itemPlanBlank), new Object[] { Items.paper, itemDyeIndigo });
		GameRegistry.registerItem(itemPlanBlank, "planBlank");
		itemPlanFull = new ItemPlan();
		GameRegistry.registerItem(itemPlanFull, "planFull");
	}
	
	public static void initAlloys() {
		CraftLib.addAlloyResult(itemIngotRed, new Object[] { new ItemStack(Items.redstone, 4), new ItemStack(Items.iron_ingot, 1) });
		CraftLib.addAlloyResult(itemIngotRed, new Object[] { new ItemStack(Items.redstone, 4), new OreStack("ingotCopper") });
		CraftLib.addAlloyResult(CoreLib.copyStack(itemIngotBrass, 4), new Object[] { new OreStack("ingotTin"), new OreStack("ingotCopper", 3) });
		CraftLib.addAlloyResult(CoreLib.copyStack(itemTinplate, 4), new Object[] { new OreStack("ingotTin"), new ItemStack(Items.iron_ingot, 2) });
		CraftLib.addAlloyResult(itemIngotBlue, new Object[] { new OreStack("ingotSilver"), new OreStack("dustNikolite", 4) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 3), new Object[] { new ItemStack(Blocks.rail, 8) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 3), new Object[] { new ItemStack(Items.bucket, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 5), new Object[] { new ItemStack(Items.minecart, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 6), new Object[] { new ItemStack(Items.iron_door, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 3), new Object[] { new ItemStack(Blocks.iron_bars, 8) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 31), new Object[] { new ItemStack(Blocks.anvil, 1, 0) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 31), new Object[] { new ItemStack(Blocks.anvil, 1, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 31), new Object[] { new ItemStack(Blocks.anvil, 1, 2) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 2), new Object[] { new ItemStack(Items.iron_sword, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 3), new Object[] { new ItemStack(Items.iron_pickaxe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 3), new Object[] { new ItemStack(Items.iron_axe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 1), new Object[] { new ItemStack(Items.iron_shovel, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 2), new Object[] { new ItemStack(Items.iron_hoe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 2), new Object[] { new ItemStack(Items.golden_sword, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 3), new Object[] { new ItemStack(Items.golden_pickaxe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 3), new Object[] { new ItemStack(Items.golden_axe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 1), new Object[] { new ItemStack(Items.golden_shovel, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 2), new Object[] { new ItemStack(Items.golden_hoe, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 5), new Object[] { new ItemStack(Items.iron_helmet, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 8), new Object[] { new ItemStack(Items.iron_chestplate, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 7), new Object[] { new ItemStack(Items.iron_leggings, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 4), new Object[] { new ItemStack(Items.iron_boots, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 5), new Object[] { new ItemStack(Items.golden_helmet, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 8), new Object[] { new ItemStack(Items.golden_chestplate, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 7), new Object[] { new ItemStack(Items.golden_leggings, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 4), new Object[] { new ItemStack(Items.golden_boots, 1) });
		CraftLib.addAlloyResult(new ItemStack(Items.gold_ingot, 1), new Object[] { new ItemStack(Items.gold_nugget, 9) });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 1), new Object[] { CoreLib.copyStack(itemNuggetIron, 9) });
		CraftLib.addAlloyResult(itemIngotSilver, new Object[] { CoreLib.copyStack(itemNuggetSilver, 9) });
		CraftLib.addAlloyResult(itemIngotCopper, new Object[] { CoreLib.copyStack(itemNuggetCopper, 9) });
		CraftLib.addAlloyResult(itemIngotTin, new Object[] { CoreLib.copyStack(itemNuggetTin, 9) });
		CraftLib.addAlloyResult(itemIngotCopper, new Object[] { itemFineCopper });
		CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 1), new Object[] { itemFineIron });
		CraftLib.addAlloyResult(itemBouleSilicon, new Object[] { new ItemStack(Items.coal, 8, 0), new ItemStack(Blocks.sand, 8) });
		CraftLib.addAlloyResult(itemBouleSilicon, new Object[] { new ItemStack(Items.coal, 8, 1), new ItemStack(Blocks.sand, 8) });
		CraftLib.addAlloyResult(itemWaferBlue, new Object[] { CoreLib.copyStack(itemWaferSilicon, 1), new OreStack("dustNikolite", 4) });
		CraftLib.addAlloyResult(itemWaferRed, new Object[] { CoreLib.copyStack(itemWaferSilicon, 1), new ItemStack(Items.redstone, 4) });
	}
	
	public static void initMicroblocks() {
		blockMicro = new BlockMicro();
		blockMicro.setBlockName("rpwire");
		GameRegistry.registerBlock(blockMicro, ItemMicro.class, "microblock");
		blockMicro.addTileEntityMapping(0, TileCovered.class);
		CoverLib.blockCoverPlate = blockMicro;
	}
	
	public static void initCoverMaterials() {
		CoverLib.addMaterial(0, 1, Blocks.cobblestone, "cobble", "Cobblestone");
		CoverLib.addMaterial(1, 1, Blocks.stone, "stone", "Stone");
		CoverLib.addMaterial(2, 0, Blocks.planks, "planks", "Wooden Plank");
		CoverLib.addMaterial(3, 1, Blocks.sandstone, "sandstone", "Sandstone");
		CoverLib.addMaterial(4, 1, Blocks.mossy_cobblestone, "moss", "Moss Stone");
		CoverLib.addMaterial(5, 1, Blocks.brick_block, "brick", "Brick");
		CoverLib.addMaterial(6, 2, Blocks.obsidian, "obsidian", "Obsidian");
		CoverLib.addMaterial(7, 1, true, Blocks.glass, "glass", "Glass");
		CoverLib.addMaterial(8, 0, Blocks.dirt, "dirt", "Dirt");
		CoverLib.addMaterial(9, 0, Blocks.clay, "clay", "Clay");
		CoverLib.addMaterial(10, 0, Blocks.bookshelf, "books", "Bookshelf");
		CoverLib.addMaterial(11, 0, Blocks.netherrack, "netherrack", "Netherrack");
		CoverLib.addMaterial(12, 0, Blocks.log, 0, "wood", "Oak Wood");
		CoverLib.addMaterial(13, 0, Blocks.log, 1, "wood1", "Spruce Wood");
		CoverLib.addMaterial(14, 0, Blocks.log, 2, "wood2", "Birch Wood");
		CoverLib.addMaterial(15, 0, Blocks.soul_sand, "soul", "Soul Sand");
		CoverLib.addMaterial(16, 1, Blocks.stone_slab, "slab", "Polished Stone");
		CoverLib.addMaterial(17, 1, Blocks.iron_block, "iron", "Iron");
		CoverLib.addMaterial(18, 1, Blocks.gold_block, "gold", "Gold");
		CoverLib.addMaterial(19, 2, Blocks.diamond_block, "diamond", "Diamond");
		CoverLib.addMaterial(20, 1, Blocks.lapis_block, "lapis", "Lapis Lazuli");
		CoverLib.addMaterial(21, 0, Blocks.snow, "snow", "Snow");
		CoverLib.addMaterial(22, 0, Blocks.pumpkin, "pumpkin", "Pumpkin");
		CoverLib.addMaterial(23, 1, Blocks.stonebrick, 0, "stonebrick", "Stone Brick");
		CoverLib.addMaterial(24, 1, Blocks.stonebrick, 1, "stonebrick1", "Stone Brick");
		CoverLib.addMaterial(25, 1, Blocks.stonebrick, 2, "stonebrick2", "Stone Brick");
		CoverLib.addMaterial(26, 1, Blocks.nether_brick, "netherbrick", "Nether Brick");
		CoverLib.addMaterial(27, 1, Blocks.stonebrick, 3, "stonebrick3", "Stone Brick");
		CoverLib.addMaterial(28, 0, Blocks.planks, 1, "planks1", "Wooden Plank");
		CoverLib.addMaterial(29, 0, Blocks.planks, 2, "planks2", "Wooden Plank");
		CoverLib.addMaterial(30, 0, Blocks.planks, 3, "planks3", "Wooden Plank");
		CoverLib.addMaterial(31, 1, Blocks.sandstone, 1, "sandstone1", "Sandstone");
		CoverLib.addMaterial(64, 1, Blocks.sandstone, 2, "sandstone2", "Sandstone");
		CoverLib.addMaterial(65, 0, Blocks.log, 3, "wood3", "Jungle Wood");
		
		for (int i = 0; i < 16; ++i) {
			CoverLib.addMaterial(32 + i, 0, Blocks.wool, i, "wool." + CoreLib.rawColorNames[i], CoreLib.enColorNames[i] + " Wool");
		}
	}
	
	public static void initAchievements() {
		AchieveLib.registerAchievement("117027", "rpMakeAlloy", 0, 0, new ItemStack(blockAppliance, 1, 0), AchievementList.buildFurnace);
		AchieveLib.registerAchievement("117028", "rpMakeSaw", 4, 0, new ItemStack(itemHandsawDiamond), AchievementList.diamonds);
		AchieveLib.registerAchievement("117029", "rpIngotRed", 2, 2, itemIngotRed, "rpMakeAlloy");
		AchieveLib.registerAchievement("117030", "rpIngotBlue", 2, 4, itemIngotBlue, "rpMakeAlloy");
		AchieveLib.registerAchievement("117031", "rpIngotBrass", 2, 6, itemIngotBrass, "rpMakeAlloy");
		AchieveLib.registerAchievement("117032", "rpAdvBench", -2, 0, new ItemStack(blockAppliance, 1, 3), AchievementList.buildWorkBench);
		AchieveLib.addCraftingAchievement(new ItemStack(blockAppliance, 1, 0),"rpMakeAlloy");
		AchieveLib.addCraftingAchievement(new ItemStack(blockAppliance, 1, 3), "rpAdvBench");
		AchieveLib.addCraftingAchievement(new ItemStack(itemHandsawDiamond), "rpMakeSaw");
		AchieveLib.addAlloyAchievement(itemIngotRed, "rpIngotRed");
		AchieveLib.addAlloyAchievement(itemIngotBlue, "rpIngotBlue");
		AchieveLib.addAlloyAchievement(itemIngotBrass, "rpIngotBrass");
		AchievementPage.registerAchievementPage(AchieveLib.achievepage);
	}
	
	public static void initBlocks() {
		blockMultiblock = new BlockMultiblock();
		GameRegistry.registerBlock(blockMultiblock, "multiblock");
		GameRegistry.registerTileEntity(TileMultiblock.class, "RPMulti");
		blockAppliance = new BlockAppliance();
		GameRegistry.registerBlock(blockAppliance, ItemExtended.class, "appliance");
		GameRegistry.registerTileEntity(TileAlloyFurnace.class, "RPAFurnace");
		blockAppliance.addTileEntityMapping(0, TileAlloyFurnace.class);
		blockAppliance.setBlockName(0, "rpafurnace");
		GameRegistry
				.addRecipe(
						new ItemStack(blockAppliance, 1, 0),
						new Object[] { "BBB", "B B", "BBB", Character
								.valueOf('B'), Blocks.brick_block });
		GameRegistry.registerTileEntity(TileAdvBench.class, "RPAdvBench");
		blockAppliance.addTileEntityMapping(3, TileAdvBench.class);
		blockAppliance.setBlockName(3, "rpabench");
		CraftLib.addOreRecipe(
				new ItemStack(blockAppliance, 1, 3),
				new Object[] { "SSS", "WTW", "WCW", Character.valueOf('S'), Blocks.stone, Character
						.valueOf('W'), "plankWood", Character.valueOf('T'), Blocks.crafting_table, Character
						.valueOf('C'), Blocks.chest });
		itemHandsawIron = new ItemHandsaw(0);
		itemHandsawIron.setUnlocalizedName("handsawIron");
		itemHandsawIron.setTextureName("rpworld:itemHandsawIron");
		itemHandsawIron.setMaxDamage(320);
		itemHandsawDiamond = new ItemHandsaw(2);
		itemHandsawDiamond.setUnlocalizedName("handsawDiamond");
		itemHandsawDiamond.setTextureName("rpworld:itemHandsawDiamond");
		itemHandsawDiamond.setMaxDamage(1280);
		GameRegistry
				.addRecipe(
						new ItemStack(itemHandsawIron, 1),
						new Object[] { "WWW", " II", " II", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemHandsawDiamond, 1),
						new Object[] { "WWW", " II", " DD", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('D'), Items.diamond, Character
								.valueOf('W'), Items.stick });
		GameRegistry.addShapelessRecipe(
				CoreLib.copyStack(itemWaferSilicon, 16),
				new Object[] { itemBouleSilicon, new ItemStack(
						itemHandsawDiamond, 1, -1) });
		itemScrewdriver = new ItemScrewdriver();
		GameRegistry
				.addRecipe(
						new ItemStack(itemScrewdriver, 1),
						new Object[] { "I ", " W", Character.valueOf('I'), Items.iron_ingot, Character
								.valueOf('W'), Items.stick });
		GameRegistry.registerItem(itemScrewdriver, "screwdriver");
		GameRegistry
				.addRecipe(
						new ItemStack(itemDrawplateDiamond, 1),
						new Object[] { " I ", "IDI", " I ", Character
								.valueOf('I'), new ItemStack(blockMicro, 1,
								5649), Character.valueOf('D'), new ItemStack(
								blockMicro, 1, 4115) });
		GameRegistry.addShapelessRecipe(itemFineIron,
				new Object[] { Items.iron_ingot, new ItemStack(
						itemDrawplateDiamond, 1, -1) });
		CraftLib.addShapelessOreRecipe(itemFineCopper,
				new Object[] { "ingotCopper", new ItemStack(
						itemDrawplateDiamond, 1, -1) });
		GameRegistry.addRecipe(CoreLib.copyStack(itemNuggetIron, 9),
				new Object[] { "I", Character.valueOf('I'), Items.iron_ingot });
		CraftLib.addOreRecipe(CoreLib.copyStack(itemNuggetCopper, 9),
				new Object[] { "I", Character.valueOf('I'), "ingotCopper" });
		CraftLib.addOreRecipe(CoreLib.copyStack(itemNuggetTin, 9),
				new Object[] { "I", Character.valueOf('I'), "ingotTin" });
		CraftLib.addOreRecipe(CoreLib.copyStack(itemNuggetSilver, 9),
				new Object[] { "I", Character.valueOf('I'), "ingotSilver" });
		GameRegistry
				.addRecipe(
						new ItemStack(Items.iron_ingot, 1, 0),
						new Object[] { "III", "III", "III", Character
								.valueOf('I'), itemNuggetIron });
		GameRegistry
				.addRecipe(
						itemIngotSilver,
						new Object[] { "III", "III", "III", Character
								.valueOf('I'), itemNuggetSilver });
		GameRegistry
				.addRecipe(
						itemIngotTin,
						new Object[] { "III", "III", "III", Character
								.valueOf('I'), itemNuggetTin });
		GameRegistry
				.addRecipe(
						itemIngotCopper,
						new Object[] { "III", "III", "III", Character
								.valueOf('I'), itemNuggetCopper });
		GameRegistry
				.addRecipe(
						itemCanvas,
						new Object[] { "SSS", "SWS", "SSS", Character
								.valueOf('S'), Items.string, Character
								.valueOf('W'), Items.stick });
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 2),
				new Object[] { "D", Character.valueOf('D'), new ItemStack(
						blockMicro, 1, 4115) });
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1),
				new Object[] { "D", Character.valueOf('D'), new ItemStack(
						blockMicro, 1, 19) });
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 2),
				new Object[] { "I", Character.valueOf('I'), new ItemStack(
						blockMicro, 1, 4113) });
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1),
				new Object[] { "I", Character.valueOf('I'), new ItemStack(
						blockMicro, 1, 17) });
	}
}
