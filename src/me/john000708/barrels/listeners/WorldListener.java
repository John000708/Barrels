package me.john000708.barrels.listeners;

import me.john000708.barrels.Barrels;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

/**
 * Created by John on 14.05.2016.
 */
public class WorldListener implements Listener {
    public WorldListener() {
        Bukkit.getPluginManager().registerEvents(this, Barrels.plugin);
    }

    @EventHandler
    public void onFireSpread(BlockBurnEvent e) {
        if (e.getBlock() == null) return;

        if (BlockStorage.hasBlockInfo(e.getBlock()) && BlockStorage.checkID(e.getBlock()).startsWith("BARREL_"))
            e.setCancelled(true);


    }
}
