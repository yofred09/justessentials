package yofred.dev.justessentials;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ActivityMessages {
    static void joined(ServerPlayer player) {
        if (!EssentialsConfig.JOIN_LEAVE_MESSAGES.get()) return;
        broadcast(player, EssentialsConfig.JOIN_MESSAGE.get(), EssentialsConfig.SHOW_JOIN_MESSAGE.get());
    }

    static void left(ServerPlayer player) {
        if (!EssentialsConfig.JOIN_LEAVE_MESSAGES.get()) return;
        broadcast(player, EssentialsConfig.LEAVE_MESSAGE.get(), EssentialsConfig.SHOW_LEAVE_MESSAGE.get());
    }

    private static void broadcast(ServerPlayer subject, String template, boolean showToRegularPlayers) {
        if (template.isBlank()) return;
        Component message = Messages.colored(template
                .replace("{player}", subject.getGameProfile().getName())
                .replace("{online}", Integer.toString(subject.server.getPlayerCount()))
                .replace("{max}", Integer.toString(subject.server.getMaxPlayers()))
                .replace("{world}", subject.level().dimension().location().toString()));
        for (ServerPlayer viewer : subject.server.getPlayerList().getPlayers()) {
            boolean staffSpy = EssentialsConfig.STAFF_ALWAYS_SEES_ACTIVITY.get()
                    && EssentialsPermissions.has(viewer, EssentialsPermissions.ACTIVITY_MESSAGES);
            boolean normallyVisible = showToRegularPlayers
                    && (viewer == subject || JustVanishCompat.canSee(viewer, subject));
            if (staffSpy || normallyVisible) viewer.sendSystemMessage(message);
        }
    }

    public static boolean replacesVanilla() { return EssentialsConfig.JOIN_LEAVE_MESSAGES.get(); }
    private ActivityMessages() {}
}
