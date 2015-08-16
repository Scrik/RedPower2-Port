package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.wiring.RenderWiring;
import com.eloraam.redpower.wiring.TileInsulatedWire;
import com.eloraam.redpower.wiring.TileRedwire;
import com.eloraam.redpower.wiring.TileWiring;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderRedwire extends RenderWiring {
	
	//private int[] glowtex = new int[] { 5, 5, 5, 5, 5, 5 };
	
	public RenderRedwire(Block bl) {
		super(bl);
	}
	
	static TileRedwire getTileEntity(IBlockAccess iba, int i, int j, int k) {
		TileEntity te = iba.getTileEntity(i, j, k);
		return !(te instanceof TileRedwire) ? null : (TileRedwire) te;
	}
	
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
	}
	
	@Override
	public void renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iba, int i, int j, int k, int md) {
		//Tessellator tessellator = Tessellator.instance;
		super.context.setBrightness(super.block.getMixedBrightnessForBlock(iba, i, j, k));
		TileCovered tc = (TileCovered) CoreLib.getTileEntity(iba, i, j, k, TileCovered.class);
		if (tc != null) {
			super.context.setTexFlags(55);
			super.context.setPos(i, j, k);
			if (tc.CoverSides > 0) {
				super.context.setTint(1.0F, 1.0F, 1.0F);
				if (renderblocks.overrideBlockTexture != null) { // TODO: >= 0
					super.context.setIcon(renderblocks.overrideBlockTexture);
					super.context.lockTexture = true;
					super.context.forceFlat = true;
				}
				super.context.readGlobalLights(iba, i, j, k);
				this.renderCovers(tc.CoverSides, tc.Covers);
				super.context.forceFlat = false;
				super.context.lockTexture = false;
			}
			
			if (md != 0) {
				TileWiring tw = (TileWiring) tc;
				int cons = tw.getConnectionMask();
				int indcon = tw.getExtConnectionMask();
				int indconex = tw.EConEMask;
				cons |= indcon;
				if (md == 1) {
					TileRedwire tx = (TileRedwire) tw;
					super.context.setTint(0.3F + 0.7F * (tx.PowerState / 255.0F), 0.0F, 0.0F);
					this.setSideTex(super.block.getIcon(1, md), super.block.getIcon(2, md), super.block.getIcon(1, md));
					this.setWireSize(0.125F, 0.125F);
				} else if (md == 2) {
					TileInsulatedWire tx1 = (TileInsulatedWire) tw;
					super.context.setTint(1.0F, 1.0F, 1.0F);
					this.setSideTex(super.block.getIcon(16 + tw.Metadata, md), super.block.getIcon((tx1.PowerState > 0 ? 48 : 32) + tw.Metadata, md), super.block.getIcon(16 + tw.Metadata, md));
					this.setWireSize(0.25F, 0.188F);
				} else if (md == 3) {
					super.context.setTint(1.0F, 1.0F, 1.0F);
					if (tw.Metadata == 0) {
						this.setSideTex(super.block.getIcon(3, md), super.block.getIcon(4, md), super.block.getIcon(3, md));
					} else {
						this.setSideTex(super.block.getIcon(63 + tw.Metadata, md), super.block.getIcon(79 + tw.Metadata, md), super.block.getIcon(3, md));
					}
					this.setWireSize(0.375F, 0.25F);
				} else if (md == 5) {
					super.context.setTint(1.0F, 1.0F, 1.0F);
					if (tw.Metadata == 0) {
						this.setSideTex(super.block.getIcon(8, md), super.block.getIcon(9, md), super.block.getIcon(8, md));
						this.setWireSize(0.25F, 0.188F);
					} else if (tw.Metadata == 1) {
						this.setSideTex(super.block.getIcon(11, md), super.block.getIcon(12, md), super.block.getIcon(11, md));
						this.setWireSize(0.375F, 0.25F);
					} else if (tw.Metadata == 2) {
						this.setSideTexJumbo(super.block.getIcon(96, md), super.block.getIcon(97, md), super.block.getIcon(98, md), super.block.getIcon(99, md), super.block.getIcon(100, md), super.block.getIcon(101, md));
						this.setWireSize(0.5F, 0.3125F);
					}
				}
				
				this.renderWireBlock(tw.ConSides, cons, indcon, indconex); // TODO: TEST
				
				if (md == 1 || md == 3 || md == 5) {
					if ((tw.ConSides & 64) != 0) {
						super.context.setTexFlags(0);
						super.context.setOrientation(0, 0);
						super.context.setTint(1.0F, 1.0F, 1.0F);
						super.context.setLocalLights(0.5F, 1.0F, 0.7F, 0.7F,
								0.7F, 0.7F);
						int tx2;
						if (md == 1) {
							tx2 = ((TileRedwire) tw).PowerState > 0 ? 6 : 5;
						} else if (md == 3) {
							tx2 = 7;
						} else {
							tx2 = 10;
						}
						this.renderCenterBlock(cons >> 24 | tw.ConSides & 63, RenderCovers.coverIcons[tw.CenterPost], super.block.getIcon(tx2, md));
					}
				}
			}
		}
	}
	
	@Override
	public void renderInvBlock(RenderBlocks renderblocks, int md) {
		Tessellator tessellator = Tessellator.instance;
		super.block.setBlockBoundsForItemRender();
		int bid = md >> 8;
		md &= 255;
		super.context.setDefaults();
		super.context.setTexFlags(55);
		super.context.setPos(-0.5D, -0.5D, -0.5D);
		float th;
		switch (bid) {
			/*case 0:
			case 16:
			case 17:
			case 27:
			case 28:
			case 29:*/
			case 30:
				switch (bid) {
					case 0:
						th = 0.063F;
						break;
					case 16:
						th = 0.125F;
						break;
					case 17:
						th = 0.25F;
						break;
					case 27:
						th = 0.188F;
						break;
					case 28:
						th = 0.313F;
						break;
					case 29:
						th = 0.375F;
						break;
					case 30:
						th = 0.438F;
						break;
					default:
						return;
				}
				
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				super.context.setSize(0.0D, 0.0D, 0.5F - th, 1.0D, 1.0D, 0.5F + th);
				super.context.calcBounds();
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderFaces(63);
				super.context.useNormal = false;
				tessellator.draw();
				return;
			/*case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 46:
			case 47:
			case 48:
			case 49:
			case 50:
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:*/
			default:
				super.context.setPos(-0.5D, -0.20000000298023224D, -0.5D);
				super.context.setOrientation(0, 0);
				if (bid == 1) {
					this.setSideTex(super.block.getIcon(1, md), super.block.getIcon(2, md), super.block.getIcon(1, md));
					this.setWireSize(0.125F, 0.125F);
					super.context.setTint(1.0F, 0.0F, 0.0F);
				} else if (bid == 2) {
					this.setSideTex(super.block.getIcon(16 + md, md), super.block.getIcon(32 + md, md), super.block.getIcon(16 + md, md));
					this.setWireSize(0.25F, 0.188F);
				} else if (bid == 3) {
					if (md == 0) {
						this.setSideTex(super.block.getIcon(3, md), super.block.getIcon(4, md), super.block.getIcon(3, md));
					} else {
						this.setSideTex(super.block.getIcon(63 + md, md), super.block.getIcon(79 + md, md), super.block.getIcon(3, md));
					}
					
					this.setWireSize(0.375F, 0.25F);
				} else {
					if (bid != 5) {
						return;
					}
					
					if (md == 0) {
						this.setSideTex(super.block.getIcon(8, md), super.block.getIcon(9, md), super.block.getIcon(8, md));
						this.setWireSize(0.25F, 0.188F);
					} else if (md == 1) {
						this.setSideTex(super.block.getIcon(11, md), super.block.getIcon(12, md), super.block.getIcon(11, md));
						this.setWireSize(0.375F, 0.25F);
					} else if (md == 2) {
						this.setSideTexJumbo(super.block.getIcon(96, md), super.block.getIcon(97, md), super.block.getIcon(98, md), super.block.getIcon(99, md), super.block.getIcon(100, md), super.block.getIcon(101, md));
						this.setWireSize(0.5F, 0.3125F);
					}
				}
				
				super.context.useNormal = true;
				//RenderLib.setRedPowerTexture(); //TODO: Check this
				tessellator.startDrawingQuads();
				this.renderSideWires(127, 0, 0);
				tessellator.draw();
				//RenderLib.setDefaultTexture();
				super.context.useNormal = false;
				return;
			/*case 18:
			case 19:
			case 20:
			case 35:
			case 36:
			case 37:*/
			case 38:
				switch (bid) {
					case 18:
						th = 0.063F;
						break;
					case 19:
						th = 0.125F;
						break;
					case 20:
						th = 0.25F;
						break;
					/*case 21:
					case 22:
					case 23:
					case 24:
					case 25:
					case 26:
					case 27:
					case 28:
					case 29:
					case 30:
					case 31:
					case 32:
					case 33:
					case 34:*/
					default:
						return;
					case 35:
						th = 0.188F;
						break;
					case 36:
						th = 0.313F;
						break;
					case 37:
						th = 0.375F;
						break;
					case 38:
						th = 0.438F;
				}
				
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				super.context.setSize(0.5F - th, 0.5F - th, 0.5F - th, 0.5F + th, 0.5F + th, 0.5F + th);
				super.context.calcBounds();
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderFaces(63);
				super.context.useNormal = false;
				tessellator.draw();
				return;
			/*case 21:
			case 22:
			case 23:
			case 39:
			case 40:
			case 41:*/
			case 42:
				switch (bid) {
					case 21:
						th = 0.063F;
						break;
					case 22:
						th = 0.125F;
						break;
					case 23:
						th = 0.25F;
						break;
					/*case 24:
					case 25:
					case 26:
					case 27:
					case 28:
					case 29:
					case 30:
					case 31:
					case 32:
					case 33:
					case 34:
					case 35:
					case 36:
					case 37:
					case 38:*/
					default:
						return;
					case 39:
						th = 0.188F;
						break;
					case 40:
						th = 0.313F;
						break;
					case 41:
						th = 0.375F;
						break;
					case 42:
						th = 0.438F;
				}
				
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				super.context.setSize(0.5F - th, 0.0D, 0.5F - th, 0.5F + th, 1.0D, 0.5F + th);
				super.context.calcBounds();
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderFaces(63);
				super.context.useNormal = false;
				tessellator.draw();
				return;
			/*case 24:
			case 25:
			case 26:
			case 31:
			case 32:
			case 33:*/
			case 34:
				switch (bid) {
					case 24:
						th = 0.063F;
						break;
					case 25:
						th = 0.125F;
						break;
					case 26:
						th = 0.25F;
						break;
					/*case 27:
					case 28:
					case 29:
					case 30:*/
					default:
						return;
					case 31:
						th = 0.188F;
						break;
					case 32:
						th = 0.313F;
						break;
					case 33:
						th = 0.375F;
						break;
					case 34:
						th = 0.438F;
				}
				
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderBox(63, 0.0D, 0.0D, 0.5F - th, 0.25D, 1.0D, 0.5F + th);
				super.context.renderBox(63, 0.75D, 0.0D, 0.5F - th, 1.0D, 1.0D, 0.5F + th);
				super.context.renderBox(15, 0.25D, 0.0D, 0.5F - th, 0.75D, 0.25D, 0.5F + th);
				super.context.renderBox(15, 0.25D, 0.75D, 0.5F - th, 0.75D, 1.0D, 0.5F + th);
				super.context.useNormal = false;
				tessellator.draw();
				return;
			/*case 43:
			case 44:*/
			case 45:
				switch (bid) {
					case 43:
						th = 0.125F;
						break;
					case 44:
						th = 0.25F;
						break;
					case 45:
						th = 0.375F;
						break;
					default:
						return;
				}
				
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				super.context.setSize(0.5F - th, 0.125D, 0.5F - th, 0.5F + th, 0.875D, 0.5F + th);
				super.context.calcBounds();
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderFaces(63);
				super.context.setSize(0.45F - th, 0.0D, 0.45F - th, 0.55F + th, 0.125D, 0.55F + th);
				super.context.calcBounds();
				super.context.renderFaces(63);
				super.context.setSize(0.45F - th, 0.875D, 0.45F - th, 0.55F + th, 1.0D, 0.55F + th);
				super.context.calcBounds();
				super.context.renderFaces(63);
				super.context.useNormal = false;
				tessellator.draw();
				return;
			/*case 64:
			case 65:*/
			case 66:
				super.context.setIcon(RenderCovers.coverIcons[md]);
				//super.context.setTexFile(CoverLib.coverTextureFiles[md]);
				tessellator.startDrawingQuads();
				super.context.useNormal = true;
				super.context.renderBox(60, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
				super.context.renderBox(15, 0.0D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
				super.context.renderBox(51, 0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
				tessellator.draw();
				tessellator.startDrawingQuads();
				//super.context.setTexFile("/eloraam/wiring/redpower1.png");
				super.context.setIcon(bid == 66 ? super.block.getIcon(10, md) : (bid == 64 ? super.block.getIcon(5, md) : super.block.getIcon(7, md)));
				super.context.renderBox(3, 0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
				super.context.renderBox(48, 0.0D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
				super.context.renderBox(12, 0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
				tessellator.draw();
				// TODO: super.context.clearTexFiles();
				super.context.useNormal = false;
		}
	}
}
