package de.morty.backpacks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class TeamBackpackManager {

    private final BackpackPlugin plugin;
    private final Map<String, Inventory> teamBackpacks = new HashMap<>();
    private final Map<String, List<UUID>> teamMembers = new HashMap<>();

    public TeamBackpackManager(BackpackPlugin plugin) {
        this.plugin = plugin;
        loadTeamBackpacks();
        loadTeamMembers();
    }

    public Inventory getTeamBackpack(Player player, String teamName) {
        if (!isMemberOfTeam(player, teamName) && !player.isOp()) {
            player.sendMessage("Du bist kein Mitglied dieses Teams und hast keinen Zugriff auf das Backpack.");
            return null;
        }

        if (!teamBackpacks.containsKey(teamName)) {
            createTeamBackpack(teamName);
        }

        return teamBackpacks.get(teamName);
    }

    public Inventory getTeamBackpack(String teamName) {
        if (!teamBackpacks.containsKey(teamName)) {
            createTeamBackpack(teamName);
        }
        return teamBackpacks.get(teamName);
    }

    public boolean isMemberOfTeam(Player player, String teamName) {
        List<UUID> members = teamMembers.get(teamName);
        return members != null && members.contains(player.getUniqueId());
    }

    public String getPlayerTeam(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        return team != null ? team.getName() : null;
    }

    public void createTeamBackpack(String teamName) {
        Inventory inv = Bukkit.createInventory(null, 54, "Team Backpack: " + teamName);
        teamBackpacks.put(teamName, inv);
        saveTeamBackpack(teamName, inv);
    }

    private void saveTeamBackpack(String teamName, Inventory inv) {
        File file = new File(plugin.getDataFolder(), "team_backpacks.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<ItemStack> items = new ArrayList<>(Arrays.asList(inv.getContents()));
        config.set(teamName + ".items", items);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTeamBackpacks() {
        File file = new File(plugin.getDataFolder(), "team_backpacks.yml");
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String teamName : config.getKeys(false)) {
            List<ItemStack> items = (List<ItemStack>) config.getList(teamName + ".items");
            Inventory inv = Bukkit.createInventory(null, 54, "Team Backpack: " + teamName);
            for (int i = 0; i < 54 && items != null && i < items.size(); i++) {
                inv.setItem(i, items.get(i));
            }
            teamBackpacks.put(teamName, inv);
        }
    }

    private void loadTeamMembers() {
        File file = new File(plugin.getDataFolder(), "teams.yml");
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String teamName : config.getKeys(false)) {
            List<UUID> members = new ArrayList<>();
            for (String id : config.getStringList(teamName + ".members")) {
                members.add(UUID.fromString(id));
            }
            teamMembers.put(teamName, members);
        }
    }

    public void saveTeamMembers() {
        File file = new File(plugin.getDataFolder(), "teams.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, List<UUID>> entry : teamMembers.entrySet()) {
            List<String> ids = new ArrayList<>();
            for (UUID uuid : entry.getValue()) {
                ids.add(uuid.toString());
            }
            config.set(entry.getKey() + ".members", ids);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMemberToTeam(String teamName, Player player) {
        teamMembers.computeIfAbsent(teamName, k -> new ArrayList<>()).add(player.getUniqueId());
        saveTeamMembers();
    }

    public void removeMemberFromTeam(String teamName, Player player) {
        if (teamMembers.containsKey(teamName)) {
            teamMembers.get(teamName).remove(player.getUniqueId());
            saveTeamMembers();
        }
    }
}
