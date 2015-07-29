package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RedPowerLib;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockLamp extends Block {
	
	public boolean lit;
	public boolean powered;
	public Block onBlock;
	public Block offBlock;
	
	public BlockLamp(boolean lit, boolean powered) {
		super(CoreLib.materialRedpower);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setHardness(0.5F);
		this.lit=lit;
		this.powered=powered;
	}
	
	@Override
	public boolean canRenderInPass(int n) {
		return true;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
	
	public boolean isACube() {
		return true;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
	
	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(RedPowerLighting.blockLampOff);
	}
	
	private void checkPowerState(World world, int i, int j, int k) {
		int md;
		if (!this.powered && /*RedPowerLib.isPowered(world, i, j, k, 16777215, 63)*/world.isBlockIndirectlyGettingPowered(i, j, k)) {
			md = world.getBlockMetadata(i, j, k);
			world.setBlock(i, j, k, this.onBlock, md, 3); //And notify
			world.markBlockForUpdate(i, j, k);
			System.out.println("KO1");
		} else if (this.powered && /*!RedPowerLib.isPowered(world, i, j, k, 16777215, 63)*/!world.isBlockIndirectlyGettingPowered(i, j, k)) {
			md = world.getBlockMetadata(i, j, k);
			world.setBlock(i, j, k, this.offBlock, md, 3); //And notify
			world.markBlockForUpdate(i, j, k);
			System.out.println("KO2");
		}
	}
	
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		this.checkPowerState(world, i, j, k);
	}
	
	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		this.checkPowerState(world, i, j, k);
	}
	
	@Override
	public int getRenderType() {
		return RedPowerCore.customBlockModel;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		Iterator<ItemStack> iter = itemList.iterator();
		while(iter.hasNext()) {
			ItemStack iStack = iter.next();
			if(Block.getBlockFromItem(iStack.getItem()) == this){
				itemList.remove(iStack);
			}
		}
	}
}
