package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.ITubeFlow;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;

import java.util.Iterator;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileTubeRenderer extends TileEntitySpecialRenderer {
	
	int[] paintColors = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, '\uff00', 16737408, 5460819, 9671571, '\uffff', 8388863, 255, 5187328, '\u8000', 16711680, 2039583 };
	RenderContext context = new RenderContext();
	EntityItem entityitem = new EntityItem((World) null);
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		Tessellator tessellator = Tessellator.instance;
		int lv = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
		tessellator.setBrightness(lv);
		this.entityitem.worldObj = te.getWorldObj();
		this.entityitem.setPosition(te.xCoord + 0.5D, te.yCoord + 0.5D, te.zCoord + 0.5D);
		RenderItem renderitem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
		this.entityitem.age = 0;
		this.entityitem.hoverStart = 0.0F;
		WorldCoord offset = new WorldCoord(0, 0, 0);
		ITubeFlow itf = (ITubeFlow) te;
		TubeFlow tf = itf.getTubeFlow();
		Iterator<TubeItem> iter = tf.contents.iterator();
		
		while (iter.hasNext()) {
			TubeItem item = (TubeItem) iter.next();
			this.entityitem.setEntityItemStack(item.item);
			offset.x = 0;
			offset.y = 0;
			offset.z = 0;
			offset.step(item.side);
			double d = item.progress / 128.0D * 0.5D;
			if (!item.scheduled) {
				d = 0.5D - d;
			}
			
			double yo = 0.0D;
			if (Item.getIdFromItem(item.item.getItem()) >= 256) {
				yo += 0.1D;
			}
			
			renderitem.doRender(this.entityitem, x + 0.5D + offset.x * d, y
					+ 0.5D - this.entityitem.yOffset - yo + offset.y * d, z
					+ 0.5D + offset.z * d, 0.0F, 0.0F);
			if (item.color > 0) {
				//this.bindTextureByName("/eloraam/machine/machine1.png");
				tessellator.startDrawingQuads();
				this.context.useNormal = true;
				this.context.setDefaults();
				this.context.setBrightness(lv);
				this.context.setPos(x + offset.x * d, y + offset.y * d, z + offset.z * d);
				this.context.setTintHex(this.paintColors[item.color - 1]);
				this.context.setIcon(te.blockType.getIcon(3, te.blockMetadata));
				this.context.renderBox(63, 0.25999999046325684D,
						0.25999999046325684D, 0.25999999046325684D,
						0.7400000095367432D, 0.7400000095367432D,
						0.7400000095367432D);
				tessellator.draw();
			}
		}
		
	}
}
