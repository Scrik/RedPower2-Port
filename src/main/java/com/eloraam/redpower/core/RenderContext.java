package com.eloraam.redpower.core;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Matrix3;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.TexVertex;
import com.eloraam.redpower.core.Vector3;

public class RenderContext {
	
	public static final int[][] texRotTable = new int[][] { { 0, 1, 2, 3, 4, 5, 0, 112347, 0 }, { 0, 1, 4, 5, 3, 2, 45, 112320, 27 }, { 0, 1, 3, 2, 5, 4, 27, 112347, 0 }, { 0, 1, 5, 4, 2, 3, 54, 112320, 27 }, { 1, 0, 2, 3, 5, 4, 112347, 112347, 0 }, { 1, 0, 4, 5, 2, 3, 112374, 112320, 27 }, { 1, 0, 3, 2, 4, 5, 112320, 112347, 0 }, { 1, 0, 5, 4, 3, 2, 112365, 112320, 27 }, { 4, 5, 0, 1, 2, 3, 217134, 1728, 110619 }, { 3, 2, 0, 1, 4, 5, 220014, 0, 112347 }, { 5, 4, 0, 1, 3, 2, 218862, 1728, 110619 }, { 2, 3, 0, 1, 5, 4, 220590, 0, 112347 }, { 4, 5, 1, 0, 3, 2, 188469, 1728, 110619 }, { 3, 2, 1, 0, 5, 4, 191349, 0, 112347 }, { 5, 4, 1, 0, 2, 3, 190197, 1728, 110619 }, { 2, 3, 1, 0, 4, 5, 191925, 0, 112347 }, { 4, 5, 3, 2, 0, 1, 2944, 110619, 1728 }, { 3, 2, 5, 4, 0, 1, 187264, 27, 112320 }, { 5, 4, 2, 3, 0, 1, 113536, 110619, 1728 }, { 2, 3, 4, 5, 0, 1, 224128, 27, 112320 }, { 4, 5, 2, 3, 1, 0, 3419, 110619, 1728 }, { 3, 2, 4, 5, 1, 0, 187739, 27, 112320 }, { 5, 4, 3, 2, 1, 0, 114011, 110619, 1728 }, { 2, 3, 5, 4, 1, 0, 224603, 27, 112320 } };
	public Matrix3 basis = new Matrix3();
	public Vector3 localOffset = new Vector3();
	public Vector3 globalOrigin = new Vector3();
	public Vector3 boxSize1 = new Vector3();
	public Vector3 boxSize2 = new Vector3();
	public RenderModel boundModel = null;
	public Vector3[] vertexList;
	private Vector3[] vertexListBox = new Vector3[8];
	public TexVertex[][] cornerList;
	private TexVertex[][] cornerListBox = new TexVertex[6][4];
	private IIcon[] texIndex; //TODO: int
	private IIcon[] texIndexBox = new IIcon[6];
	private IIcon[][] texIndexList;
	public boolean lockTexture = false;
	public boolean exactTextureCoordinates = false;
	private int texFlags = 0;
	private int rotation = 0;
	public boolean useNormal = false;
	public boolean forceFlat = false;
	private float tintR = 1.0F;
	private float tintG = 1.0F;
	private float tintB = 1.0F;
	private float tintA = 1.0F;
	public float[] lightLocal;
	private float[] lightLocalBox = new float[6];
	public int[] brightLocal;
	private int[] brightLocalBox = new int[6];
	private int[][][] lightGlobal = new int[3][3][3];
	private float[][][] aoGlobal = new float[3][3][3];
	private float[] lightFlat = new float[6];
	private int globTrans;
	
	public void setDefaults() {
		this.localOffset.set(0.0D, 0.0D, 0.0D);
		this.setOrientation(0, 0);
		this.texFlags = 0;
		this.tintR = 1.0F;
		this.tintG = 1.0F;
		this.tintB = 1.0F;
		this.tintA = 1.0F;
		this.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
		this.setBrightness(15728880);
	}
	
	public void bindBlockTexture() {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/atlas/blocks.png")); //TODO:
	}
	
	public void setPos(double x, double y, double z) {
		this.globalOrigin.set(x, y, z);
	}
	
	public void setPos(Vector3 v) {
		this.globalOrigin.set(v);
	}
	
	public void setRelPos(double x, double y, double z) {
		this.localOffset.set(x, y, z);
	}
	
	public void setRelPos(Vector3 v) {
		this.localOffset.set(v);
	}
	
	public void setOrientation(int down, int rot) {
		MathLib.orientMatrix(this.basis, down, rot);
	}
	
	public void setSize(double tx, double ty, double tz, double bx, double by, double bz) {
		this.boxSize1.set(tx, ty, tz);
		this.boxSize2.set(bx, by, bz);
	}
	
	public void rotateRenderMatrix(ForgeDirection d) {
        switch (d) {
            case UP:
                GL11.glRotatef(1, 0, 0, -90);
                break;
            case DOWN:
                GL11.glRotatef(1, 0, 0, 90);
                break;
            case NORTH:
                GL11.glRotatef(1, 0, -90, 0);
                break;
            case SOUTH:
                GL11.glRotatef(1, 0, 90, 0);
                break;
            case WEST:
                GL11.glRotatef(1, 0, 0, 180);
                break;
            case EAST:
                GL11.glRotatef(1, 0, 0, 0);
                break;
            default:
                break;
        }
    }
	
	public void rotateBlock(ForgeDirection direction) {
		if (direction == ForgeDirection.UP) {
		    GL11.glTranslated(16, 28, 16);
		    GL11.glRotatef(180, 1, 0, 0);
		}
		if (direction == ForgeDirection.DOWN) {
		    GL11.glTranslated(16, 4, 16);
		    GL11.glRotatef(0, 0, 0, 0);
		}
		if (direction == ForgeDirection.EAST) {
		    GL11.glTranslated(28, 16, 16);
		    GL11.glRotatef(90, 0, 0, 1);
		}
		if (direction == ForgeDirection.WEST) {
		    GL11.glTranslated(4, 16, 16);
		    GL11.glRotatef(90, 0, 0, -1);
		}
		if (direction == ForgeDirection.NORTH) {
		    GL11.glTranslated(16, 16, 4);
		    GL11.glRotatef(90, 1, 0, 0);
		}
		if (direction == ForgeDirection.SOUTH) {
		    GL11.glTranslated(16, 16, 28);
		    GL11.glRotatef(90, -1, 0, 0);
		}
	}
	
