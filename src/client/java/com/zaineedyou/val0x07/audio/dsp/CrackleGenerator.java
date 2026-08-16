package com.zaineedyou.val0x07.audio.dsp;

/** Burst static pendek yang dipicu secara probabilistik, menggunakan unit sampel sebagai waktu internal. */
public final class CrackleGenerator {
    private final NoiseGenerator noise = new NoiseGenerator();
    private int randomState = 0x2468ACE1;
    private int samplesRemaining;

    public float next(float sampleRate, float burstsPerMinute, float durationMs, float amplitude) {
        if (burstsPerMinute <= 0.0F || amplitude <= 0.0F) {
            return 0.0F;
        }
        if (samplesRemaining <= 0) {
            float startProbability = burstsPerMinute / (60.0F * sampleRate);
            if (nextUnit() < startProbability) {
                samplesRemaining = Math.max(1, Math.round(sampleRate * durationMs / 1000.0F));
            }
        }
        if (samplesRemaining <= 0) {
            return 0.0F;
        }
        samplesRemaining--;
        // Amplop singkat mengurangi klik kasar pada tepi burst.
        return noise.next(amplitude) * 0.75F;
    }

    private float nextUnit() {
        randomState = randomState * 1664525 + 1013904223;
        return (randomState >>> 8) / 16777216.0F;
    }
}
