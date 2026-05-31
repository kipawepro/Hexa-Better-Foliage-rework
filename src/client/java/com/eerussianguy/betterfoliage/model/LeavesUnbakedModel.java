package com.eerussianguy.betterfoliage.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.betterfoliage.Helpers;

public class LeavesUnbakedModel implements UnbakedModel, BlockStateModel.UnbakedRoot
{
    private final LeavesModel data;

    public LeavesUnbakedModel(LeavesModel data)
    {
        this.data = data;
    }

    // --- UnbakedModel ---
    @Override
    public TextureSlots.Data textureSlots()
    {
        TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder()
            .addTexture("leaves", new Material(data.leaves(), false))
            .addTexture("fluff",  new Material(data.fluff(),  false));
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
        Material.Baked leavesTex = baker.materials().get(new Material(data.leaves(), false), () -> "betterfoliage:leaves/leaves");
        Material.Baked fluffTex  = baker.materials().get(new Material(data.fluff(),  false), () -> "betterfoliage:leaves/fluff");

        Material.Baked overlayTex = null;
        if (!data.overlay().equals(Helpers.EMPTY))
            overlayTex = baker.materials().get(new Material(data.overlay(), false), () -> "betterfoliage:leaves/overlay");

        return new LeavesBakedModel(data, leavesTex, fluffTex, overlayTex, baker);
    }

    @Override
    public Object visualEqualityGroup(BlockState blockState)
    {
        return this;
    }
}