	/*public void clearTexFiles() {
		this.texBinds = null;
		RenderLib.unbindTexture();
	}
	
	public void setTexFiles(String[] tex) {
		this.texBinds = tex;
	}*/
	
	/*public void setTexFile(String tex) {
		this.texBinds = null;
		if (tex == null) {
			RenderLib.unbindTexture();
		} else {
			RenderLib.bindTexture(tex);
		}
		
	}*/
	
	/*public void setTexFiles(String a, String b, String c, String d, String e,
			String f) {
		if (a == null && b == null && c == null && d == null && e == null
				&& f == null) {
			if (this.texBinds != null) {
				this.clearTexFiles();
			}
			
		} else {
			this.texBinds = this.texBindsBox;
			this.texBinds[0] = a;
			this.texBinds[1] = b;
			this.texBinds[2] = c;
			this.texBinds[3] = d;
			this.texBinds[4] = e;
			this.texBinds[5] = f;
		}
	}*/
	
	public void setTexFlags(int fl) {
		this.texFlags = fl;
	}
	
	public void setIcon(IIcon a, IIcon b, IIcon c, IIcon d, IIcon e, IIcon f) {
		if (!this.lockTexture) {
			this.texIndex = this.texIndexBox;
			this.texIndex[0] = a;
			this.texIndex[1] = b;
			this.texIndex[2] = c;
			this.texIndex[3] = d;
			this.texIndex[4] = e;
			this.texIndex[5] = f;
		}
	}
	
	public void setIcon(IIcon a) {
		if (!this.lockTexture) {
			this.texIndex = this.texIndexBox;
			this.texIndex[0] = a;
			this.texIndex[1] = a;
			this.texIndex[2] = a;
			this.texIndex[3] = a;
			this.texIndex[4] = a;
			this.texIndex[5] = a;
		}
	}
	
	public void setIcon(IIcon[] a) {
		if (!this.lockTexture) {
			this.texIndex = a;
		}
	}
	
	public void setIcon(IIcon[][] a) {
		if (!this.lockTexture) {
			this.texIndexList = a;
			this.texIndex = a[0];
		}
	}
	
	public void setIconIndex(int n) {
		if (this.texIndexList != null) {
			this.texIndex = this.texIndexList[n];
		}
	}
	
	public void setIconNum(int num, IIcon tex) {
		this.texIndex[num] = tex;
	}
	
	public void setTint(float r, float g, float b) {
		this.tintR = r;
		this.tintG = g;
		this.tintB = b;
	}
	
	public void setTintHex(int tc) {
		this.tintR = (float) (tc >> 16) / 255.0F;
		this.tintG = (float) (tc >> 8 & 255) / 255.0F;
		this.tintB = (float) (tc & 255) / 255.0F;
	}
	
	public void setAlpha(float a) {
		this.tintA = a;
	}
	
	public void setLocalLights(float a, float b, float c, float d, float e, float f) {
		this.lightLocal = this.lightLocalBox;
		this.lightLocal[0] = a;
		this.lightLocal[1] = b;
		this.lightLocal[2] = c;
		this.lightLocal[3] = d;
		this.lightLocal[4] = e;
		this.lightLocal[5] = f;
	}
	
	public void setLocalLights(float a) {
		this.lightLocal = this.lightLocalBox;
		
		for (int i = 0; i < 6; ++i) {
			this.lightLocal[i] = a;
		}
		
	}
	
	public void setBrightness(int a) {
		this.brightLocal = this.brightLocalBox;
		
		for (int i = 0; i < 6; ++i) {
			this.brightLocal[i] = a;
		}
		
	}
	
	public void startWorldRender(RenderBlocks rbl) {
	}
	
	public boolean endWorldRender() {
		return false;
	}
	
	/*public void bindTexture(String name) {
		if (!this.lockTexture) {
			RenderLib.bindTexture(name);
		}
	}*/
	
	/*public void unbindTexture() {
		if (!this.lockTexture) {
			RenderLib.unbindTexture();
		}
	}*/
	
	public void setupBox() {
		this.vertexList = this.vertexListBox;
		this.vertexList[0].set(this.boxSize2.x, this.boxSize2.y, this.boxSize1.z);
		this.vertexList[1].set(this.boxSize1.x, this.boxSize2.y, this.boxSize1.z);
		this.vertexList[2].set(this.boxSize1.x, this.boxSize2.y, this.boxSize2.z);
		this.vertexList[3].set(this.boxSize2.x, this.boxSize2.y, this.boxSize2.z);
		this.vertexList[4].set(this.boxSize2.x, this.boxSize1.y, this.boxSize1.z);
		this.vertexList[5].set(this.boxSize1.x, this.boxSize1.y, this.boxSize1.z);
		this.vertexList[6].set(this.boxSize1.x, this.boxSize1.y, this.boxSize2.z);
		this.vertexList[7].set(this.boxSize2.x, this.boxSize1.y, this.boxSize2.z);
	}
	
	public void transformRotate() {
		for (int i = 0; i < this.vertexList.length; ++i) {
			this.vertexList[i].add(this.localOffset.x - 0.5D, this.localOffset.y - 0.5D, this.localOffset.z - 0.5D);
			this.basis.rotate(this.vertexList[i]);
			this.vertexList[i].add(this.globalOrigin.x + 0.5D, this.globalOrigin.y + 0.5D, this.globalOrigin.z + 0.5D);
		}
	}
	
	public void transform() {
		for (int i = 0; i < this.vertexList.length; ++i) {
			this.vertexList[i].add(this.localOffset);
			this.vertexList[i].add(this.globalOrigin);
		}
	}
	
