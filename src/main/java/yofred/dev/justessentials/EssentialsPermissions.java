package yofred.dev.justessentials;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

public final class EssentialsPermissions {
    public static final PermissionNode<Boolean> STAFF_CHAT = node("staff.chat", false);
    public static final PermissionNode<Boolean> SILENT_TP = node("staff.teleport.silent", false);
    public static final PermissionNode<Boolean> HISTORY = node("staff.history", false);
    public static final PermissionNode<Boolean> KICK = node("punishment.kick", true);
    public static final PermissionNode<Boolean> BAN = node("punishment.ban", true);
    public static final PermissionNode<Boolean> HEAL = node("utility.heal", false);
    public static final PermissionNode<Boolean> FEED = node("utility.feed", false);
    public static final PermissionNode<Boolean> FLY = node("utility.fly", false);
    public static final PermissionNode<Boolean> GOD = node("utility.god", true);
    public static final PermissionNode<Boolean> SPAWN = node("teleport.spawn", false);
    public static final PermissionNode<Boolean> BACK = node("teleport.back", false);
    public static final PermissionNode<Boolean> INVSEE = node("staff.inventory", true);
    public static final PermissionNode<Boolean> ENDERSEE = node("staff.enderchest", true);
    public static final PermissionNode<Boolean> CURIOSSEE = node("staff.accessories", true);
    public static final PermissionNode<Boolean> MUTE = node("punishment.mute", true);
    public static final PermissionNode<Boolean> FREEZE = node("punishment.freeze", true);
    public static final PermissionNode<Boolean> STAFF_MODE = node("staff.mode", true);
    public static final PermissionNode<Boolean> DISCORD_TEST = node("admin.discord.test", true);
    public static final PermissionNode<Boolean> TEMP_PUNISH = node("punishment.temporary", true);
    public static final PermissionNode<Boolean> HOME = node("teleport.home", false);
    public static final PermissionNode<Boolean> WARP = node("teleport.warp", false);
    public static final PermissionNode<Boolean> WARP_ADMIN = node("teleport.warp.admin", true);
    public static final PermissionNode<Boolean> TPA = node("teleport.tpa", false);

    private static PermissionNode<Boolean> node(String path, boolean admin) {
        return new PermissionNode<>(JustEssentials.MODID, path, PermissionTypes.BOOLEAN,
                (player, id, contexts) -> player != null && player.hasPermissions(admin ? EssentialsConfig.ADMIN_LEVEL.get() : EssentialsConfig.STAFF_LEVEL.get()));
    }

    public static void register(PermissionGatherEvent.Nodes event) { event.addNodes(STAFF_CHAT, SILENT_TP, HISTORY, KICK, BAN, HEAL, FEED, FLY, GOD, SPAWN, BACK, INVSEE, ENDERSEE, CURIOSSEE, MUTE, FREEZE, STAFF_MODE, DISCORD_TEST, TEMP_PUNISH, HOME, WARP, WARP_ADMIN, TPA); }
    public static boolean has(CommandSourceStack source, PermissionNode<Boolean> node) {
        return !(source.getEntity() instanceof ServerPlayer player) || PermissionAPI.getPermission(player, node);
    }
    public static boolean has(ServerPlayer player, PermissionNode<Boolean> node) { return PermissionAPI.getPermission(player, node); }
    private EssentialsPermissions() {}
}
