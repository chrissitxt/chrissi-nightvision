package chrissi.nightvision;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NightVisionHandler {

    public static void setNightVision(Minecraft client, boolean enabled) {
        if (client.player == null) return;

        if (enabled) {
            boolean show = NightVisionClient.config != null && NightVisionClient.config.showStatusEffect;
            client.player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    -1,
                    0,
                    false,
                    show,
                    show
            ));
        } else {
            client.player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}