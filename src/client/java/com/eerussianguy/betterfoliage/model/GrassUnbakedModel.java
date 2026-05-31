package com.eerussianguy.betterfoliage.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.betterfoliage.Helpers;

public class GrassUnbakedModel implements UnbakedModel, BlockStateModel.UnbakedRoot
{
    private final GrassModel data;

    public GrassUnbakedModel(GrassModel data)
    {
        this.data = data;
    }

    // --- UnbakedModel ---
    @Override
    public TextureSlots.Data textureSlots()
    {
        TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder()
            .addTexture("dirt",    new Material(data.dirt(),    false))
            .addTexture("top",     new Material(data.top(),     false));
        if (!data.overlay().equals(Helpers.EMPTY))
            builder.addTexture("overlay", new Material(data.overlay(), false));
        return builder.build();
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {}

    // --- BlockStateModel.UnbakedRoot ---
    @Override
    public BlockStateModel bake(BlockState blockState, ModelBaker baker)
    {
        Material.Baked dirtTex    = baker.materials().get(new Material(data.dirt(),    false), () -> "betterfoliage:grass/dirt");
        Material.Baked topTex     = baker.materials().get(new Material(data.top(),     false), () -> "betterfoliage:grass/top");
        Material.Baked overlayTex = baker.materials().get(new Material(data.overlay(), false), () -> "betterfoliage:grass/overlay");
        return new GrassBakedModel(data, dirtTex, topTex, overlayTex, baker);
    }

    @Override
    public Object visualEqualityGroup(BlockState blockState)
    {
        return this;
    }
}
