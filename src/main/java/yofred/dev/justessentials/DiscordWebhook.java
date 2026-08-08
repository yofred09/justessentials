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

final class DiscordWebhook {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    static void publish(String actor, String action, String target, String detail) {
        if (!EssentialsConfig.DISCORD_ENABLED.get()) return;
        if ("STAFF_CHAT".equals(action) && !EssentialsConfig.DISCORD_STAFF_CHAT.get()) return;
        String error = configurationError();
        if (error != null) {
            JustEssentials.LOGGER.warn("Discord audit delivery disabled: {}", error);
            return;
        }

        JsonObject embed = new JsonObject();
        embed.addProperty("title", "Just Essentials Audit");
        embed.addProperty("color", color(action));
        embed.addProperty("timestamp", Instant.now().toString());
        JsonArray fields = new JsonArray();
        fields.add(field("Action", action, true));
        fields.add(field("Actor", actor, true));
        fields.add(field("Target", target, true));
        fields.add(field("Details", detail, false));
        embed.add("fields", fields);

        JsonObject payload = new JsonObject();
        payload.addProperty("username", truncate(EssentialsConfig.DISCORD_USERNAME.get(), 80));
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        payload.add("embeds", embeds);

        HttpRequest request = HttpRequest.newBuilder(URI.create(EssentialsConfig.DISCORD_WEBHOOK_URL.get().trim()))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300)
                        JustEssentials.LOGGER.warn("Discord webhook returned HTTP {}", response.statusCode());
                })
                .exceptionally(errorResult -> {
                    JustEssentials.LOGGER.warn("Unable to deliver Discord audit event: {}", errorResult.getMessage());
                    return null;
                });
    }

    static String configurationError() {
        if (!EssentialsConfig.DISCORD_ENABLED.get()) return "Discord integration is disabled in the server config.";
        String raw = EssentialsConfig.DISCORD_WEBHOOK_URL.get().trim();
        if (raw.isEmpty()) return "Discord webhook URL is empty in the server config.";
        try {
            URI uri = URI.create(raw);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            boolean officialHost = host.equals("discord.com") || host.endsWith(".discord.com") || host.equals("discordapp.com") || host.endsWith(".discordapp.com");
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !officialHost || !uri.getPath().startsWith("/api/webhooks/"))
                return "Webhook URL must be an official HTTPS Discord webhook URL.";
        } catch (IllegalArgumentException exception) {
            return "Discord webhook URL is invalid.";
        }
        return null;
    }

    private static JsonObject field(String name, String value, boolean inline) {
        JsonObject field = new JsonObject();
        field.addProperty("name", name);
        field.addProperty("value", truncate(value == null || value.isBlank() ? "-" : value, 1024));
        field.addProperty("inline", inline);
        return field;
    }

    private static int color(String action) {
        if (action.contains("BAN") || action.contains("KICK") || action.contains("MUTE") || action.contains("FREEZE")) return 0xED4245;
        if (action.contains("TEST")) return 0x5865F2;
        return 0x3498DB;
    }

    private static String truncate(String value, int max) { return value.length() <= max ? value : value.substring(0, max - 1) + "…"; }
    private DiscordWebhook() {}
}
