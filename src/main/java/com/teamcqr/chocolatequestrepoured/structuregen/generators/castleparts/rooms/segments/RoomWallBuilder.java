package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments;

import com.teamcqr.chocolatequestrepoured.util.BlockPlacement;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class RoomWallBuilder
{
    protected BlockPos wallStart;
    protected WallOptions options;
    protected int doorStart = 0;
    protected int doorWidth = 0;
    protected int length;
    protected int height;
    protected EnumFacing side;
    protected Random random;

    public RoomWallBuilder(BlockPos roomStart, int height, int length, WallOptions options, EnumFacing side)
    {
        this.height = height;
        this.length = length;
        this.options = options;
        this.side = side;

        this.wallStart = roomStart;

        if (side == EnumFacing.EAST)
        {
            wallStart = wallStart.add(length - 1, 0, 0);
        }
        else if (side == EnumFacing.SOUTH)
        {
            wallStart = wallStart.add(0, 0, length - 1);
        }

        if (options.hasDoor())
        {
            this.doorStart = options.getDoor().getOffset();
            this.doorWidth = options.getDoor().getWidth();
        }
    }

    public void generate(World world)
    {
        BlockPos pos;
        IBlockState blockToBuild;

        EnumFacing iterDirection;

        if (side.getAxis() == EnumFacing.Axis.X)
        {
            iterDirection = EnumFacing.SOUTH;
        }
        else
        {
            iterDirection = EnumFacing.EAST;
        }

        for (int i = 0; i < length; i++)
        {
            for (int y = 0; y < height; y++)
            {
                pos = wallStart.offset(iterDirection, i).offset(EnumFacing.UP, y);
                blockToBuild = getBlockToBuild(pos);
                world.setBlockState(pos, blockToBuild);
            }
        }
    }

    protected IBlockState getBlockToBuild(BlockPos pos)
    {
        if (options.hasWindow())
        {
            return getBlockBasicGlass(pos);
        }
        else if (options.hasDoor())
        {
            return getBlockDoor(pos);
        }
        else
        {
            return Blocks.STONEBRICK.getDefaultState();
        }
    }

    protected IBlockState getBlockDoor(BlockPos pos)
    {
        IBlockState blockToBuild = Blocks.STONEBRICK.getDefaultState();
        int y = pos.getY() - wallStart.getY();
        int dist = getLengthPoint(pos);

        if (withinDoorWidth(dist))
        {
            if (y == 0)
            {
                blockToBuild = Blocks.STONEBRICK.getDefaultState();
            }
            else if (y < DoorPlacement.DEFAULT_HEIGHT)
            {
                blockToBuild = Blocks.AIR.getDefaultState();
            }
        }

        return blockToBuild;
    }

    private IBlockState getBlockBasicGlass(BlockPos pos)
    {
        int y = pos.getY() - wallStart.getY();
        int dist = getLengthPoint(pos);

        if ((y == 3 || y == 4) && (dist == length / 2))
        {
            return Blocks.GLASS_PANE.getDefaultState();
        } else
        {
            return Blocks.STONEBRICK.getDefaultState();
        }
    }

    private IBlockState getBlockBasicBars(BlockPos pos)
    {
        int y = pos.getY();
        int dist = getLengthPoint(pos);

        if ((y == 3 || y == 4) && (dist == length / 2))
        {
            return Blocks.IRON_BARS.getDefaultState();
        } else
        {
            return Blocks.STONEBRICK.getDefaultState();
        }
    }

    /*
     * Whether to build a window is usually determined by how far along the wall we are.
     * This function gets the relevant length along the wall based on if we are a horizontal
     * wall or a vertical wall.
     */
    protected int getLengthPoint(BlockPos pos)
    {
        if (side.getAxis() == EnumFacing.Axis.X)
        {
            return pos.getZ() - wallStart.getZ();
        } else
        {
            return pos.getX() - wallStart.getX();
        }
    }

    protected boolean withinDoorWidth(int value)
    {
        int relativeToDoor = value - doorStart;
        return (relativeToDoor >= 0 && relativeToDoor < doorWidth);
    }
}
