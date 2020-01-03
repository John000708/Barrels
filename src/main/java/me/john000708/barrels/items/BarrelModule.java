package me.john000708.barrels.items;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public abstract class BarrelModule extends SimpleSlimefunItem<ItemInteractionHandler> {
	
	public BarrelModule(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
	}
	
	public abstract boolean applyUpgrade(Block b);

	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				if (e.getClickedBlock() != null && BlockStorage.hasBlockInfo(e.getClickedBlock()) && BlockStorage.checkID(e.getClickedBlock()).startsWith("BARREL_")) {
                    if (applyUpgrade(e.getClickedBlock())) return true;
                    
                    ItemUtils.consumeItem(item, false);
                    p.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                }
				return true;
			}
			return false;
		};
	}

}
