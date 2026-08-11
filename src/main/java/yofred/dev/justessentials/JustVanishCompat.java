package yofred.dev.justessentials;

import java.lang.reflect.Method;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

final class JustVanishCompat {
    private static final String API = "yofred.dev.justvanish.api.JustVanishApi";
    private static final String PREVIOUS = "StaffPreviousVanish";

    static void enterStaffMode(ServerPlayer player) {
        if (!available() || !EssentialsConfig.JUST_VANISH_STAFF_MODE.get()) return;
        Boolean previous = isVanished(player);
        if (previous == null) return;
        PlayerState.setIntegrationFlag(player, PREVIOUS, previous);
        setVanished(player, true);
    }

    static void leaveStaffMode(ServerPlayer player) {
        if (!available() || !EssentialsConfig.JUST_VANISH_STAFF_MODE.get()) return;
        setVanished(player, PlayerState.integrationFlag(player, PREVIOUS));
        PlayerState.removeIntegrationFlag(player, PREVIOUS);
    }

    static boolean available() { return ModList.get().isLoaded("justvanish"); }
    static boolean canSee(ServerPlayer viewer, ServerPlayer target) {
        if (!available()) return true;
        try {
            return (Boolean) Class.forName(API)
                    .getMethod("canSee", ServerPlayer.class, net.minecraft.world.entity.player.Player.class)
                    .invoke(null, viewer, target);
        } catch (ReflectiveOperationException exception) {
            JustEssentials.LOGGER.error("Just Vanish API is present but tab visibility could not be queried", exception);
            return true;
        }
    }
    static boolean isVanishedForTab(ServerPlayer player) {
        Boolean vanished = isVanished(player);
        return vanished != null && vanished;
    }
    private static Boolean isVanished(ServerPlayer player) {
        try { Method method = Class.forName(API).getMethod("isVanished", net.minecraft.world.entity.player.Player.class); return (Boolean) method.invoke(null, player); }
        catch (ReflectiveOperationException exception) { JustEssentials.LOGGER.error("Just Vanish API is present but could not be queried", exception); return null; }
    }
    private static void setVanished(ServerPlayer player, boolean vanished) {
        try { Class.forName(API).getMethod("setVanished", ServerPlayer.class, boolean.class).invoke(null, player, vanished); }
        catch (ReflectiveOperationException exception) { JustEssentials.LOGGER.error("Just Vanish API is present but staff-mode vanish could not be changed", exception); }
    }
    private JustVanishCompat() {}
}
