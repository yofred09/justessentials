package yofred.dev.justessentials;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.EnumSet;

/** Builds a per-viewer tab header/footer so vanish visibility is respected. */
final class TabListManager {
    private static int ticks;
    private static boolean refreshing;

    static void tick(MinecraftServer server) {
        if (!EssentialsConfig.TAB_LIST.get()) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) TabBossBar.remove(player);
            return;
        }
        if (++ticks < EssentialsConfig.TAB_REFRESH_TICKS.get()) return;
        ticks = 0;
        int online = server.getPlayerCount();
        int max = server.getMaxPlayers();
        if (EssentialsConfig.TAB_NAME_FORMATTING.get()) {
            server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                    server.getPlayerList().getPlayers()));
        }
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            if (!enabledFor(viewer)) {
                clear(viewer);
                continue;
            }
            int visible = 0;
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                if (JustVanishCompat.canSee(viewer, target)) visible++;
            }
            int vanished = Math.max(0, online - visible);
            Component header = format(EssentialsConfig.TAB_HEADER.get(), viewer, online, max, visible, vanished);
            Component footer = format(EssentialsConfig.TAB_FOOTER.get(), viewer, online, max, visible, vanished);
            viewer.connection.send(new ClientboundTabListPacket(header, footer));
            TabBossBar.update(viewer, format(EssentialsConfig.TAB_BOSSBAR_TEXT.get(), viewer, online, max, visible, vanished));
        }
    }

    private static Component format(String template, ServerPlayer viewer, int online, int max, int visible, int vanished) {
        String world = viewer.level().dimension().location().toString();
        String formatted = template
                .replace("{player}", viewer.getGameProfile().getName())
                .replace("{online}", Integer.toString(online))
                .replace("{max}", Integer.toString(max))
                .replace("{visible}", Integer.toString(visible))
                .replace("{vanished}", Integer.toString(vanished))
                .replace("{world}", world)
                .replace("{ping}", Integer.toString(viewer.connection.latency()))
                .replace("{health}", String.format("%.1f", viewer.getHealth()))
                .replace("{food}", Integer.toString(viewer.getFoodData().getFoodLevel()))
                .replace("{x}", Integer.toString(viewer.blockPosition().getX()))
                .replace("{y}", Integer.toString(viewer.blockPosition().getY()))
                .replace("{z}", Integer.toString(viewer.blockPosition().getZ()))
                .replace("{tps}", String.format("%.1f", Math.min(20.0D, 1000.0D / Math.max(1.0D, viewer.server.getAverageTickTimeNanos() / 1_000_000.0D))))
                .replace("{mspt}", String.format("%.1f", viewer.server.getAverageTickTimeNanos() / 1_000_000.0D))
                .replace("{time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .replace("{date}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .replace("{uptime}", uptime())
                .replace("{animation:info}", animation(EssentialsConfig.TAB_ANIMATION_INFO.get()))
                .replace("{animation:bar}", animation(EssentialsConfig.TAB_ANIMATION_BAR.get()));
        return Messages.colored(formatted);
    }

    private static String animation(List<? extends String> frames) {
        if (frames.isEmpty()) return "";
        if (!EssentialsConfig.TAB_ANIMATIONS.get()) return frames.getFirst();
        long frame = System.currentTimeMillis() / EssentialsConfig.TAB_ANIMATION_INTERVAL.get();
        return frames.get((int) (frame % frames.size()));
    }

    private static String uptime() {
        long seconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L;
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
    }

    static Component formatPlayerName(ServerPlayer player) {
        String staff = EssentialsPermissions.has(player, EssentialsPermissions.STAFF_MODE) ? EssentialsConfig.TAB_STAFF_PREFIX.get() : "";
        String vanished = JustVanishCompat.isVanishedForTab(player) ? EssentialsConfig.TAB_VANISH_SUFFIX.get() : "";
        return Messages.colored(EssentialsConfig.TAB_NAME_FORMAT.get()
                .replace("{player}", player.getGameProfile().getName())
                .replace("{staff_prefix}", staff)
                .replace("{vanish_suffix}", vanished));
    }

    static void logout(ServerPlayer player) { TabBossBar.logout(player); }

    static void refreshNow(MinecraftServer server) {
        if (refreshing) return;
        refreshing = true;
        try { ticks = EssentialsConfig.TAB_REFRESH_TICKS.get(); tick(server); }
        finally { refreshing = false; }
    }

    static void clear(ServerPlayer player) {
        player.connection.send(new ClientboundTabListPacket(Component.empty(), Component.empty()));
        TabBossBar.remove(player);
    }

    private static boolean enabledFor(ServerPlayer player) {
        if (EssentialsConfig.TAB_ALLOW_PLAYER_TOGGLE.get() && !PlayerState.isCustomTabEnabled(player)) return false;
        String dimension = player.level().dimension().location().toString();
        return EssentialsConfig.TAB_DISABLED_WORLDS.get().stream().noneMatch(dimension::equalsIgnoreCase);
    }

    private TabListManager() {}
}
