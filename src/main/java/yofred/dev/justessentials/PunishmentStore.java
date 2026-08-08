package yofred.dev.justessentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

public final class PunishmentStore {
    public enum Kind { MUTE, FREEZE, BAN }
    public record Entry(UUID playerId, String playerName, Kind kind, String actor, String reason, long createdAt, long expiresAt, boolean active) {}
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<Entry>>() {}.getType();
    private static final Pattern DURATION = Pattern.compile("(?i)^(\\d+)(s|m|h|d|w)$");
    private static long lastExpiryCheck;

    public static Duration parseDuration(String input) {
        Matcher matcher = DURATION.matcher(input);
        if (!matcher.matches()) return null;
        long value;
        try { value = Long.parseLong(matcher.group(1)); } catch (NumberFormatException exception) { return null; }
        if (value < 1) return null;
        try {
            return switch (matcher.group(2).toLowerCase(Locale.ROOT)) {
                case "s" -> Duration.ofSeconds(value);
                case "m" -> Duration.ofMinutes(value);
                case "h" -> Duration.ofHours(value);
                case "d" -> Duration.ofDays(value);
                case "w" -> Duration.ofDays(Math.multiplyExact(value, 7));
                default -> null;
            };
        } catch (ArithmeticException exception) { return null; }
    }

    public static synchronized Entry add(MinecraftServer server, ServerPlayer target, Kind kind, String actor, String reason, Duration duration) {
        List<Entry> entries = load(server);
        entries = deactivate(entries, target.getUUID(), kind);
        long now = Instant.now().toEpochMilli();
        Entry entry = new Entry(target.getUUID(), target.getGameProfile().getName(), kind, actor, reason, now, now + duration.toMillis(), true);
        entries.add(entry);
        save(server, entries);
        return entry;
    }

    public static synchronized void deactivate(MinecraftServer server, UUID playerId, Kind kind) {
        List<Entry> entries = load(server);
        save(server, deactivate(entries, playerId, kind));
    }
    public static synchronized void deactivate(MinecraftServer server, String playerName, Kind kind) {
        List<Entry> entries = load(server);
        save(server, entries.stream().map(e -> e.active() && e.playerName().equalsIgnoreCase(playerName) && e.kind() == kind ? withActive(e, false) : e).toList());
    }

    public static synchronized List<Entry> history(MinecraftServer server, String playerName) {
        return load(server).stream().filter(entry -> entry.playerName().equalsIgnoreCase(playerName)).toList();
    }

    public static synchronized Entry active(MinecraftServer server, UUID playerId, Kind kind) {
        long now = Instant.now().toEpochMilli();
        return load(server).stream().filter(e -> e.active() && e.playerId().equals(playerId) && e.kind() == kind && e.expiresAt() > now).findFirst().orElse(null);
    }

    public static void tick(MinecraftServer server) {
        long now = Instant.now().toEpochMilli();
        if (now - lastExpiryCheck < 1000) return;
        lastExpiryCheck = now;
        expire(server, now);
    }

    private static synchronized void expire(MinecraftServer server, long now) {
        List<Entry> entries = load(server);
        boolean changed = false;
        List<Entry> updated = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            if (entry.active() && entry.expiresAt() <= now) {
                changed = true;
                updated.add(withActive(entry, false));
                ServerPlayer player = server.getPlayerList().getPlayer(entry.playerId());
                if (entry.kind() == Kind.MUTE && player != null) PlayerState.setMuted(player, false);
                if (entry.kind() == Kind.FREEZE && player != null) PlayerState.setFrozen(player, false);
                if (entry.kind() == Kind.BAN) server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "pardon " + entry.playerName());
                AuditLog.record(server, "SYSTEM", "TEMP_" + entry.kind() + "_EXPIRED", entry.playerName(), entry.reason());
                if (player != null) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Your temporary " + entry.kind().name().toLowerCase(Locale.ROOT) + " has expired."));
            } else updated.add(entry);
        }
        if (changed) save(server, updated);
    }

    private static List<Entry> deactivate(List<Entry> entries, UUID id, Kind kind) {
        return entries.stream().map(e -> e.active() && e.playerId().equals(id) && e.kind() == kind ? withActive(e, false) : e).toList();
    }
    private static Entry withActive(Entry e, boolean active) { return new Entry(e.playerId(), e.playerName(), e.kind(), e.actor(), e.reason(), e.createdAt(), e.expiresAt(), active); }
    private static Path path(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).resolve("justessentials-punishments.json"); }
    private static List<Entry> load(MinecraftServer server) {
        Path path = path(server);
        if (!Files.exists(path)) return new ArrayList<>();
        try {
            List<Entry> result = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), LIST_TYPE);
            return result == null ? new ArrayList<>() : new ArrayList<>(result);
        } catch (Exception exception) { JustEssentials.LOGGER.error("Unable to read punishment database", exception); return new ArrayList<>(); }
    }
    private static void save(MinecraftServer server, List<Entry> entries) {
        try { Files.writeString(path(server), GSON.toJson(entries), StandardCharsets.UTF_8); }
        catch (IOException exception) { JustEssentials.LOGGER.error("Unable to save punishment database", exception); }
    }
    private PunishmentStore() {}
}
