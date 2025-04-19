package atzen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Atzen extends JavaPlugin implements TabExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!getConfig().contains("randomize-amount")) {
            getConfig().set("randomize-amount", false);
            saveConfig();
        }

        getCommand("atzen").setExecutor(this);
        getCommand("atzen").setTabCompleter(this);

        getServer().getPluginManager().registerEvents(new DropRandomItemOnBlockBreak(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("atzen")) return false;

        FileConfiguration config = getConfig();

        if (args.length == 0) {
            sender.sendMessage("§eAvailable subcommands: /atzen randomizeAmount [true|false], reshuffleDrops, addBlacklistItem <item>, removeBlacklistItem <item>");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "randomizeamount":
                if (args.length == 1) {
                    boolean current = config.getBoolean("randomize-amount", false);
                    sender.sendMessage("§eCurrent value of §6randomize-amount§e: " + current);
                } else {
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
                    config.set("randomize-amount", newValue);
                    saveConfig();
                    sender.sendMessage("§aThe value of §6randomize-amount§a has been set to " + newValue + ".");
                }
                return true;

            case "reshuffledrops":
                if (config.contains("drops")) {
                    config.set("drops", null);
                    saveConfig();
                    sender.sendMessage("§aAll block drop mappings have been reshuffled.");
                } else {
                    sender.sendMessage("§eNo drops were set yet, nothing to reshuffle.");
                }
                return true;

            case "addblacklistitem":
                if (args.length != 2) {
                    sender.sendMessage("§cUsage: /atzen addBlacklistItem <item>");
                    return true;
                }

                String addItem = args[1].toUpperCase(Locale.ROOT).replace("MINECRAFT:", "");
                Material addMat = Material.matchMaterial(addItem);
                if (addMat == null || !addMat.isItem()) {
                    sender.sendMessage("§cInvalid item name.");
                    return true;
                }

                List<String> blacklist = config.getStringList("blacklist");
                if (blacklist.contains(addItem)) {
                    sender.sendMessage("§e" + addItem + " is already blacklisted.");
                    return true;
                }

                blacklist.add(addItem);
                config.set("blacklist", blacklist);

                // Remove drops that would result in this item
                if (config.contains("drops")) {
                    Map<String, Object> drops = config.getConfigurationSection("drops").getValues(false);
                    drops.entrySet().removeIf(entry -> addItem.equals(entry.getValue()));
                    config.set("drops", drops);
                }

                saveConfig();
                sender.sendMessage("§a" + addItem + " has been added to the blacklist and removed from drops.");
                return true;

            case "removeblacklistitem":
                if (args.length != 2) {
                    sender.sendMessage("§cUsage: /atzen removeBlacklistItem <item>");
                    return true;
                }

                String remItem = args[1].toUpperCase(Locale.ROOT).replace("MINECRAFT:", "");
                List<String> blist = config.getStringList("blacklist");

                if (!blist.contains(remItem)) {
                    sender.sendMessage("§e" + remItem + " is not in the blacklist.");
                    return true;
                }

                blist.remove(remItem);
                config.set("blacklist", blist);
                saveConfig();
                sender.sendMessage("§a" + remItem + " has been removed from the blacklist.");
                return true;

            default:
                sender.sendMessage("§cUnknown subcommand.");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("atzen")) return null;

        if (args.length == 1) {
            return Arrays.asList("randomizeAmount", "reshuffleDrops", "addBlacklistItem", "removeBlacklistItem");
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("addBlacklistItem") || args[0].equalsIgnoreCase("removeBlacklistItem"))) {
            List<String> matches = new ArrayList<>();
            for (Material mat : Material.values()) {
                if (!mat.isItem()) continue;
                String name = "minecraft:" + mat.name().toLowerCase();
                if (name.startsWith(args[1].toLowerCase())) {
                    matches.add(name);
                }
            }
            return matches;
        }

        return Collections.emptyList();
    }
}
