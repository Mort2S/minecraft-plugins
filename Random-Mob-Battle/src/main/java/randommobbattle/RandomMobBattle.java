package randommobbattle;

import org.bukkit.plugin.java.JavaPlugin;

public class RandomMobBattle extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);
        getCommand("mobstats").setExecutor(new MobStatsCommand());
    }
}
