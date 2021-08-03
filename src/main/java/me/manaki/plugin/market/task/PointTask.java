package me.manaki.plugin.market.task;

import com.google.common.collect.Maps;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.gui.MGUIHolder;
import me.manaki.plugin.market.gui.MarketGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class PointTask extends BukkitRunnable {

    private long lastAnnounce = System.currentTimeMillis();
    private final long PERIOD = 300000;

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
            player.getOpenInventory();
            var iv = player.getOpenInventory();
            Bukkit.getScheduler().runTask(Market.get(), () -> {
                var inv = player.getOpenInventory().getTopInventory();
                if (inv.getHolder() instanceof MGUIHolder) {
                    var holder = (MGUIHolder) inv.getHolder();
                    MarketGUI.openGUI(player, holder.getPage());
                }
            });
        }

        // Sell top
        if (System.currentTimeMillis() - lastAnnounce >= PERIOD) {
            lastAnnounce = System.currentTimeMillis();

            // Cal top
            var top = Commodities.calTop10Sold();
            if (top.size() == 0) return;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§f-------------------------");
                p.sendMessage("§e§lTOP Thương lái hôm nay");
                int c = 0;
                for (Map.Entry<String, Double> e : top.entrySet()) {
                    c++;
                    var k = e.getKey();
                    var v = e.getValue();
                    p.sendMessage("§6§l#" + c + ". §a" + k + ": §f" + v + "$");
                }
                p.sendMessage("§f-------------------------");
            }
        }
    }

}
