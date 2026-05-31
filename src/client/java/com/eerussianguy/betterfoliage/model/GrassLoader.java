package com.eerussianguy.betterfoliage.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import com.eerussianguy.betterfoliage.Helpers;

public class GrassLoader implements UnbakedModelDeserializer
{
    @Override
    public UnbakedModel deserialize(JsonObject json, JsonDeserializationContext context)
    {
        Identifier dirt    = Helpers.requireID(json, "dirt");
        Identifier top     = Helpers.requireID(json, "top");
        Identifier overlay = Helpers.requireID(json, "overlay");
        boolean tint       = GsonHelper.getAsBoolean(json, "tint", false);
        Identifier grass   = Helpers.identifierOrEmpty(json, "grass");

        return new GrassUnbakedModel(new GrassModel(dirt, top, overlay, tint, grass));
    }
}
