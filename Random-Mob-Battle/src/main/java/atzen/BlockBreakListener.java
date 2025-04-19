package atzen;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material[] possibleDrops = Material.values();
        Material randomMaterial;

        do {
            randomMaterial = possibleDrops[random.nextInt(possibleDrops.length)];
        } while (!randomMaterial.isItem());

        player.getWorld().dropItemNaturally(
            event.getBlock().getLocation(),
            new ItemStack(randomMaterial, 1)
        );
    }
}
