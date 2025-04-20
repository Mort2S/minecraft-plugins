package atzen;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DropRandomItemOnBlockBreak implements Listener {

    private final Atzen plugin;

    public DropRandomItemOnBlockBreak(Atzen plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("random-drops-enabled", true)) {
            return;
        }

        FileConfiguration config = plugin.getConfig();

        String blockName = event.getBlock().getType().name();

        if (!config.contains("drops." + blockName)) {
            List<String> blacklist = config.getStringList("blacklist");

            Material[] allMaterials = Material.values();
            Material randomMaterial = null;
            Random r = new Random();

            while (randomMaterial == null) {
                Material candidate = allMaterials[r.nextInt(allMaterials.length)];
                if (candidate.isItem() && !blacklist.contains(candidate.name())) {
                    randomMaterial = candidate;
                }
            }

            config.set("drops." + blockName, randomMaterial.name());
            plugin.saveConfig();
        }

        String itemName = config.getString("drops." + blockName);
        Material dropMaterial = Material.matchMaterial(itemName);

        if (dropMaterial == null || !dropMaterial.isItem()) {
            return;
        }

        boolean randomize = config.getBoolean("randomize-amount", false);
        int amount = randomize ? new Random().nextInt(64) + 1 : 1;

        ItemStack drop = new ItemStack(dropMaterial, amount);
        event.setDropItems(false);

        World world = event.getPlayer().getWorld();
        world.dropItemNaturally(event.getBlock().getLocation(), drop);
    }
}
