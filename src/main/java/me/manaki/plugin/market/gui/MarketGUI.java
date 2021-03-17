package me.manaki.plugin.market.gui;

import java.util.ArrayList;
import java.util.List;

import me.manaki.plugin.market.util.Utils;
import me.manaki.plugin.market.commodity.Commodity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodities;

public class MarketGUI {
	
	public static String TITLE = "§0§lCHỢ THƯƠNG LÁI (MARKET)";
	
	public static void openGUI(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE);
		player.openInventory(inv);
		Bukkit.getScheduler().runTaskAsynchronously(Market.get(), () -> {
			for (int slot : Commodities.itemSlots.keySet()) {
				inv.setItem(slot, getItem(slot));
			}
			inv.setItem(49, getTutItem());
		});
	}
	
	public static ItemStack getItem(int id) { 
		Commodity item = Commodities.itemSlots.get(id);
		ItemStack itemStack = item.cloneModel();
		List<String> lore = new ArrayList<String> ();
		double percent = Utils.round((double) Commodities.getPoint(id) * 100 / Market.BASE_POINT);

		lore.add("§f§m                    ");
		lore.add("§aClick chuột phải để bán");
		lore.add("§aSố lượng: §f" + item.getAmount());
		lore.add("§aGiá: §f" + Commodities.getPrice(id) + "$" + " §8(" + percent + "%)");
		lore.add("§f§m                    ");
		
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("§e§lx" + item.getAmount() + " " + item.getName());
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		
		return itemStack;
	}
	
	public static ItemStack getTutItem() {
		ItemStack item = new ItemStack(Material.BOOK);
		List<String> lore = new ArrayList<String> ();
		lore.add("§f§oHàng càng nhiều người bán thì càng rẻ và ngược lại");
		lore.add("§f§oSố % sau giá hàng là tỷ lệ giữa giá hiện tại và giá gốc");
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§lHướng dẫn");
		meta.setLore(lore);
		item.setItemMeta(meta); 
		
		return item;
	}
	
	public static void eventHandling(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(TITLE)) return;
		e.setCancelled(true);
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
		if (e.getClickedInventory() == null) return;
		if (e.getClick() != ClickType.RIGHT) return;
		
		Bukkit.getScheduler().runTask(Market.get(), () -> {
			Player player = (Player) e.getWhoClicked();
			int slot = e.getSlot();
			if (!Commodities.itemSlots.containsKey(slot)) return;
			if (!Commodities.sell(slot, player)) {
				player.sendMessage("§cXảy ra lỗi, không bán được");
				player.sendMessage("§cPhải gộp item thành stack mới bán được!");
				return;
			} else {
				e.getInventory().setItem(slot, getItem(slot));
			}	
			player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
		});
		
	}
	
	public static double secondToHour(int seconds) {
		return (double) new Double(seconds / 3600 ).intValue();
	}
	
	
	
	
	
	
	
	
}
