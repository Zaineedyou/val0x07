package com.zaineedyou.val0x07.preset;

import com.zaineedyou.val0x07.config.Val0x07Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Akses tersinkronisasi ke konfigurasi dan preset agar aman bagi callback audio. */
public final class PresetManager {
    public static final String NONE = "none";
    private static final PresetManager INSTANCE = new PresetManager();

    private final Map<String, VoicePreset> fixedPresets = new LinkedHashMap<>();
    private Val0x07Config config;
    private volatile VoicePreset activeSnapshot;
    private volatile boolean enabledSnapshot;

    private PresetManager() {
        fixedPresets.put(WarRadioPreset.ID, WarRadioPreset.create());
        fixedPresets.put(FemaleVoicePreset.ID, FemaleVoicePreset.create());
        config = Val0x07Config.load();
        refreshSnapshot();
    }

    public static PresetManager getInstance() {
        return INSTANCE;
    }

    public boolean isEnabled() {
        return enabledSnapshot && activeSnapshot != null;
    }

    public VoicePreset getActivePreset() {
        VoicePreset preset = activeSnapshot;
        return preset == null ? null : preset.copy();
    }

    public String getActivePresetId() {
        return config.activePresetId;
    }

    public boolean isEffectEnabled() {
        return enabledSnapshot;
    }

    public synchronized void setEnabled(boolean enabled) {
        config.enabled = enabled;
        refreshSnapshot();
        config.save();
    }

    public synchronized void toggleEnabled() {
        setEnabled(!config.enabled);
    }

    public synchronized void selectPreset(String id) {
        if (id == null || id.equals(NONE)) {
            config.activePresetId = NONE;
        } else if (fixedPresets.containsKey(id) || config.customPresets.containsKey(id)) {
            config.activePresetId = id;
        }
        refreshSnapshot();
        config.save();
    }

    public synchronized List<VoicePreset> getSelectablePresets() {
        List<VoicePreset> presets = new ArrayList<>();
        presets.add(new VoicePreset(NONE, "None (Off)", false, new VoicePreset.Settings()));
        fixedPresets.values().forEach(preset -> presets.add(preset.copy()));
        config.customPresets.values().forEach(preset -> presets.add(preset.copy()));
        return presets;
    }

    public synchronized VoicePreset createCustom(String requestedName) {
        String displayName = requestedName == null || requestedName.isBlank() ? "Custom Preset" : requestedName.trim();
        String baseId = normalizeId(displayName);
        String id = baseId;
        int suffix = 2;
        while (fixedPresets.containsKey(id) || config.customPresets.containsKey(id)) {
            id = baseId + "_" + suffix++;
        }
        VoicePreset preset = new VoicePreset(id, displayName, true, new VoicePreset.Settings());
        config.customPresets.put(id, preset);
        config.activePresetId = id;
        refreshSnapshot();
        config.save();
        return preset.copy();
    }

    public synchronized VoicePreset getPreset(String id) {
        VoicePreset preset = fixedPresets.get(id);
        if (preset == null) {
            preset = config.customPresets.get(id);
        }
        return preset == null ? null : preset.copy();
    }

    public synchronized void saveCustom(VoicePreset preset) {
        if (preset == null || preset.id == null || !config.customPresets.containsKey(preset.id)) {
            return;
        }
        preset.custom = true;
        preset.settings.sanitize();
        config.customPresets.put(preset.id, preset.copy());
        refreshSnapshot();
        config.save();
    }

    public synchronized boolean deleteCustom(String id) {
        if (id == null || config.customPresets.remove(id) == null) {
            return false;
        }
        if (id.equals(config.activePresetId)) {
            config.activePresetId = NONE;
        }
        refreshSnapshot();
        config.save();
        return true;
    }

    private void refreshSnapshot() {
        VoicePreset selected = fixedPresets.get(config.activePresetId);
        if (selected == null) {
            selected = config.customPresets.get(config.activePresetId);
        }
        activeSnapshot = selected == null ? null : selected.copy();
        enabledSnapshot = config.enabled;
    }

    private static String normalizeId(String input) {
        String id = input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        id = id.replaceAll("^_+|_+$", "");
        return id.isBlank() ? "custom_preset" : id;
    }
}
