package atzen;

import org.bukkit.plugin.java.JavaPlugin;

public class Atzen extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }
}
