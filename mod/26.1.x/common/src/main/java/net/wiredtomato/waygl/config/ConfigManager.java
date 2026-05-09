package net.wiredtomato.waygl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.wiredtomato.waygl.WayGL;
import net.wiredtomato.waygl.core.service.PlatformService;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformService.IMPL.getConfigDirectory().resolve("waygl.json");

    public static Config CONFIG = new Config();

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("waygl.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory glfw = builder.getOrCreateCategory(Component.translatable("waygl.config.category.glfw"));

        glfw.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("waygl.config.category.glfw.native"), CONFIG.useNativeGlfw)
                        .setDefaultValue(false)
                        .setSaveConsumer(value -> CONFIG.useNativeGlfw = value)
                        .build()
        );

        glfw.addEntry(
                entryBuilder.startStrField(Component.translatable("waygl.config.category.glfw.path"), CONFIG.nativeGlfwPath)
                        .setDefaultValue("")
                        .setSaveConsumer(value -> CONFIG.nativeGlfwPath = value)
                        .build()
        );

        builder.setSavingRunnable(ConfigManager::save);

        return builder.build();
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            CONFIG = GSON.fromJson(reader, Config.class);
        } catch (IOException e) {
            WayGL.LOGGER.error("Failed to read config from path: {}!", CONFIG_PATH, e);
        }

        save();
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(CONFIG, writer);
        } catch (IOException e) {
            WayGL.LOGGER.error("Could not save config to path: {}!", CONFIG_PATH, e);
        }
    }
}
