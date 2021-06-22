package me.manaki.plugin.market.commodity;

import org.bukkit.inventory.ItemStack;

public class Commodity {

    private final String id;
    private final CommodityType type;
    private final int amount;
    private final double baseValue;

    private final ItemStack model;

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
