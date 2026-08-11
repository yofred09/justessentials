package yofred.dev.justessentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public final class AuditLog {
    private static Path path(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).resolve("justessentials-history.log"); }

    public static void record(MinecraftServer server, String actor, String action, String target, String detail) {
        String safe = detail.replace('\n', ' ').replace('\r', ' ');
        String line = Instant.now() + "\t" + actor + "\t" + action + "\t" + target + "\t" + safe + System.lineSeparator();
        try { Files.writeString(path(server), line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND); }
        catch (IOException e) { JustEssentials.LOGGER.error("Unable to write Just Essentials audit log", e); }
        yofred.dev.justcore.api.AuditService.record(server, JustEssentials.MODID, action, null, null, actor + " -> " + target + ": " + safe);
    }

    public static List<String> history(MinecraftServer server, String playerName, int limit) {
        Path file = path(server);
        if (!Files.exists(file)) return List.of();
        try {
            List<String> matches = Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .filter(line -> line.toLowerCase().contains("\t" + playerName.toLowerCase() + "\t"))
                    .toList();
            return matches.subList(Math.max(0, matches.size() - limit), matches.size());
        } catch (IOException e) { return List.of("Unable to read history: " + e.getMessage()); }
    }
    private AuditLog() {}
}
