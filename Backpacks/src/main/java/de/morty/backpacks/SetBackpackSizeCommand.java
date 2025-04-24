package de.morty.backpacks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBackpackSizeCommand implements CommandExecutor {

    private final BackpackManager manager;

    public SetBackpackSizeCommand(BackpackManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) return false;

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        try {
            int size = Integer.parseInt(args[1]);
            if (size % 9 != 0 || size < 9 || size > 54) {
                sender.sendMessage("§cGröße muss ein Vielfaches von 9 zwischen 9 und 54 sein.");
                return true;
            }

            manager.setSize(target, size);
            sender.sendMessage("§aBackpack-Größe für " + target.getName() + " gesetzt: " + size);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cUngültige Zahl.");
        }

        return true;
    }
}
