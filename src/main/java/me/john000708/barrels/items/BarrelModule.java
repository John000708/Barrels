package me.john000708.barrels.items;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.john000708.barrels.block.Barrel;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public abstract class BarrelModule extends SimpleSlimefunItem<ItemUseHandler> {

    public BarrelModule(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    public abstract boolean applyUpgrade(Block b);

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();
            if (e.getSlimefunBlock().isPresent()) {
                SlimefunItem barrel = e.getSlimefunBlock().get();

                if (barrel instanceof Barrel) {
                    if (applyUpgrade(e.getClickedBlock().get())) {
                        ItemUtils.consumeItem(e.getItem(), false);
                        e.getPlayer().sendMessage(ChatColor.GREEN + "Module successfully applied!");
                    } else {
                        e.getPlayer().sendMessage(ChatColor.RED + "That Module is already applied!");
                    }

                }
            }
        };
    }

}
