package yofred.dev.justessentials;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

final class InspectionMenus {
    static SimpleMenuProvider playerInventory(ServerPlayer target) {
        Container view = new PlayerInventoryView(target);
        return new SimpleMenuProvider(
                (containerId, viewerInventory, viewer) -> ChestMenu.sixRows(containerId, viewerInventory, view),
                Component.literal(target.getGameProfile().getName() + " - Inventory"));
    }

    static SimpleMenuProvider enderChest(ServerPlayer target) {
        return new SimpleMenuProvider(
                (containerId, viewerInventory, viewer) -> ChestMenu.threeRows(containerId, viewerInventory, target.getEnderChestInventory()),
                Component.literal(target.getGameProfile().getName() + " - Ender Chest"));
    }

    private static final class PlayerInventoryView implements Container {
        private static final int MENU_SIZE = 54;
        private final ServerPlayer target;

        private PlayerInventoryView(ServerPlayer target) { this.target = target; }

        @Override public int getContainerSize() { return MENU_SIZE; }
        @Override public boolean isEmpty() { return target.getInventory().isEmpty(); }
        @Override public ItemStack getItem(int slot) { return mapped(slot) ? target.getInventory().getItem(slot) : ItemStack.EMPTY; }
        @Override public ItemStack removeItem(int slot, int amount) { return mapped(slot) ? target.getInventory().removeItem(slot, amount) : ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return mapped(slot) ? target.getInventory().removeItemNoUpdate(slot) : ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { if (mapped(slot)) target.getInventory().setItem(slot, stack); }
        @Override public void setChanged() { target.getInventory().setChanged(); }
        @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return target.isAlive() && !target.hasDisconnected(); }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) { return mapped(slot); }
        @Override public void clearContent() { target.getInventory().clearContent(); }

        private boolean mapped(int slot) { return slot >= 0 && slot < target.getInventory().getContainerSize(); }
    }

    private InspectionMenus() {}
}
