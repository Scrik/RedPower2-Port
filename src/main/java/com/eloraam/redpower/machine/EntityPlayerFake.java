package com.eloraam.redpower.machine;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPlayerFake extends EntityPlayer {
	
	public EntityPlayerFake(World world) {
		super(world, new GameProfile(UUID.randomUUID(), ""));
		
		for (int i = 9; i < 36; ++i) {
			super.inventory.setInventorySlotContents(i, new ItemStack(Items.stick));
		}
	}
	
	@Override
	public void mountEntity(Entity entity) {
	}
	
	@Override
	public boolean canCommandSenderUseCommand(int p, String str) {
		return false;
	}
	
	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(super.posX),
				MathHelper.floor_double(super.posY + 0.5D),
				MathHelper.floor_double(super.posZ));
	}

	@Override
	public void addChatMessage(IChatComponent chatComponent) {
	}
}
