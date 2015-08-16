package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockLogic extends BlockCoverable {
	
	//private IIconRegister reg;
	
	public BlockLogic() {
		super(CoreLib.materialRedpower);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.setHardness(0.1F);
		this.setLightLevel(0.625F);
		this.setCreativeTab(CreativeExtraTabs.tabWires);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister reg) {
		//this.reg=reg;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int damage) {
		//switch(side >> 8) {
			//default: //case 0:
				//return CoreProxyClient.getSubIcon(this.reg, "/assets/rplogic/textures/blocks/logic1.png", side);
			//case 1:
			//	return CoreProxyClient.getSubIcon(this.reg, "/assets/rplogic/textures/blocks/logic2.png", side);
			//case 2:
			//	return CoreProxyClient.getSubIcon(this.reg, "/assets/rplogic/textures/blocks/sensor1.png", side);
		//}
		return Blocks.cobblestone.getBlockTextureFromSide(0);
		//return side > 255 ? (side - 255 > 255 ? CoreProxyClient.sensorIcons[side - 255 - 255] : CoreProxyClient.logicIcons2[side - 255]) : CoreProxyClient.logicIcons1[side];
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess iba, int i, int j, int k, int dir) {
		if (dir < 0) {
			return false;
		} else {
			IRedPowerConnectable irp = (IRedPowerConnectable) CoreLib.getTileEntity(iba, i, j, k, IRedPowerConnectable.class);
			if (irp == null) {
				return false;
			} else {
				int s = RedPowerLib.mapLocalToRot(irp.getConnectableMask(), 2);
				return (s & 1 << dir) > 0;
			}
		}
	}
	
	@Override
	public int getLightValue(IBlockAccess iba, int i, int j, int k) {
		TileLogic tl = (TileLogic) CoreLib.getTileEntity(iba, i, j, k, TileLogic.class);
		return tl == null ? super.getLightValue(iba, i, j, k) : tl.getLightValue();
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
}
