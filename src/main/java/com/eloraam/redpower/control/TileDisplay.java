package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.network.IHandlePackets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileDisplay extends TileExtended implements IRedbusConnectable, IHandlePackets, IFrameSupport {
	
	public byte[] screen = new byte[4000];
	public int Rotation = 0;
	public int memRow = 0;
	public int cursX = 0;
	public int cursY = 0;
	public int cursMode = 2;
	public int kbstart = 0;
	public int kbpos = 0;
	public int blitXS = 0;
	public int blitYS = 0;
	public int blitXD = 0;
	public int blitYD = 0;
	public int blitW = 0;
	public int blitH = 0;
	public int blitMode = 0;
	public byte[] kbbuf = new byte[16];
	int rbaddr = 1;
	
	public TileDisplay() {
		Arrays.fill(this.screen, (byte) 32);
	}
	
	@Override
	public int rbGetAddr() {
		return this.rbaddr;
	}
	
	@Override
	public void rbSetAddr(int addr) {
		this.rbaddr = addr;
	}
	
	@Override
	public int rbRead(int reg) {
		if (reg >= 16 && reg < 96) {
			return this.screen[this.memRow * 80 + reg - 16];
		} else {
			switch (reg) {
				case 0:
					return this.memRow;
				case 1:
					return this.cursX;
				case 2:
					return this.cursY;
				case 3:
					return this.cursMode;
				case 4:
					return this.kbstart;
				case 5:
					return this.kbpos;
				case 6:
					return this.kbbuf[this.kbstart] & 255;
				case 7:
					return this.blitMode;
				case 8:
					return this.blitXS;
				case 9:
					return this.blitYS;
				case 10:
					return this.blitXD;
				case 11:
					return this.blitYD;
				case 12:
					return this.blitW;
				case 13:
					return this.blitH;
				default:
					return 0;
			}
		}
	}
	
	@Override
	public void rbWrite(int reg, int dat) {
		this.markDirty();
		if (reg >= 16 && reg < 96) {
			this.screen[this.memRow * 80 + reg - 16] = (byte) dat;
		} else {
			switch (reg) {
				case 0:
					this.memRow = dat;
					if (this.memRow > 49) {
						this.memRow = 49;
					}
					
					return;
				case 1:
					this.cursX = dat;
					return;
				case 2:
					this.cursY = dat;
					return;
				case 3:
					this.cursMode = dat;
					return;
				case 4:
					this.kbstart = dat & 15;
					return;
				case 5:
					this.kbpos = dat & 15;
					return;
				case 6:
					this.kbbuf[this.kbstart] = (byte) dat;
					return;
				case 7:
					this.blitMode = dat;
					return;
				case 8:
					this.blitXS = dat;
					return;
				case 9:
					this.blitYS = dat;
					return;
				case 10:
					this.blitXD = dat;
					return;
				case 11:
					this.blitYD = dat;
					return;
				case 12:
					this.blitW = dat;
					return;
				case 13:
					this.blitH = dat;
					return;
				default:
			}
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
			if (CoreLib.isClient(super.worldObj)) {
				return false;
			} else {
				ItemStack ist = player.inventory.getCurrentItem();
				if (ist == null) {
					return false;
				} else if (!(ist.getItem() instanceof ItemScrewdriver)) {
					return false;
				} else {
					player.openGui(RedPowerBase.instance, 3, super.worldObj,
							super.xCoord, super.yCoord, super.zCoord);
					return true;
				}
			}
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerControl.instance, 1, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerControl.blockPeripheral;
	}
	
	@Override
	public int getExtendedID() {
		return 0;
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	public void pushKey(byte b) {
		int np = this.kbpos + 1 & 15;
		if (np != this.kbstart) {
			this.kbbuf[this.kbpos] = b;
			this.kbpos = np;
		}
	}
	
	@Override
	public void updateEntity() {
		this.runblitter();
	}
	
	private void runblitter() {
		if (this.blitMode != 0) {
			this.markDirty();
			int w = this.blitW;
			int h = this.blitH;
			w = Math.min(w, 80 - this.blitXD);
			h = Math.min(h, 50 - this.blitYD);
			if (w >= 0 && h >= 0) {
				int doffs = this.blitYD * 80 + this.blitXD;
				int soffs;
				int j;
				switch (this.blitMode) {
					case 1:
						for (soffs = 0; soffs < h; ++soffs) {
							for (j = 0; j < w; ++j) {
								this.screen[doffs + 80 * soffs + j] = (byte) this.blitXS;
							}
						}
						
						this.blitMode = 0;
						return;
					case 2:
						for (soffs = 0; soffs < h; ++soffs) {
							for (j = 0; j < w; ++j) {
								this.screen[doffs + 80 * soffs + j] = (byte) (this.screen[doffs
										+ 80 * soffs + j] ^ 128);
							}
						}
						
						this.blitMode = 0;
						return;
					default:
						w = Math.min(w, 80 - this.blitXS);
						h = Math.min(h, 50 - this.blitYS);
						if (w >= 0 && h >= 0) {
							soffs = this.blitYS * 80 + this.blitXS;
							switch (this.blitMode) {
								case 3:
									for (j = 0; j < h; ++j) {
										for (int i = 0; i < w; ++i) {
											this.screen[doffs + 80 * j + i] = this.screen[soffs
													+ 80 * j + i];
										}
									}
									
									this.blitMode = 0;
									return;
								default:
							}
						} else {
							this.blitMode = 0;
						}
				}
			} else {
				this.blitMode = 0;
			}
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
		this.Rotation = tag.getByte("rot");
		this.screen = tag.getByteArray("fb");
		if (this.screen.length != 4000) {
			this.screen = new byte[4000];
		}
		
		this.memRow = tag.getByte("row") & 255;
		this.cursX = tag.getByte("cx") & 255;
		this.cursY = tag.getByte("cy") & 255;
		this.cursMode = tag.getByte("cm") & 255;
		this.kbstart = tag.getByte("kbs");
		this.kbpos = tag.getByte("kbp");
		this.kbbuf = tag.getByteArray("kbb");
		if (this.kbbuf.length != 16) {
			this.kbbuf = new byte[16];
		}
		
		this.blitXS = tag.getByte("blxs") & 255;
		this.blitYS = tag.getByte("blys") & 255;
		this.blitXD = tag.getByte("blxd") & 255;
		this.blitYD = tag.getByte("blyd") & 255;
		this.blitW = tag.getByte("blw") & 255;
		this.blitH = tag.getByte("blh") & 255;
		this.blitMode = tag.getByte("blmd");
		this.rbaddr = tag.getByte("rbaddr") & 255;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("rot", (byte) this.Rotation);
		tag.setByteArray("fb", this.screen);
		tag.setByte("row", (byte) this.memRow);
		tag.setByte("cx", (byte) this.cursX);
		tag.setByte("cy", (byte) this.cursY);
		tag.setByte("cm", (byte) this.cursMode);
		tag.setByte("kbs", (byte) this.kbstart);
		tag.setByte("kbp", (byte) this.kbpos);
		tag.setByteArray("kbb", this.kbbuf);
		tag.setByte("blxs", (byte) this.blitXS);
		tag.setByte("blys", (byte) this.blitYS);
		tag.setByte("blxd", (byte) this.blitXD);
		tag.setByte("blyd", (byte) this.blitYD);
		tag.setByte("blw", (byte) this.blitW);
		tag.setByte("blh", (byte) this.blitH);
		tag.setByte("blmd", (byte) this.blitMode);
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
