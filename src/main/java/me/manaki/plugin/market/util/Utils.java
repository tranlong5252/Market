package me.manaki.plugin.market.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import me.manaki.plugin.market.Market;
import me.manaki.plugin.market.gui.MarketGUI;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class Utils {

    MarketGUI gui = Market.getGui();

    public static double round(double i) {
        DecimalFormat df = new DecimalFormat("#.##");
        String s = df.format(i).replace(",", ".");
        return Double.parseDouble(s);
    }

    public Multimap<Double, String> getTopRawMessage() {
        Map<Integer, String> itemList = gui.getAllItem();
        Map<Integer, Double> priceList = gui.getAllItemPrice();
        Map<Integer, Double> percentList = gui.getAllPercent();
        Map<Integer,Integer> amountList = gui.getItemAmount();
        Multimap<Double, String> message = TreeMultimap.create(Ordering.natural().reverse(), Ordering.natural());
        for (int key : itemList.keySet()) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            String price = df.format(priceList.get(key)/amountList.get(key));
            message.put(percentList.get(key), itemList.get(key)
                    + " " + "§a($§a§l" + price + "§a/cái) §2(" + percentList.get(key) + "%)");
        }
        return message;
    }

    public Multimap<Double, String> getBottomRawMessage() {
        Map<Integer, String> itemList = gui.getAllItem();
        Map<Integer, Double> priceList = gui.getAllItemPrice();
        Map<Integer, Double> percentList = gui.getAllPercent();
        Map<Integer,Integer> amountList = gui.getItemAmount();
        Multimap<Double, String> message = TreeMultimap.create(Ordering.natural(), Ordering.natural().reversed());
        for (int key : itemList.keySet()) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            String price = df.format(priceList.get(key)/amountList.get(key));
            message.put(percentList.get(key), itemList.get(key)
                    + " " + "§c($§c§l" + price + "§c/cái) §4(" + percentList.get(key) + "%)");
        }
        return message;
    }

    public ArrayList<String> getTopMessage() {
        Multimap<Double, String> sortedMap = getTopRawMessage();
        ArrayList<String> message = new ArrayList<>();
        message.add(" ");
        message.add("§f§lBẢNG GIÁ CHỢ §e(/market)");
        message.add("§aTOP NGUYÊN LIỆU GIÁ BÁN TỐT");
        int count = 1;
        int topLimit = 0;
        for (Map.Entry<Double, String> o : sortedMap.entries()) {
            if (topLimit == 5) {
                break;
            }
            message.add(" §7§l" + count + ".§7 " + o.getValue());
            count++;
            topLimit++;
        }
        return message;
    }

    public ArrayList<String> getBottomMessage() {
        Multimap<Double, String> sortedMap = getBottomRawMessage();
        ArrayList<String> message = new ArrayList<>();
        message.add("§cTOP NGUYÊN LIỆU MẤT GIÁ");
        int count = 1;
        int bottomLimit = 0;
        for (Map.Entry<Double, String> o : sortedMap.entries()) {
            if (bottomLimit == 3) {
                break;
            }
            message.add(" §7§l" + count + ".§7 " + o.getValue());
            count++;
            bottomLimit++;
        }
        message.add(" ");
        return message;
    }
}
