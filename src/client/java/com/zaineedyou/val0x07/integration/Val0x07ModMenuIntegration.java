package com.zaineedyou.val0x07.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.zaineedyou.val0x07.gui.Val0x07ConfigScreen;

/**
 * Entrypoint opsional Mod Menu. Kelas ini hanya dimuat oleh Mod Menu ketika
 * mod tersebut tersedia; Val0x07 tetap berjalan normal tanpa Mod Menu.
 */
public final class Val0x07ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Val0x07ConfigScreen::new;
    }
}
