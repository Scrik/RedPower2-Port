package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreProxyClient;
import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.PowerLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderLogicSimple extends RenderLogic {

	private static RenderLogic.TorchPos[] torchMapLatch = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.3D, -0.15D, 0.0D, 0.8D), new RenderLogic.TorchPos(0.3D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapLatch2 = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.281D, -0.15D, -0.0938D, 0.8D), new RenderLogic.TorchPos(0.281D, -0.15D, 0.0938D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapLatch2b = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.281D, -0.15D, 0.0938D, 0.8D), new RenderLogic.TorchPos(0.281D, -0.15D, -0.0938D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapNor = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.094D, -0.25D, 0.031D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapOr = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.094D, -0.25D, 0.031D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapNand = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, 0.0D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapAnd = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, 0.0D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapXnor = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.28D, -0.25D, 0.0D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapXor = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.28D, -0.25D, 0.0D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapPulse = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.09D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.09D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapToggle = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.28D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.28D, -0.25D, -0.22D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapNot = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.031D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapBuffer = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.281D, -0.15D, 0.031D, 0.8D), new RenderLogic.TorchPos(-0.094D, -0.25D, 0.031D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapMux = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.156D, -0.25D, 0.031D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapMux2 = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031D, -0.25D, 0.22D, 0.7D), new RenderLogic.TorchPos(-0.031D, -0.25D, -0.22D, 0.7D), new RenderLogic.TorchPos(-0.156D, -0.25D, -0.031D, 0.7D), new RenderLogic.TorchPos(0.28D, -0.15D, 0.0D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapRepS = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.313D, -0.25D, -0.125D, 0.7D), new RenderLogic.TorchPos(-0.25D, -0.25D, 0.25D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapSync = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.28D, -0.25D, 0.0D, 0.7D)};
	private static RenderLogic.TorchPos[] torchMapDLatch = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.28D, -0.25D, -0.219D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.25D, -0.219D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.25D, -0.031D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.15D, 0.281D, 0.8D), new RenderLogic.TorchPos(0.281D, -0.15D, -0.094D, 0.8D)};
	private static RenderLogic.TorchPos[] torchMapDLatch2 = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.28D, -0.25D, 0.219D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.25D, 0.219D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.25D, 0.031D, 0.7D), new RenderLogic.TorchPos(0.031D, -0.15D, -0.281D, 0.8D), new RenderLogic.TorchPos(0.281D, -0.15D, 0.094D, 0.8D)};
	private static final int[] texIdxNor = new int[]{272, 288, 296, 312, 304, 316, 320};
	private static final int[] texIdxOr = new int[]{376, 384, 388, 416, 392, 418, 420};
	private static final int[] texIdxNand = new int[]{336, 352, 360, 324, 368, 328, 332};
	private static final int[] texIdxAnd = new int[]{400, 408, 412, 422, 396, 424, 426};
	private static final int[] texIdxNot = new int[]{432, 448, 456, 472, 464, 476, 428};
	private static final int[] texIdxBuf = new int[]{496, 504, 508, 257};
	private static Quat[] leverPositions = new Quat[2];


	public RenderLogicSimple(Block bl) {
		super(bl);
	}

	@Override
	protected int getTorchState(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		int eps1;
		switch(md) {
		case 0:
			if(tl.Deadmap > 1) {
				return ((tl.PowerState & 2) > 0?1:0) | ((tl.PowerState & 8) > 0?2:0);
			} else {
				if(!tl.Disabled && !tl.Active) {
					if(tl.Deadmap == 1) {
						return tl.Powered?1:2;
					}

					return tl.Powered?2:1;
				}

				return 0;
			}
		case 1:
			return tl.Powered?1:0;
		case 2:
			eps1 = tl.PowerState & ~tl.Deadmap;
			return (eps1 == 0?1:0) | (tl.Powered?2:0);
		case 3:
			eps1 = tl.PowerState | tl.Deadmap;
			return eps1 & 7 ^ 7;
		case 4:
			eps1 = tl.PowerState | tl.Deadmap;
			return eps1 & 7 ^ 7 | (tl.Powered?8:0);
		case 5:
		case 6:
			byte eps;
			switch(tl.PowerState & 5) {
			case 0:
				eps = 4;
				break;
			case 1:
				eps = 2;
				break;
			case 2:
			case 3:
			default:
				eps = 0;
				break;
			case 4:
				eps = 1;
			}

			if(md == 6) {
				return eps;
			}

			return eps | (tl.Powered?8:0);
		case 7:
			return (!tl.Powered && !tl.Active?1:0) | (!tl.Powered && !tl.Active?0:2) | (tl.Powered && !tl.Active?4:0);
		case 8:
			return !tl.Powered?1:2;
		case 9:
			return tl.Powered?1:0;
		case 10:
			return (tl.Powered?1:0) | tl.PowerState & 2;
		case 11:
			if(tl.Deadmap == 0) {
				return (tl.Powered?8:0) | ((tl.PowerState & 3) == 0?1:0) | ((tl.PowerState & 6) == 2?2:0) | ((tl.PowerState & 2) == 0?4:0);
			}

			return (tl.Powered?8:0) | ((tl.PowerState & 3) == 2?1:0) | ((tl.PowerState & 6) == 0?2:0) | ((tl.PowerState & 2) == 0?4:0);
		case 12:
			return (tl.Powered?1:0) | (tl.PowerState == 0?2:0);
		case 13:
			return tl.Powered?1:0;
		case 14:
			return 0;
		case 15:
			if(tl.Deadmap == 0) {
				switch(tl.PowerState & 6) {
				case 0:
					return tl.Powered?25:5;
				case 1:
				case 3:
				default:
					return tl.Powered?24:0;
				case 2:
					return tl.Powered?26:2;
				case 4:
					return tl.Powered?25:5;
				}
			} else {
				switch(tl.PowerState & 3) {
				case 0:
					return tl.Powered?25:5;
				case 1:
					return tl.Powered?25:5;
				case 2:
					return tl.Powered?26:2;
				default:
					return tl.Powered?24:0;
				}
			}
		default:
			return 0;
		}
	}

	@Override
	protected int getInvTorchState(int md) {
		switch(md) {
		case 256:
		case 257:
		case 258:
			return 1;
		case 259:
		case 260:
			return 7;
		case 261:
			return 12;
		case 262:
			return 4;
		case 263:
		case 264:
		case 265:
			return 1;
		case 266:
			return 2;
		case 267:
			return 12;
		case 268:
			return 1;
		case 269:
			return 0;
		case 270:
			return 0;
		case 271:
			return 5;
		default:
			return 0;
		}
	}

	@Override
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch(md) {
		case 0:
			if(tl.Deadmap == 2) {
				return torchMapLatch2;
			} else {
				if(tl.Deadmap == 3) {
					return torchMapLatch2b;
				}

				return torchMapLatch;
			}
		case 1:
			return torchMapNor;
		case 2:
			return torchMapOr;
		case 3:
			return torchMapNand;
		case 4:
			return torchMapAnd;
		case 5:
			return torchMapXnor;
		case 6:
			return torchMapXor;
		case 7:
			return torchMapPulse;
		case 8:
			return torchMapToggle;
		case 9:
			return torchMapNot;
		case 10:
			return torchMapBuffer;
		case 11:
			if(tl.Deadmap == 0) {
				return torchMapMux;
			}

			return torchMapMux2;
		case 12:
			return new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.313D, -0.25D, -0.125D, 0.7D), new RenderLogic.TorchPos(-0.25D + tl.Deadmap * 0.063D, -0.25D, 0.25D, 0.7D)};
		case 13:
			return torchMapSync;
		case 14:
			return null;
		case 15:
			if(tl.Deadmap == 0) {
				return torchMapDLatch;
			}

			return torchMapDLatch2;
		default:
			return null;
		}
	}

	@Override
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		switch(md) {
		case 256:
			return torchMapLatch;
		case 257:
			return torchMapNor;
		case 258:
			return torchMapOr;
		case 259:
			return torchMapNand;
		case 260:
			return torchMapAnd;
		case 261:
			return torchMapXnor;
		case 262:
			return torchMapXor;
		case 263:
			return torchMapPulse;
		case 264:
			return torchMapToggle;
		case 265:
			return torchMapNot;
		case 266:
			return torchMapBuffer;
		case 267:
			return torchMapMux;
		case 268:
			return torchMapRepS;
		case 269:
			return torchMapSync;
		case 270:
			return null;
		case 271:
			return torchMapDLatch;
		default:
			return null;
		}
	}

	@Override
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
		int md = tl.getExtendedMetadata();
		int tx;
		int tmp;
		switch(md) {
		case 0:
			if(tl.Deadmap < 2) {
				tx = ((tl.PowerState & 1) > 0?1:0) | ((tl.PowerState & 4) > 0?2:0);
				if(!tl.Disabled || tl.Active) {
					tx |= tl.Powered?2:1;
				}

				tx += 24 + (tl.Deadmap == 1?4:0);
			} else {
				tx = 96 + (tl.Deadmap == 3?16:0) + tl.PowerState;
			}
			break;
		case 1:
			tx = texIdxNor[tl.Deadmap] + PowerLib.cutBits(tl.PowerState | (tl.Powered?8:0), tl.Deadmap);
			break;
		case 2:
			tx = texIdxOr[tl.Deadmap] + PowerLib.cutBits(tl.PowerState, tl.Deadmap);
			break;
		case 3:
			tx = texIdxNand[tl.Deadmap] + PowerLib.cutBits(tl.PowerState | (tl.Powered?8:0), tl.Deadmap);
			break;
		case 4:
			tx = texIdxAnd[tl.Deadmap] + PowerLib.cutBits(tl.PowerState, tl.Deadmap);
			break;
		case 5:
			tx = 128 + (tl.PowerState & 1) + ((tl.PowerState & 4) >> 1);
			break;
		case 6:
			tx = 132 + ((tl.Powered?4:0) | (tl.PowerState & 12) >> 1 | tl.PowerState & 1);
			break;
		case 7:
			tx = 5;
			if(tl.Powered && !tl.Active) {
				tx = 6;
			} else if(!tl.Powered && tl.Active) {
				tx = 7;
			}
			break;
		case 8:
			tx = 140 + (tl.PowerState & 1) + (tl.PowerState >> 1 & 2);
			break;
		case 9:
			if(tl.Deadmap == 0) {
				tx = 432 + (tl.PowerState | (tl.Powered?13:0));
			} else {
				tmp = PowerLib.cutBits(tl.Deadmap, 2);
				if(tl.Powered) {
					tx = 480 + (tmp - 1 << 1) + ((tl.PowerState & 2) >> 1);
				} else {
					tx = texIdxNot[tmp] + PowerLib.cutBits(tl.PowerState, tl.Deadmap);
				}
			}
			break;
		case 10:
			if(tl.Deadmap == 0) {
				tx = 496 + (tl.PowerState | (tl.Powered?5:0));
			} else {
				tmp = PowerLib.cutBits(tl.Deadmap, 2);
				if(tl.Powered) {
					tx = 256 + (tmp << 1) + ((tl.PowerState & 2) >> 1);
				} else {
					tx = texIdxBuf[tmp] + PowerLib.cutBits(tl.PowerState, tl.Deadmap);
				}
			}
			break;
		case 11:
			tx = 144 + (tl.Deadmap > 0?8:0) + tl.PowerState;
			break;
		case 12:
			tx = 492 + (tl.PowerState >> 1) + (tl.Powered?0:2);
			break;
		case 13:
			tx = 160 + tl.PowerState + (tl.Active?8:0) + (tl.Disabled?16:0);
			break;
		case 14:
			tx = 192 + (tl.PowerState | (tl.Active?1:0) | (tl.Powered?4:0) | (tl.Disabled?8:0));
			break;
		case 15:
			if(tl.Deadmap > 0) {
				tx = 216 + tl.PowerState + (tl.Powered?4:0);
			} else {
				tx = 208 + (tl.PowerState >> 1) + (tl.Powered?4:0);
			}
			break;
		case 16:
			tx = 513 + (!tl.Powered && tl.PowerState <= 0?0:1);
			break;
		default:
			return;
		}

		this.renderWafer(tx);
		//IIcon icon1 = getIcon(1;
		//IIcon icon2 = getIcon(2;
		//IIcon icon8 = getIcon(8;
		//IIcon icon9 = getIcon(9;
		//IIcon icon20 = getIcon(20;
		if(md == 8) {
			super.context.setTexFlags(44);
			super.context.setSize(0.25D, 0.0D, 0.5550000071525574D, 0.75D, 0.30000001192092896D, 0.8050000071525574D);
			super.context.setIcon(Blocks.cobblestone.getBlockTextureFromSide(1));
			super.context.calcBounds();
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.renderFaces(62);
			Vector3 pos = new Vector3(0.0D, -0.3D, 0.18D);
			Quat q = MathLib.orientQuat(tl.Rotation >> 2, tl.Rotation & 3);
			q.rotate(pos);
			pos.add(super.context.globalOrigin);
			q.rightMultiply(leverPositions[tl.Powered?1:0]);
			RenderLib.renderSpecialLever(pos, q, Blocks.lever.getBlockTextureFromSide(1), Blocks.lever.getBlockTextureFromSide(2));
		} else if(md == 13) {
			this.renderChip(-0.125D, 0.0D, -0.1875D, tl.Disabled? 2:1);
			this.renderChip(-0.125D, 0.0D, 0.1875D, tl.Active? 2:1);
		} else if(md == 14) {
			this.renderChip(-0.25D, 0.0D, -0.25D, tl.Disabled? 9:8);
			this.renderChip(-0.25D, 0.0D, 0.25D, tl.Active? 9:8);
			this.renderChip(0.125D, 0.0D, 0.0D, tl.Powered? 9:8);
		} else if(md == 16) {
			//super.context.bindTexture("/eloraam/logic/sensor1.png");
			super.context.setTexFlags(64);
			tx = 16 + tl.Deadmap;
			IIcon tex = getIcon(0, 0);
			super.context.setIcon(tex, tex, getIcon(0, 0), getIcon(0, 0), tex, tex);
			super.context.renderBox(62, 0.125D, 0.0D, 0.18799999356269836D, 0.625D, 0.18799999356269836D, 0.8130000233650208D);
			//super.context.unbindTexture();
		}

	}

	@Override
	protected void renderInvPart(int md) {
		switch(md) {
		case 256:
			this.renderInvWafer(25);
			break;
		case 257:
			this.renderInvWafer(280);
			break;
		case 258:
			this.renderInvWafer(384);
			break;
		case 259:
			this.renderInvWafer(344);
			break;
		case 260:
			this.renderInvWafer(400);
			break;
		case 261:
			this.renderInvWafer(128);
			break;
		case 262:
			this.renderInvWafer(132);
			break;
		case 263:
			this.renderInvWafer(5);
			break;
		case 264:
			this.renderInvWafer(140);
			break;
		case 265:
			this.renderInvWafer(440);
			break;
		case 266:
			this.renderInvWafer(496);
			break;
		case 267:
			this.renderInvWafer(144);
			break;
		case 268:
			this.renderInvWafer(493);
			break;
		case 269:
			this.renderInvWafer(160);
			break;
		case 270:
			this.renderInvWafer(192);
			break;
		case 271:
			this.renderInvWafer(208);
			break;
		case 272:
			this.renderInvWafer(51);
		}

		//IIcon icon2 = 2;
		//IIcon icon8 = 8;
		//IIcon icon16 = 16;
		//IIcon icon20 = 20;
		Tessellator tessellator;
		if(md == 264) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			super.context.useNormal = true;
			super.context.setTexFlags(44);
			super.context.setSize(0.25D, 0.0D, 0.5550000071525574D, 0.75D, 0.30000001192092896D, 0.8050000071525574D);
			super.context.setIcon(Blocks.cobblestone.getBlockTextureFromSide(1));
			super.context.calcBounds();
			super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
			super.context.renderFaces(62);
			super.context.useNormal = false;
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			Vector3 pos = new Vector3(0.0D, -0.3D, 0.18D);
			Quat q = MathLib.orientQuat(0, 3);
			q.rotate(pos);
			pos.add(super.context.globalOrigin);
			q.rightMultiply(leverPositions[0]);
			RenderLib.renderSpecialLever(pos, q, Blocks.lever.getBlockTextureFromSide(1), Blocks.lever.getBlockTextureFromSide(2)); //TODO: Maybe downgrade numbs
			tessellator.draw();
		} else if(md == 269) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			super.context.useNormal = true;
			this.renderChip(-0.125D, 0.0D, -0.1875D, 2);
			this.renderChip(-0.125D, 0.0D, 0.1875D, 2);
			super.context.useNormal = false;
			tessellator.draw();
		} else if(md == 270) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			super.context.useNormal = true;
			this.renderChip(-0.25D, 0.0D, -0.25D, 8);
			this.renderChip(-0.25D, 0.0D, 0.25D, 8);
			this.renderChip(0.125D, 0.0D, 0.0D, 8);
			super.context.useNormal = false;
			tessellator.draw();
		} else if(md == 272) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			super.context.useNormal = true;
			//super.context.bindTexture("/eloraam/logic/sensor1.png");
			super.context.setIcon(getIcon(0, 0), getIcon(0, 0), getIcon(0, 0), getIcon(0, 0), getIcon(0, 0), getIcon(0, 0));
			super.context.setTexFlags(64);
			super.context.renderBox(62, 0.125D, 0.0D, 0.18799999356269836D, 0.625D, 0.18799999356269836D, 0.8130000233650208D);
			//super.context.unbindTexture();
			super.context.useNormal = false;
			tessellator.draw();
		}

	}

	static {
		leverPositions[0] = Quat.aroundAxis(1.0D, 0.0D, 0.0D, 0.8639379797371932D);
		leverPositions[1] = Quat.aroundAxis(1.0D, 0.0D, 0.0D, -0.8639379797371932D);
		leverPositions[0].multiply(MathLib.orientQuat(0, 3));
		leverPositions[1].multiply(MathLib.orientQuat(0, 3));
	}
}
