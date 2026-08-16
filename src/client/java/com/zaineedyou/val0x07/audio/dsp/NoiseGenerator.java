package com.zaineedyou.val0x07.audio.dsp;

/** Generator white-noise deterministik ringan, aman dipanggil pada thread audio. */
public final class NoiseGenerator {
    private int state = 0x5F3759DF;

    public float next(float amplitude) {
        if (amplitude <= 0.0F) {
            return 0.0F;
        }
        state ^= state << 13;
        state ^= state >>> 17;
        state ^= state << 5;
        float unit = (state & 0x7FFFFFFF) / 1073741824.0F - 1.0F;
        return unit * amplitude;
    }
}
