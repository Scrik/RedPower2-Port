package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.EnvironLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IMultiblock;
import com.eloraam.redpower.core.MultiLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.ItemWindmill;
import com.eloraam.redpower.machine.TileMachine;
import com.eloraam.redpower.machine.TileWindTurbine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileWindTurbine extends TileMachine implements IInventory, IBluePowerConnectable, IMultiblock {
	
	BluePowerConductor cond = new BluePowerConductor() {
		@Override
		public TileEntity getParent() {
			return TileWindTurbine.this;
		}
		
		@Override
		public double getInvCap() {
			return 0.25D;
		}
	};
	private byte[] rayTrace = null;
	private int efficiency = 0;
	private int tracer = 0;
	public int windSpeed = 0;
	public int speed = 0;
	public int phase = 0;
	private int power = 0;
	private int propTicks = 0;
	public boolean hasBlades = false;
	public boolean hasBrakes = false;
	public byte windmillType = 0;
	protected ItemStack[] contents = new ItemStack[1];
	public int ConMask = -1;
	public int EConMask = -1;
	
	@Override
	public int getConnectableMask() {
		return 1073741823;
	}
	
	@Override
	public int getConnectClass(int side) {
		return 65;
	}
	
	@Override
	public int getCornerPowerMode() {
		return 2;
	}
	
	@Override
	public BluePowerConductor getBlueConductor(int side) {
		return this.cond;
	}
	
	@Override
	public void setPartRotation(int part, boolean sec, int rot) {
		this.teardownBlades();
		super.setPartRotation(part, sec, rot);
	}
	
	@Override
	public void onMultiRemoval(int subnum) {
		ItemStack ist = this.contents[0];
		if (ist != null && ist.stackSize > 0) {
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord + 1,
					super.zCoord, ist);
		}
		
		this.contents[0] = null;
		this.markDirty();
		this.teardownBlades();
	}
	
	@Override
	public AxisAlignedBB getMultiBounds(int subnum) {
		switch (this.windmillType) {
			case 1:
				return AxisAlignedBB.getBoundingBox(-2.5D, 1.3D, -2.5D, 3.5D,
						9.0D, 3.5D);
			case 2:
				WorldCoord wc = new WorldCoord(0, 0, 0);
				int dir2 = WorldCoord.getRightDir(super.Rotation);
				wc.step(super.Rotation ^ 1);
				WorldCoord wc2 = wc.coordStep(super.Rotation ^ 1);
				wc.step(dir2, 8);
				wc2.step(dir2, -8);
				return AxisAlignedBB.getBoundingBox(
						Math.min(wc.x, wc2.x) + 0.5D, -7.5D,
						Math.min(wc.z, wc2.z + 0.5D),
						Math.max(wc.x, wc2.x) + 0.5D, 8.5D,
						Math.max(wc.z, wc2.z) + 0.5D);
			default:
				return AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D,
						1.0D, 1.0D);
		}
	}
	
	@Override
	public float getMultiBlockStrength(int subnum, EntityPlayer player) {
		return 0.08F;
	}
	
	@Override
	public int getExtendedID() {
		return 9;
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockMachine;
	}
	
	public List<WorldCoord> getRelayBlockList(int wmt) {
		ArrayList<WorldCoord> tr = new ArrayList<WorldCoord>();
		int dir2 = WorldCoord.getRightDir(super.Rotation);
		int x;
		int y;
		switch (wmt) {
			case 1:
				for (x = -3; x <= 3; ++x) {
					for (y = -3; y <= 3; ++y) {
						for (int var7 = 1; var7 < 8; ++var7) {
							tr.add(new WorldCoord(x + super.xCoord, var7
									+ super.yCoord, y + super.zCoord));
						}
					}
				}
				
				return tr;
			case 2:
				for (x = -8; x <= 8; ++x) {
					for (y = -8; y <= 8; ++y) {
						WorldCoord nc = new WorldCoord(this);
						nc.step(super.Rotation ^ 1);
						nc.step(dir2, x);
						nc.y += y;
						tr.add(nc);
					}
				}
		}
		
		return tr;
	}
	
	private void teardownBlades() {
		this.hasBlades = false;
		this.efficiency = 0;
		this.speed = 0;
		this.rayTrace = null;
		this.updateBlock();
		List<WorldCoord> rbl = this.getRelayBlockList(this.windmillType);
		MultiLib.removeRelays(super.worldObj, new WorldCoord(this), rbl);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (CoreLib.isClient(super.worldObj)) {
			if (this.hasBrakes) {
				this.phase = (int) (this.phase + this.speed * 0.1D);
			} else {
				this.phase += this.speed;
			}
			
		} else {
			if (!this.isTickScheduled()) {
				this.scheduleTick(5);
			}
			
			if (this.ConMask < 0) {
				this.ConMask = RedPowerLib.getConnections(super.worldObj, this,
						super.xCoord, super.yCoord, super.zCoord);
				this.EConMask = RedPowerLib.getExtConnections(super.worldObj,
						this, super.xCoord, super.yCoord, super.zCoord);
				this.cond.recache(this.ConMask, this.EConMask);
			}
			
			this.cond.iterate();
			this.markDirty();
			if (this.hasBlades) {
				if (this.contents[0] == null
						|| !(this.contents[0].getItem() instanceof ItemWindmill)) {
					this.teardownBlades();
					return;
				}
				
				ItemWindmill iwm = (ItemWindmill) this.contents[0].getItem();
				if (iwm.windmillType != this.windmillType) {
					this.teardownBlades();
					return;
				}
				
				if (this.propTicks <= 0) {
					this.contents[0].setItemDamage(this.contents[0]
							.getItemDamage() + 1);
					if (this.contents[0].getItemDamage() > this.contents[0]
							.getMaxDamage()) {
						this.contents[0] = null;
						this.markDirty();
						this.teardownBlades();
						this.contents[0] = iwm.getBrokenItem();
						this.markDirty();
						return;
					}
					
					this.markDirty();
					this.propTicks += 6600;
				}
				
				if (this.hasBrakes) {
					return;
				}
				
				--this.propTicks;
				if (this.cond.getVoltage() > 130.0D) {
					return;
				}
				
				this.cond.applyPower(this.power / 5);
			}
			
		}
	}
	
	private void traceAir0() {
		int yh = super.yCoord + 1 + this.tracer / 28;
		int xp = this.tracer % 7;
		//boolean dir = false;
		WorldCoord tp;
		byte var6;
		switch (this.tracer / 7 % 4) {
			case 0:
				var6 = 2;
				tp = new WorldCoord(super.xCoord - 3 + xp, yh, super.zCoord - 4);
				break;
			case 1:
				var6 = 4;
				tp = new WorldCoord(super.xCoord - 4, yh, super.zCoord - 3 + xp);
				break;
			case 2:
				var6 = 3;
				tp = new WorldCoord(super.xCoord - 3 + xp, yh, super.zCoord + 4);
				break;
			default:
				var6 = 5;
				tp = new WorldCoord(super.xCoord + 4, yh, super.zCoord - 3 + xp);
		}
		
		int i;
		for (i = 0; i < 10 && super.worldObj.getBlock(tp.x, tp.y, tp.z) == Blocks.air; ++i) {
			tp.step(var6);
		}
		
		if (this.rayTrace == null) {
			this.rayTrace = new byte[224];
		}
		
		this.efficiency = this.efficiency - this.rayTrace[this.tracer] + i;
		this.rayTrace[this.tracer] = (byte) i;
		++this.tracer;
		if (this.tracer >= 224) {
			this.tracer = 0;
		}
		
	}
	
	private void traceAir1() {
		int yh = this.tracer / 17;
		int xp = this.tracer % 17;
		int dir2 = WorldCoord.getRightDir(super.Rotation);
		WorldCoord tp = new WorldCoord(this);
		tp.step(super.Rotation ^ 1, 2);
		tp.step(dir2, xp - 8);
		tp.y += yh;
		
		int i;
		for (i = 0; i < 20 && super.worldObj.getBlock(tp.x, tp.y, tp.z) == Blocks.air; ++i) {
			tp.step(super.Rotation ^ 1);
		}
		
		if (this.rayTrace == null) {
			this.rayTrace = new byte[289];
		}
		
		this.efficiency = this.efficiency - this.rayTrace[this.tracer] + i;
		this.rayTrace[this.tracer] = (byte) i;
		++this.tracer;
		if (this.tracer >= 289) {
			this.tracer = 0;
		}
		
	}
	
	public int getWindScaled(int i) {
		return Math.min(i, i * this.windSpeed / 13333);
	}
	
	private void tryDeployBlades() {
		ItemWindmill iwm = (ItemWindmill) this.contents[0].getItem();
		if (iwm.canFaceDirection(super.Rotation)) {
			List<WorldCoord> rbl = this.getRelayBlockList(iwm.windmillType);
			if (MultiLib.isClear(super.worldObj, new WorldCoord(this), rbl)) {
				this.windmillType = (byte) iwm.windmillType;
				this.hasBlades = true;
				MultiLib.addRelays(super.worldObj, new WorldCoord(this), 0, rbl);
				this.updateBlock();
			}
			
		}
	}
	
	@Override
	public void onTileTick() {
		if (!this.hasBlades && this.contents[0] != null
				&& this.contents[0].getItem() instanceof ItemWindmill) {
			this.tryDeployBlades();
		}
		
		if (!this.hasBrakes && this.cond.getVoltage() > 110.0D) {
			this.hasBrakes = true;
		} else if (this.hasBrakes && this.cond.getVoltage() < 100.0D) {
			this.hasBrakes = false;
		}
		
		this.windSpeed = (int) (10000.0D * EnvironLib.getWindSpeed(
				super.worldObj, new WorldCoord(this)));
		if (this.hasBlades) {
			switch (this.windmillType) {
				case 1:
					this.power = 2 * this.windSpeed * this.efficiency / 2240;
					this.speed = this.power * this.power / 20000;
					this.traceAir0();
					break;
				case 2:
					this.power = this.windSpeed * this.efficiency / 5780;
					this.speed = this.power * this.power / 5000;
					this.traceAir1();
			}
			
			this.updateBlock();
		}
		
		this.scheduleTick(20);
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.contents[i];
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack tr;
			if (this.contents[i].stackSize <= j) {
				tr = this.contents[i];
				this.contents[i] = null;
				this.markDirty();
				return tr;
			} else {
				tr = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize == 0) {
					this.contents[i] = null;
				}
				
				this.markDirty();
				return tr;
			}
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.contents[i] == null) {
			return null;
		} else {
			ItemStack ist = this.contents[i];
			this.contents[i] = null;
			return ist;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack ist) {
		this.contents[i] = ist;
		if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
			ist.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return "Wind Turbine";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.worldObj.getTileEntity(super.xCoord, super.yCoord,
				super.zCoord) != this ? false : player.getDistanceSq(
				super.xCoord + 0.5D, super.yCoord + 0.5D, super.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
		this.ConMask = -1;
		this.EConMask = -1;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (player.isSneaking()) {
			return false;
		} else if (CoreLib.isClient(super.worldObj)) {
			return true;
		} else {
			player.openGui(RedPowerMachine.instance, 15, super.worldObj,
					super.xCoord, super.yCoord, super.zCoord);
			return true;
		}
	}
	
	@Override
	public void onBlockRemoval() {
		super.onBlockRemoval();
		if (this.hasBlades) {
			this.teardownBlades();
		}
		
		ItemStack ist = this.contents[0];
		if (ist != null && ist.stackSize > 0) {
			CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord,
					super.zCoord, ist);
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	public double func_82115_m() {
		return 1048576.0D;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList items = tag.getTagList("Items", 10); //TODO:
		this.contents = new ItemStack[this.getSizeInventory()];
		
		for (int rt = 0; rt < items.tagCount(); ++rt) {
			NBTTagCompound i = (NBTTagCompound) items.getCompoundTagAt(rt);
			int j = i.getByte("Slot") & 255;
			if (j >= 0 && j < this.contents.length) {
				this.contents[j] = ItemStack.loadItemStackFromNBT(i);
			}
		}
		
		this.windmillType = tag.getByte("wmt");
		this.hasBlades = this.windmillType > 0;
		this.efficiency = 0;
		byte[] var6 = tag.getByteArray("rays");
		if (var6 != null) {
			switch (this.windmillType) {
				case 1:
					if (var6.length != 224) {
						var6 = null;
					}
					break;
				case 2:
					if (var6.length != 289) {
						var6 = null;
					}
					break;
				default:
					var6 = null;
			}
		}
		
		this.rayTrace = var6;
		if (var6 != null) {
			for (int var7 = 0; var7 < var6.length; ++var7) {
				this.efficiency += var6[var7];
			}
		}
		
		this.tracer = tag.getInteger("tracer");
		this.speed = tag.getInteger("speed");
		this.power = tag.getInteger("spdpwr");
		this.propTicks = tag.getInteger("proptick");
		this.cond.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				this.contents[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		tag.setTag("Items", items);
		if (!this.hasBlades) {
			this.windmillType = 0;
		}
		
		tag.setByte("wmt", this.windmillType);
		if (this.rayTrace != null) {
			tag.setByteArray("rays", this.rayTrace);
		}
		
		tag.setInteger("tracer", this.tracer);
		tag.setInteger("speed", this.speed);
		tag.setInteger("spdpwr", this.power);
		tag.setInteger("proptick", this.propTicks);
		this.cond.writeToNBT(tag);
	}
	
	@Override
	protected void readFromPacket(ByteBuf buffer) {
		super.readFromPacket(buffer);
		int ps = buffer.readInt();
		this.hasBlades = (ps & 1) > 0;
		this.hasBrakes = (ps & 2) > 0;
		this.windmillType = buffer.readByte();
		this.speed = buffer.readInt();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void writeToPacket(ArrayList data) {
		super.writeToPacket(data);
		int ps = (this.hasBlades ? 1 : 0) | (this.hasBrakes ? 2 : 0);
		data.add(ps);
		data.add(this.windmillType);
		data.add(this.speed);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		return itemStack.getItem() == RedPowerMachine.itemWoodWindmill || itemStack.getItem() == RedPowerMachine.itemWoodTurbine;
	}
}
