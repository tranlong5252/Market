package me.manaki.plugin.market.commodity;

import me.manaki.plugin.market.api.ShopsAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum CommodityType {

    DEFAULT {
        @Override
        public ItemStack get(String id) {
            return new ItemStack(Material.valueOf(id.toUpperCase()));
        }
    },

    SHOPS {
        @Override
        public ItemStack get(String id) {
            return ShopsAPI.get(id);
        }
    };

    CommodityType() {}

    public abstract ItemStack get(String id);

}
