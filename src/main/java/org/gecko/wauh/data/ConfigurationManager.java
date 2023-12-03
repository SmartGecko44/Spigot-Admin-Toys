package org.gecko.wauh.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.gecko.wauh.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationManager {
    private final File configFile;
    private FileConfiguration config;
    private final Logger logger = Logger.getLogger(Main.class.getName());
    public ConfigurationManager(Main plugin) {
        File dir = new File("plugins/Wauh");

        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();

            if (!dirCreated) {
                plugin.getLogger().log(Level.SEVERE, "Config folder could not be created");
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config folder created!");
            }
        }

        this.configFile = new File(dir, "data.yml");

        try {
            // Create the data.yml file if it doesn't exist
            if (!configFile.exists()) {
                boolean fileCreated = configFile.createNewFile();
                if (!fileCreated) {
                    logger.log(Level.SEVERE, "Config file could not be created");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file created!");
                    FileWriter writer = new FileWriter(configFile);
                    writer.write("playerRadiusLimit: 20 \n");
                    writer.write("tntRadiusLimit: 5\n");
                    writer.write("creeperRadiusLimit: 0\n");
                    writer.close();
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load config file", ex);
        }
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
