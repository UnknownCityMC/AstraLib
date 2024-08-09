package de.unknowncity.astralib.paper.api.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class InventoryUtil {
    public static boolean hasEnoughItems(Player player, ItemStack itemStack, int amount) {
        var possibleItems = Arrays.stream(player.getInventory().getStorageContents())
                .filter(Objects::nonNull)
                .filter(item -> item.isSimilar(itemStack));

        var itemCount = possibleItems.filter(Objects::nonNull).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
        return itemCount >= amount;
    }

    public static void removeSpecificItemCount(Player player, ItemStack itemStack, int amount) {
        var countToRemove = amount;
        var left = amount;
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (countToRemove == 0) {
                return;
            }
            left = countToRemove;
            if (content != null && content.isSimilar(itemStack)) {
                for (int i = left; i > 0; i--) {
                    if (countToRemove == 0) {
                        return;
                    }
                    countToRemove--;
                    if (content.getAmount() == 1) {
                        player.getInventory().remove(content);
                        break;
                    }
                    content.setAmount(content.getAmount() - 1);
                }
            }
        }
    }

    public static boolean hasFreeSlot(Player player) {
        return Arrays.stream(player.getInventory().getStorageContents()).anyMatch(Objects::isNull);
    }

    public static boolean hasEnoughSpace(Player player, ItemStack itemStack, int amount) {
        var storageContent = player.getInventory().getStorageContents();
        var totalSpaceForItemType = (Arrays.stream(storageContent).filter(Objects::isNull).count()
                * itemStack.getMaxStackSize())
                + Arrays.stream(storageContent)
                .filter(currentItem -> currentItem.isSimilar(itemStack))
                .mapToInt(currentItem -> currentItem.getMaxStackSize() - currentItem.getAmount())
                .sum();
        return totalSpaceForItemType > amount;
    }
}
