package org.gecko.spigotadmintoys.logic;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;

import java.util.Set;

public class BlockRemovalScheduler {
    public void scheduleBlockRemoval(Set<Block> markedBlocks, Set<Block> removedBlocks, Player currentRemovingPlayer, Runnable removeMarkedBlocksMethod, Runnable clearMethod, int repetitions, Runnable lowerMethod) {
        if (currentRemovingPlayer == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: currentRemovingPlayer is null! Please investigate.");
            clearMethod.run();
            return;
        }
        if (!markedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), removeMarkedBlocksMethod, 10L);
        } else if (!removedBlocks.isEmpty()) {
            if (repetitions > 0) {
                lowerMethod.run();
                markedBlocks.addAll(removedBlocks);
                removedBlocks.clear();
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), removeMarkedBlocksMethod, 100L);
            } else {
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Falling block cleanup finished!"));
                clearMethod.run();
            }
        } else {
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Falling block cleanup finished!"));
            clearMethod.run();
        }
    }
}