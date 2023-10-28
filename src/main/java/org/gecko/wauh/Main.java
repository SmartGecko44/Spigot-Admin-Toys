package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.barriuh.BarrierListener;
import org.gecko.wauh.wauhbuck.BucketListener;

public final class Main extends JavaPlugin {
    int radiusLimit = 500;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Yay");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "TEST");
        getServer().getPluginManager().registerEvents(new BucketListener(), this);
        getServer().getPluginManager().registerEvents(new BarrierListener(), this);
        try {
            getCommand("stopwauh").setExecutor(new BucketListener());
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "I am in eternal suffering.");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "kys");
    }

    public int getRadiusLimit() {
        return radiusLimit + 2;
    }
}
