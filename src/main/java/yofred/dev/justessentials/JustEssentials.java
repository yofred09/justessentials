package yofred.dev.justessentials;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import yofred.dev.justcore.api.JustCoreApi;
import yofred.dev.justcore.api.JustModule;

@Mod(JustEssentials.MODID)
public final class JustEssentials {
    public static final String MODID = "justessentials";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustEssentials(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, EssentialsConfig.SPEC);
        JustCoreApi.registerModule(new JustModule(MODID, "Just Essentials", container.getModInfo().getVersion().toString()));
    }
}
