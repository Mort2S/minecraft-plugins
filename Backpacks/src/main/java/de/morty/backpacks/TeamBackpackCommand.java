package de.morty.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Team;

public class TeamBackpackCommand implements CommandExecutor {

    private final BackpackPlugin plugin;
    private final TeamBackpackManager teamBackpackManager;

    public TeamBackpackCommand(BackpackPlugin plugin, TeamBackpackManager teamBackpackManager) {
        this.plugin = plugin;
        this.teamBackpackManager = teamBackpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("");
            return true;
        }

        Player player = (Player) sender;

        String teamName;

        if (args.length == 0) {
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (team == null) {
                player.sendMessage("Du bist in keinem Team.");
                return true;
            }
            teamName = team.getName();
        } else {
            teamName = args[0];
            if (!player.isOp()) {
                player.sendMessage("Nur Admins dürfen auf andere Team-Backpacks zugreifen.");
                return true;
            }
        }

        Inventory teamInventory = teamBackpackManager.getTeamBackpack(teamName);
        player.openInventory(teamInventory);
        player.sendMessage("Team-Backpack von Team '" + teamName + "' geöffnet.");
        return true;
    }
}
