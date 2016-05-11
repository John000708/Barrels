package me.john000708.barrels;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Created by John on 10.05.2016.
 */
public class DisplayItem {
    public static void updateDisplayItem(Block b, int capacity) {
        Item entity = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.2D, b.getZ() + 0.5D), new CustomItem(new CustomItem(new ItemStack(Material.BARRIER), 1), "§6§lB4R3L - §eITEM" + System.nanoTime()));

        for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item) {
                if (b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 0.5D && n.getCustomName() != null) {
                    entity.remove();
                    entity = (Item) n;
                    break;
                }
            }
        }
        entity.setVelocity(new Vector(0, 0.1, 0));
        entity.setMetadata("no_pickup", new FixedMetadataValue(SlimefunStartup.instance, "barrel"));
        entity.setCustomNameVisible(true);
        BlockMenu menu = BlockStorage.getInventory(b);
        if (menu.getItemInSlot(22).getType() == Material.BARRIER) {
            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', "&cEmpty"));
            entity.setItemStack(new ItemStack(Material.BARRIER));
        } else {
            int storedItems = Integer.valueOf(BlockStorage.getBlockInfo(b, "storedItems"));
            ItemStack itemStack = menu.getItemInSlot(22);
            itemStack.setAmount(1);
            entity.setItemStack(itemStack);
            entity.setCustomName(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&9" + storedItems + "x &8(&e" + Math.round((float) storedItems / (float) capacity * 100.0F) + "%&8)"));
        }
    }

    public static void removeDisplayItem(Block b) {
        for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item) {
                if (b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 0.5D && n.getCustomName() != null)
                    n.remove();
            }
        }
    }
}
