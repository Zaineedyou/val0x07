package com.zaineedyou.val0x07.preset;

/** Parameter tetap yang sengaja subtil untuk timbre lebih tinggi tanpa efek chipmunk. */
public final class FemaleVoicePreset {
    public static final String ID = "female_voice";

    private FemaleVoicePreset() {
    }

    public static VoicePreset create() {
        VoicePreset.Settings settings = new VoicePreset.Settings();
        settings.pitchSemitones = 1.65F;
        settings.formantShift = 0.28F;
        // Efek lain secara eksplisit tetap nonaktif agar suara bersih.
        settings.bandpassEnabled = false;
        settings.distortionEnabled = false;
        settings.noiseEnabled = false;
        settings.crackleEnabled = false;
        return new VoicePreset(ID, "Female Voice", false, settings);
    }
}
