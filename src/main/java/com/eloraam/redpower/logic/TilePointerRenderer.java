package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.TileLogicPointer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class TilePointerRenderer extends TileEntitySpecialRenderer {
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		Tessellator tessellator = Tessellator.instance;
		if (te instanceof TileLogicPointer) {
			TileLogicPointer tlp = (TileLogicPointer) te;
			//this.bindTextureByName("/terrain.png"); //TODO: Ko-ko, may be not needed
			int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
			tessellator.setBrightness(lv);
			GL11.glDisable(2896);
			tessellator.startDrawingQuads();
			float ptrdir = tlp.getPointerDirection(f) + 0.25F;
			Quat q = MathLib.orientQuat(tlp.Rotation >> 2, tlp.Rotation & 3);
			Vector3 v = tlp.getPointerOrigin();
			q.rotate(v);
			v.add(x + 0.5D, y + 0.5D, z + 0.5D);
			q.rightMultiply(Quat.aroundAxis(0.0D, 1.0D, 0.0D, -6.283185307179586D * ptrdir));
			RenderLib.renderPointer(v, q); //TODO:
			tessellator.draw();
			GL11.glEnable(2896);
		}
	}
}
