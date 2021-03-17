package me.manaki.plugin.market.api;

import me.manaki.plugin.market.Market;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;

public class MoneyAPI {
	
	public static double getMoney(Player player) {
		Economy eco = Market.getEcononomy();
		
		return eco.getBalance(player);
	}

	public static boolean moneyCost(Player player, double money) {
		Economy eco = Market.getEcononomy();
		double moneyOfPlayer = eco.getBalance(player);
		if (moneyOfPlayer < money) {
			return false;
		}
		eco.withdrawPlayer(player, money);
		return true;
		
	}
	
	public static void giveMoney(Player player, double money) {
		Economy eco = Market.getEcononomy();
		eco.depositPlayer(player, money);
	}
	
}
