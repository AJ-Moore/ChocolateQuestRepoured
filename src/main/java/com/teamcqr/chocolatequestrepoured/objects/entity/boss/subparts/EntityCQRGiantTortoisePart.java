package com.teamcqr.chocolatequestrepoured.objects.entity.boss.subparts;

import com.teamcqr.chocolatequestrepoured.objects.entity.boss.EntityCQRGiantTortoise;

import net.minecraft.block.Block;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class EntityCQRGiantTortoisePart extends MultiPartEntityPart {

	private boolean isHead;
	
	public EntityCQRGiantTortoisePart(EntityCQRGiantTortoise parent, String partName, float width, float height, boolean isHead) {
		super(parent, partName, width, height);
		
		setSize(width, height);
		
		//setInvisible(true);
	}
	
	public EntityCQRGiantTortoise getParent() {
		return (EntityCQRGiantTortoise)parent;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(isHead) {
			amount *= 1.5F;
		}
		return getParent().attackEntityFromPart(this, source, amount);
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		++this.ticksExisted;
	}
	
	@Override
	public boolean isNonBoss() {
		return getParent().isNonBoss();
	}

	//As this is a part it does not make any noises
	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
	}
	
	@Override
	public void setRotation(float yaw, float pitch) {
		super.setRotation(yaw, pitch);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(getParent().isDead) {
			return false;
		}
		return getParent().processInitialInteract(player, hand);
	}

}
