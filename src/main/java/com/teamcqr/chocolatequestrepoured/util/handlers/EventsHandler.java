package com.teamcqr.chocolatequestrepoured.util.handlers;

import java.util.Random;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.API.events.CQDungeonStructureGenerateEvent;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.network.ParticlesMessageToClient;
import com.teamcqr.chocolatequestrepoured.structuregen.lootchests.ELootTable;
import com.teamcqr.chocolatequestrepoured.structuregen.lootchests.LootTableLoader;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;
import com.teamcqr.chocolatequestrepoured.util.data.CQRDataFileManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class EventsHandler {

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		LootTable lootTable = event.getTable();
		ResourceLocation resLoc = event.getName();
		if (ELootTable.valueOf(resLoc) != null) {
			ELootTable table = ELootTable.valueOf(resLoc);
			// System.out.println("Loaded loottable is a cq one....");
			// System.out.println("Exchanging loot...");

			try {
				LootTableLoader.fillLootTable(table, lootTable);
				/*if(table.equals(ELootTable.CQ_EQUIPMENT)) {
					System.out.println(lootTable.toString());
				}*/
			} catch (Exception ex) {
				System.err.println("Unable to fill loot table " + event.getName());
				ex.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public static void onDefense(LivingAttackEvent event) {
		boolean tep = false;

		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			@SuppressWarnings("unused")
			float amount = event.getAmount();
			World world = player.world;

			if (player.getActiveItemStack().getItem() != ModItems.SHIELD_WALKER_KING
					|| player.getHeldItemMainhand().getItem() != ModItems.SWORD_WALKER
					|| player.getRidingEntity() != null || attacker == null) {
				return;
			}

			double d = attacker.posX + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;
			double d1 = attacker.posY;
			double d2 = attacker.posZ + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;

			@SuppressWarnings("unused")
			double d3 = player.posX;
			@SuppressWarnings("unused")
			double d4 = player.posY;
			@SuppressWarnings("unused")
			double d5 = player.posZ;

			int i = MathHelper.floor(d);
			int j = MathHelper.floor(d1);
			int k = MathHelper.floor(d2);

			BlockPos ep = new BlockPos(i, j, k);
			BlockPos ep1 = new BlockPos(i, j + 1, k);

			if (world.getCollisionBoxes(player, player.getEntityBoundingBox()).size() == 0
					&& !world.containsAnyLiquid(attacker.getEntityBoundingBox()) && player.isActiveItemStackBlocking()
					&& player.getDistanceSq(attacker) >= 25.0D) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep)
						&& world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					tep = true;
				} else {
					tep = false;
					ParticlesMessageToClient packet = new ParticlesMessageToClient(player.getPositionVector(), 12, 12);
					CQRMain.NETWORK.sendToAllAround(packet, packet.getTargetPoint(player));
				}
			}

			if (tep) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep)
						&& world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					if (player instanceof EntityPlayerMP) {
						EntityPlayerMP playerMP = (EntityPlayerMP) player;

						playerMP.connection.setPlayerLocation(d, d1, d2, playerMP.rotationYaw, playerMP.rotationPitch);
						ParticlesMessageToClient packet = new ParticlesMessageToClient(playerMP.getPositionVector(), 24,
								12);
						CQRMain.NETWORK.sendToAllAround(packet, packet.getTargetPoint(playerMP));
						world.playSound(null, d, d1, d2, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.MASTER,
								1.0F, 1.0F);
					}
					event.setCanceled(true);
					tep = false;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Random rand = new Random();
		Entity entity = event.getEntity();
		NBTTagCompound tag = entity.getEntityData();

		if (tag.hasKey("Items")) {
			NBTTagList itemList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);

			if (itemList == null) {
				return;
			}

			for (int i = 0; i < itemList.tagCount(); i++) {
				NBTTagCompound entry = itemList.getCompoundTagAt(i);
				ItemStack stack = new ItemStack(entry);

				if (stack != null) {
					if (!entity.world.isRemote) {
						entity.world.spawnEntity(new EntityItem(entity.world, entity.posX + rand.nextDouble(),
								entity.posY, entity.posZ + rand.nextDouble(), stack));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load e) {
		if (!e.getWorld().isRemote) {
			CQRDataFileManager.getInstance().handleWorldLoad(e.getWorld());
		}
	}
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save e) {
		if (!e.getWorld().isRemote) {
			CQRDataFileManager.getInstance().handleWorldSaving(e.getWorld());
		}
	}
	
	@SubscribeEvent
	public static void onDungeonGenerate(CQDungeonStructureGenerateEvent e) {
		if(!e.getWorld().isRemote) {
			CQRDataFileManager.getInstance().handleDungeonGeneration(e.getDungeon(), e.getPos());
		}
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload e) {
		if (!e.getWorld().isRemote) {
			CQRDataFileManager.getInstance().handleWorldUnload(e.getWorld());
			
			// Stop export threads
			if (!CQStructure.runningExportThreads.isEmpty()) {
				for (Thread t : CQStructure.runningExportThreads) {
					try {
						t.stop();
					} catch (Exception ex) {

					}
				}
				CQStructure.runningExportThreads.clear();
			}
		}
	}

}
