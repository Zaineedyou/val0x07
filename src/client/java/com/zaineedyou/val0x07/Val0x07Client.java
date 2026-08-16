package com.zaineedyou.val0x07;

import com.zaineedyou.val0x07.keybind.OpenConfigKeybind;
import com.zaineedyou.val0x07.keybind.ToggleKeybind;
import com.zaineedyou.val0x07.preset.PresetManager;
import com.zaineedyou.val0x07.preset.VoicePreset;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Entrypoint client-side Val0x07. */
public final class Val0x07Client implements ClientModInitializer {
    public static final String MOD_ID = "val0x07";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        OpenConfigKeybind.register();
        ToggleKeybind.register();
        registerHud();

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            LOGGER.info("Mod Menu terdeteksi. Konfigurasi Val0x07 tetap tersedia melalui keybind Controls.");
        }
    }

    private void registerHud() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) {
                return;
            }
            PresetManager manager = PresetManager.getInstance();
            VoicePreset preset = manager.getActivePreset();
            String name = preset == null ? "None" : preset.displayName;
            boolean enabled = manager.isEnabled();
            int color = enabled ? 0x55FF7F : 0xFF6B6B;
            drawContext.drawTextWithShadow(client.textRenderer,
                    Text.literal("Val0x07: " + name + " [" + (enabled ? "ON" : "OFF") + "]"),
                    6, 6, color);
        });
    }
}
