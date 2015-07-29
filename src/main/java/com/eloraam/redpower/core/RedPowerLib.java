package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.ICoverable;
import com.eloraam.redpower.core.IMultipart;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IRedPowerWiring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RedPowerLib {

	private static HashSet<List<Integer>> powerClassMapping = new HashSet<List<Integer>>();
	private static HashSet<List<Integer>> blockUpdates = new HashSet<List<Integer>>();
	private static LinkedList<List<Integer>> powerSearch = new LinkedList<List<Integer>>();
	private static HashSet<List<Integer>> powerSearchTest = new HashSet<List<Integer>>();
	private static boolean searching = false;


	public static void notifyBlock(World world, int i, int j, int k, Block block) {
		if(block != null) {
			world.getBlock(i, j, k).onNeighborBlockChange(world, i, j, k, block);
		}
	}

	public static void updateIndirectNeighbors(World w, int i, int j, int k, Block block) {
		if(/*!w.editingBlocks && */!CoreLib.isClient(w)) { //TODO: !w.editingBlocks
			for(int a = -3; a <= 3; ++a) {
				for(int b = -3; b <= 3; ++b) {
					for(int c = -3; c <= 3; ++c) {
						int md = a < 0?-a:a;
						md += b < 0?-b:b;
						md += c < 0?-c:c;
						if(md <= 3) {
							notifyBlock(w, i + a, j + b, k + c, block);
						}
					}
				}
			}
		}
	}

	public static boolean isBlockRedstone(IBlockAccess iba, int i, int j, int k, int side) {
		switch(side) {
			case 0:
				--j;
				break;
			case 1:
				++j;
				break;
			case 2:
				--k;
				break;
			case 3:
				++k;
				break;
			case 4:
				--i;
				break;
			case 5:
				++i;
				break;
		}
		return iba.getBlock(i, j, k)/*.canProvidePower()*/ instanceof BlockRedstoneWire; //TODO: IRedstoneProvider or smth like that
	}

	public static boolean isSideNormal(IBlockAccess iba, int i, int j, int k, int side) {
		switch(side) {
			case 0:
				--j;
				break;
			case 1:
				++j;
				break;
			case 2:
				--k;
				break;
			case 3:
				++k;
				break;
			case 4:
				--i;
				break;
			case 5:
				++i;
		}

		side ^= 1;
		if(iba.getBlock(i, j, k).isNormalCube()) { //IsBlockNormal
			return true;
		} else {
			iba.getBlock(i, j, k);
			IMultipart im = (IMultipart)CoreLib.getTileEntity(iba, i, j, k, IMultipart.class);
			return im == null? false : im.isSideNormal(side);
		}
	}

	public static boolean canSupportWire(IBlockAccess iba, int i, int j, int k, int side) {
		switch(side) {
			case 0:
				--j;
				break;
			case 1:
				++j;
				break;
			case 2:
				--k;
				break;
			case 3:
				++k;
				break;
			case 4:
				--i;
				break;
			case 5:
				++i;
		}

		side ^= 1;
		if(iba instanceof World) {
			World bid = (World)iba;
			if(!bid.blockExists(i, j, k)) {
				return true;
			}

			if(bid.getBlock(i, j, k).isSideSolid(bid, i, j, k, ForgeDirection.getOrientation(side))) {
				return true;
			}
		}

		if(iba.getBlock(i, j, k).isBlockNormalCube()) { //isNormalCube
			return true;
		} else {
			Block block = iba.getBlock(i, j, k);
			if(block == Blocks.piston_extension) { //pistonMoving
				return true;
			} else if(block != Blocks.sticky_piston && block != Blocks.piston) {
				IMultipart mpart = (IMultipart)CoreLib.getTileEntity(iba, i, j, k, IMultipart.class);
				return mpart == null? false : mpart.isSideNormal(side);
			} else {
				int im = iba.getBlockMetadata(i, j, k) & 7;
				return i != im && im != 7;
			}
		}
	}

	public static boolean isStrongPoweringTo(IBlockAccess iba, int i, int j, int k, int l) {
		Block block = iba.getBlock(i, j, k);
		if(iba.isAirBlock(i, j, k)) {
			return false;
		} else if(searching && block == Blocks.redstone_wire) {
			return false;
		} else if(!(iba instanceof World)) {
			return false;
		} else {
			World world = (World)iba;
			return block.isProvidingStrongPower(world, i, j, k, l) >= 15; //TODO: Need to understood how this works
		}
	}

	public static boolean isStrongPowered(IBlockAccess iba, int i, int j, int k, int side) {
		return side != 1 && isStrongPoweringTo(iba, i, j - 1, k, 0) ? true :
			(side != 0 && isStrongPoweringTo(iba, i, j + 1, k, 1) ? true :
				(side != 3 && isStrongPoweringTo(iba, i, j, k - 1, 2) ? true :
					(side != 2 && isStrongPoweringTo(iba, i, j, k + 1, 3) ? true :
						(side != 5 && isStrongPoweringTo(iba, i - 1, j, k, 4) ? true:
							side != 4 && isStrongPoweringTo(iba, i + 1, j, k, 5)))));
	}

	public static boolean isWeakPoweringTo(IBlockAccess iba, int i, int j, int k, int side) {
		Block block = iba.getBlock(i, j, k);
		return iba.isAirBlock(i, j, k) ? false : 
			(searching && block == Blocks.redstone_wire ? false :
				(block.isProvidingWeakPower(iba, i, j, k, side) >= 1 ? true : 
					side > 1 && block instanceof BlockRedstoneWire && block.isProvidingWeakPower(iba, i, j, k, 1) >= 1));
	}

	public static boolean isPoweringTo(IBlockAccess iba, int i, int j, int k, int l) {
		Block block = iba.getBlock(i, j, k);
		if(block == Blocks.air) {
			return false;
		} else if(block.isProvidingWeakPower(iba, i, j, k, l) >= 1) {
			return true;
		} else if(iba.getBlock(i, j, k).isNormalCube() && isStrongPowered(iba, i, j, k, l)) { //TODO: Hmmm, looks strange
			return true;
		} else {
			if(l > 1 && block instanceof BlockRedstoneWire) {
				if(searching) {
					return false;
				}
				if(block.isProvidingWeakPower(iba, i, j, k, 1) >= 1) {
					return true;
				}
			}
			return false;
		}
	}

	public static boolean isPowered(IBlockAccess iba, int i, int j, int k, int cons, int indside) {
		return (cons & 17895680) > 0 && isWeakPoweringTo(iba, i, j - 1, k, 0) ? true : 
			((cons & 35791360) > 0 && isWeakPoweringTo(iba, i, j + 1, k, 1) ? true : 
				((cons & 71565329) > 0 && isWeakPoweringTo(iba, i, j, k - 1, 2) ? true :
					((cons & 143130658) > 0 && isWeakPoweringTo(iba, i, j, k + 1, 3) ? true : 
						((cons & 268452932) > 0 && isWeakPoweringTo(iba, i - 1, j, k, 4) ? true :
							((cons & 536905864) > 0 && isWeakPoweringTo(iba, i + 1, j, k, 5) ? true :
								((indside & 1) > 0 && isPoweringTo(iba, i, j - 1, k, 0) ? true :
									((indside & 2) > 0 && isPoweringTo(iba, i, j + 1, k, 1) ? true :
										((indside & 4) > 0 && isPoweringTo(iba, i, j, k - 1, 2) ? true :
											((indside & 8) > 0 && isPoweringTo(iba, i, j, k + 1, 3) ? true : 
												((indside & 16) > 0 && isPoweringTo(iba, i - 1, j, k, 4) ? true : 
													(indside & 32) > 0 && isPoweringTo(iba, i + 1, j, k, 5)))))))))));
	}

	private static int getSidePowerMask(IBlockAccess iba, int i, int j, int k, int ch, int side) {
		IRedPowerConnectable irp = (IRedPowerConnectable)CoreLib.getTileEntity(iba, i, j, k, IRedPowerConnectable.class);
		int mask = getConDirMask(side);
		if(irp != null) {
			int m = irp.getPoweringMask(ch);
			m = (m & 1431655765) << 1 | (m & 715827882) >> 1;
			return m & mask;
		} else {
			return ch != 0?0:(isWeakPoweringTo(iba, i, j, k, side) ? mask & 16777215 :
				(isPoweringTo(iba, i, j, k, side) ? mask : 0));
		}
	}

	public static int getPowerState(IBlockAccess iba, int i, int j, int k, int cons, int ch) {
		int trs = 0;
		if((cons & 17895680) > 0) {
			trs |= getSidePowerMask(iba, i, j - 1, k, ch, 0);
		}
		if((cons & 35791360) > 0) {
			trs |= getSidePowerMask(iba, i, j + 1, k, ch, 1);
		}
		if((cons & 71565329) > 0) {
			trs |= getSidePowerMask(iba, i, j, k - 1, ch, 2);
		}
		if((cons & 143130658) > 0) {
			trs |= getSidePowerMask(iba, i, j, k + 1, ch, 3);
		}
		if((cons & 268452932) > 0) {
			trs |= getSidePowerMask(iba, i - 1, j, k, ch, 4);
		}
		if((cons & 536905864) > 0) {
			trs |= getSidePowerMask(iba, i + 1, j, k, ch, 5);
		}
		return trs & cons;
	}

	public static int getRotPowerState(IBlockAccess iba, int i, int j, int k, int rcon, int rot, int ch) {
		int c1 = mapRotToCon(rcon, rot);
		int ps = getPowerState(iba, i, j, k, c1, ch);
		return mapConToRot(ps, rot);
	}

	public static int getConDirMask(int dir) {
		switch(dir) {
			case 0:
				return 17895680;
			case 1:
				return 35791360;
			case 2:
				return 71565329;
			case 3:
				return 143130658;
			case 4:
				return 268452932;
			default:
				return 536905864;
		}
	}

	public static int mapConToLocal(int cons, int face) {
		cons >>= face * 4;
		cons &= 15;
		switch(face) {
		case 0:
			return cons;
		case 1:
			cons ^= ((cons ^ cons >> 1) & 1) * 3;
			return cons;
		//case 2:
		default:
			cons ^= ((cons ^ cons >> 2) & 3) * 5;
			return cons;
		//case 3:
		case 4:
			cons ^= ((cons ^ cons >> 2) & 3) * 5;
			cons ^= ((cons ^ cons >> 1) & 1) * 3;
			return cons;
		}
	}

	public static int mapLocalToCon(int loc, int face) {
		switch(face) {
		case 0:
			break;
		case 1:
			loc ^= ((loc ^ loc >> 1) & 1) * 3;
			break;
		//case 2:
		default:
			loc ^= ((loc ^ loc >> 2) & 3) * 5;
			break;
		//case 3:
		case 4:
			loc ^= ((loc ^ loc >> 1) & 1) * 3;
			loc ^= ((loc ^ loc >> 2) & 3) * 5;
		}
		return loc << face * 4;
	}

	public static int mapRotToLocal(int rm, int rot) {
		rm = rm << rot | rm >> 4 - rot;
		rm &= 15;
		return rm & 8 | (rm & 3) << 1 | rm >> 2 & 1;
	}

	public static int mapLocalToRot(int rm, int rot) {
		rm = rm & 8 | (rm & 6) >> 1 | rm << 2 & 4;
		rm = rm << 4 - rot | rm >> rot;
		return rm & 15;
	}

	public static int mapConToRot(int con, int rot) {
		return mapLocalToRot(mapConToLocal(con, rot >> 2), rot & 3);
	}

	public static int mapRotToCon(int con, int rot) {
		return mapLocalToCon(mapRotToLocal(con, rot & 3), rot >> 2);
	}

	public static int getDirToRedstone(int rsd) {
		switch(rsd) {
			case 2:
				return 0;
			case 3:
				return 2;
			case 4:
				return 3;
			case 5:
				return 1;
			default:
				return 0;
		}
	}

	public static int getConSides(IBlockAccess iba, int i, int j, int k, int side, int pcl) {
		Block block = iba.getBlock(i, j, k);
		if(iba.isAirBlock(i, j, k)) {
			return 0;
		} else {
			IConnectable rpa = (IConnectable)CoreLib.getTileEntity(iba, i, j, k, IConnectable.class);
			int md;
			if(rpa != null) {
				md = rpa.getConnectClass(side);
				return isCompatible(md, pcl)?rpa.getConnectableMask():0;
			} else if(!isCompatible(0, pcl)) {
				return 0;
			} else if(block != Blocks.piston && block != Blocks.sticky_piston) {
				if(block == Blocks.piston_extension) {
					TileEntity md2 = iba.getTileEntity(i, j, k);
					if(!(md2 instanceof TileEntityPiston)) {
						return 0;
					} else {
						TileEntityPiston tep = (TileEntityPiston)md2;
						Block sid = tep.getStoredBlockID();
						if(sid != Blocks.piston && sid != Blocks.sticky_piston) {
							return 0;
						} else {
							int md1 = tep.getBlockMetadata() & 7;
							return md1 == 7 ? 0 : 1073741823 ^ getConDirMask(md1);
						}
					}
				} else if(block != Blocks.dispenser && !(block instanceof BlockButton) && block != Blocks.lever) {
					if(block != Blocks.redstone_torch && block != Blocks.unlit_redstone_torch) {
						if(block != Blocks.unpowered_repeater && block != Blocks.powered_repeater) {
							return block.canConnectRedstone(iba, i, j, k, getDirToRedstone(side)) ? getConDirMask(side) : 0;
						} else {
							md = iba.getBlockMetadata(i, j, k) & 1;
							return md > 0?12:3;
						}
					} else {
						return 1073741823;
					}
				} else {
					return 1073741823;
				}
			} else {
				md = iba.getBlockMetadata(i, j, k) & 7;
				return md == 7?0:1073741823 ^ getConDirMask(md);
			}
		}
	}

	private static int getES1(IBlockAccess iba, int i, int j, int k, int side, int pcl, int cc) {
		if(iba.isAirBlock(i, j, k)) {
			return 0;
		} else {
			IConnectable rpa = (IConnectable)CoreLib.getTileEntity(iba, i, j, k, IConnectable.class);
			if(rpa != null) {
				int cc2 = rpa.getCornerPowerMode();
				if(cc != 0 && cc2 != 0) {
					if(cc == 2 && cc2 == 2) {
						return 0;
					} else if(cc == 3 && cc2 == 1) {
						return 0;
					} else {
						int pc = rpa.getConnectClass(side);
						return isCompatible(pc, pcl) ? rpa.getConnectableMask():0;
					}
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
	}

	public static int getExtConSides(IBlockAccess iba, IConnectable irp, int i, int j, int k, int dir, int cc) {
		int cons = irp.getConnectableMask();
		cons &= getConDirMask(dir) & 16777215;
		if(cons == 0) {
			return 0;
		} else {
			Block block = iba.getBlock(i, j, k);
			int isv;
			if(CoverLib.blockCoverPlate != null && block == CoverLib.blockCoverPlate) {
				if(iba.getBlockMetadata(i, j, k) != 0) {
					return 0;
				}

				ICoverable pcl = (ICoverable)CoreLib.getTileEntity(iba, i, j, k, ICoverable.class);
				if(pcl == null) {
					return 0;
				}

				isv = pcl.getCoverMask();
				if((isv & 1 << (dir ^ 1)) > 0) {
					return 0;
				}

				isv |= isv << 12;
				isv |= isv << 6;
				isv &= 197379;
				isv |= isv << 3;
				isv &= 1118481;
				isv |= isv << 2;
				isv |= isv << 1;
				cons &= ~isv;
			} else if(iba.isAirBlock(i, j, k) && block != Blocks.flowing_water && block != Blocks.water) {
				return 0;
			}

			int pcl1 = irp.getConnectClass(dir);
			isv = 0;
			if((cons & 15) > 0) {
				isv |= getES1(iba, i, j - 1, k, 1, pcl1, cc) & 2236928;
			}

			if((cons & 240) > 0) {
				isv |= getES1(iba, i, j + 1, k, 0, pcl1, cc) & 1118464;
			}

			if((cons & 3840) > 0) {
				isv |= getES1(iba, i, j, k - 1, 3, pcl1, cc) & 8912930;
			}

			if((cons & '\uf000') > 0) {
				isv |= getES1(iba, i, j, k + 1, 2, pcl1, cc) & 4456465;
			}

			if((cons & 983040) > 0) {
				isv |= getES1(iba, i - 1, j, k, 5, pcl1, cc) & '\u8888';
			}

			if((cons & 15728640) > 0) {
				isv |= getES1(iba, i + 1, j, k, 4, pcl1, cc) & 17476;
			}

			isv >>= (dir ^ 1) << 2;
			isv = (isv & 10) >> 1 | (isv & 5) << 1;
			isv |= isv << 6;
			isv |= isv << 3;
			isv &= 4369;
			isv <<= dir & 1;
			switch(dir) {
			case 0:
			case 1:
				return isv << 8;
			case 2:
			case 3:
				return isv << 10 & 16711680 | isv & 255;
			default:
				return isv << 2;
			}
		}
	}

	public static int getConnections(IBlockAccess iba, IConnectable irp, int i, int j, int k) {
		int cons = irp.getConnectableMask();
		int cs = 0;
		int pcl;
		if((cons & 17895680) > 0) {
			pcl = irp.getConnectClass(0);
			cs |= getConSides(iba, i, j - 1, k, 1, pcl) & 35791360;
		}

		if((cons & 35791360) > 0) {
			pcl = irp.getConnectClass(1);
			cs |= getConSides(iba, i, j + 1, k, 0, pcl) & 17895680;
		}

		if((cons & 71565329) > 0) {
			pcl = irp.getConnectClass(2);
			cs |= getConSides(iba, i, j, k - 1, 3, pcl) & 143130658;
		}

		if((cons & 143130658) > 0) {
			pcl = irp.getConnectClass(3);
			cs |= getConSides(iba, i, j, k + 1, 2, pcl) & 71565329;
		}

		if((cons & 268452932) > 0) {
			pcl = irp.getConnectClass(4);
			cs |= getConSides(iba, i - 1, j, k, 5, pcl) & 536905864;
		}

		if((cons & 536905864) > 0) {
			pcl = irp.getConnectClass(5);
			cs |= getConSides(iba, i + 1, j, k, 4, pcl) & 268452932;
		}

		cs = cs << 1 & 715827882 | cs >> 1 & 357913941;
		cs &= cons;
		return cs;
	}

	public static int getExtConnections(IBlockAccess iba, IConnectable irp, int i, int j, int k) {
		byte cs = 0;
		int cc = irp.getCornerPowerMode();
		int cs1 = cs | getExtConSides(iba, irp, i, j - 1, k, 0, cc);
		cs1 |= getExtConSides(iba, irp, i, j + 1, k, 1, cc);
		cs1 |= getExtConSides(iba, irp, i, j, k - 1, 2, cc);
		cs1 |= getExtConSides(iba, irp, i, j, k + 1, 3, cc);
		cs1 |= getExtConSides(iba, irp, i - 1, j, k, 4, cc);
		cs1 |= getExtConSides(iba, irp, i + 1, j, k, 5, cc);
		return cs1;
	}

	public static int getExtConnectionExtras(IBlockAccess iba, IConnectable irp, int i, int j, int k) {
		byte cs = 0;
		int cs1 = cs | getExtConSides(iba, irp, i, j - 1, k, 0, 3);
		cs1 |= getExtConSides(iba, irp, i, j + 1, k, 1, 3);
		cs1 |= getExtConSides(iba, irp, i, j, k - 1, 2, 3);
		cs1 |= getExtConSides(iba, irp, i, j, k + 1, 3, 3);
		cs1 |= getExtConSides(iba, irp, i - 1, j, k, 4, 3);
		cs1 |= getExtConSides(iba, irp, i + 1, j, k, 5, 3);
		return cs1;
	}

	public static int getTileCurrentStrength(World world, int i, int j, int k, int cons, int ch) {
		IRedPowerConnectable irp = (IRedPowerConnectable)CoreLib.getTileEntity(world, i, j, k, IRedPowerConnectable.class);
		if(irp == null) {
			return -1;
		} else if(irp instanceof IRedPowerWiring) {
			IRedPowerWiring irw = (IRedPowerWiring)irp;
			return irw.getCurrentStrength(cons, ch);
		} else {
			return (irp.getPoweringMask(ch) & cons) > 0?255:-1;
		}
	}

	public static int getTileOrRedstoneCurrentStrength(World world, int i, int j, int k, int cons, int ch) {
		Block block = world.getBlock(i, j, k);
		if(world.isAirBlock(i, j, k)) {
			return -1;
		} else if(block == Blocks.redstone_wire) {
			int irp1 = world.getBlockMetadata(i, j, k);
			return irp1 > 0?irp1:-1;
		} else {
			IRedPowerConnectable irp = (IRedPowerConnectable)CoreLib.getTileEntity(world, i, j, k, IRedPowerConnectable.class);
			if(irp == null) {
				return -1;
			} else if(irp instanceof IRedPowerWiring) {
				IRedPowerWiring irw = (IRedPowerWiring)irp;
				return irw.getCurrentStrength(cons, ch);
			} else {
				return (irp.getPoweringMask(ch) & cons) > 0?255:-1;
			}
		}
	}

	private static int getIndCur(World world, int i, int j, int k, int d1, int d2, int ch) {
		int d3;
		switch(d1) {
		case 0:
			--j;
			d3 = d2 + 2;
			break;
		case 1:
			++j;
			d3 = d2 + 2;
			break;
		case 2:
			--k;
			d3 = d2 + (d2 & 2);
			break;
		case 3:
			++k;
			d3 = d2 + (d2 & 2);
			break;
		case 4:
			--i;
			d3 = d2;
			break;
		default:
			++i;
			d3 = d2;
		}

		int d4;
		switch(d3) {
		case 0:
			--j;
			d4 = d1 - 2;
			break;
		case 1:
			++j;
			d4 = d1 - 2;
			break;
		case 2:
			--k;
			d4 = d1 & 1 | (d1 & 4) >> 1;
			break;
		case 3:
			++k;
			d4 = d1 & 1 | (d1 & 4) >> 1;
			break;
		case 4:
			--i;
			d4 = d1;
			break;
		default:
			++i;
			d4 = d1;
		}

		return getTileCurrentStrength(world, i, j, k, 1 << (d4 ^ 1) << ((d3 ^ 1) << 2), ch);
	}

	public static int getMaxCurrentStrength(World world, int i, int j, int k, int cons, int indcon, int ch) {
		int mcs = -1;
		int ocon = cons << 1 & 715827882 | cons >> 1 & 357913941;
		if((cons & 17895680) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j - 1, k, ocon & 35791360, ch));
		}

		if((cons & 35791360) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j + 1, k, ocon & 17895680, ch));
		}

		if((cons & 71565329) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j, k - 1, ocon & 143130658, ch));
		}

		if((cons & 143130658) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j, k + 1, ocon & 71565329, ch));
		}

		if((cons & 268452932) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i - 1, j, k, ocon & 536905864, ch));
		}

		if((cons & 536905864) > 0) {
			mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i + 1, j, k, ocon & 268452932, ch));
		}

		for(int a = 0; a < 6; ++a) {
			for(int b = 0; b < 4; ++b) {
				if((indcon & 1 << a * 4 + b) > 0) {
					mcs = Math.max(mcs, getIndCur(world, i, j, k, a, b, ch));
				}
			}
		}

		return mcs;
	}

	public static void addUpdateBlock(int i, int j, int k) {
		for(int a = -3; a <= 3; ++a) {
			for(int b = -3; b <= 3; ++b) {
				for(int c = -3; c <= 3; ++c) {
					int md = a < 0?-a:a;
					md += b < 0?-b:b;
					md += c < 0?-c:c;
					if(md <= 3) {
						blockUpdates.add(Arrays.asList(new Integer[]{Integer.valueOf(i + a), Integer.valueOf(j + b), Integer.valueOf(k + c)}));
					}
				}
			}
		}

	}

	public static void addStartSearchBlock(int i, int j, int k) {
		List<Integer> sb = Arrays.asList(new Integer[]{Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
		if(!powerSearchTest.contains(sb)) {
			powerSearch.addLast(sb);
			powerSearchTest.add(sb);
		}
	}

	public static void addSearchBlock(int i, int j, int k) {
		addStartSearchBlock(i, j, k);
		blockUpdates.add(Arrays.asList(new Integer[]{Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)}));
	}

	private static void addIndBl(int i, int j, int k, int d1, int d2) {
		int d3;
		switch(d1) {
		case 0:
			--j;
			d3 = d2 + 2;
			break;
		case 1:
			++j;
			d3 = d2 + 2;
			break;
		case 2:
			--k;
			d3 = d2 + (d2 & 2);
			break;
		case 3:
			++k;
			d3 = d2 + (d2 & 2);
			break;
		case 4:
			--i;
			d3 = d2;
			break;
		default:
			++i;
			d3 = d2;
		}

		switch(d3) {
		case 0:
			--j;
			break;
		case 1:
			++j;
			break;
		case 2:
			--k;
			break;
		case 3:
			++k;
			break;
		case 4:
			--i;
			break;
		case 5:
			++i;
		}

		addSearchBlock(i, j, k);
	}

	public static void addSearchBlocks(int i, int j, int k, int cons, int indcon) {
		int ocon = cons << 1 & 11184810 | cons >> 1 & 5592405;
		if((cons & 17895680) > 0) {
			addSearchBlock(i, j - 1, k);
		}

		if((cons & 35791360) > 0) {
			addSearchBlock(i, j + 1, k);
		}

		if((cons & 71565329) > 0) {
			addSearchBlock(i, j, k - 1);
		}

		if((cons & 143130658) > 0) {
			addSearchBlock(i, j, k + 1);
		}

		if((cons & 268452932) > 0) {
			addSearchBlock(i - 1, j, k);
		}

		if((cons & 536905864) > 0) {
			addSearchBlock(i + 1, j, k);
		}

		for(int a = 0; a < 6; ++a) {
			for(int b = 0; b < 4; ++b) {
				if((indcon & 1 << a * 4 + b) > 0) {
					addIndBl(i, j, k, a, b);
				}
			}
		}

	}

	public static void updateCurrent(World world, int i, int j, int k) {
		addStartSearchBlock(i, j, k);
		if(!searching) {
			searching = true;

			while(powerSearch.size() > 0) {
				List<?> up2 = (List<?>)powerSearch.removeFirst();
				powerSearchTest.remove(up2);
				Integer[] l = ((Integer[])up2.toArray());
				IRedPowerWiring sp = (IRedPowerWiring)CoreLib.getTileEntity(world, l[0].intValue(), l[1].intValue(), l[2].intValue(), IRedPowerWiring.class);
				if(sp != null) {
					sp.updateCurrentStrength();
				}
			}

			searching = false;
			ArrayList<List<Integer>> var7 = new ArrayList<List<Integer>>(blockUpdates);
			blockUpdates.clear();

			for(int var8 = 0; var8 < var7.size(); ++var8) {
				Integer[] var9 = ((Integer[])((List<?>)var7.get(var8)).toArray());
				notifyBlock(world, var9[0].intValue(), var9[1].intValue(), var9[2].intValue(), Blocks.redstone_wire);
				world.markBlockForUpdate(var9[0].intValue(), var9[1].intValue(), var9[2].intValue());
			}
		}
	}

	public static int updateBlockCurrentStrength(World world, IRedPowerWiring irp, int i, int j, int k, int conm, int chm) {
		int cons = irp.getConnectionMask() & conm;
		int indcon = irp.getExtConnectionMask() & conm;
		int mx = -1;
		int ps = 0;
		int cs = 0;

		int ch;
		for(int chm2 = chm; chm2 > 0; ps = Math.max(ps, irp.scanPoweringStrength(cons | indcon, ch))) {
			ch = Integer.numberOfTrailingZeros(chm2);
			chm2 &= ~(1 << ch);
			cs = Math.max(cs, irp.getCurrentStrength(conm, ch));
			mx = Math.max(mx, getMaxCurrentStrength(world, i, j, k, cons, indcon, ch));
		}

		if(ps <= cs && (mx == cs + 1 || cs == 0 && mx == 0)) {
			return cs;
		} else if(ps == cs && mx <= cs) {
			return cs;
		} else {
			cs = Math.max(ps, cs);
			if(cs >= mx) {
				if(cs > ps) {
					cs = 0;
				}
			} else {
				cs = Math.max(0, mx - 1);
			}
			if((chm & 1) > 0) {
				addUpdateBlock(i, j, k);
			}
			addSearchBlocks(i, j, k, cons, indcon);
			return cs;
		}
	}

	public static boolean isSearching() {
		return searching;
	}

	public static void addCompatibleMapping(int a, int b) {
		powerClassMapping.add(Arrays.asList(new Integer[]{Integer.valueOf(a), Integer.valueOf(b)}));
		powerClassMapping.add(Arrays.asList(new Integer[]{Integer.valueOf(b), Integer.valueOf(a)}));
	}

	public static boolean isCompatible(int a, int b) {
		return a == b || powerClassMapping.contains(Arrays.asList(new Integer[]{Integer.valueOf(a), Integer.valueOf(b)}));
	}
}
