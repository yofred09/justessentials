package yofred.dev.justessentials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import net.minecraft.server.level.ServerPlayer;

final class DiscordWebhook {
    private enum Channel { GENERAL, JOIN, LEAVE, MODERATION, INSPECTION, STAFF }
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).followRedirects(HttpClient.Redirect.NEVER).build();

    static void publish(String actor, String action, String target, String detail) {
        if (!EssentialsConfig.DISCORD_ENABLED.get()) return;
        Channel channel = channel(action);
        if (!enabled(channel, action)) return;
        send(channel, "Just Essentials Audit", color(channel, action), actor, action, target, detail, null);
    }

    static void playerJoined(ServerPlayer player) {
        if (!EssentialsConfig.DISCORD_ENABLED.get() || !EssentialsConfig.DISCORD_JOINS.get()) return;
        String detail = "Online players: " + player.server.getPlayerCount() + "/" + player.server.getMaxPlayers();
        send(Channel.JOIN, "Player joined", EssentialsConfig.DISCORD_JOIN_COLOR.get(), "Server", "JOIN", player.getGameProfile().getName(), detail, avatar(player));
    }

    static void playerLeft(ServerPlayer player, Duration session) {
        if (!EssentialsConfig.DISCORD_ENABLED.get() || !EssentialsConfig.DISCORD_LEAVES.get()) return;
        long seconds = Math.max(0, session.toSeconds());
        String duration = seconds >= 3600 ? (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m" : (seconds / 60) + "m " + (seconds % 60) + "s";
        send(Channel.LEAVE, "Player left", EssentialsConfig.DISCORD_LEAVE_COLOR.get(), "Server", "LEAVE", player.getGameProfile().getName(), "Session: " + duration, avatar(player));
    }

    static String configurationError() {
        if (!EssentialsConfig.DISCORD_ENABLED.get()) return "Discord integration is disabled in the server config.";
        return webhookError(resolve(Channel.GENERAL));
    }

    private static boolean enabled(Channel channel, String action) {
        return switch (channel) {
            case MODERATION -> EssentialsConfig.DISCORD_MODERATION.get();
            case INSPECTION -> EssentialsConfig.DISCORD_INSPECTIONS.get();
            case STAFF -> !"STAFF_CHAT".equals(action) ? EssentialsConfig.DISCORD_STAFF_ACTIONS.get() : EssentialsConfig.DISCORD_STAFF_CHAT.get();
            default -> true;
        };
    }

    private static Channel channel(String action) {
        if (action.contains("BAN") || action.contains("KICK") || action.contains("MUTE") || action.contains("FREEZE")) return Channel.MODERATION;
        if (action.contains("INVSEE") || action.contains("ENDERSEE") || action.contains("CURIOSSEE")) return Channel.INSPECTION;
        if (action.contains("STAFF") || action.contains("SILENT_TELEPORT")) return Channel.STAFF;
        return Channel.GENERAL;
    }

    private static String resolve(Channel channel) {
        String specific = switch (channel) {
            case JOIN -> first(EssentialsConfig.DISCORD_JOIN_WEBHOOK.get(), EssentialsConfig.DISCORD_ACTIVITY_WEBHOOK.get());
            case LEAVE -> first(EssentialsConfig.DISCORD_LEAVE_WEBHOOK.get(), EssentialsConfig.DISCORD_ACTIVITY_WEBHOOK.get());
            case MODERATION -> EssentialsConfig.DISCORD_MODERATION_WEBHOOK.get();
            case INSPECTION -> EssentialsConfig.DISCORD_INSPECTION_WEBHOOK.get();
            case STAFF -> EssentialsConfig.DISCORD_STAFF_WEBHOOK.get();
            default -> "";
        };
        return specific.isBlank() ? EssentialsConfig.DISCORD_WEBHOOK_URL.get().trim() : specific.trim();
    }

    private static String first(String preferred, String fallback) { return preferred.isBlank() ? fallback : preferred; }

    private static void send(Channel channel, String title, int color, String actor, String action, String target, String detail, String playerAvatar) {
        String webhook = resolve(channel);
        String error = webhookError(webhook);
        if (error != null) {
            JustEssentials.LOGGER.warn("Discord {} delivery skipped: {}", channel.name().toLowerCase(Locale.ROOT), error);
            return;
        }
        JsonObject embed = new JsonObject();
        embed.addProperty("title", truncate(title, 256));
        embed.addProperty("description", "**" + truncate(EssentialsConfig.DISCORD_SERVER_NAME.get(), 200) + "**");
        embed.addProperty("color", color);
        embed.addProperty("timestamp", Instant.now().toString());
        JsonArray fields = new JsonArray();
        fields.add(field("Action", action, true));
        fields.add(field("Actor", actor, true));
        fields.add(field("Player / Target", target, true));
        fields.add(field("Details", detail, false));
        embed.add("fields", fields);
        String thumbnail = playerAvatar != null ? playerAvatar : EssentialsConfig.DISCORD_THUMBNAIL.get().trim();
        addImageObject(embed, "thumbnail", thumbnail);
        addImageObject(embed, "image", EssentialsConfig.DISCORD_IMAGE.get().trim());
        if (!EssentialsConfig.DISCORD_FOOTER.get().isBlank()) {
            JsonObject footer = new JsonObject();
            footer.addProperty("text", truncate(EssentialsConfig.DISCORD_FOOTER.get(), 2048));
            embed.add("footer", footer);
        }
        JsonObject payload = new JsonObject();
        payload.addProperty("username", truncate(EssentialsConfig.DISCORD_USERNAME.get(), 80));
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        payload.add("embeds", embeds);
        HttpRequest request = HttpRequest.newBuilder(URI.create(webhook)).timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString())).build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenAccept(response -> {
            if (response.statusCode() < 200 || response.statusCode() >= 300) JustEssentials.LOGGER.warn("Discord webhook returned HTTP {}", response.statusCode());
        }).exceptionally(failure -> { JustEssentials.LOGGER.warn("Unable to deliver Discord event: {}", failure.getMessage()); return null; });
    }

    private static String avatar(ServerPlayer player) {
        return EssentialsConfig.DISCORD_PLAYER_AVATARS.get() ? "https://mc-heads.net/avatar/" + player.getUUID() + "/128" : null;
    }
    private static String webhookError(String raw) {
        if (raw == null || raw.isBlank()) return "no webhook URL is configured for this channel or the general fallback.";
        try {
            URI uri = URI.create(raw);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            boolean official = host.equals("discord.com") || host.endsWith(".discord.com") || host.equals("discordapp.com") || host.endsWith(".discordapp.com");
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !official || !uri.getPath().startsWith("/api/webhooks/")) return "webhook must be an official HTTPS Discord webhook URL.";
        } catch (IllegalArgumentException exception) { return "webhook URL is invalid."; }
        return null;
    }
    private static void addImageObject(JsonObject embed, String key, String url) {
        if (url == null || url.isBlank()) return;
        try {
            URI uri = URI.create(url);
            if (!"https".equalsIgnoreCase(uri.getScheme())) return;
            JsonObject object = new JsonObject(); object.addProperty("url", url); embed.add(key, object);
        } catch (IllegalArgumentException ignored) {}
    }
    private static JsonObject field(String name, String value, boolean inline) {
        JsonObject field = new JsonObject(); field.addProperty("name", name); field.addProperty("value", truncate(value == null || value.isBlank() ? "-" : value, 1024)); field.addProperty("inline", inline); return field;
    }
    private static int color(Channel channel, String action) {
        if (channel == Channel.MODERATION) return EssentialsConfig.DISCORD_MODERATION_COLOR.get();
        if (channel == Channel.STAFF || action.contains("TEST")) return EssentialsConfig.DISCORD_STAFF_COLOR.get();
        return 0x3498DB;
    }
    private static String truncate(String value, int max) { return value.length() <= max ? value : value.substring(0, max - 3) + "..."; }
    private DiscordWebhook() {}
}
