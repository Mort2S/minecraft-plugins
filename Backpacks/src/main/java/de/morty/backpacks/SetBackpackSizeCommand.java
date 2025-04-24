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

        if (!sender.hasPermission("backpacks.setsize")) {
            sender.sendMessage("§cDazu hast du keine Berechtigung.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cBenutzung: /setbackpacksize <Spieler> <Größe>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden oder offline.");
            return true;
        }

        int size;
        try {
            size = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cUngültige Zahl.");
            return true;
        }

        if (size % 9 != 0 || size < 9 || size > 54) {
            sender.sendMessage("§cGröße muss durch 9 teilbar und zwischen 9 und 54 sein.");
            return true;
        }

        manager.setSize(target, size);

        if (manager.getCachedInventory(target) != null) {
            target.openInventory(manager.getBackpack(target));
        }

        sender.sendMessage("§aBackpack-Größe für " + target.getName() + " wurde auf " + size + " gesetzt.");
        return true;
    }
}
