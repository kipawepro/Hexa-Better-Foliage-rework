package com.eerussianguy.betterfoliage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class BFConfig
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("betterfoliage.json");

    public static BFConfig INSTANCE = new BFConfig();

    public int particleAttempts = 2;
    public int particleDistance = 15;
    public boolean souls = true;
    public boolean leaves = true;
    public boolean snowballs = true;
    public int leavesCacheSize = 7;
    public double leavesVariationDistance = 2.75;
    public int extraGrassRarity = 2;

    public static void init()
    {
        if (Files.exists(CONFIG_PATH))
        {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH))
            {
                BFConfig loaded = GSON.fromJson(reader, BFConfig.class);
                if (loaded != null) INSTANCE = loaded;
            }
            catch (IOException e)
            {
                INSTANCE = new BFConfig();
            }
        }
        save();
    }

    public static void save()
    {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH))
        {
            GSON.toJson(INSTANCE, writer);
        }
        catch (IOException ignored) {}
    }
}
