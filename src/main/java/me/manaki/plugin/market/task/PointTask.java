package me.manaki.plugin.market.task;

import com.google.common.collect.Maps;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.gui.MarketGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PointTask extends BukkitRunnable {

    @Override
    public void run() {
        // Check
        for (int id : Commodities.lastUpdated.keySet()) {
            if (System.currentTimeMillis() - Commodities.lastUpdated.get(id) >= Market.TIME_CHECK * 1000L) {
                Commodities.lastUpdated.put(id, System.currentTimeMillis());
                Commodities.realPoints.put(id, Commodities.realPoints.get(id) + Market.CHECK_POINT);
            }
        }

        // Save and load
        Commodities.saveAll(Market.get().getDataConfig());
        Commodities.currentPoints = Maps.newHashMap(Commodities.realPoints);

        // Update player GUI
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                var iv = player.getOpenInventory();
                if (!iv.getTitle().equals(MarketGUI.TITLE)) return;
                Bukkit.getScheduler().runTask(Market.get(), () -> {
                    MarketGUI.openGUI(player);
                });
            }
        }
    }

}
