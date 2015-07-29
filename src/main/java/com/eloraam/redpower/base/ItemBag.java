package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBag extends Item {
	
	private IIcon[] icons = new IIcon[16];
	
	public ItemBag() {
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("rpBag");
		this.setTextureName("rpbase:itemBag");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public static IInventory getBagInventory(ItemStack ist, EntityPlayer player) {
		return !(ist.getItem() instanceof ItemBag) ? null : new ItemBag.InventoryBag(ist, player);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registerer) {
		for(int i = 0; i < 16; i++) {
			this.icons[i] = registerer.registerIcon(this.getIconString() + i);
		}
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}
	
	@Override
	public IIcon getIconFromDamage(int i) {
		if(i >= icons.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return this.icons[i];
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
		if (CoreLib.isClient(world)) {
			return ist;
		} else if (player.isSneaking()) {
			return ist;
		} else {
			player.openGui(RedPowerBase.instance, 4, world, 0, 0, 0);
			return ist;
		}
	}
	
	public static class InventoryBag implements IInventory {
		ItemStack bagitem;
		ItemStack[] items;
		EntityPlayer player;
		
		InventoryBag(ItemStack ist, EntityPlayer host) {
			this.bagitem = ist;
			this.player = host;
			this.unpackInventory();
		}
		
		void unpackInventory() {
			this.items = new ItemStack[27];
			if (this.bagitem.stackTagCompound != null) {
				NBTTagList list = this.bagitem.stackTagCompound.getTagList("contents", 0); //TODO: Unknown number for me
				
				for (int i = 0; i < list.tagCount(); ++i) {
					NBTTagCompound item = (NBTTagCompound) list.getCompoundTagAt(i);
					byte slt = item.getByte("Slot");
					if (slt < 27) {
						this.items[slt] = ItemStack.loadItemStackFromNBT(item);
					}
				}
				
			}
		}
		
		void packInventory() {
			if (this.bagitem.stackTagCompound == null) {
				this.bagitem.setTagCompound(new NBTTagCompound());
			}
			
			NBTTagList contents = new NBTTagList();
			for (int i = 0; i < 27; ++i) {
				if (this.items[i] != null) {
					NBTTagCompound cpd = new NBTTagCompound();
					this.items[i].writeToNBT(cpd);
					cpd.setByte("Slot", (byte) i);
					contents.appendTag(cpd);
				}
			}
			this.bagitem.stackTagCompound.setTag("contents", contents);
		}
		
		@Override
		public int getSizeInventory() {
			return 27;
		}
		
		@Override
		public ItemStack getStackInSlot(int slot) {
			return this.items[slot];
		}
		
		@Override
		public ItemStack decrStackSize(int slot, int num) {
			if(this.bagitem == null) { //TODO: DUPE FIX
				((EntityPlayerMP)this.player).closeContainer();
				return null;
			}
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
			if(this.bagitem == null) { //TODO: DUPE FIX
				((EntityPlayerMP)this.player).closeContainer();
				return null;
			}
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
			if(this.bagitem == null) { //TODO: DUPE FIX
				((EntityPlayerMP)this.player).closeContainer();
				return;
			}
			this.items[slot] = ist;
			if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
				ist.stackSize = this.getInventoryStackLimit();
			}
			this.markDirty();
		}
		
		@Override
		public String getInventoryName() {
			return "Canvas Bag";
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
			return true; //Maybe not
		}

		@Override
		public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
			return true; //Maybe not
		}
	}
}
