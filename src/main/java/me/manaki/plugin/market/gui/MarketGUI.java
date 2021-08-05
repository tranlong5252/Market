package me.manaki.plugin.market.gui;

import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.commodity.Commodities;
import me.manaki.plugin.market.commodity.Commodity;
import me.manaki.plugin.market.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MarketGUI {
	
	public static String TITLE = "§0§lCHỢ THƯƠNG LÁI (MARKET)";

	private final static int PRE_BUTTON = 45;
	private final static int NEXT_BUTTON = 53;

	public static int SELL_LIMIT = 70000;

	public static void openGUI(Player player, int page) {
		Inventory inv = Bukkit.createInventory(new MGUIHolder(page), 54, TITLE + " - Trang " + page);
		player.openInventory(inv);
		Bukkit.getScheduler().runTaskAsynchronously(Market.get(), () -> {
			int min = (page - 1) * 45;
			int max = Math.min((page - 1) * 45 + 44, Commodities.getHighestSlot());

			for (int i = 0 ; i <= max - min ; i++) {
				int id = min + i;
				var item = Commodities.itemSlots.getOrDefault(id, null);
				if (item != null) {
					inv.setItem(i, getItem(id));
				}
			}

			for (int i = 45 ; i < 54 ; i++) inv.setItem(i, getBackButton());
			inv.setItem(49, getTutItem());
			inv.setItem(PRE_BUTTON, getPreviosButton());
			inv.setItem(NEXT_BUTTON, getNextButton());
		});
	}

	public static ItemStack getItem(int id) {
		Commodity item = Commodities.itemSlots.get(id);
		ItemStack itemStack = item.cloneModel();
		List<String> lore = new ArrayList<String> ();
		double percent = Utils.round((double) Commodities.getPoint(id) * 100 / Market.BASE_POINT);

		lore.add("§f§m                    ");
		lore.add("§aClick chuột trái để xem");
		lore.add("§aClick chuột phải để bán");
		lore.add("§aSố lượng: §f" + item.getAmount());
		lore.add("§aGiá: §f" + Commodities.getPrice(id) + "$" + " §8(" + percent + "%)");
		lore.add("§f§m                    ");

		ItemMeta meta = itemStack.getItemMeta();
//		meta.setDisplayName("§e§lx" + item.getAmount() + " " + item.getName());
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(item.getAmount());

		return itemStack;
	}

	public static ItemStack getTutItem() {
		ItemStack item = new ItemStack(Material.GLASS_PANE);
		var meta = item.getItemMeta();
		meta.setCustomModelData(25);

		List<String> lore = new ArrayList<String> ();
		lore.add("§fHàng càng nhiều người bán thì càng rẻ và ngược lại");
		lore.add("§fSố % sau giá hàng là tỷ lệ giữa giá hiện tại và giá gốc");
		lore.add("§fMột ngày giới hạn bán được §l" + SELL_LIMIT + "$§f");

		meta.setDisplayName("§6§lHướng dẫn");
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getNextButton() {
		ItemStack item = new ItemStack(Material.GLASS_PANE);
		var meta = item.getItemMeta();
		meta.setCustomModelData(22);
		meta.setDisplayName("§6§lTrang sau >>");
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getPreviosButton() {
		ItemStack item = new ItemStack(Material.GLASS_PANE);
		var meta = item.getItemMeta();
		meta.setCustomModelData(23);
		meta.setDisplayName("§6§l<< Trang trước");
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getBackButton() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		var meta = item.getItemMeta();
		meta.setDisplayName("§6");
		item.setItemMeta(meta);

		return item;
	}
	
	public static void eventHandling(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof MGUIHolder)) return;
		e.setCancelled(true);
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
		if (e.getClickedInventory() == null) return;
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();

		player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);

		var holder = (MGUIHolder) e.getInventory().getHolder();
		int page = holder.getPage();
		// Next, pre
		if (slot == NEXT_BUTTON) {
			int max = Commodities.getHighestSlot();
			int maxPage = max % 45 == 0 ? max / 45 : max / 45 + 1;
			if (page >= maxPage) return;
			openGUI(player, page + 1);
			return;
		}
		else if (slot == PRE_BUTTON) {
			if (page <= 1) return;
			openGUI(player, page - 1);
			return;
		}

		int itemSlot = (page - 1) * 45 + slot;
		if (e.getClick() == ClickType.RIGHT) {
			Bukkit.getScheduler().runTask(Market.get(), () -> {
				if (!Commodities.itemSlots.containsKey(itemSlot)) return;
				if (!Commodities.sell(itemSlot, player)) {
					player.sendMessage("§cXảy ra lỗi, không bán được");
					player.sendMessage("§cPhải gộp item thành stack mới bán được!");
					return;
				} else {
					e.getInventory().setItem(slot, getItem(itemSlot));
				}
			});
		}
		else if (e.getClick() == ClickType.LEFT) {
			if (!Commodities.itemSlots.containsKey(itemSlot)) return;
			CommodityGUI.open(player, Commodities.itemSlots.get(itemSlot));
		}

		
	}

}
