package randommobbattle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public class MobStatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        UUID uuid = player.getUniqueId();
        Map<EntityType, Integer> kills = MobKillListener.mobKills.get(uuid);

        Inventory inv = Bukkit.createInventory(player, 54, "§aDeine Mob-Kills");

        if (kills != null) {
            for (Map.Entry<EntityType, Integer> entry : kills.entrySet()) {
                EntityType type = entry.getKey();
                int count = entry.getValue();

                Material spawnEgg = Material.matchMaterial(type.name() + "_SPAWN_EGG");
                if (spawnEgg == null) continue;

                ItemStack item = new ItemStack(spawnEgg, Math.min(64, count));
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§f" + type.name() + " §7(Kills: " + count + ")");
                    item.setItemMeta(meta);
                }

                inv.addItem(item);
            }
        }

        player.openInventory(inv);
        return true;
    }
}
