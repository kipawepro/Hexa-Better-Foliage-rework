package com.eerussianguy.betterfoliage;

import java.util.List;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.betterfoliage.particle.SpritePicker;

import static com.eerussianguy.betterfoliage.BetterFoliageClient.RESOURCE_ID;

public class Helpers
{
    public static Identifier identifier(String name)
    {
        return Identifier.fromNamespaceAndPath(RESOURCE_ID, name);
    }

    public static Identifier identifier(String namespace, String name)
    {
        return Identifier.fromNamespaceAndPath(namespace, name);
    }

    public static final Identifier EMPTY = identifier("empty");

    public static Identifier requireID(JsonObject json, String member)
    {
        return Identifier.parse(GsonHelper.getAsString(json, member, EMPTY.toString()));
    }

    public static Identifier identifierOrEmpty(JsonObject json, String member)
    {
        if (!json.has(member)) return EMPTY;
        return Identifier.parse(json.get(member).getAsString());
    }

    public static float[] intervals(int n, float min, float max)
    {
        float[] f = new float[n];
        for (int i = 0; i < n; i++)
        {
            float t = (float) i / (n - 1);
            f[i] = min * t + max * (1 - t);
        }
        return f;
    }

    public static void addParticle(SingleQuadParticle particle, List<TextureAtlasSprite> sprites)
    {
        if (sprites == null || sprites.isEmpty()) return;

        SpritePicker picker = new SpritePicker();
        picker.rebind(sprites);

        particle.setSpriteFromAge(picker);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    static void addTintedParticle(SingleQuadParticle particle, List<TextureAtlasSprite> sprites, BlockState state, ClientLevel level, BlockPos pos)
    {
        if (sprites == null || sprites.isEmpty()) return;

        SpritePicker picker = new SpritePicker();
        picker.rebind(sprites);

        int color = BiomeColors.getAverageFoliageColor(level, pos);

        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;

        particle.setSpriteFromAge(picker);
        particle.setColor(r, g, b);
        Minecraft.getInstance().particleEngine.add(particle);
    }
}
