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
    private static final Map<UUID, Location> BACK = new ConcurrentHashMap<>();

    public static boolean isGod(ServerPlayer player) { return data(player).getBoolean(GOD); }
    public static void setGod(ServerPlayer player, boolean enabled) { setFlag(player, GOD, enabled); }
    public static boolean isFlight(ServerPlayer player) { return data(player).getBoolean(FLY); }
    public static void setFlight(ServerPlayer player, boolean enabled) {
        setFlag(player, FLY, enabled);
        applyFlight(player);
    }
    public static void applyFlight(ServerPlayer player) {
        boolean enabled = isFlight(player);
        player.getAbilities().mayfly = enabled || player.isCreative() || player.isSpectator();
        if (!player.getAbilities().mayfly) player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }
    public static void remember(ServerPlayer player) {
        BACK.put(player.getUUID(), new Location(player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()));
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
    private static void setFlag(ServerPlayer player, String key, boolean value) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag persisted = persistent.getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag ours = persisted.getCompound(ROOT);
        ours.putBoolean(key, value);
        persisted.put(ROOT, ours);
        persistent.put(Player.PERSISTED_NBT_TAG, persisted);
    }
    private record Location(ResourceKey<Level> dimension, double x, double y, double z, float yaw, float pitch) {}
    private PlayerState() {}
}
