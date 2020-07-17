package me.john000708.barrels;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.john000708.barrels.block.Barrel;
import me.john000708.barrels.items.BarrelModule;
import me.john000708.barrels.items.IDCard;
import me.john000708.barrels.listeners.DisplayListener;
import me.john000708.barrels.listeners.WorldListener;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin implements SlimefunAddon {

    private static Barrels instance;

    private boolean requirePlastic;
    private boolean displayItem;
    private String itemFormat;

    @Override
    public void onEnable() {
        instance = this;
        Config config = new Config(this);

        // Setting up the Auto-Updater
        Updater updater = new GitHubBuildsUpdater(this, getFile(), "John000708/Barrels/master");

        if (config.getBoolean("options.auto-update")) {
            updater.start();
        }

        new DisplayListener(this);
        new WorldListener(this);

        displayItem = config.getBoolean("options.displayItem");
        requirePlastic = config.getBoolean("options.plastic-recipe");
        itemFormat = config.getString("options.item-format");

        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void setup() {
        Category barrelCat = new Category(new NamespacedKey(this, "barrels"), new CustomItem(Material.OAK_LOG, "&aBarrels", "", "&a> Click to open"), 2);

        SlimefunItemStack smallBarrel = new SlimefunItemStack("BARREL_SMALL", Material.OAK_LOG, "&9Barrel &7- &eSmall", "", "&8\u21E8 &7Capacity: 64 Stacks");
        SlimefunItemStack mediumBarrel = new SlimefunItemStack("BARREL_MEDIUM", Material.SPRUCE_LOG, "&9Barrel &7- &eMedium", "", "&8\u21E8 &7Capacity: 128 Stacks");
        SlimefunItemStack bigBarrel = new SlimefunItemStack("BARREL_BIG", Material.DARK_OAK_LOG, "&9Barrel &7- &eBig", "", "&8\u21E8 &7Capacity: 256 Stacks");
        SlimefunItemStack largeBarrel = new SlimefunItemStack("BARREL_LARGE", Material.ACACIA_LOG, "&9Barrel &7- &eLarge", "", "&8\u21E8 &7Capacity: 512 Stacks");
        SlimefunItemStack deepStorageUnit = new SlimefunItemStack("BARREL_GIGANTIC", Material.DIAMOND_BLOCK, "&3Deep Storage Unit", "", "&4End-Game Storage Solution", "", "&8\u21E8 &7Capacity: 1048576 Stacks");

        // Upgrades
        SlimefunItemStack explosionModule = new SlimefunItemStack("BARREL_EXPLOSION_MODULE", Material.ITEM_FRAME, "&9Explosion Protection", "", "&fPrevents the barrel from", "&fgetting destroyed.");
        SlimefunItemStack biometricProtectionModule = new SlimefunItemStack("BARREL_BIO_PROTECTION", Material.ITEM_FRAME, "&9Biometric Protection", "", "&fPrevents other people", "&ffrom accessing your barrel.");
        SlimefunItemStack idCard = new SlimefunItemStack("BARREL_ID_CARD", Material.PAPER, "&fID Card", "", "&fRight click to bind.");
        SlimefunItemStack structUpgrade1 = new SlimefunItemStack("STRUCT_UPGRADE_1", Material.ITEM_FRAME, "&9Structural Upgrade &7- &eI", "&bSmall &8\u21E8 &bMedium");
        SlimefunItemStack structUpgrade2 = new SlimefunItemStack("STRUCT_UPGRADE_2", Material.ITEM_FRAME, "&9Structural Upgrade &7- &eII", "&bMedium &8\u21E8 &bBig");
        SlimefunItemStack structUpgrade3 = new SlimefunItemStack("STRUCT_UPGRADE_3", Material.ITEM_FRAME, "&9Structural Upgrade &7- &eIII", "&bBig &8\u21E8 &bLarge");

        new Barrel(barrelCat, smallBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CHEST), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB) }, 4096) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eSmall";
            }

        }.register(this);

        new Barrel(barrelCat, mediumBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), smallBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB) }, 8192) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eMedium";
            }

        }.register(this);

        new Barrel(barrelCat, bigBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), mediumBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB) }, 16384) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eBig";
            }

        }.register(this);

        new Barrel(barrelCat, largeBarrel, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.OAK_SLAB), requirePlastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), bigBarrel, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB) }, 32768) {

            @Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eLarge";
            }

        }.register(this);

        new Barrel(barrelCat, deepStorageUnit, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.PLASTIC_SHEET, largeBarrel, SlimefunItems.PLASTIC_SHEET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE }, 1048576) {

            @Override
            public String getInventoryTitle() {
                return "&3Deep Storage Unit";
            }

        }.register(this);

        new BarrelModule(barrelCat, explosionModule, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT) }) {

            @Override
            public boolean applyUpgrade(Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "explosion") != null) {
                    return false;
                }

                BlockStorage.addBlockInfo(b, "explosion", "true");
                return true;
            }

        }.register(this);

        new BarrelModule(barrelCat, structUpgrade1, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, mediumBarrel, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT }) {

            @Override
            public boolean applyUpgrade(Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_1") != null) {
                    return false;
                }

                int capacity = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
                BlockStorage.addBlockInfo(b, "STRUCT_1", "true");
                BlockStorage.addBlockInfo(b, "capacity", String.valueOf(capacity + 8192));
                return true;
            }

        }.register(this);

        new BarrelModule(barrelCat, structUpgrade2, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, bigBarrel, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT }) {

            @Override
            public boolean applyUpgrade(Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_2") != null) {
                    return false;
                }

                int capacity = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
                BlockStorage.addBlockInfo(b, "STRUCT_2", "true");
                BlockStorage.addBlockInfo(b, "capacity", String.valueOf(capacity + 16384));
                return true;
            }

        }.register(this);

        new BarrelModule(barrelCat, structUpgrade3, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, largeBarrel, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT }) {

            @Override
            public boolean applyUpgrade(Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "STRUCT_3") != null) {
                    return false;
                }

                int capacity = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "capacity"));
                BlockStorage.addBlockInfo(b, "STRUCT_3", "true");
                BlockStorage.addBlockInfo(b, "capacity", String.valueOf(capacity + 32768));
                return true;
            }

        }.register(this);

        new BarrelModule(barrelCat, biometricProtectionModule, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.PAPER), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE) }) {

            @Override
            public boolean applyUpgrade(Block b) {
                if (BlockStorage.getLocationInfo(b.getLocation(), "protected") != null) {
                    return false;
                }

                BlockStorage.addBlockInfo(b, "protected", "true");
                return true;
            }

        }.register(this);

        new IDCard(barrelCat, idCard, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.PAPER), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE) }).register(this);
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/John000708/Barrels/issues";
    }

    public static Barrels getInstance() {
        return instance;
    }

    public static String getItemFormat() {
        return instance.itemFormat;
    }

    public static boolean displayItem() {
        return instance.displayItem;
    }
}
