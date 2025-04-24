package de.morty.backpacks;

import org.bukkit.plugin.java.JavaPlugin;

public class BackpackPlugin extends JavaPlugin {

    private static BackpackPlugin instance;
    private BackpackManager backpackManager;
    private TeamBackpackManager teamBackpackManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        backpackManager = new BackpackManager(this);
        getServer().getPluginManager().registerEvents(backpackManager, this);
        getCommand("backpack").setExecutor(new BackpackCommand(backpackManager));
        getCommand("setbackpacksize").setExecutor(new SetBackpackSizeCommand(backpackManager));

        teamBackpackManager = new TeamBackpackManager(this);

        getCommand("teambackpack").setExecutor(new TeamBackpackCommand(this, teamBackpackManager));
    }

    @Override
    public void onDisable() {
        if (backpackManager != null) {
            backpackManager.saveAll();
        }

        if (teamBackpackManager != null) {
            teamBackpackManager.saveTeamMembers();
        }
    }

    public static BackpackPlugin getInstance() {
        return instance;
    }
}
