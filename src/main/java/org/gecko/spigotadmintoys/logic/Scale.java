package org.gecko.spigotadmintoys.logic;

import org.bukkit.block.Block;
import org.gecko.spigotadmintoys.listeners.*;

import java.util.Iterator;
import java.util.Set;

public class Scale {

    private final SetAndGet setAndGet;

    public Scale(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    public void scaleReverseLogic(int totalRemovedCount, int radiusLimit, Set<Block> markedBlocks, String source) {

        BucketListener bucketListener = setAndGet.getBucketListener();
        BarrierListener barrierListener = setAndGet.getBarrierListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        SphereMaker sphereMaker = setAndGet.getSphereMaker();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();

        // Set BLOCKS_PER_ITERATION dynamically based on the total count
        int sqrtTotalBlocks = (int) (Math.sqrt(totalRemovedCount) * (Math.sqrt(radiusLimit) * 1.25));
        int scaledBlocksPerIteration = Math.max(1, sqrtTotalBlocks);
        // Update BLOCKS_PER_ITERATION based on the scaled value

        Iterator<Block> iterator = markedBlocks.iterator();

        if (source.equalsIgnoreCase("bedrock")) {
            bedrockListener.cleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("bucket")) {
            bucketListener.cleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("barrier")) {
            barrierListener.cleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("wauh")) {
            waterBucketListener.cleanRemove(scaledBlocksPerIteration, iterator);
        } else if (source.equalsIgnoreCase("sphere")) {
            sphereMaker.cleanRemove(scaledBlocksPerIteration, iterator);
        }

    }
}

