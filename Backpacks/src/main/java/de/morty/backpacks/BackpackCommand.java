package de.morty.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BackpackCommand implements CommandExecutor {

    private final BackpackManager manager;

    public BackpackCommand(BackpackManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cNur Spieler können ihr eigenes Backpack öffnen.");
                return true;
            }

            Player player = (Player) sender;
            player.openInventory(manager.getBackpack(player));
            return true;
        }

        if (args.length == 1) {
            if (!(sender.hasPermission("backpack.others") || sender instanceof ConsoleCommandSender)) {
                sender.sendMessage("§cDazu hast du keine Berechtigung.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden oder offline.");
                return true;
            }

            Inventory targetInventory = manager.getCachedInventory(target);
            if (targetInventory == null) {
                targetInventory = manager.getBackpack(target);
            }

            if (sender instanceof Player) {
                ((Player) sender).openInventory(targetInventory);
                sender.sendMessage("§aBackpack von " + target.getName() + " geöffnet.");
            } else {
                sender.sendMessage("§aBackpack von " + target.getName() + " geladen.");
            }

            return true;
        }

        return false;
    }
}
