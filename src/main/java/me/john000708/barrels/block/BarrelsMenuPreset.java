package me.john000708.barrels.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

class BarrelsMenuPreset extends BlockMenuPreset {

    private static final int[] border1 = { 0, 1, 2, 9, 11, 18, 19, 20 };
    private static final int[] border2 = { 3, 5, 12, 13, 14, 21, 23 };
    private static final int[] border3 = { 6, 7, 8, 15, 17, 24, 25, 26 };

    private final Barrel barrel;

    public BarrelsMenuPreset(Barrel barrel) {
        super(barrel.getId(), barrel.getInventoryTitle());

        this.barrel = barrel;
    }

    @Override
    public void init() {
        constructMenu(this);
    }

    @Override
    public void newInstance(BlockMenu menu, Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
            menu.replaceExistingItem(4, new CustomItem(Material.BARRIER, "&7Empty"), false);
            menu.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7Empty"), false);
        }

        if (Barrels.displayItem()) {
            boolean hasRoom = b.getRelative(BlockFace.UP).getType() == Material.AIR;
            DisplayItem.updateDisplayItem(b, barrel.getCapacity(b), hasRoom);
        }
    }
    
    @Override
    protected ItemStack onItemStackChange(DirtyChestMenu menu, int slot, ItemStack previous, ItemStack next) {
        barrel.updateBarrel(((BlockMenu) menu).getBlock());
        return next;
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int i : border1) {
            preset.addItem(i, new CustomItem(Material.CYAN_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border2) {
            preset.addItem(i, new CustomItem(Material.BLACK_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        for (int i : border3) {
            preset.addItem(i, new CustomItem(Material.ORANGE_STAINED_GLASS_PANE, " "), (p, j, stack, action) -> false);
        }

        preset.addMenuClickHandler(4, ChestMenuUtils.getEmptyClickHandler());
        preset.addMenuClickHandler(22, ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    public boolean canOpen(Block b, Player p) {
        boolean protect = BlockStorage.getLocationInfo(b.getLocation(), "protected") == null || BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || (BlockStorage.getLocationInfo(b.getLocation(), "whitelist") != null && BlockStorage.getLocationInfo(b.getLocation(), "whitelist").contains(p.getUniqueId().toString()));

        return p.hasPermission("slimefun.inventory.bypass") || protect;
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
        return new int[0];
    }

    @Override
    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        if (flow == ItemTransportFlow.INSERT) {
            if (BlockStorage.getLocationInfo(((BlockMenu) menu).getLocation(), "storedItems") != null) {
                if (SlimefunUtils.isItemSimilar(item, menu.getItemInSlot(22), true, false)) {
                    return barrel.getInputSlots();
                }
                else {
                    return new int[0];
                }
            }
            else {
                return barrel.getInputSlots();
            }
        }
        else {
            return barrel.getOutputSlots();
        }
    }

}
