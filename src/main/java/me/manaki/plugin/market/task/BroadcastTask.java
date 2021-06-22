package me.manaki.plugin.market.task;

import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastTask extends BukkitRunnable {

    Utils utils = Market.getUtils();

    @Override
    public void run() {
        utils.getTopMessage().forEach(Bukkit::broadcastMessage);
        utils.getBottomMessage().forEach(Bukkit::broadcastMessage);
    }
}
