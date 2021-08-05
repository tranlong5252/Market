package me.manaki.plugin.market.player;

import com.google.gson.GsonBuilder;
import mk.plugin.playerdata.storage.PlayerDataAPI;

public class MarketPlayers {

    private static final String HOOK = "market";
    private static final String KEY = "market-player";

    public static MarketPlayer get(String name) {
        var pd = PlayerDataAPI.get(name, HOOK);
        if (pd.hasData(KEY)) {
            return new GsonBuilder().create().fromJson(pd.getValue(KEY), MarketPlayer.class);
        }
        return new MarketPlayer(name);
    }

    public static void save(MarketPlayer mp) {
        var pd = PlayerDataAPI.get(mp.getName(), HOOK);
        pd.set(KEY, new GsonBuilder().create().toJson(mp));
    }

}
