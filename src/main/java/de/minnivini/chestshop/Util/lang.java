package de.minnivini.chestshop.Util;

import de.minnivini.chestshop.ChestShop;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class lang {
    public static String getMessage(String message){
        File languageFolder = new File(ChestShop.getInstance().getDataFolder() + "/locales");
        String language = ChestShop.getInstance().getLanguage();
        File langFile = new File(languageFolder, language + ".yml");
        if (!langFile.exists()) {
            return null;
        }
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        return langConfig.getString(message);
    }
    public static void createLanguageFolder() {
        File langFolder = new File(ChestShop.getInstance().getDataFolder() + "/locales");
        if (!langFolder.exists()) {
            langFolder.mkdir();
        }
        File enFile = new File(langFolder, "en.yml");
        try {
            if (!enFile.exists()) {
                InputStream in = ChestShop.getInstance().getResource("en.yml");
                Files.copy(in, enFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


