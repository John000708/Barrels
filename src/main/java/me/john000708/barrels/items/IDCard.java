package me.john000708.barrels.items;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public class IDCard extends SimpleSlimefunItem<ItemInteractionHandler> {

	public IDCard(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
	}

	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				Block clickedBlock = e.getClickedBlock();
				
                ItemMeta meta = item.getItemMeta();
                if (!meta.hasLore()) return false;
                
                List<String> lore = item.getItemMeta().getLore();
                if (lore.size() != 2) return false;

                if (lore.get(0).equals("")) {
                    lore.set(0, ChatColor.translateAlternateColorCodes('&', "&0" + p.getUniqueId().toString()));
                    lore.set(1, ChatColor.translateAlternateColorCodes('&', "&fBound to: " + p.getName()));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    p.sendMessage(ChatColor.GREEN + "ID Card bound.");
                } 
                else if (clickedBlock != null && BlockStorage.hasBlockInfo(clickedBlock) && BlockStorage.checkID(clickedBlock).startsWith("BARREL_") && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist") != null && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "owner").equals(p.getUniqueId().toString())) {
                    String whitelistedPlayers = BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist");
                    
                    if (!whitelistedPlayers.contains(ChatColor.stripColor(lore.get(0)))) {
                        BlockStorage.addBlockInfo(clickedBlock, "whitelist", whitelistedPlayers + ChatColor.stripColor(lore.get(0)) + ";");
                        
                        ItemUtils.consumeItem(item, false);
                        p.sendMessage(ChatColor.GREEN + "Player successfully whitelisted!");
                    } 
                    else {
                        p.sendMessage(ChatColor.RED + "The player is already whitelisted.");
                    }
                }
				return true;
			}
			return false;
		};
	}

}
