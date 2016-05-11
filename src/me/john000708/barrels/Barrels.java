package me.john000708.barrels;

import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin {
	
    public static boolean displayItem;
    public static JavaPlugin plugin;

    public void onEnable() {
        plugin = this;

        PluginUtils utils = new PluginUtils(this);
        utils.setupMetrics();
        utils.setupConfig();

        new DisplayListener();

        displayItem = getConfig().getBoolean("options.displayItem");
        //utils.setupUpdater(, getFile());
        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        plugin = null;
    }

    @SuppressWarnings("deprecation")
    private void setup() {
        Category barrelCat = new Category(new CustomItem(new ItemStack(Material.LOG), "&aBarrels", "", "&a> Click to open"));

        barrelCat.register();

        ItemStack SMALL_BARREL = new CustomItem(new ItemStack(Material.LOG), "&9Barrel &7- &eSmall", "", "&8\u21E8 &7Capacity: 64 Stacks");
        ItemStack MEDIUM_BARREL = new CustomItem(new MaterialData(Material.LOG, (byte) 1), "&9Barrel &7- &eMedium", "", "&8\u21E8 &7Capacity: 128 Stacks");
        ItemStack BIG_BARREL = new CustomItem(new MaterialData(Material.LOG_2, (byte) 1), "&9Barrel &7- &eBig", "", "&8\u21E8 &7Capacity: 256 Stacks");
        ItemStack LARGE_BARREL = new CustomItem(new ItemStack(Material.LOG_2), "&9Barrel &7- &eLarge", "", "&8\u21E8 &7Capacity: 512 Stacks");
        ItemStack DSU = new CustomItem(new ItemStack(Material.DIAMOND_BLOCK), "&3Deep Storage Unit", "", "&4End-Game Storage Solution", "", "&8\u21E8 &7Capacity: 1048576 Stacks");

        new Barrel(barrelCat, SMALL_BARREL, "BARREL_SMALL", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.WOOD_STEP), SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), new ItemStack(Material.CHEST), new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), SlimefunItems.GILDED_IRON, new ItemStack(Material.WOOD_STEP)}, 4096) {
            
        	@Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eSmall";
            }
        	
        }.register();

        new Barrel(barrelCat, MEDIUM_BARREL, "BARREL_MEDIUM", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.WOOD_STEP), SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), SMALL_BARREL, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), SlimefunItems.GILDED_IRON, new ItemStack(Material.WOOD_STEP)}, 8192) {
            
        	@Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eMedium";
            }
        	
        }.register();

        new Barrel(barrelCat, BIG_BARREL, "BARREL_BIG", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.WOOD_STEP), SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), MEDIUM_BARREL, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), SlimefunItems.GILDED_IRON, new ItemStack(Material.WOOD_STEP)}, 16384) {
            
        	@Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eBig";
            }
        	
        }.register();

        new Barrel(barrelCat, LARGE_BARREL, "BARREL_LARGE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.WOOD_STEP), SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), BIG_BARREL, new ItemStack(Material.WOOD_STEP), new ItemStack(Material.WOOD_STEP), SlimefunItems.GILDED_IRON, new ItemStack(Material.WOOD_STEP)}, 32768) {
            
        	@Override
            public String getInventoryTitle() {
                return "&9Barrel &7- &eLarge";
            }
        	
        }.register();

        new Barrel(barrelCat, DSU, "BARREL_GIGANTIC", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.PLASTIC_SHEET, BIG_BARREL, SlimefunItems.PLASTIC_SHEET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE}, 1048576) {
            
        	@Override
            public String getInventoryTitle() {
                return "&3Deep Storage Unit";
            }
        	
        }.register();
    }
}
