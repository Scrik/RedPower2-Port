package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.machine.TileMachinePanel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMachinePanel extends BlockMultipart {
	
	public IIcon topSolarPanelIcon;
	public IIcon sideSolarPanelIcon;
	
	public IIcon sideGrateIcon;
	public IIcon mossySideGrateIcon;
	public IIcon backGrateIcon;
	public IIcon emptyBackGrateIcon;
	
	public BlockMachinePanel() {
		super(Material.rock);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
		this.topSolarPanelIcon = reg.registerIcon("rpmachine:blockSolarPanelTop");
		this.sideSolarPanelIcon = reg.registerIcon("rpmachine:blockSolarPanelSide");
		
		this.sideGrateIcon = reg.registerIcon("rpmachine:blockGrateSide");
		this.mossySideGrateIcon = reg.registerIcon("rpmachine:blockGrateMossySide");
		this.backGrateIcon = reg.registerIcon("rpmachine:blockGrateBack");
		this.emptyBackGrateIcon = reg.registerIcon("rpmachine:blockGrateEmptyBack");
		
        this.blockIcon = reg.registerIcon(this.getTextureName());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileMachinePanel tile = (TileMachinePanel)CoreLib.getTileEntity(world, x, y, z, TileMachinePanel.class);
		if(tile != null) {
			switch(meta) {
				case 0: { //SOLAR PANEL
					if(tile instanceof TileSolarPanel) {
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topSolarPanelIcon;
							case DOWN: 
								return this.sideSolarPanelIcon;
							default:
								return RedPowerMachine.blockMachine.bottomEletroIcon;
						}
					}
				}
				case 3: { //GRATE
					if(tile instanceof TileGrate) {
						switch(ForgeDirection.getOrientation(side)) {
							case NORTH:
								return this.backGrateIcon;
							case SOUTH:
								return this.emptyBackGrateIcon;
							case WEST:
								return this.mossySideGrateIcon;
							default:
								return this.sideGrateIcon;
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
			case 0: { //SOLAR PANEL
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topSolarPanelIcon;
					case DOWN: 
						return this.sideSolarPanelIcon;
					default:
						return RedPowerMachine.blockMachine.bottomEletroIcon;
				}
			}
			case 3: { //GRATE
				switch(ForgeDirection.getOrientation(side)) {
					case NORTH:
						return this.backGrateIcon;
					case WEST:
						return this.mossySideGrateIcon;
					default:
						return this.sideGrateIcon;
				}
			}
		}
        return this.blockIcon;
    }
	
	@Override
	public int getLightValue(IBlockAccess iba, int i, int j, int k) {
		TileMachinePanel tmp = (TileMachinePanel) CoreLib.getTileEntity(iba, i, j, k, TileMachinePanel.class);
		return tmp == null ? 0 : tmp.getLightValue();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isACube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
}
