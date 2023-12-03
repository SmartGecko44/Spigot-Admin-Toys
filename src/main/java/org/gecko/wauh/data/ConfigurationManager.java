package org.gecko.wauh.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.gecko.wauh.Main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationManager {
    private final File configFile;
    private final FileConfiguration config;
    private final Logger logger = Logger.getLogger(Main.class.getName());
    public ConfigurationManager(Main plugin) {
        this.configFile = new File(plugin.getDataFolder(), "data.yml");

        // Create the data.yml file if it doesn't exist
        if (!configFile.exists()) {
            File dir = new File("Wauh");
            boolean dirCreated = dir.mkdirs();
            if (!dirCreated) {
                logger.log(Level.SEVERE, "Config folder could not be created");
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config folder created!");
            }
            plugin.saveResource("Wauh/data.yml", false);
        }

        // Load the config
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to save config", e);
        }
    }

}
