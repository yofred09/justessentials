package yofred.dev.justessentials;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class StaffTools {
    private static final String SAVED = "StaffSavedInventory";
    static void activate(ServerPlayer player) {
        var ours = player.getPersistentData().getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG).getCompound("JustEssentials");
        if (!ours.contains(SAVED)) ours.put(SAVED, player.getInventory().save(new ListTag()));
        writeData(player, ours);
        player.getInventory().clearContent();
        player.getInventory().setItem(0, tool(Items.COMPASS, "Random Player Teleport"));
        player.getInventory().setItem(1, tool(Items.CHEST, "Right-click Player: Inspect Inventory"));
        player.getInventory().setItem(2, tool(Items.BLAZE_ROD, "Right-click Player: Freeze / Unfreeze"));
        player.getInventory().setItem(8, tool(Items.BARRIER, "Exit Staff Mode"));
        player.getInventory().setChanged();
    }
    static void deactivate(ServerPlayer player) {
        var ours = player.getPersistentData().getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG).getCompound("JustEssentials");
        if (ours.contains(SAVED)) {
            player.getInventory().clearContent();
            player.getInventory().load(ours.getList(SAVED, net.minecraft.nbt.Tag.TAG_COMPOUND));
            ours.remove(SAVED); writeData(player, ours); player.getInventory().setChanged();
        }
    }
    static boolean handlePlayer(ServerPlayer staff, ServerPlayer target, ItemStack held) {
        if (!PlayerState.isStaffMode(staff)) return false;
        if (held.is(Items.CHEST)) { staff.openMenu(InspectionMenus.playerInventory(target)); AuditLog.record(staff.server, staff.getGameProfile().getName(), "STAFF_TOOL_INVSEE", target.getGameProfile().getName(), "opened"); return true; }
        if (held.is(Items.BLAZE_ROD)) { boolean frozen = !PlayerState.isFrozen(target); PlayerState.setFrozen(target, frozen); AuditLog.record(staff.server, staff.getGameProfile().getName(), frozen ? "FREEZE" : "UNFREEZE", target.getGameProfile().getName(), "staff tool"); staff.sendSystemMessage(Component.literal(target.getGameProfile().getName() + (frozen ? " frozen." : " unfrozen."))); return true; }
        return false;
    }
    static boolean handleItem(ServerPlayer staff, ItemStack held) {
        if (!PlayerState.isStaffMode(staff)) return false;
        if (held.is(Items.BARRIER)) { PlayerState.toggleStaffMode(staff); deactivate(staff); staff.sendSystemMessage(Component.literal("Staff mode disabled.")); AuditLog.record(staff.server, staff.getGameProfile().getName(), "STAFF_MODE", staff.getGameProfile().getName(), "disabled with tool"); return true; }
        if (held.is(Items.COMPASS)) {
            List<ServerPlayer> choices = staff.server.getPlayerList().getPlayers().stream().filter(player -> player != staff).toList();
            if (choices.isEmpty()) { staff.sendSystemMessage(Component.literal("No other online players.")); return true; }
            ServerPlayer target = choices.get(staff.getRandom().nextInt(choices.size())); PlayerState.remember(staff); staff.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot()); staff.sendSystemMessage(Component.literal("Teleported to " + target.getGameProfile().getName() + ".")); return true;
        }
        return false;
    }
    private static ItemStack tool(net.minecraft.world.item.Item item, String name) { ItemStack stack = new ItemStack(item); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(name).withStyle(ChatFormatting.AQUA)); return stack; }
    private static void writeData(ServerPlayer player, net.minecraft.nbt.CompoundTag ours) { var persistent = player.getPersistentData(); var persisted = persistent.getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG); persisted.put("JustEssentials", ours); persistent.put(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG, persisted); }
    private StaffTools() {}
}
