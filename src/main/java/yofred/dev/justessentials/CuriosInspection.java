package yofred.dev.justessentials;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

final class CuriosInspection {
    static boolean open(ServerPlayer viewer, ServerPlayer target) {
        var capability = CuriosApi.getCuriosInventory(target);
        if (capability.isEmpty()) return false;

        List<IDynamicStackHandler> handlers = new ArrayList<>();
        capability.get().getCurios().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(entry -> handlers.add(entry.getValue().getStacks()));
        int slots = handlers.stream().mapToInt(IDynamicStackHandler::getSlots).sum();
        int rows = Math.max(1, Math.min(6, (slots + 8) / 9));
        Container container = new CuriosView(handlers, rows * 9);
        viewer.openMenu(new SimpleMenuProvider(
                (id, inventory, player) -> new ChestMenu(menuType(rows), id, inventory, container, rows),
                Component.literal(target.getGameProfile().getName() + " - Curios")));
        return true;
    }

    private static net.minecraft.world.inventory.MenuType<ChestMenu> menuType(int rows) {
        return switch (rows) {
            case 1 -> net.minecraft.world.inventory.MenuType.GENERIC_9x1;
            case 2 -> net.minecraft.world.inventory.MenuType.GENERIC_9x2;
            case 3 -> net.minecraft.world.inventory.MenuType.GENERIC_9x3;
            case 4 -> net.minecraft.world.inventory.MenuType.GENERIC_9x4;
            case 5 -> net.minecraft.world.inventory.MenuType.GENERIC_9x5;
            default -> net.minecraft.world.inventory.MenuType.GENERIC_9x6;
        };
    }

    private record SlotRef(IDynamicStackHandler handler, int slot) {}

    private static final class CuriosView implements Container {
        private final List<SlotRef> slots = new ArrayList<>();
        private final int menuSize;

        private CuriosView(List<IDynamicStackHandler> handlers, int menuSize) {
            this.menuSize = menuSize;
            for (IDynamicStackHandler handler : handlers)
                for (int slot = 0; slot < handler.getSlots() && slots.size() < menuSize; slot++) slots.add(new SlotRef(handler, slot));
        }

        @Override public int getContainerSize() { return menuSize; }
        @Override public boolean isEmpty() { return slots.stream().allMatch(ref -> ref.handler().getStackInSlot(ref.slot()).isEmpty()); }
        @Override public ItemStack getItem(int slot) { return mapped(slot) ? slots.get(slot).handler().getStackInSlot(slots.get(slot).slot()) : ItemStack.EMPTY; }
        @Override public ItemStack removeItem(int slot, int amount) { return mapped(slot) ? slots.get(slot).handler().extractItem(slots.get(slot).slot(), amount, false) : ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) {
            if (!mapped(slot)) return ItemStack.EMPTY;
            SlotRef ref = slots.get(slot);
            ItemStack result = ref.handler().getStackInSlot(ref.slot());
            ref.handler().setStackInSlot(ref.slot(), ItemStack.EMPTY);
            return result;
        }
        @Override public void setItem(int slot, ItemStack stack) { if (mapped(slot)) { SlotRef ref = slots.get(slot); ref.handler().setStackInSlot(ref.slot(), stack); } }
        @Override public void setChanged() {}
        @Override public boolean stillValid(Player player) { return true; }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) { return mapped(slot) && slots.get(slot).handler().isItemValid(slots.get(slot).slot(), stack); }
        @Override public void clearContent() { slots.forEach(ref -> ref.handler().setStackInSlot(ref.slot(), ItemStack.EMPTY)); }
        private boolean mapped(int slot) { return slot >= 0 && slot < slots.size(); }
    }

    private CuriosInspection() {}
}
