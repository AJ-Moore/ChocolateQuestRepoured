package com.teamcqr.chocolatequestrepoured.structuregen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import com.teamcqr.chocolatequestrepoured.structuregen.generators.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class DungeonBase {
	
	protected IDungeonGenerator generator;
	private boolean replaceBanners = false;
	//private CQFaction owningFaction
	protected String name;
	private Item placeItem;
	private UUID dunID;
	protected int underGroundOffset = 0;
	protected int chance;
	protected int yOffset = 0;
	protected int[] allowedDims = {0};
	protected boolean unique = false;
	protected boolean buildSupportPlatform = true;
	protected boolean protectFromDestruction = false;
	protected boolean useCoverBlock = false;
	private boolean spawnBehindWall = false;
	private int iconID;
	private FileInputStream fisConfigFile = null;
	private Block supportBlock = Blocks.STONE;
	private Block supportTopBlock = Blocks.GRASS;
	protected Block coverBlock = Blocks.AIR;
	private BlockPos lockedPos = null;
	private boolean isPosLocked = false;
	protected boolean registeredSuccessful = false;
	
	public void generate(BlockPos pos, World world) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		Random rdm = new Random();
		rdm.setSeed(WorldDungeonGenerator.getSeed(world, chunk.x, chunk.z));
		generate(pos.getX(), pos.getZ(), world, chunk, rdm);
	}
	
	protected void generate(int x, int z, World world, Chunk chunk, Random random) {
		this.dunID = MathHelper.getRandomUUID();
	}
	
	public DungeonBase(File configFile) {
		//DONE: read values from file
		Properties prop = loadConfig(configFile);/*new Properties();
		this.dunID = MathHelper.getRandomUUID();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(configFile);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to read config file: " + configFile.getName());
			e.printStackTrace();
			prop = null;
			configFile = null;
		} catch (IOException e) {
			System.out.println("Unable to read config file: " + configFile.getName());
			e.printStackTrace();
			prop = null;
			configFile = null;
		}*/
		//if(prop != null && configFile != null && fis != null) {
		if(prop != null) {
			this.name = configFile.getName().replace(".properties", "");
			this.chance = PropertyFileHelper.getIntProperty(prop, "chance", 20);
			this.underGroundOffset = PropertyFileHelper.getIntProperty(prop, "undergroundoffset", 0);
			this.allowedDims = PropertyFileHelper.getIntArrayProperty(prop, "allowedDims", new int[]{0});
			this.unique = PropertyFileHelper.getBooleanProperty(prop, "unique", false);
			this.protectFromDestruction = PropertyFileHelper.getBooleanProperty(prop, "protectblocks", false);
			this.useCoverBlock = PropertyFileHelper.getBooleanProperty(prop, "usecoverblock", false);
			this.spawnBehindWall = PropertyFileHelper.getBooleanProperty(prop, "spawnOnlyBehindWall", false);
			this.iconID = PropertyFileHelper.getIntProperty(prop, "icon", 0);
			this.yOffset = PropertyFileHelper.getIntProperty(prop, "yoffset", 0);
			this.replaceBanners = PropertyFileHelper.getBooleanProperty(prop, "replaceBanners", false);
		
			this.buildSupportPlatform = PropertyFileHelper.getBooleanProperty(prop, "buildsupportplatform", false);
			if(this.buildSupportPlatform) {
				this.supportBlock = Blocks.STONE;
				try {
					Block tmp = Block.getBlockFromName(prop.getProperty("supportblock", "minecraft:stone"));
					if(tmp != null) {
						this.supportBlock = tmp;
					}
				} catch(Exception ex) {
					System.out.println("couldnt load supportblock block! using default value (stone block)...");
				}
				
				this.supportTopBlock = Blocks.GRASS;
				try {
					Block tmp = Block.getBlockFromName(prop.getProperty("supportblocktop", "minecraft:stone"));
					if(tmp != null) {
						this.supportTopBlock = tmp;
					}
				} catch(Exception ex) {
					System.out.println("couldnt load supportblocktop block! using default value (air block)...");
				}
			}
			this.coverBlock = Blocks.AIR;
			try {
				Block tmp = Block.getBlockFromName(prop.getProperty("coverblock", "minecraft:air"));
				if(tmp != null) {
					this.coverBlock = tmp;
				}
			} catch(Exception ex) {
				System.out.println("couldnt load cover block! using default value (air block)...");
			}
			closeConfigFile();
		} else {
			registeredSuccessful = false;
		}
	}
	
	public void closeConfigFile() {
		try {
			fisConfigFile.close();
		} catch (IOException e) {
			registeredSuccessful = false;
			e.printStackTrace();
		}
	}

	public IDungeonGenerator getGenerator() {
		return this.generator;
	}
	public Item getDungeonPlaceItem() {
		return this.placeItem;
	}
	public String getDungeonName() {
		return this.name;
	}
	public int getSpawnChance() {
		return this.chance;
	}
	public int[] getAllowedDimensions() {
		return this.allowedDims;
	}
	public boolean isUnique() {
		return this.unique;
	}

	public Block getSupportTopBlock() {
		return supportTopBlock;
	}

	public Block getSupportBlock() {
		return supportBlock;
	}

	public int getUnderGroundOffset() {
		return underGroundOffset;
	}

	public BlockPos getLockedPos() {
		return lockedPos;
	}

	public boolean isPosLocked() {
		return isPosLocked;
	}
	public int getIconID() {
		return this.iconID;
	}

	public void setLockPos(BlockPos pos, boolean locked) {
		this.lockedPos = pos;
		this.isPosLocked = locked;
	}
	public boolean isRegisteredSuccessful() {
		return this.registeredSuccessful;
	}
	public boolean doBuildSupportPlatform() {
		return this.buildSupportPlatform;
	}
	public Block getCoverBlock() {
		return this.coverBlock;
	}

	public UUID getDungeonID() {
		return this.dunID;
	}
	public boolean isProtectedFromModifications() {
		return this.protectFromDestruction;
	}
	public boolean isCoverBlockEnabled() {
		return this.useCoverBlock;
	}
	public boolean doesSpawnOnlyBehindWall() {
		return this.spawnBehindWall;
	}
	public boolean replaceBanners() {
		return this.replaceBanners;
	}
	
	public Properties loadConfig(File configFile) {
		Properties prop = new Properties();
		fisConfigFile = null;
		registeredSuccessful = true;
		try {
			fisConfigFile = new FileInputStream(configFile);
			prop.load(fisConfigFile);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to read config file: " + configFile.getName());
			e.printStackTrace();
			try {
				fisConfigFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			prop = null;
			configFile = null;
			registeredSuccessful = false;
		} catch (IOException e) {
			System.out.println("Unable to read config file: " + configFile.getName());
			e.printStackTrace();
			try {
				fisConfigFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			prop = null;
			configFile = null;
			registeredSuccessful = false;
		}
		if(prop != null && configFile != null && fisConfigFile != null) {
			return prop;
		}
		registeredSuccessful = false;
		return null;
	}
}