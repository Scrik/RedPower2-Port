package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.WorldCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSeedBag extends Item {
	
	private IIcon emptyIcon;
	private IIcon fullIcon;
	
	public ItemSeedBag() {
		this.setMaxDamage(576);
		this.setMaxStackSize(1);
		this.setUnlocalizedName("rpSeedBag");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return meta > 0 ? this.fullIcon : this.emptyIcon;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.emptyIcon = register.registerIcon("rpworld:itemSeedBagEmpty");
        this.fullIcon = register.registerIcon("rpworld:itemSeedBagFull");
    }
	
	public static IInventory getBagInventory(ItemStack ist) {
		return !(ist.getItem() instanceof ItemSeedBag) ? null : new ItemSeedBag.InventorySeedBag(ist);
	}
	
	public static boolean canAdd(IInventory inv, ItemStack ist) {
		if (!(ist.getItem() instanceof IPlantable)) {
			return false;
		} else {
			for (int i = 0; i < inv.getSizeInventory(); ++i) {
				ItemStack is2 = inv.getStackInSlot(i);
				if (is2 != null && is2.stackSize != 0
						&& CoreLib.compareItemStack(is2, ist) != 0) {
					return false;
				}
			}
			return true;
		}
	}
	
	public static ItemStack getPlant(IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack is2 = inv.getStackInSlot(i);
			if (is2 != null && is2.stackSize != 0) {
				return is2;
			}
		}
		return null;
	}
	
	private static void decrPlant(IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack is2 = inv.getStackInSlot(i);
			if (is2 != null && is2.stackSize != 0) {
				inv.decrStackSize(i, 1);
				break;
			}
		}
		
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world,
			EntityPlayer player) {
		if (CoreLib.isClient(world)) {
			return ist;
		} else if (!player.isSneaking()) {
			return ist;
		} else {
			player.openGui(RedPowerWorld.instance, 1, world, 0, 0, 0);
			return ist;
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world,
			int x, int y, int z, int side, float par8, float par9, float par10) {
		if (side != 1) {
			return false;
		} else if (CoreLib.isClient(world)) {
			return false;
		} else if (player.isSneaking()) {
			return false;
		} else {
			IInventory baginv = getBagInventory(ist);
			ItemSeedBag.SpiralSearch search = new ItemSeedBag.SpiralSearch(
					new WorldCoord(x, y, z), 5);
			
			for (boolean st = false; search.again(); search.step()) {
				Block soil = world.getBlock(search.point.x, search.point.y, search.point.z);
				if (soil == null) {
					if (!st) {
						break;
					}
				} else {
					ItemStack plantstk = getPlant(baginv);
					if (plantstk == null
							|| !(plantstk.getItem() instanceof IPlantable)) {
						break;
					}
					
					IPlantable plant = (IPlantable) plantstk.getItem();
					if (soil != null
							&& soil.canSustainPlant(world, search.point.x,
									search.point.y, search.point.z,
									ForgeDirection.UP, plant)) {
						if (!world.isAirBlock(search.point.x, search.point.y + 1,
										search.point.z)) {
							if (!st) {
								break;
							}
						} else {
							st = true;
							world.setBlock(search.point.x,
									search.point.y + 1, search.point.z, plant
											.getPlant(world, search.point.x,
													search.point.y + 1,
													search.point.z), plant
											.getPlantMetadata(world,
													search.point.x,
													search.point.y + 1,
													search.point.z), 3);
							if (!player.capabilities.isCreativeMode) {
								decrPlant(baginv);
							}
						}
					} else if (!st) {
						break;
					}
				}
			}
			
			return true;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List lines, boolean par4) {
		if (ist.stackTagCompound != null && ist.getItemDamage() != 0) {
			IInventory baginv = getBagInventory(ist);
			
			for (int i = 0; i < baginv.getSizeInventory(); ++i) {
				ItemStack is2 = baginv.getStackInSlot(i);
				if (is2 != null && is2.stackSize != 0) {
					lines.add(StatCollector.translateToLocal("item."+is2.getItem().getUnlocalizedName(is2)+".name"));
					return;
				}
			}
			
		}
	}
	
	public static class InventorySeedBag implements IInventory {
		
		ItemStack bagitem;
		ItemStack[] items;
		
		InventorySeedBag(ItemStack ist) {
			this.bagitem = ist;
			this.unpackInventory();
		}
		
		void unpackInventory() {
			this.items = new ItemStack[9];
			if (this.bagitem.stackTagCompound != null) {
				NBTTagList list = this.bagitem.stackTagCompound.getTagList("contents", 0); //TODO: DIDN'T UNDERSTAND THIS NUMBER
				
				for (int i = 0; i < list.tagCount(); ++i) {
					NBTTagCompound item = (NBTTagCompound) list.getCompoundTagAt(i); //TODO:
					byte slt = item.getByte("Slot");
					if (slt < 9) {
						this.items[slt] = ItemStack.loadItemStackFromNBT(item);
					}
				}
				
			}
		}
		
		void packInventory() {
			if (this.bagitem.stackTagCompound == null) {
				this.bagitem.setTagCompound(new NBTTagCompound());
			}
			
			int itc = 0;
			NBTTagList contents = new NBTTagList();
			
			for (int i = 0; i < 9; ++i) {
				if (this.items[i] != null) {
					itc += this.items[i].stackSize;
					NBTTagCompound cpd = new NBTTagCompound();
					this.items[i].writeToNBT(cpd);
					cpd.setByte("Slot", (byte) i);
					contents.appendTag(cpd);
				}
			}
			
			this.bagitem.stackTagCompound.setTag("contents", contents);
			this.bagitem.setItemDamage(itc == 0 ? 0 : 577 - itc);
		}
		
		@Override
		public int getSizeInventory() {
			return 9;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot) {
			return this.items[slot];
		}
		
		@Override
		public ItemStack decrStackSize(int slot, int num) {
			if (this.items[slot] == null) {
				return null;
			} else {
				ItemStack tr;
				if (this.items[slot].stackSize <= num) {
					tr = this.items[slot];
					this.items[slot] = null;
					this.markDirty();
					return tr;
				} else {
					tr = this.items[slot].splitStack(num);
					if (this.items[slot].stackSize == 0) {
						this.items[slot] = null;
					}
					
					this.markDirty();
					return tr;
				}
			}
		}
		
		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			if (this.items[slot] == null) {
				return null;
			} else {
				ItemStack tr = this.items[slot];
				this.items[slot] = null;
				return tr;
			}
		}
		
		@Override
		public void setInventorySlotContents(int slot, ItemStack ist) {
			this.items[slot] = ist;
			if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
				ist.stackSize = this.getInventoryStackLimit();
			}
			
			this.markDirty();
		}
		
		@Override
		public String getInventoryName() {
			return "Seed Bag";
		}
		
		@Override
		public int getInventoryStackLimit() {
			return 64;
		}
		
		@Override
		public void markDirty() {
			this.packInventory();
		}
		
		@Override
		public boolean isUseableByPlayer(EntityPlayer pl) {
			return true;
		}
		
		@Override
		public void openInventory() {
		}
		
		@Override
		public void closeInventory() {
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
			return false;
		}
	}
	
	public static class SpiralSearch {
		
		int curs;
		int rem;
		int ln;
		int steps;
		public WorldCoord point;
		
		public SpiralSearch(WorldCoord start, int size) {
			this.point = start;
			this.curs = 0;
			this.rem = 1;
			this.ln = 1;
			this.steps = size * size;
		}
		
		public boolean again() {
			return this.steps > 0;
		}
		
		public boolean step() {
			if (--this.steps == 0) {
				return false;
			} else {
				--this.rem;
				switch (this.curs) {
					case 0:
						this.point.step(2);
						break;
					case 1:
						this.point.step(4);
						break;
					case 2:
						this.point.step(3);
						break;
					default:
						this.point.step(5);
				}
				
				if (this.rem > 0) {
					return true;
				} else {
					this.curs = this.curs + 1 & 3;
					this.rem = this.ln;
					if ((this.curs & 1) > 0) {
						++this.ln;
					}
					
					return true;
				}
			}
		}
	}
}
