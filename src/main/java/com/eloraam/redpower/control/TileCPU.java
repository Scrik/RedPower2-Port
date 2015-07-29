package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.RedbusLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileCPU extends TileExtended implements IRedbusConnectable, IHandlePackets, IFrameSupport {
	
	public int Rotation = 0;
	public byte[] memory = new byte[8192];
	int addrPOR;
	int addrBRK;
	int regSP;
	int regPC;
	int regA;
	int regB;
	int regX;
	int regY;
	int regR;
	int regI;
	int regD;
	boolean flagC;
	boolean flagZ;
	boolean flagID;
	boolean flagD;
	boolean flagBRK;
	boolean flagO;
	boolean flagN;
	boolean flagE;
	boolean flagM;
	boolean flagX;
	int mmuRBB = 0;
	int mmuRBA = 0;
	int mmuRBW = 0;
	boolean mmuEnRB = false;
	boolean mmuEnRBW = false;
	private boolean rbTimeout = false;
	private boolean waiTimeout = false;
	public int sliceCycles = -1;
	IRedbusConnectable rbCache = null;
	public int rtcTicks = 0;
	public int byte0 = 2;
	public int byte1 = 1;
	public int rbaddr = 0;
	TileBackplane[] backplane = new TileBackplane[7];
	
	public TileCPU() {
		this.coldBootCPU();
	}
	
	public void coldBootCPU() {
		this.addrPOR = 8192;
		this.addrBRK = 8192;
		this.regSP = 512;
		this.regPC = 1024;
		this.regR = 768;
		this.regA = 0;
		this.regX = 0;
		this.regY = 0;
		this.regD = 0;
		this.flagC = false;
		this.flagZ = false;
		this.flagID = false;
		this.flagD = false;
		this.flagBRK = false;
		this.flagO = false;
		this.flagN = false;
		this.flagE = true;
		this.flagM = true;
		this.flagX = true;
		this.memory[0] = (byte) this.byte0;
		this.memory[1] = (byte) this.byte1;
		InputStream is = RedPowerControl.class.getResourceAsStream("/assets/rpcontrol/forth/rpcboot.bin");
		
		try {
			try {
				is.read(this.memory, 1024, 256);
			} finally {
				if (is != null) {
					is.close();
				}
				
			}
		} catch (IOException var6) {
			var6.printStackTrace();
		}
		
		this.sliceCycles = -1;
	}
	
	public void warmBootCPU() {
		if (this.sliceCycles >= 0) {
			this.regSP = 512;
			this.regR = 768;
			this.regPC = this.addrPOR;
		}
		
		this.sliceCycles = 0;
	}
	
	public void haltCPU() {
		this.sliceCycles = -1;
	}
	
	public boolean isRunning() {
		return this.sliceCycles >= 0;
	}
	
	@Override
	public int rbGetAddr() {
		return this.rbaddr;
	}
	
	@Override
	public void rbSetAddr(int addr) {
	}
	
	@Override
	public int rbRead(int reg) {
		return !this.mmuEnRBW ? 0 : this.readOnlyMem(this.mmuRBW + reg);
	}
	
	@Override
	public void rbWrite(int reg, int dat) {
		if (this.mmuEnRBW) {
			this.writeOnlyMem(this.mmuRBW + reg, dat);
		}
	}
	
	@Override
	public int getConnectableMask() {
		return 16777215;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 66;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 0;
	}
	
	@Override
	public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
		this.Rotation = (int) Math.floor(ent.rotationYaw * 4.0F / 360.0F + 0.5D) + 1 & 3;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerControl.instance, 2, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerControl.blockPeripheral;
	}
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	protected void refreshBackplane() {
		boolean bpok = true;
		WorldCoord wc = new WorldCoord(this);
		
		for (int i = 0; i < 7; ++i) {
			if (!bpok) {
				this.backplane[i] = null;
			} else {
				wc.step(CoreLib.rotToSide(this.Rotation));
				TileBackplane tbp = (TileBackplane) CoreLib.getTileEntity(
						super.worldObj, wc, TileBackplane.class);
				this.backplane[i] = tbp;
				if (tbp == null) {
					bpok = false;
				}
			}
		}
		
	}
	
	@Override
	public void updateEntity() {
		++this.rtcTicks;
		if (this.sliceCycles >= 0) {
			this.rbTimeout = false;
			this.rbCache = null;
			this.waiTimeout = false;
			this.sliceCycles += 1000;
			if (this.sliceCycles > 100000) {
				this.sliceCycles = 100000;
			}
			
			this.refreshBackplane();
			
			while (this.sliceCycles > 0 && !this.waiTimeout && !this.rbTimeout) {
				--this.sliceCycles;
				this.executeInsn();
			}
			
		}
	}
	
	protected int readOnlyMem(int addr) {
		addr &= '\uffff';
		if (addr < 8192) {
			return this.memory[addr] & 255;
		} else {
			int atop = (addr >> 13) - 1;
			return this.backplane[atop] == null ? 255 : this.backplane[atop]
					.readBackplane(addr & 8191);
		}
	}
	
	public int readMem(int addr) {
		if (this.mmuEnRB && addr >= this.mmuRBB && addr < this.mmuRBB + 256) {
			if (this.rbCache == null) {
				this.rbCache = RedbusLib.getAddr(super.worldObj,
						new WorldCoord(this), this.mmuRBA);
			}
			
			if (this.rbCache == null) {
				this.rbTimeout = true;
				return 0;
			} else {
				int tr = this.rbCache.rbRead(addr - this.mmuRBB);
				return tr;
			}
		} else {
			return this.readOnlyMem(addr);
		}
	}
	
	protected void writeOnlyMem(int addr, int val) {
		addr &= '\uffff';
		if (addr < 8192) {
			this.memory[addr] = (byte) val;
		} else {
			int atop = (addr >> 13) - 1;
			if (this.backplane[atop] != null) {
				this.backplane[atop].writeBackplane(addr & 8191, val);
			}
		}
	}
	
	public void writeMem(int addr, int val) {
		if (this.mmuEnRB && addr >= this.mmuRBB && addr < this.mmuRBB + 256) {
			if (this.rbCache == null) {
				this.rbCache = RedbusLib.getAddr(super.worldObj,
						new WorldCoord(this), this.mmuRBA);
			}
			
			if (this.rbCache == null) {
				this.rbTimeout = true;
			} else {
				this.rbCache.rbWrite(addr - this.mmuRBB, val & 255);
			}
		} else {
			this.writeOnlyMem(addr, val);
		}
	}
	
	private void incPC() {
		this.regPC = this.regPC + 1 & '\uffff';
	}
	
	private int maskM() {
		return this.flagM ? 255 : '\uffff';
	}
	
	private int maskX() {
		return this.flagX ? 255 : '\uffff';
	}
	
	private int negM() {
		return this.flagM ? 128 : '\u8000';
	}
	
	private int negX() {
		return this.flagX ? 128 : '\u8000';
	}
	
	private int readB() {
		int i = this.readMem(this.regPC);
		this.incPC();
		return i;
	}
	
	private int readM() {
		int i = this.readMem(this.regPC);
		this.incPC();
		if (!this.flagM) {
			i |= this.readMem(this.regPC) << 8;
			this.incPC();
		}
		
		return i;
	}
	
	private int readX() {
		int i = this.readMem(this.regPC);
		this.incPC();
		if (!this.flagX) {
			i |= this.readMem(this.regPC) << 8;
			this.incPC();
		}
		
		return i;
	}
	
	private int readM(int addr) {
		int i = this.readMem(addr);
		if (!this.flagM) {
			i |= this.readMem(addr + 1) << 8;
		}
		
		return i;
	}
	
	private int readX(int addr) {
		int i = this.readMem(addr);
		if (!this.flagX) {
			i |= this.readMem(addr + 1) << 8;
		}
		
		return i;
	}
	
	private void writeM(int addr, int reg) {
		this.writeMem(addr, reg);
		if (!this.flagM) {
			this.writeMem(addr + 1, reg >> 8);
		}
		
	}
	
	private void writeX(int addr, int reg) {
		this.writeMem(addr, reg);
		if (!this.flagX) {
			this.writeMem(addr + 1, reg >> 8);
		}
		
	}
	
	private int readBX() {
		int i = this.readMem(this.regPC) + this.regX;
		if (this.flagX) {
			i &= 255;
		}
		
		this.incPC();
		return i;
	}
	
	private int readBY() {
		int i = this.readMem(this.regPC) + this.regY;
		if (this.flagX) {
			i &= 255;
		}
		
		this.incPC();
		return i;
	}
	
	private int readBS() {
		int i = this.readMem(this.regPC) + this.regSP & '\uffff';
		this.incPC();
		return i;
	}
	
	private int readBR() {
		int i = this.readMem(this.regPC) + this.regR & '\uffff';
		this.incPC();
		return i;
	}
	
	private int readBSWY() {
		int i = this.readMem(this.regPC) + this.regSP & '\uffff';
		this.incPC();
		return this.readW(i) + this.regY & '\uffff';
	}
	
	private int readBRWY() {
		int i = this.readMem(this.regPC) + this.regR & '\uffff';
		this.incPC();
		return this.readW(i) + this.regY & '\uffff';
	}
	
	private int readW() {
		int i = this.readMem(this.regPC);
		this.incPC();
		i |= this.readMem(this.regPC) << 8;
		this.incPC();
		return i;
	}
	
	private int readW(int addr) {
		int i = this.readMem(addr);
		i |= this.readMem(addr + 1) << 8;
		return i;
	}
	
	private int readWX() {
		int i = this.readMem(this.regPC);
		this.incPC();
		i |= this.readMem(this.regPC) << 8;
		this.incPC();
		return i + this.regX & '\uffff';
	}
	
	private int readWY() {
		int i = this.readMem(this.regPC);
		this.incPC();
		i |= this.readMem(this.regPC) << 8;
		this.incPC();
		return i + this.regY & '\uffff';
	}
	
	private int readWXW() {
		int i = this.readMem(this.regPC);
		this.incPC();
		i |= this.readMem(this.regPC) << 8;
		this.incPC();
		i = i + this.regX & '\uffff';
		int j = this.readMem(i);
		j |= this.readMem(i + 1) << 8;
		return j;
	}
	
	private int readBW() {
		int i = this.readMem(this.regPC);
		this.incPC();
		int j = this.readMem(i);
		j |= this.readMem(i + 1) << 8;
		return j;
	}
	
	private int readWW() {
		int i = this.readMem(this.regPC);
		this.incPC();
		i |= this.readMem(this.regPC) << 8;
		this.incPC();
		int j = this.readMem(i);
		j |= this.readMem(i + 1) << 8;
		return j;
	}
	
	private int readBXW() {
		int i = this.readMem(this.regPC) + this.regX & 255;
		this.incPC();
		int j = this.readMem(i);
		j |= this.readMem(i + 1) << 8;
		return j;
	}
	
	private int readBWY() {
		int i = this.readMem(this.regPC);
		this.incPC();
		int j = this.readMem(i);
		j |= this.readMem(i + 1) << 8;
		return j + this.regY & '\uffff';
	}
	
	private void upNZ() {
		this.flagN = (this.regA & this.negM()) > 0;
		this.flagZ = this.regA == 0;
	}
	
	private void upNZ(int i) {
		this.flagN = (i & this.negM()) > 0;
		this.flagZ = i == 0;
	}
	
	private void upNZX(int i) {
		this.flagN = (i & this.negX()) > 0;
		this.flagZ = i == 0;
	}
	
	private void push1(int b) {
		if (this.flagE) {
			this.regSP = this.regSP - 1 & 255 | this.regSP & '\uff00';
		} else {
			this.regSP = this.regSP - 1 & '\uffff';
		}
		
		this.writeMem(this.regSP, b);
	}
	
	private void push1r(int b) {
		this.regR = this.regR - 1 & '\uffff';
		this.writeMem(this.regR, b);
	}
	
	private void push2(int w) {
		this.push1(w >> 8);
		this.push1(w & 255);
	}
	
	private void push2r(int w) {
		this.push1r(w >> 8);
		this.push1r(w & 255);
	}
	
	private void pushM(int b) {
		if (this.flagM) {
			this.push1(b);
		} else {
			this.push2(b);
		}
		
	}
	
	private void pushX(int b) {
		if (this.flagX) {
			this.push1(b);
		} else {
			this.push2(b);
		}
		
	}
	
	private void pushMr(int b) {
		if (this.flagM) {
			this.push1r(b);
		} else {
			this.push2r(b);
		}
		
	}
	
	private void pushXr(int b) {
		if (this.flagX) {
			this.push1r(b);
		} else {
			this.push2r(b);
		}
		
	}
	
	private int pop1() {
		int tr = this.readMem(this.regSP);
		if (this.flagE) {
			this.regSP = this.regSP + 1 & 255 | this.regSP & '\uff00';
		} else {
			this.regSP = this.regSP + 1 & '\uffff';
		}
		
		return tr;
	}
	
	private int pop1r() {
		int tr = this.readMem(this.regR);
		this.regR = this.regR + 1 & '\uffff';
		return tr;
	}
	
	private int pop2() {
		int tr = this.pop1();
		tr |= this.pop1() << 8;
		return tr;
	}
	
	private int pop2r() {
		int tr = this.pop1r();
		tr |= this.pop1r() << 8;
		return tr;
	}
	
	private int popM() {
		return this.flagM ? this.pop1() : this.pop2();
	}
	
	private int popMr() {
		return this.flagM ? this.pop1r() : this.pop2r();
	}
	
	private int popX() {
		return this.flagX ? this.pop1() : this.pop2();
	}
	
	private int popXr() {
		return this.flagX ? this.pop1r() : this.pop2r();
	}
	
	private int getFlags() {
		return (this.flagC ? 1 : 0) | (this.flagZ ? 2 : 0)
				| (this.flagID ? 4 : 0) | (this.flagD ? 8 : 0)
				| (this.flagX ? 16 : 0) | (this.flagM ? 32 : 0)
				| (this.flagO ? 64 : 0) | (this.flagN ? 128 : 0);
	}
	
	private void setFlags(int flags) {
		this.flagC = (flags & 1) > 0;
		this.flagZ = (flags & 2) > 0;
		this.flagID = (flags & 4) > 0;
		this.flagD = (flags & 8) > 0;
		boolean m2 = (flags & 32) > 0;
		this.flagO = (flags & 64) > 0;
		this.flagN = (flags & 128) > 0;
		if (this.flagE) {
			this.flagX = false;
			this.flagM = false;
		} else {
			this.flagX = (flags & 16) > 0;
			if (this.flagX) {
				this.regX &= 255;
				this.regY &= 255;
			}
			
			if (m2 != this.flagM) {
				if (m2) {
					this.regB = this.regA >> 8;
					this.regA &= 255;
				} else {
					this.regA |= this.regB << 8;
				}
				
				this.flagM = m2;
			}
		}
		
	}
	
	private void i_adc(int val) {
		int v;
		if (this.flagM) {
			if (this.flagD) {
				v = (this.regA & 15) + (val & 15) + (this.flagC ? 1 : 0);
				if (v > 9) {
					v = (v + 6 & 15) + 16;
				}
				
				int v2 = (this.regA & 240) + (val & 240) + v;
				if (v2 > 160) {
					v2 += 96;
				}
				
				this.flagC = v2 > 100;
				this.regA = v2 & 255;
				this.flagO = false;
			} else {
				v = this.regA + val + (this.flagC ? 1 : 0);
				this.flagC = v > 255;
				this.flagO = ((v ^ this.regA) & (v ^ val) & 128) > 0;
				this.regA = v & 255;
			}
		} else {
			v = this.regA + val + (this.flagC ? 1 : 0);
			this.flagC = v > '\uffff';
			this.flagO = ((v ^ this.regA) & (v ^ val) & '\u8000') > 0;
			this.regA = v & '\uffff';
		}
		
		this.upNZ();
	}
	
	private void i_sbc(int val) {
		int v;
		if (this.flagM) {
			if (this.flagD) {
				v = (this.regA & 15) - (val & 15) + (this.flagC ? 1 : 0) - 1;
				if (v < 0) {
					v = (v - 6 & 15) - 16;
				}
				
				int v2 = (this.regA & 240) - (val & 240) + v;
				if (v2 < 0) {
					v2 -= 96;
				}
				
				this.flagC = v2 < 100;
				this.regA = v2 & 255;
				this.flagO = false;
			} else {
				v = this.regA - val + (this.flagC ? 1 : 0) - 1;
				this.flagC = (v & 256) == 0;
				this.flagO = ((v ^ this.regA) & (v ^ -val) & 128) > 0;
				this.regA = v & 255;
			}
		} else {
			v = this.regA - val + (this.flagC ? 1 : 0) - 1;
			this.flagC = (v & 65536) == 0;
			this.flagO = ((v ^ this.regA) & (v ^ -val) & '\u8000') > 0;
			this.regA = v & '\uffff';
		}
		
		this.upNZ();
	}
	
	private void i_mul(int val) {
		if (this.flagM) {
			int v;
			if (this.flagC) {
				v = (byte) val * (byte) this.regA;
			} else {
				v = val * this.regA;
			}
			
			this.regA = v & 255;
			this.regD = v >> 8 & 255;
			this.flagN = v < 0;
			this.flagZ = v == 0;
			this.flagO = this.regD != 0 && this.regD != 255;
		} else {
			long v1;
			if (this.flagC) {
				v1 = (short) val * (short) this.regA;
			} else {
				v1 = val * this.regA;
			}
			
			this.regA = (int) (v1 & 65535L);
			this.regD = (int) (v1 >> 16 & 65535L);
			this.flagN = v1 < 0L;
			this.flagZ = v1 == 0L;
			this.flagO = this.regD != 0 && this.regD != '\uffff';
		}
		
	}
	
	private void i_div(int val) {
		if (val == 0) {
			this.regA = 0;
			this.regD = 0;
			this.flagO = true;
			this.flagZ = false;
			this.flagN = false;
		} else {
			int q;
			if (this.flagM) {
				if (this.flagC) {
					q = (byte) this.regD << 8 | this.regA;
					val = (byte) val;
				} else {
					q = this.regD << 8 | this.regA;
				}
				
				this.regD = q % val & 255;
				q /= val;
				this.regA = q & 255;
				if (this.flagC) {
					this.flagO = q > 127 || q < -128;
				} else {
					this.flagO = q > 255;
				}
				
				this.flagZ = this.regA == 0;
				this.flagN = q < 0;
			} else if (this.flagC) {
				q = (short) this.regD << 16 | this.regA;
				short val1 = (short) val;
				this.regD = q % val1 & '\uffff';
				q /= val1;
				this.regA = q & '\uffff';
				this.flagO = q > 32767 || q < -32768;
				this.flagZ = this.regA == 0;
				this.flagN = q < 0;
			} else {
				long q1 = this.regD << 16 | this.regA;
				this.regD = (int) (q1 % val & 65535L);
				q1 /= val;
				this.regA = (int) (q1 & 65535L);
				this.flagO = q1 > 65535L;
				this.flagZ = this.regA == 0;
				this.flagN = q1 < 0L;
			}
			
		}
	}
	
	private void i_and(int val) {
		this.regA &= val;
		this.upNZ();
	}
	
	private void i_asl(int addr) {
		int i = this.readM(addr);
		this.flagC = (i & this.negM()) > 0;
		i = i << 1 & this.maskM();
		this.upNZ(i);
		this.writeM(addr, i);
	}
	
	private void i_lsr(int addr) {
		int i = this.readM(addr);
		this.flagC = (i & 1) > 0;
		i >>>= 1;
		this.upNZ(i);
		this.writeM(addr, i);
	}
	
	private void i_rol(int addr) {
		int i = this.readM(addr);
		int n = (i << 1 | (this.flagC ? 1 : 0)) & this.maskM();
		this.flagC = (i & this.negM()) > 0;
		this.upNZ(n);
		this.writeM(addr, n);
	}
	
	private void i_ror(int addr) {
		int i = this.readM(addr);
		int n = i >>> 1 | (this.flagC ? this.negM() : 0);
		this.flagC = (i & 1) > 0;
		this.upNZ(n);
		this.writeM(addr, n);
	}
	
	private void i_brc(boolean cond) {
		int n = this.readB();
		if (cond) {
			this.regPC = this.regPC + (byte) n & '\uffff';
		}
		
	}
	
	private void i_bit(int val) {
		if (this.flagM) {
			this.flagO = (val & 64) > 0;
			this.flagN = (val & 128) > 0;
		} else {
			this.flagO = (val & 16384) > 0;
			this.flagN = (val & '\u8000') > 0;
		}
		
		this.flagZ = (val & this.regA) > 0;
	}
	
	private void i_trb(int val) {
		this.flagZ = (val & this.regA) > 0;
		this.regA &= ~val;
	}
	
	private void i_tsb(int val) {
		this.flagZ = (val & this.regA) > 0;
		this.regA |= val;
	}
	
	private void i_cmp(int reg, int val) {
		reg -= val;
		this.flagC = reg >= 0;
		this.flagZ = reg == 0;
		this.flagN = (reg & this.negM()) > 0;
	}
	
	private void i_cmpx(int reg, int val) {
		reg -= val;
		this.flagC = reg >= 0;
		this.flagZ = reg == 0;
		this.flagN = (reg & this.negX()) > 0;
	}
	
	private void i_dec(int addr) {
		int i = this.readM(addr);
		i = i - 1 & this.maskM();
		this.writeM(addr, i);
		this.upNZ(i);
	}
	
	private void i_inc(int addr) {
		int i = this.readM(addr);
		i = i + 1 & this.maskM();
		this.writeM(addr, i);
		this.upNZ(i);
	}
	
	private void i_eor(int val) {
		this.regA ^= val;
		this.upNZ();
	}
	
	private void i_or(int val) {
		this.regA |= val;
		this.upNZ();
	}
	
	private void i_mmu(int mmu) {
		switch (mmu) {
			case 0:
				int t = this.regA & 255;
				if (this.mmuRBA != t) {
					if (this.rbCache != null) {
						this.rbTimeout = true;
					}
					
					this.mmuRBA = t;
				}
				break;
			case 1:
				this.mmuRBB = this.regA;
				break;
			case 2:
				this.mmuEnRB = true;
				break;
			case 3:
				this.mmuRBW = this.regA;
				break;
			case 4:
				this.mmuEnRBW = true;
				break;
			case 5:
				this.addrBRK = this.regA;
				break;
			case 6:
				this.addrPOR = this.regA;
				break;
			case 128:
				this.regA = this.mmuRBA;
				break;
			case 129:
				this.regA = this.mmuRBB;
				if (this.flagM) {
					this.regB = this.regA >> 8;
					this.regA &= 255;
				}
				break;
			case 130:
				this.mmuEnRB = false;
				break;
			case 131:
				this.regA = this.mmuRBW;
				if (this.flagM) {
					this.regB = this.regA >> 8;
					this.regA &= 255;
				}
				break;
			case 132:
				this.mmuEnRBW = false;
				break;
			case 133:
				this.regA = this.addrBRK;
				if (this.flagM) {
					this.regB = this.regA >> 8;
					this.regA &= 255;
				}
				break;
			case 134:
				this.regA = this.addrPOR;
				if (this.flagM) {
					this.regB = this.regA >> 8;
					this.regA &= 255;
				}
				break;
			case 135:
				this.regA = this.rtcTicks & '\uffff';
				this.regD = this.rtcTicks >> 16 & '\uffff';
		}
		
	}
	
	public void executeInsn() {
		int insn = this.readMem(this.regPC);
		this.incPC();
		int n;
		switch (insn) {
			case 0:
				this.push2(this.regPC);
				this.push1(this.getFlags());
				this.flagBRK = true;
				this.regPC = this.addrBRK;
				break;
			case 1:
				this.i_or(this.readM(this.readBXW()));
				break;
			case 2:
				this.regPC = this.readW(this.regI);
				this.regI += 2;
				break;
			case 3:
				this.i_or(this.readM(this.readBS()));
				break;
			case 4:
				this.i_tsb(this.readM(this.readB()));
				break;
			case 5:
				this.i_or(this.readM(this.readB()));
				break;
			case 6:
				this.i_asl(this.readB());
				break;
			case 7:
				this.i_or(this.readM(this.readBR()));
				break;
			case 8:
				this.push1(this.getFlags());
				break;
			case 9:
				this.i_or(this.readM());
				break;
			case 10:
				this.flagC = (this.regA & this.negM()) > 0;
				this.regA = this.regA << 1 & this.maskM();
				this.upNZ();
				break;
			case 11:
				this.push2r(this.regI);
				break;
			case 12:
				this.i_tsb(this.readM(this.readW()));
				break;
			case 13:
				this.i_or(this.readM(this.readW()));
				break;
			case 14:
				this.i_asl(this.readW());
				break;
			case 15:
				this.i_mul(this.readM(this.readB()));
				break;
			case 16:
				this.i_brc(!this.flagN);
				break;
			case 17:
				this.i_or(this.readM(this.readBWY()));
				break;
			case 18:
				this.i_or(this.readM(this.readBW()));
				break;
			case 19:
				this.i_or(this.readM(this.readBSWY()));
				break;
			case 20:
				this.i_trb(this.readM(this.readB()));
				break;
			case 21:
				this.i_or(this.readM(this.readBX()));
				break;
			case 22:
				this.i_asl(this.readBX());
				break;
			case 23:
				this.i_or(this.readM(this.readBRWY()));
				break;
			case 24:
				this.flagC = false;
				break;
			case 25:
				this.i_or(this.readM(this.readWY()));
				break;
			case 26:
				this.regA = this.regA + 1 & this.maskM();
				this.upNZ(this.regA);
				break;
			case 27:
				this.pushXr(this.regX);
				break;
			case 28:
				this.i_trb(this.readM(this.readW()));
				break;
			case 29:
				this.i_or(this.readM(this.readWX()));
				break;
			case 30:
				this.i_asl(this.readWX());
				break;
			case 31:
				this.i_mul(this.readM(this.readBX()));
				break;
			case 32:
				this.push2(this.regPC + 1);
				this.regPC = this.readW();
				break;
			case 33:
				this.i_and(this.readM(this.readBXW()));
				break;
			case 34:
				this.push2r(this.regI);
				this.regI = this.regPC + 2;
				this.regPC = this.readW(this.regPC);
				break;
			case 35:
				this.i_and(this.readM(this.readBS()));
				break;
			case 36:
				this.i_bit(this.readM(this.readB()));
				break;
			case 37:
				this.i_and(this.readM(this.readB()));
				break;
			case 38:
				this.i_rol(this.readB());
				break;
			case 39:
				this.i_and(this.readM(this.readBR()));
				break;
			case 40:
				this.setFlags(this.pop1());
				break;
			case 41:
				this.i_and(this.readM());
				break;
			case 42:
				n = (this.regA << 1 | (this.flagC ? 1 : 0)) & this.maskM();
				this.flagC = (this.regA & this.negM()) > 0;
				this.regA = n;
				this.upNZ();
				break;
			case 43:
				this.regI = this.pop2r();
				this.upNZX(this.regI);
				break;
			case 44:
				this.i_bit(this.readM(this.readW()));
				break;
			case 45:
				this.i_and(this.readM(this.readW()));
				break;
			case 46:
				this.i_rol(this.readW());
				break;
			case 47:
				this.i_mul(this.readM(this.readW()));
				break;
			case 48:
				this.i_brc(this.flagN);
				break;
			case 49:
				this.i_and(this.readM(this.readBWY()));
				break;
			case 50:
				this.i_and(this.readM(this.readBW()));
				break;
			case 51:
				this.i_and(this.readM(this.readBSWY()));
				break;
			case 52:
				this.i_bit(this.readM(this.readBX()));
				break;
			case 53:
				this.i_and(this.readM(this.readBX()));
				break;
			case 54:
				this.i_rol(this.readBX());
				break;
			case 55:
				this.i_and(this.readM(this.readBRWY()));
				break;
			case 56:
				this.flagC = true;
				break;
			case 57:
				this.i_and(this.readM(this.readWY()));
				break;
			case 58:
				this.regA = this.regA - 1 & this.maskM();
				this.upNZ(this.regA);
				break;
			case 59:
				this.regX = this.popXr();
				this.upNZX(this.regX);
				break;
			case 60:
				this.i_bit(this.readM(this.readWX()));
				break;
			case 61:
				this.i_and(this.readM(this.readWX()));
				break;
			case 62:
				this.i_rol(this.readWX());
				break;
			case 63:
				this.i_mul(this.readM(this.readWX()));
				break;
			case 64:
				this.setFlags(this.pop1());
				this.regPC = this.pop2();
				break;
			case 65:
				this.i_eor(this.readM(this.readBXW()));
				break;
			case 66:
				if (this.flagM) {
					this.regA = this.readMem(this.regI);
					++this.regI;
				} else {
					this.regA = this.readW(this.regI);
					this.regI += 2;
				}
				break;
			case 67:
				this.i_eor(this.readM(this.readBS()));
				break;
			case 68:
				this.push2r(this.readW());
				break;
			case 69:
				this.i_eor(this.readM(this.readB()));
				break;
			case 70:
				this.i_lsr(this.readB());
				break;
			case 71:
				this.i_eor(this.readM(this.readBR()));
				break;
			case 72:
				this.pushM(this.regA);
				break;
			case 73:
				this.i_eor(this.readM());
				break;
			case 74:
				this.flagC = (this.regA & 1) > 0;
				this.regA >>>= 1;
				this.upNZ();
				break;
			case 75:
				this.pushMr(this.regA);
				break;
			case 76:
				this.regPC = this.readW();
				break;
			case 77:
				this.i_eor(this.readM(this.readW()));
				break;
			case 78:
				this.i_lsr(this.readW());
				break;
			case 79:
				this.i_div(this.readM(this.readB()));
				break;
			case 80:
				this.i_brc(!this.flagO);
				break;
			case 81:
				this.i_eor(this.readM(this.readBWY()));
				break;
			case 82:
				this.i_eor(this.readM(this.readBW()));
				break;
			case 83:
				this.i_eor(this.readM(this.readBSWY()));
				break;
			case 84:
				this.push2r(this.readBW());
				break;
			case 85:
				this.i_eor(this.readM(this.readBX()));
				break;
			case 86:
				this.i_lsr(this.readBX());
				break;
			case 87:
				this.i_eor(this.readM(this.readBRWY()));
				break;
			case 88:
				this.flagID = false;
				break;
			case 89:
				this.i_eor(this.readM(this.readWY()));
				break;
			case 90:
				this.pushX(this.regY);
				break;
			case 91:
				this.pushXr(this.regY);
				break;
			case 92:
				this.regI = this.regX;
				this.upNZX(this.regX);
				break;
			case 93:
				this.i_eor(this.readM(this.readWX()));
				break;
			case 94:
				this.i_lsr(this.readWX());
				break;
			case 95:
				this.i_div(this.readM(this.readBX()));
				break;
			case 96:
				this.regPC = this.pop2() + 1;
				break;
			case 97:
				this.i_adc(this.readM(this.readBXW()));
				break;
			case 98:
				n = this.readB();
				this.push2(this.regPC + n);
				break;
			case 99:
				this.i_adc(this.readM(this.readBS()));
				break;
			case 100:
				this.writeM(this.readB(), 0);
				break;
			case 101:
				this.i_adc(this.readM(this.readB()));
				break;
			case 102:
				this.i_ror(this.readB());
				break;
			case 103:
				this.i_adc(this.readM(this.readBR()));
				break;
			case 104:
				this.regA = this.popM();
				this.upNZ();
				break;
			case 105:
				this.i_adc(this.readM());
				break;
			case 106:
				n = this.regA >>> 1 | (this.flagC ? this.negM() : 0);
				this.flagC = (this.regA & 1) > 0;
				this.regA = n;
				this.upNZ();
				break;
			case 107:
				this.regA = this.popMr();
				this.upNZ(this.regA);
				break;
			case 108:
				this.regPC = this.readWW();
				break;
			case 109:
				this.i_adc(this.readM(this.readW()));
				break;
			case 110:
				this.i_ror(this.readW());
				break;
			case 111:
				this.i_div(this.readM(this.readW()));
				break;
			case 112:
				this.i_brc(this.flagO);
				break;
			case 113:
				this.i_adc(this.readM(this.readBWY()));
				break;
			case 114:
				this.i_adc(this.readM(this.readBW()));
				break;
			case 115:
				this.i_adc(this.readM(this.readBSWY()));
				break;
			case 116:
				this.writeM(this.readBX(), 0);
				break;
			case 117:
				this.i_adc(this.readM(this.readBX()));
				break;
			case 118:
				this.i_ror(this.readBX());
				break;
			case 119:
				this.i_adc(this.readM(this.readBRWY()));
				break;
			case 120:
				this.flagID = true;
				break;
			case 121:
				this.i_adc(this.readM(this.readWY()));
				break;
			case 122:
				this.regY = this.popX();
				this.upNZX(this.regY);
				break;
			case 123:
				this.regY = this.popXr();
				this.upNZX(this.regY);
				break;
			case 124:
				this.regPC = this.readWXW();
				break;
			case 125:
				this.i_adc(this.readM(this.readWX()));
				break;
			case 126:
				this.i_ror(this.readWX());
				break;
			case 127:
				this.i_div(this.readM(this.readWX()));
				break;
			case 128:
				this.i_brc(true);
				break;
			case 129:
				this.writeM(this.readBXW(), this.regA);
				break;
			case 130:
				n = this.readB();
				this.push2r(this.regPC + n);
				break;
			case 131:
				this.writeM(this.readBS(), this.regA);
				break;
			case 132:
				this.writeX(this.readB(), this.regY);
				break;
			case 133:
				this.writeM(this.readB(), this.regA);
				break;
			case 134:
				this.writeX(this.readB(), this.regX);
				break;
			case 135:
				this.writeM(this.readBR(), this.regA);
				break;
			case 136:
				this.regY = this.regY - 1 & this.maskX();
				this.upNZ(this.regY);
				break;
			case 137:
				this.flagZ = (this.readM() & this.regA) == 0;
				break;
			case 138:
				this.regA = this.regX;
				if (this.flagM) {
					this.regA &= 255;
				}
				
				this.upNZ();
				break;
			case 139:
				if (this.flagX) {
					this.regSP = this.regR & '\uff00' | this.regX & 255;
				} else {
					this.regR = this.regX;
				}
				
				this.upNZX(this.regR);
				break;
			case 140:
				this.writeX(this.readW(), this.regY);
				break;
			case 141:
				this.writeM(this.readW(), this.regA);
				break;
			case 142:
				this.writeX(this.readW(), this.regX);
				break;
			case 143:
				this.regD = 0;
				this.regB = 0;
				break;
			case 144:
				this.i_brc(!this.flagC);
				break;
			case 145:
				this.writeM(this.readBWY(), this.regA);
				break;
			case 146:
				this.writeM(this.readBW(), this.regA);
				break;
			case 147:
				this.writeM(this.readBSWY(), this.regA);
				break;
			case 148:
				this.writeX(this.readBX(), this.regY);
				break;
			case 149:
				this.writeM(this.readBX(), this.regA);
				break;
			case 150:
				this.writeX(this.readBY(), this.regX);
				break;
			case 151:
				this.writeM(this.readBRWY(), this.regA);
				break;
			case 152:
				this.regA = this.regY;
				if (this.flagM) {
					this.regA &= 255;
				}
				
				this.upNZX(this.regY);
				break;
			case 153:
				this.writeM(this.readWY(), this.regA);
				break;
			case 154:
				if (this.flagX) {
					this.regSP = this.regSP & '\uff00' | this.regX & 255;
				} else {
					this.regSP = this.regX;
				}
				
				this.upNZX(this.regX);
				break;
			case 155:
				this.regY = this.regX;
				this.upNZX(this.regY);
				break;
			case 156:
				this.writeM(this.readW(), 0);
				break;
			case 157:
				this.writeM(this.readWX(), this.regA);
				break;
			case 158:
				this.writeM(this.readWX(), 0);
				break;
			case 159:
				this.regD = (this.regA & this.negM()) > 0 ? '\uffff' : 0;
				this.regB = this.regD & 255;
				break;
			case 160:
				this.regY = this.readX();
				this.upNZ(this.regY);
				break;
			case 161:
				this.regA = this.readM(this.readBXW());
				this.upNZ();
				break;
			case 162:
				this.regX = this.readX();
				this.upNZ(this.regX);
				break;
			case 163:
				this.regA = this.readM(this.readBS());
				this.upNZ();
				break;
			case 164:
				this.regY = this.readX(this.readB());
				this.upNZ(this.regY);
				break;
			case 165:
				this.regA = this.readM(this.readB());
				this.upNZ();
				break;
			case 166:
				this.regX = this.readX(this.readB());
				this.upNZ(this.regX);
				break;
			case 167:
				this.regA = this.readM(this.readBR());
				this.upNZ();
				break;
			case 168:
				this.regY = this.regA;
				if (this.flagX) {
					this.regY &= 255;
				}
				
				this.upNZX(this.regY);
				break;
			case 169:
				this.regA = this.readM();
				this.upNZ();
				break;
			case 170:
				this.regX = this.regA;
				if (this.flagX) {
					this.regX &= 255;
				}
				
				this.upNZX(this.regX);
				break;
			case 171:
				this.regX = this.regR;
				if (this.flagX) {
					this.regX &= 255;
				}
				
				this.upNZX(this.regX);
				break;
			case 172:
				this.regY = this.readX(this.readW());
				this.upNZ(this.regY);
				break;
			case 173:
				this.regA = this.readM(this.readW());
				this.upNZ();
				break;
			case 174:
				this.regX = this.readX(this.readW());
				this.upNZ(this.regX);
				break;
			case 175:
				this.regA = this.regD;
				if (this.flagM) {
					this.regA &= 255;
				}
				
				this.upNZ(this.regA);
				break;
			case 176:
				this.i_brc(this.flagC);
				break;
			case 177:
				this.regA = this.readM(this.readBWY());
				this.upNZ();
				break;
			case 178:
				this.regA = this.readM(this.readBW());
				this.upNZ();
				break;
			case 179:
				this.regA = this.readM(this.readBSWY());
				this.upNZ();
				break;
			case 180:
				this.regY = this.readX(this.readBX());
				this.upNZ(this.regY);
				break;
			case 181:
				this.regA = this.readM(this.readBX());
				this.upNZ();
				break;
			case 182:
				this.regX = this.readX(this.readBY());
				this.upNZ(this.regX);
				break;
			case 183:
				this.regA = this.readM(this.readBRWY());
				this.upNZ();
				break;
			case 184:
				this.flagO = false;
				break;
			case 185:
				this.regA = this.readM(this.readWY());
				this.upNZ();
				break;
			case 186:
				this.regX = this.regSP;
				if (this.flagX) {
					this.regX &= 255;
				}
				
				this.upNZX(this.regX);
				break;
			case 187:
				this.regX = this.regY;
				this.upNZX(this.regX);
				break;
			case 188:
				this.regY = this.readX(this.readWX());
				this.upNZ(this.regY);
				break;
			case 189:
				this.regA = this.readM(this.readWX());
				this.upNZ();
				break;
			case 190:
				this.regX = this.readX(this.readWY());
				this.upNZ(this.regX);
				break;
			case 191:
				if (this.flagM) {
					this.regD = this.regA | this.regB << 8;
				} else {
					this.regD = this.regA;
				}
				
				this.upNZ(this.regA);
				break;
			case 192:
				this.i_cmpx(this.regY, this.readX());
				break;
			case 193:
				this.i_cmp(this.regA, this.readM(this.readBXW()));
				break;
			case 194:
				this.setFlags(this.getFlags() & ~this.readB());
				break;
			case 195:
				this.i_cmp(this.regA, this.readM(this.readBS()));
				break;
			case 196:
				this.i_cmpx(this.regY, this.readX(this.readB()));
				break;
			case 197:
				this.i_cmp(this.regA, this.readM(this.readB()));
				break;
			case 198:
				this.i_dec(this.readB());
				break;
			case 199:
				this.i_cmp(this.regA, this.readM(this.readBR()));
				break;
			case 200:
				this.regY = this.regY + 1 & this.maskX();
				this.upNZ(this.regY);
				break;
			case 201:
				this.i_cmp(this.regA, this.readM());
				break;
			case 202:
				this.regX = this.regX - 1 & this.maskX();
				this.upNZ(this.regX);
				break;
			case 203:
				this.waiTimeout = true;
				break;
			case 204:
				this.i_cmpx(this.regY, this.readX(this.readW()));
				break;
			case 205:
				this.i_cmp(this.regA, this.readM(this.readW()));
				break;
			case 206:
				this.i_dec(this.readW());
				break;
			case 207:
				this.regD = this.popM();
				break;
			case 208:
				this.i_brc(!this.flagZ);
				break;
			case 209:
				this.i_cmp(this.regA, this.readM(this.readBWY()));
				break;
			case 210:
				this.i_cmp(this.regA, this.readM(this.readBW()));
				break;
			case 211:
				this.i_cmp(this.regA, this.readM(this.readBSWY()));
				break;
			case 212:
				this.push2(this.readBW());
				break;
			case 213:
				this.i_cmp(this.regA, this.readM(this.readBX()));
				break;
			case 214:
				this.i_dec(this.readBX());
				break;
			case 215:
				this.i_cmp(this.regA, this.readM(this.readBRWY()));
				break;
			case 216:
				this.flagD = false;
				break;
			case 217:
				this.i_cmp(this.regA, this.readM(this.readWY()));
				break;
			case 218:
				this.pushX(this.regX);
				break;
			case 219:
				this.sliceCycles = -1;
				if (super.worldObj.isAirBlock(super.xCoord, super.yCoord + 1,
						super.zCoord)) {
					super.worldObj.playSoundEffect(super.xCoord + 0.5D,
							super.yCoord + 0.5D, super.zCoord + 0.5D,
							"fire.ignite", 1.0F,
							super.worldObj.rand.nextFloat() * 0.4F + 0.8F);
					super.worldObj.setBlock(super.xCoord, super.yCoord + 1, super.zCoord, Blocks.fire, 0, 3);
				}
				break;
			case 220:
				this.regX = this.regI;
				if (this.flagX) {
					this.regX &= 255;
				}
				
				this.upNZX(this.regX);
				break;
			case 221:
				this.i_cmp(this.regA, this.readM(this.readWX()));
				break;
			case 222:
				this.i_dec(this.readWX());
				break;
			case 223:
				this.pushM(this.regD);
				break;
			case 224:
				this.i_cmpx(this.regX, this.readX());
				break;
			case 225:
				this.i_sbc(this.readM(this.readBXW()));
				break;
			case 226:
				this.setFlags(this.getFlags() | this.readB());
				break;
			case 227:
				this.i_sbc(this.readM(this.readBS()));
				break;
			case 228:
				this.i_cmpx(this.regX, this.readX(this.readB()));
				break;
			case 229:
				this.i_sbc(this.readM(this.readB()));
				break;
			case 230:
				this.i_inc(this.readB());
				break;
			case 231:
				this.i_sbc(this.readM(this.readBR()));
				break;
			case 232:
				this.regX = this.regX + 1 & this.maskX();
				this.upNZ(this.regX);
				break;
			case 233:
				this.i_sbc(this.readM());
			case 234:
			default:
				break;
			case 235:
				if (this.flagM) {
					n = this.regA;
					this.regA = this.regB;
					this.regB = n;
				} else {
					this.regA = this.regA >> 8 & 255 | this.regA << 8
							& '\uff00';
				}
				break;
			case 236:
				this.i_cmpx(this.regX, this.readX(this.readW()));
				break;
			case 237:
				this.i_sbc(this.readM(this.readW()));
				break;
			case 238:
				this.i_inc(this.readW());
				break;
			case 239:
				this.i_mmu(this.readB());
				break;
			case 240:
				this.i_brc(this.flagZ);
				break;
			case 241:
				this.i_sbc(this.readM(this.readBWY()));
				break;
			case 242:
				this.i_sbc(this.readM(this.readBW()));
				break;
			case 243:
				this.i_sbc(this.readM(this.readBSWY()));
				break;
			case 244:
				this.push2(this.readW());
				break;
			case 245:
				this.i_sbc(this.readM(this.readBX()));
				break;
			case 246:
				this.i_inc(this.readBX());
				break;
			case 247:
				this.i_sbc(this.readM(this.readBRWY()));
				break;
			case 248:
				this.flagD = true;
				break;
			case 249:
				this.i_sbc(this.readM(this.readWY()));
				break;
			case 250:
				this.regX = this.popX();
				this.upNZX(this.regX);
				break;
			case 251:
				if (this.flagE != this.flagC) {
					if (this.flagE) {
						this.flagE = false;
						this.flagC = true;
					} else {
						this.flagE = true;
						this.flagC = false;
						if (!this.flagM) {
							this.regB = this.regA >> 8;
						}
						
						this.flagM = true;
						this.flagX = true;
						this.regA &= 255;
						this.regX &= 255;
						this.regY &= 255;
					}
				}
				break;
			case 252:
				this.push2(this.regPC + 1);
				this.regPC = this.readWXW();
				break;
			case 253:
				this.i_sbc(this.readM(this.readWX()));
				break;
			case 254:
				this.i_inc(this.readWX());
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getFramePacket() {
		ArrayList data = new ArrayList();
		data.add(7);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handleFramePacket(ByteBuf buffer) {
		if(buffer.readInt() == 7) {
			this.readFromPacket(buffer);
		}
	}
	
	@Override
	public void onFrameRefresh(IBlockAccess iba) {
	}
	
	@Override
	public void onFramePickup(IBlockAccess iba) {
	}
	
	@Override
	public void onFrameDrop() {
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.memory = tag.getByteArray("ram");
		if (this.memory.length != 8192) {
			this.memory = new byte[8192];
		}
		
		this.Rotation = tag.getByte("rot");
		this.addrPOR = tag.getShort("por") & '\uffff';
		this.addrBRK = tag.getShort("brk") & '\uffff';
		byte efl = tag.getByte("efl");
		this.flagE = (efl & 1) > 0;
		this.mmuEnRB = (efl & 2) > 0;
		this.mmuEnRBW = (efl & 4) > 0;
		this.setFlags(tag.getByte("fl"));
		this.regSP = tag.getShort("rsp") & '\uffff';
		this.regPC = tag.getShort("rpc") & '\uffff';
		this.regA = tag.getShort("ra") & '\uffff';
		if (this.flagM) {
			this.regB = this.regA >> 8;
			this.regA &= 255;
		}
		
		this.regX = tag.getShort("rx") & '\uffff';
		this.regY = tag.getShort("ry") & '\uffff';
		this.regD = tag.getShort("rd") & '\uffff';
		this.regR = tag.getShort("rr") & '\uffff';
		this.regI = tag.getShort("ri") & '\uffff';
		this.mmuRBB = tag.getShort("mmrb") & '\uffff';
		this.mmuRBW = tag.getShort("mmrbw") & '\uffff';
		this.mmuRBA = tag.getByte("mmra") & 255;
		this.sliceCycles = tag.getInteger("cyc");
		this.rtcTicks = tag.getInteger("rtct");
		this.byte0 = tag.getByte("b0") & 255;
		this.byte1 = tag.getByte("b1") & 255;
		this.rbaddr = tag.getByte("rbaddr") & 255;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("rot", (byte) this.Rotation);
		tag.setByteArray("ram", this.memory);
		tag.setShort("por", (short) this.addrPOR);
		tag.setShort("brk", (short) this.addrBRK);
		int efl = (this.flagE ? 1 : 0) | (this.mmuEnRB ? 2 : 0)
				| (this.mmuEnRBW ? 4 : 0);
		tag.setByte("efl", (byte) efl);
		tag.setByte("fl", (byte) this.getFlags());
		tag.setShort("rsp", (short) this.regSP);
		tag.setShort("rpc", (short) this.regPC);
		if (this.flagM) {
			this.regA = this.regA & 255 | this.regB << 8;
		}
		
		tag.setShort("ra", (short) this.regA);
		if (this.flagM) {
			this.regA &= 255;
		}
		
		tag.setShort("rx", (short) this.regX);
		tag.setShort("ry", (short) this.regY);
		tag.setShort("rd", (short) this.regD);
		tag.setShort("rr", (short) this.regR);
		tag.setShort("ri", (short) this.regI);
		tag.setShort("mmrb", (short) this.mmuRBB);
		tag.setShort("mmrbw", (short) this.mmuRBW);
		tag.setByte("mmra", (byte) this.mmuRBA);
		tag.setInteger("cyc", this.sliceCycles);
		tag.setInteger("rtct", this.rtcTicks);
		tag.setByte("b0", (byte) this.byte0);
		tag.setByte("b1", (byte) this.byte1);
		tag.setByte("rbaddr", (byte) this.rbaddr);
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.Rotation = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.Rotation);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ArrayList getNetworkedData(ArrayList data) {
		data.add(7);
		this.writeToPacket(data);
		return data;
	}
	
	@Override
	public void handlePacketData(ByteBuf buffer) {
		try {
			if (buffer.readInt() != 7) {
				return;
			}
			this.readFromPacket(buffer);
		} catch (Throwable thr) {}
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
	}
}
