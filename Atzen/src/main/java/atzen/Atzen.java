package atzen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Atzen extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!getConfig().contains("randomize-amount")) {
            getConfig().set("randomize-amount", false);
            saveConfig();
        }

        getServer().getPluginManager().registerEvents(new DropRandomItemOnBlockBreak(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("atzen")) {
            if (args.length == 0) {
                sender.sendMessage("§eAvailable subcommands: /atzen randomizeAmount [true|false]");
                return true;
            }

            if (args[0].equalsIgnoreCase("randomizeAmount")) {
                if (args.length == 1) {

                    boolean current = getConfig().getBoolean("randomize-amount", false);
                    sender.sendMessage("§eCurrent value of §6randomize-amount§e: " + current);
                    return true;
                }

                String input = args[1].toLowerCase();
                boolean newValue;

                if (input.equals("true") || input.equals("1")) {
                    newValue = true;
                } else if (input.equals("false") || input.equals("0")) {
                    newValue = false;
                } else {
                    sender.sendMessage("§cInvalid value. Use true/false or 1/0.");
                    return true;
                }

                getConfig().set("randomize-amount", newValue);
                saveConfig();

                sender.sendMessage("§aThe value of §6randomize-amount§a has been set to " + newValue + ".");
                return true;
            }

            sender.sendMessage("§cUnknown subcommand.");
            return true;
        }

        return false;
    }

}