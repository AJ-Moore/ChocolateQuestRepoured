package com.teamcqr.chocolatequestrepoured.structuregen.generators.volcano;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.teamcqr.chocolatequestrepoured.API.events.CQDungeonStructureGenerateEvent;
import com.teamcqr.chocolatequestrepoured.objects.factories.SpawnerFactory;
import com.teamcqr.chocolatequestrepoured.structuregen.WorldDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.dungeons.VolcanoDungeon;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.IDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.volcano.StairCaseHelper.EStairSection;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.volcano.brickfortress.EntranceBuilder;
import com.teamcqr.chocolatequestrepoured.structuregen.lootchests.ELootTable;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;
import com.teamcqr.chocolatequestrepoured.util.ThreadingUtil;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

/**
 * Copyright (c) 29.04.2019
 * Developed by DerToaster98
 * GitHub: https://github.com/DerToaster98
 */
public class VolcanoGenerator implements IDungeonGenerator{

	//DONE: Make chests and blocks (stoneMat, CobbleMat, lavaMat, magmaMat, pathMat) customisable
	//DONE: Lower chest and spawner chance
	
	//BOss name: Volcovare Akvel
	
	/**
	 * Generate: Given height, given base radius, given top inner radius
	 * steepness: In % 
	 * level begins at 0!!
	 * DONE: Outer Radius -> make new function, current function dows not really work out
	 * Outer Radius --> y=(level * steepness) * level^2 --> y = steepness *level^3 --> RADIUS = baseRAD - (level/steepness)^1/3
	 * Inner Radius --> given, OR if (OuterRadius - (maxHeight - currHeight)) > given && (OuterRadius - (maxHeight - currHeight)) < maxInnerRad then use (OuterRadius - (maxHeight - currHeight))
	 * Block placing: generate circles. Per outer layer, the probability to place a block is reduced, same for height. A block can only be placed if it has a support block 
	 * probability for blocks: max rad +1: 0%    minRad -1: 100%  --> P(x) = 1- [ (x-MIN)/(MAX-MIN)  -steepNess * level]     x = radius of block -> distance to center - innerRadius
	 * 
	 * 
	 * Config Values:
	 *  > Steepness
	 *  > minRadius
	 *  > maxHeight/topY
	 *  
	 *  Calculate first: minY in affected region
	 *  difference between minY and topY --> height
	 *  
	 *  then: calculate base radius
	 *	baseRad = minRadius + ((maxHeight+1)/steepness)^1/3 
	 *
	 *
	 *	MOVE CALCULATION OF BASERADIUS TO THE DUNGEON OBJECT!!!! -> NO, the maxHeight can be different as it is a random value....
	 * 
	 */
	
	private VolcanoDungeon dungeon;
	
	private int baseRadius = 1;
	private int minY = 1;
	private int maxHeight = 10;
	private int minRadius = 1;
	private int entranceDistToWall = 10;
	private double steepness = 0.0D;
	private List<BlockPos> spawnersNChestsOnPath = new ArrayList<>();
	private BlockPos centerLoc = null;
	private BlockPos entranceStartPos = null;
	private EStairSection entranceDirection = null; 
	
	double oldProgress = -1.0;
	
	public VolcanoGenerator(VolcanoDungeon dungeon) {
		this.dungeon = dungeon;
		
		this.maxHeight = DungeonGenUtils.getIntBetweenBorders(dungeon.getMinHeight(), dungeon.getMaxHeight());
		this.minRadius = dungeon.getInnerRadius();
		this.steepness = dungeon.getSteepness();
		
		this.baseRadius = new Double(this.minRadius + Math.pow(((this.maxHeight +1)/this.steepness), 1/3)).intValue();
	}
	
	//TODO: Merge all parts
	//DONE: Lower ore gen
	//TODO: add noise in crater like in new version that is too slow
	
