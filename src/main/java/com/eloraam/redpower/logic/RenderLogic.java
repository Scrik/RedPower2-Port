package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoreProxyClient;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;

import java.nio.DoubleBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public abstract class RenderLogic extends RenderCovers {
	
	public RenderLogic(Block bl) {
		super(bl);
	}
	
	public void renderCovers(IBlockAccess iba, TileLogic tl) {
		if (tl.Cover != 255) {
			super.context.setPos(tl.xCoord, tl.yCoord, tl.zCoord);
			super.context.readGlobalLights(iba, tl.xCoord, tl.yCoord, tl.zCoord);
			this.renderCover(tl.Rotation, tl.Cover);
		}
	}
	
	public TileLogic getTileEntity(IBlockAccess iba, int i, int j, int k) {
		TileEntity te = iba.getTileEntity(i, j, k);
		return !(te instanceof TileLogic) ? null : (TileLogic) te;
	}
	
	public void setMatrixWorld(int x, int y, int z, int rot) {
		super.context.setOrientation(rot >> 2, rot & 3);
		super.context.setPos(x, y, z);
	}
	
	public void setMatrixDisplayTick(int i, int j, int k, int rot, Random random) {
		float x = i + 0.5F + (random.nextFloat() - 0.5F) * 0.2F;
		float y = j + 0.7F + (random.nextFloat() - 0.5F) * 0.2F;
		float z = k + 0.5F + (random.nextFloat() - 0.5F) * 0.2F;
		super.context.setOrientation(0, rot);
		super.context.setPos(x, y, z);
	}
	
	public void setMatrixInv() {
		super.context.setOrientation(0, 3);
		super.context.setPos(-0.5D, -0.5D, -0.5D);
	}
	
	public void renderWafer(int tx) {
		tx &= 255;
		super.context.setRelPos(0.0D, 0.0D, 0.0D);
		super.context.setTint(1.0F, 1.0F, 1.0F);
		super.context.setTexFlags(0);
		super.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
		super.context.setIcon(null, getIcon(0, 0), null, null, null, null);
		super.context.calcBounds();
		super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		super.context.renderFaces(62);
		//super.context.unbindTexture();
	}
	
	public void renderInvWafer(int tx) {
		super.context.useNormal = true;
		/*switch (tx >> 8) {
			default: //case 0:
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("rplogic", "textures/blocks/logic1.png"));
				icns = CoreProxyClient.logicIcons1;
				break;
			case 1:
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("rplogic", "textures/blocks/logic2.png"));
				icns = CoreProxyClient.logicIcons2;
				break;
			case 2:
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("rplogic", "textures/blocks/sensor1.png"));
				icns = CoreProxyClient.sensorIcons;
		}
		
		tx &= 255;*/
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		super.context.setTint(1.0F, 1.0F, 1.0F);
		super.context.setTexFlags(0);
		super.context.setSize(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
		super.context.setIcon(null, getIcon(0, 0), null, null, null, null);
		super.context.calcBounds();
		super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		super.context.renderFaces(63);
		tessellator.draw();
		//RenderLib.setDefaultTexture();
		super.context.useNormal = false;
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
	}
	
	public void renderCover(int rot, int cov) {
		if (cov != 255) {
			rot >>= 2;
			rot ^= 1;
			short[] rs = new short[] { (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0 };
			rs[rot] = (short) cov;
			super.context.setTint(1.0F, 1.0F, 1.0F);
			this.renderCovers(1 << rot, rs);
		}
	}
	
	//private static RenderBlocks rb = new RenderBlocks();
	
	/*public static void renderRedstoneTorch(double x, double y, double z, double height, boolean state) {
        Block b = null;
        if (state) b = Blocks.redstone_torch;
        else b = Blocks.unlit_redstone_torch;
        Tessellator tess = Tessellator.instance;
        boolean isDrawing = false;
        try {
        	tess.startDrawingQuads();
        	tess.draw();
        } catch(IllegalStateException exc) {
        	isDrawing = true;
        }
        
        if(isDrawing) {
        	tess.draw();
        }
        
        GL11.glTranslated(x, y, z);
        
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, planeEquation(0, 0, 0, 0, 0, 1, 1, 0, 1));
        
        
        
        tess.startDrawingQuads();
        
        tess.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        rb.renderTorchAtAngle(b, 0, y + height - 1, 0, 0, 0, 0);
        tess.draw();
        
        GL11.glDisable(GL11.GL_CLIP_PLANE0);
        
        GL11.glTranslated(-x, -y, -z);
        
        if(isDrawing) {
        	tess.startDrawingQuads();
        }
    }*/
	
	public void renderRedstoneTorch(double x, double y, double z, double h, boolean state) {
		IIcon tex = Blocks.unlit_redstone_torch.getBlockTextureFromSide(1);
		if(state) {
			tex = Blocks.redstone_torch.getBlockTextureFromSide(1);
		}
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
		super.context.setTexFlags(0);
		super.context.setRelPos(x, y, z);
		super.context.setIcon(tex);
		super.context.setLocalLights(1.0F);
		super.context.setTint(1.0F, 1.0F, 1.0F);
		super.context.setSize(0.4375D, 1.0D - h, 0.0D, 0.5625D, 1.0D, 1.0D);
		super.context.calcBounds();
		super.context.renderFaces(48);
		super.context.setSize(0.0D, 1.0D - h, 0.4375D, 1.0D, 1.0D, 0.5625D);
		super.context.calcBounds();
		super.context.renderFaces(12);
		super.context.setSize(0.375D, 0.0D, 0.4375D, 0.5D, 1.0D, 0.5625D);
		super.context.setRelPos(x + 0.0625D, y - 0.375D, z);
		super.context.calcBounds();
		super.context.setTexFlags(24);
		super.context.renderFaces(2);
		super.context.setRelPos(0.0D, 0.0D, 0.0D);
	}
	
	public void renderTorchPuff(World world, String name, double x, double y, double z) {
		Vector3 v = new Vector3(x, y, z);
		super.context.basis.rotate(v);
		v.add(super.context.globalOrigin);
		world.spawnParticle(name, v.x, v.y, v.z, 0.0D, 0.0D, 0.0D);
	}
	
	public void renderChip(double x, double y, double z, int tex) {
		//super.context.bindTexture("/eloraam/logic/logic1.png");
		super.context.setTexFlags(0);
		super.context.setRelPos(x, y, z);
		super.context.setIcon(getIcon(0, 0));
		super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
		super.context.renderBox(62, 0.375D, 0.0625D, 0.375D, 0.625D, 0.1875D, 0.625D);
		//super.context.unbindTexture();
	}
	
	protected int getTorchState(TileLogic tl) {
		return 0;
	}
	
	protected int getInvTorchState(int md) {
		return 0;
	}
	
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		return null;
	}
	
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		return null;
	}
	
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
	}
	
	protected void renderInvPart(int md) {
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		TileLogic tl = (TileLogic) CoreLib.getTileEntity(world, i, j, k,
				TileLogic.class);
		if (tl != null) {
			int ts = this.getTorchState(tl);
			if (ts != 0) {
				this.setMatrixDisplayTick(i, j, k, tl.Rotation, random);
				RenderLogic.TorchPos[] tpv = this.getTorchVectors(tl);
				if (tpv != null) {
					int rv = random.nextInt(tpv.length);
					if ((ts & 1 << rv) != 0) {
						this.renderTorchPuff(world, "reddust", tpv[rv].x, tpv[rv].y, tpv[rv].z);
					}
				}
			}
		}
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		TileLogic tl = (TileLogic) CoreLib.getTileEntity(iba, i, j, k, TileLogic.class);
		if (tl != null) {
			Tessellator tess = Tessellator.instance;
			tess.draw();
			
			//this.renderCovers(iba, tl);
			super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
			this.setMatrixWorld(i, j, k, tl.Rotation);
			this.renderWorldPart(iba, tl);
			int ts = this.getTorchState(tl);
			RenderLogic.TorchPos[] tpv = this.getTorchVectors(tl);
			if (tpv != null) {
				for (int n = 0; n < tpv.length; ++n) { //TODO: .
					//IIcon icon = (ts & 1 << n) > 0 ? super.block.getIcon(0, md) :super.block.getIcon(1, md);
					this.renderRedstoneTorch(tpv[n].x, tpv[n].y, tpv[n].z, tpv[n].h, (ts & 1 << n) > 0);
				}
			}
			
			this.context.bindBlockTexture();
			tess.startDrawingQuads();
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		super.block.setBlockBoundsForItemRender();
		super.context.setDefaults();
		this.setMatrixInv();
		this.renderInvPart(md);
		GL11.glDisable(2896);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		int ts = this.getInvTorchState(md);
		RenderLogic.TorchPos[] tpv = this.getInvTorchVectors(md);
		if (tpv != null) {
			for (int n = 0; n < tpv.length; ++n) {
				this.renderRedstoneTorch(tpv[n].x, tpv[n].y, tpv[n].z, tpv[n].h, (ts & 1 << n) > 0);
			}
		}
		
		tessellator.draw();
		GL11.glEnable(2896);
	}
	
	public static DoubleBuffer planeEquation(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        double[] eq = new double[4];
        eq[0] = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
        eq[1] = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
        eq[2] = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        eq[3] = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1));
        DoubleBuffer b = BufferUtils.createDoubleBuffer(8).put(eq);
        b.flip();
        return b;
    }
	
	public static class TorchPos {
		
		double x;
		double y;
		double z;
		double h;
		
		public TorchPos(double xi, double yi, double zi, double hi) {
			this.x = xi;
			this.y = yi;
			this.z = zi;
			this.h = hi;
		}
	}
}
