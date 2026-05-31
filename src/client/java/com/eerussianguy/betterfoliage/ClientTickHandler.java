package com.eerussianguy.betterfoliage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.eerussianguy.betterfoliage.particle.LeafParticle;
import com.eerussianguy.betterfoliage.particle.SoulParticle;

@Environment(EnvType.CLIENT)
public class ClientTickHandler
{
    private static final Map<ParticleLocation, List<TextureAtlasSprite>> SPRITE_CACHE = new HashMap<>();

    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    public static void clearCache()
    {
        SPRITE_CACHE.clear();
    }

    private static void onClientTick(Minecraft mc)
    {
        if (mc.isPaused()) return;

        Entity entity = mc.getCameraEntity();
        if (entity == null) return;

        ClientLevel level = (ClientLevel) entity.level();
        if (level == null) return;
        if (level.getGameTime() % 2 != 0) return;

        final Vec3 ePos = entity.position();
        final Vec3i pos = new Vec3i((int) ePos.x, (int) ePos.y, (int) ePos.z);
        final RandomSource rand = level.getRandom();
        final int spawnDistance = BFConfig.INSTANCE.particleDistance;

        final TextureAtlas atlas = (TextureAtlas) mc.getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);

        for (int i = 0; i < BFConfig.INSTANCE.particleAttempts; i++)
        {
            BlockPos searchPos = new BlockPos(pos.offset(
                rand.nextInt(spawnDistance) - rand.nextInt(spawnDistance),
                rand.nextInt(spawnDistance) - 1,
                rand.nextInt(spawnDistance) - rand.nextInt(spawnDistance)
            ));
            BlockState state = level.getBlockState(searchPos);

            if (state.is(BlockTags.LEAVES) && level.isEmptyBlock(searchPos.below()))
            {
                LeafParticle particle = new LeafParticle(level, searchPos.getX() + 0.5D, searchPos.getY() - 1D, searchPos.getZ() + 0.5D);

                if (BFConfig.INSTANCE.snowballs && rand.nextInt(2) == 0 && level.getBlockState(searchPos.above()).is(Blocks.SNOW))
                {
                    Helpers.addParticle(particle, getTextures(ParticleLocation.SNOWBALL, atlas));
                }
                else if (BFConfig.INSTANCE.leaves && !BetterFoliageClient.LEAVES_DISABLED_BY_MOD)
                {
                    Holder<Biome> biome = level.getBiome(searchPos);
                    if (biome.is(BiomeTags.IS_TAIGA))
                        Helpers.addTintedParticle(particle, getTextures(ParticleLocation.LEAF_SPRUCE, atlas), state, level, searchPos);
                    else if (biome.is(BiomeTags.IS_JUNGLE))
                        Helpers.addTintedParticle(particle, getTextures(ParticleLocation.LEAF_JUNGLE, atlas), state, level, searchPos);
                    else
                        Helpers.addTintedParticle(particle, getTextures(ParticleLocation.LEAF, atlas), state, level, searchPos);
                }
            }
            else if (BFConfig.INSTANCE.souls && (state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL)) && level.isEmptyBlock(searchPos.above()))
            {
                SoulParticle particle = new SoulParticle(level, searchPos.getX() + 0.5D, searchPos.getY() + 1.0D, searchPos.getZ() + 0.5D);
                Helpers.addParticle(particle, getTextures(ParticleLocation.SOUL, atlas));
                getTextures(ParticleLocation.SOUL_TRAIL, atlas);
            }
        }
    }

    public static List<TextureAtlasSprite> getTextures(ParticleLocation location, @Nullable TextureAtlas atlas)
    {
        if (atlas == null)
            return SPRITE_CACHE.get(location);
        return SPRITE_CACHE.computeIfAbsent(location, s -> location.getResourceLocations().stream().map(atlas::getSprite).toList());
    }
}
