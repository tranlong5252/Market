package me.manaki.plugin.market.commodity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.api.MoneyAPI;
import me.manaki.plugin.market.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Commodities {
	
	public static Map<Integer, Commodity> itemSlots = new HashMap<Integer, Commodity> ();
	public static Map<Integer, Integer> realPoints = new HashMap<Integer, Integer> ();

	public static Map<Integer, Long> lastUpdated = new HashMap<Integer, Long> ();
	public static Map<Integer, Integer> currentPoints = new HashMap<Integer, Integer> ();

	public static void load(FileConfiguration config, FileConfiguration dataConfig) {
		itemSlots.clear();
		realPoints.clear();
		for (String s : config.getConfigurationSection("items").getKeys(false)) {
			int slot = Integer.parseInt(s);
			String material = config.getString("items." + s + ".material");
			int amount = config.getInt("items." + s + ".amount");
			double baseValue = config.getDouble("items." + s + ".base-value");
			Commodity item = null;

			// Get commodity
			String a[] = material.split(" ");
			if (a.length <= 1) item = new Commodity(material, CommodityType.DEFAULT, amount, baseValue);
			else item = new Commodity(a[1], CommodityType.valueOf(a[0].toUpperCase()), amount, baseValue);


			int point = Market.BASE_POINT;
			long last = System.currentTimeMillis();
			
			if (dataConfig.contains("points." + s)) {
				point = dataConfig.getInt("points." + s + ".value");
				last = dataConfig.getLong("points." + s + ".last-updated");
				if (point > Market.MAX_POINT) point = Market.MAX_POINT;
			}
			
			itemSlots.put(slot, item);
			lastUpdated.put(slot, last);

			realPoints.put(slot, point);
			currentPoints.put(slot, point);
		}
	}
	
	public static double getPrice(int itemId) {
		return Utils.round(((double) getPoint(itemId) / Market.BASE_POINT) * itemSlots.get(itemId).getBaseValue());
	}
	
	public static int getPoint(int itemId) {
		int point = currentPoints.get(itemId);
		if (point > Market.MAX_POINT) point = Market.MAX_POINT;
		return point;
	}
	
	public static boolean sell(int itemId, Player player) {
		Commodity marketCommodity = itemSlots.get(itemId);
		
		// Check inv
		PlayerInventory inv = player.getInventory();
		ItemStack[] items = inv.getContents();
		boolean has = false;
		for (int i = 0 ; i < items.length ; i ++) {
			ItemStack item = items[i];
			if (item != null) {
				if (marketCommodity.is(item)) {
					int amount = marketCommodity.getAmount();
					if (item.getAmount() == amount) {
						items[i] = null;
						has = true;
						break;
					}
					else if (item.getAmount() > amount) {
						item.setAmount(item.getAmount() - amount);
						has = true;
						break;
					}
					else if (item.getAmount() < amount) continue;
				}
			}
		}
		if (!has) return false;
		inv.setContents(items);
		
		// Subtract point
		int point = realPoints.get(itemId);
		if (point == 0) return true;
		point--;
		realPoints.put(itemId, point);
		lastUpdated.put(itemId, System.currentTimeMillis());
		
		// Add money
		MoneyAPI.giveMoney(player, getPrice(itemId));
		player.sendMessage("§aĐã bán §f" + "x" + marketCommodity.getAmount() + " " + marketCommodity.getName() + " §anhận " + getPrice(itemId) + "$");
		
		return true;
	}
	
	private static void savePoint(int itemId, FileConfiguration config) {
		int pointValue = realPoints.get(itemId);
		config.set("points." + itemId + ".value", pointValue);
		config.set("points." + itemId + ".last-updated", lastUpdated.get(itemId));
	}
	
	public static void saveAll(FileConfiguration config) {
		for (int i : itemSlots.keySet())  {
			savePoint(i, config);
		}
		Market.get().saveData(config);
	}
	
}
