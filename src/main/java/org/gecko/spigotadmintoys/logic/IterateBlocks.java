package org.gecko.spigotadmintoys.logic;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

public class IterateBlocks {

    public void iterateBlocks(Block block, Set<Block> nextSet, Set<Material> immutableMaterials) {
        // Iterate through neighboring blocks and add them to the next set
        for (int i = -1; i <= 1; i++) {
            if (i == 0) continue; // Skip the current block
            addIfValid(block.getRelative(i, 0, 0), nextSet, immutableMaterials);
            addIfValid(block.getRelative(0, i, 0), nextSet, immutableMaterials);
            addIfValid(block.getRelative(0, 0, i), nextSet, immutableMaterials);
        }
    }

    private void addIfValid(Block block, Set<Block> nextSet, Set<Material> immutableMaterials) {
        if (immutableMaterials.contains(block.getType())) {
            nextSet.add(block);
        }
    }

}
