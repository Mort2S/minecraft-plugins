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
        loadSizes();
    }

    public Inventory getBackpack(Player player) {
        UUID uuid = player.getUniqueId();
        int size = playerBackpackSizes.getOrDefault(uuid, 27);

        File file = new File(plugin.getDataFolder(), "backpacks.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<ItemStack> items = (List<ItemStack>) config.getList("backpacks." + uuid + ".items");
        if (items == null) items = new ArrayList<>();

        Inventory inv = Bukkit.createInventory(null, size, "Backpack von " + player.getName());
        for (int i = 0; i < size; i++) {
            if (i < items.size()) inv.setItem(i, items.get(i));
        }

        openInventories.put(uuid, inv);
        return inv;
    }

    public void setSize(Player player, int size) {
        playerBackpackSizes.put(player.getUniqueId(), size);
        saveSizes();
    }

    public void saveAll() {
        File file = new File(plugin.getDataFolder(), "backpacks.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<UUID, Inventory> entry : openInventories.entrySet()) {
            UUID uuid = entry.getKey();
            Inventory inv = entry.getValue();
            List<ItemStack> items = Arrays.asList(inv.getContents());
            config.set("backpacks." + uuid + ".items", items);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSizes() {
        File file = new File(plugin.getDataFolder(), "sizes.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : playerBackpackSizes.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSizes() {
        File file = new File(plugin.getDataFolder(), "sizes.yml");
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int size = config.getInt(key);
                playerBackpackSizes.put(uuid, size);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (openInventories.containsKey(uuid)) {
            openInventories.put(uuid, event.getInventory());
        }
    }

    public Inventory getCachedInventory(Player player) {
        return openInventories.get(player.getUniqueId());
    }
}
