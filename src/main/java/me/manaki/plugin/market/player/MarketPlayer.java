package me.manaki.plugin.market.player;

import me.manaki.plugin.market.gui.MarketGUI;

import java.time.LocalDate;

public class MarketPlayer {

    private String name;
    private int day;
    private double sum;

    public MarketPlayer(String name) {
        this.name = name;
        this.day = LocalDate.now().getDayOfMonth();
        this.sum = 0;
    }

    public MarketPlayer(String name, int day, double sum) {
        this.name = name;
        this.day = day;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return day;
    }

    public double getSum() {
        return sum;
    }

    public boolean canAdd() {
        return this.day != LocalDate.now().getDayOfMonth() || this.sum < MarketGUI.SELL_LIMIT;
    }

    public void add(double value) {
        if (day != LocalDate.now().getDayOfMonth()) {
            this.day = LocalDate.now().getDayOfMonth();
            this.sum = 0;
        }
        this.sum += value;
    }

    public void save() {
        MarketPlayers.save(this);
    }
}
