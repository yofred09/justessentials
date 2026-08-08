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
        if (EssentialsConfig.STAFF_TOOLS.get()) {
            player.getInventory().setItem(0, tool(Items.COMPASS, EssentialsConfig.STAFF_TOOL_TELEPORT.get(), "teleport"));
            player.getInventory().setItem(1, tool(Items.CHEST, EssentialsConfig.STAFF_TOOL_INSPECT.get(), "inspect"));
            player.getInventory().setItem(2, tool(Items.BLAZE_ROD, EssentialsConfig.STAFF_TOOL_FREEZE.get(), "freeze"));
            player.getInventory().setItem(8, tool(Items.BARRIER, EssentialsConfig.STAFF_TOOL_EXIT.get(), "exit"));
        }
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
        if (isTool(held, "inspect")) { staff.openMenu(InspectionMenus.playerInventory(target)); AuditLog.record(staff.server, staff.getGameProfile().getName(), "STAFF_TOOL_INVSEE", target.getGameProfile().getName(), "opened"); return true; }
        if (isTool(held, "freeze")) { boolean frozen = !PlayerState.isFrozen(target); PlayerState.setFrozen(target, frozen); AuditLog.record(staff.server, staff.getGameProfile().getName(), frozen ? "FREEZE" : "UNFREEZE", target.getGameProfile().getName(), "staff tool"); staff.sendSystemMessage(Messages.message("{player} " + (frozen ? "&cfrozen." : "&aunfrozen."), java.util.Map.of("player", target.getGameProfile().getName()))); return true; }
        return false;
    }
    static boolean handleItem(ServerPlayer staff, ItemStack held) {
        if (!PlayerState.isStaffMode(staff)) return false;
        if (isTool(held, "exit")) { PlayerState.toggleStaffMode(staff); deactivate(staff); JustVanishCompat.leaveStaffMode(staff); staff.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_STAFF_OFF.get())); AuditLog.record(staff.server, staff.getGameProfile().getName(), "STAFF_MODE", staff.getGameProfile().getName(), "disabled with tool"); return true; }
        if (isTool(held, "teleport")) {
            List<ServerPlayer> choices = staff.server.getPlayerList().getPlayers().stream().filter(player -> player != staff).toList();
            if (choices.isEmpty()) { staff.sendSystemMessage(Component.literal("No other online players.")); return true; }
            ServerPlayer target = choices.get(staff.getRandom().nextInt(choices.size())); PlayerState.remember(staff); staff.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot()); staff.sendSystemMessage(Component.literal("Teleported to " + target.getGameProfile().getName() + ".")); return true;
        }
        return false;
    }
    private static ItemStack tool(net.minecraft.world.item.Item item, String name, String id) { ItemStack stack = new ItemStack(item); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Messages.colored(name)); net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag(); tag.putString("JustEssentialsStaffTool", id); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag)); return stack; }
    private static boolean isTool(ItemStack stack, String id) { var data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA); return data != null && id.equals(data.copyTag().getString("JustEssentialsStaffTool")); }
    private static void writeData(ServerPlayer player, net.minecraft.nbt.CompoundTag ours) { var persistent = player.getPersistentData(); var persisted = persistent.getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG); persisted.put("JustEssentials", ours); persistent.put(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG, persisted); }
    private StaffTools() {}
}
