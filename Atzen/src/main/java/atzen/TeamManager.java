package atzen;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamManager {

    private final JavaPlugin plugin;
    private final Map<String, Inventory> teamBackpacks = new HashMap<>();
    private final Map<Player, String> playerTeams;

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerTeams = new HashMap<>();
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
}
