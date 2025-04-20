package atzen;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MobTrackerManager implements Listener {

    private final Atzen plugin;

    private final Map<UUID, Map<EntityType, Integer>> mobKills = new HashMap<>();
    private final Set<UUID> trackingActive = new HashSet<>();

    public MobTrackerManager(Atzen plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean startTracking(Player player, String durationString) {
        if (trackingActive.contains(player.getUniqueId())) {
            player.sendMessage("§cMob-Tracking läuft bereits.");
            return false;
        }

        long durationTicks = parseDurationToTicks(durationString);
        if (durationTicks <= 0) {
            player.sendMessage("§cUngültiges Zeitformat. Beispiel: 30s oder 2m");
            return false;
        }

        trackingActive.add(player.getUniqueId());
        mobKills.put(player.getUniqueId(), new HashMap<>());
        player.sendMessage("§aMob-Tracking gestartet für " + durationString + ".");

        new BukkitRunnable() {
            @Override
            public void run() {
                trackingActive.remove(player.getUniqueId());
                player.sendTitle("§aMob-Tracking beendet", "§7Nutze /atzen mobTrackerList", 10, 60, 20);
            }
        }.runTaskLater(plugin, durationTicks);

        return true;
    }

    public void openMobInventory(Player player) {
        Map<EntityType, Integer> kills = mobKills.getOrDefault(player.getUniqueId(), Collections.emptyMap());

        Inventory inv = Bukkit.createInventory(null, 54, "§aDeine getöteten Mobs");

        for (Map.Entry<EntityType, Integer> entry : kills.entrySet()) {
            Material eggMaterial = Material.matchMaterial(entry.getKey().name() + "_SPAWN_EGG");

            if (eggMaterial == null) continue;

            ItemStack egg = new ItemStack(eggMaterial, Math.min(entry.getValue(), 64));
            inv.addItem(egg);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player killer && trackingActive.contains(killer.getUniqueId())) {
            EntityType type = event.getEntityType();
            mobKills.get(killer.getUniqueId())
                    .merge(type, 1, Integer::sum);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        trackingActive.remove(event.getPlayer().getUniqueId());
    }

    private long parseDurationToTicks(String input) {
        Pattern pattern = Pattern.compile("(\\d+)([sm])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            return unit.equals("m") ? value * 60L * 20 : value * 20L;
        }
        return -1;
    }
}
