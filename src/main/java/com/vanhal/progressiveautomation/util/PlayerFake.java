package com.vanhal.progressiveautomation.util;


import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.vanhal.progressiveautomation.ProgressiveAutomation;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * This class was pulled from COFH Core
 * 
 * {@link} https://github.com/CoFH/CoFHCore/blob/master/src/main/java/cofh/entity/PlayerFake.java
 * @author COFH
 *
 */

public class PlayerFake extends EntityPlayerMP {

	private static GameProfile NAME = new GameProfile(UUID.fromString("08B9E87C-A9F9-5161-AEC6-B671C8F4FCB9"), "[VANHAL]");
	//private static GameProfile NAME = new GameProfile("08B9E87C-A9F9-5161-AEC6-B671C8F4FCB9", "[VANHAL]");

	public boolean isSneaking = false;
	public ItemStack previousItem = null;
	public String myName = "[VANHAL]";
	
	public PlayerFake(WorldServer world, String FakeName) {
		this(world, new GameProfile(UUID.randomUUID(), FakeName));
		myName = FakeName;
	}
	
	public PlayerFake(WorldServer world, GameProfile FakeName) {
		super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, FakeName, new ItemInWorldManager(world));
		this.addedToChunk = false;
		this.onGround = true;
	}

	public PlayerFake(WorldServer world) {
		this(world, NAME);
	}

	public static boolean isBlockBreakable(PlayerFake myFakePlayer, World worldObj, int x, int y, int z) {

		Block block = worldObj.getBlock(x, y, z);
		if (myFakePlayer == null) {
			return block.getBlockHardness(worldObj, x, y, z) > -1;
		} else {
			return block.getPlayerRelativeBlockHardness(myFakePlayer, worldObj, x, y, z) > -1;
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {

		return false;
	}
	
	@Override
	public boolean isSprinting() {
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {

		return null;
	}

	public void setItemInHand(ItemStack m_item) {

		this.inventory.currentItem = 0;
		this.inventory.setInventorySlotContents(0, m_item);
	}

	public void setItemInHand(int slot) {

		this.inventory.currentItem = slot;
	}

	@Override
	public double getDistanceSq(double x, double y, double z) {

		return 0F;
	}

	@Override
	public double getDistance(double x, double y, double z) {

		return 0F;
	}

	@Override
	public boolean isSneaking() {

		return isSneaking;
	}

	@Override
	public void onUpdate() {

		ItemStack itemstack = previousItem;
		ItemStack itemstack1 = getHeldItem();

		if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
			if (itemstack != null) {
				getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers());
			}
			if (itemstack1 != null) {
				getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers());
			}
			myName = "[VANHAL]" + (itemstack1 != null ? " using " + itemstack1.getDisplayName() : "");
		}
		previousItem = itemstack1 == null ? null : itemstack1.copy();
		theItemInWorldManager.updateBlockRemoving();
	}

	@Override
	protected void updateItemUse(ItemStack par1ItemStack, int par2) {

		if (par1ItemStack.getItemUseAction() == EnumAction.drink) {
			this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (par1ItemStack.getItemUseAction() == EnumAction.eat) {
			this.playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Override
	public String getDisplayName() {

		return getCommandSenderName();
	}

	@Override
	public float getEyeHeight() {

		return 1.1F;
	}

	@Override
	public ItemStack getCurrentArmor(int par1) {

		return new ItemStack(Items.diamond_chestplate);
	}

	@Override
	public void addChatMessage(IChatComponent chatmessagecomponent) {

	}

	@Override
	public void addChatComponentMessage(IChatComponent chatmessagecomponent) {

	}

	@Override
	public void addStat(StatBase par1StatBase, int par2) {

	}

	@Override
	public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {

	}

	@Override
	public boolean isEntityInvulnerable() {

		return true;
	}

	@Override
	public void onDeath(DamageSource source) {

		return;
	}

	@Override
	public void travelToDimension(int dim) {

		return;
	}

	@Override
	public void func_147100_a(C15PacketClientSettings pkt) {

		return;
	}
}
