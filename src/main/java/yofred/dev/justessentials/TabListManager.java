package yofred.dev.justessentials;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Builds a per-viewer tab header/footer so vanish visibility is respected. */
final class TabListManager {
    private static int ticks;

    static void tick(MinecraftServer server) {
        if (!EssentialsConfig.TAB_LIST.get()) return;
        if (++ticks < EssentialsConfig.TAB_REFRESH_TICKS.get()) return;
        ticks = 0;
        int online = server.getPlayerCount();
        int max = server.getMaxPlayers();
        for (ServerPlayer viewer : server.getPlayerList().getPlayers()) {
            int visible = 0;
            for (ServerPlayer target : server.getPlayerList().getPlayers()) {
                if (JustVanishCompat.canSee(viewer, target)) visible++;
            }
            int vanished = Math.max(0, online - visible);
            Component header = format(EssentialsConfig.TAB_HEADER.get(), viewer, online, max, visible, vanished);
            Component footer = format(EssentialsConfig.TAB_FOOTER.get(), viewer, online, max, visible, vanished);
            viewer.connection.send(new ClientboundTabListPacket(header, footer));
        }
    }

    private static Component format(String template, ServerPlayer viewer, int online, int max, int visible, int vanished) {
        return Messages.message(template
                .replace("{player}", viewer.getGameProfile().getName())
                .replace("{online}", Integer.toString(online))
                .replace("{max}", Integer.toString(max))
                .replace("{visible}", Integer.toString(visible))
                .replace("{vanished}", Integer.toString(vanished)));
    }

    private TabListManager() {}
}
