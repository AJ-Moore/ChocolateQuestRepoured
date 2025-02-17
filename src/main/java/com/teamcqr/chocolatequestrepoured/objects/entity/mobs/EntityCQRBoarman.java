package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EFaction;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.ELootTablesNormal;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityCQRBoarman extends AbstractEntityCQR {
	
	public EntityCQRBoarman(World worldIn) {
		super(worldIn);
		this.setSize(0.95F, 2.0F);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.GREAT_SWORD_IRON));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.PIGMAN.getValue();
	}

	@Override
	public EFaction getFaction() {
		return EFaction.UNDEAD;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ZOMBIE_PIG_HURT;
	}
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_PIG_AMBIENT;
	}
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_PIG_DEATH;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return ELootTablesNormal.ENTITY_BOARMAN.getLootTable();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.isFireDamage()) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public int getTextureCount() {
		return 3;
	}
	
	@Override
	public boolean canRide() {
		return true;
	}
}