	@Override
	public void preProcess(World world, Chunk chunk, int x, int y, int z) { 
		//X Y Z mark the C E N T E R / Middle of the crater!!!!
		this.centerLoc = new BlockPos(x, y, z);
		
		Random rdm = new Random();
		
		maxHeight += new Double(maxHeight *0.1).intValue();
		this.baseRadius = new Double(this.minRadius + Math.cbrt(this.maxHeight/this.steepness)).intValue();
		
		//System.out.println("Calculating minY...");
		minY = getMinY(centerLoc, baseRadius, world) /*- (new Double(0.1 * maxHeight).intValue())*/;
		//System.out.println("MinY: " + minY);
		
		//System.out.println("Max Height: " + maxHeight);
		//System.out.println("Steepness: " + steepness);
		//System.out.println("Min Radius: " + minRadius);
		//System.out.println("Base Radius: " + baseRadius);
		
		//1) Calculate MinY
		//2) Calculate the base radius
		//4) calculate all block positions
		//5) Create a new SimpleThread that places all blocks
		//6) Place cover blocks -> calculate positions and let the thread place the blocks
		//7) Calculate the blocks for the spire
		//8) Build the dungeon
		
		//System.out.println("Creating lists...");
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		List<BlockPos> blocksLower = new ArrayList<BlockPos>();
		List<BlockPos> lava = new ArrayList<BlockPos>();
		List<BlockPos> airBlocks = new ArrayList<BlockPos>();
		List<BlockPos> magma = new ArrayList<BlockPos>();
		List<BlockPos> stairBlocks = new ArrayList<BlockPos>();
		List<BlockPos> pillarCenters = new ArrayList<BlockPos>();
		//System.out.println("Created lists!");
		
		//DONE: Split generation of volcano into 4 threads (corners, means x- z- , x+ z- , x+ z+ , x- z+) IMPORTANT: They need to use the same variables (like the random) -> NOPE, problem was removin things from lists...
		
		//Calculates all the "wall" blocks
		//DONE Place lava
		//DONE inner "cave" digs down to bedrock, volcano shape begins at minY and spreads below it to 0!!
		
		//System.out.println("Calculating block positions...");
		int yMax = ((minY + this.maxHeight) < 256 ? this.maxHeight : (255 - minY));
		
		//Lower "cave" part
		int lowYMax = minY + (new Double(0.1 * maxHeight).intValue());
		int[] radiusArr = new int[(int) (lowYMax *0.9)];
		for(int iY = 0; iY <= lowYMax -2; iY++) {
			int radius = new Double(Math.sqrt(-1.0 * new Double((iY - lowYMax) / (10.0 * this.dungeon.getSteepness()))) + (double)this.minRadius).intValue();
			if(iY < radiusArr.length) {
				radiusArr[iY] = radius;
			}
			for(int iX = -radius -2; iX <= radius +2; iX++) {
				for(int iZ = -radius -2; iZ <= radius +2; iZ++) {
					if(DungeonGenUtils.isInsideCircle(iX, iZ, radius, centerLoc)) {
						if(DungeonGenUtils.isInsideCircle(iX, iZ, (radius -1), centerLoc)) {
							if(iY < 2) {
								//We're low enought, place lava
								lava.add(new BlockPos(iX +x, iY +6, iZ +z));
							} else {
								//We're over the lava -> air
								airBlocks.add(new BlockPos(iX +x, iY +6, iZ +z));
							}
						} else {
							//System.out.println("SPHERE");
							//We are in the outer wall -> random spheres to make it more cave
							if(DungeonGenUtils.getIntBetweenBorders(0, 101) > 95) {
								blocksLower.addAll(getSphereBlocks(new BlockPos(iX +x, iY +6, iZ +z), rdm.nextInt(3) +2));
							}
						}
					}
				}
			}
		}
		
		//Infamous nether staircase
		EStairSection currStairSection = StairCaseHelper.getRandomStartSection();
		this.entranceDirection = currStairSection.getSuccessor();
		if(this.dungeon.doBuildStairs()) {
			int yStairCase, stairRadius = 1;
			for(int i = -3; i < radiusArr.length; i++) {
				yStairCase = i >= 0 ? i +7 : 7;
				stairRadius = i >= 0 ? radiusArr[i] : radiusArr[0];
				
				//Calculates the position of the entrance to the stronghold
				if(dungeon.doBuildDungeon() && i == 0) {
					entranceDistToWall = (radiusArr[i] /3); 
					int vecI = radiusArr[i] - entranceDistToWall;
					switch(entranceDirection) {
					case EAST: case EAST_SEC:
						entranceStartPos = new BlockPos(centerLoc.getX(), yStairCase, centerLoc.getZ()).add(vecI,0,0);
						break;
					case NORTH: case NORTH_SEC:
						entranceStartPos = new BlockPos(centerLoc.getX(), yStairCase, centerLoc.getZ()).add(0,0,-vecI);
						break;
					case SOUTH: case SOUTH_SEC:
						entranceStartPos = new BlockPos(centerLoc.getX(), yStairCase, centerLoc.getZ()).add(0,0,vecI);
						break;
					case WEST: case WEST_SEC:
						entranceStartPos = new BlockPos(centerLoc.getX(), yStairCase, centerLoc.getZ()).add(-vecI,0,0);
						break;
					default:
						break;
					
					}
				}
				
				
				for(int iX = -stairRadius; iX <= stairRadius; iX++) {
					for(int iZ = -stairRadius; iZ <= stairRadius; iZ++) {
						//Pillars
						if(dungeon.doBuildDungeon() && i == -3 && StairCaseHelper.isPillarCenterLocation(iX, iZ, stairRadius)) {
							//System.out.println("Adding pillar pos");
							pillarCenters.add(new BlockPos(iX +x, yStairCase -3, iZ +z));
						}
						//Stairwell -> check if it is in the volcano
						if(DungeonGenUtils.isInsideCircle(iX, iZ, stairRadius +1, centerLoc) && !DungeonGenUtils.isInsideCircle(iX, iZ, stairRadius /2, centerLoc)) {
							//Check that it is outside of the middle circle
							if(StairCaseHelper.isLocationFine(currStairSection, iX, iZ, stairRadius)) {
								BlockPos pos = new BlockPos(iX +x, yStairCase, iZ +z);
								stairBlocks.add(pos);
								//Spawners and chets, spawn only in a certain radius and only with 1% chance
								if(DungeonGenUtils.isInsideCircle(iX, iZ, (stairRadius /2) + (stairRadius /4) + (stairRadius /6), centerLoc)) {
									if(new Random().nextInt(this.dungeon.getChestChance() +1) >= (this.dungeon.getChestChance() -1)) {
										spawnersNChestsOnPath.add(pos.add(0,1,0));
									}
								}
								
							}
						}
					}
				}
				currStairSection = currStairSection.getSuccessor();
			}
		}
		
		//Upper volcano part
		for(int iY = 0; iY < yMax; iY++) {
			//RADIUS = baseRAD - (level/steepness)^1/3
			int radiusOuter = new Double(this.baseRadius - Math.cbrt(iY/this.steepness)).intValue();
			int innerRadius = this.minRadius; //DONE calculate minRadius
			
			for(int iX = -radiusOuter*2; iX <= radiusOuter*2; iX++) {
				for(int iZ = -radiusOuter*2; iZ <= radiusOuter*2; iZ++) {
					//First check if it is within the base radius...
					if(DungeonGenUtils.isInsideCircle(iX, iZ, radiusOuter*2, centerLoc)) {
						//If it is at the bottom and also inside the inner radius -> lava
						if(!DungeonGenUtils.isInsideCircle(iX, iZ, innerRadius, centerLoc)) {
							//Else it is a wall block
							//SO now we decide what the wall is gonna be...
							if(DungeonGenUtils.PercentageRandom(dungeon.getLavaChance(), rdm.nextLong()) && !DungeonGenUtils.isInsideCircle(iX, iZ, innerRadius +2, centerLoc)) {
								//It is lava :D
								lava.add(new BlockPos(iX +x, iY + minY, iZ +z));
							} else if(DungeonGenUtils.PercentageRandom(dungeon.getMagmaChance(), rdm.nextLong())) {
								//It is magma
								magma.add(new BlockPos(iX +x, iY + minY, iZ +z));
							} else {
								//It is stone or ore
								if(DungeonGenUtils.getIntBetweenBorders(0, 101) > 95) {
									blocks.addAll(getSphereBlocks(new BlockPos(iX +x, iY + minY, iZ +z), rdm.nextInt(3) +1));
								} else {
									blocks.add(new BlockPos(iX +x, iY + minY, iZ +z));
								}
							}
						}
					}
				}
			}
			
			//System.out.println("Progress: " + iY + "/" + yMax + " layers calculated...");
		}
		
		if(this.dungeon.isVolcanoDamaged()) {
			//System.out.println("Generating damage / holes...");
			generateHoles(blocks, airBlocks);
			//System.out.println("Calculated air for holes!");
		}
		
		ThreadingUtil.passListWithBlocksToThreads(blocks, dungeon.getUpperMainBlock(), world, 150, true);
		if(this.dungeon.generateOres()) {
			//System.out.println("Generating ore...");
			generateOres(world, blocks);
			//-> Takes very long ??? weird. Problem was removing elements from lists....
			//System.out.println("Ore generated!");
		}
		
		//System.out.println("Placing blocks...");
		ThreadingUtil.passListWithBlocksToThreads(lava, dungeon.getLavaBlock(), world, 150, true);
		ThreadingUtil.passListWithBlocksToThreads(magma, dungeon.getMagmaBlock(), world, 150, true);
		ThreadingUtil.passListWithBlocksToThreads(airBlocks, Blocks.AIR, world, 150, true);
		ThreadingUtil.passListWithBlocksToThreads(blocksLower, dungeon.getLowerMainBlock(),  dungeon.getMagmaBlock(), new Double((this.dungeon.getMagmaChance() *100.0D) *2.0D).intValue(), world, 150);
		if(this.dungeon.doBuildStairs()) {
			ThreadingUtil.passListWithBlocksToThreads(stairBlocks, dungeon.getRampBlock(), world, 150, true);
		}
		if(dungeon.doBuildDungeon()) {
			generatePillars(pillarCenters, lowYMax +10, world);
		}
		
		BlockPos lowerCorner = new BlockPos(x -(baseRadius*2), 0, z-(baseRadius*2));
		BlockPos upperCorner = new BlockPos(2*(baseRadius*2), yMax +y, 2*(baseRadius*2));
		CQDungeonStructureGenerateEvent event = new CQDungeonStructureGenerateEvent(this.dungeon, lowerCorner, upperCorner, world);
		MinecraftForge.EVENT_BUS.post(event);
		//System.out.println("Blocks palced!");
		
		//DONE Pass the list to a simplethread to place the blocks
		
		//TIME
		//All: About 20 seconds
	}

