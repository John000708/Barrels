package me.john000708.barrels.listeners;

import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

/**
 * Created by John on 10.05.2016.
 */

public class DisplayListener implements Listener {

    public DisplayListener() {
        Bukkit.getPluginManager().registerEvents(this, Barrels.plugin);
    }

    public static final String DISPLAY_NAME = "§6§lB4R3L - §eITEM";

    @EventHandler
    static void onPickup(EntityPickupItemEvent e) {
        if (!e.getItem().hasMetadata("no_pickup") && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().hasDisplayName() && e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(DISPLAY_NAME)) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }


    @EventHandler
    static void onInventoryPickup(InventoryPickupItemEvent e) {
        if (!e.getItem().hasMetadata("no_pickup") && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().hasDisplayName() && e.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(DISPLAY_NAME)) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(BlockPlaceEvent e) {
        if (!e.isCancelled()) { // Check if the block was actually placed
            DisplayItem.removeDisplayItem(e.getBlock().getRelative(0, -1, 0));
        }
    }
}
