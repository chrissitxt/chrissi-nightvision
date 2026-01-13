package chrissi.nightvision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class NightVisionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("chrissi-nightvision.json");

    // user-configurable settings
    public boolean resetOnDeath = false;
    public boolean playSound = false;
    public boolean showStatusEffect = true;
    public boolean showToggleMessage = true;
    public boolean persistState = true;

    // internal state tracking
    public boolean lastEnabledState = false;

    public static NightVisionConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, NightVisionConfig.class);
            } catch (Exception e) {
                return createDefault();
            }
        }
        return createDefault();
    }

    private static NightVisionConfig createDefault() {
        NightVisionConfig config = new NightVisionConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (Exception e) {
            // silent fail
        }
    }
}