	public void setSideUV(int s, IIcon ti) {
		if(ti == null) return;
		double u1 = ti.getMinU(), u2 = ti.getMaxU(), v1 = ti.getMinV(), v2 = ti.getMaxV();
		
		//int txl = this.texFlags >> s * 3;
		
		//switch(this.rotation) {
			//default: {
				this.cornerList[s][0].setUV(u2, v1);
				this.cornerList[s][1].setUV(u2, v2);
				this.cornerList[s][2].setUV(u1, v2);
				this.cornerList[s][3].setUV(u1, v1);
			//}
			//case 1: {
			//	this.cornerList[s][0].setUV(u2, v2);
			//	this.cornerList[s][1].setUV(u2, v1);
			//	this.cornerList[s][2].setUV(u1, v1);
			//	this.cornerList[s][3].setUV(u1, v2);
			//}
			//case 2: {
			//	this.cornerList[s][0].setUV(u2, v1);
			//	this.cornerList[s][1].setUV(u2, v2);
			//	this.cornerList[s][2].setUV(u1, v2);
			//	this.cornerList[s][3].setUV(u1, v1);
			//}
			//case 3: {
			//	this.cornerList[s][0].setUV(u1, v2);
			//	this.cornerList[s][1].setUV(u2, v1);
			//	this.cornerList[s][2].setUV(u2, v1);
			//	this.cornerList[s][3].setUV(u1, v2);
			//}
			//case 4: {
			//	this.cornerList[s][0].setUV(u1, v1);
			//	this.cornerList[s][1].setUV(u2, v2);
			//	this.cornerList[s][2].setUV(u2, v2);
			//	this.cornerList[s][3].setUV(u1, v1);
			//}
			//case 5: {
			//	this.cornerList[s][0].setUV(u1, v1);
			//	this.cornerList[s][1].setUV(u2, v2);
			//	this.cornerList[s][2].setUV(u2, v2);
			//	this.cornerList[s][3].setUV(u1, v1);
			//}
		//}
		/*if (!this.exactTextureCoordinates) {
			u1 += 0.001D;
			v1 += 0.001D;
			u2 -= 0.001D;
			v2 -= 0.001D;
		}*/
		
		/*u1 *= 0.0625D;
		u2 *= 0.0625D;
		v1 *= 0.0625D;
		v2 *= 0.0625D;*/
		/*if ((txl & 1) > 0) {
			u1 = 0.0625D - u1;
			u2 = 0.0625D - u2;
		}
		if ((txl & 2) > 0) {
			v1 = 0.0625D - v1;
			v2 = 0.0625D - v2;
		}*/
		/*IIcon *//*ti = this.texIndexBox[s];*/ //TODO: VEERY VERY WRONG
		//double tx = (double) (ti & 15) * 0.0625D;
		//double ty = (double) (ti >> 4) * 0.0625D;
		//if ((txl & 4) > 0) {
			//u1 += ty;
			//u2 += ty;
			//v1 += tx;
			//v2 += tx;
			//this.cornerList[s][0].setUV(v1, u1);
			//this.cornerList[s][1].setUV(v2, u1);
			//this.cornerList[s][2].setUV(v2, u2);
			//this.cornerList[s][3].setUV(v1, u2);
			this.cornerList[s][0].setUV(u2, v2);
			this.cornerList[s][1].setUV(u2, v1);
			this.cornerList[s][2].setUV(u1, v1);
			this.cornerList[s][3].setUV(u1, v2);
		//} else {
			//u1 += tx;
			//u2 += tx;
			//v1 += ty;
			//v2 += ty;
			//this.cornerList[s][0].setUV(u1, v1);
			//this.cornerList[s][1].setUV(u1, v2);
			//this.cornerList[s][2].setUV(u2, v2);
			//this.cornerList[s][3].setUV(u2, v1);
		//}
	}
	
	public void doMappingBox(int sides) {
		this.cornerList = this.cornerListBox;
		/*double u1;
		double u2;
		double v1;
		double v2;*/
		if ((sides & 3) > 0) {
			//v1 = 1.0D - this.boxSize2.x;
			//v2 = 1.0D - this.boxSize1.x;
			if ((sides & 1) > 0) {
				//u1 = 1.0D - this.boxSize2.z;
				//u2 = 1.0D - this.boxSize1.z;
				this.setSideUV(0, this.texIndex[0] != null ? this.texIndex[0] : null/*u1, u2, v1, v2*/);
			}
			
			if ((sides & 2) > 0) {
				//u1 = this.boxSize1.z;
				//u2 = this.boxSize2.z;
				this.setSideUV(1, this.texIndex[1] != null ? this.texIndex[1] : null/*u1, u2, v1, v2*/);
			}
		}
		
		if ((sides & 60) != 0) {
			//v1 = 1.0D - this.boxSize2.y;
			//v2 = 1.0D - this.boxSize1.y;
			if ((sides & 4) > 0) {
				//u1 = 1.0D - this.boxSize2.x;
				//u2 = 1.0D - this.boxSize1.x;
				this.setSideUV(2, this.texIndex[2] != null ? this.texIndex[2] : null/*u1, u2, v1, v2*/);
			}
			
			if ((sides & 8) > 0) {
				//u1 = this.boxSize1.x;
				//u2 = this.boxSize2.x;
				this.setSideUV(3, this.texIndex[3] != null ? this.texIndex[3] : null/*u1, u2, v1, v2*/);
			}
			
			if ((sides & 16) > 0) {
				//u1 = this.boxSize1.z;
				//u2 = this.boxSize2.z;
				this.setSideUV(4, this.texIndex[4] != null ? this.texIndex[4] : null/*u1, u2, v1, v2*/);
			}
			
			if ((sides & 32) > 0) {
				//u1 = 1.0D - this.boxSize2.z;
				//u2 = 1.0D - this.boxSize1.z;
				this.setSideUV(5, this.texIndex[5] != null ? this.texIndex[5] : null/*u1, u2, v1, v2*/);
			}
		}
	}
	
	public void calcBoundsGlobal() {
		this.setupBox();
		this.transform();
	}
	
	public void calcBounds() {
		this.setupBox();
		this.transformRotate();
	}
	
	private void swaptex(int a, int b) {
		IIcon t = this.texIndexBox[a];
		this.texIndexBox[a] = this.texIndexBox[b];
		this.texIndexBox[b] = t;
	}
	
	public void orientTextures(int down) {
		this.rotation=down;
		switch (down) {
			case 0:
				break;
			case 1:
				this.swaptex(0, 1);
				this.swaptex(4, 5);
				this.texFlags = 112347;
				break;
			case 2:
				this.swaptex(0, 2);
				this.swaptex(1, 3);
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.texFlags = 217134;
				break;
			case 3:
				this.swaptex(0, 3);
				this.swaptex(1, 2);
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.texFlags = 188469;
				break;
			case 4:
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.swaptex(2, 3);
				this.texFlags = 2944;
				break;
			case 5:
				this.swaptex(0, 5);
				this.swaptex(1, 4);
				this.swaptex(0, 1);
				this.texFlags = 3419;
				break;
			default:
				return;
		}
	}
	
