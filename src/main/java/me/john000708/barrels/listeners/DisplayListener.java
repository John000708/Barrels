package me.john000708.barrels.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;

/**
 * Created by John on 10.05.2016.
 */
public class DisplayListener implements Listener {

    public DisplayListener(Barrels plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (DisplayItem.isDisplayItem(e.getEntity())) {
            e.setCancelled(true);
            if (!DisplayItem.isCurrentLocation(e.getEntity())) {
                DisplayItem.removeDisplayItem(e.getEntity());
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (DisplayItem.isDisplayItem(e.getItem())) {
            e.setCancelled(true);
            if (DisplayItem.isCurrentLocation(e.getItem())) {
                e.getItem().setPickupDelay(20);
            }
            else {
                DisplayItem.removeDisplayItem(e.getItem());
            }
        }
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent e) {
        if (DisplayItem.isDisplayItem(e.getItem())) {
            e.setCancelled(true);
            if (DisplayItem.isCurrentLocation(e.getItem())) {
                e.getItem().setPickupDelay(20);
            }
            else {
                DisplayItem.removeDisplayItem(e.getItem());
            }
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent e) {
        if (DisplayItem.isDisplayItem(e.getEntity())) {
            e.setCancelled(true);
            e.getTarget().remove();
        }
    }
}
