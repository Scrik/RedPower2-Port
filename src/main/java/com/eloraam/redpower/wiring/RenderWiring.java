package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.RenderCovers;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public abstract class RenderWiring extends RenderCovers {
	
	private float wireWidth;
	private float wireHeight;
	private IIcon[][] sidetex = new IIcon[7][6];
	
	public RenderWiring(Block bl) {
		super(bl);
	}
	
	public void setWireSize(float w, float h) {
		this.wireWidth = w * 0.5F;
		this.wireHeight = h;
	}
	
	public void renderSideWire(int n) {
		super.context.setLocalLights(0.5F, 1.0F, 0.7F, 0.7F, 0.7F, 0.7F);
		switch (n) {
			case 2:
				super.context.setSize(0.0D, 0.0D, 0.5F - this.wireWidth,
						0.5F - this.wireWidth, this.wireHeight,
						0.5F + this.wireWidth);
				super.context.calcBounds();
				super.context.renderFaces(54);
				break;
			case 3:
				super.context.setSize(0.5F + this.wireWidth, 0.0D,
						0.5F - this.wireWidth, 1.0D, this.wireHeight,
						0.5F + this.wireWidth);
				super.context.calcBounds();
				super.context.renderFaces(58);
				break;
			case 4:
				super.context.setSize(0.5F - this.wireWidth, 0.0D, 0.0D,
						0.5F + this.wireWidth, this.wireHeight,
						0.5F - this.wireWidth);
				super.context.calcBounds();
				super.context.renderFaces(30);
				break;
			case 5:
				super.context.setSize(0.5F - this.wireWidth, 0.0D,
						0.5F + this.wireWidth, 0.5F + this.wireWidth,
						this.wireHeight, 1.0D);
				super.context.calcBounds();
				super.context.renderFaces(46);
		}
		
	}
	
	public void setSideTex(IIcon top, IIcon cent, IIcon cfix) {
		int j;
		for (j = 0; j < 6; ++j) {
			this.sidetex[0][j] = j >> 1 == 0 ? cent : cfix;
		}
		
		int i;
		for (i = 1; i < 3; ++i) {
			for (j = 0; j < 6; ++j) {
				this.sidetex[i][j] = j >> 1 == i ? cent : top;
			}
		}
		
		for (i = 1; i < 3; ++i) {
			for (j = 0; j < 6; ++j) {
				this.sidetex[i + 2][j] = j >> 1 == i ? cent : cfix;
			}
		}
		
		for (i = 0; i < 6; ++i) {
			this.sidetex[5][i] = top;
			this.sidetex[6][i] = top;
		}
		
		this.sidetex[5][4] = cent;
		this.sidetex[5][5] = cent;
		this.sidetex[6][2] = cent;
		this.sidetex[6][3] = cent;
		this.sidetex[5][0] = cent;
		this.sidetex[6][0] = cent;
		super.context.setIcon(this.sidetex);
	}
	
	public void setSideTexJumbo(IIcon sides, IIcon top, IIcon cent, IIcon centside, IIcon end, IIcon corners) {
		int j;
		for (j = 0; j < 6; ++j) {
			this.sidetex[0][j] = j >> 1 == 0 ? cent : centside;
		}
		
		int i;
		for (i = 1; i < 3; ++i) {
			for (j = 0; j < 6; ++j) {
				this.sidetex[i][j] = j >> 1 == 0 ? top : (j >> 1 == i ? end : sides);
			}
		}
		
		for (i = 1; i < 3; ++i) {
			for (j = 0; j < 6; ++j) {
				this.sidetex[i + 2][j] = j >> 1 == 0 ? top : (j >> 1 == i ? end : centside);
			}
		}
		
		for (i = 0; i < 6; ++i) {
			this.sidetex[5][i] = top;
			this.sidetex[6][i] = top;
		}
		
		this.sidetex[5][4] = corners;
		this.sidetex[5][5] = corners;
		this.sidetex[6][2] = corners;
		this.sidetex[6][3] = corners;
		this.sidetex[5][0] = corners;
		this.sidetex[6][0] = corners;
		super.context.setIcon(this.sidetex);
	}
	
	public void renderSideWires(int cs, int ucs, int fn) {
		int fxl = 0;
		int fzl = 0;
		int fc = 62;
		int fxs1 = 0;
		int fxs2 = 0;
		int fzs1 = 0;
		int fzs2 = 0;
		byte stb = 3;
		float x1 = (ucs & 4) == 0 ? 0.0F : this.wireHeight;
		float x2 = (ucs & 8) == 0 ? 1.0F : 1.0F - this.wireHeight;
		float z1 = (ucs & 1) == 0 ? 0.0F : this.wireHeight;
		float z2 = (ucs & 2) == 0 ? 1.0F : 1.0F - this.wireHeight;
		super.context.setLocalLights(0.5F, 1.0F, 0.7F, 0.7F, 0.7F, 0.7F);
		cs |= ucs;
		if ((cs & 12) == 0) {
			fzl |= 62;
			fc = 0;
			if ((cs & 1) == 0) {
				z1 = 0.26F;
			}
			
			if ((cs & 2) == 0) {
				z2 = 0.74F;
			}
			
			stb = 1;
		} else if ((cs & 3) == 0) {
			fxl |= 62;
			fc = 0;
			if ((cs & 4) == 0) {
				x1 = 0.26F;
			}
			
			if ((cs & 8) == 0) {
				x2 = 0.74F;
			}
			
			stb = 1;
		} else {
			if ((cs & 7) == 3) {
				fzl |= 28;
				fc &= -17;
			} else {
				if ((cs & 1) > 0) {
					fzs1 |= 20;
				}
				
				if ((cs & 2) > 0) {
					fzs2 |= 24;
				}
			}
			
			if ((cs & 11) == 3) {
				fzl |= 44;
				fc &= -33;
			} else {
				if ((cs & 1) > 0) {
					fzs1 |= 36;
				}
				
				if ((cs & 2) > 0) {
					fzs2 |= 40;
				}
			}
			
			if ((cs & 13) == 12) {
				fxl |= 52;
				fc &= -5;
			} else {
				if ((cs & 4) > 0) {
					fxs1 |= 20;
				}
				
				if ((cs & 8) > 0) {
					fxs2 |= 36;
				}
			}
			
			if ((cs & 14) == 12) {
				fxl |= 56;
				fc &= -9;
			} else {
				if ((cs & 4) > 0) {
					fxs1 |= 24;
				}
				
				if ((cs & 8) > 0) {
					fxs2 |= 40;
				}
			}
			
			if ((cs & 1) > 0) {
				fzs1 |= 2;
				fc &= -5;
			}
			
			if ((cs & 2) > 0) {
				fzs2 |= 2;
				fc &= -9;
			}
			
			if ((cs & 4) > 0) {
				fxs1 |= 2;
				fc &= -17;
			}
			
			if ((cs & 8) > 0) {
				fxs2 |= 2;
				fc &= -33;
			}
			
			if ((cs & 64) > 0) {
				fxs1 |= 1;
				fxs2 |= 1;
				fzs1 |= 1;
				fzs2 |= 1;
				fc |= 1;
			}
		}
		
		int tmpf = ~((ucs & 12) << 2);
		fxl &= tmpf;
		fxs1 &= tmpf;
		fxs2 &= tmpf;
		tmpf = ~((ucs & 3) << 2);
		fzl &= tmpf;
		fzs1 &= tmpf;
		fzs2 &= tmpf;
		char fxf = '\u8b80';
		int fzf = 217640;
		int fcf = 220032;
		switch (fn) {
			case 1:
			case 2:
			case 4:
				fxf = 7512;
				fcf = 220488;
			case 3:
			default:
				if (fxl > 0) {
					super.context.setSize(x1, 0.0D, 0.5F - this.wireWidth, x2,
							this.wireHeight, 0.5F + this.wireWidth);
					super.context.calcBounds();
					super.context.setTexFlags(fxf);
					super.context.setIconIndex(stb + 1);
					super.context.renderFaces(fxl);
				}
				
				if (fzl > 0) {
					super.context.setSize(0.5F - this.wireWidth, 0.0D, z1,
							0.5F + this.wireWidth, this.wireHeight, z2);
					super.context.calcBounds();
					super.context.setTexFlags(fzf);
					super.context.setIconIndex(stb);
					super.context.renderFaces(fzl);
				}
				
				if (fc > 0) {
					super.context.setSize(0.5F - this.wireWidth, 0.0D,
							0.5F - this.wireWidth, 0.5F + this.wireWidth,
							this.wireHeight, 0.5F + this.wireWidth);
					super.context.calcBounds();
					super.context.setTexFlags(fcf);
					super.context.setIconIndex(0);
					super.context.renderFaces(fc);
				}
				
				if (fxs1 > 0) {
					super.context.setSize(x1, 0.0D, 0.5F - this.wireWidth,
							0.5F - this.wireWidth, this.wireHeight,
							0.5F + this.wireWidth);
					super.context.calcBounds();
					super.context.setTexFlags(fxf);
					super.context.setIconIndex(stb + 1);
					super.context.renderFaces(fxs1);
				}
				
				if (fxs2 > 0) {
					super.context.setSize(0.5F + this.wireWidth, 0.0D,
							0.5F - this.wireWidth, x2, this.wireHeight,
							0.5F + this.wireWidth);
					super.context.calcBounds();
					super.context.setTexFlags(fxf);
					super.context.setIconIndex(stb + 1);
					super.context.renderFaces(fxs2);
				}
				
				if (fzs1 > 0) {
					super.context.setSize(0.5F - this.wireWidth, 0.0D, z1,
							0.5F + this.wireWidth, this.wireHeight,
							0.5F - this.wireWidth);
					super.context.calcBounds();
					super.context.setTexFlags(fzf);
					super.context.setIconIndex(stb);
					super.context.renderFaces(fzs1);
				}
				
				if (fzs2 > 0) {
					super.context.setSize(0.5F - this.wireWidth, 0.0D,
							0.5F + this.wireWidth, 0.5F + this.wireWidth,
							this.wireHeight, z2);
					super.context.calcBounds();
					super.context.setTexFlags(fzf);
					super.context.setIconIndex(stb);
					super.context.renderFaces(fzs2);
				}
				
				if (fn < 2) {
					super.context.setTexFlags(0);
				} else {
					if ((ucs & 2) > 0) {
						super.context.setSize(0.5F - this.wireWidth, 0.0D,
								1.0F - this.wireHeight, 0.5F + this.wireWidth,
								this.wireHeight, 1.0D);
						super.context.calcBounds();
						super.context.setTexFlags(73728);
						super.context.setIconIndex(5);
						super.context.renderFaces(48);
					}
					
					if ((ucs & 4) > 0) {
						super.context.setSize(0.0D, 0.0D,
								0.5F - this.wireWidth, this.wireHeight,
								this.wireHeight, 0.5F + this.wireWidth);
						super.context.calcBounds();
						if (fn != 2 && fn != 4) {
							super.context.setTexFlags(1728);
						} else {
							super.context.setTexFlags(1152);
						}
						
						super.context.setIconIndex(6);
						super.context.renderFaces(12);
					}
					
					if ((ucs & 8) > 0) {
						super.context.setSize(1.0F - this.wireHeight, 0.0D,
								0.5F - this.wireWidth, 1.0D, this.wireHeight,
								0.5F + this.wireWidth);
						super.context.calcBounds();
						if (fn != 2 && fn != 4) {
							super.context.setTexFlags(1152);
						} else {
							super.context.setTexFlags(1728);
						}
						
						super.context.setIconIndex(6);
						super.context.renderFaces(12);
					}
					
					super.context.setTexFlags(0);
				}
		}
	}
	
	public void renderEndCaps(int cs, int fn) {
		if (cs != 0) {
			super.context.setIconIndex(5);
			if ((cs & 1) > 0) {
				super.context.setSize(0.5F - this.wireWidth, 0.0D,
						1.0F - this.wireHeight, 0.5F + this.wireWidth,
						this.wireHeight, 1.0D);
				super.context.setRelPos(0.0D, 0.0D, -1.0D);
				super.context.setTexFlags('\u962c');
				super.context.setLocalLights(0.7F, 1.0F, 0.7F, 1.0F, 0.7F, 0.7F);
				super.context.calcBounds();
				super.context.renderFaces(55);
			}
			
			if ((cs & 2) > 0) {
				super.context.setSize(0.5F - this.wireWidth, 0.0D, 0.0D, 0.5F + 
						this.wireWidth, this.wireHeight, this.wireHeight);
				super.context.setRelPos(0.0D, 0.0D, 1.0D);
				super.context.setTexFlags('\u962c');
				super.context.setLocalLights(0.7F, 1.0F, 0.7F, 1.0F, 0.7F, 0.7F);
				super.context.calcBounds();
				super.context.renderFaces(59);
			}
			
			super.context.setIconIndex(6);
			if ((cs & 4) > 0) {
				super.context.setSize(1.0F - this.wireHeight, 0.0D,
						0.5F - this.wireWidth, 1.0D, this.wireHeight,
						0.5F + this.wireWidth);
				super.context.setRelPos(-1.0D, 0.0D, 0.0D);
				if (fn != 2 && fn != 4) {
					super.context.setTexFlags(3);
				} else {
					super.context.setTexFlags('\ub25a');
				}
				
				super.context
						.setLocalLights(0.7F, 1.0F, 0.7F, 0.7F, 1.0F, 0.7F);
				super.context.calcBounds();
				super.context.renderFaces(31);
			}
			
			if ((cs & 8) > 0) {
				super.context
						.setSize(0.0D, 0.0D, 0.5F - this.wireWidth,
								this.wireHeight, this.wireHeight,
								0.5F + this.wireWidth);
				super.context.setRelPos(1.0D, 0.0D, 0.0D);
				if (fn != 2 && fn != 4) {
					super.context.setTexFlags(102977);
				} else {
					super.context.setTexFlags(24);
				}
				
				super.context
						.setLocalLights(0.7F, 1.0F, 0.7F, 0.7F, 0.7F, 1.0F);
				super.context.calcBounds();
				super.context.renderFaces(47);
			}
			
			super.context.setRelPos(0.0D, 0.0D, 0.0D);
		}
	}
	
	public void renderWireBlock(int consides, int cons, int indcon, int indconex) {
		int ucons = 0;
		indcon &= ~indconex;
		if ((consides & 1) > 0) {
			ucons |= 1118464;
		}
		
		if ((consides & 2) > 0) {
			ucons |= 2236928;
		}
		
		if ((consides & 4) > 0) {
			ucons |= 4456465;
		}
		
		if ((consides & 8) > 0) {
			ucons |= 8912930;
		}
		
		if ((consides & 16) > 0) {
			ucons |= 17476;
		}
		
		if ((consides & 32) > 0) {
			ucons |= '\u8888';
		}
		
		if ((consides & 1) > 0) {
			super.context.setOrientation(0, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 0),
					RedPowerLib.mapConToLocal(ucons, 0), 0);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 0), 0);
		}
		
		if ((consides & 2) > 0) {
			super.context.setOrientation(1, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 1),
					RedPowerLib.mapConToLocal(ucons, 1), 1);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 1), 1);
		}
		
		if ((consides & 4) > 0) {
			super.context.setOrientation(2, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 2),
					RedPowerLib.mapConToLocal(ucons, 2), 2);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indcon, 2) & 14, 2);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 2), 2);
		}
		
		if ((consides & 8) > 0) {
			super.context.setOrientation(3, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 3),
					RedPowerLib.mapConToLocal(ucons, 3), 3);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indcon, 3) & 14, 3);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 3), 3);
		}
		
		if ((consides & 16) > 0) {
			super.context.setOrientation(4, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 4),
					RedPowerLib.mapConToLocal(ucons, 4), 4);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indcon, 4) & 14, 4);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 4), 4);
		}
		
		if ((consides & 32) > 0) {
			super.context.setOrientation(5, 0);
			this.renderSideWires(RedPowerLib.mapConToLocal(cons, 5),
					RedPowerLib.mapConToLocal(ucons, 5), 5);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indcon, 5) & 14, 5);
			this.renderEndCaps(RedPowerLib.mapConToLocal(indconex, 5), 5);
		}
	}
	
	void setJacketIcons(int cons, IIcon[] tex, IIcon st) {
		super.context.setIcon((cons & 1) > 0 ? st : tex[0],
				(cons & 2) > 0 ? st : tex[1], (cons & 4) > 0 ? st : tex[2],
				(cons & 8) > 0 ? st : tex[3], (cons & 16) > 0 ? st : tex[4],
				(cons & 32) > 0 ? st : tex[5]);
	}
	
	public void renderCenterBlock(int cons, IIcon[] icon, IIcon sidtex) {
		if (cons == 0) {
			this.setJacketIcons(3, icon, sidtex);
			super.context.renderBox(63, 0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
		} else if (cons == 3) {
			this.setJacketIcons(3, icon, sidtex);
			super.context.renderBox(63, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
		} else if (cons == 12) {
			this.setJacketIcons(12, icon, sidtex);
			super.context.renderBox(63, 0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
		} else if (cons == 48) {
			this.setJacketIcons(48, icon, sidtex);
			super.context.renderBox(63, 0.0D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
		} else {
			if (Integer.bitCount(cons) > 1) {
				super.context.setIcon(icon);
			} else {
				int rc = cons;
				if (cons == 0) {
					rc = 3;
				}
				
				rc = (rc & 21) << 1 | (rc & 42) >> 1;
				this.setJacketIcons(rc, icon, sidtex);
			}
			
			super.context.renderBox(63 ^ cons, 0.25D, 0.25D, 0.25D, 0.75D,
					0.75D, 0.75D);
			if ((cons & 1) > 0) {
				this.setJacketIcons(1, icon, sidtex);
				super.context.renderBox(61, 0.25D, 0.0D, 0.25D, 0.75D, 0.25D,
						0.75D);
			}
			
			if ((cons & 2) > 0) {
				this.setJacketIcons(2, icon, sidtex);
				super.context.renderBox(62, 0.25D, 0.75D, 0.25D, 0.75D, 1.0D,
						0.75D);
			}
			
			if ((cons & 4) > 0) {
				this.setJacketIcons(4, icon, sidtex);
				super.context.renderBox(55, 0.25D, 0.25D, 0.0D, 0.75D, 0.75D,
						0.25D);
			}
			
			if ((cons & 8) > 0) {
				this.setJacketIcons(8, icon, sidtex);
				super.context.renderBox(59, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D,
						1.0D);
			}
			
			if ((cons & 16) > 0) {
				this.setJacketIcons(16, icon, sidtex);
				super.context.renderBox(31, 0.0D, 0.25D, 0.25D, 0.25D, 0.75D,
						0.75D);
			}
			
			if ((cons & 32) > 0) {
				this.setJacketIcons(32, icon, sidtex);
				super.context.renderBox(47, 0.75D, 0.25D, 0.25D, 1.0D, 0.75D,
						0.75D);
			}
			
			//super.context.clearTexFiles(); //TODO:
		}
	}
}
