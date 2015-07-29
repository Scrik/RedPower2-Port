package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.logic.TileLogic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

public class TileLogicSimple extends TileLogic {
	
	private static final int[] toDead = new int[] { 0, 1, 2, 4, 6, 5, 3 };
	private static final int[] fromDead = new int[] { 0, 1, 2, 6, 3, 5, 4 };
	private static final int[] toDeadNot = new int[] { 0, 1, 8, 4, 12, 5, 9 };
	private static final int[] fromDeadNot = new int[] { 0, 1, 0, 0, 3, 5, 0, 0, 2, 6, 0, 0, 4 };
	private static final int[] toDeadBuf = new int[] { 0, 1, 4, 5 };
	private static final int[] fromDeadBuf = new int[] { 0, 1, 0, 0, 2, 3 };
	private static int[] tickSchedule = new int[] { 2, 4, 6, 8, 16, 32, 64, 128, 256 };
	
	@Override
	public void initSubType(int st) {
		super.initSubType(st);
	}
	
	@Override
	public int getPartMaxRotation(int part, boolean sec) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					return 3;
				case 1:
				case 2:
				case 3:
				case 4:
				case 9:
					return 6;
				case 5:
				case 6:
				case 7:
				case 8:
				case 12:
				case 13:
				case 14:
				default:
					break;
				case 10:
					return 3;
				case 11:
				case 15:
					return 1;
				case 16:
					return 3;
			}
		}
		
		return super.getPartMaxRotation(part, sec);
	}
	
	@Override
	public int getPartRotation(int part, boolean sec) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					return super.Deadmap;
				case 1:
				case 2:
				case 3:
				case 4:
					return fromDead[super.Deadmap];
				case 5:
				case 6:
				case 7:
				case 8:
				case 12:
				case 13:
				case 14:
				default:
					break;
				case 9:
					return fromDeadNot[super.Deadmap];
				case 10:
					return fromDeadBuf[super.Deadmap];
				case 11:
				case 15:
				case 16:
					return super.Deadmap;
			}
		}
		
		return super.getPartRotation(part, sec);
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		if (sec) {
			switch (super.SubId) {
				case 0:
					super.Deadmap = rot;
					super.PowerState = 0;
					super.Active = false;
					super.Powered = false;
					this.updateBlockChange();
					return;
				case 1:
				case 2:
				case 3:
				case 4:
					super.Deadmap = toDead[rot];
					this.updateBlockChange();
					return;
				case 5:
				case 6:
				case 7:
				case 8:
				case 12:
				case 13:
				case 14:
				default:
					break;
				case 9:
					super.Deadmap = toDeadNot[rot];
					this.updateBlockChange();
					return;
				case 10:
					super.Deadmap = toDeadBuf[rot];
					this.updateBlockChange();
					return;
				case 11:
				case 15:
				case 16:
					super.Deadmap = rot;
					this.updateBlockChange();
					return;
			}
		}
		
		super.setPartRotation(part, sec, rot);
	}
	
	private void latchUpdatePowerState() {
		if (!super.Disabled || super.Active) {
			int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 5, super.Rotation, 0);
			if (ps != super.PowerState) {
				this.updateBlock();
			}
			
			super.PowerState = ps;
			if (!this.isTickRunnable()) {
				if (super.Active) {
					if (ps == 5) {
						super.Disabled = true;
					} else {
						super.Disabled = false;
					}
				} else if ((ps != 1 || !super.Powered)
						&& (ps != 4 || super.Powered)) {
					if (ps == 5) {
						super.Active = true;
						super.Disabled = true;
						super.Powered = !super.Powered;
						this.scheduleTick(2);
						this.updateBlockChange();
					}
				} else {
					super.Powered = !super.Powered;
					super.Active = true;
					this.playSound("random.click", 0.3F, 0.5F, false);
					this.scheduleTick(2);
					this.updateBlockChange();
				}
				
			}
		}
	}
	
	private void latchTick() {
		if (super.Active) {
			super.Active = false;
			if (super.Disabled) {
				this.updateBlockChange();
				this.scheduleTick(2);
			} else {
				this.updateBlockChange();
			}
		} else if (super.Disabled) {
			int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 5, super.Rotation, 0);
			if (ps != super.PowerState) {
				this.updateBlock();
			}
			
			super.PowerState = ps;
			switch (ps) {
				case 0:
					super.Disabled = false;
					super.Powered = super.worldObj.rand.nextInt(2) == 0;
					this.updateBlockChange();
					break;
				case 1:
					super.Disabled = false;
					super.Powered = false;
					this.updateBlockChange();
					this.playSound("random.click", 0.3F, 0.5F, false);
				case 2:
				case 3:
				default:
					break;
				case 4:
					super.Disabled = false;
					super.Powered = true;
					this.updateBlockChange();
					this.playSound("random.click", 0.3F, 0.5F, false);
					break;
				case 5:
					this.scheduleTick(4);
			}
			
		}
	}
	
	private int latch2NextState() {
		if ((super.PowerState & 5) == 0) {
			return super.PowerState;
		} else {
			int ps = super.PowerState & 5 | 10;
			if (super.Deadmap == 2) {
				if ((ps & 1) > 0) {
					ps &= -9;
				}
				
				if ((ps & 4) > 0) {
					ps &= -3;
				}
			} else {
				if ((ps & 1) > 0) {
					ps &= -3;
				}
				
				if ((ps & 4) > 0) {
					ps &= -9;
				}
			}
			
			return ps;
		}
	}
	
	private void latch2UpdatePowerState() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 5, super.Rotation, 0);
		boolean upd = false;
		if (ps != (super.PowerState & 5)) {
			super.PowerState = super.PowerState & 10 | ps;
			upd = true;
		}
		
		int p2 = this.latch2NextState();
		if (p2 != super.PowerState || (super.PowerState & 5) == 0) {
			this.scheduleTick(2);
			upd = true;
		}
		
		if (upd) {
			this.updateBlock();
		}
		
	}
	
	private void latchChange() {
		if (super.Deadmap < 2) {
			this.latchUpdatePowerState();
		} else {
			if (this.isTickRunnable()) {
				return;
			}
			
			this.latch2UpdatePowerState();
		}
		
	}
	
	private void latch2Tick() {
		boolean upd = false;
		if (super.PowerState == 0) {
			super.PowerState |= super.worldObj.rand.nextInt(2) == 0 ? 1 : 4;
			upd = true;
		}
		
		int ps = this.latch2NextState();
		if (ps != super.PowerState) {
			super.PowerState = ps;
			upd = true;
		}
		
		if (upd) {
			this.updateBlockChange();
		}
		
		this.latch2UpdatePowerState();
	}
	
	private void pulseChange() {
		int ps;
		if (super.Active) {
			ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 2, super.Rotation, 0);
			if (ps == 0) {
				super.Active = false;
				this.updateBlock();
			}
		} else if (!super.Powered) {
			ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 2, super.Rotation, 0);
			if (ps > 0) {
				super.Powered = true;
				this.updateBlockChange();
				this.scheduleTick(2);
			}
		}
		
	}
	
	private void pulseTick() {
		if (super.Powered) {
			super.Powered = false;
			int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 2, super.Rotation, 0);
			if (ps > 0) {
				super.Active = true;
			}
			
			this.updateBlockChange();
		}
		
	}
	
	private void toggleUpdatePowerState() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 5, super.Rotation, 0);
		if (ps != super.PowerState) {
			int t = 5 & ps & ~super.PowerState;
			if (t == 1 || t == 4) {
				super.Active = true;
			}
			
			super.PowerState = ps;
			this.updateBlock();
			if (super.Active) {
				this.scheduleTick(2);
			}
		}
		
	}
	
	private void toggleTick() {
		if (super.Active) {
			this.playSound("random.click", 0.3F, 0.5F, false);
			super.Powered = !super.Powered;
			super.Active = false;
			this.updateBlockChange();
		}
		
		this.toggleUpdatePowerState();
	}
	
	private void repUpdatePowerState() {
		if (!super.Active) {
			int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
					super.yCoord, super.zCoord, 2, super.Rotation, 0);
			if (ps != super.PowerState) {
				this.updateBlock();
			}
			
			super.PowerState = ps;
			boolean pwr = super.PowerState > 0;
			if (pwr != super.Powered) {
				super.Active = true;
				this.scheduleTick(tickSchedule[super.Deadmap]);
			}
			
		}
	}
	
	private void repTick() {
		//boolean pwr = this.simpleWantsPower();
		if (super.Active) {
			super.Powered = !super.Powered;
			super.Active = false;
			this.updateBlockChange();
			this.repUpdatePowerState();
		}
	}
	
	private void syncChange() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 7, super.Rotation, 0);
		int psc = ps & ~super.PowerState;
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		boolean upd = false;
		if ((ps & 2) == 2) {
			if (!super.Powered && (super.Active || super.Disabled)) {
				super.Active = false;
				super.Disabled = false;
				upd = true;
			}
		} else {
			if ((psc & 1) > 0 && !super.Active) {
				super.Active = true;
				upd = true;
			}
			
			if ((psc & 4) > 0 && !super.Disabled) {
				super.Disabled = true;
				upd = true;
			}
		}
		
		if (upd) {
			this.updateBlock();
			this.scheduleTick(2);
		}
		
	}
	
	private void syncTick() {
		if (super.Active && super.Disabled && !super.Powered) {
			super.Powered = true;
			super.Active = false;
			super.Disabled = false;
			this.scheduleTick(2);
			this.updateBlockChange();
		} else if (super.Powered) {
			super.Powered = false;
			this.updateBlockChange();
		}
		
	}
	
	private void randUpdatePowerState() {
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, 15, super.Rotation, 0);
		int psc = ps & ~super.PowerState;
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		if ((psc & 2) > 0) {
			this.scheduleTick(2);
		}
		
	}
	
	private void randTick() {
		if ((super.PowerState & 2) != 0) {
			int rv = super.worldObj.rand.nextInt(8);
			super.Disabled = (rv & 1) > 0;
			super.Active = (rv & 2) > 0;
			super.Powered = (rv & 4) > 0;
			this.updateBlockChange();
			this.randUpdatePowerState();
			if ((super.PowerState & 2) > 0) {
				this.scheduleTick(4);
			}
			
		}
	}
	
	private void lightTick() {
		int lb = super.worldObj.getBlockLightValue(super.xCoord, super.yCoord,
				super.zCoord);
		super.Active = lb > super.Deadmap * 4;
		if (super.Cover != 7 && super.Cover != 255) {
			super.Active = false;
		}
		
		if (super.Active != super.Powered) {
			this.scheduleTick(2);
		}
		
		this.simpleTick();
	}
	
	private boolean simpleWantsPower() {
		switch (super.SubId) {
			case 1:
				return (super.PowerState & 7 & ~super.Deadmap) == 0;
			case 2:
				return (super.PowerState & ~super.Deadmap) > 0;
			case 3:
				return (super.PowerState & 7 | super.Deadmap) < 7;
			case 4:
				return (super.PowerState | super.Deadmap) == 7;
			case 5:
				return super.PowerState == 5 || super.PowerState == 0;
			case 6:
				int t = super.PowerState & 5;
				return t == 4 || t == 1;
			/*case 7:
			case 8:
			case 12:
			case 13:
			case 14:*/
			default:
				return false;
			case 9:
				return (super.PowerState & 2) == 0;
			case 10:
				return (super.PowerState & 2) > 0;
			case 11:
				if (super.Deadmap == 0) {
					return (super.PowerState & 3) == 1
							|| (super.PowerState & 6) == 6;
				}
				
				return (super.PowerState & 3) == 3
						|| (super.PowerState & 6) == 4;
			case 15:
				if ((super.PowerState & 2) == 0) {
					return super.Powered;
				} else {
					if (super.Deadmap == 0) {
						return (super.PowerState & 4) == 4;
					}
					
					return (super.PowerState & 1) == 1;
				}
			case 16:
				return super.Active;
		}
	}
	
	private void simpleUpdatePowerState() {
		int sides = 15;
		switch (super.SubId) {
			case 2:
				sides = 7;
			/*case 3:
			case 7:
			case 8:
			case 9:
			case 13:
			case 14:*/
			default:
				break;
			case 4:
				sides = 7;
				break;
			case 5:
				sides = 5;
				break;
			case 6:
				sides = 13;
				break;
			case 10:
				sides = 7;
				break;
			case 11:
				sides = 7;
				break;
			case 12:
				sides = 2;
				break;
			case 15:
				sides = super.Deadmap == 0 ? 6 : 3;
				break;
			case 16:
				sides = 8;
		}
		
		int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord,
				super.yCoord, super.zCoord, sides, super.Rotation, 0);
		if (ps != super.PowerState) {
			this.updateBlock();
		}
		
		super.PowerState = ps;
		boolean pwr = this.simpleWantsPower();
		if (pwr != super.Powered) {
			this.scheduleTick(2);
		}
		
	}
	
	private void simpleTick() {
		boolean pwr = this.simpleWantsPower();
		if (super.Powered && !pwr) {
			super.Powered = false;
			this.updateBlockChange();
		} else if (!super.Powered && pwr) {
			super.Powered = true;
			this.updateBlockChange();
		}
		
		this.simpleUpdatePowerState();
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		if (!this.tryDropBlock()) {
			switch (super.SubId) {
				case 0:
					this.latchChange();
					break;
				/*case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 9:
				case 10:
				case 11:
				case 15:*/
				case 16:
					if (!this.isTickRunnable()) {
						this.simpleUpdatePowerState();
					}
					break;
				case 7:
					this.pulseChange();
					break;
				case 8:
					if (!this.isTickRunnable()) {
						this.toggleUpdatePowerState();
					}
					break;
				case 12:
					if (!this.isTickRunnable()) {
						this.repUpdatePowerState();
					}
					break;
				case 13:
					this.syncChange();
					break;
				case 14:
					if (!this.isTickRunnable()) {
						this.randUpdatePowerState();
					}
			}
			
		}
	}
	
	@Override
	public void onTileTick() {
		switch (super.SubId) {
			case 0:
				if (super.Deadmap < 2) {
					this.latchTick();
				} else {
					this.latch2Tick();
				}
				break;
			/*case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 9:
			case 10:
			case 11:*/
			case 15:
				this.simpleTick();
				break;
			case 7:
				this.pulseTick();
				break;
			case 8:
				this.toggleTick();
				break;
			case 12:
				this.repTick();
				break;
			case 13:
				this.syncTick();
				break;
			case 14:
				this.randTick();
				break;
			case 16:
				this.lightTick();
		}
		
	}
	
	@Override
	public int getPoweringMask(int ch) {
		if (ch != 0) {
			return 0;
		} else {
			switch (super.SubId) {
				case 0:
					int ps;
					if (super.Deadmap > 1) {
						ps = super.PowerState & 10;
					} else if (super.Disabled && !super.Active) {
						ps = 0;
					} else if (super.Active) {
						ps = super.Powered ? 4 : 1;
					} else if (super.Deadmap == 1) {
						ps = super.Powered ? 6 : 9;
					} else {
						ps = super.Powered ? 12 : 3;
					}
					
					return RedPowerLib.mapRotToCon(ps, super.Rotation);
				/*case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 11:
				case 12:
				case 13:*/
				default:
					return super.getPoweringMask(ch);
				case 8:
					if (super.Powered) {
						return RedPowerLib.mapRotToCon(2, super.Rotation);
					}
					
					return RedPowerLib.mapRotToCon(8, super.Rotation);
				case 9:
				case 10:
					if (super.Powered) {
						return RedPowerLib.mapRotToCon(13 & ~super.Deadmap,
								super.Rotation);
					}
					
					return 0;
				case 14:
					return RedPowerLib.mapRotToCon((super.Active ? 1 : 0)
							| (super.Disabled ? 4 : 0)
							| (super.Powered ? 8 : 0), super.Rotation);
				case 15:
					return RedPowerLib
							.mapRotToCon(
									super.Deadmap == 0 ? (super.Powered ? 9 : 0) : (super.Powered ? 12 : 0),
									super.Rotation);
			}
		}
	}
	
	@Override
	public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
		switch (super.SubId) {
			case 8:
				if (part != super.Rotation >> 2) {
					return false;
				}
				
				this.playSound("random.click", 0.3F, 0.5F, false);
				if (super.Powered) {
					super.Powered = false;
				} else {
					super.Powered = true;
				}
				
				this.updateBlockChange();
				return true;
			case 12:
				if (part != super.Rotation >> 2) {
					return false;
				}
				
				++super.Deadmap;
				if (super.Deadmap > 8) {
					super.Deadmap = 0;
				}
				
				this.updateBlockChange();
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public int getConnectableMask() {
		switch (super.SubId) {
			//case 1:
			//case 2:
			//case 3:
			case 4:
				return RedPowerLib.mapRotToCon(8 | 7 & ~super.Deadmap,
						super.Rotation);
			//case 5:
			case 6:
				return RedPowerLib.mapRotToCon(13, super.Rotation);
			case 7:
				return RedPowerLib.mapRotToCon(10, super.Rotation);
			//case 8:
			//case 11:
			default:
				return super.getConnectableMask();
			case 9:
				return RedPowerLib.mapRotToCon(2 | 13 & ~super.Deadmap,
						super.Rotation);
			case 10:
				return RedPowerLib.mapRotToCon(10 | 5 & ~super.Deadmap,
						super.Rotation);
			case 12:
				return RedPowerLib.mapRotToCon(10, super.Rotation);
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (super.SubId == 16 && !this.isTickScheduled()) {
			this.scheduleTick(8);
		}
		
	}
	
	@Override
	public int getLightValue() {
		return super.SubId == 16 ? 0 : super.getLightValue();
	}
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
}
