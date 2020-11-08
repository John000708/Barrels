package me.john000708.barrels;
import java.util.Optional;

import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.data.PersistentDataAPI;

/**
 * Created by John on 10.05.2016.
 */
public final class DisplayItem {

    public static final NamespacedKey NAMESPACED_KEY = new NamespacedKey(Barrels.getInstance(), "display_item");
    public static final String NAME = ChatColor.translateAlternateColorCodes('&', "&cBarrelDisplayItem&8-&7");

    private DisplayItem() {}

    public static void updateDisplayItem(Block b, int capacity, boolean allow) {
        if (!allow) {
            removeDisplayItem(b);
            return;
        }

        ItemStack displayItem = new ItemStack(Material.BARRIER, 1);
        String nametag = ChatColor.RED + "Empty";
        BlockMenu menu = BlockStorage.getInventory(b);

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") != null) {
            int storedItems = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
            displayItem = menu.getItemInSlot(22).clone();
            displayItem.setAmount(1);
            nametag = ChatColor.translateAlternateColorCodes('&', Barrels.getItemFormat());
            nametag = nametag.replace("<storedAmount>", String.valueOf(storedItems));
            nametag = nametag.replace("<storedPercentage>", String.valueOf((int)((float) storedItems / (float) capacity * 100.0F)));
            nametag = nametag.replace("<storedItem>", ItemUtils.getItemName(displayItem));
        }
        ItemMeta meta = displayItem.getItemMeta();
        meta.setDisplayName(NAME + toString(b));
        displayItem.setItemMeta(meta);

        Item item;

        Optional<ArmorStand> base = getEntity(b);
        if (base.isPresent()) {
            ArmorStand stand = base.get();
            if (stand.getPassengers().size() == 1) {
                Entity e = stand.getPassengers().get(0);
                if (e instanceof Item && PersistentDataAPI.hasString(e, NAMESPACED_KEY)) {
                    item = (Item) e;
                    item.setItemStack(displayItem);
                }
                else {
                    item = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.0D, b.getZ() + 0.5D), displayItem);
                    item.setCustomNameVisible(true);
                    PersistentDataAPI.setString(item, NAMESPACED_KEY, toString(b));

                    stand.getPassengers().forEach(Entity::remove);
                    stand.addPassenger(item);
                }
            }
            else {
                item = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.0D, b.getZ() + 0.5D), displayItem);
                item.setCustomNameVisible(true);
                PersistentDataAPI.setString(item, NAMESPACED_KEY, toString(b));

                stand.getPassengers().forEach(Entity::remove);
                stand.addPassenger(item);
            }

        }
        else {
            ArmorStand stand = (ArmorStand) b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.0D, b.getZ() + 0.5D), EntityType.ARMOR_STAND);
            stand.setGravity(false);
            stand.setVisible(false);
            stand.setSmall(true);
            stand.setMarker(true);
            stand.setInvulnerable(true);
            PersistentDataAPI.setString(stand, NAMESPACED_KEY, toString(b));

            item = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.0D, b.getZ() + 0.5D), displayItem);
            item.setCustomNameVisible(true);
            PersistentDataAPI.setString(item, NAMESPACED_KEY, toString(b));

            stand.addPassenger(item);
        }

        item.setCustomName(nametag);
        item.setInvulnerable(true);

        if (!SlimefunUtils.hasNoPickupFlag(item)) {
            SlimefunUtils.markAsNoPickup(item, "barrel");
        }
    }

    public static void removeDisplayItem(Block b) {
        getEntity(b).ifPresent(stand -> {
            stand.getPassengers().forEach(Entity::remove);
            stand.remove();
        });
    }

    public static void removeDisplayItem(Item item) {
        for (Entity stand : item.getNearbyEntities(0.1, 1, 0.1)) {
            if (stand instanceof ArmorStand && stand.getPassengers().contains(item)) {
                stand.getPassengers().forEach(Entity::remove);
                stand.remove();
            }
        }
        if (item.isValid()) {
            item.remove();
        }
    }

    public static void removeDisplayItem(Entity entity) {
        if (entity instanceof ArmorStand) {
            entity.getPassengers().forEach(Entity::remove);
            entity.remove();
        }
        else if (entity instanceof Item) {
            removeDisplayItem((Item) entity);
        }
    }

    private static Optional<ArmorStand> getEntity(Block b) {
        for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof ArmorStand && b.getLocation().add(0.5, 1.0, 0.5).distanceSquared(n.getLocation()) < 1.25) {
                ArmorStand stand = (ArmorStand) n;

                Optional<String> data = PersistentDataAPI.getOptionalString(stand, NAMESPACED_KEY);

                if (data.isPresent() && data.get().equals(toString(b))) {
                    return Optional.of(stand);
                }
            }
        }

        return Optional.empty();
    }

    public static boolean isDisplayItem(Entity entity) {
        return PersistentDataAPI.hasString(entity, NAMESPACED_KEY);
    }

    public static boolean isCurrentLocation(Entity entity) {
        Optional<String> data = PersistentDataAPI.getOptionalString(entity, DisplayItem.NAMESPACED_KEY);
        Block b = entity.getWorld().getBlockAt(entity.getLocation().add(0, -1, 0));
        String id = BlockStorage.checkID(b);
        return data.isPresent() && data.get().equals(toString(b)) && id != null && id.startsWith("BARREL_");
    }

    private static String toString(Block b) {
        return b.getWorld().getUID().toString() + " | " + b.getX() + ", " + b.getY() + ", " + b.getZ();
    }
}
