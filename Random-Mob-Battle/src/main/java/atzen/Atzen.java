package atzen;

import org.bukkit.plugin.java.JavaPlugin;

public class Atzen extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new DropRandomItemOnBlockBreak(this), this);
    }

    @Override
    public void onDisable() {
    }
}
