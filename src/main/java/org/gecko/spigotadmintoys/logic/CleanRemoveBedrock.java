package org.gecko.spigotadmintoys.logic;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class CleanRemoveBedrock {

    private static final Set<Material> fallingBlocks = EnumSet.of(Material.SAND, Material.GRAVEL);
    private final SetAndGet setAndGet;

    CleanRemoveBedrock(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    public void cleanRemoveBedrockFunction(int scaledBlocksPerIteration, Iterator<Block> iterator, Set<Block> markedBlocks, String source) {
        List<Block> blocksToRemove = new ArrayList<>();
        boolean repeated;
        Player currentRemovingPlayer;
        int repetitions;
        Set<Block> removedBlocks;
        if (source.equalsIgnoreCase("bedrock")) {
            repeated = setAndGet.getBedrockListener().isRepeated();
            currentRemovingPlayer = setAndGet.getBedrockListener().getCurrentRemovingPlayer();
            repetitions = setAndGet.getBedrockListener().getRepetitions();
            removedBlocks = setAndGet.getBedrockListener().getRemovedBlocks();
        } else {
            repeated = setAndGet.getSphereMaker().isRepeated();
            currentRemovingPlayer = setAndGet.getSphereMaker().getCurrentRemovingPlayer();
            repetitions = setAndGet.getSphereMaker().getRepetitions();
            removedBlocks = setAndGet.getSphereMaker().getRemovedBlocks();
        }
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            if (repeated) {
                assert currentRemovingPlayer != null;
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up falling blocks (" + repetitions + (repetitions == 1 ? " repetition left)" : " repetitions left)")));
                if (fallingBlocks.contains(block.getType())) {
                    block.setType(Material.AIR);
                    // Add the block to the new set
                    removedBlocks.add(block);

                    // Add the block to temporary list
                    blocksToRemove.add(block);
                } else {
                    // Add the block to temporary list
                    blocksToRemove.add(block);
                }
            } else {
                block.setType(Material.AIR);
                // Add the block to the new set
                removedBlocks.add(block);

                // Add the block to temporary list
                blocksToRemove.add(block);
                assert currentRemovingPlayer != null;
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up blocks, " + markedBlocks.size() + " blocks left. That's " + (markedBlocks.size() / scaledBlocksPerIteration + 1) + " iteration(s) left"));
            }
        }

        for (Block block : blocksToRemove) {
            markedBlocks.remove(block);
        }
    }

}
