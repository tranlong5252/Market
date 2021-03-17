package me.manaki.plugin.market.command;

import me.manaki.plugin.market.Market;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.manaki.plugin.market.gui.MarketGUI;

public class MarketCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if (args.length == 1 && sender.hasPermission("market.admin")) {
			if (args[0].equals("reload")) {
				Market.get().reloadConfig();
				sender.sendMessage("§aAll reloaded");
				return false;
			}
		}
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			MarketGUI.openGUI(player);
		} else sender.sendMessage("Ingame command!");
		
		
		
		return false;
	}

}
