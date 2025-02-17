package com.teamcqr.chocolatequestrepoured.factions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.teamcqr.chocolatequestrepoured.factions.EReputationState.EReputationStateRough;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.util.Constants;

public enum EFaction {
	
	UNDEAD(new String[] {"WALKERS", "VILLAGERS", "PLAYERS", "TRITONS"}, new String[] {"GOBLIN", "ENDERMEN"}, EReputationState.ENEMY),
	PIRATE(new String[] {"WALKERS", "VILLAGERS", "INQUISITION", "PLAYERS", "TRITONS"}, new String[] {"ILLAGERS"}, EReputationState.ENEMY),
	WALKERS(new String[] {"UNDEAD", "PIRATE", "DWARVES_AND_GOLEMS", "GOBLINS", "ENDERMEN", "PLAYERS", "OGRES_AND_GREMLINS", "INQUISITION", "ILLAGERS", "VILLAGERS", "NPC"}, new String[] {}, EReputationState.ARCH_ENEMY),
	DWARVES_AND_GOLEMS(new String[] {"WALKERS", "ENDERMEN", "ILLAGERS", "UNDEAD"}, new String[] {"VILLAGERS", "NPC", "INQUISITION"}, EReputationState.ACCEPTED),
	GOBLINS(new String[] {"OGRES_AND_GREMLINS", "WALKERS", "VILLAGERS", "INQUISITION", "PLAYERS"}, new String[] {"ENDERMEN", "ILLAGERS"}, EReputationState.ENEMY),
	ENDERMEN(new String[] {"WALKERS", "PLAYERS", "DWARVES_AND_GOLEMS", "VILLAGERS", "NPCS", "PIRATE", "TRITONS"}, new String[] {"ILLAGERS", "UNDEAD"}, EReputationState.NEUTRAL),
	//OGRES_AND_GREMLINS(new String[] {}, new String[] {}, EReputationState.NEUTRAL),
	INQUISITION(new String[] {"WALKERS", "ILLAGERS", "UNDEAD", "GOBLINS"}, new String[] {"DWARVES_AND_GOLEMS", "NPC", "VILLAGERS"}, EReputationState.NEUTRAL),
	BEASTS(new String[] {"WALKERS", "PLAYERS", "VILLAGERS", "NPC", "TRITONS", "UNDEAD"}, new String[] {"ENDERMEN", "PIRATE"}, EReputationState.NEUTRAL),
	VILLAGERS(new String[] {"WALKERS", "UNDEAD", "ILLAGERS"}, new String[] {"NPC", "TRITONS", "PLAYERS"}, EReputationState.NEUTRAL),
	NEUTRAL(new String[] {}, new String[] {}, EReputationState.NEUTRAL),
	TRITONS(new String[] {"WALKERS", "UNDEAD", "PIRATE", "ENDERMEN"}, new String[] {"NPC", "VILLAGERS"}, EReputationState.NEUTRAL),
	PLAYERS(new String[] {}, new String[] {"VILLAGERS", "NPC"}, EReputationState.NEUTRAL),
	;

	public static final int REPU_DECREMENT_ON_MEMBER_KILL = 5;
	public static final int REPU_DECREMENT_ON_ENEMY_KILL = 1;
	public static final int REPU_DECREMENT_ON_ALLY_KILL = 2;
	
	public static final int LOWEST_REPU = EReputationState.ARCH_ENEMY.getValue();
	public static final int HIGHEST_REPU = EReputationState.MEMBER.getValue(); 
	
	private String[] enemies;
	private String[] allies;
	private EReputationState defaultRepu;
	private Map<UUID, Integer> reputationMap = new HashMap<>(); 
	
	private EFaction(String[] enemies, String[] allies, EReputationState startState) {
		this.enemies = enemies;
		this.allies = allies;
		this.defaultRepu = startState;
	}
	
	public EReputationState getDefaultReputation() {
		return this.defaultRepu;
	}
	
