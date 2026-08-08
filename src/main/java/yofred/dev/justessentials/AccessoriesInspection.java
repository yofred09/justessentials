package yofred.dev.justessentials;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

final class AccessoriesInspection {
    static boolean open(ServerPlayer viewer, ServerPlayer target) {
        AccessoriesCapability capability = AccessoriesCapability.get(target);
        if (capability == null) return false;
        List<AccessoriesContainer> containers = capability.getContainers().entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).map(java.util.Map.Entry::getValue).toList();
        int count = containers.stream().mapToInt(AccessoriesContainer::getSize).sum();
        int rows = Math.max(1, Math.min(6, (count + 8) / 9));
        Container view = new View(containers, rows * 9);
        viewer.openMenu(new SimpleMenuProvider((id, inventory, player) -> new ChestMenu(type(rows), id, inventory, view, rows), Messages.plain(EssentialsConfig.MENU_ACCESSORIES.get(), java.util.Map.of("player", target.getGameProfile().getName()))));
        return true;
    }
    private static net.minecraft.world.inventory.MenuType<ChestMenu> type(int rows) { return switch (rows) { case 1 -> net.minecraft.world.inventory.MenuType.GENERIC_9x1; case 2 -> net.minecraft.world.inventory.MenuType.GENERIC_9x2; case 3 -> net.minecraft.world.inventory.MenuType.GENERIC_9x3; case 4 -> net.minecraft.world.inventory.MenuType.GENERIC_9x4; case 5 -> net.minecraft.world.inventory.MenuType.GENERIC_9x5; default -> net.minecraft.world.inventory.MenuType.GENERIC_9x6; }; }
    private record Slot(AccessoriesContainer owner, int index) {}
    private static final class View implements Container {
        private final List<Slot> slots = new ArrayList<>(); private final int size;
        private View(List<AccessoriesContainer> containers, int size) { this.size = size; for (var container : containers) for (int index = 0; index < container.getSize() && slots.size() < size; index++) slots.add(new Slot(container, index)); }
        @Override public int getContainerSize() { return size; }
        @Override public boolean isEmpty() { return slots.stream().allMatch(slot -> slot.owner().getAccessories().getItem(slot.index()).isEmpty()); }
        @Override public ItemStack getItem(int slot) { return mapped(slot) ? slots.get(slot).owner().getAccessories().getItem(slots.get(slot).index()) : ItemStack.EMPTY; }
        @Override public ItemStack removeItem(int slot, int amount) { if (!mapped(slot)) return ItemStack.EMPTY; Slot ref = slots.get(slot); ItemStack result = ref.owner().getAccessories().removeItem(ref.index(), amount); ref.owner().markChanged(); return result; }
        @Override public ItemStack removeItemNoUpdate(int slot) { if (!mapped(slot)) return ItemStack.EMPTY; Slot ref = slots.get(slot); ItemStack result = ref.owner().getAccessories().removeItemNoUpdate(ref.index()); ref.owner().markChanged(); return result; }
        @Override public void setItem(int slot, ItemStack stack) { if (mapped(slot)) { Slot ref = slots.get(slot); ref.owner().getAccessories().setItem(ref.index(), stack); ref.owner().markChanged(); } }
        @Override public void setChanged() { slots.forEach(slot -> slot.owner().markChanged()); }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) { return mapped(slot) && slots.get(slot).owner().getAccessories().canPlaceItem(slots.get(slot).index(), stack); }
        @Override public void clearContent() { slots.forEach(slot -> { slot.owner().getAccessories().setItem(slot.index(), ItemStack.EMPTY); slot.owner().markChanged(); }); }
        private boolean mapped(int slot) { return slot >= 0 && slot < slots.size(); }
    }
    private AccessoriesInspection() {}
}
