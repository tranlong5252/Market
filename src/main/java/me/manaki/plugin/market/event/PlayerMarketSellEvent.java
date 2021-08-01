package me.manaki.plugin.market.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerMarketSellEvent extends PlayerEvent {

    private final ItemStack is;
    private final int amount;

    public PlayerMarketSellEvent(@NotNull Player who, ItemStack is, int amount) {
        super(who);
        this.is = is;
        this.amount = amount;
    }

    public ItemStack getItemStack() {
        return is;
    }

    public int getAmount() {
        return amount;
    }

    // Required

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
