package com.eloraam.redpower.core;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FluidBuffer;
import com.eloraam.redpower.core.IPipeConnectable;
import com.eloraam.redpower.core.WorldCoord;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

public class PipeLib {
	
	private static boolean isConSide(IBlockAccess iba, int i, int j, int k, int side) {
		TileEntity te = iba.getTileEntity(i, j, k);
		if (te instanceof IPipeConnectable) {
			IPipeConnectable itc1 = (IPipeConnectable) te;
			int ilt1 = itc1.getPipeConnectableSides();
			return (ilt1 & 1 << side) > 0;
		} else if (te instanceof IFluidHandler) {
			IFluidHandler itc = (IFluidHandler) te;
			FluidTankInfo[] info = itc.getTankInfo(ForgeDirection.getOrientation(side));
			return info != null && info[0] != null && info[0].capacity > 0;
		} else {
			return false;
		}
	}
	
	public static int getConnections(IBlockAccess iba, int i, int j, int k) {
		IPipeConnectable itc = (IPipeConnectable) CoreLib.getTileEntity(iba, i, j, k, IPipeConnectable.class);
		if (itc == null) {
			return 0;
		} else {
			int trs = 0;
			int sides = itc.getPipeConnectableSides();
			if ((sides & 1) > 0 && isConSide(iba, i, j - 1, k, 1)) {
				trs |= 1;
			}
			
			if ((sides & 2) > 0 && isConSide(iba, i, j + 1, k, 0)) {
				trs |= 2;
			}
			
			if ((sides & 4) > 0 && isConSide(iba, i, j, k - 1, 3)) {
				trs |= 4;
			}
			
			if ((sides & 8) > 0 && isConSide(iba, i, j, k + 1, 2)) {
				trs |= 8;
			}
			
			if ((sides & 16) > 0 && isConSide(iba, i - 1, j, k, 5)) {
				trs |= 16;
			}
			
			if ((sides & 32) > 0 && isConSide(iba, i + 1, j, k, 4)) {
				trs |= 32;
			}
			return trs;
		}
	}
	
	public static int getFlanges(IBlockAccess iba, WorldCoord wci, int sides) {
		int tr = 0;
		
		for (int i = 0; i < 6; ++i) {
			if ((sides & 1 << i) != 0) {
				WorldCoord wc = wci.copy();
				wc.step(i);
				TileEntity te = iba.getTileEntity(wc.x, wc.y, wc.z);
				if (te != null) {
					if (te instanceof IPipeConnectable) {
						IPipeConnectable itc = (IPipeConnectable) te;
						if ((itc.getPipeFlangeSides() & 1 << (i ^ 1)) > 0) {
							tr |= 1 << i;
						}
					} //TODO: Чет костыльненько...
					
					if (te instanceof IFluidHandler) {
						IFluidHandler itc = (IFluidHandler) te;
						FluidTankInfo[] info = itc.getTankInfo(ForgeDirection.getOrientation(i ^ 1));

						if (info != null && info[0] != null && info[0].capacity > 0) {
							tr |= 1 << i;
						}
					}
				}
			}
		}
		
		return tr;
	}
	
	public static int getPressure(World world, WorldCoord wc, int dir) {
		TileEntity te = world.getTileEntity(wc.x, wc.y, wc.z);
		if (te == null) {
			return 0;
		} else if (te instanceof IPipeConnectable) {
			IPipeConnectable itc1 = (IPipeConnectable) te;
			return itc1.getPipePressure(dir);
		}/* else if (te instanceof IFluidHandler) {
			IFluidHandler itc = (IFluidHandler) te;
			FluidTankInfo[] info = itc.getTankInfo(ForgeDirection.getOrientation(side));
			//return info != null && info[0] != null && info[0].capacity > 0;
			
			if (info == null) {
				return null;
			} else {
				int p = ilt.getTankPressure();
				return p > 0 ? Integer.valueOf(100) : (p < 0 ? Integer.valueOf(-100) : Integer.valueOf(0));
			}
		}*/ else {
			return 0;
		}
	}
	
