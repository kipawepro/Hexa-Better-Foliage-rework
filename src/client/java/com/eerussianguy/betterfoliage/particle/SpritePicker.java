package com.eerussianguy.betterfoliage.particle;

import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

public class SpritePicker implements SpriteSet
{
    private List<TextureAtlasSprite> sprites;

    public SpritePicker() { }

    @Override
    public TextureAtlasSprite get(int index, int max)
    {
        return sprites.get(index * (sprites.size() - 1) / Math.max(max, 1));
    }

    @Override
    public TextureAtlasSprite get(RandomSource rand)
    {
        return sprites.get(rand.nextInt(sprites.size()));
    }

    @Override
    public TextureAtlasSprite first()
    {
        return sprites.get(0);
    }

    public void rebind(List<TextureAtlasSprite> spriteList)
    {
        sprites = ImmutableList.copyOf(spriteList);
    }
}
