package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.ICoverable;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemMicro extends ItemBlock {
	
	IMicroPlacement[] placers = new IMicroPlacement[256];
	
	public ItemMicro(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	private boolean useCover(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
		MovingObjectPosition pos = CoreLib.retraceBlock(world, player, i, j, k);
		if (pos == null) {
			return false;
		} else if (pos.typeOfHit != MovingObjectType.BLOCK) {
			return false;
		} else {
			pos = CoverLib.getPlacement(world, pos, ist.getItemDamage());
			if (pos == null) {
				return false;
			} else {
				if (world.canPlaceEntityOnSide(
						world.getBlock(pos.blockX, pos.blockY, pos.blockZ),
						pos.blockX, pos.blockY, pos.blockZ, false, l, player, ist)) {
					world.setBlock(pos.blockX, pos.blockY, pos.blockZ, RedPowerBase.blockMicro, 0, 3);
				}
				
				TileEntity te = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
				if (!(te instanceof ICoverable)) {
					return false;
				} else {
					ICoverable icv = (ICoverable) te;
					if (icv.tryAddCover(pos.subHit, CoverLib.damageToCoverValue(ist.getItemDamage()))) {
						--ist.stackSize;
						CoreLib.placeNoise(world, pos.blockX, pos.blockY, pos.blockZ, 
							CoverLib.getBlock(ist.getItemDamage() & 255));
						RedPowerLib.updateIndirectNeighbors(world, pos.blockX, pos.blockY, pos.blockZ, 
							RedPowerBase.blockMicro);
						world.markBlockForUpdate(pos.blockX, pos.blockY, pos.blockZ);
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack ist) {
		return true;
	}
	
	@Override
	public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		return player == null ? false : (player.isSneaking() ? false : this.itemUseShared(ist, player, world, i, j, k, l));
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, 
			World world, int i, int j, int k, int l, float xp, float yp, float zp) {
		return CoreLib.isClient(world) ? false : (!player.isSneaking() ? 
			false : this.itemUseShared(ist, player, world, i, j, k, l));
	}
	
	private boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
		int hb = ist.getItemDamage();
		//int lb = hb & 255;
		hb >>= 8;
		return hb != 0 && (hb < 16 || hb > 45) ? (this.placers[hb] == null ? false : this.placers[hb]
				.onPlaceMicro(ist, player, world, new WorldCoord(i, j, k), l)) : this
				.useCover(ist, player, world, i, j, k, l);
	}
	
	private String getMicroName(int hb) {
		switch (hb) {
			case 0:
				return "rpcover";
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			default:
				return null;
			case 16:
				return "rppanel";
			case 17:
				return "rpslab";
			case 18:
				return "rpcovc";
			case 19:
				return "rppanc";
			case 20:
				return "rpslabc";
			case 21:
				return "rpcovs";
			case 22:
				return "rppans";
			case 23:
				return "rpslabs";
			case 24:
				return "rphcover";
			case 25:
				return "rphpanel";
			case 26:
				return "rphslab";
			case 27:
				return "rpcov3";
			case 28:
				return "rpcov5";
			case 29:
				return "rpcov6";
			case 30:
				return "rpcov7";
			case 31:
				return "rphcov3";
			case 32:
				return "rphcov5";
			case 33:
				return "rphcov6";
			case 34:
				return "rphcov7";
			case 35:
				return "rpcov3c";
			case 36:
				return "rpcov5c";
			case 37:
				return "rpcov6c";
			case 38:
				return "rpcov7c";
			case 39:
				return "rpcov3s";
			case 40:
				return "rpcov5s";
			case 41:
				return "rpcov6s";
			case 42:
				return "rpcov7s";
			case 43:
				return "rppole1";
			case 44:
				return "rppole2";
			case 45:
				return "rppole3";
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		int hb = ist.getItemDamage();
		int lb = hb & 255;
		hb >>= 8;
		String stub = this.getMicroName(hb);
		String name;
		if (stub != null) {
			name = CoverLib.getName(lb);
			if (name == null) {
				throw new IndexOutOfBoundsException();
			} else {
				return "tile." + stub + "." + name;
			}
		} else if (this.placers[hb] == null) {
			throw new IndexOutOfBoundsException();
		} else {
			name = this.placers[hb].getMicroName(hb, lb);
			if (name == null) {
				throw new IndexOutOfBoundsException();
			} else {
				return name;
			}
		}
	}
	
	public void registerPlacement(int md, IMicroPlacement imp) {
		this.placers[md] = imp;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item id, CreativeTabs tab, List list) {
		int i;
		if (tab != CreativeExtraTabs.tabWires && tab != CreativeExtraTabs.tabMachine) {
			if (tab == CreativeExtraTabs.tabMicros) {
				String stub;
				for (i = 0; i < 255; ++i) {
					stub = CoverLib.getName(i);
					if (stub != null) {
						list.add(new ItemStack(RedPowerBase.blockMicro, 1, i));
					}
				}
				
				for (i = 1; i < 255; ++i) {
					stub = this.getMicroName(i);
					if (stub != null) {
						list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8));
					}
				}
				
				for (i = 1; i < 255; ++i) {
					stub = this.getMicroName(i);
					if (stub != null) {
						list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 2));
					}
				}
				
				for (i = 1; i < 255; ++i) {
					stub = this.getMicroName(i);
					if (stub != null) {
						list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 23));
					}
				}
				
				for (i = 1; i < 255; ++i) {
					stub = this.getMicroName(i);
					if (stub != null) {
						list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 26));
					}
				}
			}
		} else {
			for (i = 0; i < 255; ++i) {
				if (this.placers[i] != null) {
					this.placers[i].addCreativeItems(i, tab, list);
				}
			}
		}
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs() {
		return new CreativeTabs[] { CreativeExtraTabs.tabWires, CreativeExtraTabs.tabMicros, CreativeExtraTabs.tabMachine };
	}
}