	/*public static void registerVanillaFluid(Block blockStill, BlockLiquid blockMoving) {
		if (blockStill != null) {
			FluidClassVanilla fluid = new FluidClassVanilla(blockStill,
					blockStill, blockMoving, blockStill.getTextureFile(),
					bl.getBlockTextureFromSide(0));
			fluidByItem.put(Arrays.asList(new Integer[] { Integer
					.valueOf(blockStill), Integer.valueOf(0) }), fluid);
			fluidByBlock.put(Integer.valueOf(blockStill), fluid);
			fluidByBlock.put(Integer.valueOf(blockMoving), fluid);
			fluidByID.put(Integer.valueOf(blockStill), fluid);
		}
	}*/
	
	/*public static void registerForgeFluid(String name, LiquidStack stack) {
		System.out.printf("Fluid registration: %s\n", new Object[] { name });
		Item it = Item.itemsList[stack.itemID];
		if (it != null) {
			int id = stack.itemID + (stack.itemMeta << 16);
			FluidClassItem_DPR fluid = new FluidClassItem_DPR(id, stack.itemID,
					stack.itemMeta, it.getTextureFile(),
					CoreProxy.instance.getItemIcon(it, stack.itemMeta));
			fluidByID.put(Integer.valueOf(id), fluid);
			fluidByItem.put(Arrays.asList(new Integer[] { Integer
					.valueOf(stack.itemID), Integer.valueOf(stack.itemMeta) }),
					fluid);
		}
	}*/
	
	/*public static void registerFluids() {
		LiquidDictionary hack = new LiquidDictionary() {
		};
		Iterator i$ = LiquidDictionary.getLiquids().entrySet().iterator();
		
		while (i$.hasNext()) {
			Entry entry = (Entry) i$.next();
			registerForgeFluid((String) entry.getKey(),
					(LiquidStack) entry.getValue());
		}
		
	}*/
	
	public static int getFluidId(World world, WorldCoord wc) {
		Block bid = world.getBlock(wc.x, wc.y, wc.z);
		if(bid instanceof IFluidBlock) {
			IFluidBlock fcl = (IFluidBlock)bid;
			return fcl.getFluid().getID();
		}
		return 0;
	}
	
	/*public static FluidClass_DPR getLiquidClass(int liquidID) {
		return (FluidClass_DPR) fluidByID.get(Integer.valueOf(liquidID));
	}*/
	
	/*public static FluidClass_DPR getLiquidClass(LiquidStack ls) {
		return (FluidClass_DPR) fluidByItem.get(Arrays
				.asList(new Integer[] { Integer.valueOf(ls.itemID), Integer
						.valueOf(ls.itemMeta) }));
	}*/
	
