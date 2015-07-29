package com.eloraam.redpower;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemPartialCraft;
import com.eloraam.redpower.core.ItemTextured;
import com.eloraam.redpower.world.BlockBrickMossifier;
import com.eloraam.redpower.world.BlockCobbleMossifier;
import com.eloraam.redpower.world.BlockCustomCrops;
import com.eloraam.redpower.world.BlockCustomFlower;
import com.eloraam.redpower.world.BlockCustomLeaves;
import com.eloraam.redpower.world.BlockCustomLog;
import com.eloraam.redpower.world.BlockCustomOre;
import com.eloraam.redpower.world.BlockCustomStone;
import com.eloraam.redpower.world.BlockStorage;
import com.eloraam.redpower.world.EnchantmentDisjunction;
import com.eloraam.redpower.world.EnchantmentVorpal;
import com.eloraam.redpower.world.ItemAthame;
import com.eloraam.redpower.world.ItemCustomAxe;
import com.eloraam.redpower.world.ItemCustomFlower;
import com.eloraam.redpower.world.ItemCustomHoe;
import com.eloraam.redpower.world.ItemCustomOre;
import com.eloraam.redpower.world.ItemCustomPickaxe;
import com.eloraam.redpower.world.ItemCustomSeeds;
import com.eloraam.redpower.world.ItemCustomShovel;
import com.eloraam.redpower.world.ItemCustomStone;
import com.eloraam.redpower.world.ItemCustomSword;
import com.eloraam.redpower.world.ItemPaintBrush;
import com.eloraam.redpower.world.ItemPaintCan;
import com.eloraam.redpower.world.ItemSeedBag;
import com.eloraam.redpower.world.ItemSickle;
import com.eloraam.redpower.world.ItemStorage;
import com.eloraam.redpower.world.ItemWoolCard;
import com.eloraam.redpower.world.WorldEvents;
import com.eloraam.redpower.world.WorldGenHandler;
import com.eloraam.redpower.world.WorldProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = "RedPowerWorld", name = "RedPower World", version = "2.0pr6", certificateFingerprint = "28f7f8a775e597088f3a418ea29290b6a1d23c7b", dependencies = "required-after:RedPowerBase")
public class RedPowerWorld {
	
