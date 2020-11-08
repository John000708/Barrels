package me.john000708.barrels.block;

import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * Created by John on 06.05.2016.
 */
public class Barrel extends SlimefunItem {

    private final int capacity;

    public Barrel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int capacity) {
        super(category, item, recipeType, recipe);

        this.capacity = capacity;

        new BarrelsMenuPreset(this, item.getDisplayName());
        registerBlockHandler(getId(), new BarrelsBlockHandler(this));
    }

    @Override
    public void preRegister() {

        addItemHandler(
                new BlockPlaceHandler(false) {
                    @Override
                    public void onPlayerPlace(BlockPlaceEvent e) {
                        BlockStorage.addBlockInfo(e.getBlock() , "owner", e.getPlayer().getUniqueId().toString());
                    }
                },
                new BlockTicker() {
                    @Override
                    public boolean isSynchronized() {
                        return true;
                    }

                    @Override
                    public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                        updateBarrel(block);

                        if (Barrels.displayItem() && !block.isEmpty()) {
                            boolean hasRoom = block.getRelative(BlockFace.UP).isEmpty();
                            DisplayItem.updateDisplayItem(block, getCapacity(block), hasRoom);
                        }
                    }
                }
                );
    }

    public int getCapacity(Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "capacity") == null) {
            BlockStorage.addBlockInfo(b, "capacity", String.valueOf(this.capacity));
        }

        // There's no need to box the integer.
        return Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
    }

    public int[] getInputSlots() {
        return new int[] { 10 };
    }

    public int[] getOutputSlots() {
        return new int[] { 16 };
    }

    public ItemStack getStoredItem(DirtyChestMenu inventory) {
        ItemStack item = inventory.getItemInSlot(22);
        return item.getType() == Material.BARRIER ? null : item;
    }

    protected void updateCapacityItem(BlockMenu inventory, int capacity, int storedItem) {
        StringBuilder bar = new StringBuilder(64);
        float rate = (float) storedItem / (float) capacity;

        bar.append("&8[");

        switch((int) (rate * 4)) {
            case 0 : bar.append("&2");
            case 1 : bar.append("&a");
            case 2 : bar.append("&e");
            default : bar.append("&c");
        }

        String gauge = "::::::::::::::::::::";
        bar.append(gauge);
        bar.insert(bar.length() - gauge.length() + Math.max(0, Math.min(gauge.length(), (int) (gauge.length() * rate))), "&7");

        bar.append("&8] &7- ").append((int) (rate * 100.0F)).append("%");

        inventory.replaceExistingItem(4, new CustomItem(Material.CAULDRON, "&7" + storedItem + "/" + capacity, bar.toString()), false);
    }

    void updateBarrel(Block b) {
        BlockMenu inventory = BlockStorage.getInventory(b);

        if (inventory == null) {
            return;
        }

        int capacity = getCapacity(b);
        String storedAmountInfo = BlockStorage.getLocationInfo(b.getLocation(), "storedItems");
        int storedAmount = storedAmountInfo == null ? 0 : Integer.parseInt(storedAmountInfo);

        for (int slot : getInputSlots()) {
            if (inventory.getItemInSlot(slot) == null)
                continue;

            ItemStack input = inventory.getItemInSlot(slot);

            if (input.getType() == Material.BARRIER) {
                continue;
            }

            if (SlimefunUtils.isItemSimilar(input, getStoredItem(inventory), true, false)) {

                if (storedAmount < capacity) {
                    if (storedAmount + input.getAmount() > capacity) {
                        input.setAmount(storedAmount + input.getAmount() - capacity);
                        inventory.replaceExistingItem(slot, input, false);
                        storedAmount = capacity;
                    }
                    else {
                        inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                        storedAmount += input.getAmount();
                    }
                    BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
                    updateCapacityItem(inventory, capacity, storedAmount);
                }
            }
            else if (getStoredItem(inventory) == null) {
                storedAmount = input.getAmount();

                input.setAmount(input.getMaxStackSize());
                inventory.replaceExistingItem(22, input, false);
                inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);

                BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
                updateCapacityItem(inventory, capacity, storedAmount);
            }
        }

        if (storedAmount == 0) {
            return;
        }

        for (int slot : getOutputSlots()) {
            ItemStack requestedItem = inventory.getItemInSlot(slot);
            ItemStack output = getStoredItem(inventory).clone();

            int pushAmount = output.getMaxStackSize();

            if (requestedItem == null || requestedItem.getType() == Material.AIR) {
                pushAmount = Math.min(storedAmount, pushAmount);
                output.setAmount(pushAmount);
            }
            else {
                if (requestedItem.getAmount() == requestedItem.getMaxStackSize() ||
                        !SlimefunUtils.isItemSimilar(requestedItem, output, true, false)) {
                    continue;
                }

                pushAmount = Math.min(storedAmount, requestedItem.getMaxStackSize() - requestedItem.getAmount());
                output.setAmount(requestedItem.getAmount() + pushAmount);
            }

            inventory.replaceExistingItem(slot, output);
            storedAmount -= pushAmount;


            if (storedAmount <= 0) {
                BlockStorage.addBlockInfo(b, "storedItems", null);
                inventory.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7Empty"), false);
            }
            else {
                BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount));
            }
            updateCapacityItem(inventory, capacity, storedAmount);
        }
    }
}
