package yofred.dev.justessentials;

import com.mojang.authlib.GameProfile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

final class OfflineInspection {
    static String open(ServerPlayer viewer, String playerName, boolean enderChest) {
        MinecraftServer server = viewer.server;
        ServerPlayer online = server.getPlayerList().getPlayerByName(playerName);
        if (online != null) {
            viewer.openMenu(enderChest ? InspectionMenus.enderChest(online) : InspectionMenus.playerInventory(online));
            return null;
        }
        Optional<GameProfile> profile = server.getProfileCache().get(playerName);
        if (profile.isEmpty()) return "Unknown player. They must have joined this server before.";
        Path file = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(profile.get().getId() + ".dat");
        if (!Files.exists(file)) return "No saved player data exists for " + playerName + ".";
        try {
            CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
            ListTag list = root.getList(enderChest ? "EnderItems" : "Inventory", CompoundTag.TAG_COMPOUND);
            int size = enderChest ? 27 : 54;
            ItemStack[] items = new ItemStack[size];
            java.util.Arrays.fill(items, ItemStack.EMPTY);
            for (int index = 0; index < list.size(); index++) {
                CompoundTag itemTag = list.getCompound(index);
                int rawSlot = itemTag.getByte("Slot") & 255;
                int menuSlot = enderChest ? rawSlot : inventorySlot(rawSlot);
                if (menuSlot >= 0 && menuSlot < size)
                    ItemStack.parse(server.registryAccess(), itemTag).ifPresent(stack -> items[menuSlot] = stack);
            }
            Container snapshot = new ReadOnlyContainer(items);
            int rows = enderChest ? 3 : 6;
            viewer.openMenu(new SimpleMenuProvider((id, inventory, player) -> rows == 3
                    ? ChestMenu.threeRows(id, inventory, snapshot)
                    : ChestMenu.sixRows(id, inventory, snapshot), Component.literal(playerName + (enderChest ? " - Offline Ender Chest" : " - Offline Inventory (Read Only)"))));
            return null;
        } catch (Exception exception) {
            JustEssentials.LOGGER.error("Unable to inspect offline player {}", playerName, exception);
            return "Unable to read that player's saved data.";
        }
    }

    private static int inventorySlot(int raw) {
        if (raw >= 0 && raw <= 35) return raw;
        if (raw >= 100 && raw <= 103) return 36 + (raw - 100);
        if (raw == 150) return 40;
        return -1;
    }

    private static final class ReadOnlyContainer implements Container {
        private final ItemStack[] items;
        private ReadOnlyContainer(ItemStack[] items) { this.items = items; }
        @Override public int getContainerSize() { return items.length; }
        @Override public boolean isEmpty() { return java.util.Arrays.stream(items).allMatch(ItemStack::isEmpty); }
        @Override public ItemStack getItem(int slot) { return slot >= 0 && slot < items.length ? items[slot] : ItemStack.EMPTY; }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) {}
        @Override public void setChanged() {}
        @Override public boolean stillValid(Player player) { return true; }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
        @Override public void clearContent() {}
    }
    private OfflineInspection() {}
}
