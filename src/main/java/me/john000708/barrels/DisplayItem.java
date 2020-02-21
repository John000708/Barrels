package me.john000708.barrels;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * Created by John on 10.05.2016.
 */
public class DisplayItem {
	
	private static final String ITEM_DATA = ChatColor.translateAlternateColorCodes('&', "&6&lB4R3L - &eITEM");

    public static void updateDisplayItem(Block b, int capacity, boolean allow) {
        if (!allow) {
            removeDisplayItem(b);
            return;
        }

    	ItemStack stack = new ItemStack(Material.BARRIER, 1);
    	String nametag = ChatColor.translateAlternateColorCodes('&', "&cEmpty");

    	BlockMenu menu = BlockStorage.getInventory(b);
    	
    	if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") != null) {
            int storedItems = Integer.valueOf(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
            stack = menu.getItemInSlot(22).clone();
            nametag = ChatColor.translateAlternateColorCodes('&', Barrels.getItemFormat());
            nametag = nametag.replace("<storedAmount>", String.valueOf(storedItems));
            nametag = nametag.replace("<storedPercentage>", String.valueOf(Math.round((float) storedItems / (float) capacity * 100.0F)));
            nametag = nametag.replace("<storedItem>", stack.getItemMeta().getDisplayName());
        }

        Optional<Item> entity = getEntity(b);
        
        if (!entity.isPresent()) {
        	Item item = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.2D, b.getZ() + 0.5D), new CustomItem(stack, ITEM_DATA + System.nanoTime()));
        	item.setVelocity(new Vector(0, 0.1, 0));
        	item.setMetadata("no_pickup", new FixedMetadataValue(Barrels.getInstance(), "barrel"));
        	item.setCustomNameVisible(true);
        	item.setCustomName(nametag);
        	item.setInvulnerable(true);
        }
        else {
        	Item item = entity.get();
        	item.setItemStack(new CustomItem(stack, ITEM_DATA + System.nanoTime()));
        	item.setCustomName(nametag);
        	item.setInvulnerable(true);
        }
    }

    public static void removeDisplayItem(Block b) {
        getEntity(b).ifPresent(Item::remove);
    }

    private static Optional<Item> getEntity(Block b) {
    	for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item && b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 1D) {
            	Item item = (Item) n;
            	
                if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().getDisplayName().startsWith(ITEM_DATA)) {
                	return Optional.of(item);
                }
            }
        }
    	
    	return Optional.empty();
    }
}
