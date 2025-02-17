package com.teamcqr.chocolatequestrepoured.objects.items.guns;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.init.ModSounds;
import com.teamcqr.chocolatequestrepoured.objects.entity.projectiles.ProjectileBullet;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRevolver extends Item implements IRangedWeapon{

	public ItemRevolver() {
		setMaxDamage(300);
		setMaxStackSize(1);
	}

	/*
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}
	*/

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.BLUE + "5.0 " + I18n.format("description.bullet_damage.name"));
		tooltip.add(TextFormatting.RED + "-30 " + I18n.format("description.fire_rate.name"));
		tooltip.add(TextFormatting.RED + "-50" + "% " + I18n.format("description.accuracy.name"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.gun.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		//System.out.println("Hand: " + handIn.toString());
		ItemStack stack = playerIn.getHeldItem(handIn);
		boolean flag = !findAmmo(playerIn).isEmpty();

		if (!playerIn.capabilities.isCreativeMode && !flag && getBulletStack(stack, playerIn) == ItemStack.EMPTY) {
			if (flag) {
				shoot(stack, worldIn, playerIn);
			}
			return flag ? new ActionResult(EnumActionResult.PASS, stack)
					: new ActionResult(EnumActionResult.FAIL, stack);
		}

		else {
			shoot(stack, worldIn, playerIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

	public void shoot(ItemStack stack, World worldIn, EntityPlayer player) {
		boolean flag = player.capabilities.isCreativeMode;
		ItemStack itemstack = findAmmo(player);

		if (!itemstack.isEmpty() || flag) {
			if (!worldIn.isRemote) {
				if (flag && itemstack.isEmpty()) {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, 1);
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 5F);
					player.getCooldownTracker().setCooldown(stack.getItem(), 10);
					worldIn.spawnEntity(bulletE);
				} else {
					ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, getBulletType(itemstack));
					bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 5F);
					player.getCooldownTracker().setCooldown(stack.getItem(), 10);
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
						bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 5F);
						player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
								10);
						worldIn.spawnEntity(bulletE);
					} else {
						ProjectileBullet bulletE = new ProjectileBullet(worldIn, player, getBulletType(itemstack));
						bulletE.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.5F, 5F);
						player.getCooldownTracker().setCooldown(player.getHeldItem(player.getActiveHand()).getItem(),
								10);
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

	protected boolean isBullet(ItemStack stack) {
		return stack.getItem() instanceof ItemBullet;
	}

	protected ItemStack findAmmo(EntityPlayer player) {
		if (isBullet(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (isBullet(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (isBullet(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected ItemStack getBulletStack(ItemStack stack, EntityPlayer player) {
		if (stack.getItem() == ModItems.BULLET_IRON) {
			return new ItemStack(ModItems.BULLET_IRON);
		}

		if (stack.getItem() == ModItems.BULLET_GOLD) {
			return new ItemStack(ModItems.BULLET_GOLD);
		}

		if (stack.getItem() == ModItems.BULLET_DIAMOND) {
			return new ItemStack(ModItems.BULLET_DIAMOND);
		}

		if (stack.getItem() == ModItems.BULLET_FIRE) {
			return new ItemStack(ModItems.BULLET_FIRE);
		} else {
			System.out.println("IT'S A BUG!!!! IF YOU SEE THIS REPORT IT TO MOD'S AUTHOR");
			return ItemStack.EMPTY; // #SHOULD NEVER HAPPEN
		}
	}

	protected int getBulletType(ItemStack stack) {
		if (stack.getItem() == ModItems.BULLET_IRON) {
			return 1;
		}

		if (stack.getItem() == ModItems.BULLET_GOLD) {
			return 2;
		}

		if (stack.getItem() == ModItems.BULLET_DIAMOND) {
			return 3;
		}

		if (stack.getItem() == ModItems.BULLET_FIRE) {
			return 4;
		}

		else {
			System.out.println("IT'S A BUG!!!! IF YOU SEE THIS REPORT IT TO MOD'S AUTHOR");
			return 0; // #SHOULD NEVER HAPPEN
		}
	}

}
