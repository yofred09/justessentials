package yofred.dev.justessentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
    }

    private static int staffChat(CommandSourceStack source, String message) {
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
    private EssentialsCommands() {}
}
