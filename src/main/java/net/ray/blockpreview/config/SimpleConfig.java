package net.ray.blockpreview.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "blockpreview-config.json";

    private static ConfigData config = new ConfigData();

    public static class ConfigData {
        public boolean allBlocks = true;
    }

    public static void load() {
        try {
            Path configPath = getConfigPath();
            if (Files.exists(configPath)) {
                try (FileReader reader = new FileReader(configPath.toFile())) {
                    config = GSON.fromJson(reader, ConfigData.class);
                }
            } else {
                save();
            }
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Path configPath = getConfigPath();
            Files.createDirectories(configPath.getParent());
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    private static Path getConfigPath() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config")
                .resolve(CONFIG_FILE_NAME);
    }

    public static boolean showAllBlocks() {
        return config.allBlocks;
    }

    public static void toggleBlockFilter() {
        config.allBlocks = !config.allBlocks;
        save();
    }
}