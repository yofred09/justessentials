package yofred.dev.justessentials;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

final class TabBossBar {
    private static final Map<UUID, ServerBossEvent> BARS = new ConcurrentHashMap<>();

    static void update(ServerPlayer player, Component text) {
        if (!EssentialsConfig.TAB_BOSSBAR.get()) {
            remove(player);
            return;
        }
        ServerBossEvent bar = BARS.computeIfAbsent(player.getUUID(), id -> {
            ServerBossEvent created = new ServerBossEvent(text, color(), overlay());
            created.addPlayer(player);
            return created;
        });
        bar.setName(text);
        bar.setColor(color());
        bar.setOverlay(overlay());
        bar.setProgress(EssentialsConfig.TAB_BOSSBAR_PROGRESS.get().floatValue());
        if (!bar.getPlayers().contains(player)) bar.addPlayer(player);
    }

    static void remove(ServerPlayer player) {
        ServerBossEvent bar = BARS.remove(player.getUUID());
        if (bar != null) bar.removeAllPlayers();
    }

    private static BossEvent.BossBarColor color() {
        try { return BossEvent.BossBarColor.valueOf(EssentialsConfig.TAB_BOSSBAR_COLOR.get().toUpperCase()); }
        catch (IllegalArgumentException ignored) { return BossEvent.BossBarColor.PURPLE; }
    }

    private static BossEvent.BossBarOverlay overlay() {
        try { return BossEvent.BossBarOverlay.valueOf(EssentialsConfig.TAB_BOSSBAR_STYLE.get().toUpperCase()); }
        catch (IllegalArgumentException ignored) { return BossEvent.BossBarOverlay.PROGRESS; }
    }

    private TabBossBar() {}
}
