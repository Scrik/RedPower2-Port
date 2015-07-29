package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import com.eloraam.redpower.logic.RenderLogic;
import com.eloraam.redpower.logic.TileLogic;
import com.eloraam.redpower.logic.TileLogicStorage;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

public class RenderLogicStorage extends RenderLogic {
	
	private static RenderLogic.TorchPos[] torchMapCounter = new RenderLogic.TorchPos[] { new RenderLogic.TorchPos(
			0.0D, 0.125D, 0.188D, 1.0D), new RenderLogic.TorchPos(0.3D, -0.3D,
			0.0D, 0.6000000238418579D), new RenderLogic.TorchPos(-0.3D, -0.3D,
			0.0D, 0.6000000238418579D) };
	
	public RenderLogicStorage(Block bl) {
		super(bl);
	}
	
	@Override
	protected int getTorchState(TileLogic tl) {
		TileLogicStorage tls = (TileLogicStorage) tl;
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 0:
				TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) tls
						.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
				return 1 | (lsc.Count == lsc.CountMax ? 2 : 0)
						| (lsc.Count == 0 ? 4 : 0);
			default:
				return 0;
		}
	}
	
	@Override
	protected int getInvTorchState(int md) {
		switch (md) {
			case 768:
				return 5;
			default:
				return 0;
		}
	}
	
	@Override
	protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tl) {
		int md = tl.getExtendedMetadata();
		switch (md) {
			case 0:
				return torchMapCounter;
			default:
				return null;
		}
	}
	
	@Override
	protected RenderLogic.TorchPos[] getInvTorchVectors(int md) {
		switch (md) {
			case 768:
				return torchMapCounter;
			default:
				return null;
		}
	}
	
	@Override
	protected void renderWorldPart(IBlockAccess iba, TileLogic tl) {
		int md = tl.getExtendedMetadata();
		TileLogicStorage tls = (TileLogicStorage) tl;
		switch (md) {
			case 0:
				int tx = 224 + (tl.Deadmap > 0 ? 4 : 0) + (tl.PowerState & 1) + ((tl.PowerState & 4) >> 1);
				this.renderWafer(tx);
				if (md == 0) {
					TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter) tls.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
					if (lsc.CountMax == 0) {
						lsc.CountMax = 1;
					}
					float dir = 0.58F + 0.34F * ((float) lsc.Count / (float) lsc.CountMax);
					Vector3 pos = new Vector3(0.0D, -0.1D, 0.188D);
					super.context.basis.rotate(pos);
					pos.add(super.context.globalOrigin);
					pos.add(0.5D, 0.5D, 0.5D);
					Quat q = Quat.aroundAxis(0.0D, 1.0D, 0.0D, (-dir) * 3.141592653589793D * 2.0D);
					q.multiply(MathLib.orientQuat(tl.Rotation >> 2,tl.Rotation & 3));
					RenderLib.renderPointer(pos, q);
				}
				return;
			default:
		}
	}
	
	@Override
	protected void renderInvPart(int md) {
		switch (md) {
			case 768:
				this.renderInvWafer(224);
			default:
				if (md == 768) {
					Tessellator tessellator = Tessellator.instance;
					tessellator.startDrawingQuads();
					tessellator.setNormal(0.0F, 0.0F, 1.0F);
					Vector3 v = new Vector3(0.0D, -0.1D, 0.188D);
					Quat q = Quat.aroundAxis(0.0D, 1.0D, 0.0D, 3.64424747816416D);
					super.context.basis.rotate(v);
					q.multiply(MathLib.orientQuat(0, 1));
					RenderLib.renderPointer(v, q); //TODO:
					tessellator.draw();
				}
				
		}
	}
	
}
