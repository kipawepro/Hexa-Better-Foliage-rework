package com.eerussianguy.betterfoliage.model;

import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.betterfoliage.BFConfig;
import com.eerussianguy.betterfoliage.Helpers;

public class LeavesBakedModel implements BlockStateModel
{
    private final LeavesModel data;
    private final Material.Baked leavesTex;
    private final Material.Baked fluffTex;

    // core = solid 1x1x1 cube of leaves texture
    private final Mesh coreMesh;
    // outerCoreMesh = solid cube of overlay texture (null if no overlay)
    private final Mesh outerCoreMesh; // nullable
    // crosses = cache of rotated cross quads at various offsets
    private final Mesh[] crosses;

    public LeavesBakedModel(LeavesModel data, Material.Baked leavesTex, Material.Baked fluffTex, Material.Baked overlayTex, ModelBaker baker)
    {
        this.data = data;
        this.leavesTex = leavesTex;
        this.fluffTex = fluffTex;

        MutableMesh builder = Renderer.get().mutableMesh();

        this.coreMesh = buildBlock(builder, leavesTex, data.tintLeaves());
        this.outerCoreMesh = overlayTex != null ? buildBlock(builder, overlayTex, data.tintOverlay()) : null;

        int cacheSize = BFConfig.INSTANCE.leavesCacheSize;
        int total = (int) Math.pow(cacheSize, 3);
        this.crosses = new Mesh[total];
        buildCrosses(builder);
    }

    private Mesh buildBlock(MutableMesh builder, Material.Baked tex, boolean tinted)
    {
        builder.clear();
        QuadEmitter e = builder.emitter();
        for (Direction dir : Direction.values())
        {
            e.square(dir, 0f, 0f, 1f, 1f, 0f);
            e.materialBake(tex, 0);
            e.atlas(QuadAtlas.BLOCK);
            e.chunkLayer(ChunkSectionLayer.CUTOUT);
            e.cullFace(dir);
            e.ambientOcclusion(TriState.TRUE);
            if (tinted) e.tintIndex(0);
            e.emit();
        }
        return builder.immutableCopy();
    }

    private void buildCrosses(MutableMesh builder)
    {
        int cacheSize = BFConfig.INSTANCE.leavesCacheSize;
        float dist = (float) BFConfig.INSTANCE.leavesVariationDistance;
        float[] intervals = Helpers.intervals(cacheSize, -dist, dist);

        int ordinal = 0;
        for (float x : intervals)
        {
            for (float y : intervals)
            {
                for (float z : intervals)
                {
                    builder.clear();
                    QuadEmitter e = builder.emitter();
                    buildCrossQuads(e, x, y, z);
                    crosses[ordinal++] = builder.immutableCopy();
                }
            }
        }
    }

    private void buildCrossQuads(QuadEmitter e, float ox, float oy, float oz)
    {
        // Two intersecting vertical planes rotated 45° around Y, offset by (ox, oy, oz)
        // Plane 1: NS axis
        float left1  = (-8f + ox / 2f)  / 16f;
        float right1 = (24f + ox / 2f)  / 16f;
        float bot1   = (-8f + oy / 1.2f) / 16f;
        float top1   = (24f + oy / 1.2f) / 16f;

        // North face of plane 1
        emitCrossQuad(e, Direction.NORTH, left1, bot1, right1, top1, 8f / 16f + oz / 32f);
        // South face of plane 1
        emitCrossQuad(e, Direction.SOUTH, left1, bot1, right1, top1, 8f / 16f + oz / 32f);

        // Plane 2: EW axis (same offsets, perpendicular)
        float left2  = (-8f + oz / 2f)  / 16f;
        float right2 = (24f + oz / 2f)  / 16f;

        emitCrossQuad(e, Direction.WEST, left2, bot1, right2, top1, 8f / 16f + ox / 32f);
        emitCrossQuad(e, Direction.EAST, left2, bot1, right2, top1, 8f / 16f + ox / 32f);
    }

    private void emitCrossQuad(QuadEmitter e, Direction dir, float left, float bottom, float right, float top, float depth)
    {
        e.square(dir, left, bottom, right, top, depth);
        e.materialBake(fluffTex, 0);
        e.atlas(QuadAtlas.BLOCK);
        e.chunkLayer(ChunkSectionLayer.CUTOUT);
        e.diffuseShade(false);
        e.ambientOcclusion(TriState.FALSE);
        if (data.tintLeaves()) e.tintIndex(0);
        e.emit();
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {}

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<Direction> cullTest)
    {
        coreMesh.outputTo(emitter);

        if (state != null)
        {
            LeavesOrdinalData ordinalData = new LeavesOrdinalData(pos);
            crosses[ordinalData.get()].outputTo(emitter);

            if (outerCoreMesh != null)
                outerCoreMesh.outputTo(emitter);
        }
    }

    @Override
    public Material.Baked particleMaterial()
    {
        return leavesTex;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags()
    {
        return 0;
    }
}
