package com.zaineedyou.val0x07.audio.dsp;

/** Band-pass ringan yang merangkai high-pass dan low-pass biquad Butterworth. */
public final class BandpassFilter {
    private static final float Q = 0.70710678F;
    private final Biquad highPass = new Biquad();
    private final Biquad lowPass = new Biquad();
    private float configuredSampleRate = -1.0F;
    private float configuredLow = -1.0F;
    private float configuredHigh = -1.0F;

    public void configure(float sampleRate, float lowCutHz, float highCutHz) {
        float nyquistSafe = Math.max(100.0F, sampleRate * 0.45F);
        lowCutHz = clamp(lowCutHz, 20.0F, nyquistSafe - 20.0F);
        highCutHz = clamp(highCutHz, lowCutHz + 20.0F, nyquistSafe);
        if (sampleRate == configuredSampleRate && lowCutHz == configuredLow && highCutHz == configuredHigh) {
            return;
        }
        highPass.configureHighPass(sampleRate, lowCutHz, Q);
        lowPass.configureLowPass(sampleRate, highCutHz, Q);
        configuredSampleRate = sampleRate;
        configuredLow = lowCutHz;
        configuredHigh = highCutHz;
    }

    public float process(float input) {
        return lowPass.process(highPass.process(input));
    }

    public void reset() {
        highPass.reset();
        lowPass.reset();
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class Biquad {
        private float b0 = 1.0F;
        private float b1;
        private float b2;
        private float a1;
        private float a2;
        private float x1;
        private float x2;
        private float y1;
        private float y2;

        private void configureLowPass(float sampleRate, float cutoff, float q) {
            float omega = (float) (2.0D * Math.PI * cutoff / sampleRate);
            float cos = (float) Math.cos(omega);
            float alpha = (float) Math.sin(omega) / (2.0F * q);
            set((1.0F - cos) * 0.5F, 1.0F - cos, (1.0F - cos) * 0.5F,
                    1.0F + alpha, -2.0F * cos, 1.0F - alpha);
        }

        private void configureHighPass(float sampleRate, float cutoff, float q) {
            float omega = (float) (2.0D * Math.PI * cutoff / sampleRate);
            float cos = (float) Math.cos(omega);
            float alpha = (float) Math.sin(omega) / (2.0F * q);
            set((1.0F + cos) * 0.5F, -(1.0F + cos), (1.0F + cos) * 0.5F,
                    1.0F + alpha, -2.0F * cos, 1.0F - alpha);
        }

        private void set(float newB0, float newB1, float newB2, float newA0, float newA1, float newA2) {
            b0 = newB0 / newA0;
            b1 = newB1 / newA0;
            b2 = newB2 / newA0;
            a1 = newA1 / newA0;
            a2 = newA2 / newA0;
        }

        private float process(float input) {
            float output = b0 * input + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
            x2 = x1;
            x1 = input;
            y2 = y1;
            y1 = output;
            return output;
        }

        private void reset() {
            x1 = x2 = y1 = y2 = 0.0F;
        }
    }
}
