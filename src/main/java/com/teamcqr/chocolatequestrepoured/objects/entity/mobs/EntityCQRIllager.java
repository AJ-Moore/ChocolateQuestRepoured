package com.teamcqr.chocolatequestrepoured.objects.entity.mobs;

import com.teamcqr.chocolatequestrepoured.factions.EFaction;
import com.teamcqr.chocolatequestrepoured.objects.entity.EBaseHealths;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityCQRIllager extends AbstractEntityCQR {

	public EntityCQRIllager(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {

	}

	@Override
	public float getBaseHealth() {
		return EBaseHealths.ILLAGER.getValue();
	}

	@Override
	public EFaction getFaction() {
		return EFaction.BEASTS;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableList.ENTITIES_VINDICATION_ILLAGER;
	}
	
	@Override
	public int getTextureCount() {
		return 1;
	}
	
	@Override
	public boolean canRide() {
		return true;
	}

}
