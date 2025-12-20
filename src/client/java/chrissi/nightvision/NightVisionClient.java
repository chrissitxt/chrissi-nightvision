package chrissi.nightvision;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightVisionClient implements ClientModInitializer {
    public static final String MOD_ID = "chrissi-nightvision";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static NightVisionClient INSTANCE;

    private static final int COLOR_GREEN = 0x00FF00;
    private static final int COLOR_RED = 0xFF0000;

    private KeyMapping toggleKey;
    private boolean isEnabled = false;
    public static NightVisionConfig config;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        config = NightVisionConfig.load();

        // restore previous state if persistence is enabled
        if (config.persistState && config.lastEnabledState) {
            isEnabled = true;
        }

        // register keybind (default: V key)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chrissi-nightvision.toggle",
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.MISC
        ));

        // main tick loop - maintains effect and handles keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // ensure effect stays active when enabled
            if (isEnabled && !client.player.hasEffect(MobEffects.NIGHT_VISION)) {
                NightVisionHandler.setNightVision(client, true);
            }

            // remove effect if disabled but somehow still present
            if (!isEnabled && client.player.hasEffect(MobEffects.NIGHT_VISION)) {
                client.player.removeEffect(MobEffects.NIGHT_VISION);
            }

            // optional: reset on death
            if (client.player.isDeadOrDying() && isEnabled && config.resetOnDeath) {
                isEnabled = false;
            }

            // handle keybind press
            while (toggleKey.consumeClick()) {
                toggleNightVision(client);
            }
        });

        // save state on shutdown if persistence is enabled
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            if (config.persistState) {
                config.lastEnabledState = isEnabled;
                config.save();
            }
        });
    }

    private void toggleNightVision(Minecraft client) {
        isEnabled = !isEnabled;
        NightVisionHandler.setNightVision(client, isEnabled);

        // optional: play sound feedback
        if (config.playSound && client.player != null) {
            client.player.playSound(
                    isEnabled ? SoundEvents.NOTE_BLOCK_PLING.value() : SoundEvents.NOTE_BLOCK_BASS.value(),
                    0.3f,
                    isEnabled ? 2.0f : 0.5f
            );
        }

        // optional: show toggle message in actionbar
        if (config.showToggleMessage && client.player != null) {
            Component statusText = Component.literal(isEnabled ? "ON" : "OFF")
                    .withColor(isEnabled ? COLOR_GREEN : COLOR_RED);

            Component message = Component.literal("Night Vision: ").append(statusText);

            client.player.displayClientMessage(message, true);
        }
    }
}