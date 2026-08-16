package com.zaineedyou.val0x07.audio.dsp;

/** Distorsi soft-clip tanpa alokasi memori pada jalur audio. */
public final class DistortionEffect {
    public float process(float sample, float drive, float threshold) {
        drive = Math.max(1.0F, drive);
        threshold = Math.max(0.05F, Math.min(1.0F, threshold));
        float driven = sample * drive;
        // Soft knee berbasis tanh menjaga transisi clipping tidak terlalu tajam.
        float normalized = (float) Math.tanh(driven / threshold);
        float scale = (float) Math.tanh(1.0F / threshold);
        return (normalized / scale) * threshold;
    }
}
