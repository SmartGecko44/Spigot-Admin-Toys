package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.barriuh.BarrierListener;
import org.gecko.wauh.wauhbuck.BucketListener;
import org.gecko.wauh.commands.SetRadiusLimitCommand;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Yay");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "TEST");
        getServer().getPluginManager().registerEvents(new BucketListener(), this);
        getServer().getPluginManager().registerEvents(new BarrierListener(), this);
        try {
            this.getCommand("stopwauh").setExecutor(new BucketListener());
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "I am in eternal suffering.");
        }
        this.getCommand("kit").setExecutor(new SetRadiusLimitCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "kys");
    }
    int radiusLimit = 20;

    public int getRadiusLimit() {
        return radiusLimit + 2;
    }
}