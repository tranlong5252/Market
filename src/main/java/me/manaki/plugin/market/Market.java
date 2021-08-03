package me.manaki.plugin.market;

import com.google.common.collect.Maps;
import me.manaki.plugin.market.gui.MarketGUI;
import me.manaki.plugin.market.listener.MarketListener;
import me.manaki.plugin.market.command.MarketCommand;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.task.PointTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Market extends JavaPlugin{

	private static Economy econ = null;
	
	public static int BASE_POINT = 0;
	public static int CHECK_POINT = 0;
	public static int TIME_CHECK = 0;
	public static int MAX_POINT = 0;

	private final static Map<Material, String> trans = Maps.newHashMap();
	
	public FileConfiguration config;
	
	@Override
	public void onEnable() {
		// Config
		this.reloadConfig();

		// Listeners
		Bukkit.getPluginManager().registerEvents(new MarketListener(), this);

		// Commands
		this.getCommand("market").setExecutor(new MarketCommand());

		// Tasks
		new PointTask().runTaskTimerAsynchronously(this, 0, TIME_CHECK * 20);

		// Hook
		hookVault();
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

		MarketGUI.SELL_LIMIT = config.getInt("sell-limit", 100000);

		trans.clear();
		for (String m : config.getConfigurationSection("trans").getKeys(false)) {
			trans.put(Material.valueOf(m.toUpperCase()), config.getString("trans." + m));
		}

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
		} catch (IOException e) {}
	}

	// Methods

	public static String trans(Material m) {
		if (trans.containsKey(m)) return trans.get(m) + " (Mặc định)";
		return m.name();
	}


	// Hook

    public static Economy getEcononomy() {
    	return econ;
    }
    
	private boolean hookVault() {
	    if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return false;
	     }
	    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	    if (rsp == null) {
	    	return false;
	    }
	    econ = rsp.getProvider();
	    return econ != null;
	}
	
    public static Market get() {
		return JavaPlugin.getPlugin(Market.class);
	}


}
