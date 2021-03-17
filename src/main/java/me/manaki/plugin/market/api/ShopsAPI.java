package me.manaki.plugin.market.api;

import me.manaki.plugin.shops.storage.ItemStorage;
import org.bukkit.inventory.ItemStack;

public class ShopsAPI {

    public static ItemStack get(String id) {
        return ItemStorage.get(id);
    }

}
