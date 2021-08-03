package me.manaki.plugin.market.gui;

import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommodityGUI {

    public static void open(Player player, Commodity commodity) {
        var inv = Bukkit.createInventory(new CommodityGUIHolder(), 9, "§0§lXEM MẶT HÀNG");
        player.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(Market.get(), () -> {
            for (int i = 0 ; i < inv.getSize() ; i++) inv.setItem(i, getBack());
            inv.setItem(4, commodity.cloneModel());
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof CommodityGUIHolder) e.setCancelled(true);
    }

    public static void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CommodityGUIHolder) {
            var player = (Player) e.getPlayer();
            Bukkit.getScheduler().runTask(Market.get(), () -> {
               MarketGUI.openGUI(player, 1);
            });
        }
    }

    private static ItemStack getBack() {
        var is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        var meta = is.getItemMeta();
        meta.setDisplayName("§l");
        is.setItemMeta(meta);
        return is;
    }

}

class CommodityGUIHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
