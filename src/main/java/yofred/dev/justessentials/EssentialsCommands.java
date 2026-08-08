package yofred.dev.justessentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public final class EssentialsCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("staffchat").then(Commands.argument("message", StringArgumentType.greedyString())
                .requires(s -> EssentialsConfig.STAFF_CHAT.get() && EssentialsPermissions.has(s, EssentialsPermissions.STAFF_CHAT))
                .executes(c -> staffChat(c.getSource(), StringArgumentType.getString(c, "message")))));
        dispatcher.register(Commands.literal("sc").then(Commands.argument("message", StringArgumentType.greedyString())
                .requires(s -> EssentialsConfig.STAFF_CHAT.get() && EssentialsPermissions.has(s, EssentialsPermissions.STAFF_CHAT))
                .executes(c -> staffChat(c.getSource(), StringArgumentType.getString(c, "message")))));
        dispatcher.register(Commands.literal("stp")
                .requires(s -> EssentialsConfig.SILENT_TELEPORT.get() && EssentialsPermissions.has(s, EssentialsPermissions.SILENT_TP))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> silentTeleport(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("history")
                .requires(s -> EssentialsConfig.HISTORY.get() && EssentialsPermissions.has(s, EssentialsPermissions.HISTORY))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> history(c.getSource(), StringArgumentType.getString(c, "player")))));
        dispatcher.register(Commands.literal("jkick")
                .requires(s -> EssentialsConfig.PUNISHMENTS.get() && EssentialsPermissions.has(s, EssentialsPermissions.KICK))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("reason", StringArgumentType.greedyString()).executes(c -> kick(c.getSource(), EntityArgument.getPlayer(c, "player"), StringArgumentType.getString(c, "reason"))))));
        dispatcher.register(Commands.literal("jban")
                .requires(s -> EssentialsConfig.PUNISHMENTS.get() && EssentialsPermissions.has(s, EssentialsPermissions.BAN))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("reason", StringArgumentType.greedyString()).executes(c -> ban(c.getSource(), EntityArgument.getPlayer(c, "player"), StringArgumentType.getString(c, "reason"))))));
        dispatcher.register(Commands.literal("heal")
                .requires(s -> EssentialsConfig.PLAYER_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.HEAL))
                .executes(c -> heal(c.getSource(), c.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> heal(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("feed")
                .requires(s -> EssentialsConfig.PLAYER_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.FEED))
                .executes(c -> feed(c.getSource(), c.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> feed(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("fly")
                .requires(s -> EssentialsConfig.PLAYER_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.FLY))
                .executes(c -> fly(c.getSource(), c.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> fly(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("god")
                .requires(s -> EssentialsConfig.PLAYER_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.GOD))
                .executes(c -> god(c.getSource(), c.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> god(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("spawn")
                .requires(s -> EssentialsConfig.TELEPORT_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.SPAWN))
                .executes(c -> spawn(c.getSource())));
        dispatcher.register(Commands.literal("back")
                .requires(s -> EssentialsConfig.TELEPORT_UTILITIES.get() && EssentialsPermissions.has(s, EssentialsPermissions.BACK))
                .executes(c -> back(c.getSource())));
        dispatcher.register(Commands.literal("invsee")
                .requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.INVSEE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> inspectInventory(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("endersee")
                .requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.ENDERSEE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> inspectEnderChest(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("curiossee")
                .requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.CURIOSSEE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> inspectCurios(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("accessoriessee")
                .requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.CURIOSSEE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> inspectAccessories(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("mute")
                .requires(s -> EssentialsConfig.MODERATION.get() && EssentialsPermissions.has(s, EssentialsPermissions.MUTE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> setMuted(c.getSource(), EntityArgument.getPlayer(c, "player"), true))));
        dispatcher.register(Commands.literal("unmute")
                .requires(s -> EssentialsConfig.MODERATION.get() && EssentialsPermissions.has(s, EssentialsPermissions.MUTE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> setMuted(c.getSource(), EntityArgument.getPlayer(c, "player"), false))));
        dispatcher.register(Commands.literal("freeze")
                .requires(s -> EssentialsConfig.MODERATION.get() && EssentialsPermissions.has(s, EssentialsPermissions.FREEZE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> setFrozen(c.getSource(), EntityArgument.getPlayer(c, "player"), true))));
        dispatcher.register(Commands.literal("unfreeze")
                .requires(s -> EssentialsConfig.MODERATION.get() && EssentialsPermissions.has(s, EssentialsPermissions.FREEZE))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> setFrozen(c.getSource(), EntityArgument.getPlayer(c, "player"), false))));
        dispatcher.register(Commands.literal("staffmode")
                .requires(s -> EssentialsConfig.STAFF_MODE.get() && EssentialsPermissions.has(s, EssentialsPermissions.STAFF_MODE))
                .executes(c -> staffMode(c.getSource())));
        dispatcher.register(Commands.literal("staffchattoggle")
                .requires(s -> EssentialsConfig.STAFF_CHAT.get() && EssentialsPermissions.has(s, EssentialsPermissions.STAFF_CHAT))
                .executes(c -> staffChatToggle(c.getSource())));
        dispatcher.register(Commands.literal("discordtest")
                .requires(s -> EssentialsPermissions.has(s, EssentialsPermissions.DISCORD_TEST))
                .executes(c -> discordTest(c.getSource())));
        dispatcher.register(Commands.literal("jereload")
                .requires(s -> EssentialsPermissions.has(s, EssentialsPermissions.DISCORD_TEST))
                .executes(c -> { c.getSource().sendSuccess(() -> Messages.message("&aConfiguration values are active. File changes are watched by NeoForge."), false); return 1; }));
        registerTemporaryPunishment(dispatcher, "tempmute", PunishmentStore.Kind.MUTE);
        registerTemporaryPunishment(dispatcher, "tempfreeze", PunishmentStore.Kind.FREEZE);
        registerTemporaryPunishment(dispatcher, "tempban", PunishmentStore.Kind.BAN);
        dispatcher.register(Commands.literal("punishments")
                .requires(s -> EssentialsPermissions.has(s, EssentialsPermissions.HISTORY))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> punishments(c.getSource(), StringArgumentType.getString(c, "player"), false))));
        dispatcher.register(Commands.literal("punishmentinfo")
                .requires(s -> EssentialsPermissions.has(s, EssentialsPermissions.HISTORY))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> punishments(c.getSource(), StringArgumentType.getString(c, "player"), true))));
        dispatcher.register(Commands.literal("junban")
                .requires(s -> EssentialsConfig.PUNISHMENTS.get() && EssentialsPermissions.has(s, EssentialsPermissions.BAN))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> unban(c.getSource(), StringArgumentType.getString(c, "player")))));
        dispatcher.register(Commands.literal("sethome").requires(s -> EssentialsConfig.HOMES.get() && EssentialsPermissions.has(s, EssentialsPermissions.HOME))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> setHome(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("home").requires(s -> EssentialsConfig.HOMES.get() && EssentialsPermissions.has(s, EssentialsPermissions.HOME))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> home(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("delhome").requires(s -> EssentialsConfig.HOMES.get() && EssentialsPermissions.has(s, EssentialsPermissions.HOME))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> delHome(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("setwarp").requires(s -> EssentialsConfig.WARPS.get() && EssentialsPermissions.has(s, EssentialsPermissions.WARP_ADMIN))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> setWarp(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("warp").requires(s -> EssentialsConfig.WARPS.get() && EssentialsPermissions.has(s, EssentialsPermissions.WARP))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> warp(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("delwarp").requires(s -> EssentialsConfig.WARPS.get() && EssentialsPermissions.has(s, EssentialsPermissions.WARP_ADMIN))
                .then(Commands.argument("name", StringArgumentType.word()).executes(c -> delWarp(c.getSource(), StringArgumentType.getString(c, "name")))));
        dispatcher.register(Commands.literal("tpa").requires(s -> EssentialsConfig.TPA.get() && EssentialsPermissions.has(s, EssentialsPermissions.TPA))
                .then(Commands.argument("player", EntityArgument.player()).executes(c -> tpa(c.getSource(), EntityArgument.getPlayer(c, "player")))));
        dispatcher.register(Commands.literal("tpaccept").requires(s -> EssentialsConfig.TPA.get() && EssentialsPermissions.has(s, EssentialsPermissions.TPA)).executes(c -> tpAccept(c.getSource())));
        dispatcher.register(Commands.literal("tpdeny").requires(s -> EssentialsConfig.TPA.get() && EssentialsPermissions.has(s, EssentialsPermissions.TPA)).executes(c -> tpDeny(c.getSource())));
        dispatcher.register(Commands.literal("invseeoffline").requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.INVSEE))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> offlineInspect(c.getSource(), StringArgumentType.getString(c, "player"), false))));
        dispatcher.register(Commands.literal("enderseeoffline").requires(s -> EssentialsConfig.INVENTORY_INSPECTION.get() && EssentialsPermissions.has(s, EssentialsPermissions.ENDERSEE))
                .then(Commands.argument("player", StringArgumentType.word()).executes(c -> offlineInspect(c.getSource(), StringArgumentType.getString(c, "player"), true))));
    }

    private static void registerTemporaryPunishment(CommandDispatcher<CommandSourceStack> dispatcher, String command, PunishmentStore.Kind kind) {
        dispatcher.register(Commands.literal(command)
                .requires(s -> EssentialsConfig.MODERATION.get() && EssentialsPermissions.has(s, EssentialsPermissions.TEMP_PUNISH))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("duration", StringArgumentType.word())
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(c -> temporaryPunishment(c.getSource(), EntityArgument.getPlayer(c, "player"), kind,
                                                StringArgumentType.getString(c, "duration"), StringArgumentType.getString(c, "reason")))))));
    }

    static int staffChat(CommandSourceStack source, String message) {
        String actor = source.getTextName();
        Component formatted = Component.literal("[Staff] ").withStyle(ChatFormatting.DARK_AQUA)
                .append(Component.literal(actor + ": ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(message).withStyle(ChatFormatting.WHITE));
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers())
            if (EssentialsPermissions.has(player, EssentialsPermissions.STAFF_CHAT)) player.sendSystemMessage(formatted);
        source.getServer().sendSystemMessage(formatted);
        AuditLog.record(source.getServer(), actor, "STAFF_CHAT", actor, message);
        return 1;
    }

    private static int silentTeleport(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer actor = source.getPlayerOrException();
        PlayerState.remember(actor);
        actor.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
        source.sendSuccess(() -> Component.literal("Silently teleported to " + target.getGameProfile().getName() + "."), false);
        AuditLog.record(source.getServer(), actor.getGameProfile().getName(), "SILENT_TELEPORT", target.getGameProfile().getName(), target.serverLevel().dimension().location().toString());
        return 1;
    }

    private static int history(CommandSourceStack source, String player) {
        var entries = AuditLog.history(source.getServer(), player, 10);
        source.sendSuccess(() -> Component.literal(entries.isEmpty() ? "No history for " + player + "." : String.join("\n", entries)), false);
        return entries.size();
    }

    private static int kick(CommandSourceStack source, ServerPlayer target, String reason) {
        String actor = source.getTextName();
        AuditLog.record(source.getServer(), actor, "KICK", target.getGameProfile().getName(), reason);
        target.connection.disconnect(Component.literal(reason));
        source.sendSuccess(() -> Component.literal("Kicked " + target.getGameProfile().getName() + "."), true);
        return 1;
    }

    private static int ban(CommandSourceStack source, ServerPlayer target, String reason) {
        String actor = source.getTextName();
        String name = target.getGameProfile().getName();
        AuditLog.record(source.getServer(), actor, "BAN", name, reason);
        source.getServer().getCommands().performPrefixedCommand(source, "ban " + name + " " + reason);
        return 1;
    }

    private static int heal(CommandSourceStack source, ServerPlayer target) {
        target.setHealth(target.getMaxHealth());
        target.clearFire();
        source.sendSuccess(() -> Component.literal("Healed " + target.getGameProfile().getName() + "."), false);
        return 1;
    }

    private static int feed(CommandSourceStack source, ServerPlayer target) {
        target.getFoodData().setFoodLevel(20);
        target.getFoodData().setSaturation(20.0F);
        source.sendSuccess(() -> Component.literal("Fed " + target.getGameProfile().getName() + "."), false);
        return 1;
    }

    private static int fly(CommandSourceStack source, ServerPlayer target) {
        boolean enabled = !PlayerState.isFlight(target);
        PlayerState.setFlight(target, enabled);
        source.sendSuccess(() -> Component.literal("Flight " + (enabled ? "enabled" : "disabled") + " for " + target.getGameProfile().getName() + "."), true);
        return 1;
    }

    private static int god(CommandSourceStack source, ServerPlayer target) {
        boolean enabled = !PlayerState.isGod(target);
        PlayerState.setGod(target, enabled);
        source.sendSuccess(() -> Component.literal("God mode " + (enabled ? "enabled" : "disabled") + " for " + target.getGameProfile().getName() + "."), true);
        return 1;
    }

    private static int spawn(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerState.remember(player);
        var level = source.getServer().overworld();
        var pos = level.getSharedSpawnPos();
        player.teleportTo(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), player.getXRot());
        source.sendSuccess(() -> Component.literal("Teleported to spawn."), false);
        return 1;
    }

    private static int back(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!PlayerState.goBack(player)) {
            source.sendFailure(Component.literal("No previous teleport location is available."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Returned to your previous location."), false);
        return 1;
    }

    private static int inspectInventory(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer viewer = source.getPlayerOrException();
        viewer.openMenu(InspectionMenus.playerInventory(target));
        AuditLog.record(source.getServer(), viewer.getGameProfile().getName(), "INVSEE", target.getGameProfile().getName(), "opened");
        return 1;
    }

    private static int inspectEnderChest(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer viewer = source.getPlayerOrException();
        viewer.openMenu(InspectionMenus.enderChest(target));
        AuditLog.record(source.getServer(), viewer.getGameProfile().getName(), "ENDERSEE", target.getGameProfile().getName(), "opened");
        return 1;
    }

    private static int inspectCurios(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        if (!ModList.get().isLoaded("curios")) {
            source.sendFailure(Component.literal("Curios is not installed. Accessories requires its Curios compatibility layer for this command."));
            return 0;
        }
        ServerPlayer viewer = source.getPlayerOrException();
        if (!CuriosInspection.open(viewer, target)) {
            source.sendFailure(Component.literal("No Curios inventory capability is available for that player."));
            return 0;
        }
        AuditLog.record(source.getServer(), viewer.getGameProfile().getName(), "CURIOSSEE", target.getGameProfile().getName(), "opened");
        return 1;
    }
    private static int inspectAccessories(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        if (!ModList.get().isLoaded("accessories")) { source.sendFailure(Component.literal("Accessories is not installed.")); return 0; }
        ServerPlayer viewer = source.getPlayerOrException();
        if (!AccessoriesInspection.open(viewer, target)) { source.sendFailure(Component.literal("No Accessories capability is available for that player.")); return 0; }
        AuditLog.record(source.getServer(), viewer.getGameProfile().getName(), "ACCESSORIESSEE", target.getGameProfile().getName(), "opened"); return 1;
    }

    private static int setMuted(CommandSourceStack source, ServerPlayer target, boolean enabled) {
        if (!canModerate(source, target)) return 0;
        PlayerState.setMuted(target, enabled);
        if (!enabled) PunishmentStore.deactivate(source.getServer(), target.getUUID(), PunishmentStore.Kind.MUTE);
        String action = enabled ? "MUTE" : "UNMUTE";
        AuditLog.record(source.getServer(), source.getTextName(), action, target.getGameProfile().getName(), "manual");
        target.sendSystemMessage(Messages.message(enabled ? EssentialsConfig.MESSAGE_MUTED_APPLIED.get() : EssentialsConfig.MESSAGE_UNMUTED.get()));
        source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + (enabled ? " muted." : " unmuted.")), true);
        return 1;
    }

    private static int setFrozen(CommandSourceStack source, ServerPlayer target, boolean enabled) {
        if (!canModerate(source, target)) return 0;
        PlayerState.setFrozen(target, enabled);
        if (!enabled) PunishmentStore.deactivate(source.getServer(), target.getUUID(), PunishmentStore.Kind.FREEZE);
        target.setDeltaMovement(0, 0, 0);
        String action = enabled ? "FREEZE" : "UNFREEZE";
        AuditLog.record(source.getServer(), source.getTextName(), action, target.getGameProfile().getName(), "manual");
        target.sendSystemMessage(Messages.message(enabled ? EssentialsConfig.MESSAGE_FROZEN.get() : EssentialsConfig.MESSAGE_UNFROZEN.get()));
        source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + (enabled ? " frozen." : " unfrozen.")), true);
        return 1;
    }

    private static int staffMode(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = PlayerState.toggleStaffMode(player);
        if (enabled) StaffTools.activate(player); else StaffTools.deactivate(player);
        AuditLog.record(source.getServer(), player.getGameProfile().getName(), "STAFF_MODE", player.getGameProfile().getName(), enabled ? "enabled" : "disabled");
        source.sendSuccess(() -> Messages.message(enabled ? EssentialsConfig.MESSAGE_STAFF_ON.get() : EssentialsConfig.MESSAGE_STAFF_OFF.get()), false);
        return 1;
    }

    private static int staffChatToggle(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = !PlayerState.isStaffChat(player);
        PlayerState.setStaffChat(player, enabled);
        source.sendSuccess(() -> Component.literal("Staff chat mode " + (enabled ? "enabled." : "disabled.")), false);
        return 1;
    }

    private static int discordTest(CommandSourceStack source) {
        String error = DiscordWebhook.configurationError();
        if (error != null) {
            source.sendFailure(Component.literal(error));
            return 0;
        }
        DiscordWebhook.publish(source.getTextName(), "DISCORD_TEST", "Discord", "Manual webhook test");
        source.sendSuccess(() -> Component.literal("Discord test queued. Check the configured channel."), false);
        return 1;
    }

    private static int temporaryPunishment(CommandSourceStack source, ServerPlayer target, PunishmentStore.Kind kind, String durationText, String reason) {
        if (!canModerate(source, target)) return 0;
        Duration duration = PunishmentStore.parseDuration(durationText);
        if (duration == null || duration.compareTo(Duration.ofDays(3650)) > 0) {
            source.sendFailure(Component.literal("Invalid duration. Use 30s, 15m, 2h, 7d, or 4w (maximum 3650d)."));
            return 0;
        }
        PunishmentStore.Entry entry = PunishmentStore.add(source.getServer(), target, kind, source.getTextName(), reason, duration);
        if (kind == PunishmentStore.Kind.MUTE) PlayerState.setMuted(target, true);
        if (kind == PunishmentStore.Kind.FREEZE) PlayerState.setFrozen(target, true);
        AuditLog.record(source.getServer(), source.getTextName(), "TEMP_" + kind, target.getGameProfile().getName(), durationText + " | " + reason);
        String expires = Instant.ofEpochMilli(entry.expiresAt()).toString();
        if (kind == PunishmentStore.Kind.BAN) {
            source.getServer().getCommands().performPrefixedCommand(source, "ban " + target.getGameProfile().getName() + " " + reason + " (until " + expires + ")");
        } else {
            target.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_TEMP_PUNISHMENT.get(), java.util.Map.of("type", kind.name().toLowerCase(Locale.ROOT), "reason", reason, "expires", expires)));
        }
        source.sendSuccess(() -> Component.literal("Applied temporary " + kind.name().toLowerCase(Locale.ROOT) + " to " + target.getGameProfile().getName() + " until " + expires + "."), true);
        return 1;
    }

    private static int punishments(CommandSourceStack source, String playerName, boolean activeOnly) {
        var entries = PunishmentStore.history(source.getServer(), playerName).stream().filter(entry -> !activeOnly || entry.active() && entry.expiresAt() > System.currentTimeMillis()).toList();
        if (entries.isEmpty()) {
            source.sendFailure(Component.literal("No " + (activeOnly ? "active " : "") + "punishments found for " + playerName + "."));
            return 0;
        }
        int start = Math.max(0, entries.size() - 10);
        for (var entry : entries.subList(start, entries.size())) {
            String status = entry.active() && entry.expiresAt() > System.currentTimeMillis() ? "ACTIVE" : "EXPIRED/REMOVED";
            source.sendSuccess(() -> Component.literal("[" + status + "] " + entry.kind() + " by " + entry.actor() + " | " + entry.reason() + " | until " + Instant.ofEpochMilli(entry.expiresAt())), false);
        }
        return entries.size();
    }

    private static int unban(CommandSourceStack source, String playerName) {
        source.getServer().getCommands().performPrefixedCommand(source, "pardon " + playerName);
        PunishmentStore.deactivate(source.getServer(), playerName, PunishmentStore.Kind.BAN);
        AuditLog.record(source.getServer(), source.getTextName(), "UNBAN", playerName, "manual");
        return 1;
    }

    private static int setHome(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!TravelStore.setHome(source.getServer(), player, name, EssentialsConfig.MAX_HOMES.get())) { source.sendFailure(Component.literal("Home limit reached (" + EssentialsConfig.MAX_HOMES.get() + ").")); return 0; }
        source.sendSuccess(() -> Component.literal("Home '" + name + "' set."), false); return 1;
    }
    private static int home(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException(); var location = TravelStore.home(source.getServer(), player, name);
        if (location == null || !TravelStore.teleport(player, location)) { source.sendFailure(Component.literal("Home '" + name + "' was not found or its dimension is unavailable.")); return 0; }
        source.sendSuccess(() -> Component.literal("Teleported to home '" + name + "'."), false); return 1;
    }
    private static int delHome(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        if (!TravelStore.deleteHome(source.getServer(), source.getPlayerOrException(), name)) { source.sendFailure(Component.literal("Home '" + name + "' was not found.")); return 0; }
        source.sendSuccess(() -> Component.literal("Home '" + name + "' deleted."), false); return 1;
    }
    private static int setWarp(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException(); TravelStore.setWarp(source.getServer(), player, name); AuditLog.record(source.getServer(), source.getTextName(), "SET_WARP", name, player.level().dimension().location().toString());
        source.sendSuccess(() -> Component.literal("Warp '" + name + "' set."), true); return 1;
    }
    private static int warp(CommandSourceStack source, String name) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        var location = TravelStore.warp(source.getServer(), name); ServerPlayer player = source.getPlayerOrException();
        if (location == null || !TravelStore.teleport(player, location)) { source.sendFailure(Component.literal("Warp '" + name + "' was not found or its dimension is unavailable.")); return 0; }
        source.sendSuccess(() -> Component.literal("Teleported to warp '" + name + "'."), false); return 1;
    }
    private static int delWarp(CommandSourceStack source, String name) { if (!TravelStore.deleteWarp(source.getServer(), name)) { source.sendFailure(Component.literal("Warp '" + name + "' was not found.")); return 0; } AuditLog.record(source.getServer(), source.getTextName(), "DELETE_WARP", name, "manual"); source.sendSuccess(() -> Component.literal("Warp '" + name + "' deleted."), true); return 1; }
    private static int tpa(CommandSourceStack source, ServerPlayer target) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer requester = source.getPlayerOrException(); if (requester == target) { source.sendFailure(Component.literal("You cannot send a teleport request to yourself.")); return 0; }
        TravelStore.request(requester, target, EssentialsConfig.TPA_TIMEOUT.get()); target.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_TPA_RECEIVED.get(), java.util.Map.of("player", requester.getGameProfile().getName()))); source.sendSuccess(() -> Messages.message(EssentialsConfig.MESSAGE_TPA_SENT.get(), java.util.Map.of("player", target.getGameProfile().getName())), false); return 1;
    }
    private static int tpAccept(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer target = source.getPlayerOrException(); TravelStore.Request request = TravelStore.takeRequest(target); if (request == null) { source.sendFailure(Component.literal("No active teleport request.")); return 0; }
        ServerPlayer requester = source.getServer().getPlayerList().getPlayer(request.requester()); if (requester == null) { source.sendFailure(Component.literal("The requester is no longer online.")); return 0; }
        PlayerState.remember(requester); requester.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot()); requester.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_TPA_ACCEPTED.get())); source.sendSuccess(() -> Messages.message(EssentialsConfig.MESSAGE_TPA_ACCEPTED.get()), false); return 1;
    }
    private static int tpDeny(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException { if (!TravelStore.deny(source.getPlayerOrException())) { source.sendFailure(Component.literal("No active teleport request.")); return 0; } source.sendSuccess(() -> Messages.message(EssentialsConfig.MESSAGE_TPA_DENIED.get()), false); return 1; }
    private static int offlineInspect(CommandSourceStack source, String playerName, boolean enderChest) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer viewer = source.getPlayerOrException(); String error = OfflineInspection.open(viewer, playerName, enderChest);
        if (error != null) { source.sendFailure(Component.literal(error)); return 0; }
        AuditLog.record(source.getServer(), viewer.getGameProfile().getName(), enderChest ? "OFFLINE_ENDERSEE" : "OFFLINE_INVSEE", playerName, "read-only"); return 1;
    }

    private static boolean canModerate(CommandSourceStack source, ServerPlayer target) {
        if (!(source.getEntity() instanceof ServerPlayer actor)) return true;
        if (actor.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.literal("You cannot punish yourself."));
            return false;
        }
        int actorLevel = permissionLevel(actor);
        if (actorLevel < 4 && target.hasPermissions(actorLevel)) {
            source.sendFailure(Component.literal("You cannot punish a player with an equal or higher permission level."));
            return false;
        }
        return true;
    }

    private static int permissionLevel(ServerPlayer player) {
        for (int level = 4; level >= 0; level--) if (player.hasPermissions(level)) return level;
        return 0;
    }
    private EssentialsCommands() {}
}
