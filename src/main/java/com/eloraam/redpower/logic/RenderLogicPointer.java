package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreProxyClient;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderLogicPointer extends RenderLogic {

   private static RenderLogic.TorchPos[] torchMapSequencer = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.0D, 0.125D, 0.0D, 1.0D), new RenderLogic.TorchPos(0.0D, -0.3D, 0.3D, 0.6D), new RenderLogic.TorchPos(-0.3D, -0.3D, 0.0D, 0.6D), new RenderLogic.TorchPos(0.0D, -0.3D, -0.3D, 0.6D), new RenderLogic.TorchPos(0.3D, -0.3D, 0.0D, 0.6D)};
   private static RenderLogic.TorchPos[] torchMapTimer = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.0D, 0.125D, 0.0D, 1.0D), new RenderLogic.TorchPos(0.3D, -0.3D, 0.0D, 0.6D)};
   private static RenderLogic.TorchPos[] torchMapStateCell = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.0D, 0.125D, 0.25D, 1.0D), new RenderLogic.TorchPos(0.281D, -0.3D, 0.156D, 0.6D)};
   private static RenderLogic.TorchPos[] torchMapStateCell2 = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.0D, 0.125D, -0.25D, 1.0D), new RenderLogic.TorchPos(0.281D, -0.3D, -0.156D, 0.6D)};

	public RenderLogicPointer(Block bl) {
		super(bl);
	}
	
	@Override
	protected int getTorchState(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 0:
				return (tl.Disabled ? 0 : 1)
						| (tl.Powered && !tl.Disabled ? 2 : 0);
			case 1:
				return 1 | 2 << tl.PowerState & 31;
			case 2:
				return (tl.Active && !tl.Powered && !tl.Disabled ? 1 : 0)
						| (tl.Active && tl.Powered ? 2 : 0);
			default:
				return 0;
		}
	}
	
	@Override
	protected int getInvTorchState(int md) {
		switch (md) {
			case 0:
				return 1;
			case 1:
				return 5;
			case 2:
				return 0;
			default:
				return 0;
		}
	}
	
	@Override
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 0:
				return torchMapTimer;
			case 1:
				return torchMapSequencer;
			case 2:
				if (tl.Deadmap > 0) {
					return torchMapStateCell2;
				}
				
				return torchMapStateCell;
			default:
				return null;
		}
	}
	
	@Override
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		switch (md) {
			case 0:
				return torchMapTimer;
			case 1:
				return torchMapSequencer;
			case 2:
				return torchMapStateCell;
			default:
				return null;
		}
	}
	
	@Override
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
		int md = tl.getExtendedMetadata();
		int tx;
		switch (md) {
			case 0:
				tx = 16 + (tl.PowerState | (tl.Powered ? 5 : 0));
				break;
			case 1:
				if (tl.Deadmap == 1) {
					tx = 4;
				} else {
					tx = 3;
				}
				break;
			case 2:
				tx = 32 + ((tl.Deadmap > 0 ? 32 : 0) | tl.PowerState
						| (tl.Active && tl.Powered ? 8 : 0)
						| (tl.Active && !tl.Powered && !tl.Disabled ? 0 : 16) | (tl.Active
						&& !tl.Powered ? (tl.Deadmap > 0 ? 1 : 4) : 0));
				break;
			default:
				return;
		}
		
		this.renderWafer(tx);
		//IIcon icon1 = getIcon(1, md);
		//IIcon icon2 = getIcon(2, md);
		if (md == 2) {
			if (tl.Deadmap > 0) {
				this.renderChip(-0.125D, 0.0D, 0.125D, tl.Active ? 2 : 1);
			} else {
				this.renderChip(-0.125D, 0.0D, -0.125D, tl.Active ? 2 : 1);
			}
		}
	}
	
	@Override
	protected void renderInvPart(int md) {
		//IIcon icon1 = CoreProxyClient.logicIcons1[3];
		//IIcon icon2 = CoreProxyClient.logicIcons1[16];
		//IIcon icon3 = CoreProxyClient.logicIcons1[48];
		switch (md) {
			case 0:
				super.context.setOrientation(0, 1);
				this.renderInvWafer(16);
				break;
			case 1:
				this.renderInvWafer(3);
				break;
			case 2:
				super.context.setOrientation(0, 1);
				this.renderInvWafer(48);
		}
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		if (md == 2) {
			RenderLib.renderPointer(new Vector3(-0.25D, -0.1D, 0.0D), Quat.aroundAxis(0.0D, 1.0D, 0.0D, 0.0D)); //TODO:
			super.context.useNormal = true;
			this.renderChip(-0.125D, 0.0D, -0.125D, 1);
			super.context.useNormal = false;
		} else {
			RenderLib.renderPointer(new Vector3(0.0D, -0.1D, 0.0D), Quat.aroundAxis(0.0D, 1.0D, 0.0D, -1.5707963267948966D));
		}
		tessellator.draw();
	}
}
