package org.gecko.wauh.logic;

import org.gecko.wauh.Main;
import org.gecko.wauh.logic.ScaleReverse;
import org.gecko.wauh.listeners.BarrierListener;
import org.gecko.wauh.listeners.BedrockListener;
import org.gecko.wauh.listeners.BucketListener;
import org.gecko.wauh.listeners.WaterBucketListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;

import static org.junit.jupiter.api.Assertions.*;

class ScaleReverseTest {
  
  // create dummy block
  Block blockMock = Mockito.mock(Block.class);
  Set<Block> markedBlocks = new HashSet<>();


  @Test
  void testScaleReverseLogic_BedrockSource() {
    ScaleReverse scaleReverse = new ScaleReverse();
    int totalRemovedCount = 10;
    int radiusLimit = 2;
    markedBlocks.add(blockMock);

    scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bedrock");
    
    // assertions
  }

  @Test
  void testScaleReverseLogic_BucketSource() {
    ScaleReverse scaleReverse = new ScaleReverse();
    int totalRemovedCount = 20;
    int radiusLimit = 3;
    markedBlocks.add(blockMock);

    scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bucket");
    
    // assertions
  }

  @Test
  void testScaleReverseLogic_BarrierSource() {
    ScaleReverse scaleReverse = new ScaleReverse();
    int totalRemovedCount = 30;
    int radiusLimit = 4;
    markedBlocks.add(blockMock);

    scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "barrier");
    
    // assertions
  }

  @Test
  void testScaleReverseLogic_WauhSource() {
    ScaleReverse scaleReverse = new ScaleReverse();
    int totalRemovedCount = 40;
    int radiusLimit = 5;
    markedBlocks.add(blockMock);

    scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "wauh");
    
    // assertions
  }
}