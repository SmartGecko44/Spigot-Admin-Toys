package org.gecko.wauh.logic;

import org.bukkit.block.Block;
import org.gecko.wauh.Main;
import org.gecko.wauh.listeners.BarrierListener;
import org.gecko.wauh.listeners.BedrockListener;
import org.gecko.wauh.listeners.BucketListener;
import org.gecko.wauh.listeners.WaterBucketListener;

import java.util.*;

public class Scale {

    public void ScaleReverseLogic(int totalRemovedCount, int radiusLimit, Set<Block> markedBlocks, String source) {
        Main mainPlugin = Main.getPlugin(Main.class);

        BucketListener bucketListener = mainPlugin.getBucketListener();
        BarrierListener barrierListener = mainPlugin.getBarrierListener();
        BedrockListener bedrockListener = mainPlugin.getBedrockListener();
        WaterBucketListener waterBucketListener = mainPlugin.getWaterBucketListener();

        // Set BLOCKS_PER_ITERATION dynamically based on the total count
        int sqrtTotalBlocks = (int) (Math.sqrt(totalRemovedCount) * (Math.sqrt(radiusLimit) * 1.25));
        int scaledBlocksPerIteration = Math.max(1, sqrtTotalBlocks);
        // Update BLOCKS_PER_ITERATION based on the scaled value

        Iterator<Block> iterator = markedBlocks.iterator();

        if (source.equalsIgnoreCase("bedrock")) {
            bedrockListener.CleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("bucket")) {
            bucketListener.CleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("barrier")) {
            barrierListener.CleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("wauh")) {
            waterBucketListener.CleanRemove(scaledBlocksPerIteration, iterator);
        }

    }
}

