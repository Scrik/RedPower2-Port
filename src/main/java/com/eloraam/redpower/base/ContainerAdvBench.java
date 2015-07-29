package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.ContainerAdvBench;
import com.eloraam.redpower.base.InventorySubCraft;
import com.eloraam.redpower.base.SlotCraftRefill;
import com.eloraam.redpower.base.TileAdvBench;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.network.IHandleGuiEvent;
import com.eloraam.redpower.network.PacketGuiEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ContainerAdvBench extends Container implements IHandleGuiEvent {
	
	SlotCraftRefill slotCraft;
	private TileAdvBench tileAdvBench;
	public InventorySubCraft craftMatrix;
	public IInventory craftResult;
	public InventoryCrafting fakeInv;
	public int satisfyMask;
	
	public ContainerAdvBench(InventoryPlayer inv, TileAdvBench td) {
		this.tileAdvBench = td;
		this.craftMatrix = new InventorySubCraft(this, td);
		this.craftResult = new InventoryCraftResult();
		
		int i;
		int j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3,
						48 + j * 18, 18 + i * 18));
			}
		}
		
		this.addSlotToContainer(new ContainerAdvBench.SlotPlan(
				new ContainerAdvBench.InventorySubUpdate(td, 9, 1), 0, 17, 36));
		this.slotCraft = new SlotCraftRefill(inv.player, this.craftMatrix,
				this.craftResult, td, this, 0, 143, 36);
		this.addSlotToContainer(this.slotCraft);
		ContainerAdvBench.InventorySubUpdate ingrid = new ContainerAdvBench.InventorySubUpdate(
				td, 10, 18);
		
		for (i = 0; i < 2; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(ingrid, j + i * 9, 8 + j * 18,
						90 + i * 18));
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + i * 9 + 9,
						8 + j * 18, 140 + i * 18));
			}
		}
		
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 198));
		}
		
		this.fakeInv = new InventoryCrafting(
				new ContainerAdvBench.ContainerNull(), 3, 3);
		this.onCraftMatrixChanged(this.craftMatrix);
	}
	
	@Override
	public void putStackInSlot(int num, ItemStack ist) {
		super.putStackInSlot(num, ist);
	}
	
	public static ItemStack[] getShadowItems(ItemStack ist) {
		if (ist.stackTagCompound == null) {
			return null;
		} else {
			NBTTagList require = ist.stackTagCompound.getTagList("requires", 10); // TODO:

			if (require == null) {
				return null;
			} else {
				ItemStack[] tr = new ItemStack[9];
				
				for (int i = 0; i < require.tagCount(); ++i) {
					NBTTagCompound item = (NBTTagCompound) require
							.getCompoundTagAt(i);
					ItemStack is2 = ItemStack.loadItemStackFromNBT(item);
					byte sl = item.getByte("Slot");
					if (sl >= 0 && sl < 9) {
						tr[sl] = is2;
					}
				}
				
				return tr;
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tileAdvBench.isUseableByPlayer(player);
	}
	
	public ItemStack[] getPlanItems() {
		ItemStack plan = this.tileAdvBench.getStackInSlot(9);
		return plan == null ? null : getShadowItems(plan);
	}
	
	public int getSatisfyMask() {
		ItemStack plan = this.tileAdvBench.getStackInSlot(9);
		ItemStack[] items = null;
		if (plan != null) {
			items = getShadowItems(plan);
		}
		
		int bits = 0;
		
		int i;
		ItemStack test;
		for (i = 0; i < 9; ++i) {
			test = this.tileAdvBench.getStackInSlot(i);
			if (test != null) {
				bits |= 1 << i;
			} else if (items == null || items[i] == null) {
				bits |= 1 << i;
			}
		}
		
		if (bits == 511) {
			return 511;
		} else {
			for (i = 0; i < 18; ++i) {
				test = this.tileAdvBench.getStackInSlot(10 + i);
				if (test != null && test.stackSize != 0) {
					int sc = test.stackSize;
					
					for (int j = 0; j < 9; ++j) {
						if ((bits & 1 << j) <= 0) {
							ItemStack st = this.tileAdvBench.getStackInSlot(j);
							if (st == null) {
								st = items[j];
								if (st != null
										&& CoreLib.matchItemStackOre(st, test)) {
									bits |= 1 << j;
									--sc;
									if (sc == 0) {
										break;
									}
								}
							}
						}
					}
				}
			}
			
			return bits;
		}
	}
	
	private int findMatch(ItemStack a) {
		for (int i = 0; i < 18; ++i) {
			ItemStack test = this.tileAdvBench.getStackInSlot(10 + i);
			if (test != null && test.stackSize != 0
					&& CoreLib.matchItemStackOre(a, test)) {
				return 10 + i;
			}
		}
		
		return -1;
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory iinventory) {
		ItemStack plan = this.tileAdvBench.getStackInSlot(9);
		ItemStack[] items = null;
		if (plan != null) {
			items = getShadowItems(plan);
		}
		
		for (int i = 0; i < 9; ++i) {
			ItemStack tos = this.tileAdvBench.getStackInSlot(i);
			if (tos == null && items != null && items[i] != null) {
				int j = this.findMatch(items[i]);
				if (j > 0) {
					tos = this.tileAdvBench.getStackInSlot(j);
				}
			}
			
			this.fakeInv.setInventorySlotContents(i, tos);
		}
		
		this.satisfyMask = this.getSatisfyMask();
		if (this.satisfyMask == 511) {
			this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.fakeInv, this.tileAdvBench.getWorldObj()));
		} else {
			this.craftResult.setInventorySlotContents(0, (ItemStack) null);
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) super.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i == 10) {
				this.mergeCrafting(player, slot, 29, 65);
				return null;
			}
			
			if (i < 9) {
				if (!this.mergeItemStack(itemstack1, 11, 29, false)) {
					return null;
				}
			} else if (i < 29) {
				if (!this.mergeItemStack(itemstack1, 29, 65, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 11, 29, false)) {
				return null;
			}
			
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			
			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			
			slot.onPickupFromSlot(player, itemstack1);
		}
		
		return itemstack;
	}
	
	protected boolean canFit(ItemStack ist, int st, int ed) {
		int ms = 0;
		
		for (int i = st; i < ed; ++i) {
			Slot slot = (Slot) super.inventorySlots.get(i);
			ItemStack is2 = slot.getStack();
			if (is2 == null) {
				return true;
			}
			
			if (CoreLib.compareItemStack(is2, ist) == 0) {
				ms += is2.getMaxStackSize() - is2.stackSize;
				if (ms >= ist.stackSize) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void fitItem(ItemStack ist, int st, int ed) {
		int i;
		Slot slot;
		ItemStack is2;
		if (ist.isStackable()) {
			for (i = st; i < ed; ++i) {
				slot = (Slot) super.inventorySlots.get(i);
				is2 = slot.getStack();
				if (is2 != null && CoreLib.compareItemStack(is2, ist) == 0) {
					int n = Math.min(ist.stackSize, ist.getMaxStackSize()
							- is2.stackSize);
					if (n != 0) {
						ist.stackSize -= n;
						is2.stackSize += n;
						slot.onSlotChanged();
						if (ist.stackSize == 0) {
							return;
						}
					}
				}
			}
		}
		
		for (i = st; i < ed; ++i) {
			slot = (Slot) super.inventorySlots.get(i);
			is2 = slot.getStack();
			if (is2 == null) {
				slot.putStack(ist);
				slot.onSlotChanged();
				return;
			}
		}
		
	}
	
	protected void mergeCrafting(EntityPlayer player, Slot cslot, int st, int ed) {
		int cc = 0;
		ItemStack ist = cslot.getStack();
		if (ist != null && ist.stackSize != 0) {
			ItemStack craftas = ist.copy();
			int mss = craftas.getMaxStackSize();
			if (mss == 1) {
				mss = 16;
			}
			
			do {
				if (!this.canFit(ist, st, ed)) {
					return;
				}
				
				cc += ist.stackSize;
				this.fitItem(ist, st, ed);
				cslot.onPickupFromSlot(player, ist);
				if (cc >= mss) {
					return;
				}
				
				if (this.slotCraft.isLastUse()) {
					return;
				}
				
				ist = cslot.getStack();
				if (ist == null || ist.stackSize == 0) {
					return;
				}
			} while (CoreLib.compareItemStack(ist, craftas) == 0);
			
		}
	}
	
	@Override
	public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
		if (this.tileAdvBench.getWorldObj() != null&& !CoreLib.isClient(this.tileAdvBench.getWorldObj())) {
			try {
				if (message.eventId == 1) {
					ItemStack blank = this.tileAdvBench.getStackInSlot(9);
					if (blank != null && blank.getItem() == RedPowerBase.itemPlanBlank) {
						ItemStack plan = new ItemStack(RedPowerBase.itemPlanFull);
						plan.stackTagCompound = new NBTTagCompound();
						NBTTagCompound result = new NBTTagCompound();
						this.craftResult.getStackInSlot(0).writeToNBT(result);
						plan.stackTagCompound.setTag("result", result);
						NBTTagList requires = new NBTTagList();
						
						for (int i = 0; i < 9; ++i) {
							ItemStack is1 = this.craftMatrix.getStackInSlot(i);
							if (is1 != null) {
								ItemStack ist = CoreLib.copyStack(is1, 1);
								NBTTagCompound item = new NBTTagCompound();
								ist.writeToNBT(item);
								item.setByte("Slot", (byte) i);
								requires.appendTag(item);
							}
						}
						plan.stackTagCompound.setTag("requires", requires);
						this.tileAdvBench.setInventorySlotContents(9, plan);
					}
				}
			} catch(Throwable thr) {}
		}
	}
	
	public static class ContainerNull extends Container {
		
		@Override
		public boolean canInteractWith(EntityPlayer var1) {
			return false;
		}
		
		@Override
		public void onCraftMatrixChanged(IInventory inv) {
		}
	}
	
	public class InventorySubUpdate implements IInventory {
		int size;
		int start;
		IInventory parent;
		
		public InventorySubUpdate(IInventory par, int st, int sz) {
			this.parent = par;
			this.start = st;
			this.size = sz;
		}
		
		@Override
		public int getSizeInventory() {
			return this.size;
		}
		
		@Override
		public ItemStack getStackInSlot(int idx) {
			return this.parent.getStackInSlot(idx + this.start);
		}
		
		@Override
		public ItemStack decrStackSize(int idx, int num) {
			ItemStack tr = this.parent.decrStackSize(idx + this.start, num);
			if (tr != null) {
				ContainerAdvBench.this.onCraftMatrixChanged(this);
			}
			
			return tr;
		}
		
		@Override
		public ItemStack getStackInSlotOnClosing(int idx) {
			return this.parent.getStackInSlotOnClosing(idx + this.start);
		}
		
		@Override
		public void setInventorySlotContents(int idx, ItemStack ist) {
			this.parent.setInventorySlotContents(idx + this.start, ist);
			ContainerAdvBench.this.onCraftMatrixChanged(this);
		}
		
		@Override
		public String getInventoryName() {
			return this.parent.getInventoryName();
		}
		
		@Override
		public int getInventoryStackLimit() {
			return this.parent.getInventoryStackLimit();
		}
		
		@Override
		public void markDirty() {
			ContainerAdvBench.this.onCraftMatrixChanged(this);
			this.parent.markDirty();
		}
		
		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			return false;
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
			return true;
		}
	}
	
	public static class SlotPlan extends Slot {
		public SlotPlan(IInventory inv, int i, int j, int k) {
			super(inv, i, j, k);
		}
		
		@Override
		public boolean isItemValid(ItemStack ist) {
			return ist.getItem() == RedPowerBase.itemPlanBlank
					|| ist.getItem() == RedPowerBase.itemPlanFull;
		}
		
		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}
}
