package com.eerussianguy.betterfoliage;

import com.eerussianguy.betterfoliage.model.GrassLoader;
import com.eerussianguy.betterfoliage.model.LeavesLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ModelEventHandler
{
    public static final ExtraModelKey<BlockStateModel> BETTER_GRASS_KEY =
        ExtraModelKey.create(() -> "better_grass");
    public static final ExtraModelKey<BlockStateModel> BETTER_GRASS_SNOWED_KEY =
        ExtraModelKey.create(() -> "better_grass_snowed");
    public static final ExtraModelKey<BlockStateModel> BETTER_MYCELIUM_KEY =
        ExtraModelKey.create(() -> "better_mycelium");

    public static void init()
    {
        // Register custom JSON model deserializers (replaces NeoForge IGeometryLoader)
        UnbakedModelDeserializer.register(
            Helpers.identifier("leaves"),
            new LeavesLoader()
        );
        UnbakedModelDeserializer.register(
            Helpers.identifier("grass"),
            new GrassLoader()
        );

        // Register extra standalone models (grass cross models)
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModel(BETTER_GRASS_KEY,
                SimpleUnbakedExtraModel.blockStateModel(modelId("block/better_grass")));
            pluginContext.addModel(BETTER_GRASS_SNOWED_KEY,
                SimpleUnbakedExtraModel.blockStateModel(modelId("block/better_grass_snowed")));
            pluginContext.addModel(BETTER_MYCELIUM_KEY,
                SimpleUnbakedExtraModel.blockStateModel(modelId("block/better_mycelium")));
        });

        // Clear particle sprite cache on resource reload
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register(
            client -> ClientTickHandler.clearCache()
        );
    }

    private static Identifier modelId(String path)
    {
        return Helpers.identifier(path);
    }
}
