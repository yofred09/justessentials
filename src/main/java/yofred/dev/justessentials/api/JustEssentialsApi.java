package yofred.dev.justessentials.api;

import net.minecraft.server.MinecraftServer;
import yofred.dev.justessentials.AuditLog;

public final class JustEssentialsApi {
    public static void audit(MinecraftServer server, String actor, String action, String target, String detail) { AuditLog.record(server, actor, action, target, detail); }
    private JustEssentialsApi() {}
}
