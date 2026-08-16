package com.zaineedyou.val0x07.integration;

import com.zaineedyou.val0x07.audio.VoiceProcessor;
import com.zaineedyou.val0x07.preset.PresetManager;
import com.zaineedyou.val0x07.preset.VoicePreset;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Plugin SVC resmi; tidak memakai mixin, refleksi, atau API internal Simple Voice Chat. */
public final class SvcPluginImpl implements VoicechatPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("val0x07");
    private final VoiceProcessor processor = new VoiceProcessor();

    @Override
    public String getPluginId() {
        return "val0x07";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientSoundEvent.class, event -> {
            PresetManager manager = PresetManager.getInstance();
            if (!manager.isEnabled()) {
                return;
            }
            VoicePreset preset = manager.getActivePreset();
            if (preset == null) {
                return;
            }
            try {
                event.setRawAudio(processor.process(event.getRawAudio(), preset));
            } catch (RuntimeException exception) {
                // Jangan mengganggu transmisi suara bila parameter atau perangkat mengalami masalah.
                LOGGER.error("Pemrosesan audio Val0x07 gagal untuk frame ini.", exception);
            }
        });
    }
}
