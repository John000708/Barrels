package me.john000708.barrels;

import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.cscorelib2.updater.BukkitUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.GitHubBuildsUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.Updater;
import me.john000708.barrels.listeners.DisplayListener;
import me.john000708.barrels.listeners.WorldListener;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin {

    public static boolean displayItem;
    public static JavaPlugin plugin;
    public static Config config;

    boolean plastic;

    public void onEnable() {
        plugin = this;

        PluginUtils utils = new PluginUtils(this);
        utils.setupConfig();
        config = utils.getConfig();
        
        // Setting up bStats
        new Metrics(this);

		// Setting up the Auto-Updater
		Updater updater;

		if (!getDescription().getVersion().startsWith("DEV - ")) {
			// We are using an official build, use the BukkitDev Updater
			updater = new BukkitUpdater(this, getFile(), 99947);
		}
		else {
			// If we are using a development build, we want to switch to our custom 
			updater = new GitHubBuildsUpdater(this, getFile(), "John000708/Barrels/master");
		}

		if (config.getBoolean("options.auto-update")) updater.start();

        new DisplayListener();
        new WorldListener();

        displayItem = config.getBoolean("options.displayItem");
        plastic = config.getBoolean("options.plastic-recipe");
        
        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        plugin = null;
    }
    
    private void setup() {
        Category barrelCat = new Category(new CustomItem(new ItemStack(Material.OAK_LOG), "&aBarrels", "", "&a> Click to open"), 2);

        ItemStack SMALL_BARREL = new CustomItem(new ItemStack(Material.OAK_LOG), "&9Barrel &7- &eSmall", "", "&8\u21E8 &7Capacity: 64 Stacks");
        ItemStack MEDIUM_BARREL = new CustomItem(Material.SPRUCE_LOG, "&9Barrel &7- &eMedium", "", "&8\u21E8 &7Capacity: 128 Stacks");
        ItemStack BIG_BARREL = new CustomItem(Material.DARK_OAK_LOG, "&9Barrel &7- &eBig", "", "&8\u21E8 &7Capacity: 256 Stacks");
        ItemStack LARGE_BARREL = new CustomItem(new ItemStack(Material.ACACIA_LOG), "&9Barrel &7- &eLarge", "", "&8\u21E8 &7Capacity: 512 Stacks");
        ItemStack DSU = new CustomItem(new ItemStack(Material.DIAMOND_BLOCK), "&3Deep Storage Unit", "", "&4End-Game Storage Solution", "", "&8\u21E8 &7Capacity: 1048576 Stacks");

        //Upgrades
        final ItemStack EXPLOSION_MODULE = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Explosion Protection", "", "&fPrevents the barrel from", "&fgetting destroyed.");
        final ItemStack BIOMETRIC_PROTECTION = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Biometric Protection", "", "&fPrevents other people", "&ffrom accessing your barrel.");
        final ItemStack ID_CARD = new CustomItem(new ItemStack(Material.PAPER), "&fID Card", "", "&fRight click to bind.");
        final ItemStack STRUCT_UPGRADE_1 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Structural Upgrade &7- &eI", "&bSmall &8\u21E8 &bMedium");
        final ItemStack STRUCT_UPGRADE_2 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Structural Upgrade &7- &eII", "&bMedium &8\u21E8 &bBig");
        final ItemStack STRUCT_UPGRADE_3 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Structural Upgrade &7- &eIII", "&bBig &8\u21E8 &bLarge");

        new Barrel(barrelCat, SMALL_BARREL, "BARREL_SMALL", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CHEST), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 4096) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eSmall";
            }

        }.register();

        new Barrel(barrelCat, MEDIUM_BARREL, "BARREL_MEDIUM", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SMALL_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 8192) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eMedium";
            }

        }.register();

        new Barrel(barrelCat, BIG_BARREL, "BARREL_BIG", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), MEDIUM_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 16384) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eBig";
            }

        }.register();

        new Barrel(barrelCat, LARGE_BARREL, "BARREL_LARGE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), BIG_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 32768) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eLarge";
            }

        }.register();

        new Barrel(barrelCat, DSU, "BARREL_GIGANTIC", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.PLASTIC_SHEET, LARGE_BARREL, SlimefunItems.PLASTIC_SHEET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE}, 1048576) {

            @Override
            public String getInventoryTitle() {
                return "&3Deep Storage Unit";
            }

        }.register();

        new SlimefunItem(barrelCat, EXPLOSION_MODULE, "EXPLOSION_MODULE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT)}).register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, EXPLOSION_MODULE, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_")) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();
                    if (BlockStorage.getLocationInfo(clickedBlock.getLocation(), "explosion") == null) {
                        BlockStorage.addBlockInfo(clickedBlock, "explosion", "true");
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                        player.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                    }
                }
                return false;
            }

        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_1, "STRUCT_UPGRADE_1", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, MEDIUM_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT}).register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_1, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_1") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_1", "true");
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.valueOf(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 8192));
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    player.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                }
                return false;
            }

        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_2, "STRUCT_UPGRADE_2", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, BIG_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT}).register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_2, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_2") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_2", "true");
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.valueOf(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 16384));
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    player.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                }
                return false;
            }

        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_3, "STRUCT_UPGRADE_3", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, LARGE_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT}).register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_3, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_3") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_3", "true");
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.valueOf(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 32768));
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    player.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                }
                return false;
            }

        });

        new SlimefunItem(barrelCat, BIOMETRIC_PROTECTION, "BIO_PROTECTION", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.PAPER), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE)}).register(false, new ItemInteractionHandler() {
            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, BIOMETRIC_PROTECTION, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "BIO_PROT") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "protected", "true");
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    player.sendMessage(ChatColor.GREEN + "Module successfully applied!");
                }
                return false;
            }
        });

        new SlimefunItem(barrelCat, ID_CARD, "BARREL_ID_CARD", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.PAPER), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE)}).register(false, new ItemInteractionHandler() {
            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, ID_CARD, false)) return false;
                Block clickedBlock = itemUseEvent.getClickedBlock();
                ItemStack idCard = itemStack;
                ItemMeta meta = idCard.getItemMeta();
                List<String> lore = idCard.getItemMeta().getLore();

                if (lore.get(0).equals("")) {
                    lore.set(0, ChatColor.translateAlternateColorCodes('&', "&0" + player.getUniqueId().toString()));
                    lore.set(1, ChatColor.translateAlternateColorCodes('&', "&fBound to: " + player.getName()));
                    meta.setLore(lore);
                    idCard.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "ID Card bound.");
                } else if (clickedBlock != null && BlockStorage.hasBlockInfo(clickedBlock) && BlockStorage.checkID(clickedBlock).startsWith("BARREL_") && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist") != null && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "owner").equals(player.getUniqueId().toString())) {
                    String whitelistedPlayers = BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist");
                    if (!whitelistedPlayers.contains(ChatColor.stripColor(lore.get(0)))) {
                        BlockStorage.addBlockInfo(clickedBlock, "whitelist", whitelistedPlayers + ChatColor.stripColor(lore.get(0)) + ";");
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                        player.sendMessage(ChatColor.GREEN + "Player successfully whitelisted!");
                    } 
                    else {
                        player.sendMessage(ChatColor.RED + "The player is already whitelisted.");
                    }

                }
                return false;
            }
        });
    }
}
