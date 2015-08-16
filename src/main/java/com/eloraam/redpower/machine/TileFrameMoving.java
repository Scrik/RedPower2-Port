package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IHandlePackets;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;
import com.eloraam.redpower.core.WorldCoord;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileMotor;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class TileFrameMoving extends TileMultipart implements IFrameLink, IHandlePackets {
	
	TileFrameMoving.FrameBlockAccess frameblock = new TileFrameMoving.FrameBlockAccess();
	public int motorX;
	public int motorY;
	public int motorZ;
	public Block movingBlockID = Blocks.air;
	public int movingBlockMeta = 0;
	public boolean movingCrate = false;
	public TileEntity movingTileEntity = null;
	public byte lastMovePos = 0;
	
	@Override
	public boolean isFrameMoving() {
		return true;
	}
	
	@Override
	public boolean canFrameConnectIn(int dir) {
		return true;
	}
	
	@Override
	public boolean canFrameConnectOut(int dir) {
		return true;
	}
	
	@Override
	public WorldCoord getFrameLinkset() {
		return new WorldCoord(this.motorX, this.motorY, this.motorZ);
	}
	
	@Override
	public int getExtendedID() {
		return 1;
	}
	
	@Override
	public void onBlockNeighborChange(Block bl) {
	}
	
	@Override
	public Block getBlockType() {
		return RedPowerMachine.blockFrame;
	}
	
	@Override
	public int getPartsMask() {
		return this.movingBlockID == Blocks.air ? 0 : 536870912;
	}
	
	@Override
	public int getSolidPartsMask() {
		return this.movingBlockID == Blocks.air ? 0 : 536870912;
	}
	
	@Override
	public boolean blockEmpty() {
		return false;
	}
	
	@Override
	public void onHarvestPart(EntityPlayer player, int part) {
	}
	
	@Override
	public void addHarvestContents(List<ItemStack> ist) {
		super.addHarvestContents(ist);
	}
	
	@Override
	public float getPartStrength(EntityPlayer player, int part) {
		//BlockMachine bl = RedPowerMachine.blockMachine;
		return 0.0F;
	}
	
	@Override
	public void setPartBounds(BlockMultipart bl, int part) {
		TileMotor tm = (TileMotor) CoreLib.getTileEntity(super.worldObj,
				this.motorX, this.motorY, this.motorZ, TileMotor.class);
		if (tm != null) {
			float ofs = tm.getMoveScaled();
			switch (tm.MoveDir) {
				case 0:
					bl.setBlockBounds(0.0F, 0.0F - ofs, 0.0F, 1.0F, 1.0F - ofs,
							1.0F);
					break;
				case 1:
					bl.setBlockBounds(0.0F, 0.0F + ofs, 0.0F, 1.0F, 1.0F + ofs,
							1.0F);
					break;
				case 2:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F - ofs, 1.0F, 1.0F,
							1.0F - ofs);
					break;
				case 3:
					bl.setBlockBounds(0.0F, 0.0F, 0.0F + ofs, 1.0F, 1.0F,
							1.0F + ofs);
					break;
				case 4:
					bl.setBlockBounds(0.0F - ofs, 0.0F, 0.0F, 1.0F - ofs, 1.0F,
							1.0F);
					break;
				case 5:
					bl.setBlockBounds(0.0F + ofs, 0.0F, 0.0F, 1.0F + ofs, 1.0F,
							1.0F);
			}
			
		}
	}
	
	public IBlockAccess getFrameBlockAccess() {
		return this.frameblock;
	}
	
	public void setContents(Block bid, int md, int mx, int my, int mz,
			TileEntity bte) {
		this.movingBlockID = bid;
		this.movingBlockMeta = md;
		this.motorX = mx;
		this.motorY = my;
		this.motorZ = mz;
		this.movingTileEntity = bte;
		if (this.movingTileEntity != null) {
			if (RedPowerMachine.FrameAlwaysCrate) {
				this.movingCrate = true;
			}
			
			if (!(this.movingTileEntity instanceof IFrameSupport)) {
				this.movingCrate = true;
			}
		}
		
	}
	
	public void doRefresh(IBlockAccess iba) {
		if (this.movingTileEntity instanceof IFrameSupport) {
			IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
			ifs.onFrameRefresh(iba);
		}
	}
	
	public void dropBlock() {
		super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, this.movingBlockID, this.movingBlockMeta, 3);
		if (this.movingTileEntity != null) {
			this.movingTileEntity.xCoord = super.xCoord;
			this.movingTileEntity.yCoord = super.yCoord;
			this.movingTileEntity.zCoord = super.zCoord;
			this.movingTileEntity.validate();
			super.worldObj.setTileEntity(super.xCoord, super.yCoord, super.zCoord, this.movingTileEntity);
		}
		
		super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
		CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
		RedPowerLib.updateIndirectNeighbors(super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.movingBlockID);
	}
	
	private AxisAlignedBB getAABB(int dir, float dist) {
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(super.xCoord,
				super.yCoord, super.zCoord, super.xCoord + 1, super.yCoord + 1,
				super.zCoord + 1);
		switch (dir) {
			case 0:
				aabb.minY -= dist;
				aabb.maxY -= dist;
				break;
			case 1:
				aabb.minY += dist;
				aabb.maxY += dist;
				break;
			case 2:
				aabb.minZ -= dist;
				aabb.maxZ -= dist;
				break;
			case 3:
				aabb.minZ += dist;
				aabb.maxZ += dist;
				break;
			case 4:
				aabb.minX -= dist;
				aabb.maxX -= dist;
				break;
			case 5:
				aabb.minX += dist;
				aabb.maxX += dist;
		}
		
		return aabb;
	}
	
	@SuppressWarnings("unchecked")
	void pushEntities(TileMotor tm) {
		float f1 = this.lastMovePos / 16.0F;
		float f2 = tm.MovePos / 16.0F;
		this.lastMovePos = (byte) tm.MovePos;
		float xm = 0.0F;
		float ym = 0.0F;
		float zm = 0.0F;
		switch (tm.MoveDir) {
			case 0:
				ym -= f2 - f1;
				break;
			case 1:
				ym += f2 - f1;
				break;
			case 2:
				zm -= f2 - f1;
				break;
			case 3:
				zm += f2 - f1;
				break;
			case 4:
				xm -= f2 - f1;
				break;
			case 5:
				xm += f2 - f1;
		}
		
		AxisAlignedBB aabb = this.getAABB(tm.MoveDir, f2);
		List<Entity> li = super.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, aabb);
		ArrayList<Entity> li2 = new ArrayList<Entity>();
		li2.addAll(li);
		Iterator<Entity> i$ = li2.iterator();
		
		while (i$.hasNext()) {
			Entity ent = i$.next();
			ent.moveEntity(xm, ym, zm);
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		TileMotor tm = (TileMotor) CoreLib.getTileEntity(super.worldObj,
				this.motorX, this.motorY, this.motorZ, TileMotor.class);
		if (tm != null && tm.MovePos >= 0) {
			this.pushEntities(tm);
		} else if (!CoreLib.isClient(super.worldObj)) {
			this.dropBlock();
		}
	}
	
	@Override
	public void validate() {
		super.validate();
		if (this.movingTileEntity != null) {
			this.movingTileEntity.setWorldObj(super.worldObj);
		}
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.motorX = tag.getInteger("mx");
		this.motorY = tag.getInteger("my");
		this.motorZ = tag.getInteger("mz");
		this.movingBlockID = Block.getBlockById(tag.getInteger("mbid")); //TODO: Replace this in 1.8
		this.movingBlockMeta = tag.getInteger("mbmd");
		this.lastMovePos = tag.getByte("lmp");
		if (tag.hasKey("mte")) {
			NBTTagCompound mte = tag.getCompoundTag("mte");
			this.movingTileEntity = TileEntity.createAndLoadEntity(mte);
		} else {
			this.movingTileEntity = null;
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("mx", this.motorX);
		tag.setInteger("my", this.motorY);
		tag.setInteger("mz", this.motorZ);
		tag.setInteger("mbid", Block.getIdFromBlock(this.movingBlockID)); //TODO: Replace this in 1.8
		tag.setInteger("mbmd", this.movingBlockMeta);
		tag.setByte("lmp", this.lastMovePos);
		if (this.movingTileEntity != null) {
			NBTTagCompound mte = new NBTTagCompound();
			this.movingTileEntity.writeToNBT(mte);
			tag.setTag("mte", mte);
		}
		
	}
	
	protected void readFromPacket(ByteBuf buffer) {
		this.motorX = buffer.readInt();
		this.motorY = buffer.readInt();
		this.motorZ = buffer.readInt();
		this.movingBlockID = Block.getBlockById(buffer.readInt()); //TODO: Replace this in 1.8
		this.movingBlockMeta = buffer.readInt();
		if (this.movingBlockID != Blocks.air) {
			this.movingTileEntity = this.movingBlockID.createTileEntity(super.worldObj, this.movingBlockMeta);
			if (this.movingTileEntity != null) {
				if (!(this.movingTileEntity instanceof IFrameSupport)) {
					this.movingCrate = true;
					return;
				}
				
				this.movingTileEntity.setWorldObj(super.worldObj);
				this.movingTileEntity.xCoord = super.xCoord;
				this.movingTileEntity.yCoord = super.yCoord;
				this.movingTileEntity.zCoord = super.zCoord;
				IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
				ifs.handleFramePacket(buffer); //TODO: Needs to work...
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void writeToPacket(ArrayList data) {
		data.add(this.motorX);
		data.add(this.motorY);
		data.add(this.motorZ);
		data.add(Block.getIdFromBlock(this.movingBlockID)); //TODO: Replace this in 1.8
		data.add(this.movingBlockMeta);
		if (this.movingTileEntity instanceof IFrameSupport) {
			IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
			data.add(ifs.getFramePacket());
		} else {
			data.add(new ArrayList());
		}
		
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
	
	private class FrameBlockAccess implements IBlockAccess {
		
		//private final Vec3Pool vecpool = new Vec3Pool(300, 2000);
		
		private TileFrameMoving getFrame(int i, int j, int k) {
			TileFrameMoving tfm = (TileFrameMoving) CoreLib.getTileEntity(
					TileFrameMoving.super.worldObj, i, j, k,
					TileFrameMoving.class);
			return tfm == null ? null : (tfm.motorX == TileFrameMoving.this.motorX
					&& tfm.motorY == TileFrameMoving.this.motorY
					&& tfm.motorZ == tfm.motorZ ? tfm : null);
		}
		
		@Override
		public Block getBlock(int i, int j, int k) {
			TileFrameMoving tfm = this.getFrame(i, j, k);
			return tfm == null ? Blocks.air : tfm.movingBlockID;
		}
		
		@Override
		public TileEntity getTileEntity(int i, int j, int k) {
			TileFrameMoving tfm = this.getFrame(i, j, k);
			return tfm == null ? null : tfm.movingTileEntity;
		}
		
		@Override
		public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
			return TileFrameMoving.super.worldObj.getLightBrightnessForSkyBlocks(i, j, k, l);
		}
		
		/*
		public float getBrightness(int i, int j, int k, int l) {
			return TileFrameMoving.super.worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, i, j, k, l); //TODO: I think this is very very bad thing
		}*/
		
		/*public float getLightBrightness(int i, int j, int k) {
			return TileFrameMoving.super.worldObj.getLightBrightness(i, j, k);
		}*/
		
		@Override
		public int getBlockMetadata(int i, int j, int k) {
			TileFrameMoving tfm = this.getFrame(i, j, k);
			return tfm == null ? 0 : tfm.movingBlockMeta;
		}
		
		/*public Material getBlockMaterial(int i, int j, int k) {
			Block l = this.getBlock(i, j, k);
			return l == Blocks.air ? Material.air : l.getMaterial();
		}*/
		
		/*public boolean isBlockOpaqueCube(int i, int j, int k) {
			Block block = this.getBlock(i, j, k);
			return block == null ? false : block.isOpaqueCube();
		}*/
		
		/*public boolean isBlockNormalCube(int i, int j, int k) {
			Block block = this.getBlock(i, j, k);
			return block == null ? false : block.renderAsNormalBlock();//(
					//TileFrameMoving.super.worldObj, i, j, k);
		}*/
		
		@Override
		public boolean isAirBlock(int i, int j, int k) {
			Block bid = this.getBlock(i, j, k);
			return bid == Blocks.air ? true : bid.isAir(TileFrameMoving.super.worldObj, i, j, k);
		}
		
		@Override
		public int getHeight() {
			return TileFrameMoving.super.worldObj.getHeight();
		}
		
		/*public boolean doesBlockHaveSolidTopSurface(int x, int y, int z) {
			return TileFrameMoving.super.worldObj.getBlock(x, y, z).isSideSolid(TileFrameMoving.super.worldObj, x, y, z, ForgeDirection.UP);
		}*/
		
		@Override
		public boolean extendedLevelsInChunkCache() {
			return false;
		}
		
		@Override
		public BiomeGenBase getBiomeGenForCoords(int a, int b) {
			return TileFrameMoving.super.worldObj.getBiomeGenForCoords(a, b);
		}
		
		@Override
		public int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4) {
			return 0;
		}/*
		
		public Vec3Pool getWorldVec3Pool() {
			return this.vecpool;
		}*/
		

		@Override
		public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
			/*if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
	        {
	            return _default;
	        }

	        Chunk chunk = this.chunkProvider.provideChunk(x >> 4, z >> 4);
	        if (chunk == null || chunk.isEmpty())
	        {
	            return _default;
	        }
	        return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
			*/return TileFrameMoving.super.worldObj.getBlock(x, y, z).isSideSolid(TileFrameMoving.super.worldObj, x, y, z, side);
		}
	}
}
