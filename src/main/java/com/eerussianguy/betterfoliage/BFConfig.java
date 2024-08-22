package com.eerussianguy.betterfoliage;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BFConfig
{
    public static final ClientConfig CLIENT;
    public static final ModConfigSpec SPEC;

    static
    {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = specPair.getLeft();
        SPEC = specPair.getRight();
    }
}
