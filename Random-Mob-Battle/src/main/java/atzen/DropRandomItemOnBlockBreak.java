package atzen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DropRandomItemOnBlockBreak extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        List<String> blacklist = this.getConfig().getStringList("blacklist");

        Material[] allMaterials = Material.values();
        List<Material> validMaterials = new ArrayList<>();

        for (Material mat : allMaterials) {
            if (mat.isItem() && !blacklist.contains(mat.name())) {
                validMaterials.add(mat);
            }
        }

        if (validMaterials.isEmpty()) return;

        Random r = new Random();
        Material randomMat = validMaterials.get(r.nextInt(validMaterials.size()));
        int amount = r.nextInt(16) + 1;

        ItemStack drop = new ItemStack(randomMat, amount);

        event.setDropItems(false);
        World world = event.getPlayer().getWorld();
        world.dropItemNaturally(event.getBlock().getLocation(), drop);
    }
}