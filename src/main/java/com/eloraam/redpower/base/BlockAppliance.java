package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;
import com.eloraam.redpower.machine.TileBlueFurnace;
import com.eloraam.redpower.machine.TileBufferChest;
import com.eloraam.redpower.machine.TileChargingBench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAppliance extends BlockExtended {
	
	IIcon vertFurnaceIcon;
	IIcon sideFurnaceIcon;
	IIcon frontFurnaceIcon;
	IIcon frontFurnaceOnIcon;
	
	IIcon topBenchIcon;
	IIcon bottomBenchIcon;
	IIcon frontBenchIcon;
	IIcon sideBenchIcon;
	
	IIcon topBTFurnaceIcon;
	IIcon sideBTFurnaceIcon;
	IIcon frontBTFurnaceIcon;
	IIcon frontBTFurnaceOnIcon;
	
	IIcon topBTAFurnaceIcon;
	IIcon sideBTAFurnaceIcon;
	IIcon frontBTAFurnaceIcon;
	IIcon frontBTAFurnaceOnIcon;
	
	IIcon topBTChargerIcon;
	IIcon topBTChargerOnIcon;
	IIcon bottomBTChargerIcon;
	IIcon sideBTChargerIcon;
	IIcon sideBTChargerOnIcon;
	IIcon[] frontBTChargerIcon = new IIcon[6];
	IIcon[] frontBTChargerPoweredIcon = new IIcon[6];
	IIcon[] frontBTChargerActiveIcon = new IIcon[6];
	
	IIcon topBufferIcon;
	IIcon bottomBufferIcon;
	IIcon sideBufferIcon;
	
	public BlockAppliance() {
		super(Material.rock);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeExtraTabs.tabMachine);
		this.setBlockTextureName("stone");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
		this.vertFurnaceIcon = reg.registerIcon("rpbase:blockAlloyFurnaceVert");
		this.sideFurnaceIcon = reg.registerIcon("rpbase:blockAlloyFurnaceSide");
		this.frontFurnaceIcon = reg.registerIcon("rpbase:blockAlloyFurnaceFront");
		this.frontFurnaceOnIcon = reg.registerIcon("rpbase:blockAlloyFurnaceFrontOn");
		
		this.topBenchIcon = reg.registerIcon("rpbase:blockProjectTableTop");
		this.bottomBenchIcon = reg.registerIcon("rpbase:blockProjectTableBottom");
		this.frontBenchIcon = reg.registerIcon("rpbase:blockProjectTableFront");
		this.sideBenchIcon = reg.registerIcon("rpbase:blockProjectTableSide");
		
		this.topBTFurnaceIcon = reg.registerIcon("rpmachine:blockBTFurnaceTop");
		this.sideBTFurnaceIcon = reg.registerIcon("rpmachine:blockBTFurnaceSide");
		this.frontBTFurnaceIcon = reg.registerIcon("rpmachine:blockBTFurnaceFront");
		this.frontBTFurnaceOnIcon = reg.registerIcon("rpmachine:blockBTFurnaceFrontOn");
		
		this.topBTAFurnaceIcon = reg.registerIcon("rpmachine:blockBTAFurnaceTop");
		this.sideBTAFurnaceIcon = reg.registerIcon("rpmachine:blockBTAFurnaceSide");
		this.frontBTAFurnaceIcon = reg.registerIcon("rpmachine:blockBTAFurnaceFront");
		this.frontBTAFurnaceOnIcon = reg.registerIcon("rpmachine:blockBTAFurnaceFrontOn");
		
		this.topBTChargerIcon = reg.registerIcon("rpmachine:blockBTChargerTop");
		this.topBTChargerOnIcon = reg.registerIcon("rpmachine:blockBTChargerTopOn");
		this.bottomBTChargerIcon = reg.registerIcon("rpmachine:blockBTChargerBottom");
		this.sideBTChargerIcon = reg.registerIcon("rpmachine:blockBTChargerSide");
		this.sideBTChargerOnIcon = reg.registerIcon("rpmachine:blockBTChargerSideOn");
		for(int i = 0; i < 6; i ++) {
			this.frontBTChargerIcon[i] = reg.registerIcon("rpmachine:blockBTChargerFront"+i);
		}
		for(int i = 0; i < 6; i ++) {
			this.frontBTChargerPoweredIcon[i] = reg.registerIcon("rpmachine:blockBTChargerFrontPowered"+i);
		}
		for(int i = 0; i < 6; i ++) {
			this.frontBTChargerActiveIcon[i] = reg.registerIcon("rpmachine:blockBTChargerFrontActive"+i);
		}
		
		this.topBufferIcon = reg.registerIcon("rpmachine:blockBufferTop");
		this.bottomBufferIcon = reg.registerIcon("rpmachine:blockBufferBottom");
		this.sideBufferIcon = reg.registerIcon("rpmachine:blockBufferSide");
		
        this.blockIcon = reg.registerIcon(this.getTextureName());
    }
	
	@SuppressWarnings("unused")
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileAppliance tile = (TileAppliance)CoreLib.getTileEntity(world, x, y, z, TileAppliance.class);
		if(tile != null) {
			int facing = CoreLib.getFacing(tile.Rotation);
			switch(meta) {
				case 0: {
					if(tile instanceof TileAlloyFurnace) {
						TileAlloyFurnace furnace = (TileAlloyFurnace)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.vertFurnaceIcon;
							case DOWN:
								return this.vertFurnaceIcon;
							default:
								return side == facing ? (furnace.Active ? this.frontFurnaceOnIcon : this.frontFurnaceIcon) : this.sideFurnaceIcon;
						}
					}
				}
				case 1: { //BT BASE FURNACE
					if(tile instanceof TileBlueFurnace) {
						TileBlueFurnace furnace = (TileBlueFurnace)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topBTFurnaceIcon;
							case DOWN:
								return RedPowerMachine.blockMachine.bottomEletroIcon;
							default:
								return side == facing ? (furnace.Active ? this.frontBTFurnaceOnIcon : this.frontBTFurnaceIcon) : this.sideBTFurnaceIcon; 
						}
					}
				}
				case 2: { //BUFFER CHEST
					if(tile instanceof TileBufferChest) {
						TileBufferChest bfchest = (TileBufferChest)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topBufferIcon;
							case DOWN:
								return this.bottomBufferIcon;
							default:
								return this.sideBufferIcon;	
						}
					}
				}
				case 3: {
					if(tile instanceof TileAdvBench) {
						TileAdvBench bench = (TileAdvBench)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topBenchIcon;
							case DOWN:
								return this.bottomBenchIcon;
							default:
								return side == facing ? this.frontBenchIcon : this.sideBenchIcon; 
						}
					}
				}
				case 4: { //BT ALLOY FURNACE
					if(tile instanceof TileBlueAlloyFurnace) {
						TileBlueAlloyFurnace furnace = (TileBlueAlloyFurnace)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return this.topBTAFurnaceIcon;
							case DOWN:
								return RedPowerMachine.blockMachine.bottomEletroIcon;
							default:
								return side == facing ? (furnace.Active ? this.frontBTAFurnaceOnIcon : this.frontBTAFurnaceIcon) : this.sideBTAFurnaceIcon; 
						}
					}
				}
				case 5: {
					if(tile instanceof TileChargingBench) {
						TileChargingBench charger = (TileChargingBench)tile;
						switch(ForgeDirection.getOrientation(side)) {
							case UP:
								return charger.Active ? this.topBTChargerOnIcon : this.topBTChargerIcon;
							case DOWN:
								return this.bottomBTChargerIcon;
							default: {
								boolean a = charger.Active;
								boolean p = charger.Powered;
								int s = charger.getStorageForRender();
								IIcon result = (side == facing ? this.frontBTChargerIcon[s] : this.sideBTChargerIcon);
								if(p && side == facing) {
									result = this.frontBTChargerPoweredIcon[s];
								}
								if(a && side == facing) {
									result = this.frontBTChargerActiveIcon[s];
								}
								return result;
							}
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
			case 0: {
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.vertFurnaceIcon;
					case DOWN:
						return this.vertFurnaceIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontFurnaceIcon : this.sideFurnaceIcon;
				}
			}
			case 1: { //BT BASE FURNACE
				switch (ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBTFurnaceIcon;
					case DOWN:
						return RedPowerMachine.blockMachine.bottomEletroIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontBTFurnaceIcon : this.sideBTFurnaceIcon;
				}
			}
			case 2: {
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBufferIcon;
					case DOWN:
						return this.bottomBufferIcon;
					default:
						return this.sideBufferIcon;	
				}
			}
			case 3: {
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBenchIcon;
					case DOWN:
						return this.bottomBenchIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontBenchIcon : this.sideBenchIcon; 
				}
			}
			case 4: { //BT ALLOY FURNACE
				switch (ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBTAFurnaceIcon;
					case DOWN:
						return RedPowerMachine.blockMachine.bottomEletroIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontBTAFurnaceIcon : this.sideBTAFurnaceIcon;
				}
			}
			case 5: {
				switch (ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topBTChargerIcon;
					case DOWN:
						return this.bottomBTChargerIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontBTChargerIcon[0] : this.sideBTChargerIcon;
				}
			}
		}
        return this.blockIcon;
    }
	
	@Override
	public int getLightValue(IBlockAccess iba, int i, int j, int k) {
		TileAppliance taf = (TileAppliance) CoreLib.getTileEntity(iba, i, j, k, TileAppliance.class);
		return taf == null ? super.getLightValue(iba, i, j, k) : taf.getLightValue();
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
	
	@Override
	public int damageDropped(int i) {
		return i;
	}
}
