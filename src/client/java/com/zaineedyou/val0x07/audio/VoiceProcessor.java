package com.zaineedyou.val0x07.audio;

import com.zaineedyou.val0x07.audio.dsp.BandpassFilter;
import com.zaineedyou.val0x07.audio.dsp.CrackleGenerator;
import com.zaineedyou.val0x07.audio.dsp.DistortionEffect;
import com.zaineedyou.val0x07.audio.dsp.FormantShifter;
import com.zaineedyou.val0x07.audio.dsp.NoiseGenerator;
import com.zaineedyou.val0x07.audio.dsp.PitchShifter;
import com.zaineedyou.val0x07.preset.VoicePreset;

/** Pemroses audio PCM 16-bit outgoing, dipanggil dari ClientSoundEvent SVC. */
public final class VoiceProcessor {
    // Simple Voice Chat mengirim frame PCM mono 48 kHz sebelum dikodekan Opus.
    public static final float SAMPLE_RATE = 48_000.0F;

    private final BandpassFilter bandpass = new BandpassFilter();
    private final DistortionEffect distortion = new DistortionEffect();
    private final NoiseGenerator noise = new NoiseGenerator();
    private final CrackleGenerator crackle = new CrackleGenerator();
    private final PitchShifter pitchShifter = new PitchShifter();
    private final FormantShifter formantShifter = new FormantShifter();
    private float[] working = new float[0];

    /**
     * Mengembalikan frame baru agar setRawAudio dapat mengganti data event tanpa
     * memodifikasi buffer yang mungkin dimiliki SVC.
     */
    public synchronized short[] process(short[] rawAudio, VoicePreset preset) {
        if (rawAudio == null || rawAudio.length == 0 || preset == null || preset.settings == null) {
            return rawAudio;
        }
        VoicePreset.Settings settings = preset.settings;
        settings.sanitize();
        ensureCapacity(rawAudio.length);

        for (int i = 0; i < rawAudio.length; i++) {
            float sample = rawAudio[i] / 32768.0F;
            if (settings.bandpassEnabled) {
                bandpass.configure(SAMPLE_RATE, settings.lowCutHz, settings.highCutHz);
                sample = bandpass.process(sample);
            }
            if (settings.distortionEnabled) {
                sample = distortion.process(sample, settings.distortionDrive, settings.distortionThreshold);
            }
            if (settings.noiseEnabled) {
                sample += noise.next(settings.noiseAmplitude);
            }
            if (settings.crackleEnabled) {
                sample += crackle.next(SAMPLE_RATE, settings.crackleBurstsPerMinute,
                        settings.crackleDurationMs, settings.crackleAmplitude);
            }
            working[i] = sample;
        }

        if (Math.abs(settings.pitchSemitones) >= 0.01F) {
            pitchShifter.process(working, settings.pitchSemitones);
        }
        if (Math.abs(settings.formantShift) >= 0.01F) {
            formantShifter.configure(SAMPLE_RATE, settings.formantShift);
            for (int i = 0; i < rawAudio.length; i++) {
                working[i] = formantShifter.process(working[i], settings.formantShift);
            }
        }

        short[] processed = new short[rawAudio.length];
        for (int i = 0; i < rawAudio.length; i++) {
            float clamped = Math.max(-1.0F, Math.min(1.0F, working[i]));
            processed[i] = (short) Math.round(clamped * 32767.0F);
        }
        return processed;
    }

    public synchronized void reset() {
        bandpass.reset();
        formantShifter.reset();
        pitchShifter.reset();
    }

    private void ensureCapacity(int requiredLength) {
        if (working.length < requiredLength) {
            working = new float[requiredLength];
        }
    }
}
