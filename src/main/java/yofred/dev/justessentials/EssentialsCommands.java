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

    private static int setMuted(CommandSourceStack source, ServerPlayer target, boolean enabled) {
        PlayerState.setMuted(target, enabled);
        String action = enabled ? "MUTE" : "UNMUTE";
        AuditLog.record(source.getServer(), source.getTextName(), action, target.getGameProfile().getName(), "manual");
        target.sendSystemMessage(Component.literal(enabled ? "You have been muted." : "You have been unmuted.").withStyle(enabled ? ChatFormatting.RED : ChatFormatting.GREEN));
        source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + (enabled ? " muted." : " unmuted.")), true);
        return 1;
    }

    private static int setFrozen(CommandSourceStack source, ServerPlayer target, boolean enabled) {
        PlayerState.setFrozen(target, enabled);
        target.setDeltaMovement(0, 0, 0);
        String action = enabled ? "FREEZE" : "UNFREEZE";
        AuditLog.record(source.getServer(), source.getTextName(), action, target.getGameProfile().getName(), "manual");
        target.sendSystemMessage(Component.literal(enabled ? "You have been frozen by staff." : "You are no longer frozen.").withStyle(enabled ? ChatFormatting.RED : ChatFormatting.GREEN));
        source.sendSuccess(() -> Component.literal(target.getGameProfile().getName() + (enabled ? " frozen." : " unfrozen.")), true);
        return 1;
    }

    private static int staffMode(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = PlayerState.toggleStaffMode(player);
        AuditLog.record(source.getServer(), player.getGameProfile().getName(), "STAFF_MODE", player.getGameProfile().getName(), enabled ? "enabled" : "disabled");
        source.sendSuccess(() -> Component.literal("Staff mode " + (enabled ? "enabled. Flight, god mode, and staff chat are active." : "disabled. Previous flight and god settings restored.")), false);
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
    private EssentialsCommands() {}
}
