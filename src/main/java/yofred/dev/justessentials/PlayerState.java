package yofred.dev.justessentials;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class PlayerState {
    private static final String ROOT = "JustEssentials";
    private static final String GOD = "GodMode";
    private static final String FLY = "Flight";
    private static final String MUTED = "Muted";
    private static final String FROZEN = "Frozen";
    private static final String STAFF_MODE = "StaffMode";
    private static final String STAFF_CHAT = "StaffChatMode";
    private static final String PREVIOUS_GOD = "PreviousGodMode";
    private static final String PREVIOUS_FLY = "PreviousFlight";
    private static final Map<UUID, Location> BACK = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> FREEZE_LOCATIONS = new ConcurrentHashMap<>();

    public static boolean isGod(ServerPlayer player) { return data(player).getBoolean(GOD); }
    public static void setGod(ServerPlayer player, boolean enabled) { setFlag(player, GOD, enabled); }
    public static boolean isFlight(ServerPlayer player) { return data(player).getBoolean(FLY); }
    public static void setFlight(ServerPlayer player, boolean enabled) {
        setFlag(player, FLY, enabled);
        applyFlight(player);
    }
    public static boolean isMuted(ServerPlayer player) { return data(player).getBoolean(MUTED); }
    public static void setMuted(ServerPlayer player, boolean enabled) { setFlag(player, MUTED, enabled); }
    public static boolean isFrozen(ServerPlayer player) { return data(player).getBoolean(FROZEN); }
    public static void setFrozen(ServerPlayer player, boolean enabled) {
        setFlag(player, FROZEN, enabled);
        if (enabled) FREEZE_LOCATIONS.put(player.getUUID(), currentLocation(player));
        else FREEZE_LOCATIONS.remove(player.getUUID());
    }
    public static void enforceFreeze(ServerPlayer player) {
        if (!isFrozen(player)) return;
        Location location = FREEZE_LOCATIONS.computeIfAbsent(player.getUUID(), id -> currentLocation(player));
        ServerLevel level = player.server.getLevel(location.dimension());
        if (level != null && (player.level() != level || player.distanceToSqr(location.x(), location.y(), location.z()) > 0.0025D))
            player.teleportTo(level, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0;
    }
    public static boolean isStaffMode(ServerPlayer player) { return data(player).getBoolean(STAFF_MODE); }
    public static boolean isStaffChat(ServerPlayer player) { return data(player).getBoolean(STAFF_CHAT); }
    public static void setStaffChat(ServerPlayer player, boolean enabled) { setFlag(player, STAFF_CHAT, enabled); }
    public static boolean toggleStaffMode(ServerPlayer player) {
        boolean enabled = !isStaffMode(player);
        if (enabled) {
            setFlag(player, PREVIOUS_GOD, isGod(player));
            setFlag(player, PREVIOUS_FLY, isFlight(player));
            setGod(player, true);
            setFlight(player, true);
            setStaffChat(player, true);
        } else {
            setGod(player, data(player).getBoolean(PREVIOUS_GOD));
            setFlight(player, data(player).getBoolean(PREVIOUS_FLY));
            setStaffChat(player, false);
        }
        setFlag(player, STAFF_MODE, enabled);
        return enabled;
    }
    public static void applyFlight(ServerPlayer player) {
        boolean enabled = isFlight(player);
        player.getAbilities().mayfly = enabled || player.isCreative() || player.isSpectator();
        if (!player.getAbilities().mayfly) player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }
    public static void remember(ServerPlayer player) {
        BACK.put(player.getUUID(), currentLocation(player));
    }
    public static boolean goBack(ServerPlayer player) {
        Location location = BACK.get(player.getUUID());
        if (location == null) return false;
        MinecraftServer server = player.server;
        ServerLevel level = server.getLevel(location.dimension());
        if (level == null) return false;
        Location current = new Location(player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        player.teleportTo(level, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
        BACK.put(player.getUUID(), current);
        return true;
    }
    private static CompoundTag data(ServerPlayer player) { return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(ROOT); }
    private static Location currentLocation(ServerPlayer player) { return new Location(player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()); }
    private static void setFlag(ServerPlayer player, String key, boolean value) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag persisted = persistent.getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag ours = persisted.getCompound(ROOT);
        ours.putBoolean(key, value);
        persisted.put(ROOT, ours);
        persistent.put(Player.PERSISTED_NBT_TAG, persisted);
    }
    static boolean integrationFlag(ServerPlayer player, String key) { return data(player).getBoolean(key); }
    static void setIntegrationFlag(ServerPlayer player, String key, boolean value) { setFlag(player, key, value); }
    static void removeIntegrationFlag(ServerPlayer player, String key) {
        CompoundTag persistent = player.getPersistentData(); CompoundTag persisted = persistent.getCompound(Player.PERSISTED_NBT_TAG); CompoundTag ours = persisted.getCompound(ROOT);
        ours.remove(key); persisted.put(ROOT, ours); persistent.put(Player.PERSISTED_NBT_TAG, persisted);
    }
    private record Location(ResourceKey<Level> dimension, double x, double y, double z, float yaw, float pitch) {}
    private PlayerState() {}
}
