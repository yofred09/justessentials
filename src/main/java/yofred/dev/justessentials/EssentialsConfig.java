package yofred.dev.justessentials;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EssentialsConfig {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue STAFF_CHAT = B.define("modules.staffChat", true);
    public static final ModConfigSpec.BooleanValue SILENT_TELEPORT = B.define("modules.silentTeleport", true);
    public static final ModConfigSpec.BooleanValue HISTORY = B.define("modules.history", true);
    public static final ModConfigSpec.BooleanValue PUNISHMENTS = B.define("modules.punishments", true);
    public static final ModConfigSpec.BooleanValue PLAYER_UTILITIES = B.define("modules.playerUtilities", true);
    public static final ModConfigSpec.BooleanValue TELEPORT_UTILITIES = B.define("modules.teleportUtilities", true);
    public static final ModConfigSpec.BooleanValue INVENTORY_INSPECTION = B.define("modules.inventoryInspection", true);
    public static final ModConfigSpec.BooleanValue MODERATION = B.define("modules.moderation", true);
    public static final ModConfigSpec.BooleanValue STAFF_MODE = B.define("modules.staffMode", true);
    public static final ModConfigSpec.IntValue STAFF_LEVEL = B.defineInRange("fallbackStaffPermissionLevel", 2, 0, 4);
    public static final ModConfigSpec.IntValue ADMIN_LEVEL = B.defineInRange("fallbackAdminPermissionLevel", 3, 0, 4);
    public static final ModConfigSpec SPEC = B.build();
    private EssentialsConfig() {}
}
