package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.machine.TileIgniter;
import com.eloraam.redpower.machine.TileMachine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMachine extends BlockExtended {
	
	public IIcon bottomEletroIcon;
	
	IIcon topBatteryIcon;
	IIcon[] sideBatteryIcon = new IIcon[9];
	
	public BlockMachine() {
		super(Material.rock);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
		this.setBlockTextureName("redstone_block");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
		this.topBatteryIcon = reg.registerIcon("rpmachine:blockBatteryVert");
		for(int i = 0; i < 9; i ++) {
			this.sideBatteryIcon[i] = reg.registerIcon("rpmachine:blockBatterySide"+i);
		}
		this.bottomEletroIcon = reg.registerIcon("rpmachine:blockElectroBottom");
		
        this.blockIcon = reg.registerIcon(this.getTextureName());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileExtended tile = (TileExtended)CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
		if(tile != null) {
			switch(meta) {
				case 6: { //BATBOX
					if(tile instanceof TileBatteryBox) {
						TileBatteryBox battery = (TileBatteryBox)tile;
						switch (ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topBatteryIcon;
							case DOWN:
								return this.bottomEletroIcon;
							default:
								return this.sideBatteryIcon[battery.getStorageForRender()];
						}
					}
				}
			}
		}
		return this.blockIcon;
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
		switch(meta) {
			case 6: { //BATBOX
				switch (ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBatteryIcon;
					case DOWN:
						return this.bottomEletroIcon;
					default:
						return this.sideBatteryIcon[0];
				}
			}
		}
        return this.blockIcon;
    }
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	@Override
	public boolean isACube() {
		return true;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}
	
	public boolean isBlockNormalCube(World world, int i, int j, int k) {
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess iba, int i, int j, int k, ForgeDirection side) {
		return true;
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int i, int j, int k, int l) {
		TileMachine tm = (TileMachine) CoreLib.getTileEntity(iba, i, j, k, TileMachine.class);
		return tm == null ? 0 : tm.isPoweringTo(l) ? 1 : 0; // TODO: Something wrong...
	}
	
	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection face) {
		int md = world.getBlockMetadata(x, y, z);
		if (md != 12) {
			return false;
		} else {
			TileIgniter tig = (TileIgniter) CoreLib.getTileEntity(world, x, y, z, TileIgniter.class);
			return tig == null ? false : tig.isOnFire(face);
		}
	}
}
