package org.gecko.wauh.enchantments.tools.pickaxes;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Smelt extends Enchantment implements Listener {

    private final Set<Material> smeltMaterials = EnumSet.of(Material.IRON_ORE, Material.GOLD_ORE);

    public Smelt() {
        super(104);
    }

    @Override
    public String getName() {
        return "Smelt";
    }

    @Override
    public int getMaxLevel() {
        return 1; // You can adjust the maximum level as needed
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment == Enchantment.getByName("Drill");
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType().equals(Material.DIAMOND_PICKAXE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // This uses a map of all enchantments because for some reason, using the preexisting function doesn't work
        Map<Enchantment, Integer> itemEnch = event.getPlayer().getInventory().getItemInMainHand().getEnchantments();
        if (itemEnch.containsKey(Enchantment.getByName("Smelt"))) {
            event.getBlock().getDrops().clear();
            if (smeltMaterials.contains(event.getBlock().getType())) {
                event.setCancelled(true);
                if (event.getBlock().getType() == Material.IRON_ORE) {
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, 1));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You smelted iron ore!"));
                } else if (event.getBlock().getType() == Material.GOLD_ORE) {
                    event.getBlock().setType(Material.AIR);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, 1));
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You smelted gold ore!"));
                }
            } else {
                event.getBlock().getDrops().add(new ItemStack(event.getBlock().getType()));
            }
        }
    }
}