package yofred.dev.justessentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

final class SeenStore {
    record Entry(String uuid, String name, long firstJoin, long lastJoin, long lastLeave, long playtimeSeconds) {}
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static synchronized void login(ServerPlayer player) {
        Map<String, Entry> data = load(player.server); long now = System.currentTimeMillis(); String key = player.getUUID().toString(); Entry old = data.get(key);
        data.put(key, new Entry(key, player.getGameProfile().getName(), old == null ? now : old.firstJoin(), now, old == null ? 0 : old.lastLeave(), old == null ? 0 : old.playtimeSeconds())); save(player.server, data);
    }
    static synchronized void logout(ServerPlayer player) {
        Map<String, Entry> data = load(player.server); long now = System.currentTimeMillis(); String key = player.getUUID().toString(); Entry old = data.get(key);
        if (old != null) data.put(key, new Entry(key, player.getGameProfile().getName(), old.firstJoin(), old.lastJoin(), now, old.playtimeSeconds() + Math.max(0, (now - old.lastJoin()) / 1000))); save(player.server, data);
    }
    static synchronized Entry find(MinecraftServer server, String name) { return load(server).values().stream().filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElse(null); }
    private static Path path(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).resolve("justessentials-seen.json"); }
    private static Map<String, Entry> load(MinecraftServer server) { try { Path p=path(server); if(!Files.exists(p)) return new LinkedHashMap<>(); java.lang.reflect.Type t=new com.google.gson.reflect.TypeToken<Map<String,Entry>>(){}.getType(); Map<String,Entry> result=GSON.fromJson(Files.readString(p, StandardCharsets.UTF_8),t); return result==null?new LinkedHashMap<>():new LinkedHashMap<>(result); } catch(Exception e){ JustEssentials.LOGGER.error("Unable to read seen database",e); return new LinkedHashMap<>(); } }
    private static void save(MinecraftServer server, Map<String,Entry> data) { try { SafeFiles.writeAtomically(path(server),GSON.toJson(data)); } catch(Exception e){ JustEssentials.LOGGER.error("Unable to save seen database",e); } }
    private SeenStore() {}
}
