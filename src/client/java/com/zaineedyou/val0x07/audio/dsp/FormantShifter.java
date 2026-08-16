package com.zaineedyou.val0x07.audio.dsp;

/**
 * Pendekatan formant shift berbiaya rendah: energi dua pita resonansi vocal
 * dipetakan dari frekuensi sumber ke frekuensi target. Ini dipisahkan dari
 * pitch shift dan optimal untuk perubahan kecil yang natural.
 */
public final class FormantShifter {
    private final Resonator sourceLow = new Resonator();
    private final Resonator sourceHigh = new Resonator();
    private final Resonator targetLow = new Resonator();
    private final Resonator targetHigh = new Resonator();
    private float configuredSampleRate = -1.0F;
    private float configuredShift = Float.NaN;

    public void configure(float sampleRate, float shift) {
        shift = Math.max(-1.0F, Math.min(1.0F, shift));
        if (sampleRate == configuredSampleRate && shift == configuredShift) {
            return;
        }
        // 0.5 berarti sekitar ±30% perubahan pusat formant; default preset memakai 0.28.
        float multiplier = 1.0F + shift * 0.60F;
        sourceLow.configure(sampleRate, 650.0F, 1.4F);
        sourceHigh.configure(sampleRate, 1850.0F, 1.6F);
        targetLow.configure(sampleRate, clamp(650.0F * multiplier, 180.0F, 2800.0F), 1.4F);
        targetHigh.configure(sampleRate, clamp(1850.0F * multiplier, 700.0F, 5500.0F), 1.6F);
        configuredSampleRate = sampleRate;
        configuredShift = shift;
    }

    public float process(float input, float amount) {
        if (Math.abs(amount) < 0.01F) {
            return input;
        }
        float originalEnergy = sourceLow.process(input) + sourceHigh.process(input);
        float shiftedEnergy = targetLow.process(input) + targetHigh.process(input);
        // Mix konservatif menghindari resonansi berlebihan dan artefak robotic.
        return input + (shiftedEnergy - originalEnergy) * 0.72F;
    }

    public void reset() {
        sourceLow.reset();
        sourceHigh.reset();
        targetLow.reset();
        targetHigh.reset();
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static final class Resonator {
        private float b0;
        private float a1;
        private float a2;
        private float x1;
        private float x2;
        private float y1;
        private float y2;

        void configure(float sampleRate, float frequency, float q) {
            float omega = (float) (2.0D * Math.PI * frequency / sampleRate);
            float alpha = (float) Math.sin(omega) / (2.0F * q);
            float a0 = 1.0F + alpha;
            b0 = alpha / a0;
            a1 = (-2.0F * (float) Math.cos(omega)) / a0;
            a2 = (1.0F - alpha) / a0;
        }

        float process(float input) {
            float output = b0 * (input - x2) - a1 * y1 - a2 * y2;
            x2 = x1;
            x1 = input;
            y2 = y1;
            y1 = output;
            return output;
        }

        void reset() {
            x1 = x2 = y1 = y2 = 0.0F;
        }
    }
}
