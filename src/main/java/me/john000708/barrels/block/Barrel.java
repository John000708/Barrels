package me.john000708.barrels.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.john000708.barrels.Barrels;
import me.john000708.barrels.DisplayItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

/**
 * Created by John on 06.05.2016.
 */
public abstract class Barrel extends SimpleSlimefunItem<BlockTicker> {

	protected static final String LORE_DATA = ChatColor.translateAlternateColorCodes('&', "&b&a&r&r&e&l");
	
    private static final int[] border1 = {0, 1, 2, 9, 11, 18, 19, 20};
    private static final int[] border2 = {3, 5, 12, 13, 14, 21, 23};
    private static final int[] border3 = {6, 7, 8, 15, 17, 24, 25, 26};

    private int capacity;
    private boolean allowDisplayItem;

    protected Barrel(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int capacity) {
        super(category, item, recipeType, recipe);

        this.capacity = capacity;

        new BlockMenuPreset(getID(), getInventoryTitle()) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {

                registerEvent((slot, prev, next) -> {
                	 updateBarrel(b);
                     return next;
                });

                if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
                    menu.replaceExistingItem(4, new CustomItem(Material.BARRIER, "&7Empty"), false);
                    menu.replaceExistingItem(22, new CustomItem(Material.BARRIER, "&7Empty"), false);
                }

                if (Barrels.displayItem()) {
                    allowDisplayItem = b.getRelative(BlockFace.UP).getType() == Material.AIR;

                    DisplayItem.updateDisplayItem(b, getCapacity(b), allowDisplayItem);
                }
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
                    	return isSimilar(item, menu.getItemInSlot(22)) ? getInputSlots() : new int[0];
                    }
                    else return getInputSlots();
                } 
                else return getOutputSlots();
            }
        };

        registerBlockHandler(getID(), new BarrelsBlockHandler(this));
    }
    
    public abstract String getInventoryTitle();

	@Override
	public BlockTicker getItemHandler() {
		return new BlockTicker() {

            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                updateBarrel(block);

                if (Barrels.displayItem()) {
                    allowDisplayItem = block.getRelative(BlockFace.UP).getType() == Material.AIR;
                    DisplayItem.updateDisplayItem(block, getCapacity(block), allowDisplayItem);
                }
            }
        };
	}

    public int getCapacity(Block b) {
        if (BlockStorage.getLocationInfo(b.getLocation(), "capacity") == null) {
            BlockStorage.addBlockInfo(b, "capacity", String.valueOf(this.capacity));
        }

        //There's no need to box the integer.
        return Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
    }
    
    public int[] getInputSlots() {
        return new int[] {10};
    }
    
    public int[] getOutputSlots() {
        return new int[] {16};
    }

    private ItemStack getCapacityItem(Block b) {
        StringBuilder bar = new StringBuilder();

        //There's no need to box the integer.
        int storedItems = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
        float percentage = Math.round((float) storedItems / (float) getCapacity(b) * 100.0F);

        bar.append("&8[");

        if (percentage < 25) {
            bar.append("&2");
        } 
        else if (percentage < 50) {
            bar.append("&a");
        } 
        else if (percentage < 75) {
            bar.append("&e");
        } 
        else {
            bar.append("&c");
        }

        int lines = 20;

        for (int i = (int) percentage; i >= 5; i -= 5) {
            bar.append(":");
            lines--;
        }

        bar.append("&7");

        for (int i = 0; i < lines; i++) {
            bar.append(":");
        }

        bar.append("&8] &7- " + percentage + "%");

        return new CustomItem(new ItemStack(Material.CAULDRON), "&7" + BlockStorage.getLocationInfo(b.getLocation(), "storedItems") + "/" + getCapacity(b), ChatColor.translateAlternateColorCodes('&', bar.toString()));
    }

    private void updateBarrel(Block b) {
        BlockMenu inventory = BlockStorage.getInventory(b);

        if (inventory == null) return;

        for (int slot : getInputSlots()) {
            if (inventory.getItemInSlot(slot) != null) {
                ItemStack input = inventory.getItemInSlot(slot);

                if (isSimilar(input, inventory.getItemInSlot(22))) {
                    if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) {
                        BlockStorage.addBlockInfo(b, "storedItems", "1");
                    }
                    //There's no need to box the integer.
                    int storedAmount = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));

                    if (storedAmount < getCapacity(b)) {
                        if (storedAmount + input.getAmount() > getCapacity(b)) {
                            BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(getCapacity(b)));
                            inventory.replaceExistingItem(slot, InvUtils.decreaseItem(inventory.getItemInSlot(slot), getCapacity(b) - storedAmount), false);
                            inventory.replaceExistingItem(4, getCapacityItem(b), false);
                        } 
                        else {
                            BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(storedAmount + input.getAmount()));
                            inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                            inventory.replaceExistingItem(4, getCapacityItem(b), false);
                        }
                    }
                }
                else if (inventory.getItemInSlot(22).getType() == Material.BARRIER) {
                    ItemStack stack = input.clone();
                    List<String> lore = (stack.hasItemMeta() && stack.getItemMeta().hasLore()) ? stack.getItemMeta().getLore() : new ArrayList<String>();
                    lore.add(LORE_DATA);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                    BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(input.getAmount()));

                    inventory.replaceExistingItem(22, new CustomItem(stack, 1), false);
                    inventory.replaceExistingItem(slot, new ItemStack(Material.AIR), false);
                    inventory.replaceExistingItem(4, getCapacityItem(b), false);
                }
            }
        }

        if (BlockStorage.getLocationInfo(b.getLocation(), "storedItems") == null) return;

        //There's no need to box the integer.
        int stored = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "storedItems"));
        ItemStack output = inventory.getItemInSlot(22).clone();

        if (inventory.getItemInSlot(getOutputSlots()[0]) != null) {
            if (!isSimilar(inventory.getItemInSlot(getOutputSlots()[0]), output)) {
                return;
            }

            int requested = output.getMaxStackSize() - inventory.getItemInSlot(getOutputSlots()[0]).getAmount();

            output.setAmount(Math.min(stored, requested));
        } 
        else {
            output.setAmount(Math.min(stored, output.getMaxStackSize()));
        }

        ItemMeta meta = output.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();

        for (int i = 0; i <= lore.size() - 1; i++) {
            if (lore.get(i).equals(LORE_DATA)) {
                lore.remove(i);
                break;
            }
        }

        meta.setLore(lore);
        output.setItemMeta(meta);

        if (!fits(b, output)) return;

        BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(stored - output.getAmount()));

        // There's no need to create an array in here.
        pushItems(b, output);

        if ((stored - output.getAmount()) <= 0) {
            BlockStorage.addBlockInfo(b, "storedItems", null);
            inventory.replaceExistingItem(4, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
            inventory.replaceExistingItem(22, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
            return;
        }

        inventory.replaceExistingItem(4, getCapacityItem(b), false);
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

    private boolean isSimilar(ItemStack i1, ItemStack i2) {
        if (i1 == null) return false;
        if (i2 == null) return false;

        ItemStack itemStack1 = i1.clone();
        itemStack1.setAmount(1);
        ItemStack itemStack2 = i2.clone();
        itemStack2.setAmount(1);

        if (!itemStack2.hasItemMeta()) return false;
        if (!itemStack2.getItemMeta().hasLore()) return false;

        ItemMeta meta = itemStack2.getItemMeta();

        List<String> lore = meta.getLore();
        for (int i = 0; i <= lore.size() - 1; i++) {
            if (lore.get(i).equals(LORE_DATA)) {
                lore.remove(i);
                meta.setLore(lore);
                itemStack2.setItemMeta(meta);
                break;
            }
        }

        return itemStack1.isSimilar(itemStack2);
    }

    private Inventory inject(Block b) {
        int size = BlockStorage.getInventory(b).toInventory().getSize();
        Inventory inv = Bukkit.createInventory(null, size);
        
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new CustomItem(Material.COMMAND_BLOCK, "&4ALL YOUR PLACEHOLDERS ARE BELONG TO US"));
        }
        
        for (int slot : getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(b).getItemInSlot(slot));
        }
        return inv;
    }

    protected boolean fits(Block b, ItemStack... items) {
        return inject(b).addItem(items).isEmpty();
    }

    protected void pushItems(Block b, ItemStack... items) {
        Inventory inv = inject(b);
        inv.addItem(items);

        for (int slot : getOutputSlots()) {
            BlockStorage.getInventory(b).replaceExistingItem(slot, inv.getItem(slot));
        }
    }
}
