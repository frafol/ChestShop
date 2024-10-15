package de.minnivini.chestshop;

import de.minnivini.chestshop.Util.lang;
import de.minnivini.chestshop.commands.ShopInfo;
import de.minnivini.chestshop.commands.csCommand;
import de.minnivini.chestshop.commands.tabCompleter;
import de.minnivini.chestshop.listeners.BlockBreak;
import de.minnivini.chestshop.listeners.InvListener;
import de.minnivini.chestshop.listeners.SignListener;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ChestShop extends JavaPlugin {

    @Getter
    private static ChestShop instance;

    private static Economy econ = null;
    private FileConfiguration shopConfig;
    private File shopConfigFile;
    private FileConfiguration ItemConfig;
    private File ItemConfigFile;
    public FileConfiguration defaultConfig;


    @Override
    public void onEnable() {
        instance = this;
        setupConfig();
        lang.createLanguageFolder();
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new InvListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);

        setupShopConfig();
        setupItemConfig();

        if (!setupEconomy()) {
            System.out.println(lang.getMessage("noVault"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("chestshop").setTabCompleter(new tabCompleter());
        getCommand("chestshop").setExecutor(new csCommand());
        getCommand("shopinfo").setExecutor(new ShopInfo());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        saveShopConfig();
        saveItemConfig();
        instance = null;
    }

    private void setupConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
        defaultConfig = this.getConfig();
    }

    public String getLanguage() {
        if (defaultConfig.contains("language")) {
            return defaultConfig.getString("language");
        }
        return "en";
    }

    public List<String> getBlackWorlds() {
        if (defaultConfig.contains("World_Blacklist")) {
            return defaultConfig.getStringList("World_Blacklist");
        }
        return Collections.singletonList("example%example");
    }

    //------------------------------------------Shop Config-------------------------------------------------------------
    private void setupShopConfig() {
        if (shopConfigFile == null) {
            shopConfigFile = new File(getDataFolder(), "shops.yml");
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopConfigFile);

        if (!shopConfigFile.exists()) {
            saveDefaultShopConfig();
        }
    }

    private void saveShopConfig() {
        try {
            shopConfig.save(shopConfigFile);
        } catch (IOException e) {
            getLogger().severe(lang.getMessage("saveErrShop") + e.getMessage());
        }
    }

    private void saveDefaultShopConfig() {
        saveResource("shops.yml", false);
    }

    //-------------------------------------------Item Config---------------------------------------------------------
    private void setupItemConfig() {
        if (ItemConfigFile == null) {
            ItemConfigFile = new File(getDataFolder(), "items.yml");
        }
        ItemConfig = YamlConfiguration.loadConfiguration(ItemConfigFile);

        if (!ItemConfigFile.exists()) {
            saveDefaultItemConfig();
        }
    }

    private void saveItemConfig() {
        try {
            ItemConfig.save(ItemConfigFile);
        } catch (IOException e) {
            getLogger().severe(lang.getMessage("saveErrItem") + e.getMessage());
        }
    }

    private void saveDefaultItemConfig() {
        saveResource("items.yml", false);
    }

    //-------------------------------------------------Shop Methoden
    public void addItemToShopConfig(String world, int xCoord, int yCoord, int zCoord, String item, Player p) {
        String key = world + "§" + xCoord + "§" + yCoord + "§" + zCoord;
        String key1 = "uuid" + xCoord + "_" + yCoord + "_" + zCoord;
        if (getBlackWorlds() == null) {
            shopConfig.set("shops." + key, item);
            saveShopConfig();
        } else {
            if (getBlackWorlds().contains(world)) {
                p.sendMessage(lang.getMessage("FalseWorld"));
            } else {
                shopConfig.set("shops." + key, item);
                saveShopConfig();
            }
        }
    }

    public String getItemFromShopConfig(String world, int xCoord, int yCoord, int zCoord) {
        String key = world + "§" + xCoord + "§" + yCoord + "§" + zCoord;
        return shopConfig.getString("shops." + key);
    }

    public void removeItemFromShopConfig(String world, int xCoord, int yCoord, int zCoord) {
        String key = world + "§" + xCoord + "§" + yCoord + "§" + zCoord;
        if (shopConfig.contains("shops." + key)) {
            shopConfig.set("shops." + key, null);
            saveShopConfig();
        } else {
            getLogger().warning(lang.getMessage("noShoptoRem") + key);
        }
    }

    public List<String> searchItemFromShopCongig(String gesuchtesItem) {
        ConfigurationSection shops = shopConfig.getConfigurationSection("shops");
        List<String> gefundenenKoordinaten = new ArrayList<>();
        List<String> realKoordinaten = new ArrayList<>();

        if (shops != null) {
            for (String koordinaten : shops.getKeys(false)) {
                String item = shops.getString(koordinaten);
                if (item != null && item.equalsIgnoreCase(gesuchtesItem)) {
                    gefundenenKoordinaten.add(koordinaten);
                }
            }
        }
        if (!gefundenenKoordinaten.isEmpty()) {
            if (gefundenenKoordinaten.size() > 7) {
                Random random = new Random();
                for (int i = 0; i < 7; i++) {
                    int zufallsIndex = random.nextInt(gefundenenKoordinaten.size());
                    realKoordinaten.add(gefundenenKoordinaten.get(zufallsIndex));
                }
            } else {
                realKoordinaten = gefundenenKoordinaten;
            }
        } else {
            return null;
        }
        return realKoordinaten;
    }

    public String formatiereKoordinaten(String roheKoordinaten) {
        // Ersetze den Unterstrich durch Leerzeichen
        roheKoordinaten = roheKoordinaten.replace("§", " ");

        // Teile die Koordinaten anhand von Leerzeichen
        String[] teile = roheKoordinaten.split(" ");

        // Baue die formatierten Koordinaten
        return teile[0] + " " + teile[1] + " " + teile[2] + " " + teile[3];
    }

    //--------------------------------------------------------item Methoden-----------------------------------------------
    public void addcurrentnumber(ItemStack item) {
        int currentID = ItemConfig.getInt("current_id", 0);
        currentID++;
        String itemID = item.getType().toString() + "#" + currentID;
        saveItemToConfig(itemID, item);

        // Die neue ID in die Config schreiben
        ItemConfig.set("current_id", currentID);
        saveItemConfig();
    }

    public void saveItemToConfig(String itemID, ItemStack itemStack) {
        ConfigurationSection itemSection = ItemConfig.createSection(itemID);
        itemSection.set("ItemStack", itemStack);
        saveItemConfig();
    }

    public int curentID() {
        return ItemConfig.getInt("current_id", 0);
    }

    public String IDCheck(String key) {
        return ItemConfig.getString(key);
    }
    public String searchForItemStack(ItemStack vergleich) {
        for (String key : ItemConfig.getKeys(false)) {
            ItemStack configItem = ItemConfig.getItemStack(key + ".ItemStack");
            if (vergleich.isSimilar(configItem)) {
                return key;
            }
        }
        return null;
    }

    public ItemStack getNBT(String id) {
        ConfigurationSection itemSection = ItemConfig.getConfigurationSection(id);
        ItemStack item1 = itemSection.getItemStack("ItemStack");
        return item1;
    }
}

