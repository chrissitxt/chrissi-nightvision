package chrissi.nightvision;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NightVisionConfigScreen extends Screen {
    private final Screen parent;
    private final NightVisionConfig config;

    protected NightVisionConfigScreen(Screen parent) {
        super(Component.literal("Night Vision Config"));
        this.parent = parent;
        this.config = NightVisionClient.config;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 6;
        int spacing = 24;

        this.addRenderableWidget(new StringWidget(x, y, 200, 10,
                Component.literal("Presets"), this.font));
        y += 14;

        this.addRenderableWidget(Button.builder(Component.literal("Default"), button -> applyDefaultPreset())
                .bounds(x, y, 97, 20)
                .tooltip(Tooltip.create(Component.literal(
                        "Resets all settings below to their default values.")))
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Subtle"), button -> applySubtlePreset())
                .bounds(x + 103, y, 97, 20)
                .tooltip(Tooltip.create(Component.literal(
                        "Turns off the status icon and toggle message, keeps the sound.")))
                .build());
        y += spacing + 4;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.resetOnDeath)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Turns Night Vision off automatically when you die.")))
                .create(x, y, 200, 20, Component.literal("Reset on Death"),
                        (button, value) -> config.resetOnDeath = value));
        y += spacing;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.playSound)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Plays a sound whenever you toggle Night Vision.")))
                .create(x, y, 200, 20, Component.literal("Play Sound"),
                        (button, value) -> config.playSound = value));
        y += spacing;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.showStatusEffect)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Shows the Night Vision icon in your status effect list.")))
                .create(x, y, 200, 20, Component.literal("Show Status Effect Icon"),
                        (button, value) -> config.showStatusEffect = value));
        y += spacing;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.showToggleMessage)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Shows an on-screen message whenever you toggle Night Vision.")))
                .create(x, y, 200, 20, Component.literal("Show Toggle Message"),
                        (button, value) -> config.showToggleMessage = value));
        y += spacing;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.persistState)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Remembers whether Night Vision was on the next time you launch the game.")))
                .create(x, y, 200, 20, Component.literal("Persist State"),
                        (button, value) -> config.persistState = value));
        y += spacing;

        this.addRenderableWidget(CycleButton.onOffBuilder(config.autoToggleByTime)
                .withTooltip(value -> Tooltip.create(Component.literal(
                        "Automatically turns Night Vision on at night and off during the day. Overrides manual toggling while active.")))
                .create(x, y, 200, 20, Component.literal("Auto Toggle by Time"),
                        (button, value) -> config.autoToggleByTime = value));
        y += spacing;

        SoundVolumeSlider volumeSlider = new SoundVolumeSlider(x, y, 200, 20, config);
        volumeSlider.setTooltip(Tooltip.create(Component.literal(
                "Volume of the toggle sound. Only applies when Play Sound is on.")));
        this.addRenderableWidget(volumeSlider);
        y += spacing + 12;

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(x, y, 200, 20)
                .build());
    }

    private static class SoundVolumeSlider extends AbstractSliderButton {
        private final NightVisionConfig config;

        protected SoundVolumeSlider(int x, int y, int width, int height, NightVisionConfig config) {
            super(x, y, width, height, Component.literal(
                    "Sound Volume: " + Math.round(config.soundVolume * 100) + "%"), config.soundVolume);
            this.config = config;
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Sound Volume: " + Math.round(this.value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            config.soundVolume = (float) this.value;
        }
    }

    private void applyDefaultPreset() {
        config.resetOnDeath = false;
        config.playSound = false;
        config.showStatusEffect = true;
        config.showToggleMessage = true;
        config.persistState = true;
        config.autoToggleByTime = false;
        config.soundVolume = 0.3f;
        refresh();
    }

    private void applySubtlePreset() {
        config.playSound = true;
        config.showStatusEffect = false;
        config.showToggleMessage = false;
        refresh();
    }

    private void refresh() {
        this.minecraft.gui.setScreen(new NightVisionConfigScreen(parent));
    }

    @Override
    public void onClose() {
        config.save();
        this.minecraft.gui.setScreen(parent);
    }
}