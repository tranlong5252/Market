package me.manaki.plugin.market.gui;

import com.meowj.langutils.lang.LanguageHelper;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.commodity.Commodity;
import me.manaki.plugin.market.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MarketGUI {
	
	public static String TITLE = "§1§lMARKET";

	public static void openGUI(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE);
		player.openInventory(inv);
		Bukkit.getScheduler().runTaskAsynchronously(Market.get(), () -> {
			for (int slot : Commodities.itemSlots.keySet()) {
				inv.setItem(slot, getItem(slot));
			}
		});
	}
	
	public static ItemStack getItem(int id) { 
		Commodity item = Commodities.itemSlots.get(id);
		ItemStack itemStack = item.cloneModel();
		List<String> lore = new ArrayList<>();
		double percent = Utils.round((double) Commodities.getPoint(id) * 100 / Market.BASE_POINT);
		String PC = "§e" + percent;
		if (percent>100) PC = "§a" + percent;
		if (percent<100) PC = "§c" + percent;
		lore.add("§f§m                    ");
		lore.add("§aClick chuột phải để bán");
		lore.add("§aShift + chuột phải để bán tất cả");
		lore.add("§aSố lượng: §f" + item.getAmount());
		lore.add("§aGiá: §f" + Commodities.getPrice(id) + "$" + " §8(" + PC + "%§8)");
		lore.add("§f§m                    ");
		
		ItemMeta meta = itemStack.getItemMeta();
//		meta.setDisplayName("§e§lx" + item.getAmount() + " " + item.getName());
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(item.getAmount());
		
		return itemStack;
	}

	
	public static void eventHandling(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(TITLE)) return;
		e.setCancelled(true);
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
		if (e.getClickedInventory() == null) return;
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();

		player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
		if (e.isRightClick()) {
			if (e.isShiftClick()) {
				Bukkit.getScheduler().runTask(Market.get(), () -> {
					if (!Commodities.itemSlots.containsKey(slot)) return;
					if (!Commodities.sell(slot, player)) {
						player.sendMessage("§cLỗi: Phải gộp item thành stack mới bán được!");
					} else {
						while (Commodities.sell(slot, player))
						e.getInventory().setItem(slot, getItem(slot));
					}
				});
				return;
			}
			Bukkit.getScheduler().runTask(Market.get(), () -> {
				if (!Commodities.itemSlots.containsKey(slot)) return;
				if (!Commodities.sell(slot, player)) {
					player.sendMessage("§cLỗi: Phải gộp item thành stack mới bán được!");
				} else {
					e.getInventory().setItem(slot, getItem(slot));
				}
			});
		}
	}

	public Map<Integer,Double> getAllPercent() {
		TreeMap<Integer,Double> percentlist = new TreeMap<>();
		for (int id: Commodities.itemSlots.keySet()) {
			double percent = Utils.round((double) Commodities.getPoint(id) * 100 / Market.BASE_POINT);
			percentlist.put(id,percent);
		}
		return percentlist;
	}

	public Map<Integer,String> getAllItem() {
		TreeMap<Integer,String> itemlist = new TreeMap<>();
		for (int id: Commodities.itemSlots.keySet()) {
			String item = LanguageHelper.getMaterialName(getItem(id).getType(), "vi_vn");
			itemlist.put(id,item);
		}
		return itemlist;
	}

	public Map<Integer,Double> getAllItemPrice() {
		TreeMap<Integer,Double> pricelist = new TreeMap<>();
		for (int id: Commodities.itemSlots.keySet()) {
			pricelist.put(id,Commodities.getPrice(id));
		}
		return pricelist;
	}
	public Map<Integer,Integer> getItemAmount() {
		TreeMap<Integer, Integer> amountList = new TreeMap<>();
		for (int id : Commodities.itemSlots.keySet()) {
			amountList.put(id, Commodities.itemSlots.get(id).getAmount());
		}
		return amountList;
	}
}
