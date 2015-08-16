package com.eloraam.redpower.control;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPeripheral extends BlockExtended {
	
	IIcon bottomPeripheralIcon;
	IIcon topPeripheralIcon;
	IIcon sidePeripheralIcon;
	IIcon backPeripheralIcon;
	
	IIcon frontCPUIcon;
	IIcon frontDisplayIcon;
	
	IIcon sideDiskDriveIcon;
	IIcon topDiskDriveIcon;
	IIcon frontDiskDriveIcon;
	IIcon frontDiskDriveFullIcon;
	IIcon frontDiskDriveOnIcon;
	
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
    public void registerBlockIcons(IIconRegister reg) {
		this.bottomPeripheralIcon = reg.registerIcon("rpcontrol:blockPeripheralBottom");
		this.topPeripheralIcon = reg.registerIcon("rpcontrol:blockPeripheralTop");
		this.sidePeripheralIcon = reg.registerIcon("rpcontrol:blockPeripheralSide");
		this.backPeripheralIcon = reg.registerIcon("rpcontrol:blockPeripheralBack");
		
		this.frontCPUIcon = reg.registerIcon("rpcontrol:blockCPUFront");
		this.frontDisplayIcon = reg.registerIcon("rpcontrol:blockDisplayFront");
		
		this.sideDiskDriveIcon = reg.registerIcon("rpcontrol:blockDiskDriveSide");
		this.topDiskDriveIcon = reg.registerIcon("rpcontrol:blockDiskDriveTop");
		this.frontDiskDriveIcon = reg.registerIcon("rpcontrol:blockDiskDriveFront");
		this.frontDiskDriveFullIcon = reg.registerIcon("rpcontrol:blockDiskDriveFrontFull");
		this.frontDiskDriveOnIcon = reg.registerIcon("rpcontrol:blockDiskDriveFrontOn");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
			case 0: { //DISPLAY
				TileDisplay display = (TileDisplay)CoreLib.getTileEntity(world, x, y, z, TileDisplay.class);
				if(display != null) {
					int facing = CoreLib.getFacing(display.Rotation);
					switch(ForgeDirection.getOrientation(side)) {
						case UP: 
							return this.topPeripheralIcon;
						case DOWN:
							return this.bottomPeripheralIcon;
						default:
							return side == facing ? this.frontDisplayIcon : (side == ForgeDirection.getOrientation(facing).getOpposite().ordinal() ? this.backPeripheralIcon : this.sidePeripheralIcon);
					}
				}
			}
			case 1: { //CPU
				TileCPU cpu = (TileCPU)CoreLib.getTileEntity(world, x, y, z, TileCPU.class);
				if(cpu != null) {
					int facing = CoreLib.getFacing(cpu.Rotation);
					switch(ForgeDirection.getOrientation(side)) {
						case UP: 
							return this.topPeripheralIcon;
						case DOWN:
							return this.bottomPeripheralIcon;
						default:
							return side == facing ? this.frontCPUIcon : (side == ForgeDirection.getOrientation(facing).getOpposite().ordinal() ? this.backPeripheralIcon : this.sidePeripheralIcon);
					}
				}
			}
			case 2: { //DISK DRIVE
				TileDiskDrive drive = (TileDiskDrive)CoreLib.getTileEntity(world, x, y, z, TileDiskDrive.class);
				if(drive != null) {
					int facing = CoreLib.getFacing(drive.Rotation);
					switch(ForgeDirection.getOrientation(side)) {
						case UP: 
							return this.topDiskDriveIcon;
						case DOWN:
							return this.bottomPeripheralIcon;
						default:
							return side == facing ? (drive.hasDisk ? (drive.Active ? this.frontDiskDriveOnIcon : this.frontDiskDriveFullIcon) : this.frontDiskDriveIcon) : (side == ForgeDirection.getOrientation(facing).getOpposite().ordinal() ? this.backPeripheralIcon : this.sideDiskDriveIcon);
					}
				}
			}
		}
		return this.blockIcon;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getIcon(int side, int meta) {
		switch(meta) {
			case 0: { //DISPLAY
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topPeripheralIcon;
					case DOWN:
						return this.bottomPeripheralIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontDisplayIcon : side == ForgeDirection.SOUTH.ordinal() ? this.backPeripheralIcon : this.sidePeripheralIcon;
				}
			}
			case 1: { //CPU
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topPeripheralIcon;
					case DOWN:
						return this.bottomPeripheralIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontCPUIcon : side == ForgeDirection.SOUTH.ordinal() ? this.backPeripheralIcon : this.sidePeripheralIcon;
				}
			}
			case 2: { //DISK DRIVE
				switch(ForgeDirection.getOrientation(side)) {
					case UP:
						return this.topDiskDriveIcon;
					case DOWN:
						return this.bottomPeripheralIcon;
					default:
						return side == ForgeDirection.NORTH.ordinal() ? this.frontDiskDriveIcon : side == ForgeDirection.SOUTH.ordinal() ? this.backPeripheralIcon : this.sideDiskDriveIcon;
				}
			}
		}
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
