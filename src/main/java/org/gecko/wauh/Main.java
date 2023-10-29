package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.Listeners.BarrierListener;
import org.gecko.wauh.Listeners.BedrockListener;
import org.gecko.wauh.Listeners.BucketListener;
import org.gecko.wauh.commands.SetRadiusLimitCommand;
import org.gecko.wauh.commands.StopWauh;

public final class Main extends JavaPlugin {
    private int radiusLimit = 20;

    public int getRadiusLimit() {
        return radiusLimit + 2;
    }

    public void setRadiusLimit(int newLimit) {
        radiusLimit = newLimit;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Yay");

        // Create instances of the listeners
        BucketListener bucketListener = new BucketListener();
        BarrierListener barrierListener = new BarrierListener();
        BedrockListener bedrockListener = new BedrockListener();
        // RemovalInfo removalInfo = new RemovalInfo(bucketListener, barrierListener);

        // Register the listeners
        getServer().getPluginManager().registerEvents(bucketListener, this);
        getServer().getPluginManager().registerEvents(barrierListener, this);
        getServer().getPluginManager().registerEvents(bedrockListener, this);
        // removalInfo.ShowRemovalInfo();

        // Register the StopWauh command with the listeners as arguments
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "kys");
    }
}