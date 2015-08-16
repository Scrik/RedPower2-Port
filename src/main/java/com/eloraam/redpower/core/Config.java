package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.TagFile;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import net.minecraft.block.Block;

public class Config {
	
	static boolean[] reservedIds = new boolean['\u8000'];
	static File configDir = null;
	static File configFile = null;
	static TagFile config = null;
	static Properties rpTranslateTable = null;
	static boolean autoAssign = true;
	
	public static void loadConfig() {
		config = new TagFile();
		InputStream is = RedPowerCore.class.getResourceAsStream("/assets/rpcore/default.cfg");
		config.readStream(is);
		File file;
		if (configDir == null) {
			file = Loader.instance().getConfigDir();
			file = new File(file, "/redpower/");
			file.mkdir();
			configDir = file;
			configFile = new File(file, "redpower.cfg");
		}
		
		if (configFile.exists()) {
			config.readFile(configFile);
		}
		
		config.commentFile("RedPower 2 Configuration");
		
		String entry;
		Iterator<?> file1;
		for (file1 = config.query("blocks.%.%.id").iterator(); file1.hasNext(); reservedIds[config.getInt(entry)] = true) {
			entry = (String)file1.next();
		}
		
		for (file1 = config.query("items.%.%.id").iterator(); file1.hasNext(); reservedIds[config.getInt(entry) + 256] = true) {
			entry = (String)file1.next();
		}
		
		if (rpTranslateTable == null) {
			rpTranslateTable = new Properties();
		}
		
		try {
			rpTranslateTable.load(RedPowerCore.class.getResourceAsStream("/assets/rpcore/redpower.lang"));
			file = new File(configDir, "redpower.lang");
			if (file.exists()) {
				FileInputStream entry1 = new FileInputStream(file);
				rpTranslateTable.load(entry1);
			}
		} catch (IOException var3) {
			var3.printStackTrace();
		}
		
		file1 = rpTranslateTable.entrySet().iterator();
		
		while (file1.hasNext()) {
			Entry<?, ?> entry2 = (Entry<?, ?>) file1.next();
			LanguageRegistry.instance().addStringLocalization((String) entry2.getKey(), (String) entry2.getValue());
		}
		
		autoAssign = config.getInt("settings.core.autoAssign") > 0;
		config.addInt("settings.core.autoAssign", 0);
		config.commentTag("settings.core.autoAssign", "Automatically remap conflicting IDs.\nWARNING: May corrupt existing worlds");
	}
	
	public static void saveConfig() {
		config.saveFile(configFile);
		
		try {
			File e = new File(configDir, "redpower.lang");
			FileOutputStream os = new FileOutputStream(e);
			rpTranslateTable.store(os, "RedPower Language File");
		} catch (IOException var2) {
			var2.printStackTrace();
		}
		
	}
	
	public static void addName(String tag, String name) {
		if (rpTranslateTable.get(tag) == null) {
			rpTranslateTable.put(tag, name);
			LanguageRegistry.instance().addStringLocalization(tag, name);
		}
	}
	
	public static void addName(Block bl, String name) {
		addName(bl.getUnlocalizedName() + ".name", name);
	}
	
	public static void die(String msg) {
		throw new RuntimeException("RedPowerCore: " + msg);
	}
	
	/*public static int getItemID(String name) {
		int cid = config.getInt(name);
		if (Item.itemsList[256 + cid] == null) {
			return cid;
		} else if (!autoAssign) {
			die(String.format("ItemID %d exists, autoAssign is disabled.",
					new Object[] { Integer.valueOf(cid) }));
			return -1;
		} else {
			for (int i = 1024; i < 32000; ++i) {
				if (!reservedIds[i] && Item.itemsList[i] == null) {
					config.addInt(name, i - 256);
					return i;
				}
			}
			
			die("Out of available ItemIDs, could not autoassign!");
			return -1;
		}
	}*/
	
	/*public static int getBlockID(String name) {
		int cid = config.getInt(name);
		if (Block.blocksList[cid] == null) {
			return cid;
		} else if (!autoAssign) {
			die(String.format(
					"BlockID %d occupied by %s, autoAssign is disabled.",
					new Object[] { Integer.valueOf(cid), Block.blocksList[cid]
							.getClass().getName() }));
			return -1;
		} else {
			for (int i = 255; i >= 20; --i) {
				if (!reservedIds[i] && Block.blocksList[i] == null) {
					config.addInt(name, i);
					return i;
				}
			}
			
			die("Out of available BlockIDs, could not autoassign!");
			return -1;
		}
	}*/
	
	public static int getInt(String name) {
		return config.getInt(name);
	}
	
	public static String getString(String name) {
		return config.getString(name);
	}
	
}
