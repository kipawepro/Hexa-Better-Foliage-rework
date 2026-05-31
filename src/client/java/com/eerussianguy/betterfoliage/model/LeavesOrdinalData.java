package com.eerussianguy.betterfoliage.model;

import java.util.Random;

import net.minecraft.core.BlockPos;

import com.eerussianguy.betterfoliage.BFConfig;

public class LeavesOrdinalData
{
    private static final Random RANDOM = new Random();

    public final int ordinal;

    public LeavesOrdinalData(BlockPos pos)
    {
        RANDOM.setSeed(pos.asLong() * 524287L);
        ordinal = RANDOM.nextInt((int) Math.pow(BFConfig.INSTANCE.leavesCacheSize, 3));
    }

    public int get() { return ordinal; }
}
