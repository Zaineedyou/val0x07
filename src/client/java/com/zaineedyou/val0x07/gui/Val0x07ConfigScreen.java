package com.zaineedyou.val0x07.gui;

import com.zaineedyou.val0x07.preset.PresetManager;
import com.zaineedyou.val0x07.preset.VoicePreset;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Layar konfigurasi independen; tidak menyuntikkan antarmuka konfigurasi SVC. */
public final class Val0x07ConfigScreen extends Screen {
    private final Screen parent;
    private final PresetManager presets = PresetManager.getInstance();
    private VoicePreset editing;
    private List<VoicePreset> selectable;
    private int selectedIndex;

    public Val0x07ConfigScreen(Screen parent) {
        super(Text.literal("Val0x07 Voice Changer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        selectable = presets.getSelectablePresets();
        selectedIndex = indexFor(presets.getActivePresetId());
        VoicePreset active = presets.getActivePreset();
        editing = active != null && active.custom ? active : null;

        int center = width / 2;
        int y = 38;
        addDrawableChild(ButtonWidget.builder(Text.literal(currentPresetLabel()), button -> cyclePreset())
                .dimensions(center - 105, y, 210, 20).build());
        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("Effects: " + (presets.isEffectEnabled() ? "ON" : "OFF")), button -> {
                    presets.toggleEnabled();
                    clearAndInit();
                }).dimensions(center - 105, y, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Create Custom"), button -> {
                    editing = presets.createCustom("Custom Preset");
                    clearAndInit();
                }).dimensions(center + 5, y, 100, 20).build());
        y += 29;

        if (editing == null) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Select or create a custom preset to edit"), button -> { })
                    .dimensions(center - 145, y, 290, 20).build()).active = false;
            return;
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> {
                    presets.saveCustom(editing);
                    clearAndInit();
                }).dimensions(center - 155, y, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Load"), button -> {
                    editing = presets.getPreset(editing.id);
                    clearAndInit();
                }).dimensions(center - 49, y, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), button -> {
                    presets.deleteCustom(editing.id);
                    editing = null;
                    clearAndInit();
                }).dimensions(center + 57, y, 98, 20).build());
        y += 28;

        VoicePreset.Settings s = editing.settings;
        addToggle(center - 155, y, "Bandpass", () -> s.bandpassEnabled, value -> s.bandpassEnabled = value);
        addSlider(center - 45, y, 200, "Low-cut", s.lowCutHz, 80.0F, 2000.0F, value -> s.lowCutHz = value, " Hz");
        y += 22;
        addSlider(center - 155, y, 200, "High-cut", s.highCutHz, 500.0F, 8000.0F, value -> s.highCutHz = Math.max(value, s.lowCutHz + 40.0F), " Hz");
        addToggle(center + 55, y, "Distortion", () -> s.distortionEnabled, value -> s.distortionEnabled = value);
        y += 22;
        addSlider(center - 155, y, 200, "Drive", s.distortionDrive, 1.0F, 12.0F, value -> s.distortionDrive = value, "x");
        addToggle(center + 55, y, "Static", () -> s.noiseEnabled, value -> s.noiseEnabled = value);
        y += 22;
        addSlider(center - 155, y, 200, "Noise", s.noiseAmplitude, 0.0F, 0.15F, value -> s.noiseAmplitude = value, "");
        addToggle(center + 55, y, "Crackle", () -> s.crackleEnabled, value -> s.crackleEnabled = value);
        y += 22;
        addSlider(center - 155, y, 200, "Crackle/min", s.crackleBurstsPerMinute, 0.0F, 40.0F, value -> s.crackleBurstsPerMinute = value, "");
        addSlider(center + 55, y, 100, "Duration", s.crackleDurationMs, 20.0F, 300.0F, value -> s.crackleDurationMs = value, " ms");
        y += 22;
        addSlider(center - 155, y, 200, "Pitch", s.pitchSemitones, -6.0F, 6.0F, value -> s.pitchSemitones = value, " st");
        addSlider(center + 55, y, 100, "Formant", s.formantShift, -1.0F, 1.0F, value -> s.formantShift = value, "");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFF);
        if (editing != null) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Editing: " + editing.displayName + " (save to persist changes)"), width / 2, height - 26, 0xAAAAAA);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    private void cyclePreset() {
        if (selectable.isEmpty()) {
            return;
        }
        selectedIndex = (selectedIndex + 1) % selectable.size();
        presets.selectPreset(selectable.get(selectedIndex).id);
        clearAndInit();
    }

    private int indexFor(String id) {
        for (int i = 0; i < selectable.size(); i++) {
            if (selectable.get(i).id.equals(id)) {
                return i;
            }
        }
        return 0;
    }

    private String currentPresetLabel() {
        if (selectable.isEmpty()) {
            return "Preset: None";
        }
        return "Preset: " + selectable.get(selectedIndex).displayName + "  (click to change)";
    }

    private void addToggle(int x, int y, String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label + ": " + (getter.get() ? "ON" : "OFF")), button -> {
                    setter.accept(!getter.get());
                    clearAndInit();
                }).dimensions(x, y, 100, 20).build());
    }

    private void addSlider(int x, int y, int widgetWidth, String label, float initial, float minimum, float maximum,
                           Consumer<Float> setter, String suffix) {
        double normalized = (initial - minimum) / (maximum - minimum);
        addDrawableChild(new SettingSlider(x, y, widgetWidth, 20, label, normalized, minimum, maximum, setter, suffix));
    }

    private static final class SettingSlider extends SliderWidget {
        private final String label;
        private final float minimum;
        private final float maximum;
        private final Consumer<Float> setter;
        private final String suffix;

        private SettingSlider(int x, int y, int width, int height, String label, double value,
                              float minimum, float maximum, Consumer<Float> setter, String suffix) {
            super(x, y, width, height, Text.empty(), Math.max(0.0D, Math.min(1.0D, value)));
            this.label = label;
            this.minimum = minimum;
            this.maximum = maximum;
            this.setter = setter;
            this.suffix = suffix;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float actual = getActual();
            setMessage(Text.literal(label + ": " + String.format(Locale.ROOT, "%.2f", actual) + suffix));
        }

        @Override
        protected void applyValue() {
            setter.accept(getActual());
        }

        private float getActual() {
            return minimum + (float) value * (maximum - minimum);
        }
    }
}
