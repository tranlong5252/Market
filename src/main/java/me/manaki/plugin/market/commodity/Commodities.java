package me.manaki.plugin.market.commodity;

import com.google.common.collect.Maps;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.api.MoneyAPI;
import me.manaki.plugin.market.event.PlayerMarketSellEvent;
import me.manaki.plugin.market.gui.MarketGUI;
import me.manaki.plugin.market.player.MarketPlayers;
import me.manaki.plugin.market.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class Commodities {
	
	public static Map<Integer, Commodity> itemSlots = new HashMap<Integer, Commodity> ();
	public static Map<Integer, Integer> realPoints = new HashMap<Integer, Integer> ();

	public static Map<Integer, Long> lastUpdated = new HashMap<Integer, Long> ();
	public static Map<Integer, Integer> currentPoints = new HashMap<Integer, Integer> ();

	public static Map<String, Double> earned = Maps.newConcurrentMap();

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

	public static boolean sell(int itemId, Player player, boolean all) {
		Commodity commodity = itemSlots.get(itemId);
		var inv = player.getInventory();
		var mp = MarketPlayers.get(player.getName());

		double maxEarn = MarketGUI.SELL_LIMIT - mp.getSum();
		double earnedMoney = mp.getSum();
		double price = getPrice(itemId);

		int count = 0;
		for (ItemStack is : inv.getContents()) {
			if (is == null) continue;
			if (commodity.is(is)) count += is.getAmount();
		}

		// Check
		int times = !all ? (int) Math.min(count < commodity.getAmount() ? 0 : 1, (int) maxEarn / price) : Math.min(count / commodity.getAmount(), Double.valueOf(maxEarn / price).intValue());
		if (times <= 0) {
			player.sendMessage("§cKhông đủ số lượng hoặc đã bán chạm mức tối đa của ngày! (Hôm nay có §f" + Utils.round(earnedMoney) + "$)");
			return false;
		}
		int amount = times * commodity.getAmount();
		int removed = 0;

		// Remove
		for (ItemStack is : inv.getContents()) {
			if (!mp.canAdd()) break;
			if (removed >= amount) break;
			if (is == null) continue;
			if (commodity.is(is)) {
				int amountToRemove = Math.min(amount - removed, is.getAmount());
				is.setAmount(is.getAmount() - amountToRemove);
				removed += amountToRemove;
			}
		}

		// Data
		int point = realPoints.get(itemId);
		if (point != 0) {
			point -= times;
			realPoints.put(itemId, point);
			lastUpdated.put(itemId, System.currentTimeMillis());
		}

		double earn = times * price;
		mp.add(earn);
		mp.save();
		MoneyAPI.giveMoney(player, earn);

		player.sendMessage("§aBán §fx" + amount + " " + commodity.getName() + " §anhận §f" + Utils.round(earn) + "$ §a(Hôm nay có §f" + Utils.round(mp.getSum()) + "$§a)");
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

		// Cache
		earned.put(player.getName(), earned.getOrDefault(player.getName(), 0d) + earn);

		// Event
		Bukkit.getPluginManager().callEvent(new PlayerMarketSellEvent(player, commodity.getModel().clone(), commodity.getAmount()));

		return true;
	}

//	public static boolean sell(int itemId, Player player) {
//		// Check limit
//		var mp = MarketPlayers.get(player.getName());
//		if (!mp.canAdd()) {
//			player.sendMessage("§cBạn đã bán chạm mức tối đa là §f§l" + MarketGUI.SELL_LIMIT + "$");
//			return false;
//		}
//
//		Commodity marketCommodity = itemSlots.get(itemId);
//
//		// Check inv
//		PlayerInventory inv = player.getInventory();
//		ItemStack[] items = inv.getContents();
//		boolean has = false;
//		for (int i = 0 ; i < items.length ; i ++) {
//			ItemStack item = items[i];
//			if (item != null) {
//				if (marketCommodity.is(item)) {
//					int amount = marketCommodity.getAmount();
//					if (item.getAmount() == amount) {
//						items[i] = null;
//						has = true;
//						break;
//					}
//					else if (item.getAmount() > amount) {
//						item.setAmount(item.getAmount() - amount);
//						has = true;
//						break;
//					}
//					else if (item.getAmount() < amount) continue;
//				}
//			}
//		}
//		if (!has) return false;
//		inv.setContents(items);
//
//		// Subtract point
//		int point = realPoints.get(itemId);
//		if (point == 0) return true;
//		point--;
//		realPoints.put(itemId, point);
//		lastUpdated.put(itemId, System.currentTimeMillis());
//
//		// Add money
//		double price = getPrice(itemId);
//		MoneyAPI.giveMoney(player, price);
//
//		// Data
//		mp.add(price);
//		mp.save();
//
//		// Cache
//		earned.put(player.getName(), earned.getOrDefault(player.getName(), 0d) + price);
//
//		player.sendMessage("§aĐã bán §f" + "x" + marketCommodity.getAmount() + " " + marketCommodity.getName() + " §anhận " + getPrice(itemId) + "$");
//
//		// Event
//		var is = marketCommodity.getModel().clone();
//		int amount = marketCommodity.getAmount();
//		Bukkit.getPluginManager().callEvent(new PlayerMarketSellEvent(player, is, amount));
//
//		return true;
//	}
	
	private static void savePoint(int itemId, FileConfiguration config) {
		int pointValue = getPoint(itemId);
		config.set("points." + itemId + ".value", pointValue);
		config.set("points." + itemId + ".last-updated", lastUpdated.get(itemId));
	}
	
	public static void saveAll(FileConfiguration config) {
		for (int i : itemSlots.keySet())  {
			savePoint(i, config);
		}
		Market.get().saveData(config);
	}

	public static int getHighestSlot() {
		int max = 0;
		for (Integer i : itemSlots.keySet()) {
			max = Math.max(i, max);
		}
		return max;
	}

	public static Map<String, Double> calTop10Sold() {
		Map<String, Double> result = Maps.newLinkedHashMap();

		List<Double> values = new ArrayList<> (earned.values());
		Collections.sort(values, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return -1 * o1.compareTo(o2);
			}
		});

		for (int i = 0 ; i < Math.min(values.size(), 10) ; i++) {
			double value = values.get(i);
			for (Map.Entry<String, Double> e : earned.entrySet()) {
				if (e.getValue() == value) {
					result.put(e.getKey(), value);
				}
			}
		}

		return result;
	}
	
}
