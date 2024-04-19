package org.gecko.spigotadmintoys.enchantments.tools.pickaxes;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Drill extends Enchantment implements Listener {

    public static final String DRILLSTRING = "Drill";
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA);

    public Drill() {
        super(103);
    }

    @Override
    public String getName() {
        return DRILLSTRING;
    }

    @Override
    public int getMaxLevel() {
        return 5; // You can adjust the maximum level as needed
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
        return enchantment == Enchantment.getByName("Smelt");
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType().equals(Material.DIAMOND_PICKAXE);
    }

    @EventHandler
    public void onDrill(BlockBreakEvent event) {
        ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();

        // This uses a map of all enchantments because for some reason, using the preexisting function doesn't work
        Map<Enchantment, Integer> itemEnch = mainHandItem.getEnchantments();
        if (itemEnch.containsKey(Enchantment.getByName(DRILLSTRING))) {
            int level = itemEnch.get(Enchantment.getByName(DRILLSTRING));
            int range = 2 * level + 1;

            // Calculate middle position
            int middleX = range / 2;
            int middleY = range / 2;
            int middleZ = range / 2;

            for (int x = 0; x < range; x++) {
                for (int y = 0; y < range; y++) {
                    for (int z = 0; z < range; z++) {
                        Block block = event.getBlock().getRelative(x - middleX, y - middleY, z - middleZ);

                        // Check if the block is an immutable material
                        if (IMMUTABLE_MATERIALS.contains(block.getType())) {
                            continue;
                        }

                        // Break the block naturally
                        block.breakNaturally();
                    }
                }
            }
        }
    }
}