	@Instance("RedPowerWorld")
	public static RedPowerWorld instance;
	@SidedProxy(clientSide = "com.eloraam.redpower.world.WorldProxyClient", serverSide = "com.eloraam.redpower.world.WorldProxy")
	public static WorldProxy proxy;
	public static BlockCustomFlower blockPlants;
	public static BlockCustomOre blockOres;
	public static BlockCustomLeaves blockLeaves;
	public static BlockCustomLog blockLogs;
	public static BlockCustomStone blockStone;
	public static BlockCustomCrops blockCrops;
	public static BlockStorage blockStorage;
	public static ItemStack itemOreRuby;
	public static ItemStack itemOreGreenSapphire;
	public static ItemStack itemOreSapphire;
	public static ItemStack itemMarble;
	public static ToolMaterial toolMaterialRuby;
	public static ToolMaterial toolMaterialGreenSapphire;
	public static ToolMaterial toolMaterialSapphire;
	public static ItemSickle itemSickleWood;
	public static ItemSickle itemSickleStone;
	public static ItemSickle itemSickleIron;
	public static ItemSickle itemSickleDiamond;
	public static ItemSickle itemSickleGold;
	public static ItemSickle itemSickleRuby;
	public static ItemSickle itemSickleGreenSapphire;
	public static ItemSickle itemSickleSapphire;
	public static ItemCustomPickaxe itemPickaxeRuby;
	public static ItemCustomPickaxe itemPickaxeGreenSapphire;
	public static ItemCustomPickaxe itemPickaxeSapphire;
	public static ItemCustomShovel itemShovelRuby;
	public static ItemCustomShovel setUnlocalizedName;
	public static ItemCustomShovel itemShovelSapphire;
	public static ItemCustomShovel itemShovelGreenSapphire;
	public static ItemCustomAxe itemAxeRuby;
	public static ItemCustomAxe itemAxeGreenSapphire;
	public static ItemCustomAxe itemAxeSapphire;
	public static ItemCustomSword itemSwordRuby;
	public static ItemCustomSword itemSwordGreenSapphire;
	public static ItemCustomSword itemSwordSapphire;
	public static ItemAthame itemAthame;
	public static ItemCustomHoe itemHoeRuby;
	public static ItemCustomHoe itemHoeGreenSapphire;
	public static ItemCustomHoe itemHoeSapphire;
	public static ItemCustomSeeds itemSeeds;
	public static Item itemHandsawRuby;
	public static Item itemHandsawGreenSapphire;
	public static Item itemHandsawSapphire;
	public static Item itemBrushDry;
	public static Item itemPaintCanEmpty;
	public static Item[] itemBrushPaint = new Item[16];
	public static ItemPartialCraft[] itemPaintCanPaint = new ItemPartialCraft[16];
	public static Item itemWoolCard;
	public static Item itemSeedBag;
	public static Enchantment enchantDisjunction;
	public static Enchantment enchantVorpal;
	public static final String blockTextureFile = "/eloraam/world/world1.png";
	public static final String itemTextureFile = "/eloraam/world/worlditems1.png";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new WorldEvents());
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGenHandler(), 1);
		this.setupOres();
		this.setupPlants();
		this.setupTools();
		this.setupMisc();
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public void setupPlants() {
		blockPlants = new BlockCustomFlower("rpworld:blockIndigoFlower", "rpworld:blockRubberSapling");
		blockPlants.setBlockName("plant");
		GameRegistry.registerBlock(blockPlants, ItemCustomFlower.class, "plants");
		
		
		//GameRegistry.registerWorldGenerator(new WorldGenFlowers(blockPlants), 1);
		//MinecraftForge.addGrassPlant(blockPlants, 0, 10);
		GameRegistry.addShapelessRecipe(new ItemStack(RedPowerBase.itemDyeIndigo, 2, 0), new Object[] { blockPlants });
		itemSeeds = new ItemCustomSeeds();
		MinecraftForge.addGrassSeed(new ItemStack(itemSeeds, 1, 0), 5);
		blockCrops = new BlockCustomCrops();
		GameRegistry.registerBlock(blockCrops, "flax");
		GameRegistry.registerItem(itemSeeds, "flaxseeds");
		blockLeaves = new BlockCustomLeaves("rpworld:blockRubberLeaves_opaque", "rpworld:blockRubberLeaves_transparent");
		blockLeaves.setBlockName("rpleaves");
		GameRegistry.registerBlock(blockLeaves, "leaves");
		blockLogs = new BlockCustomLog("rpworld:blockRubberLogSide", "rpworld:blockRubberLogTop");
		blockLogs.setBlockName("rplog");
		GameRegistry.registerBlock(blockLogs, "logs");
		blockLogs.setHarvestLevel("axe", 0, 0);
		OreDictionary.registerOre("woodRubber", new ItemStack(blockLogs));
		GameRegistry.addRecipe(new ItemStack(Items.stick, 8), new Object[] { "W", Character.valueOf('W'), blockLogs });
		GameRegistry.addSmelting(new ItemStack(blockLogs, 1, 0), new ItemStack(Items.coal, 1, 1), 0.15F);
		CoverLib.addMaterial(53, 0, blockLogs, 0, "rplog", "Rubberwood");
	}
	
	public void setupOres() {
		blockStone = new BlockCustomStone();
		blockStone.setBlockName("rpstone");
		GameRegistry.registerBlock(blockStone, ItemCustomStone.class, "stone");
		itemMarble = new ItemStack(blockStone, 0);
		blockStone.setHarvestLevel("pickaxe", 0);
		blockStone.setBlockTexture(0, "rpworld:blockMarble");
		blockStone.setBlockTexture(1, "rpworld:blockBasalt");
		blockStone.setBlockTexture(2, "rpworld:blockMarbleBrick");
		blockStone.setBlockTexture(3, "rpworld:blockBasaltCobble");
		blockStone.setBlockTexture(4, "rpworld:blockBasaltBrick");
		blockStone.setBlockTexture(5, "rpworld:blockChiseledBasaltBrick");
		blockStone.setBlockTexture(6, "rpworld:blockBasaltPaver");
		CoverLib.addMaterial(48, 1, blockStone, 0, "marble", "Marble");
		CoverLib.addMaterial(49, 1, blockStone, 1, "basalt", "Basalt");
		CoverLib.addMaterial(50, 1, blockStone, 2, "marbleBrick", "Marble Brick");
		CoverLib.addMaterial(51, 1, blockStone, 3, "basaltCobble", "Basalt Cobblestone");
		CoverLib.addMaterial(52, 1, blockStone, 4, "basaltBrick", "Basalt Brick");
		CoverLib.addMaterial(57, 1, blockStone, 5, "basaltCircle", "Chiseled Basalt Brick");
		CoverLib.addMaterial(58, 1, blockStone, 6, "basaltPaver", "Basalt Paver");
		blockOres = new BlockCustomOre();
		GameRegistry.registerBlock(blockOres, ItemCustomOre.class, "ores");
		itemOreRuby = new ItemStack(blockOres, 1, 0);
		itemOreGreenSapphire = new ItemStack(blockOres, 1, 1);
		itemOreSapphire = new ItemStack(blockOres, 1, 2);
		blockOres.setHarvestLevel("pickaxe", 2, 0);
		blockOres.setHarvestLevel("pickaxe", 2, 1);
		blockOres.setHarvestLevel("pickaxe", 2, 2);
		blockOres.setHarvestLevel("pickaxe", 1, 3);
		blockOres.setHarvestLevel("pickaxe", 0, 4);
		blockOres.setHarvestLevel("pickaxe", 0, 5);
		blockOres.setHarvestLevel("pickaxe", 2, 6);
		blockOres.setHarvestLevel("pickaxe", 2, 7);
		
		GameRegistry.addSmelting(new ItemStack(blockOres, 1, 3), RedPowerBase.itemIngotSilver, 1.0F);
		GameRegistry.addSmelting(new ItemStack(blockOres, 1, 4), RedPowerBase.itemIngotTin, 0.7F);
		GameRegistry.addSmelting(new ItemStack(blockOres, 1, 5), RedPowerBase.itemIngotCopper, 0.7F);
		
		OreDictionary.registerOre("oreRuby", new ItemStack(blockOres, 1, 0));
		OreDictionary.registerOre("oreGreenSapphire", new ItemStack(blockOres, 1, 1));
		OreDictionary.registerOre("oreSapphire", new ItemStack(blockOres, 1, 2));
		OreDictionary.registerOre("oreSilver", new ItemStack(blockOres, 1, 3));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOres, 1, 4));
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOres, 1, 5));
		OreDictionary.registerOre("oreTungsten", new ItemStack(blockOres, 1, 6));
		OreDictionary.registerOre("oreNikolite", new ItemStack(blockOres, 1, 7));
		GameRegistry
				.addRecipe(
						new ItemStack(blockStone, 4, 2),
						new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(
								blockStone, 1, 0) });
		GameRegistry.addSmelting(new ItemStack(blockStone, 1, 3), new ItemStack(blockStone, 1, 1), 0.2F);
		
		GameRegistry
				.addRecipe(
						new ItemStack(blockStone, 4, 4),
						new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(
								blockStone, 1, 1) });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStone, 4, 5),
						new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(
								blockStone, 1, 4) });
		GameRegistry.addRecipe(new ItemStack(blockStone, 1, 6),
				new Object[] { "S", Character.valueOf('S'), new ItemStack(blockStone, 1, 1) });
		blockStorage = new BlockStorage();
		GameRegistry.registerBlock(blockStorage, ItemStorage.class, "orestorage");
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 0),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemRuby });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 1),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemGreenSapphire });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 2),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemSapphire });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 3),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemIngotSilver });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 4),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemIngotTin });
		GameRegistry
				.addRecipe(
						new ItemStack(blockStorage, 1, 5),
						new Object[] { "GGG", "GGG", "GGG", Character
								.valueOf('G'), RedPowerBase.itemIngotCopper });
		GameRegistry.addRecipe(CoreLib.copyStack(RedPowerBase.itemRuby, 9),
				new Object[] { "G", Character.valueOf('G'), new ItemStack(
						blockStorage, 1, 0) });
		GameRegistry.addRecipe(CoreLib.copyStack(
				RedPowerBase.itemGreenSapphire, 9),
				new Object[] { "G", Character.valueOf('G'), new ItemStack(
						blockStorage, 1, 1) });
		GameRegistry.addRecipe(CoreLib.copyStack(RedPowerBase.itemSapphire, 9),
				new Object[] { "G", Character.valueOf('G'), new ItemStack(
						blockStorage, 1, 2) });
		GameRegistry.addRecipe(CoreLib.copyStack(RedPowerBase.itemIngotSilver,
				9), new Object[] { "G", Character.valueOf('G'), new ItemStack(
				blockStorage, 1, 3) });
		GameRegistry.addRecipe(CoreLib.copyStack(RedPowerBase.itemIngotTin, 9),
				new Object[] { "G", Character.valueOf('G'), new ItemStack(
						blockStorage, 1, 4) });
		GameRegistry.addRecipe(CoreLib.copyStack(RedPowerBase.itemIngotCopper,
				9), new Object[] { "G", Character.valueOf('G'), new ItemStack(
				blockStorage, 1, 5) });
		blockStorage.setHarvestLevel("pickaxe", 2, 0);
		blockStorage.setHarvestLevel("pickaxe", 2, 1);
		blockStorage.setHarvestLevel("pickaxe", 2, 2);
		blockStorage.setHarvestLevel("pickaxe", 2, 3);
		blockStorage.setHarvestLevel("pickaxe", 2, 4);
		blockStorage.setHarvestLevel("pickaxe", 2, 5);
		
		CoverLib.addMaterial(54, 2, blockStorage, 0, "rubyBlock", "Ruby Block");
		CoverLib.addMaterial(55, 2, blockStorage, 1, "greenSapphireBlock", "Green Sapphire Block");
		CoverLib.addMaterial(56, 2, blockStorage, 2, "sapphireBlock", "Sapphire Block");
		CoverLib.addMaterial(66, 2, blockStorage, 3, "silverBlock", "Silver Block");
		CoverLib.addMaterial(67, 2, blockStorage, 4, "tinBlock", "Tin Block");
		CoverLib.addMaterial(68, 2, blockStorage, 5, "copperBlock", "Copper Block");
	}
	
	public void setupTools() {
		toolMaterialRuby = EnumHelper.addToolMaterial("RUBY", 2, 500, 8.0F, 3, 12);
		toolMaterialGreenSapphire = EnumHelper.addToolMaterial("GREENSAPPHIRE", 2, 500, 8.0F, 3, 12);
		toolMaterialSapphire = EnumHelper.addToolMaterial("SAPPHIRE", 2, 500, 8.0F, 3, 12);
		
		itemPickaxeRuby = new ItemCustomPickaxe(toolMaterialRuby);
		itemPickaxeRuby.setUnlocalizedName("pickaxeRuby");
		itemPickaxeRuby.setTextureName("rpworld:itemPickaxeRuby");
		itemPickaxeGreenSapphire = new ItemCustomPickaxe(toolMaterialGreenSapphire);
		itemPickaxeGreenSapphire.setUnlocalizedName("pickaxeGreenSapphire");
		itemPickaxeGreenSapphire.setTextureName("rpworld:itemPickaxeGreenSapphire");
		itemPickaxeSapphire = new ItemCustomPickaxe(toolMaterialSapphire);
		itemPickaxeSapphire.setUnlocalizedName("pickaxeSapphire");
		itemPickaxeSapphire.setTextureName("rpworld:itemPickaxeSapphire");
		
		itemPickaxeRuby.setHarvestLevel("pickaxe", 2);
		itemPickaxeGreenSapphire.setHarvestLevel("pickaxe", 2);
		itemPickaxeSapphire.setHarvestLevel("pickaxe", 2);
		
		GameRegistry
				.addRecipe(
						new ItemStack(itemPickaxeRuby, 1),
						new Object[] { "GGG", " W ", " W ", Character
								.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemPickaxeGreenSapphire, 1),
						new Object[] { "GGG", " W ", " W ", Character
								.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemPickaxeSapphire, 1),
						new Object[] { "GGG", " W ", " W ", Character
								.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemShovelRuby = new ItemCustomShovel(toolMaterialRuby);
		itemShovelRuby.setUnlocalizedName("shovelRuby");
		itemShovelRuby.setTextureName("rpworld:itemShovelRuby");
		itemShovelGreenSapphire = new ItemCustomShovel(toolMaterialGreenSapphire);
		itemShovelGreenSapphire.setUnlocalizedName("shovelGreenSapphire");
		itemShovelGreenSapphire.setTextureName("rpworld:itemShovelGreenSapphire");
		itemShovelSapphire = new ItemCustomShovel(toolMaterialSapphire);
		itemShovelSapphire.setUnlocalizedName("shovelSapphire");
		itemShovelSapphire.setTextureName("rpworld:itemShovelSapphire");
		
		itemShovelRuby.setHarvestLevel("shovel", 2);
		itemShovelGreenSapphire.setHarvestLevel("shovel", 2);
		itemShovelSapphire.setHarvestLevel("shovel", 2);
		GameRegistry
				.addRecipe(
						new ItemStack(itemShovelRuby, 1),
						new Object[] { "G", "W", "W", Character.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemShovelGreenSapphire, 1),
						new Object[] { "G", "W", "W", Character.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemShovelSapphire, 1),
						new Object[] { "G", "W", "W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemAxeRuby = new ItemCustomAxe(toolMaterialRuby);
		itemAxeRuby.setUnlocalizedName("axeRuby");
		itemAxeRuby.setTextureName("rpworld:itemAxeRuby");
		itemAxeGreenSapphire = new ItemCustomAxe(toolMaterialGreenSapphire);
		itemAxeGreenSapphire.setUnlocalizedName("axeGreenSapphire");
		itemAxeGreenSapphire.setTextureName("rpworld:itemAxeGreenSapphire");
		itemAxeSapphire = new ItemCustomAxe(toolMaterialSapphire);
		itemAxeSapphire.setTextureName("axeSapphire");
		itemAxeSapphire.setTextureName("rpworld:itemAxeSapphire");
		itemAxeRuby.setHarvestLevel("axe", 2);
		itemAxeGreenSapphire.setHarvestLevel("axe", 2);
		itemAxeSapphire.setHarvestLevel("axe", 2);
		GameRegistry
				.addRecipe(
						new ItemStack(itemAxeRuby, 1),
						new Object[] { "GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemAxeGreenSapphire, 1),
						new Object[] { "GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemAxeSapphire, 1),
						new Object[] { "GG", "GW", " W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemSwordRuby = new ItemCustomSword(toolMaterialRuby);
		itemSwordRuby.setUnlocalizedName("swordRuby");
		itemSwordRuby.setUnlocalizedName("rpworld:itemSwordRuby");
		itemSwordGreenSapphire = new ItemCustomSword(toolMaterialGreenSapphire);
		itemSwordGreenSapphire.setUnlocalizedName("swordGreenSapphire");
		itemSwordGreenSapphire.setTextureName("rpworld:itemSwordGreenSapphire");
		itemSwordSapphire = new ItemCustomSword(toolMaterialSapphire);
		itemSwordSapphire.setUnlocalizedName("swordSapphire");
		itemSwordSapphire.setTextureName("rpworld:itemSwordSapphire");
		itemAthame = new ItemAthame();
		itemAthame.setUnlocalizedName("athame");
		//MinecraftForge.setToolClass(itemSwordRuby, "sword", 2); TODO: FIGURE IT OUT
		//MinecraftForge.setToolClass(itemSwordGreenSapphire, "sword", 2);
		//MinecraftForge.setToolClass(itemSwordSapphire, "sword", 2);
		//MinecraftForge.setToolClass(itemAthame, "sword", 0);
		CraftLib.addOreRecipe(
				new ItemStack(itemAthame, 1),
				new Object[] { "S", "W", Character.valueOf('S'), "ingotSilver", Character
						.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSwordRuby, 1),
						new Object[] { "G", "G", "W", Character.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSwordGreenSapphire, 1),
						new Object[] { "G", "G", "W", Character.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSwordSapphire, 1),
						new Object[] { "G", "G", "W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemHoeRuby = new ItemCustomHoe(toolMaterialRuby);
		itemHoeRuby.setUnlocalizedName("hoeRuby");
		itemHoeRuby.setTextureName("rpworld:itemHoeRuby");
		itemHoeRuby.setMaxDamage(500);
		itemHoeGreenSapphire = new ItemCustomHoe(toolMaterialGreenSapphire);
		itemHoeGreenSapphire.setUnlocalizedName("hoeGreenSapphire");
		itemHoeGreenSapphire.setTextureName("rpworld:itemHoeGreenSapphire");
		itemHoeGreenSapphire.setMaxDamage(500);
		itemHoeSapphire = new ItemCustomHoe(toolMaterialSapphire);
		itemHoeSapphire.setUnlocalizedName("hoeSapphire");
		itemHoeSapphire.setTextureName("rpworld:itemHoeSapphire");
		itemHoeSapphire.setMaxDamage(500);
		itemHoeRuby.setHarvestLevel("hoe", 2);
		itemHoeGreenSapphire.setHarvestLevel("hoe", 2);
		itemHoeSapphire.setHarvestLevel("hoe", 2);
		GameRegistry
				.addRecipe(
						new ItemStack(itemHoeRuby, 1),
						new Object[] { "GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemHoeGreenSapphire, 1),
						new Object[] { "GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemHoeSapphire, 1),
						new Object[] { "GG", " W", " W", Character.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemSickleWood = new ItemSickle(ToolMaterial.WOOD);
		itemSickleWood.setUnlocalizedName("sickleWood");
		itemSickleWood.setTextureName("rpworld:itemSickleWood");
		itemSickleStone = new ItemSickle(ToolMaterial.STONE);
		itemSickleStone.setUnlocalizedName("sickleStone");
		itemSickleStone.setTextureName("rpworld:itemSickleStone");
		itemSickleIron = new ItemSickle(ToolMaterial.IRON);
		itemSickleIron.setUnlocalizedName("sickleIron");
		itemSickleIron.setTextureName("rpworld:itemSickleIron");
		itemSickleDiamond = new ItemSickle(ToolMaterial.EMERALD);
		itemSickleDiamond.setUnlocalizedName("sickleDiamond");
		itemSickleDiamond.setTextureName("rpworld:itemSickleDiamond");
		itemSickleGold = new ItemSickle(ToolMaterial.GOLD);
		itemSickleGold.setUnlocalizedName("sickleGold");
		itemSickleGold.setTextureName("rpworld:itemSickleGold");
		itemSickleRuby = new ItemSickle(toolMaterialRuby);
		itemSickleRuby.setUnlocalizedName("sickleRuby");
		itemSickleRuby.setTextureName("rpworld:itemSickleRuby");
		itemSickleGreenSapphire = new ItemSickle(toolMaterialGreenSapphire);
		itemSickleGreenSapphire.setUnlocalizedName("sickleGreenSapphire");
		itemSickleGreenSapphire.setTextureName("rpworld:itemSickleGreenSapphire");
		itemSickleSapphire = new ItemSickle(toolMaterialSapphire);
		itemSickleSapphire.setUnlocalizedName("sickleSapphire");
		itemSickleSapphire.setTextureName("rpworld:itemSickleSapphire");
		CraftLib.addOreRecipe(
				new ItemStack(itemSickleWood, 1),
				new Object[] { " I ", "  I", "WI ", Character.valueOf('I'), "plankWood", Character
						.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleStone, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), Blocks.cobblestone, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleIron, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleDiamond, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), Items.diamond, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleGold, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), Items.gold_ingot, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleRuby, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleGreenSapphire, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemSickleSapphire, 1),
						new Object[] { " I ", "  I", "WI ", Character
								.valueOf('I'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemHandsawRuby = new ItemHandsaw(1);
		itemHandsawGreenSapphire = new ItemHandsaw(1);
		itemHandsawSapphire = new ItemHandsaw(1);
		itemHandsawRuby.setUnlocalizedName("handsawRuby").setTextureName("rpworld:itemHandsawRuby");
		itemHandsawGreenSapphire.setUnlocalizedName("handsawGreenSapphire").setTextureName("rpworld:itemHandsawGreenSapphire");
		itemHandsawSapphire.setUnlocalizedName("handsawSapphire").setTextureName("rpworld:itemHandsawParrhire");
		itemHandsawRuby.setMaxDamage(640);
		itemHandsawGreenSapphire.setMaxDamage(640);
		itemHandsawSapphire.setMaxDamage(640);
		GameRegistry
				.addRecipe(
						new ItemStack(itemHandsawRuby, 1),
						new Object[] { "WWW", " II", " GG", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('G'), RedPowerBase.itemRuby, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemHandsawGreenSapphire, 1),
						new Object[] { "WWW", " II", " GG", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('G'), RedPowerBase.itemGreenSapphire, Character
								.valueOf('W'), Items.stick });
		GameRegistry
				.addRecipe(
						new ItemStack(itemHandsawSapphire, 1),
						new Object[] { "WWW", " II", " GG", Character
								.valueOf('I'), Items.iron_ingot, Character
								.valueOf('G'), RedPowerBase.itemSapphire, Character
								.valueOf('W'), Items.stick });
		itemWoolCard = new ItemWoolCard();
		CraftLib.addOreRecipe(
				new ItemStack(itemWoolCard, 1),
				new Object[] { "W", "P", "S", Character.valueOf('W'), RedPowerBase.itemFineIron, Character
						.valueOf('P'), "plankWood", Character.valueOf('S'), Items.stick });
		GameRegistry
				.addShapelessRecipe(
						new ItemStack(Items.string, 4),
						new Object[] { new ItemStack(itemWoolCard, 1, -1), new ItemStack(
								Blocks.wool, 1, -1) });
		itemBrushDry = new ItemTextured("rpworld:itemBrushDry");
		itemBrushDry.setUnlocalizedName("paintbrush.dry");
		GameRegistry
				.addRecipe(
						new ItemStack(itemBrushDry),
						new Object[] { "W ", " S", Character.valueOf('S'), Items.stick, 
							Character.valueOf('W'), Blocks.wool });
		itemPaintCanEmpty = new ItemTextured("rpworld:itemPaintCanEmpty");
		itemPaintCanEmpty.setUnlocalizedName("paintcan.empty");
		GameRegistry
				.addRecipe(
						new ItemStack(itemPaintCanEmpty, 3),
						new Object[] { "T T", "T T", "TTT", Character
								.valueOf('T'), RedPowerBase.itemTinplate });
		
		for (int i = 0; i < 16; ++i) {
			itemPaintCanPaint[i] = new ItemPaintCan(i);
			itemPaintCanPaint[i].setUnlocalizedName("paintcan." + CoreLib.rawColorNames[i]);
			itemPaintCanPaint[i].setEmptyItem(new ItemStack(itemPaintCanEmpty));
			Config.addName("item.paintcan." + CoreLib.rawColorNames[i]
					+ ".name", CoreLib.enColorNames[i] + " Paint");
			GameRegistry.addShapelessRecipe(
					new ItemStack(itemPaintCanPaint[i]),
					new Object[] { itemPaintCanEmpty, new ItemStack(
							Items.dye, 1, 15 - i), new ItemStack(
							itemSeeds, 1, 0), new ItemStack(itemSeeds, 1, 0) });
			itemBrushPaint[i] = new ItemPaintBrush(i);
			itemBrushPaint[i].setUnlocalizedName("paintbrush."+ CoreLib.rawColorNames[i]);
			Config.addName("item.paintbrush." + CoreLib.rawColorNames[i]
					+ ".name", CoreLib.enColorNames[i] + " Paint Brush");
			GameRegistry
					.addShapelessRecipe(new ItemStack(itemBrushPaint[i]),
							new Object[] { new ItemStack(itemPaintCanPaint[i],
									1, -1), itemBrushDry });
		}
		
		CraftLib.addShapelessOreRecipe(new ItemStack(itemPaintCanPaint[11]),
				new Object[] { itemPaintCanEmpty, "dyeBlue", new ItemStack(
						itemSeeds, 1, 0), new ItemStack(itemSeeds, 1, 0) });
		itemSeedBag = new ItemSeedBag();
		GameRegistry
				.addRecipe(
						new ItemStack(itemSeedBag, 1, 0),
						new Object[] { " S ", "C C", "CCC", Character
								.valueOf('S'), Items.string, Character
								.valueOf('C'), RedPowerBase.itemCanvas });
	}
	
	void setupMisc() {
		if (Config.getInt("settings.world.tweaks.spreadmoss") > 0) {
			CoreLib.setFinalValue(Blocks.class, null, new BlockCobbleMossifier(), new String[]{"mossy_cobblestone"});
			CoreLib.setFinalValue(Blocks.class, null, new BlockBrickMossifier(), new String[]{"stonebrick"});
			//TODO: Blocks.mossy_cobblestone = new BlockCobbleMossifier();
			//TODO: Blocks.stonebrick = new BlockBrickMossifier();
		}
		
		if (Config.getInt("settings.world.tweaks.craftcircle") > 0) {
			GameRegistry
					.addRecipe(
							new ItemStack(Blocks.stonebrick, 4, 3),
							new Object[] { "BB", "BB", Character.valueOf('B'), new ItemStack(
									Blocks.stonebrick, 1, 0) });
		}
		
		if (Config.getInt("settings.world.tweaks.unbricks") > 0) {
			GameRegistry.addShapelessRecipe(new ItemStack(Items.brick, 4, 0),
					new Object[] { new ItemStack(Blocks.brick_block, 1, 0) });
		}
		
		enchantDisjunction = new EnchantmentDisjunction(Config.getInt("enchant.disjunction.id"), 10);
		enchantVorpal = new EnchantmentVorpal(Config.getInt("enchant.vorpal.id"), 10);
	}
}
