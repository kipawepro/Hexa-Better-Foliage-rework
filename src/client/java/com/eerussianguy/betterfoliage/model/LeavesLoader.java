package com.eerussianguy.betterfoliage.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import com.eerussianguy.betterfoliage.Helpers;

public class LeavesLoader implements UnbakedModelDeserializer
{
    @Override
    public UnbakedModel deserialize(JsonObject json, JsonDeserializationContext context)
    {
        Identifier leaves   = Helpers.requireID(json, "leaves");
        Identifier fluff    = Helpers.requireID(json, "fluff");
        Identifier overlay  = Helpers.identifierOrEmpty(json, "overlay");
        boolean tintLeaves  = GsonHelper.getAsBoolean(json, "tintLeaves", true);
        boolean tintOverlay = GsonHelper.getAsBoolean(json, "tintOverlay", false);

        return new LeavesUnbakedModel(new LeavesModel(leaves, fluff, overlay, tintLeaves, tintOverlay));
    }
}
