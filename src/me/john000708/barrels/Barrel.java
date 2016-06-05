package me.john000708.barrels;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.ItemManipulationEvent;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 06.05.2016.
 */
public class Barrel extends SlimefunItem {

    private int[] border1 = {0, 1, 2, 9, 11, 18, 19, 20};
    private int[] border2 = {3, 5, 12, 13, 14, 21, 23};
    private int[] border3 = {6, 7, 8, 15, 17, 24, 25, 26};

    private int capacity;

    public Barrel(Category category, ItemStack item, String name, RecipeType recipeType, final ItemStack[] recipe, int capacity) {
        super(category, item, name, recipeType, recipe);

        this.capacity = capacity;

        new BlockMenuPreset(name, getInventoryTitle()) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public void newInstance(final BlockMenu menu, final Block b) {

                registerEvent(new ItemManipulationEvent() {
                	
                    @Override
                    public void onEvent(int i, ItemStack itemStack, ItemStack itemStack1) {
                        updateBarrel(b);
                    }
                    
                });
                
                if (BlockStorage.getBlockInfo(b, "storedItems") == null) {
                    menu.replaceExistingItem(4, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
                    menu.replaceExistingItem(22, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
                }
                
                if (Barrels.displayItem) {
                	DisplayItem.updateDisplayItem(b, getCapacity(b));
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                boolean protect = BlockStorage.getBlockInfo(b, "protected") == null || BlockStorage.getBlockInfo(b, "owner").equals(p.getUniqueId().toString()) || (BlockStorage.getBlockInfo(b, "whitelist") != null && BlockStorage.getBlockInfo(b, "whitelist").contains(p.getUniqueId().toString()));

                return p.hasPermission("slimefun.inventory.bypass") || protect;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }
            
            @Override
            public int[] getSlotsAccessedByItemTransport(BlockMenu menu, ItemTransportFlow flow, ItemStack item) {
            	if (flow == ItemTransportFlow.INSERT) {
            		if (BlockStorage.getBlockInfo(menu.getBlock(), "storedItems") != null) return isSimiliar(item, menu.getItemInSlot(22)) ? getInputSlots(): new int[0];
            		else return getInputSlots();
            	}
                else return getOutputSlots();
            }
        };

        registerBlockHandler(name, new SlimefunBlockHandler() {
            @Override
            public void onPlace(Player player, Block block, SlimefunItem slimefunItem) {
                BlockStorage.addBlockInfo(block, "owner", player.getUniqueId().toString());
                BlockStorage.addBlockInfo(block, "whitelist", " ");
                // DONT DO ANYTHING - Inventory is not yet loaded
            }

            @Override
            public boolean onBreak(Player player, Block block, SlimefunItem slimefunItem, UnregisterReason unregisterReason) {
                if (unregisterReason.equals(UnregisterReason.EXPLODE)) {
                    if (BlockStorage.getBlockInfo(block, "explosion") != null) return false;
                }
                else if (unregisterReason.equals(UnregisterReason.PLAYER_BREAK)) {
                	if (!BlockStorage.getBlockInfo(block, "owner").equals(player.getUniqueId().toString())) return false;
                }

                DisplayItem.removeDisplayItem(block);

                BlockMenu inv = BlockStorage.getInventory(block);

                if(BlockStorage.getBlockInfo(block, "explosion") != null) block.getWorld().dropItem(block.getLocation(), SlimefunItem.getByName("EXPLOSION_MODULE").getItem());
                if(BlockStorage.getBlockInfo(block, "STRUCT_1") != null) block.getWorld().dropItem(block.getLocation(), SlimefunItem.getByName("STRUCT_UPGRADE_1").getItem());
                if(BlockStorage.getBlockInfo(block, "STRUCT_2") != null) block.getWorld().dropItem(block.getLocation(), SlimefunItem.getByName("STRUCT_UPGRADE_2").getItem());
                if(BlockStorage.getBlockInfo(block, "STRUCT_3") != null) block.getWorld().dropItem(block.getLocation(), SlimefunItem.getByName("STRUCT_UPGRADE_3").getItem());
                if(BlockStorage.getBlockInfo(block, "protected") != null) block.getWorld().dropItem(block.getLocation(), SlimefunItem.getByName("BIO_PROTECTION").getItem());

                if (BlockStorage.getBlockInfo(block, "storedItems") == null) return true;
                int storedAmount = Integer.valueOf(BlockStorage.getBlockInfo(block, "storedItems"));

                ItemStack item = inv.getItemInSlot(22);
                ItemMeta meta = item.getItemMeta();

                List<String> lore = meta.getLore();
                for (int i = 0; i <= lore.size() - 1; i++) {
                    if (lore.get(i).equals("§b§a§r§r§e§l")) {
                        lore.remove(i);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        break;
                    }
                }

                while (storedAmount > 0) {
                    int amount = item.getMaxStackSize();
                    
                    if (storedAmount > amount) {
                        storedAmount -= amount;
                    } 
                    else {
                        amount = storedAmount;
                        storedAmount = 0;
                    }
                    
                    block.getWorld().dropItem(block.getLocation(), new CustomItem(item, amount));
                }

                if (inv.getItemInSlot(getInputSlots()[0]) != null)
                    block.getWorld().dropItem(block.getLocation(), inv.getItemInSlot(getInputSlots()[0]));
                
                if (inv.getItemInSlot(getOutputSlots()[0]) != null)
                    block.getWorld().dropItem(block.getLocation(), inv.getItemInSlot(getOutputSlots()[0]));

                return true;
            }
        });
    }

    @Override
    public void register(boolean slimefun) {
        addItemHandler(new BlockTicker() {
        	
            @Override
            public boolean isSynchronized() {
                return true;
            }

            @Override
            public void uniqueTick() {

            }

            @Override
            public void tick(Block block, SlimefunItem slimefunItem, Config config) {
                updateBarrel(block);

                if (Barrels.displayItem) {
                    DisplayItem.updateDisplayItem(block, getCapacity(block));
                }
            }
        });

        super.register(false);
    }

    public String getInventoryTitle() {
        return "&6Barrel";
    }

    public int getCapacity(Block b) {
        if (BlockStorage.getBlockInfo(b, "capacity") == null) {
            BlockStorage.addBlockInfo(b, "capacity", String.valueOf(this.capacity));
        }

        return Integer.valueOf(BlockStorage.getBlockInfo(b, "capacity"));
    }

    public int[] getInputSlots() {
        return new int[]{10};
    }

    public int[] getOutputSlots() {
        return new int[]{16};
    }

    private ItemStack getCapacityItem(Block b) {
        StringBuilder bar = new StringBuilder();

        int storedItems = Integer.valueOf(BlockStorage.getBlockInfo(b, "storedItems"));

        float percentage = Math.round((float) storedItems / (float) getCapacity(b) * 100.0F);

        bar.append("&8[");

        if (percentage < 25) {
            bar.append("&2");
        } else if (percentage < 50) {
            bar.append("&a");
        } else if (percentage < 75) {
            bar.append("&e");
        } else {
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

        return new CustomItem(new ItemStack(Material.CAULDRON_ITEM), "&7" + BlockStorage.getBlockInfo(b, "storedItems") + "/" + getCapacity(b), ChatColor.translateAlternateColorCodes('&', bar.toString()));
    }

    private void updateBarrel(Block b) {
        BlockMenu inventory = BlockStorage.getInventory(b);

        if (inventory == null) return;

        for (int slot : getInputSlots()) {
            if (inventory.getItemInSlot(slot) != null) {
                ItemStack input = inventory.getItemInSlot(slot);

                if (isSimiliar(input, inventory.getItemInSlot(22))) {
                    if (BlockStorage.getBlockInfo(b, "storedItems") == null) {
                        BlockStorage.addBlockInfo(b, "storedItems", "1");
                    }
                    int storedAmount = Integer.valueOf(BlockStorage.getBlockInfo(b, "storedItems"));

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
                    lore.add("§b§a§r§r§e§l");
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

        if (BlockStorage.getBlockInfo(b, "storedItems") == null) return;

        int stored = Integer.valueOf(BlockStorage.getBlockInfo(b, "storedItems"));
        ItemStack output = inventory.getItemInSlot(22).clone();

        if (inventory.getItemInSlot(getOutputSlots()[0]) != null) {
            if (!isSimiliar(inventory.getItemInSlot(getOutputSlots()[0]), output)) {
            	return;
            }

            int requested = output.getMaxStackSize() - inventory.getItemInSlot(getOutputSlots()[0]).getAmount();

            if (stored >= requested) {
            	output.setAmount(requested);
            } 
            else {
            	output.setAmount(stored);
            }
        } 
        else {
            if (stored > output.getMaxStackSize()) {
            	output.setAmount(output.getMaxStackSize());
            } 
            else {
                output.setAmount(stored);
            }
        }

        ItemMeta meta = output.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();

        for (int i = 0; i <= lore.size() - 1; i++) {
            if (lore.get(i).equals("§b§a§r§r§e§l")) {
                lore.remove(i);
                break;
            }
        }
        
        meta.setLore(lore);
        output.setItemMeta(meta);

        if (!fits(b, new ItemStack[]{output})) return;

        BlockStorage.addBlockInfo(b, "storedItems", String.valueOf(stored - output.getAmount()));

        pushItems(b, new ItemStack[] {output});

        if ((stored - output.getAmount()) <= 0) {
            BlockStorage.addBlockInfo(b, "storedItems", null);
            inventory.replaceExistingItem(4, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
            inventory.replaceExistingItem(22, new CustomItem(new ItemStack(Material.BARRIER), "&7Empty"), false);
            return;
        }

        inventory.replaceExistingItem(4, getCapacityItem(b), false);
    }

    @SuppressWarnings("deprecation")
    private void constructMenu(final BlockMenuPreset preset) {
        for (int i : border1) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "), new ChestMenu.MenuClickHandler() {
                @Override
                public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }
            });
        }

        for (int i : border2) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), " "), new ChestMenu.MenuClickHandler() {
                @Override
                public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }
            });
        }

        for (int i : border3) {
            preset.addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), " "), new ChestMenu.MenuClickHandler() {
                @Override
                public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                    return false;
                }
            });
        }

        preset.addMenuClickHandler(4, new ChestMenu.MenuClickHandler() {
            @Override
            public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                return false;
            }
        });

        preset.addMenuClickHandler(22, new ChestMenu.MenuClickHandler() {
            @Override
            public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
                return false;
            }
        });
    }

    private boolean isSimiliar(ItemStack i1, ItemStack i2) {
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
            if (lore.get(i).equals("§b§a§r§r§e§l")) {
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
            inv.setItem(i, new CustomItem(Material.COMMAND, " §4ALL YOUR PLACEHOLDERS ARE BELONG TO US", 0));
        }
        for (int slot : getOutputSlots()) {
            inv.setItem(slot, BlockStorage.getInventory(b).getItemInSlot(slot));
        }
        return inv;
    }

    protected boolean fits(Block b, ItemStack[] items) {
        return inject(b).addItem(items).isEmpty();
    }

    protected void pushItems(Block b, ItemStack[] items) {
        Inventory inv = inject(b);
        inv.addItem(items);

        for (int slot : getOutputSlots()) {
            BlockStorage.getInventory(b).replaceExistingItem(slot, inv.getItem(slot));
        }
    }
}
