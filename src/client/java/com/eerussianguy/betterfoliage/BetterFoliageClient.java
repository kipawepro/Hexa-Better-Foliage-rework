package com.eerussianguy.betterfoliage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class BetterFoliageClient implements ClientModInitializer
{
    public static final String MOD_ID = "hexa-betterfoliage";
    public static final String RESOURCE_ID = "betterfoliage";

    public static boolean LEAVES_DISABLED_BY_MOD = false;

    @Override
    public void onInitializeClient()
    {
        BFConfig.init();
        ClientTickHandler.init();
        ModelEventHandler.init();

        if (FabricLoader.getInstance().isModLoaded("tfc"))
        {
            LEAVES_DISABLED_BY_MOD = true;
        }
    }
}
