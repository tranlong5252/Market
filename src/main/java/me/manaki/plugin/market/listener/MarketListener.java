package me.manaki.plugin.market.listener;

import me.manaki.plugin.market.gui.MarketGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MarketListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        MarketGUI.eventHandling(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().equals(MarketGUI.TITLE)) e.setCancelled(true);
    }


}