	@Override
	public void buildStructure(World world, Chunk chunk, int x, int y, int z) {
		if(dungeon.doBuildDungeon()) {
			EntranceBuilder entranceBuilder = new EntranceBuilder(entranceStartPos, entranceDistToWall, dungeon, entranceDirection.getAsSkyDirection(), world);
			entranceBuilder.generate();
		}
		
		
		//Generates the stronghold
		//TODO: Generate stronghold -> like a good old rogue dungeon
		
		//IMPORTANT: Entrance + Staircase: Same as original
		
		//1) Build entrance
		//2) figure out direction
		//3) choose a random number of rooms for layer
		//4) create a "map" that knows the rooms locations
		//5) check via warshall algorithm
		//6) choose place for random staircase
		//7) if there are still layers to build, goto 3)
		//9) if all layers all build, generate a final layer with one to three rooms with weapons / healing / food and a final hallway that leads to the boss chamber
	}

	@Override
	public void postProcess(World world, Chunk chunk, int x, int y, int z) {
		
	}

	@Override
	public void fillChests(World world, Chunk chunk, int x, int y, int z) {
		// DONE Fill chests on path
		Random rdm = new Random();
		for(BlockPos pos : spawnersNChestsOnPath) {
			if(rdm.nextBoolean()) {
				world.setBlockState(pos, Blocks.CHEST.getDefaultState());
				TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
				int eltID = dungeon.getChestIDs()[rdm.nextInt(dungeon.getChestIDs().length)];
				if(chest != null) {
					ResourceLocation resLoc = null;
					try {
						resLoc = ELootTable.valueOf(eltID).getResourceLocation();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					if(resLoc != null) {
						long seed = WorldDungeonGenerator.getSeed(world, x +pos.getX() + pos.getY(), z +pos.getZ() + pos.getY());
						chest.setLootTable(resLoc, seed);
					}
				}
			}
		}
	}

	@Override
	public void placeSpawners(World world, Chunk chunk, int x, int y, int z) {
		// DONE Place spawners for dwarves/golems/whatever on path
		for(BlockPos pos : spawnersNChestsOnPath) {
			/*world.setBlockState(pos.add(0,1,0), Blocks.MOB_SPAWNER.getDefaultState());
			
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)world.getTileEntity(pos.add(0,1,0));
			
			spawner.getSpawnerBaseLogic().setEntityId(this.dungeon.getMob());
			//System.out.println("Spawner Mob: " + this.dungeon.getMob().toString());
			spawner.updateContainingBlockInfo();
			
			spawner.update();*/
			SpawnerFactory.createSimpleMultiUseSpawner(world, pos.add(0,1,0), dungeon.getRampMob());
		}
	}

	@Override
	public void placeCoverBlocks(World world, Chunk chunk, int x, int y, int z) {
		if(this.dungeon.isCoverBlockEnabled()) {
			List<BlockPos> coverBlocks = new ArrayList<>();
			
			for(int iX = new Double(x - (this.baseRadius*1.25)).intValue(); iX <= new Double(x + (this.baseRadius*1.25)).intValue(); iX++) {
				for(int iZ = new Double(z - (this.baseRadius*1.25)).intValue(); iZ <= new Double(z + (this.baseRadius*1.25)).intValue(); iZ++) {
					coverBlocks.add(world.getTopSolidOrLiquidBlock(new BlockPos(iX, 0, iZ).add(0, 1, 0)));
				}
			}
			
			ThreadingUtil.passListWithBlocksToThreads(coverBlocks, this.dungeon.getCoverBlock(), world, 50, true);
		}
		//DONE Pass the list to a simplethread to place the blocks
	}
	
	private List<BlockPos> getSphereBlocks(BlockPos center, int radius) {
		List<BlockPos> posList = new ArrayList<>();
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				for(int z = -radius; z <= radius; z++) {
					BlockPos p = center.add(x, y, z);
					if(DungeonGenUtils.isInsideSphere(p, center, radius)) {
						posList.add(p);
					}
				}
			}
		}
		return posList;
	}
	
	private void generateOres(World world, List<BlockPos> blocks) {
		Random rdm = new Random();
		
		//System.out.println("Generating ore lists...");
		List<BlockPos> coals = new ArrayList<>();
		List<BlockPos> irons = new ArrayList<>();
		List<BlockPos> golds = new ArrayList<>();
		List<BlockPos> redstones = new ArrayList<>();
		List<BlockPos> emeralds = new ArrayList<>();
		List<BlockPos> diamonds = new ArrayList<>();
		//System.out.println("Ore lists created!");
		
		List<Integer> usedIndexes = new ArrayList<>();
		Double divisor = new Double((double)this.dungeon.getOreChance() / 100.0);
		//System.out.println("Double: " + divisor);
		//System.out.println("Block Count: " + blocks.size());
		//System.out.println("Ore count: " + (new Double(divisor * blocks.size()).intValue()));
		for(int i = 0; i < (new Double(divisor * blocks.size()).intValue()); i++) {
			int blockIndex = rdm.nextInt(blocks.size());
			while(usedIndexes.contains(blockIndex)) {
				blockIndex = rdm.nextInt(blocks.size());
			}
			BlockPos p = blocks.get(blockIndex);
			int chance = rdm.nextInt(200) +1;
			
			if(chance >= 190) {
				//DIAMOND
				diamonds.add(p);
			} else
			if(chance >= 180) {
				//EMERALD
				emeralds.add(p);
			} else
			if(chance >= 90) {
				//GOLD
				golds.add(p);
			} else
			if(chance >= 60) {
				//REDSTONE
				redstones.add(p);
			} else
			if(chance >=  55) {
				//IRON
				irons.add(p);
			} else
			if(chance >=35) {
				//COAL
				coals.add(p);
			} 
				
		}
		
		//System.out.println("Coal: " + coals.size());
		//System.out.println("Iron: " + irons.size());
		//System.out.println("Gold: " + golds.size());
		//System.out.println("Redstone: " + redstones.size());
		//System.out.println("Emeralds: " + emeralds.size());
		//System.out.println("Diamonds: " + diamonds.size());
		
		ThreadingUtil.passListWithBlocksToThreads(coals, Blocks.COAL_ORE, world, coals.size(), true);
		ThreadingUtil.passListWithBlocksToThreads(irons, Blocks.IRON_ORE, world, irons.size(), true);
		ThreadingUtil.passListWithBlocksToThreads(golds, Blocks.GOLD_ORE, world, golds.size(), true);
		ThreadingUtil.passListWithBlocksToThreads(redstones, Blocks.REDSTONE_ORE, world, redstones.size(), true);
		ThreadingUtil.passListWithBlocksToThreads(emeralds, Blocks.EMERALD_ORE, world, emeralds.size(), true);
		ThreadingUtil.passListWithBlocksToThreads(diamonds, Blocks.DIAMOND_ORE, world, diamonds.size(), true);
	}
	
	private void generateHoles(List<BlockPos> blocks, List<BlockPos> airBlocks) {
		Random rdm = new Random();
		//Makes random holes
		for(int holeCount = 0; holeCount < maxHeight *1.5; holeCount++) {
			BlockPos center = blocks.get(rdm.nextInt(blocks.size()));
			
			int radius = DungeonGenUtils.getIntBetweenBorders(1, this.dungeon.getMaxHoleSize());
			
			for(BlockPos p : getSphereBlocks(center, radius)) {
				airBlocks.add(p);
			}
			
		}
	}
	
	private void generatePillars(List<BlockPos> centers, int maxY, World world) {
		List<BlockPos> pillarBlocks = new ArrayList<BlockPos>();
		for(BlockPos center : centers) {
			for(int iY = 0; iY <= maxY; iY++) {
				for(int iX = -3; iX <= 3; iX++) {
					for(int iZ = -3; iZ <= 3; iZ++) {
						if(DungeonGenUtils.isInsideCircle(iX, iZ, 3, center)) {
							pillarBlocks.add(center.add(iX, iY, iZ));
						}
					}
				}
			}
		}
		ThreadingUtil.passListWithBlocksToThreads(pillarBlocks, dungeon.getPillarBlock(), world, pillarBlocks.size(), true);
	}
	
	private int getMinY(BlockPos center, int radius, World world) {
		int minY = 256;
		for(int iX = -radius; iX <= radius; iX++) {
			for(int iZ = -radius; iZ <= radius; iZ++) {
				int yTmp = DungeonGenUtils.getHighestYAt(world.getChunkFromBlockCoords(center.add(iX, 0, iZ)), iX, iZ, true);
				if(yTmp < minY) {
					minY = yTmp;
				}
			}
		}
		return minY -5;
	}
	
}
