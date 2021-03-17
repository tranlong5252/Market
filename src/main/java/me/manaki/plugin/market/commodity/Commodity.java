package me.manaki.plugin.market.commodity;

import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.gui.MarketGUI;
import org.bukkit.inventory.ItemStack;

public class Commodity {
	
	private String id;
	private CommodityType type;
	private int amount;
	private double baseValue;

	private ItemStack model;
	
	public Commodity(String id, CommodityType type, int amount, double baseValue) {
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.baseValue = baseValue;

		this.model = type.get(id);
	}

	public boolean is(ItemStack is) {
		return model.isSimilar(is);
	}

	public ItemStack cloneModel() {
		return model.clone();
	}

	public String getName() {
		if (model.hasItemMeta() && model.getItemMeta().hasDisplayName()) return model.getItemMeta().getDisplayName();
		return Market.trans(model.getType());
	}

	public String getID() {
		return id;
	}

	public CommodityType getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public double getBaseValue() {
		return baseValue;
	}

	public ItemStack getModel() {
		return model;
	}
}