	public void orientTextureRot(int down, int rot) {
		int r = rot > 1 ? (rot == 2 ? 3 : 6) : (rot == 0 ? 0 : 5);
		r |= r << 3;
		switch (down) {
			case 0:
				this.texFlags = r;
				break;
			case 1:
				this.swaptex(0, 1);
				this.swaptex(4, 5);
				this.texFlags = 112347 ^ r;
				break;
			case 2:
				this.swaptex(0, 2);
				this.swaptex(1, 3);
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.texFlags = 217134 ^ r << 6;
				break;
			case 3:
				this.swaptex(0, 3);
				this.swaptex(1, 2);
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.texFlags = 188469 ^ r << 6;
				break;
			case 4:
				this.swaptex(0, 4);
				this.swaptex(1, 5);
				this.swaptex(2, 3);
				this.texFlags = 2944 ^ r << 12;
				break;
			case 5:
				this.swaptex(0, 5);
				this.swaptex(1, 4);
				this.swaptex(0, 1);
				this.texFlags = 3419 ^ r << 12;
				break;
			default:
				return;
		}
		
	}
	
	private void swaptexfl(int a, int b) {
		IIcon t = this.texIndexBox[a];
		this.texIndexBox[a] = this.texIndexBox[b];
		this.texIndexBox[b] = t;
		a *= 3;
		b *= 3;
		int f1 = this.texFlags >> a & 7;
		int f2 = this.texFlags >> b & 7;
		this.texFlags &= ~(7 << a | 7 << b);
		this.texFlags |= f1 << b | f2 << a;
	}
	
	public void rotateTextures(int rot) {
		int r = rot > 1 ? (rot == 2 ? 3 : 6) : (rot == 0 ? 0 : 5);
		r |= r << 3;
		this.texFlags ^= r;
		switch (rot) {
			case 1:
				this.swaptexfl(2, 4);
				this.swaptexfl(3, 4);
				this.swaptexfl(3, 5);
				break;
			case 2:
				this.swaptexfl(2, 3);
				this.swaptexfl(4, 5);
				break;
			case 3:
				this.swaptexfl(2, 5);
				this.swaptexfl(3, 5);
				this.swaptexfl(3, 4);
		}
		
	}
	
	public void orientTextureFl(int down) {
		switch (down) {
			case 0:
				break;
			case 1:
				this.swaptexfl(0, 1);
				this.swaptexfl(4, 5);
				this.texFlags ^= 112347;
				break;
			case 2:
				this.swaptexfl(0, 2);
				this.swaptexfl(1, 3);
				this.swaptexfl(0, 4);
				this.swaptexfl(1, 5);
				this.texFlags ^= 217134;
				break;
			case 3:
				this.swaptexfl(0, 3);
				this.swaptexfl(1, 2);
				this.swaptexfl(0, 4);
				this.swaptexfl(1, 5);
				this.texFlags ^= 188469;
				break;
			case 4:
				this.swaptexfl(0, 4);
				this.swaptexfl(1, 5);
				this.swaptexfl(2, 3);
				this.texFlags ^= 2944;
				break;
			case 5:
				this.swaptexfl(0, 5);
				this.swaptexfl(1, 4);
				this.swaptexfl(0, 1);
				this.texFlags ^= 3419;
				break;
			default:
				return;
		}
		
	}
	
	public void orientTextureNew(int rv) {
		IIcon[] texSrc = new IIcon[6];
		System.arraycopy(this.texIndexBox, 0, texSrc, 0, 6);
		int[] rot = texRotTable[rv]; //TODO: IIcon MAYBE
		int tfo = 0;
		
		int t2;
		for (t2 = 0; t2 < 6; ++t2) {
			this.texIndexBox[t2] = texSrc[rot[t2]];
			tfo |= (this.texFlags >> rot[t2] * 3 & 7) << t2 * 3;
		}
		
		t2 = (tfo & '\u9249') << 1 | (tfo & 74898) >> 1;
		this.texFlags = rot[6] ^ tfo & rot[7] ^ t2 & rot[8];
	}
	
	public void flipTextures() {
		this.swaptex(0, 1);
		this.swaptex(2, 3);
		this.swaptex(4, 5);
	}
	
	public void renderBox(int sides, double x1, double y1, double z1, double x2, double y2, double z2) {
		this.setSize(x1, y1, z1, x2, y2, z2);
		this.setupBox();
		this.transformRotate();
		this.renderFaces(sides);
	}
	
	public void doubleBox(int sides, double x1, double y1, double z1, double x2, double y2, double z2, double ino) {
		int s2 = sides << 1 & 42 | sides >> 1 & 21;
		this.renderBox(sides, x1, y1, z1, x2, y2, z2);
		this.flipTextures();
		this.renderBox(s2, x2 - ino, y2 - ino, z2 - ino, x1 + ino, y1 + ino, z1 + ino);
	}
	
	public void doLightLocal(int sides) {
		for (int i = 0; i < this.cornerList.length; ++i) {
			if ((sides & 1 << i) != 0) {
				TexVertex c = this.cornerList[i][0];
				c.r = this.lightLocal[i] * this.tintR;
				c.g = this.lightLocal[i] * this.tintG;
				c.b = this.lightLocal[i] * this.tintB;
				c.brtex = this.brightLocal[i];
			}
		}
		
	}
	
