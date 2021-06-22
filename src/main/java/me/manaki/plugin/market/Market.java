package me.manaki.plugin.market;

import me.manaki.plugin.market.command.MarketCommand;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.gui.MarketGUI;
import me.manaki.plugin.market.listener.MarketListener;
import me.manaki.plugin.market.task.BroadcastTask;
import me.manaki.plugin.market.task.PointTask;
import me.manaki.plugin.market.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Market extends JavaPlugin{

	private static Economy econ = null;
	
	public static int BASE_POINT = 0;
	public static int CHECK_POINT = 0;
	public static int TIME_CHECK = 0;
	public static int MAX_POINT = 0;
	public static int TIME_BROADCAST = 0;

	static Utils utils;
	static MarketGUI gui;
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		// Config
		gui = new MarketGUI();
		utils = new Utils();
		this.reloadConfig();

		// Listeners
		Bukkit.getPluginManager().registerEvents(new MarketListener(), this);

		// Commands
		this.getCommand("market").setExecutor(new MarketCommand());

		// Tasks
		new PointTask().runTaskTimerAsynchronously(this, 0, TIME_CHECK * 20L);
		new BroadcastTask().runTaskTimerAsynchronously(this, 0, TIME_BROADCAST * 20L);

		// Hook
		hookVault();
	}
	public static MarketGUI getGui() {
		return gui;
	}
	public static Utils getUtils(){
		return utils;
	}

	@Override
	public void onDisable() {
		Commodities.saveAll(getDataConfig());
	}

	@Override
	public void reloadConfig() {
		this.saveDefaultConfig();
		File file = new File(this.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		BASE_POINT = config.getInt("options.base-point");
		CHECK_POINT = config.getInt("options.check-point");
		TIME_CHECK = config.getInt("options.time-check");
		MAX_POINT = config.getInt("options.max-point");
		TIME_BROADCAST = config.getInt("options.broadcast-time");

		Commodities.load(this.config, getDataConfig());
	}

	public FileConfiguration getDataConfig() {
		File file = new File(Market.get().getDataFolder(), "data.yml");
		if (file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveData(FileConfiguration config) {
		File file = new File(this.getDataFolder(), "data.yml");
		try {
			config.save(file);
		} catch (IOException e) {e.printStackTrace();}
	}


	// Hook

    public static Economy getEcononomy() {
    	return econ;
    }
    
	private void hookVault() {
	    if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return;
	     }
	    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	    if (rsp == null) {
	    	return;
	    }
	    econ = rsp.getProvider();
	}
	
    public static Market get() {
		return JavaPlugin.getPlugin(Market.class);
	}


}
