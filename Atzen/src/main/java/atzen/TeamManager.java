package atzen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<String, Inventory> teamBackpacks = new HashMap<>();
    private final Map<Player, String> playerTeams;

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerTeams = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Team getPlayerTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }

    public void openTeamBackpack(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§cDu bist in keinem Minecraft-Team.");
            return;
        }

        String teamName = team.getName();
        Inventory backpack = teamBackpacks.computeIfAbsent(teamName,
                key -> Bukkit.createInventory(null, 36, "Team Backpack: " + teamName));
        player.openInventory(backpack);
    }

    public void saveBackpacks() {
        FileConfiguration config = plugin.getConfig();

        for (Map.Entry<String, Inventory> entry : teamBackpacks.entrySet()) {
            String teamName = entry.getKey();
            Inventory inv = entry.getValue();
            config.set("backpacks." + teamName, inv.getContents());
        }

        plugin.saveConfig();
    }

    public void loadBackpacks() {
        if (!plugin.getConfig().isConfigurationSection("backpacks")) return;

        for (String teamName : plugin.getConfig().getConfigurationSection("backpacks").getKeys(false)) {
            List<ItemStack> itemList = (List<ItemStack>) plugin.getConfig().get("backpacks." + teamName);
            ItemStack[] contents = itemList.toArray(new ItemStack[0]);

            Inventory inv = Bukkit.createInventory(null, 36, "Team Backpack: " + teamName);
            inv.setContents(contents);
            teamBackpacks.put(teamName, inv);
        }
    }

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent event) {
        HumanEntity human = event.getPlayer();
        if (!(human instanceof Player player)) return;

        Inventory closedInv = event.getInventory();

        for (Map.Entry<String, Inventory> entry : teamBackpacks.entrySet()) {
            if (entry.getValue().equals(closedInv)) {
                plugin.getConfig().set("backpacks." + entry.getKey(), closedInv.getContents());
                plugin.saveConfig();
                return;
            }
        }
    }
}
