package org.gecko.wauh.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.gecko.wauh.Main;

import java.io.File;
import java.io.IOException;

public class ConfigurationManager {
    private final Main plugin;
    private final File configFile;
    private FileConfiguration config;

    public ConfigurationManager(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "data.yml");

        // Create the data.yml file if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveResource("data.yml", false);
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
            e.printStackTrace();
        }
    }

}
