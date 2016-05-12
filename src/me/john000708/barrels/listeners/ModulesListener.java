package me.john000708.barrels.listeners;

import me.john000708.barrels.Barrels;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;

/**
 * Created by John on 12.05.2016.
 */
public class ModulesListener implements Listener {
    public ModulesListener() {
        Bukkit.getPluginManager().registerEvents(this, Barrels.plugin);
    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        Iterator<Block> iterator = e.blockList().iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();

            if (BlockStorage.hasBlockInfo(block) && BlockStorage.checkID(block).startsWith("BARREL_") && Boolean.valueOf(BlockStorage.getBlockInfo(block, "explosion"))) {
                iterator.remove();
            }
        }
    }
}
