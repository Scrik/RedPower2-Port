package com.eloraam.redpower.control;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CreativeExtraTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPeripheral extends BlockExtended {
	
	public BlockPeripheral() {
		super(Material.rock);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
		this.setBlockTextureName("cobblestone");
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }
	
	@Override
	public boolean isACube() {
		return true;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		return true;
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
}
