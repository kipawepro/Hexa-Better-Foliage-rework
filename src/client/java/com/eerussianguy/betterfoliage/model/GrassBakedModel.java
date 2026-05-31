package com.eerussianguy.betterfoliage.model;

import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricModelManager;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.eerussianguy.betterfoliage.BFConfig;
import com.eerussianguy.betterfoliage.Helpers;
import com.eerussianguy.betterfoliage.ModelEventHandler;

public class GrassBakedModel implements BlockStateModel
{
    private final Mesh[] meshes = new Mesh[16];
    private final Material.Baked particleMat;
    private final GrassModel data;

    public GrassBakedModel(GrassModel data, Material.Baked dirtTex, Material.Baked topTex, Material.Baked overlayTex, ModelBaker baker)
    {
        this.data = data;
        this.particleMat = topTex;
        buildMeshes(dirtTex, topTex, overlayTex);
    }

    private void buildMeshes(Material.Baked dirtTex, Material.Baked topTex, Material.Baked overlayTex)
    {
        MutableMesh builder = Renderer.get().mutableMesh();
        for (int meta = 0; meta < 16; meta++)
        {
            builder.clear();
            QuadEmitter e = builder.emitter();
            boolean[] conn = stateFromMeta(meta); // N E S W

            emitFace(e, Direction.DOWN,  dirtTex,                             false);
            emitFace(e, Direction.UP,    topTex,                              data.tint());
            emitFace(e, Direction.NORTH, conn[0] ? topTex : overlayTex, data.tint() && conn[0]);
            emitFace(e, Direction.EAST,  conn[1] ? topTex : overlayTex, data.tint() && conn[1]);
            emitFace(e, Direction.SOUTH, conn[2] ? topTex : overlayTex, data.tint() && conn[2]);
            emitFace(e, Direction.WEST,  conn[3] ? topTex : overlayTex, data.tint() && conn[3]);

            meshes[meta] = builder.immutableCopy();
        }
    }

    private void emitFace(QuadEmitter e, Direction dir, Material.Baked tex, boolean tinted)
    {
        e.square(dir, 0f, 0f, 1f, 1f, 0f);
        e.materialBake(tex, 0);
        e.atlas(QuadAtlas.BLOCK);
        e.chunkLayer(ChunkSectionLayer.SOLID);
        e.cullFace(dir);
        e.ambientOcclusion(TriState.TRUE);
        if (tinted) e.tintIndex(0);
        e.emit();
    }

    private static boolean[] stateFromMeta(int meta)
    {
        return new boolean[] { (meta & 1) > 0, (meta & 2) > 0, (meta & 4) > 0, (meta & 8) > 0 };
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {}

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, java.util.function.Predicate<Direction> cullTest)
    {
        GrassConnectionData conn = computeConnection(level, pos);
        meshes[conn.get()].outputTo(emitter);

        if (conn.hasUp() && !data.grassLocation().equals(Helpers.EMPTY) && random.nextInt(BFConfig.INSTANCE.extraGrassRarity) == 0)
        {
            BlockStateModel grassModel = ((FabricModelManager) Minecraft.getInstance().getModelManager()).getModel(ModelEventHandler.BETTER_GRASS_KEY);
            if (grassModel != null)
                grassModel.emitQuads(emitter, level, pos, state, random, cullTest);
        }
    }

    private GrassConnectionData computeConnection(BlockAndTintGetter level, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos down = pos.below();
        boolean north = level.getBlockState(mutable.setWithOffset(down, Direction.NORTH)).hasProperty(BlockStateProperties.SNOWY);
        boolean east  = level.getBlockState(mutable.setWithOffset(down, Direction.EAST)).hasProperty(BlockStateProperties.SNOWY);
        boolean south = level.getBlockState(mutable.setWithOffset(down, Direction.SOUTH)).hasProperty(BlockStateProperties.SNOWY);
        boolean west  = level.getBlockState(mutable.setWithOffset(down, Direction.WEST)).hasProperty(BlockStateProperties.SNOWY);
        BlockState upState = level.getBlockState(mutable.setWithOffset(pos, Direction.UP));
        boolean up = upState.isAir() || upState.is(Blocks.SNOW);
        return new GrassConnectionData(north, east, south, west, up);
    }

    @Override
    public Material.Baked particleMaterial()
    {
        return particleMat;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags()
    {
        return 0;
    }
}
