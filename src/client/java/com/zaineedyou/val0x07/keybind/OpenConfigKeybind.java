package com.zaineedyou.val0x07.keybind;

import com.zaineedyou.val0x07.gui.Val0x07ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class OpenConfigKeybind {
    private static KeyBinding keyBinding;

    private OpenConfigKeybind() {
    }

    public static void register() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.val0x07.open_config", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_RIGHT_SHIFT, Val0x07KeybindCategory.CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new Val0x07ConfigScreen(null));
                }
            }
        });
    }
}
