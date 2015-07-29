package com.eloraam.redpower.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemPlan extends Item {
	
	public ItemPlan() {
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("planFull");
		this.setTextureName("rpbase:itemPlanFull");
		this.setMaxStackSize(1);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName(ItemStack ist) {
		if (ist.stackTagCompound == null) {
			return super.getItemStackDisplayName(ist);
		} else if (!ist.stackTagCompound.hasKey("result")) {
			return super.getItemStackDisplayName(ist);
		} else {
			NBTTagCompound res = ist.stackTagCompound.getCompoundTag("result");
			ItemStack result = ItemStack.loadItemStackFromNBT(res);
			return result.getItem().getItemStackDisplayName(result) + " Plan";
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List lines, boolean par4) {
		if (ist.stackTagCompound != null) {
			NBTTagList require = ist.stackTagCompound.getTagList("requires", 10); //TODO: Remember this
			if (require != null) {
				HashMap<HashMap<Item, Integer>, Integer> counts = new HashMap<HashMap<Item, Integer>, Integer>();
				
				for (int i = 0; i < require.tagCount(); ++i) {
					NBTTagCompound kv = (NBTTagCompound)require.getCompoundTagAt(i);
					ItemStack li = ItemStack.loadItemStackFromNBT(kv);
					HashMap<Item, Integer> i2d = new HashMap<Item, Integer>();
					i2d.put(li.getItem(), li.getItemDamage());
					Integer lc = (Integer)counts.get(i2d);
					if (lc == null) {
						lc = Integer.valueOf(0);
					}
					counts.put(i2d, Integer.valueOf(lc.intValue() + 1));
				}
				
				Iterator<Entry<HashMap<Item, Integer>, Integer>> iter = counts.entrySet().iterator();
				
				while(iter.hasNext()) {
					Entry<HashMap<Item, Integer>, Integer> entry = iter.next();
					HashMap<Item, Integer> keySet = entry.getKey();
					ItemStack itemStack = new ItemStack(keySet.keySet().iterator().next(), 1, keySet.values().iterator().next()); //TODO: Maybe very very bad
					lines.add(entry.getValue() + " x " + itemStack.getItem().getItemStackDisplayName(itemStack));
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack ist) {
		return EnumRarity.rare;
	}
	
	@Override
	public boolean getShareTag() {
		return true;
	}
}
