package de.unknowncity.astralib.paper.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;

    private ItemBuilder(ItemStack itemStack) {
        this.item = itemStack.clone();
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
        var itemMeta = this.item.getItemMeta();
        itemMeta.displayName(name);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder lore(Component... lore) {
        var itemMeta = this.item.getItemMeta();
        itemMeta.lore(Arrays.asList(lore));
        this.item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        var itemMeta = this.item.getItemMeta();
        itemMeta.lore(lore);
        this.item.setItemMeta(itemMeta);
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
        var itemMeta = this.item.getItemMeta();
        itemMeta.setEnchantmentGlintOverride(glow);
        this.item.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack item() {
        return item;
    }
}
