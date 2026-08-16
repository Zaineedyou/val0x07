package com.zaineedyou.val0x07.audio.dsp;

/**
 * Pitch shifter streaming ringan untuk frame PCM 16-bit. Algoritma memakai pembacaan
 * ulang circular buffer dengan interpolasi linear dan crossfade saat playhead diselaraskan.
 * Algoritma ini sengaja menghindari FFT besar agar layak untuk CPU perangkat mobile.
 */
public final class PitchShifter {
    private static final int BUFFER_SIZE = 8192;
    private static final int TARGET_DELAY = 1536;
    private static final int MIN_DELAY = 384;
    private static final int MAX_DELAY = 4096;
    private static final int CROSSFADE_SAMPLES = 128;

    private final float[] delayLine = new float[BUFFER_SIZE];
    private int writeIndex;
    private float readPosition;
    private float replacementReadPosition;
    private int crossfadeRemaining;
    private boolean initialized;

    public void process(float[] samples, float semitones) {
        if (Math.abs(semitones) < 0.01F) {
            return;
        }
        float ratio = (float) Math.pow(2.0D, semitones / 12.0D);
        ratio = Math.max(0.5F, Math.min(2.0F, ratio));
        for (int i = 0; i < samples.length; i++) {
            delayLine[writeIndex] = samples[i];
            writeIndex = advance(writeIndex, 1.0F);
            if (!initialized) {
                readPosition = wrap(writeIndex - TARGET_DELAY);
                initialized = true;
            }

            int delay = circularDistance(writeIndex, readPosition);
            if (crossfadeRemaining == 0 && (delay < MIN_DELAY || delay > MAX_DELAY)) {
                replacementReadPosition = wrap(writeIndex - TARGET_DELAY);
                crossfadeRemaining = CROSSFADE_SAMPLES;
            }

            float primary = interpolate(readPosition);
            readPosition = advance(readPosition, ratio);
            if (crossfadeRemaining > 0) {
                float replacement = interpolate(replacementReadPosition);
                replacementReadPosition = advance(replacementReadPosition, ratio);
                float alpha = 1.0F - crossfadeRemaining / (float) CROSSFADE_SAMPLES;
                samples[i] = primary * (1.0F - alpha) + replacement * alpha;
                crossfadeRemaining--;
                if (crossfadeRemaining == 0) {
                    readPosition = replacementReadPosition;
                }
            } else {
                samples[i] = primary;
            }
        }
    }

    public void reset() {
        writeIndex = 0;
        readPosition = 0.0F;
        replacementReadPosition = 0.0F;
        crossfadeRemaining = 0;
        initialized = false;
        java.util.Arrays.fill(delayLine, 0.0F);
    }

    private float interpolate(float position) {
        int left = (int) position;
        int right = (left + 1) % BUFFER_SIZE;
        float fraction = position - left;
        return delayLine[left] + (delayLine[right] - delayLine[left]) * fraction;
    }

    private int circularDistance(int newer, float older) {
        int oldIndex = (int) older;
        int distance = newer - oldIndex;
        return distance < 0 ? distance + BUFFER_SIZE : distance;
    }

    private float advance(float position, float amount) {
        return wrap(position + amount);
    }

    private int advance(int position, float amount) {
        return (int) wrap(position + amount);
    }

    private float wrap(float position) {
        while (position >= BUFFER_SIZE) {
            position -= BUFFER_SIZE;
        }
        while (position < 0.0F) {
            position += BUFFER_SIZE;
        }
        return position;
    }
}
