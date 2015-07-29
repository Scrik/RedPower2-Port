package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.TileExtended;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockExtended extends BlockContainer {
	
	@SuppressWarnings("rawtypes")
	private Class[] tileEntityMap = new Class[16];
	
	public BlockExtended(Material m) {
		super(m);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	public boolean isACube() {
		return false;
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
	
	public float getHardness() {
		return super.blockHardness;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList getDrops(World world, int i, int j, int k, int md, int fortune) {
		ArrayList ist = new ArrayList();
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		if (tl == null) {
			return ist;
		} else {
			tl.addHarvestContents(ist);
			return ist;
		}
	}
	
	@Override
	public Item getItemDropped(int i, Random random, int j) { //TODO: VERY VERY WRONG MAYBE
		return Item.getItemFromBlock(Blocks.air);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int l) {
		this.removedByPlayer(world, player, x, y, z);
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int i, int j, int k) {
		if (CoreLib.isClient(world)) {
			return true;
		} else {
			Block bl = world.getBlock(i, j, k);
			int md = world.getBlockMetadata(i, j, k);
			if (bl == null) {
				return false;
			} else {
				if (bl.canHarvestBlock(player, md) && !player.capabilities.isCreativeMode) {
					ArrayList<ItemStack> il = this.getDrops(world, i, j, k, md, EnchantmentHelper.getFortuneModifier(player));
					Iterator<ItemStack> iter = il.iterator();
					while (iter.hasNext()) {
						ItemStack it = (ItemStack) iter.next();
						CoreLib.dropItem(world, i, j, k, it);
					}
				}
				world.setBlockToAir(i, j, k);
				return true;
			}
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		if (tl == null) {
			world.setBlockToAir(i, j, k);
		} else {
			tl.onBlockNeighborChange(block);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase ent, ItemStack ist) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		//int side = MathHelper.floor_double((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int side = BlockPistonBase.determineOrientation(world, i, j, k, ent);
		
		if (tl != null) {
			tl.onBlockPlaced(ist, side, ent); //TODO: For a while
		}
	}
	
	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int md) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		if (tl != null) {
			tl.onBlockRemoval();
			super.breakBlock(world, i, j, k, block, md);
		}
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess iba, int i, int j, int k, int l) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(iba, i, j, k, TileExtended.class);
		return tl == null ? 0 : tl.isBlockStrongPoweringTo(l) ? 15 : 0; //TODO: I think that wrong
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int i, int j, int k, int l) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(iba, i, j, k, TileExtended.class);
		return tl == null ? 0 : tl.isBlockWeakPoweringTo(l) ? 1 : 0; //TODO: I think that wrong too
	}
	
	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float xp, float yp, float zp) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		return tl == null ? false : tl.onBlockActivated(player);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		if (tl != null) {
			tl.onEntityCollidedWithBlock(entity);
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		TileExtended tl = (TileExtended) CoreLib.getTileEntity(world, i, j, k, TileExtended.class);
		if (tl != null) {
			AxisAlignedBB bb = tl.getCollisionBoundingBox();
			if (bb != null) {
				return bb;
			}
		}
		return super.getCollisionBoundingBoxFromPool(world, i, j, k);
	}
	
	@Override
	public int getRenderType() {
		return RedPowerCore.customBlockModel;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		int md = world.getBlockMetadata(i, j, k);
		RenderCustomBlock rend = RenderLib.getRenderer(this, md);
		if (rend != null) {
			rend.randomDisplayTick(world, i, j, k, random);
		}
	}
	
	/*
	public TileEntity getBlockEntity() {
		return null;
	}*/
	
	@SuppressWarnings("rawtypes")
	public void addTileEntityMapping(int md, Class cl) {
		this.tileEntityMap[md] = cl;
	}
	
	public void setBlockName(int md, String name) {
		Item item = Item.getItemFromBlock(this);
		((ItemExtended) item).setMetaName(md, "tile." + name);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public TileEntity createNewTileEntity(World world, int md) {
		try {
			return (TileEntity)this.tileEntityMap[md].getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}
}