	public void readGlobalLights(IBlockAccess iba, int i, int j, int k) {
		Block block = iba.getBlock(i, j, k);
		if (Minecraft.isAmbientOcclusionEnabled() && !this.forceFlat) {
			for (int a = 0; a < 3; ++a) {
				for (int b = 0; b < 3; ++b) {
					for (int c = 0; c < 3; ++c) {
						this.aoGlobal[a][b][c] = iba.getBlock(i + a - 1, j + b - 1, k + c - 1).getAmbientOcclusionLightValue();//block.getAmbientOcclusionLightValue(/*iba, i + a - 1,
								//j + b - 1, k + c - 1*/);
						this.lightGlobal[a][b][c] = block.getMixedBrightnessForBlock(iba, i + a - 1, j + b - 1, k + c - 1);
					}
				}
			}
			
			int t = 0;
			if (iba.getBlock(i, j - 1, k - 1).getCanBlockGrass()) {
				t |= 1;
			}
			
			if (iba.getBlock(i, j - 1, k + 1).getCanBlockGrass()) {
				t |= 2;
			}
			
			if (iba.getBlock(i - 1, j - 1, k).getCanBlockGrass()) {
				t |= 4;
			}
			
			if (iba.getBlock(i + 1, j - 1, k).getCanBlockGrass()) {
				t |= 8;
			}
			
			if (iba.getBlock(i - 1, j, k - 1).getCanBlockGrass()) {
				t |= 16;
			}
			
			if (iba.getBlock(i - 1, j, k + 1).getCanBlockGrass()) {
				t |= 32;
			}
			
			if (iba.getBlock(i + 1, j, k - 1).getCanBlockGrass()) {
				t |= 64;
			}
			
			if (iba.getBlock(i + 1, j, k + 1).getCanBlockGrass()) {
				t |= 128;
			}
			
			if (iba.getBlock(i, j + 1, k - 1).getCanBlockGrass()) {
				t |= 256;
			}
			
			if (iba.getBlock(i, j + 1, k + 1).getCanBlockGrass()) {
				t |= 512;
			}
			
			if (iba.getBlock(i - 1, j + 1, k).getCanBlockGrass()) {
				t |= 1024;
			}
			
			if (iba.getBlock(i + 1, j + 1, k).getCanBlockGrass()) {
				t |= 2048;
			}
			
			this.globTrans = t;
		} else {
			this.lightFlat[0] = block.getMixedBrightnessForBlock(iba, i, j - 1, k);
			this.lightFlat[1] = block.getMixedBrightnessForBlock(iba, i, j + 1, k);
			this.lightFlat[2] = block.getMixedBrightnessForBlock(iba, i, j, k - 1);
			this.lightFlat[3] = block.getMixedBrightnessForBlock(iba, i, j, k + 1);
			this.lightFlat[4] = block.getMixedBrightnessForBlock(iba, i - 1, j, k);
			this.lightFlat[5] = block.getMixedBrightnessForBlock(iba, i + 1, j, k); //TODO: getBlockBrightness
		}
	}
	
	public static int blendLight(int i, int j, int k, int l) {
		if (j == 0) {
			j = i;
		}
		
		if (k == 0) {
			k = i;
		}
		
		if (l == 0) {
			l = i;
		}
		
		return i + j + k + l >> 2 & 16711935;
	}
	
