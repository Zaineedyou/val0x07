package com.zaineedyou.val0x07.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaineedyou.val0x07.preset.VoicePreset;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Konfigurasi persisten di config/val0x07.json. */
public final class Val0x07Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("val0x07");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("val0x07.json");

    public String activePresetId = "none";
    public boolean enabled = true;
    public Map<String, VoicePreset> customPresets = new LinkedHashMap<>();

    public static Val0x07Config load() {
        if (!Files.exists(CONFIG_PATH)) {
            return new Val0x07Config();
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Val0x07Config loaded = GSON.fromJson(reader, Val0x07Config.class);
            if (loaded == null) {
                return new Val0x07Config();
            }
            if (loaded.customPresets == null) {
                loaded.customPresets = new LinkedHashMap<>();
            }
            loaded.sanitize();
            return loaded;
        } catch (Exception exception) {
            LOGGER.error("Tidak dapat membaca konfigurasi Val0x07; konfigurasi baru dipakai.", exception);
            return new Val0x07Config();
        }
    }

    public void save() {
        sanitize();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Tidak dapat menyimpan konfigurasi Val0x07.", exception);
        }
    }

    private void sanitize() {
        if (activePresetId == null || activePresetId.isBlank()) {
            activePresetId = "none";
        }
        if (customPresets == null) {
            customPresets = new LinkedHashMap<>();
            return;
        }
        customPresets.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null);
        customPresets.forEach((id, preset) -> {
            preset.id = id;
            preset.custom = true;
            if (preset.displayName == null || preset.displayName.isBlank()) {
                preset.displayName = id;
            }
            if (preset.settings == null) {
                preset.settings = new VoicePreset.Settings();
            }
            preset.settings.sanitize();
        });
    }
}
