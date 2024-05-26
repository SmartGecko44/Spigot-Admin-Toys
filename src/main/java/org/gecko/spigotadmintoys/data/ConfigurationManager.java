package org.gecko.spigotadmintoys.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.gecko.spigotadmintoys.gui.ConfigGUI.*;

public class ConfigurationManager {
    public static final String ENABLEDCONF = ": 1\n";
    public static final String COULD_NOT_BE_CREATED = "Config file could not be created";
    public static final String CREATED = "Config file created!";
    private final File configFile;
    private final Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
    private FileConfiguration config;
    private final SetAndGet setAndGet;

    public ConfigurationManager(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
        File dir = new File("plugins/Spigot-Admin-Toys");

        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();

            if (!dirCreated) {
                JavaPlugin.getPlugin(Main.class).getLogger().log(Level.SEVERE, "Config folder could not be created");
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
                    logger.log(Level.SEVERE, COULD_NOT_BE_CREATED);
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + CREATED);
                    FileWriter writer = getFileWriter();
                    writer.close();
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException ex) {
            JavaPlugin.getPlugin(Main.class).getLogger().log(Level.SEVERE, "Could not load config file", ex);
        }
    }

    public FileWriter getFileWriter() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("playerRadiusLimit: 20\n");
            writer.write("tntRadiusLimit: 5\n");
            writer.write("creeperRadiusLimit: 5\n");
            writer.write(BUCKET_ENABLED + ENABLEDCONF);
            writer.write(BARRIER_ENABLED + ENABLEDCONF);
            writer.write(BEDROCK_ENABLED + ENABLEDCONF);
            writer.write(SPHERE_ENABLED + ENABLEDCONF);
            writer.write(TSUNAMI_ENABLED + ENABLEDCONF);
            writer.write(CREEPER_ENABLED + ": 0\n");
            writer.write(TNT_ENABLED + ENABLEDCONF);
            writer.write("Removal visible: true\n");
            return writer;
        } catch (IOException e) {
            throw new IOException("Unable to create FileWriter", e);
        }
    }

    public FileConfiguration getConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to save config", e);
        }
    }

    public void resetConfig(Player player) {
        try {
            // Create the data.yml file if it doesn't exist
            if (!configFile.exists()) {
                boolean fileCreated = configFile.createNewFile();
                if (!fileCreated) {
                    logger.log(Level.SEVERE, COULD_NOT_BE_CREATED);
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + CREATED);
                    FileWriter writer = setAndGet.getConfigManager().getFileWriter();
                    writer.close();
                }
            } else {
                if (!isFileDeleted()) {
                    logger.log(Level.SEVERE, "Config file could not be deleted");
                    player.sendMessage(ChatColor.RED + "Config file could not be reset");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file deleted!");
                    boolean fileCreated = configFile.createNewFile();
                    if (!fileCreated) {
                        logger.log(Level.SEVERE, COULD_NOT_BE_CREATED);
                        player.sendMessage(ChatColor.RED + "Config file could not be reset");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + CREATED);
                        FileWriter writer = setAndGet.getConfigManager().getFileWriter();
                        writer.close();
                        player.sendMessage(ChatColor.GREEN + "Config reset!");
                    }
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not reset config file", ex);
        }
    }

    private boolean isFileDeleted() {
        try {
            Files.delete(configFile.toPath());
            // Deletion successful
            return true;
        } catch (NoSuchFileException e) {
            // File does not exist, consider it deleted
            return true;
        } catch (IOException e) {
            // Directory is not empty, consider it not deleted or Unable to delete file for various causes, consider it not deleted
            return false;
        }
    }

}
