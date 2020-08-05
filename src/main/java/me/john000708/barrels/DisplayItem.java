package me.john000708.barrels;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.data.PersistentDataAPI;

/**
 * Created by John on 10.05.2016.
 */
public final class DisplayItem {

    public static final NamespacedKey NAMESPACED_KEY = new NamespacedKey(Barrels.getInstance(), "display_item");

    private DisplayItem() {}

    public static void updateDisplayItem(Block b, int capacity, boolean allow) {
        if (!allow) {
            removeDisplayItem(b);
            return;
        }

        ItemStack stack = new ItemStack(Material.BARRIER, 1);
        String nametag = ChatColor.RED + "Empty";
        BlockMenu menu = BlockStorage.getInventory(b);

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") != null) {
            int storedItems = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
            stack = menu.getItemInSlot(22).clone();
            stack.setAmount(1);
            nametag = ChatColor.translateAlternateColorCodes('&', Barrels.getItemFormat());
            nametag = nametag.replace("<storedAmount>", String.valueOf(storedItems));
            nametag = nametag.replace("<storedPercentage>", String.valueOf(Math.round((float) storedItems / (float) capacity * 100.0F)));
            nametag = nametag.replace("<storedItem>", stack.getItemMeta().getDisplayName());
        }

        Optional<Item> entity = getEntity(b);
        Item item;

        if (!entity.isPresent()) {
            item = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.2D, b.getZ() + 0.5D), stack);
            item.setVelocity(new Vector(0, 0.1, 0));
            item.setCustomNameVisible(true);

            PersistentDataAPI.setString(item, NAMESPACED_KEY, toString(b));
        }
        else {
            item = entity.get();
            item.setItemStack(stack);
        }

        item.setCustomName(nametag);
        item.setInvulnerable(true);

        if (!SlimefunUtils.hasNoPickupFlag(item)) {
            SlimefunUtils.markAsNoPickup(item, "barrel");
        }
    }

    public static void removeDisplayItem(Block b) {
        getEntity(b).ifPresent(item -> {
            item.remove();
            item.removeMetadata("no_pickup", Barrels.getInstance());
        });
    }

    private static Optional<Item> getEntity(Block b) {
        for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item && b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 1.25) {
                Item item = (Item) n;

                Optional<String> data = PersistentDataAPI.getOptionalString(item, NAMESPACED_KEY);

                if (data.isPresent() && data.get().equals(toString(b))) {
                    return Optional.of(item);
                }
            }
        }

        return Optional.empty();
    }

    private static String toString(Block b) {
        return b.getWorld().getUID().toString() + " | " + b.getX() + ", " + b.getY() + ", " + b.getZ();
    }
}
