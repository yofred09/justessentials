package yofred.dev.justessentials;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

@EventBusSubscriber(modid = JustEssentials.MODID)
public final class EssentialsEvents {
    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) { EssentialsCommands.register(event.getDispatcher()); }
    @SubscribeEvent
    public static void permissions(PermissionGatherEvent.Nodes event) { EssentialsPermissions.register(event); }
    @SubscribeEvent
    public static void damage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerState.isGod(player)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) PlayerState.applyFlight(player);
    }
    private EssentialsEvents() {}
}
