package com.teamcqr.chocolatequestrepoured.objects.items.guns;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.teamcqr.chocolatequestrepoured.init.ModSounds;
import com.teamcqr.chocolatequestrepoured.objects.entity.projectiles.ProjectileBullet;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMusket extends ItemRevolver implements IRangedWeapon{

	public ItemMusket() {
		setMaxDamage(300);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.BLUE + "7.5 " + I18n.format("description.bullet_damage.name"));
		tooltip.add(TextFormatting.RED + "-60 " + I18n.format("description.fire_rate.name"));
		tooltip.add(TextFormatting.RED + "-10" + "% " + I18n.format("description.accuracy.name"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.gun.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@Override
	public void shoot(ItemStack stack, World worldIn, EntityPlayer player) {
		boolean flag = player.capabilities.isCreativeMode;
		ItemStack itemstack = findAmmo(player);

		if (!itemstack.isEmpty() || flag) {
			if (!worldIn.isRemote) {
				if (flag && itemstack.isEmpty()) {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, 1);
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
					player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(), 30);
					worldIn.spawnEntity(bulletE);
				} else {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, getBulletType(itemstack));
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
					player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(), 30);
					worldIn.spawnEntity(bulletE);
					stack.damageItem(1, player);
				}
			}

			worldIn.playSound(player.posX, player.posY, player.posZ, ModSounds.GUN_SHOOT, SoundCategory.MASTER,
					1.0F, 1.0F, false);
			player.rotationPitch -= worldIn.rand.nextFloat() * 10;

			if (!flag) {
				itemstack.shrink(1);

				if (itemstack.isEmpty()) {
					player.inventory.deleteStack(itemstack);
				}
			}
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) 
	{
		if (!worldIn.isRemote) {
			if (entityIn instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entityIn;

				if (player.getHeldItemMainhand() == stack)
				{
					if (!player.getHeldItemOffhand().isEmpty()) {
						if (!player.inventory.addItemStackToInventory(player.getHeldItemOffhand())) {
							player.entityDropItem(player.getHeldItemOffhand(), 0F);
						}

						if (!player.capabilities.isCreativeMode) {
							player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	/*
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			boolean flag = player.capabilities.isCreativeMode;
			ItemStack itemstack = findAmmo(player);

			if (!itemstack.isEmpty() || flag) {
				if (!worldIn.isRemote) {
					if (flag && itemstack.isEmpty()) {
						ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, 1);
						bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
						player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
								30);
						worldIn.spawnEntity(bulletE);
					} else {
						ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, getBulletType(itemstack));
						bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 2F);
						player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
								30);
						worldIn.spawnEntity(bulletE);
						stack.damageItem(1, player);
					}
				}

				worldIn.playSound(player.posX, player.posY, player.posZ, SoundsHandler.GUN_SHOOT, SoundCategory.MASTER,
						1.0F, 1.0F, false);
				entityLiving.rotationPitch -= worldIn.rand.nextFloat() * 10;

				if (!flag) {
					itemstack.shrink(1);

					if (itemstack.isEmpty()) {
						player.inventory.deleteStack(itemstack);
					}
				}
			}
		}
	}
	*/

}
