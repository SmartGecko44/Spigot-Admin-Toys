package org.gecko.spigotadmintoys.logic;

import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

public class Scale {

    private final SetAndGet setAndGet;

    public Scale(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    public void scaleReverseLogic(int totalRemovedCount, int radiusLimit, Set<Block> markedBlocks, String source, BiConsumer<Integer, Iterator<Block>> cleanRemoveMethod) {
        // Set BLOCKS_PER_ITERATION dynamically based on the total count
        int sqrtTotalBlocks = (int) (Math.sqrt(totalRemovedCount) * (Math.sqrt(radiusLimit) * 1.25));
        int scaledBlocksPerIteration = Math.max(1, sqrtTotalBlocks);
        // Update BLOCKS_PER_ITERATION based on the scaled value

        Iterator<Block> iterator = markedBlocks.iterator();

        if (source.equalsIgnoreCase("bedrock") || source.equalsIgnoreCase("sphere")) {
            setAndGet.getCleanRemoveBedrock().cleanRemoveBedrockFunction(scaledBlocksPerIteration, iterator, markedBlocks, source);
            return;
        }
            cleanRemoveMethod.accept(scaledBlocksPerIteration, iterator);
    }
}

