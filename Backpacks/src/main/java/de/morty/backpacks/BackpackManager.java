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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BackpackManager implements Listener {

    private final BackpackPlugin plugin;
    private final Map<UUID, Inventory> openInventories = new HashMap<>();
    private final Map<UUID, Integer> playerBackpackSizes = new HashMap<>();

    public BackpackManager(BackpackPlugin plugin) {
        this.plugin = plugin;
        loadAllBackpacks();
    }

    public Inventory getBackpack(Player player) {
        UUID uuid = player.getUniqueId();
        int size = playerBackpackSizes.getOrDefault(uuid, 27);

        File file = new File(plugin.getDataFolder(), "backpacks.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<ItemStack> items = (List<ItemStack>) config.getList("backpacks." + uuid + ".items");
        if (items == null) {
            items = new ArrayList<>();
        }

        Inventory inv = Bukkit.createInventory(null, size, "Backpack von " + player.getName());

        ItemStack[] contents = new ItemStack[size];
        for (int i = 0; i < Math.min(size, items.size()); i++) {
            contents[i] = items.get(i);
        }

        inv.setContents(contents);
        openInventories.put(uuid, inv);
        return inv;
    }

    public void setSize(Player player, int size) {
        playerBackpackSizes.put(player.getUniqueId(), size);
        saveBackpack(player);
    }

    public void saveBackpack(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inv = openInventories.get(uuid);
        if (inv == null) {
            return;
        }

        File file = new File(plugin.getDataFolder(), "backpacks.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("backpacks." + uuid + ".items", Arrays.asList(inv.getContents()));
        config.set("backpacks." + uuid + ".size", inv.getSize());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        openInventories.remove(uuid);
    }

    public void saveAll() {
        for (UUID uuid : new ArrayList<>(openInventories.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                saveBackpack(player);
            }
        }
    }

    private void loadAllBackpacks() {
        File file = new File(plugin.getDataFolder(), "backpacks.yml");
        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("backpacks")) {
            return;
        }

        for (String uuidString : config.getConfigurationSection("backpacks").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int size = config.getInt("backpacks." + uuid + ".size", 27);
            playerBackpackSizes.put(uuid, size);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        saveBackpack(player);
    }
}
