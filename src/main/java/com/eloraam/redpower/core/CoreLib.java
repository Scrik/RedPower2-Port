package com.eloraam.redpower.core;

import com.eloraam.redpower.core.WorldCoord;

import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class CoreLib {
	
	public static Comparator<ItemStack> itemStackComparator = new Comparator<ItemStack>() {
		public int compare(ItemStack o1, ItemStack o2) {
			return CoreLib.compareItemStack(o1, o2);
		}
	};
	private static TreeMap<ItemStack, String> oreMap = new TreeMap<ItemStack, String>(itemStackComparator);
	public static String[] rawColorNames = new String[] { "white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black" };
	public static String[] enColorNames = new String[] { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
	public static int[] paintColors = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, '\uff00', 16737408, 5460819, 9671571, '\uffff', 8388863, 255, 5187328, '\u8000', 16711680, 2039583 };
	public static final Material materialRedpower = new Material(MapColor.woodColor);
	
	public static boolean isClient(World world) {
		return world.isRemote;
	}
	
	public static void updateAllLightTypes(World world, int x, int y, int z) {
		world.updateLightByType(EnumSkyBlock.Block, x, y, z);
		world.updateLightByType(EnumSkyBlock.Sky, x, y, z);
	}
	
	@Deprecated
	void initModule(String name) {
		Class<?> cl;
		try {
			cl = Class.forName(name);
		} catch (ClassNotFoundException var8) {
			return;
		}
		
		Method mth;
		try {
			mth = cl.getDeclaredMethod("initialize", new Class[0]);
		} catch (NoSuchMethodException var7) {
			return;
		}
		
		try {
			mth.invoke((Object) null, new Object[0]);
		} catch (IllegalAccessException var5) {
			;
		} catch (InvocationTargetException var6) {
			;
		}
	}
	
	public static Object getTileEntity(IBlockAccess iba, int i, int j, int k, Class<?> cl) {
		TileEntity tr = iba.getTileEntity(i, j, k);
		return !cl.isInstance(tr) ? null : tr;
	}
	
	public static Object getTileEntity(IBlockAccess iba, WorldCoord wc, Class<?> cl) {
		TileEntity tr = iba.getTileEntity(wc.x, wc.y, wc.z);
		return !cl.isInstance(tr) ? null : tr;
	}
	
	public static Object getGuiTileEntity(World world, int i, int j, int k, Class<?> cl) {
		if (world.isRemote) {
			try {
				return cl.newInstance();
			} catch (InstantiationException var6) {
				return null;
			} catch (IllegalAccessException var7) {
				return null;
			}
		} else {
			TileEntity tr = world.getTileEntity(i, j, k);
			return !cl.isInstance(tr) ? null : tr;
		}
	}
	
	public static void markBlockDirty(World world, int i, int j, int k) {
		if (world.blockExists(i, j, k)) {
			world.getChunkFromBlockCoords(i, k).setChunkModified();
		}
	}
	
	public static int compareItemStack(ItemStack a, ItemStack b) {
		return Item.getIdFromItem(a.getItem()) != Item.getIdFromItem(b
				.getItem()) ? Item.getIdFromItem(a.getItem())
				- Item.getIdFromItem(b.getItem()) : (a.getItemDamage() == b
				.getItemDamage() ? 0 : (a.getItem().getHasSubtypes() ? a
				.getItemDamage() - b.getItemDamage() : 0));
	}
	
	static void registerOre(String name, ItemStack ore) {
		oreMap.put(ore, name);
	}
	
	public static void readOres() {
		String[] arr$ = OreDictionary.getOreNames();
		int len$ = arr$.length;
		
		for (int i$ = 0; i$ < len$; ++i$) {
			String st = arr$[i$];
			Iterator<?> i$1 = OreDictionary.getOres(st).iterator();
			
			while (i$1.hasNext()) {
				ItemStack ist = (ItemStack) i$1.next();
				registerOre(st, ist);
			}
		}
		
	}
	
	public static String getOreClass(ItemStack ist) {
		String st = (String) oreMap.get(ist);
		if (st != null) {
			return st;
		} else {
			ist = new ItemStack(ist.getItem(), 1, -1);
			return (String) oreMap.get(ist);
		}
	}
	
	public static boolean matchItemStackOre(ItemStack a, ItemStack b) {
		String s1 = getOreClass(a);
		String s2 = getOreClass(b);
		return s1 != null && s2 != null && s1.equals(s2) ? true : compareItemStack(
				a, b) == 0;
	}
	
	public static void dropItem(World world, int i, int j, int k, ItemStack ist) {
		if (!isClient(world)) {
			double d = 0.7D;
			double x = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
			double y = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
			double z = world.rand.nextFloat() * d + (1.0D - d) * 0.5D;
			EntityItem item = new EntityItem(world, i + x, j + y, k + z, ist);
			item.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(item);
		}
	}
	
	public static ItemStack copyStack(ItemStack ist, int n) {
		return new ItemStack(ist.getItem(), n, ist.getItemDamage());
	}
	
	public static int rotToSide(int r) {
		switch (r) {
			case 0:
				return 5;
			case 1:
				return 3;
			case 2:
				return 4;
			default:
				return 2;
		}
	}
	
	public static int getFacing(int side) {
		switch(side) {
			case 0: return 2;
			case 1: return 5;
			case 2: return 3;
			case 3: return 4;
			default: return 2;
		}
	}
	
	public static MovingObjectPosition retraceBlock(World world, EntityLivingBase ent, int i, int j, int k) {
		Vec3 org = Vec3.createVectorHelper(ent.posX, ent.posY + 1.62D - ent.yOffset, ent.posZ);
		Vec3 vec = ent.getLook(1.0F);
		Vec3 end = org.addVector(vec.xCoord * 5.0D, vec.yCoord * 5.0D, vec.zCoord * 5.0D);
		Block bl = world.getBlock(i, j, k);
		return bl == null ? null : bl.collisionRayTrace(world, i, j, k, org, end);
	}
	
	public static MovingObjectPosition traceBlock(EntityPlayer player) {
		Vec3 org = Vec3.createVectorHelper(player.posX, player.posY + 1.62D - player.yOffset, player.posZ);
		Vec3 vec = player.getLook(1.0F);
		Vec3 end = org.addVector(vec.xCoord * 5.0D, vec.yCoord * 5.0D, vec.zCoord * 5.0D);
		return player.worldObj.rayTraceBlocks(org, end);
	}
	
	public static void placeNoise(World world, int i, int j, int k, Block block) {
		world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, "step.stone",
				(block.stepSound.getVolume() + 1.0F) / 2.0F,
				block.stepSound.getPitch() * 0.8F);
	}
	
	public static int getBurnTime(ItemStack ist) {
		return TileEntityFurnace.getItemBurnTime(ist);
	}
	
	public static double getAverageEdgeLength(AxisAlignedBB aabb) {
		double d = aabb.maxX - aabb.minX;
		double d1 = aabb.maxY - aabb.minY;
		double d2 = aabb.maxZ - aabb.minZ;
		return (d + d1 + d2) / 3.0D;
	}
	
	public static void writeChat(EntityPlayer pl, String str) {
		if (pl instanceof EntityPlayerMP) {
			EntityPlayerMP emp = (EntityPlayerMP) pl;
			emp.addChatComponentMessage(new ChatComponentText(str));
		}
	}
	
	public static void updateBlock(World world, int x, int y, int z) {
		if(!(world.getTileEntity(x, y, z) instanceof TileExtended)) {
			world.func_147479_m(x, y, z);
		}
	}
	
	public static int[] toIntArray(List<Integer> integerList) {
	    int[] intArray = new int[integerList.size()];
	    for (int i = 0; i < integerList.size(); i++) {
	        intArray[i] = integerList.get(i);
	    }
	    return intArray;
	}
	
	public static <T, E> void setFinalValue(Class <? super T > classToAccess, T instance, E value, String... fieldNames) {
        try {
            findField(classToAccess, fieldNames).set(instance, value);
        } catch (Exception e) {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }
	
	public static Field findField(Class<?> clazz, String... fieldNames) {
        Exception failed = null;
        for (String fieldName : fieldNames) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
        		modifiersField.setAccessible(true);
        		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                
                return field;
            } catch (Exception e) {
                failed = e;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }
	
	/*static void setFinalStatic(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		
		field.set(null, newValue);
	}*/
}
