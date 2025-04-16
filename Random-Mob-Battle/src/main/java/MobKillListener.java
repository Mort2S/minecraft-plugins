package randommobbattle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobKillListener implements Listener {

    public static final Map<UUID, Map<EntityType, Integer>> mobKills = new HashMap<>();

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        EntityType mobType = event.getEntityType();
        UUID uuid = killer.getUniqueId();

        mobKills.putIfAbsent(uuid, new HashMap<>());
        Map<EntityType, Integer> playerKills = mobKills.get(uuid);
        playerKills.put(mobType, playerKills.getOrDefault(mobType, 0) + 1);
    }
}
