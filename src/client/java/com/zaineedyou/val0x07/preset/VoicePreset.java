package com.zaineedyou.val0x07.preset;

import java.util.Objects;

/**
 * Preset serializable yang menjelaskan urutan parameter DSP Val0x07.
 * Field dibiarkan public agar Gson dapat menyimpan konfigurasi tanpa adaptor tambahan.
 */
public final class VoicePreset {
    public String id;
    public String displayName;
    public boolean custom;
    public Settings settings;

    public VoicePreset() {
        this("custom", "Custom", true, new Settings());
    }

    public VoicePreset(String id, String displayName, boolean custom, Settings settings) {
        this.id = id;
        this.displayName = displayName;
        this.custom = custom;
        this.settings = settings;
    }

    public VoicePreset copy() {
        return new VoicePreset(id, displayName, custom, settings.copy());
    }

    public static final class Settings {
        public boolean bandpassEnabled = false;
        public float lowCutHz = 300.0F;
        public float highCutHz = 3400.0F;

        public boolean distortionEnabled = false;
        public float distortionDrive = 1.0F;
        public float distortionThreshold = 0.92F;

        public boolean noiseEnabled = false;
        public float noiseAmplitude = 0.0F;

        public boolean crackleEnabled = false;
        /** Jumlah burst rata-rata per menit. */
        public float crackleBurstsPerMinute = 0.0F;
        public float crackleDurationMs = 80.0F;
        public float crackleAmplitude = 0.25F;

        /** Rentang kecil dianjurkan, -6 sampai +6 semitone. */
        public float pitchSemitones = 0.0F;
        /** Pergeseran karakter resonansi, -1 sampai +1. */
        public float formantShift = 0.0F;

        public Settings copy() {
            Settings copy = new Settings();
            copy.bandpassEnabled = bandpassEnabled;
            copy.lowCutHz = lowCutHz;
            copy.highCutHz = highCutHz;
            copy.distortionEnabled = distortionEnabled;
            copy.distortionDrive = distortionDrive;
            copy.distortionThreshold = distortionThreshold;
            copy.noiseEnabled = noiseEnabled;
            copy.noiseAmplitude = noiseAmplitude;
            copy.crackleEnabled = crackleEnabled;
            copy.crackleBurstsPerMinute = crackleBurstsPerMinute;
            copy.crackleDurationMs = crackleDurationMs;
            copy.crackleAmplitude = crackleAmplitude;
            copy.pitchSemitones = pitchSemitones;
            copy.formantShift = formantShift;
            return copy;
        }

        public void sanitize() {
            lowCutHz = clamp(lowCutHz, 40.0F, 8000.0F);
            highCutHz = clamp(highCutHz, lowCutHz + 40.0F, 10000.0F);
            distortionDrive = clamp(distortionDrive, 1.0F, 20.0F);
            distortionThreshold = clamp(distortionThreshold, 0.1F, 1.0F);
            noiseAmplitude = clamp(noiseAmplitude, 0.0F, 0.5F);
            crackleBurstsPerMinute = clamp(crackleBurstsPerMinute, 0.0F, 60.0F);
            crackleDurationMs = clamp(crackleDurationMs, 10.0F, 500.0F);
            crackleAmplitude = clamp(crackleAmplitude, 0.0F, 1.0F);
            pitchSemitones = clamp(pitchSemitones, -12.0F, 12.0F);
            formantShift = clamp(formantShift, -1.0F, 1.0F);
        }

        private static float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    public boolean hasId(String candidate) {
        return Objects.equals(id, candidate);
    }
}
