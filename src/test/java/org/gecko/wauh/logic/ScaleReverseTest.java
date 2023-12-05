package org.gecko.wauh.logic;

import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.Main;
import org.gecko.wauh.listeners.BarrierListener;
import org.gecko.wauh.listeners.BedrockListener;
import org.gecko.wauh.listeners.BucketListener;
import org.gecko.wauh.listeners.WaterBucketListener;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.*;

class ScaleReverseTest {

    // create dummy block
    Block blockMock = Mockito.mock(Block.class);
    Set<Block> markedBlocks = new HashSet<>();

    @Test
    void testScaleReverseLogic_BucketSource() {
        try (MockedStatic<JavaPlugin> mocked = Mockito.mockStatic(JavaPlugin.class)) {
            // Arrange
            Main mainMock = Mockito.mock(Main.class);
            mocked.when(() -> JavaPlugin.getPlugin(Main.class)).thenReturn(mainMock);

            BucketListener bucketListenerMock = Mockito.mock(BucketListener.class);
            when(mainMock.getBucketListener()).thenReturn(bucketListenerMock);

            BarrierListener barrierListenerMock = Mockito.mock(BarrierListener.class);
            when(mainMock.getBarrierListener()).thenReturn(barrierListenerMock);

            BedrockListener bedrockListenerMock = Mockito.mock(BedrockListener.class);
            when(mainMock.getBedrockListener()).thenReturn(bedrockListenerMock);

            WaterBucketListener waterBucketListenerMock = Mockito.mock(WaterBucketListener.class);
            when(mainMock.getWaterBucketListener()).thenReturn(waterBucketListenerMock);

            ScaleReverse scaleReverse = new ScaleReverse();
            int totalRemovedCount = 20;
            int radiusLimit = 3;
            markedBlocks.add(blockMock);

            // Act
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bucket");

            // Assertions
            // Verify that CleanRemove() method of BucketListener is called once
            verify(bucketListenerMock, times(1)).CleanRemove(anyInt(), any(Iterator.class));
        }
    }

    @Test
    void testScaleReverseLogic_BarrierSource() {
        try (MockedStatic<JavaPlugin> mocked = Mockito.mockStatic(JavaPlugin.class)) {
            // Arrange
            Main mainMock = Mockito.mock(Main.class);
            mocked.when(() -> JavaPlugin.getPlugin(Main.class)).thenReturn(mainMock);

            BucketListener bucketListenerMock = Mockito.mock(BucketListener.class);
            when(mainMock.getBucketListener()).thenReturn(bucketListenerMock);

            BarrierListener barrierListenerMock = Mockito.mock(BarrierListener.class);
            when(mainMock.getBarrierListener()).thenReturn(barrierListenerMock);

            BedrockListener bedrockListenerMock = Mockito.mock(BedrockListener.class);
            when(mainMock.getBedrockListener()).thenReturn(bedrockListenerMock);

            WaterBucketListener waterBucketListenerMock = Mockito.mock(WaterBucketListener.class);
            when(mainMock.getWaterBucketListener()).thenReturn(waterBucketListenerMock);

            ScaleReverse scaleReverse = new ScaleReverse();
            int totalRemovedCount = 30;
            int radiusLimit = 4;
            markedBlocks.add(blockMock);

            // Act
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "barrier");

            // Assertions
            // Verify that CleanRemove() method of BarrierListener is called once
            verify(barrierListenerMock, times(1)).CleanRemove(anyInt(), any(Iterator.class));
        }
    }

    @Test
    void testScaleReverseLogic_BedrockSource() {
        try (MockedStatic<JavaPlugin> mocked = Mockito.mockStatic(JavaPlugin.class)) {
            // Arrange
            Main mainMock = Mockito.mock(Main.class);
            mocked.when(() -> JavaPlugin.getPlugin(Main.class)).thenReturn(mainMock);

            BucketListener bucketListenerMock = Mockito.mock(BucketListener.class);
            when(mainMock.getBucketListener()).thenReturn(bucketListenerMock);

            BarrierListener barrierListenerMock = Mockito.mock(BarrierListener.class);
            when(mainMock.getBarrierListener()).thenReturn(barrierListenerMock);

            BedrockListener bedrockListenerMock = Mockito.mock(BedrockListener.class);
            when(mainMock.getBedrockListener()).thenReturn(bedrockListenerMock);

            WaterBucketListener waterBucketListenerMock = Mockito.mock(WaterBucketListener.class);
            when(mainMock.getWaterBucketListener()).thenReturn(waterBucketListenerMock);

            ScaleReverse scaleReverse = new ScaleReverse();
            int totalRemovedCount = 10;
            int radiusLimit = 2;
            markedBlocks.add(blockMock);

            // Act
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bedrock");

            // Assertions
            // Verify that CleanRemove() method of BedrockListener is called once
            verify(bedrockListenerMock, times(1)).CleanRemove(anyInt(), any(Iterator.class));
        }
    }

    @Test
    void testScaleReverseLogic_WauhSource() {
        try (MockedStatic<JavaPlugin> mocked = Mockito.mockStatic(JavaPlugin.class)) {
            // Arrange
            Main mainMock = Mockito.mock(Main.class);
            mocked.when(() -> JavaPlugin.getPlugin(Main.class)).thenReturn(mainMock);

            BucketListener bucketListenerMock = Mockito.mock(BucketListener.class);
            when(mainMock.getBucketListener()).thenReturn(bucketListenerMock);

            BarrierListener barrierListenerMock = Mockito.mock(BarrierListener.class);
            when(mainMock.getBarrierListener()).thenReturn(barrierListenerMock);

            BedrockListener bedrockListenerMock = Mockito.mock(BedrockListener.class);
            when(mainMock.getBedrockListener()).thenReturn(bedrockListenerMock);

            WaterBucketListener waterBucketListenerMock = Mockito.mock(WaterBucketListener.class);
            when(mainMock.getWaterBucketListener()).thenReturn(waterBucketListenerMock);

            ScaleReverse scaleReverse = new ScaleReverse();
            int totalRemovedCount = 40;
            int radiusLimit = 5;
            markedBlocks.add(blockMock);

            // Act
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "wauh");

            // Assertions
            // Verify that CleanRemove() method of WauhBucketListener is called once
            verify(waterBucketListenerMock, times(1)).CleanRemove(anyInt(), any(Iterator.class));
        }
    }
}