	public boolean isEnemy(EFaction otherFac) {
		if(otherFac == this) {
			return false;
		}
		if(otherFac != null) {
			for(String str : this.enemies) {
				if(otherFac.toString().toUpperCase().equals(str)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isAlly(EFaction otherFac) {
		if(otherFac == this) {
			return true;
		}
		if(otherFac != null) {
			for(String str : this.allies) {
				if(otherFac.toString().toUpperCase().equals(str)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//DONE: Methods to check wether a faction is an ally or an enemy
	public EReputationStateRough getRelation(AbstractEntityCQR e1, AbstractEntityCQR e2) {
		
		EFaction e1Fac = e1.getFaction();
		EFaction e2Fac = e2.getFaction();
		
		if(e1Fac.isAlly(e2Fac) || e2Fac.isAlly(e1Fac)) {
			return EReputationStateRough.ALLY;
		}
		
		if(e1Fac.isEnemy(e2Fac) || e2Fac.isEnemy(e1Fac)) {
			return EReputationStateRough.ENEMY;
		}
		
		return EReputationStateRough.NEUTRAL;
	}
	
	public EReputationState getFineReputation(UUID uuid) {
		return EReputationState.getByInt(getReputation(uuid));
	}
	
	public boolean isEntityEnemy(Entity entity) {
		if(entity == null) {
			return false;
		}
		if(entity.getEntityWorld().getDifficulty().equals(EnumDifficulty.PEACEFUL)) {
			return false;
		}
		if(getFactionOfEntity(entity) != null) {
			if(getFactionOfEntity(entity) == PLAYERS) {
				//System.out.println("Repu Rough: " + EReputationStateRough.getByRepuScore(getReputation(entity.getPersistentID())));
				return EReputationStateRough.getByRepuScore(getReputation(entity.getPersistentID())).equals(EReputationStateRough.ENEMY);
			}
			return isEnemy(getFactionOfEntity(entity)) || getFactionOfEntity(entity).isEnemy(this);
		}
		return false;
	}
	
	public boolean isEntityAlly(Entity entity) {
		if(entity == null) {
			return false;
		}
		if(getFactionOfEntity(entity) != null) {
			if(entity instanceof EntityPlayer) {
				return EReputationStateRough.getByRepuScore(getReputation(entity.getPersistentID())).equals(EReputationStateRough.ALLY);
			}
			return isAlly(getFactionOfEntity(entity)) || getFactionOfEntity(entity).isAlly(this);
		}
		return false;
	}
	
	public static EFaction getFactionOfEntity(Entity entity) {
		if(entity instanceof EntityTameable) {
			return getFactionOfEntity(((EntityTameable)entity).getOwner());
		}
		
		if(entity instanceof AbstractEntityCQR) {
			return ((AbstractEntityCQR)entity).getFaction();
		}
		
		if(entity instanceof EntityVillager || entity instanceof EntityGolem) {
			return VILLAGERS;
		}
		if(entity instanceof EntityIllusionIllager || entity instanceof EntityVex || entity instanceof EntityVindicator || entity instanceof EntityEvoker) {
			return BEASTS;
		}
		
		if(entity instanceof EntityEnderman || entity instanceof EntityEndermite || entity instanceof EntityDragon) {
			return ENDERMEN;
		}
		
		if(entity instanceof EntityAnimal) {
			return NEUTRAL;
		}
		
		if(entity instanceof EntityMob) {
			return UNDEAD;
		}
		
		if(entity instanceof EntityWaterMob) {
			return TRITONS;
		}
		
		if(entity instanceof EntityPlayer) {
			return PLAYERS;
		}
		
		return null;
		
	}
	
	public void decrementReputation(EntityPlayer player, int amount) {
		if(canDecrementRepu(player)) {
			this.reputationMap.put(player.getPersistentID(), getReputation(player.getPersistentID()) -amount);
			//System.out.println("Repu of: " + player.getDisplayNameString() + " towards " + this.name() + " is: " + getReputation(player.getPersistentID()));
		}
	}
	
	private boolean canDecrementRepu(EntityPlayer player) {
		if(getReputation(player.getPersistentID()) < LOWEST_REPU) {
			return false;
		}
		return canRepuChange(player);
	}
	
	private boolean canRepuChange(EntityPlayer player) {
		return !(player.getEntityWorld().getDifficulty().equals(EnumDifficulty.PEACEFUL) || player.isCreative() || player.isSpectator());
	}

	private int getReputation(UUID persistentID) {
		if(this.reputationMap.containsKey(persistentID)) {
			return reputationMap.get(persistentID);
		}
		return defaultRepu.getValue();
	}

	public void incrementReputation(EntityPlayer player, int amount) {
		if(canIncrementRepu(player)) {
			this.reputationMap.put(player.getPersistentID(), getReputation(player.getPersistentID()) +amount);
			//System.out.println("Repu of: " + player.getDisplayNameString() + " towards " + this.name() + " is: " + getReputation(player.getPersistentID()));
		}
	}

	private boolean canIncrementRepu(EntityPlayer player) {
		if(getReputation(player.getPersistentID()) > HIGHEST_REPU) {
			return false;
		}
		return canRepuChange(player);
	}
	
	public static void loadFromNBT(NBTTagCompound compound) {
		if(compound.hasKey("Factions")) {
			NBTTagList factions = compound.getTagList("Factions", Constants.NBT.TAG_COMPOUND);
			factions.forEach(new Consumer<NBTBase>() {

				@Override
				public void accept(NBTBase t) {
					try {
						NBTTagCompound factionTag = (NBTTagCompound)t;
						EFaction faction = EFaction.valueOf(factionTag.getString("Name"));
						
						NBTTagList repuListTag = factionTag.getTagList("Reputations", Constants.NBT.TAG_COMPOUND);
						repuListTag.forEach(new Consumer<NBTBase>() {

							@Override
							public void accept(NBTBase t) {
								NBTTagCompound repuTag = (NBTTagCompound)t;
								faction.reputationMap.put(repuTag.getUniqueId("UUID"), repuTag.getInteger("Reputation"));
							}
						});
						
					} catch(Exception ex) {
						System.err.println("Unable to load faction data!");
						ex.printStackTrace();
					}
				}
				
			});
		}
		
	}
	
	public static NBTTagCompound saveDataAsNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList factions = new NBTTagList();
		
		for(EFaction faction : values()) {
			NBTTagList repus = new NBTTagList();
			for(Map.Entry<UUID, Integer> entry : faction.reputationMap.entrySet()) {
				NBTTagCompound repuTag = new NBTTagCompound();
				repuTag.setUniqueId("UUID", entry.getKey());
				repuTag.setInteger("Reputation", entry.getValue());
				
				repus.appendTag(repuTag);
			}
			NBTTagCompound factionTag = new NBTTagCompound();
			factionTag.setString("Name", faction.name());
			factionTag.setTag("Reputations", repus);
			
			factions.appendTag(factionTag);
		}
		compound.setTag("Factions", factions);
		//System.out.println("Faction data saved!");
		return compound;
	}

}
