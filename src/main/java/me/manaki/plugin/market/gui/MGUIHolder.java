package me.manaki.plugin.market.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MGUIHolder implements InventoryHolder {

    private int page;

    public MGUIHolder(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}
