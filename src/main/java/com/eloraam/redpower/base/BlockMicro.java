package com.eloraam.redpower.base;

import com.eloraam.redpower.base.ItemMicro;
import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockMicro extends BlockCoverable {
	
	public BlockMicro() {
		super(CoreLib.materialRedpower);
		this.setHardness(0.1F);
		this.setCreativeTab(CreativeExtraTabs.tabWires);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
		
		
		this.blockIcon = reg.registerIcon(this.getTextureName());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		TileExtended tile = (TileExtended)CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
		if(tile != null) {
			switch(meta) {
				case 8: { //BASE PNEUMATIC TUBE
					
				}
				
				case 9: { //REDSTONE TUBE
					
				}
				
				case 10: { //RESTRICTION TUBE
					
				}
				
				case 11: { //MAGNETIC TUBE
					
				}
			}
		}
		return this.blockIcon;
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
		switch(meta) {
			case 2048: { //BASE PNEUMATIC TUBE
				
			}
			
			case 2304: { //REDSTONE TUBE
				
			}
			
			case 2560: { //RESTRICTION TUBE
				
			}
			
			case 2816: { //MAGNETIC TUBE
				
			}
		}
        return this.blockIcon;
    }
	
	@Override
	public boolean canProvidePower() {
		return !RedPowerLib.isSearching();
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess iba, int i, int j, int k, int dir) {
		if (RedPowerLib.isSearching()) {
			return false;
		} else {
			int md = iba.getBlockMetadata(i, j, k);
			return md == 1 || md == 2;
		}
	}
	
	public void registerPlacement(int md, IMicroPlacement imp) {
		((ItemMicro) Item.getItemFromBlock(this)).registerPlacement(md, imp);
	}
}
