package me.john000708.barrels.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

class BarrelsBlockHandler implements SlimefunBlockHandler {

    private final Barrel barrel;

    public BarrelsBlockHandler(Barrel barrel) {
        this.barrel = barrel;
    }

    @Override
    public boolean onBreak(Player player, Block b, SlimefunItem slimefunItem, UnregisterReason unregisterReason) {
        if (unregisterReason.equals(UnregisterReason.EXPLODE)) {
            if (BlockStorage.getLocationInfo(b.getLocation(), "explosion") != null) {
                // This Barrel has been protected from explosions
                return false;
            }
        }
        else if (unregisterReason.equals(UnregisterReason.PLAYER_BREAK)) {
            // Only the Owner may break this Barrel
            String owner = BlockStorage.getLocationInfo(b.getLocation(), "owner");
            if (owner != null && !owner.equals(player.getUniqueId().toString())) {
                return false;
            }
        }

        DisplayItem.removeDisplayItem(b);

        BlockMenu inv = BlockStorage.getInventory(b);

        if (BlockStorage.getLocationInfo(b.getLocation(), "explosion") != null) {
            b.getWorld().dropItem(b.getLocation(), SlimefunItem.getByID("BARREL_EXPLOSION_MODULE").getItem());
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_1") != null) {
            b.getWorld().dropItem(b.getLocation(), SlimefunItem.getByID("STRUCT_UPGRADE_1").getItem());
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_2") != null) {
            b.getWorld().dropItem(b.getLocation(), SlimefunItem.getByID("STRUCT_UPGRADE_2").getItem());
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_3") != null) {
            b.getWorld().dropItem(b.getLocation(), SlimefunItem.getByID("STRUCT_UPGRADE_3").getItem());
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "protected") != null) {
            b.getWorld().dropItem(b.getLocation(), SlimefunItem.getByID("BARREL_BIO_PROTECTION").getItem());
        }

        inv.dropItems(b.getLocation(), barrel.getInputSlots());
        inv.dropItems(b.getLocation(), barrel.getOutputSlots());

        String storedAmountString = BlockStorage.getLocationInfo(b.getLocation(), "storedItems");
        if (storedAmountString == null) {
            return true;
        }
        int storedAmount = Integer.parseInt(storedAmountString);

        ItemStack item = inv.getItemInSlot(22);

        while (storedAmount > 0) {
            int amount = item.getMaxStackSize();

            if (storedAmount > amount) {
                storedAmount -= amount;
            }
            else {
                amount = storedAmount;
                storedAmount = 0;
            }

            b.getWorld().dropItem(b.getLocation(), new CustomItem(item, amount));
        }
        BlockStorage.addBlockInfo(b, "storedItems", null);
        inv.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7Empty"), false);

        return true;
    }
}
