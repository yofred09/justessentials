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
    public static final ModConfigSpec.BooleanValue HOMES = B.define("modules.homes", true);
    public static final ModConfigSpec.BooleanValue WARPS = B.define("modules.warps", true);
    public static final ModConfigSpec.BooleanValue TPA = B.define("modules.tpa", true);
    public static final ModConfigSpec.IntValue MAX_HOMES = B.defineInRange("travel.maxHomesPerPlayer", 5, 1, 100);
    public static final ModConfigSpec.IntValue TPA_TIMEOUT = B.defineInRange("travel.tpaTimeoutSeconds", 60, 10, 600);
    public static final ModConfigSpec.BooleanValue DISCORD_ENABLED = B.define("discord.enabled", false);
    public static final ModConfigSpec.ConfigValue<String> DISCORD_WEBHOOK_URL = B.define("discord.webhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_USERNAME = B.define("discord.username", "Just Essentials");
    public static final ModConfigSpec.BooleanValue DISCORD_STAFF_CHAT = B.define("discord.logStaffChat", false);
    public static final ModConfigSpec.ConfigValue<String> DISCORD_ACTIVITY_WEBHOOK = B.define("discord.channels.activityWebhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_JOIN_WEBHOOK = B.define("discord.channels.joinWebhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_LEAVE_WEBHOOK = B.define("discord.channels.leaveWebhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_MODERATION_WEBHOOK = B.define("discord.channels.moderationWebhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_INSPECTION_WEBHOOK = B.define("discord.channels.inspectionWebhookUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_STAFF_WEBHOOK = B.define("discord.channels.staffWebhookUrl", "");
    public static final ModConfigSpec.BooleanValue DISCORD_JOINS = B.define("discord.events.joins", true);
    public static final ModConfigSpec.BooleanValue DISCORD_LEAVES = B.define("discord.events.leaves", true);
    public static final ModConfigSpec.BooleanValue DISCORD_MODERATION = B.define("discord.events.moderation", true);
    public static final ModConfigSpec.BooleanValue DISCORD_INSPECTIONS = B.define("discord.events.inspections", true);
    public static final ModConfigSpec.BooleanValue DISCORD_STAFF_ACTIONS = B.define("discord.events.staffActions", true);
    public static final ModConfigSpec.ConfigValue<String> DISCORD_SERVER_NAME = B.define("discord.branding.serverName", "Minecraft Server");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_THUMBNAIL = B.define("discord.branding.thumbnailUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_IMAGE = B.define("discord.branding.imageUrl", "");
    public static final ModConfigSpec.ConfigValue<String> DISCORD_FOOTER = B.define("discord.branding.footerText", "Just Essentials");
    public static final ModConfigSpec.BooleanValue DISCORD_PLAYER_AVATARS = B.define("discord.branding.usePlayerAvatars", true);
    public static final ModConfigSpec.IntValue DISCORD_JOIN_COLOR = B.defineInRange("discord.colors.join", 0x57F287, 0, 0xFFFFFF);
    public static final ModConfigSpec.IntValue DISCORD_LEAVE_COLOR = B.defineInRange("discord.colors.leave", 0xFEE75C, 0, 0xFFFFFF);
    public static final ModConfigSpec.IntValue DISCORD_MODERATION_COLOR = B.defineInRange("discord.colors.moderation", 0xED4245, 0, 0xFFFFFF);
    public static final ModConfigSpec.IntValue DISCORD_STAFF_COLOR = B.defineInRange("discord.colors.staff", 0x5865F2, 0, 0xFFFFFF);
    public static final ModConfigSpec.IntValue STAFF_LEVEL = B.defineInRange("fallbackStaffPermissionLevel", 2, 0, 4);
    public static final ModConfigSpec.IntValue ADMIN_LEVEL = B.defineInRange("fallbackAdminPermissionLevel", 3, 0, 4);
    public static final ModConfigSpec SPEC = B.build();
    private EssentialsConfig() {}
}
