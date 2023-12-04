package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.listeners.*;
import org.gecko.wauh.commands.SetRadiusLimitCommand;
import org.gecko.wauh.commands.StopWauh;
import org.gecko.wauh.commands.ToggleRemovalView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class MainTest {

    private Main plugin;
    private FileConfiguration config;
    private ConfigurationManager configManager;
    private Server server;
    private PluginManager pluginManager;
    private ConsoleCommandSender sender;

    @Test
    public void testOnEnable() {

        plugin = Mockito.spy(Main.class);
        server = mock(Server.class);
        pluginManager = mock(PluginManager.class);
        sender = mock(ConsoleCommandSender.class);
        config = mock(FileConfiguration.class);
        configManager = new ConfigurationManager(plugin);

        // Mock Plugin methods
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getConsoleSender()).thenReturn(sender);

        when(plugin.getCommand(anyString())).thenReturn(null);

        // Mock ConfigurationManager methods
        when(config.getInt(anyString(), anyInt())).thenReturn(10);

        // Mock Main methods
        when(plugin.getConfigManager()).thenReturn(configManager);
        when(configManager.getConfig()).thenReturn(config);

        plugin.onEnable();

        // Verify methods called
        verify(plugin, times(6)).getServer();
        verify(server, times(6)).getPluginManager();
        verify(server, times(2)).getConsoleSender();
        verify(sender, times(2)).sendMessage(anyString());
        verify(plugin, times(3)).getCommand(anyString());

        // Verify listeners registration
        verify(pluginManager).registerEvents(any(BucketListener.class), eq(plugin));
        verify(pluginManager).registerEvents(any(BarrierListener.class), eq(plugin));
        verify(pluginManager).registerEvents(any(BedrockListener.class), eq(plugin));
        verify(pluginManager).registerEvents(any(WaterBucketListener.class), eq(plugin));
        verify(pluginManager).registerEvents(any(TNTListener.class), eq(plugin));
        verify(pluginManager).registerEvents(any(CreeperListener.class), eq(plugin));
    }
}