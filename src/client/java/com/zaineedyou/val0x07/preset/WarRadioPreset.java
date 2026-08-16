package com.zaineedyou.val0x07.preset;

/** Parameter tetap untuk karakter transmisi radio medan perang. */
public final class WarRadioPreset {
    public static final String ID = "war_radio";

    private WarRadioPreset() {
    }

    public static VoicePreset create() {
        VoicePreset.Settings settings = new VoicePreset.Settings();
        settings.bandpassEnabled = true;
        settings.lowCutHz = 300.0F;
        settings.highCutHz = 3400.0F;
        settings.distortionEnabled = true;
        settings.distortionDrive = 2.2F;
        settings.distortionThreshold = 0.82F;
        settings.noiseEnabled = true;
        settings.noiseAmplitude = 0.018F;
        settings.crackleEnabled = true;
        settings.crackleBurstsPerMinute = 18.0F; // rata-rata satu burst tiap 2–5 detik
        settings.crackleDurationMs = 95.0F;
        settings.crackleAmplitude = 0.16F;
        settings.pitchSemitones = -0.65F;
        settings.formantShift = 0.0F;
        return new VoicePreset(ID, "War Radio", false, settings);
    }
}
