package me.john000708.barrels;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.Slimefun.SlimefunStartup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * Created by John on 10.05.2016.
 */
public class DisplayItem {
	
    public static void updateDisplayItem(Block b, int capacity) {
    	ItemStack stack = new CustomItem(new ItemStack(Material.BARRIER), 1);
    	String nametag = "§cEmpty";
    	
    	BlockMenu menu = BlockStorage.getInventory(b);
    	if (BlockStorage.getBlockInfo(b, "storedItems") != null) {
            int storedItems = Integer.valueOf(BlockStorage.getBlockInfo(b, "storedItems"));
            stack = menu.getItemInSlot(22).clone();
            nametag = ChatColor.translateAlternateColorCodes('&', Barrels.config.getString("options.item-format"));
            nametag = nametag.replace("<storedAmount>", String.valueOf(storedItems));
            nametag = nametag.replace("<storedPercentage>", String.valueOf(Math.round((float) storedItems / (float) capacity * 100.0F)));
            nametag = nametag.replace("<storedItem>", StringUtils.formatItemName(stack, false));
        }
    	
        Item entity = getEntity(b);
        if (entity == null) {
        	entity = b.getWorld().dropItem(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.2D, b.getZ() + 0.5D), new CustomItem(stack, "§6§lB4R3L - §eITEM" + System.nanoTime()));
        	entity.setVelocity(new Vector(0, 0.1, 0));
            entity.setMetadata("no_pickup", new FixedMetadataValue(SlimefunStartup.instance, "barrel"));
            entity.setCustomNameVisible(true);
        }
        else {
        	entity.setItemStack(new CustomItem(stack, "§6§lB4R3L - §eITEM" + System.nanoTime()));
        }
        
        entity.setCustomName(nametag);
    }

    public static void removeDisplayItem(Block b) {
        for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item) {
                if (b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 1D && ((Item) n).getItemStack().hasItemMeta() && ((Item) n).getItemStack().getItemMeta().getDisplayName().startsWith("§6§lB4R3L - §eITEM"))
                    n.remove();
            }
        }
    }
    
    private static Item getEntity(Block b) {
    	for (Entity n : b.getChunk().getEntities()) {
            if (n instanceof Item) {
                if (b.getLocation().add(0.5, 1.2, 0.5).distanceSquared(n.getLocation()) < 1D && ((Item) n).getItemStack().hasItemMeta() && ((Item) n).getItemStack().getItemMeta().getDisplayName().startsWith("§6§lB4R3L - §eITEM"))
                return (Item) n;
            }
        }
    	return null;
    }
}
