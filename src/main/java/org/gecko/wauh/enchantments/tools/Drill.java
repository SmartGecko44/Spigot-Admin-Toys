package org.gecko.wauh.enchantments.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class Drill extends Enchantment implements Listener {

    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA);

    public Drill(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Drill";
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
        return EnchantmentTarget.TOOL;
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
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType().equals(Material.DIAMOND_PICKAXE);
    }

    @EventHandler
    public void onDrill(BlockBreakEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(this)) {
            int range = 1 + getMaxLevel();
            for (int x = -1; x <= range; x++) {
                for (int y = -1; y <= range; y++) {
                    for (int z = -1; z <= range; z++) {
                        Block block = event.getBlock().getRelative(x, y, z);
                        if (IMMUTABLE_MATERIALS.contains(block.getType())) {
                            continue;
                        }
                        block.breakNaturally();
                    }
                }
            }
        }
    }
}
