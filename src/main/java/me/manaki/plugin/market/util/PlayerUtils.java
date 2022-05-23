package me.manaki.plugin.market.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerUtils {
    public static UUID getUUID(String playerName) {
        UUID uuid;
        uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
        return uuid;
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        return Bukkit.getOfflinePlayer(PlayerUtils.getUUID(name));
    }

    public static List<ItemStack> getInventoryStorage(Player p, boolean clone) {
        List<ItemStack> items = new ArrayList<>();
        for(ItemStack item : p.getInventory().getStorageContents()) {
            if(!ItemStackUtils.checkNullorAir(item)) continue;
            if(!clone)
                items.add(item);
            else items.add(item.clone());
        }
        return items;
    }

    public static int getSlotEmptyInventory(PlayerInventory inv) {
        int emptySlot = 0;
        for(ItemStack item : inv.getStorageContents()) {
            if(!ItemStackUtils.checkNullorAir(item)) emptySlot += 1;
        }
        return emptySlot;
    }

}
