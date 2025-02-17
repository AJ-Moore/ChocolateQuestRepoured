package com.teamcqr.chocolatequestrepoured.structuregen.dungeons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import com.teamcqr.chocolatequestrepoured.structuregen.DungeonBase;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.CastleGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Copyright (c) 25.05.2019
 * Developed by KalgogSmash
 * GitHub: https://github.com/KalgogSmash
 */
public class CastleDungeon extends DungeonBase {
	private int maxSize = 10;
	private int roomSize = 10;
	private int floorHeight = 8;
	private Random random;
	private Block wallBlock = Blocks.STONE;
	private Block floorBlock = Blocks.WHITE_GLAZED_TERRACOTTA;

	private String roomMobName = "minecraft:ghast";
	private String bossMobName = "minecraft:wither_boss";

	@Override
	public IDungeonGenerator getGenerator() {
		return new CastleGenerator(this);
	}


	public CastleDungeon(File configFile) {
		super(configFile);
		boolean success = true;
		Properties prop = loadConfig(configFile);
		if(prop != null)
		{
			this.maxSize = PropertyFileHelper.getIntProperty(prop, "maxSize", 60);
			this.roomSize = PropertyFileHelper.getIntProperty(prop, "roomSize", 10);
			this.floorHeight = PropertyFileHelper.getIntProperty(prop, "floorHeight", 8);

			roomMobName = prop.getProperty("spawnerMob", "minecraft:zombie");

			this.wallBlock = Blocks.STONEBRICK;
			try
			{
				Block tmp = Block.getBlockFromName(prop.getProperty("floorblock", "minecraft:stone"));
				if(tmp != null)
				{
					this.wallBlock = tmp;
				}
			}
			catch(Exception ex)
			{
				System.out.println("couldn't load wall block! using default value (stone bricks)...");
			}

			try
			{
				Block tmp = Block.getBlockFromName(prop.getProperty("wallblock", "minecraft:white_terracotta"));
				if(tmp != null)
				{
					this.floorBlock = tmp;
				}
			}
			catch(Exception ex)
			{
				System.out.println("couldn't load wall block! using default value (white clay)...");
			}

			closeConfigFile();
		} else {
			success = false;
		}
		if(success) {
			this.registeredSuccessful = true;
		}

		this.random = new Random();
	}

	@Override
	protected void generate(int x, int z, World world, Chunk chunk, Random random) {
		super.generate(x, z, world, chunk, random);

		this.generator = new CastleGenerator(this);

		//Generating it...
		int y = DungeonGenUtils.getHighestYAt(chunk, x, z, false);
		System.out.println("Generating structure " + this.name + " at X: " + x + "  Y: " + y + "  Z: " + z + "  ...");
		this.generator.generate(world, chunk, x, y, z);
	}

	public Block getWallBlock() {return this.wallBlock;}
	public Block getFloorBlock() {return this.floorBlock;}
	public int getMaxSize() {return this.maxSize;}
	public int getRoomSize() {return this.roomSize;}
	public int getFloorHeight() {return this.floorHeight;}
	public Random getRandom() {return this.random;}

	public ResourceLocation getSpawnerMob() {
		String[] bossString = this.roomMobName.split(":");

		return new ResourceLocation(bossString[0], bossString[1]);
	}

	public ResourceLocation getBossMob() {
		String[] bossString = this.bossMobName.split(":");

		return new ResourceLocation(bossString[0], bossString[1]);
	}

}