	public static void movePipeLiquid(World world, IPipeConnectable src, WorldCoord wsrc, int sides) {
		for (int i = 0; i < 6; ++i) {
			if ((sides & 1 << i) != 0) {
				WorldCoord wc = wsrc.coordStep(i);
				TileEntity te = world.getTileEntity(wc.x, wc.y, wc.z);
				if (te != null) {
					int p2;
					int l2;
					if (te instanceof IPipeConnectable) {
						IPipeConnectable itc = (IPipeConnectable) te;
						int ilt = src.getPipePressure(i);
						p2 = itc.getPipePressure(i ^ 1);
						if (ilt < p2) {
							continue;
						}
						
						FluidBuffer p1 = src.getPipeBuffer(i);
						if (p1 == null) {
							continue;
						}
						
						int f1 = p1.getLevel();
						f1 += p1.Delta;
						if (p1.Type == 0 || f1 <= 0) {
							continue;
						}
						
						FluidBuffer l1 = itc.getPipeBuffer(i ^ 1);
						if (l1 == null) {
							continue;
						}
						
						l2 = l1.getLevel();
						if (l1.Type != 0 && l1.Type != p1.Type) {
							continue;
						}
						
						int ls = Math.max(ilt > p2 ? 25 : 0, (f1 - l2) / 2);
						ls = Math.min(Math.min(ls, l1.getMaxLevel() - l2), f1);
						if (ls <= 0) {
							continue;
						}
						
						p1.addLevel(p1.Type, -ls);
						l1.addLevel(p1.Type, ls);
					}
					
					if (te instanceof IFluidHandler) {
						IFluidHandler fluidHandler = (IFluidHandler) te;
						for(FluidTankInfo info : fluidHandler.getTankInfo(ForgeDirection.getOrientation(i ^ 1))) {
							int capacity = info.capacity;
							p2 = info.fluid.amount >= capacity ? 100 : -100; //TODO: Доработать
							int srcPressure = src.getPipePressure(i);
							FluidBuffer buff = src.getPipeBuffer(i);
							if(buff != null) {
								int level = buff.getLevel();
								level += buff.Delta;
								l2 = 0;
								FluidStack fStack = info.fluid;
								if(fStack != null && fStack.amount > 0) {
									if(fStack.getFluid() != null) {
										l2 = fStack.amount;
										if(buff.Type != 0 && buff.Type != fStack.fluidID) {
											break; //TODO: Don't understand, but may work
											//continue;
										}
									} else {
										break;  //TODO: Don't understand, but may work
										//continue;
									}
								}
								
								//
								int qty;
								if (srcPressure < p2 && l2 > 0) {
									qty = Math.max(25, (l2 - level) / 2);
									qty = Math.min(Math.min(qty, buff.getMaxLevel() - level), l2);
									if (qty > 0) {
										FluidStack drStack = fluidHandler.drain(ForgeDirection.getOrientation(i), new FluidStack(fStack.getFluid(), qty), true);
										//LiquidStack ls2 = var19.drain(qty, true);
										buff.addLevel(drStack.fluidID, drStack.amount);
									}
								} else if (srcPressure > p2 && buff.Type != 0 && level > 0) {
									qty = Math.max(25, (level - l2) / 2);
									qty = Math.min(Math.min(qty, info.capacity - l2), level);
									if (qty > 0) {
										//fc = getLiquidClass(var21.Type);
										qty = fluidHandler.fill(ForgeDirection.getOrientation(i), new FluidStack(fStack.getFluid(), qty), true);
										buff.addLevel(buff.Type, -qty);
									}
								}
							}
						}/*
						ILiquidTank var19 = var18.getTank(, (LiquidStack) null);
						if (var19 != null) {
							p2 = var19.getTankPressure();
							p2 = p2 > 0 ? 100 : (p2 < 0 ? -100 : 0);
							int var20 = src.getPipePressure(i);
							FluidBuffer var21 = src.getPipeBuffer(i);
							if (var21 != null) {
								int var22 = var21.getLevel();
								var22 += var21.Delta;
								l2 = 0;
								LiquidStack var23 = var19.getLiquid();
								//FluidClass_DPR fc = null;
								if (var23 != null && var23.amount > 0) {
									fc = getLiquidClass(var23);
									if (fc == null) {
										continue;
									}
									
									l2 = var19.getLiquid().amount;
									if (var21.Type != 0 && var21.Type != fc.getFluidId()) {
										continue;
									}
								}
								
								int qty;
								if (var20 < p2 && l2 > 0) {
									qty = Math.max(25, (l2 - var22) / 2);
									qty = Math.min(
											Math.min(qty, var21.getMaxLevel()
													- var22), l2);
									if (qty > 0) {
										LiquidStack ls2 = var19
												.drain(qty, true);
										var21.addLevel(fc.getFluidId(),
												ls2.amount);
									}
								} else if (var20 > p2 && var21.Type != 0 && var22 > 0) {
									qty = Math.max(25, (var22 - l2) / 2);
									qty = Math.min(Math.min(qty, var19.getCapacity()- l2), var22);
									if (qty > 0) {
										fc = getLiquidClass(var21.Type);
										qty = var19.fill(fc.getLiquidStack(qty), true);
										var21.addLevel(var21.Type, -qty);
									}
								}
							}
						}*/
					}
				}
			}
		}
	}	
}
