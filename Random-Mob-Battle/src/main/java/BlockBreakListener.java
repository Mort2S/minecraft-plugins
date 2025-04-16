package randommobbattle;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class BlockBreakListener implements Listener {

    private final List<String> allowedDrops;
    private final Random random = new Random();

    public BlockBreakListener(FileConfiguration config) {
        this.allowedDrops = config.getStringList("allowed-drops");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (allowedDrops.isEmpty()) return;

        String materialName = allowedDrops.get(random.nextInt(allowedDrops.size()));
        Material material = Material.matchMaterial(materialName);

        if (material != null && material.isItem()) {
            player.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(material, 1));
        }
    }
}
