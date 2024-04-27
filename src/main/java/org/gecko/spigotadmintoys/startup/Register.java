package org.gecko.spigotadmintoys.startup;

import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.commands.*;
import org.gecko.spigotadmintoys.gui.ConfigGUI;
import org.gecko.spigotadmintoys.items.weapons.Shortbow;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import static org.bukkit.Bukkit.getServer;

public class Register {
    public void registerListeners(Main main, SetAndGet setAndGet, ConfigGUI configGUI) {
        getServer().getPluginManager().registerEvents(setAndGet.getBucketListener(), main);
        getServer().getPluginManager().registerEvents(setAndGet.getBarrierListener(), main);
        getServer().getPluginManager().registerEvents(setAndGet.getBedrockListener(), main);
        getServer().getPluginManager().registerEvents(setAndGet.getWaterBucketListener(), main);
        getServer().getPluginManager().registerEvents(setAndGet.getTntListener(), main);
        getServer().getPluginManager().registerEvents(setAndGet.getCreeperListener(), main);
        getServer().getPluginManager().registerEvents(configGUI, main);
        getServer().getPluginManager().registerEvents(new Shortbow(), main);
    }

    public void registerCommands(Main main, SetAndGet setAndGet, ConfigGUI configGUI) {
        main.getCommand("stopwauh").setExecutor(new StopWauh(setAndGet));
        main.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(setAndGet));
        main.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(setAndGet));
        main.getCommand("Test").setExecutor(new Test(configGUI));
        main.getCommand("givecustomitems").setExecutor(new GiveCustomItems());
        main.getCommand("ench").setExecutor(new Ench(setAndGet));
        main.getCommand("spawn").setExecutor(new Spawn());
    }

    public void registerTabCompleters(Main main, SetAndGet setAndGet) {
        main.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(setAndGet));
        main.getCommand("givecustomitems").setTabCompleter(new GiveCustomItems());
        main.getCommand("ench").setTabCompleter(new Ench(setAndGet));
        main.getCommand("spawn").setTabCompleter(new Spawn());
    }
}
