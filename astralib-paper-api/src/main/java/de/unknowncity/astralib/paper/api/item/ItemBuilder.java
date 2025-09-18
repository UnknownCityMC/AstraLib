package de.unknowncity.astralib.paper.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta itemMeta;

    private ItemBuilder(ItemStack itemStack) {
        this.item = itemStack.clone();
        this.itemMeta = itemStack.getItemMeta();
    }

    public static ItemBuilder of(final Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static ItemBuilder of(final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder amount(final int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public static ItemBuilder of(final Material material, int amount) {
        return new ItemBuilder(new ItemStack(material, amount));
    }

    public ItemBuilder name(Component name) {
        itemMeta.displayName(name);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        itemMeta.lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        itemMeta.lore(lore);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment) {
        this.item.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        itemMeta.setEnchantmentGlintOverride(glow);
        return this;
    }

    public ItemBuilder itemModel(NamespacedKey namespacedKey) {
        itemMeta.setItemModel(namespacedKey);
        return this;
    }

    public ItemStack item() {
        this.item.setItemMeta(itemMeta);
        return item;
    }
}
