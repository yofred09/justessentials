package yofred.dev.justessentials;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = JustEssentials.MODID)
public final class EssentialsEvents {
    private static final Map<UUID, Instant> SESSIONS = new ConcurrentHashMap<>();
    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) { EssentialsCommands.register(event.getDispatcher()); }
    @SubscribeEvent
    public static void permissions(PermissionGatherEvent.Nodes event) { EssentialsPermissions.register(event); }
    @SubscribeEvent
    public static void damage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerState.isGod(player)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerState.applyFlight(player);
            PlayerState.enforceFreeze(player);
            SESSIONS.put(player.getUUID(), Instant.now());
            DiscordWebhook.playerJoined(player);
            if (PlayerState.isStaffMode(player)) StaffTools.activate(player);
        }
    }
    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Instant joined = SESSIONS.remove(player.getUUID());
            DiscordWebhook.playerLeft(player, joined == null ? Duration.ZERO : Duration.between(joined, Instant.now()));
        }
    }
    @SubscribeEvent
    public static void chat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (PlayerState.isMuted(player)) {
            event.setCanceled(true);
            player.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_MUTED.get()));
        } else if (PlayerState.isStaffChat(player) && EssentialsPermissions.has(player, EssentialsPermissions.STAFF_CHAT)) {
            event.setCanceled(true);
            EssentialsCommands.staffChat(player.createCommandSourceStack(), event.getRawText());
        }
    }
    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerState.enforceFreeze(player);
        }
    }
    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        PunishmentStore.tick(event.getServer());
        TabListManager.tick(event.getServer());
    }
    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) { cancelIfFrozen(event.getEntity(), event); }
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) { cancelIfFrozen(event.getEntity(), event); }
    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event) { cancelIfFrozen(event.getEntity(), event); }
    @SubscribeEvent
    public static void entityInteract(PlayerInteractEvent.EntityInteract event) { cancelIfFrozen(event.getEntity(), event); }
    @SubscribeEvent
    public static void entityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) { cancelIfFrozen(event.getEntity(), event); }
    @SubscribeEvent
    public static void staffToolItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer staff && StaffTools.handleItem(staff, event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void staffToolPlayer(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() instanceof ServerPlayer staff && event.getTarget() instanceof ServerPlayer target && StaffTools.handlePlayer(staff, target, event.getItemStack())) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void attack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PlayerState.isFrozen(player)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player && PlayerState.isFrozen(player)) event.setCanceled(true);
    }
    private static void cancelIfFrozen(net.minecraft.world.entity.player.Player player, net.neoforged.bus.api.ICancellableEvent event) {
        if (player instanceof ServerPlayer serverPlayer && PlayerState.isFrozen(serverPlayer)) event.setCanceled(true);
    }
    private EssentialsEvents() {}
}