	private void lightSmoothFace(int fn) {
		int ff = 0;
		if (this.boxSize1.y > 0.0D) {
			ff |= 1;
		}
		
		if (this.boxSize2.y < 1.0D) {
			ff |= 2;
		}
		
		if (this.boxSize1.z > 0.0D) {
			ff |= 4;
		}
		
		if (this.boxSize2.z < 1.0D) {
			ff |= 8;
		}
		
		if (this.boxSize1.x > 0.0D) {
			ff |= 16;
		}
		
		if (this.boxSize2.x < 1.0D) {
			ff |= 32;
		}
		
		float ao1;
		float ao2;
		float ao3;
		float ao4;
		float gf2;
		float gf3;
		float gf4;
		int lv1;
		int lv2;
		int lv3;
		int lv4;
		int gl2;
		int gl3;
		int gl4;
		float gf1 = gf2 = gf3 = gf4 = this.aoGlobal[1][1][1];
		int gl1 = gl2 = gl3 = gl4 = this.lightGlobal[1][1][1];
		switch (fn) {
			case 0:
				if ((ff & 61) <= 0) {
					ao1 = ao2 = this.aoGlobal[0][0][1];
					ao3 = ao4 = this.aoGlobal[2][0][1];
					lv1 = lv2 = this.lightGlobal[0][0][1];
					lv3 = lv4 = this.lightGlobal[2][0][1];
					if ((this.globTrans & 5) > 0) {
						ao1 = this.aoGlobal[0][0][0];
						lv1 = this.lightGlobal[0][0][0];
					}
					
					if ((this.globTrans & 6) > 0) {
						ao2 = this.aoGlobal[0][0][2];
						lv2 = this.lightGlobal[0][0][2];
					}
					
					if ((this.globTrans & 9) > 0) {
						ao3 = this.aoGlobal[2][0][0];
						lv3 = this.lightGlobal[2][0][0];
					}
					
					if ((this.globTrans & 10) > 0) {
						ao4 = this.aoGlobal[2][0][2];
						lv4 = this.lightGlobal[2][0][2];
					}
					
					gf3 = 0.25F * (this.aoGlobal[1][0][1]
							+ this.aoGlobal[1][0][0] + this.aoGlobal[0][0][1] + ao1);
					gf4 = 0.25F * (this.aoGlobal[1][0][1]
							+ this.aoGlobal[1][0][0] + this.aoGlobal[2][0][1] + ao3);
					gf2 = 0.25F * (this.aoGlobal[1][0][1]
							+ this.aoGlobal[1][0][2] + this.aoGlobal[0][0][1] + ao2);
					gf1 = 0.25F * (this.aoGlobal[1][0][1]
							+ this.aoGlobal[1][0][2] + this.aoGlobal[2][0][1] + ao4);
					gl3 = blendLight(this.lightGlobal[1][0][1],
							this.lightGlobal[1][0][0],
							this.lightGlobal[0][0][1], lv1);
					gl4 = blendLight(this.lightGlobal[1][0][1],
							this.lightGlobal[1][0][0],
							this.lightGlobal[2][0][1], lv3);
					gl2 = blendLight(this.lightGlobal[1][0][1],
							this.lightGlobal[1][0][2],
							this.lightGlobal[0][0][1], lv2);
					gl1 = blendLight(this.lightGlobal[1][0][1],
							this.lightGlobal[1][0][2],
							this.lightGlobal[2][0][1], lv4);
				}
				break;
			case 1:
				if ((ff & 62) <= 0) {
					ao1 = ao2 = this.aoGlobal[0][2][1];
					ao3 = ao4 = this.aoGlobal[2][2][1];
					lv1 = lv2 = this.lightGlobal[0][2][1];
					lv3 = lv4 = this.lightGlobal[2][2][1];
					if ((this.globTrans & 1280) > 0) {
						ao1 = this.aoGlobal[0][2][0];
						lv1 = this.lightGlobal[0][2][0];
					}
					
					if ((this.globTrans & 1536) > 0) {
						ao2 = this.aoGlobal[0][2][2];
						lv2 = this.lightGlobal[0][2][2];
					}
					
					if ((this.globTrans & 2304) > 0) {
						ao3 = this.aoGlobal[2][2][0];
						lv3 = this.lightGlobal[2][2][0];
					}
					
					if ((this.globTrans & 2560) > 0) {
						ao4 = this.aoGlobal[2][2][2];
						lv4 = this.lightGlobal[2][2][2];
					}
					
					gf2 = 0.25F * (this.aoGlobal[1][2][1]
							+ this.aoGlobal[1][2][0] + this.aoGlobal[0][2][1] + ao1);
					gf1 = 0.25F * (this.aoGlobal[1][2][1]
							+ this.aoGlobal[1][2][0] + this.aoGlobal[2][2][1] + ao3);
					gf3 = 0.25F * (this.aoGlobal[1][2][1]
							+ this.aoGlobal[1][2][2] + this.aoGlobal[0][2][1] + ao2);
					gf4 = 0.25F * (this.aoGlobal[1][2][1]
							+ this.aoGlobal[1][2][2] + this.aoGlobal[2][2][1] + ao4);
					gl2 = blendLight(this.lightGlobal[1][2][1],
							this.lightGlobal[1][2][0],
							this.lightGlobal[0][2][1], lv1);
					gl1 = blendLight(this.lightGlobal[1][2][1],
							this.lightGlobal[1][2][0],
							this.lightGlobal[2][2][1], lv3);
					gl3 = blendLight(this.lightGlobal[1][2][1],
							this.lightGlobal[1][2][2],
							this.lightGlobal[0][2][1], lv2);
					gl4 = blendLight(this.lightGlobal[1][2][1],
							this.lightGlobal[1][2][2],
							this.lightGlobal[2][2][1], lv4);
				}
				break;
			case 2:
				if ((ff & 55) <= 0) {
					ao1 = ao2 = this.aoGlobal[0][1][0];
					ao3 = ao4 = this.aoGlobal[2][1][0];
					lv1 = lv2 = this.lightGlobal[0][1][0];
					lv3 = lv4 = this.lightGlobal[2][1][0];
					if ((this.globTrans & 17) > 0) {
						ao1 = this.aoGlobal[0][0][0];
						lv1 = this.lightGlobal[0][0][0];
					}
					
					if ((this.globTrans & 272) > 0) {
						ao2 = this.aoGlobal[0][2][0];
						lv2 = this.lightGlobal[0][2][0];
					}
					
					if ((this.globTrans & 65) > 0) {
						ao3 = this.aoGlobal[2][0][0];
						lv3 = this.lightGlobal[2][0][0];
					}
					
					if ((this.globTrans & 320) > 0) {
						ao4 = this.aoGlobal[2][2][0];
						lv4 = this.lightGlobal[2][2][0];
					}
					
					gf3 = 0.25F * (this.aoGlobal[1][1][0]
							+ this.aoGlobal[1][0][0] + this.aoGlobal[0][1][0] + ao1);
					gf4 = 0.25F * (this.aoGlobal[1][1][0]
							+ this.aoGlobal[1][2][0] + this.aoGlobal[0][1][0] + ao2);
					gf2 = 0.25F * (this.aoGlobal[1][1][0]
							+ this.aoGlobal[1][0][0] + this.aoGlobal[2][1][0] + ao3);
					gf1 = 0.25F * (this.aoGlobal[1][1][0]
							+ this.aoGlobal[1][2][0] + this.aoGlobal[2][1][0] + ao4);
					gl3 = blendLight(this.lightGlobal[1][1][0],
							this.lightGlobal[1][0][0],
							this.lightGlobal[0][1][0], lv1);
					gl4 = blendLight(this.lightGlobal[1][1][0],
							this.lightGlobal[1][2][0],
							this.lightGlobal[0][1][0], lv2);
					gl2 = blendLight(this.lightGlobal[1][1][0],
							this.lightGlobal[1][0][0],
							this.lightGlobal[2][1][0], lv3);
					gl1 = blendLight(this.lightGlobal[1][1][0],
							this.lightGlobal[1][2][0],
							this.lightGlobal[2][1][0], lv4);
				}
				break;
			case 3:
				if ((ff & 59) <= 0) {
					ao1 = ao2 = this.aoGlobal[0][1][2];
					ao3 = ao4 = this.aoGlobal[2][1][2];
					lv1 = lv2 = this.lightGlobal[0][1][2];
					lv3 = lv4 = this.lightGlobal[2][1][2];
					if ((this.globTrans & 34) > 0) {
						ao1 = this.aoGlobal[0][0][2];
						lv1 = this.lightGlobal[0][0][2];
					}
					
					if ((this.globTrans & 544) > 0) {
						ao2 = this.aoGlobal[0][2][2];
						lv2 = this.lightGlobal[0][2][2];
					}
					
					if ((this.globTrans & 130) > 0) {
						ao3 = this.aoGlobal[2][0][2];
						lv3 = this.lightGlobal[2][0][2];
					}
					
					if ((this.globTrans & 640) > 0) {
						ao4 = this.aoGlobal[2][2][2];
						lv4 = this.lightGlobal[2][2][2];
					}
					
					gf2 = 0.25F * (this.aoGlobal[1][1][2]
							+ this.aoGlobal[1][0][2] + this.aoGlobal[0][1][2] + ao1);
					gf1 = 0.25F * (this.aoGlobal[1][1][2]
							+ this.aoGlobal[1][2][2] + this.aoGlobal[0][1][2] + ao3);
					gf3 = 0.25F * (this.aoGlobal[1][1][2]
							+ this.aoGlobal[1][0][2] + this.aoGlobal[2][1][2] + ao2);
					gf4 = 0.25F * (this.aoGlobal[1][1][2]
							+ this.aoGlobal[1][2][2] + this.aoGlobal[2][1][2] + ao4);
					gl2 = blendLight(this.lightGlobal[1][1][2],
							this.lightGlobal[1][0][2],
							this.lightGlobal[0][1][2], lv1);
					gl1 = blendLight(this.lightGlobal[1][1][2],
							this.lightGlobal[1][2][2],
							this.lightGlobal[0][1][2], lv2);
					gl3 = blendLight(this.lightGlobal[1][1][2],
							this.lightGlobal[1][0][2],
							this.lightGlobal[2][1][2], lv3);
					gl4 = blendLight(this.lightGlobal[1][1][2],
							this.lightGlobal[1][2][2],
							this.lightGlobal[2][1][2], lv4);
				}
				break;
			case 4:
				if ((ff & 31) <= 0) {
					ao1 = ao2 = this.aoGlobal[0][1][0];
					ao3 = ao4 = this.aoGlobal[0][1][2];
					lv1 = lv2 = this.lightGlobal[0][1][0];
					lv3 = lv4 = this.lightGlobal[0][1][2];
					if ((this.globTrans & 20) > 0) {
						ao1 = this.aoGlobal[0][0][0];
						lv1 = this.lightGlobal[0][0][0];
					}
					
					if ((this.globTrans & 1040) > 0) {
						ao2 = this.aoGlobal[0][2][0];
						lv2 = this.lightGlobal[0][2][0];
					}
					
					if ((this.globTrans & 36) > 0) {
						ao3 = this.aoGlobal[0][0][2];
						lv3 = this.lightGlobal[0][0][2];
					}
					
					if ((this.globTrans & 1056) > 0) {
						ao4 = this.aoGlobal[0][2][2];
						lv4 = this.lightGlobal[0][2][2];
					}
					
					gf2 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][0][1] + this.aoGlobal[0][1][0] + ao1);
					gf1 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][2][1] + this.aoGlobal[0][1][0] + ao2);
					gf3 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][0][1] + this.aoGlobal[0][1][2] + ao3);
					gf4 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][2][1] + this.aoGlobal[0][1][2] + ao4);
					gl2 = blendLight(this.lightGlobal[0][1][1],this.lightGlobal[0][0][1], this.lightGlobal[0][1][0], lv1);
					gl1 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][2][1], this.lightGlobal[0][1][0], lv2);
					gl3 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][0][1], this.lightGlobal[0][1][2], lv3);
					gl4 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][2][1], this.lightGlobal[0][1][2], lv4);
				}
				break;
			default:
				if ((ff & 47) <= 0) {
					ao1 = ao2 = this.aoGlobal[2][1][0];
					ao3 = ao4 = this.aoGlobal[2][1][2];
					lv1 = lv2 = this.lightGlobal[2][1][0];
					lv3 = lv4 = this.lightGlobal[2][1][2];
					if ((this.globTrans & 72) > 0) {
						ao1 = this.aoGlobal[2][0][0];
						lv1 = this.lightGlobal[2][0][0];
					}
					
					if ((this.globTrans & 2112) > 0) {
						ao2 = this.aoGlobal[2][2][0];
						lv2 = this.lightGlobal[2][2][0];
					}
					
					if ((this.globTrans & 136) > 0) {
						ao3 = this.aoGlobal[2][0][2];
						lv3 = this.lightGlobal[2][0][2];
					}
					
					if ((this.globTrans & 2176) > 0) {
						ao4 = this.aoGlobal[2][2][2];
						lv4 = this.lightGlobal[2][2][2];
					}
					
					gf3 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][0][1] + this.aoGlobal[2][1][0] + ao1);
					gf4 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][2][1] + this.aoGlobal[2][1][0] + ao2);
					gf2 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][0][1] + this.aoGlobal[2][1][2] + ao3);
					gf1 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][2][1] + this.aoGlobal[2][1][2] + ao4);
					gl3 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][0][1], this.lightGlobal[2][1][0], lv1);
					gl4 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][2][1], this.lightGlobal[2][1][0], lv2);
					gl2 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][0][1], this.lightGlobal[2][1][2], lv3);
					gl1 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][2][1], this.lightGlobal[2][1][2], lv4);
				}
		}
		
		TexVertex c = this.cornerList[fn][0];
		float fc = this.lightLocal[fn] * gf1;
		c.r = fc * this.tintR;
		c.g = fc * this.tintG;
		c.b = fc * this.tintB;
		c.brtex = gl1;
		c = this.cornerList[fn][1];
		fc = this.lightLocal[fn] * gf2;
		c.r = fc * this.tintR;
		c.g = fc * this.tintG;
		c.b = fc * this.tintB;
		c.brtex = gl2;
		c = this.cornerList[fn][2];
		fc = this.lightLocal[fn] * gf3;
		c.r = fc * this.tintR;
		c.g = fc * this.tintG;
		c.b = fc * this.tintB;
		c.brtex = gl3;
		c = this.cornerList[fn][3];
		fc = this.lightLocal[fn] * gf4;
		c.r = fc * this.tintR;
		c.g = fc * this.tintG;
		c.b = fc * this.tintB;
		c.brtex = gl4;
	}
	
	public void doLightSmooth(int sides) {
		for (int i = 0; i < 6; ++i) {
			if ((sides & 1 << i) != 0) {
				this.lightSmoothFace(i);
			}
		}
		
	}
	
	private void doLightFlat(int sides) {
		for (int i = 0; i < this.cornerList.length; ++i) {
			if ((sides & 1 << i) != 0) {
				TexVertex c = this.cornerList[i][0];
				c.r = this.lightFlat[i] * this.lightLocal[i] * this.tintR;
				c.g = this.lightFlat[i] * this.lightLocal[i] * this.tintG;
				c.b = this.lightFlat[i] * this.lightLocal[i] * this.tintB;
				c.brtex = this.brightLocal[i];
			}
		}
		
	}
	
	public void renderFlat(int sides) {
		//bindBlockTexture();
		//Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		Tessellator tessellator = Tessellator.instance;
		
		for (int i = 0; i < this.cornerList.length; ++i) {
			if ((sides & 1 << i) != 0) {
				TexVertex c = this.cornerList[i][0];
				tessellator.setColorOpaque_F(c.r, c.g, c.b);
				Vector3 v;
				if (this.useNormal) {
					v = this.vertexList[c.vtx];
					c = this.cornerList[i][1];
					Vector3 v1 = new Vector3(this.vertexList[c.vtx]);
					c = this.cornerList[i][2];
					Vector3 v2 = new Vector3(this.vertexList[c.vtx]);
					v1.subtract(v);
					v2.subtract(v);
					v1.crossProduct(v2);
					v1.normalize();
					tessellator.setNormal((float) v1.x, (float) v1.y, (float) v1.z);
				} else {
					tessellator.setBrightness(c.brtex);
				}
				
				for (int j = 0; j < 4; ++j) {
					c = this.cornerList[i][j];
					v = this.vertexList[c.vtx];
					tessellator.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
				}
			}
		}
		//Minecraft.getMinecraft().renderEngine.deleteTexture(TextureMap.locationBlocksTexture);
	}
	
	public void renderRangeFlat(int st, int ed) {
		//bindBlockTexture();
		Tessellator tessellator = Tessellator.instance;
		
		for (int i = st; i < ed; ++i) {
			TexVertex c = this.cornerList[i][0];
			tessellator.setColorRGBA_F(c.r * this.tintR, c.g * this.tintG, c.b * this.tintB, this.tintA);
			Vector3 v;
			if (this.useNormal) {
				v = this.vertexList[c.vtx];
				c = this.cornerList[i][1];
				Vector3 v1 = new Vector3(this.vertexList[c.vtx]);
				c = this.cornerList[i][2];
				Vector3 v2 = new Vector3(this.vertexList[c.vtx]);
				v1.subtract(v);
				v2.subtract(v);
				v1.crossProduct(v2);
				v1.normalize();
				tessellator.setNormal((float) v1.x, (float) v1.y, (float) v1.z);
			} else {
				tessellator.setBrightness(c.brtex);
			}
			
			for (int j = 0; j < 4; ++j) {
				c = this.cornerList[i][j];
				v = this.vertexList[c.vtx];
				tessellator.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
			}
		}
		/*//TODO:
		if (this.texBinds != null) {
			RenderLib.unbindTexture();
		}*/
		
	}
	
	public void renderAlpha(int sides, float alpha) {
		Tessellator tessellator = Tessellator.instance;
		
		for (int i = 0; i < this.cornerList.length; ++i) {
			if ((sides & 1 << i) != 0) {
				TexVertex c = this.cornerList[i][0];
				tessellator.setColorRGBA_F(c.r, c.g, c.b, alpha);
				if (!this.useNormal) {
					tessellator.setBrightness(c.brtex);
				}
				
				for (int j = 0; j < 4; ++j) {
					c = this.cornerList[i][j];
					Vector3 v = this.vertexList[c.vtx];
					tessellator.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
				}
			}
		}
	}
	
	public void renderSmooth(int sides) {
		//bindBlockTexture();
		Tessellator tessellator = Tessellator.instance;
		
		for (int i = 0; i < this.cornerList.length; ++i) {
			if ((sides & 1 << i) != 0) {
				for (int j = 0; j < 4; ++j) {
					TexVertex c = this.cornerList[i][j];
					tessellator.setColorOpaque_F(c.r, c.g, c.b);
					if (!this.useNormal) {
						tessellator.setBrightness(c.brtex);
					}
					
					Vector3 v = this.vertexList[c.vtx];
					tessellator.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
				}
			}
		}
		/*//TODO:
		if (this.texBinds != null) {
			RenderLib.unbindTexture();
		}*/
	}
	
	public void renderFaces(int faces) {
		this.doMappingBox(faces);
		this.doLightLocal(faces);
		this.renderFlat(faces);
	}
	
	public void renderGlobFaces(int faces) {
		this.doMappingBox(faces);
		this.doLightLocal(faces);
		if (Minecraft.isAmbientOcclusionEnabled() && !this.forceFlat) {
			this.doLightSmooth(faces);
			this.renderSmooth(faces);
		} else {
			this.doLightFlat(faces);
			this.renderFlat(faces);
		}
		
	}
	
	public void drawPoints(int... points) {
		Tessellator tessellator = Tessellator.instance;
		int[] arr$ = points;
		int len$ = points.length;
		
		for (int i$ = 0; i$ < len$; ++i$) {
			int p = arr$[i$];
			Vector3 v = this.vertexList[p];
			tessellator.addVertex(v.x, v.y, v.z);
		}
	}
	
	public void bindModel(RenderModel model) {
		this.vertexList = new Vector3[model.vertexList.length];
		for (int i = 0; i < this.vertexList.length; ++i) {
			Vector3 v = new Vector3(model.vertexList[i]);
			this.basis.rotate(v);
			v.add(this.globalOrigin);
			this.vertexList[i] = v;
		}
		this.cornerList = model.texList;
		this.boundModel = model;
	}
	
	public void bindModelOffset(RenderModel model, double ofx, double ofy, double ofz) {
		this.vertexList = new Vector3[model.vertexList.length];
		
		for (int i = 0; i < this.vertexList.length; ++i) {
			Vector3 v = new Vector3(model.vertexList[i]);
			v.add(this.localOffset.x - ofx, this.localOffset.y - ofy, this.localOffset.z - ofz);
			this.basis.rotate(v);
			v.add(ofx, ofy, ofz);
			v.add(this.globalOrigin);
			this.vertexList[i] = v;
		}
		
		this.cornerList = model.texList;
		this.boundModel = model;
	}
	
	public void renderModelGroup(int gr, int sgr) {
		for (int i = 0; i < this.cornerList.length; ++i) {
			TexVertex c = this.cornerList[i][0];
			c.brtex = this.brightLocal[0];
		}
		this.renderRangeFlat(this.boundModel.groups[gr][sgr][0], this.boundModel.groups[gr][sgr][1]);
	}
	
	public void renderModel(RenderModel model) {
		this.bindModel(model);
		for (int i = 0; i < this.cornerList.length; ++i) {
			TexVertex c = this.cornerList[i][0];
			c.brtex = this.brightLocal[0];
		}
		this.renderRangeFlat(0, this.cornerList.length);
	}
	
	public RenderContext() {
		int i;
		for (i = 0; i < 8; ++i) {
			this.vertexListBox[i] = new Vector3();
		}
		int[][] vtxl = new int[][] { { 7, 6, 5, 4 }, { 0, 1, 2, 3 }, { 0, 4, 5, 1 }, { 2, 6, 7, 3 }, { 1, 5, 6, 2 }, { 3, 7, 4, 0 } };
		
		for (i = 0; i < 6; ++i) {
			for (int j = 0; j < 4; ++j) {
				this.cornerListBox[i][j] = new TexVertex();
				this.cornerListBox[i][j].vtx = vtxl[i][j];
			}
		}
		this.setDefaults();
	}
	
	/*public void bindAtlas(int atlasIndex) {
		Minecraft.getMinecraft().renderEngine.bindTexture(atlasIndex == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
	}*/
}
