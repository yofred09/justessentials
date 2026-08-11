package yofred.dev.justessentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

final class TravelStore {
    record Location(String dimension, double x, double y, double z, float yaw, float pitch) {}
    record Request(UUID requester, long expiresAt) {}
    private static final class Data {
        Map<String, Map<String, Location>> homes = new HashMap<>();
        Map<String, Location> warps = new LinkedHashMap<>();
    }
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, Request> REQUESTS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_TELEPORT = new ConcurrentHashMap<>();

    static synchronized boolean setHome(MinecraftServer server, ServerPlayer player, String name, int max) {
        Data data = load(server);
        Map<String, Location> homes = data.homes.computeIfAbsent(player.getUUID().toString(), key -> new LinkedHashMap<>());
        String normalized = name.toLowerCase(java.util.Locale.ROOT);
        if (!homes.containsKey(normalized) && homes.size() >= max) return false;
        homes.put(normalized, here(player)); save(server, data); return true;
    }
    static synchronized boolean deleteHome(MinecraftServer server, ServerPlayer player, String name) {
        Data data = load(server); Map<String, Location> homes = data.homes.get(player.getUUID().toString());
        if (homes == null || homes.remove(name.toLowerCase(java.util.Locale.ROOT)) == null) return false;
        save(server, data); return true;
    }
    static synchronized Location home(MinecraftServer server, ServerPlayer player, String name) {
        Map<String, Location> homes = load(server).homes.get(player.getUUID().toString());
        return homes == null ? null : homes.get(name.toLowerCase(java.util.Locale.ROOT));
    }
    static synchronized java.util.Set<String> homes(MinecraftServer server, ServerPlayer player) {
        Map<String, Location> homes = load(server).homes.get(player.getUUID().toString());
        return homes == null ? java.util.Set.of() : new java.util.TreeSet<>(homes.keySet());
    }
    static synchronized void setWarp(MinecraftServer server, ServerPlayer player, String name) { Data data = load(server); data.warps.put(name.toLowerCase(java.util.Locale.ROOT), here(player)); save(server, data); }
    static synchronized boolean deleteWarp(MinecraftServer server, String name) { Data data = load(server); if (data.warps.remove(name.toLowerCase(java.util.Locale.ROOT)) == null) return false; save(server, data); return true; }
    static synchronized Location warp(MinecraftServer server, String name) { return load(server).warps.get(name.toLowerCase(java.util.Locale.ROOT)); }
    static synchronized java.util.Set<String> warps(MinecraftServer server) { return new java.util.TreeSet<>(load(server).warps.keySet()); }
    static boolean teleport(ServerPlayer player, Location location) {
        if (!ready(player)) return false;
        ResourceLocation id = ResourceLocation.tryParse(location.dimension());
        if (id == null) return false;
        ServerLevel level = player.server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
        if (level == null) return false;
        net.minecraft.core.BlockPos destination = net.minecraft.core.BlockPos.containing(location.x(), location.y(), location.z());
        if (EssentialsConfig.SAFE_TELEPORT.get()) { destination = safe(level, destination); if (destination == null) return false; }
        PlayerState.remember(player); player.teleportTo(level, destination.getX()+0.5D, destination.getY(), destination.getZ()+0.5D, location.yaw(), location.pitch()); LAST_TELEPORT.put(player.getUUID(),System.currentTimeMillis()); return true;
    }
    static boolean ready(ServerPlayer player) { int seconds=EssentialsConfig.TELEPORT_COOLDOWN.get(); return seconds<=0 || System.currentTimeMillis()-LAST_TELEPORT.getOrDefault(player.getUUID(),0L)>=seconds*1000L; }
    static void markTeleported(ServerPlayer player) { LAST_TELEPORT.put(player.getUUID(),System.currentTimeMillis()); }
    private static net.minecraft.core.BlockPos safe(ServerLevel level,net.minecraft.core.BlockPos origin) { for(int radius=0;radius<=4;radius++) for(int dy=-4;dy<=8;dy++) for(int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++){ net.minecraft.core.BlockPos p=origin.offset(dx,dy,dz); if(p.getY()<=level.getMinBuildHeight()+1||p.getY()>=level.getMaxBuildHeight()-2)continue; if(level.isEmptyBlock(p)&&level.isEmptyBlock(p.above())&&!level.getBlockState(p.below()).getCollisionShape(level,p.below()).isEmpty())return p; } return null; }
    static void request(ServerPlayer requester, ServerPlayer target, int timeoutSeconds) { REQUESTS.put(target.getUUID(), new Request(requester.getUUID(), System.currentTimeMillis() + timeoutSeconds * 1000L)); }
    static Request takeRequest(ServerPlayer target) { Request request = REQUESTS.remove(target.getUUID()); return request != null && request.expiresAt() >= System.currentTimeMillis() ? request : null; }
    static boolean deny(ServerPlayer target) { return REQUESTS.remove(target.getUUID()) != null; }
    private static Location here(ServerPlayer player) { return new Location(player.level().dimension().location().toString(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()); }
    private static Path path(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).resolve("justessentials-travel.json"); }
    private static Data load(MinecraftServer server) { Path path = path(server); try { if (!Files.exists(path)) return new Data(); Data data = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), Data.class); return data == null ? new Data() : data; } catch (Exception exception) { SafeFiles.preserveCorrupt(path); JustEssentials.LOGGER.error("Unable to read travel database; a corrupt backup was preserved", exception); return new Data(); } }
    private static void save(MinecraftServer server, Data data) { try { SafeFiles.writeAtomically(path(server), GSON.toJson(data)); } catch (Exception exception) { JustEssentials.LOGGER.error("Unable to save travel database", exception); } }
    private TravelStore() {}
}
