/*package org.gecko.wauh.informations;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.gecko.wauh.Listeners.BarrierListener;
import org.gecko.wauh.Listeners.BucketListener;

public class RemovalInfo {
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    Player playerWauh = null;
    Player playerBarrier = null;

    public RemovalInfo(BucketListener bucketListener, BarrierListener barrierListener) {
        this.bucketListener = bucketListener;
        this.barrierListener = barrierListener;
    }

    public void ShowRemovalInfo() {
        String message = ChatColor.RED.toString() + ChatColor.BOLD;
        if (bucketListener.currentRemovingPlayer != null) {
            playerWauh = bucketListener.currentRemovingPlayer;
        } else if (barrierListener.currentRemovingPlayer != null) {
            playerBarrier = barrierListener.currentRemovingPlayer;
        }

        if (bucketListener.wauhRemovalActive && barrierListener.blockRemovalActive) {
            message += "A wauh removal and a block removal is running";
        } else if (bucketListener.wauhRemovalActive) {
            message += "A wauh removal is running";
        } else if (barrierListener.blockRemovalActive) {
            message += "A block removal is running";
        }
        if (playerWauh != null) {
            playerWauh.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        } else if (playerBarrier != null) {
            playerBarrier.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
        playerBarrier = null;
        playerWauh = null;
    }
}
*/