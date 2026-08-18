package com.zaineedyou.val0x07.keybind;

import com.zaineedyou.val0x07.preset.PresetManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class ToggleKeybind {
    private static KeyBinding keyBinding;

    private ToggleKeybind() {
    }

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.val0x07.toggle", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), Val0x07KeybindCategory.CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                PresetManager.getInstance().toggleEnabled();
            }
        });
    }
}
