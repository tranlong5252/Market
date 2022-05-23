package me.manaki.plugin.market.util;

import com.google.gson.Gson;
import me.manaki.plugin.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemStackUtils {

    public static Gson gson = new Gson();
    static Market main = Market.get();

    public static void setUnbreakable(ItemStack item, boolean enable) {
        if (!checkNullorAir(item)) return;
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(enable);
        item.setItemMeta(meta);
    }

    public static void dropItems(List<ItemStack> items, Location loc) {
        for (ItemStack item : items) {
            loc.getWorld().dropItemNaturally(loc, item);
        }
    }

    public static boolean isVanilla(ItemStack item) {
        if (item.hasItemMeta()) return false;
        return !item.hasItemMeta() || !item.getItemMeta().hasDisplayName();
    }

    public static String getDisplayName(ItemStack item) {
        return item.getItemMeta().getDisplayName();
    }

    public static void insertLore(List<String> currentLore, List<String> lore, int line) {
        int j = 0;
        for (int i = line; i <= lore.size() - 1; i++) {
            String currentLine = currentLore.get(i);
            currentLine += lore.get(j);
            currentLore.set(i, currentLine);
            j++;
        }
    }

    public static boolean hasFlag(ItemStack item, ItemFlag flag) {
        return item.hasItemMeta() && item.getItemMeta().hasItemFlag(flag);
    }

    public static boolean hasEnchant(ItemStack item, String name) {
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            for (Enchantment enchant : item.getItemMeta().getEnchants().keySet()) {
                if (enchant.getName().equals(name)) return true;
            }
        }
        return false;
    }

    public static boolean hasDisplayName(ItemStack item) {
        if (!checkNullorAir(item)) return false;
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName();
    }

    public static String listStringToJson(List<String> list) {
        return gson.toJson(list);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String> jsonToListString(String json) {
        return gson.fromJson(json, ArrayList.class);
    }

    public static String getJsonFromLore(List<String> lore) {
        return gson.toJson(lore);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String> getLoreFromJson(String s) {
        return gson.fromJson(s, new ArrayList<String>().getClass());
    }

    public static List<String> getLore(ItemStack item) {
        if (!checkNullorAir(item)) return new ArrayList<>();
        if (!item.hasItemMeta()) return new ArrayList<>();
        if (!item.getItemMeta().hasLore()) return new ArrayList<>();
        return item.getItemMeta().getLore();
    }

    public static void addVanillaEnchant(Enchantment type, int level, ItemStack item) {
        if (!checkNullorAir(item)) return;
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(type, level, true);
        item.setItemMeta(meta);
    }

    public static int getEnchantment(Enchantment type, ItemStack item) {
        if (!checkNullorAir(item)) return 0;
        if (!item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchant(type)) {
            return 0;
        } else {
            return meta.getEnchantLevel(type);
        }
    }

    public static int getSlotEmptyInventory(PlayerInventory inv) {
        int emptySlot = 0;
        for (ItemStack item : inv.getStorageContents()) {
            if (!checkNullorAir(item)) emptySlot += 1;
        }
        return emptySlot;
    }

    public static int getAmountItemInInventory(PlayerInventory inv, ItemStack itemReduce) {
        ItemStack itemReduceClone = itemReduce.clone();
        itemReduceClone.setAmount(1);
        String dataReduce = ItemStackUtils.toBase64(itemReduceClone);
        int currentAmount = 0;
        for (int i = 0; i <= 35; i++) {
            ItemStack item = inv.getItem(i);
            if (!checkNullorAir(item)) continue;
            ItemStack itemClone = item.clone();
            itemClone.setAmount(1);
            String data = toBase64(itemClone);
            if (data.equals(dataReduce)) {
                currentAmount += item.getAmount();
            }
        }
        return currentAmount;
    }

    public static boolean reduceItemInInventory(PlayerInventory inv, ItemStack itemReduce, int amount) {
        List<Integer> slots = new ArrayList<>();
        ItemStack itemReduceClone = itemReduce.clone();
        itemReduceClone.setAmount(1);
        String dataReduce = ItemStackUtils.toBase64(itemReduceClone);
        int currentAmount = 0;
        for (int i = 0; i <= 35; i++) {
            if (currentAmount >= amount) break;
            ItemStack item = inv.getItem(i);
            if (!checkNullorAir(item)) continue;
            ItemStack itemClone = item.clone();
            itemClone.setAmount(1);
            String data = toBase64(itemClone);
            if (data.equals(dataReduce)) {
                slots.add(i);
                currentAmount += item.getAmount();
            }
        }
        if (currentAmount < amount) return false;
        Bukkit.getScheduler().runTask(main, () -> clearItemBySlots(slots, inv, amount));
        return true;
    }

    public static void clearItemBySlots(List<Integer> slots, PlayerInventory inv, int require) {
        for (int i : slots) {
            if (!ItemStackUtils.checkNullorAir(inv.getItem(i))) continue;
            ItemStack item = inv.getItem(i).clone();
            if (require - item.getAmount() > 0) {
                require -= item.getAmount();
                inv.setItem(i, new ItemStack(Material.AIR));
            } else {
                item.setAmount(item.getAmount() - require);
                inv.setItem(i, item);
            }
        }
    }

    public static List<String> cutLine(String s, String chatcolor, boolean convertChatColor) {
        if (convertChatColor) s = s.replaceAll("&", "§");
        List<String> strings = new ArrayList<>();
        if (s.length() < 20) {
            strings.add(chatcolor + s);
            return strings;
        } else {
            int i = 0;
            int j = 0;
            StringBuilder add = new StringBuilder();
            for (String str : s.split(" ")) {
                if (i >= 9) {
                    strings.add(chatcolor + add);
                    add = new StringBuilder();
                    i = 0;
                }
                add.append(str).append(" ");
                i++;
                j++;
                if (j >= s.split(" ").length) {
                    strings.add(chatcolor + add);
                }
            }
        }
        return strings;
    }

    public static List<ItemStack> toItemStacks(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        if (strings.isEmpty()) return items;
        for (String s : strings) {
            items.add(ItemStackUtils.toItemStack(s));
        }
        return items;
    }

    public static List<String> toStringItems(List<ItemStack> items) {
        List<String> strings = new ArrayList<>();
        if (items.isEmpty()) return strings;
        for (ItemStack item : items) {
            if (item != null && !item.getType().equals(Material.AIR))
                strings.add(ItemStackUtils.toBase64(item));
        }
        return strings;
    }

    public static void reduceItemInHand(ItemStack item, PlayerInventory inv) {
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
        else inv.setItemInMainHand(new ItemStack(Material.AIR));
    }

    public static void removeItemInHand(PlayerInventory inv) {
        inv.setItemInMainHand(new ItemStack(Material.AIR));
    }

    public static ItemStack createDec() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material type, String name) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemStack(Material type, int data) {
        ItemStack item = new ItemStack(type);
        item.setDurability((short) data);
        return item;
    }

    public static void setDisplayName(ItemStack item, String displayName) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
    }

    public static void setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static void addLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore1 = new ArrayList<>();
        if (meta.hasLore()) lore1.addAll(meta.getLore());
        lore1.addAll(lore);
        meta.setLore(lore1);
        item.setItemMeta(meta);
    }

    public static boolean checkNullorAir(ItemStack item) {
        return item != null && !item.getType().equals(Material.AIR);
    }

    public static void addItemFlag(ItemStack item, ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flag);
        item.setItemMeta(meta);
    }

    public static boolean compareItem(ItemStack item1, ItemStack item2, boolean ignoreAmount) {
        if (ignoreAmount) {
            item1 = item1.clone();
            item2 = item2.clone();
            item1.setAmount(1);
            item2.setAmount(1);
        }
        return toBase64(item1).equals(toBase64(item2));
    }

    public static String toBase64(Inventory inv) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput;
        try {
            dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(inv);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }
//	public static String toJsonString(ItemStack item) {
//		return gson.toJson(item);
//	}
//
//	public static ItemStack formJsonString(String string) {
//		return gson.fromJson(string, ItemStack.class);
//	}

    public static String toBase64(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput;
        try {
            dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static ItemStack toItemStack(String data) {
        if (data == null || data.isEmpty()) return null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = null;
        try {
            dataInput = new BukkitObjectInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ItemStack item = null;
        try {
            if (dataInput != null) {
                item = (ItemStack) dataInput.readObject();
                dataInput.close();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static void removeItem(Player p, List<ItemStack> requirement) {
        List<ItemStack> itemsInv = PlayerUtils.getInventoryStorage(p, false);
        Iterator<ItemStack> iteRequirement = requirement.iterator();
        require:
        while (iteRequirement.hasNext()) {
            ItemStack itemRequire = iteRequirement.next();
            Iterator<ItemStack> iteItemInv = itemsInv.iterator();
            while (iteItemInv.hasNext()) {
                ItemStack itemInv = iteItemInv.next();
                if (ItemStackUtils.compareItem(itemRequire, itemInv, true)) {
                    if (itemRequire.getAmount() > itemInv.getAmount()) {
                        itemRequire.setAmount(itemRequire.getAmount() - itemInv.getAmount());
                        itemInv.setAmount(0);
                        itemInv.setType(Material.AIR);
                        iteItemInv.remove();
                    } else {
                        itemInv.setAmount(itemInv.getAmount() - itemRequire.getAmount());
                        if (itemInv.getAmount() == 0) {
                            itemInv.setType(Material.AIR);
                            iteItemInv.remove();
                        }
                        iteRequirement.remove();
                        continue require;
                    }
                }
            }
        }
    }

    public static boolean isPlayerHasEnoughItem(Player p, List<ItemStack> requirement) {
        List<ItemStack> itemsInv = PlayerUtils.getInventoryStorage(p, true);
        Iterator<ItemStack> iteRequirement = requirement.iterator();
        require:
        while (iteRequirement.hasNext()) {
            ItemStack itemRequire = iteRequirement.next();
            Iterator<ItemStack> iteItemInv = itemsInv.iterator();
            while (iteItemInv.hasNext()) {
                ItemStack itemInv = iteItemInv.next();
                if (ItemStackUtils.compareItem(itemRequire, itemInv, true)) {
                    if (itemRequire.getAmount() > itemInv.getAmount()) {
                        itemRequire.setAmount(itemRequire.getAmount() - itemInv.getAmount());
                        itemInv.setAmount(0);
                        itemInv.setType(Material.AIR);
                        iteItemInv.remove();
                    } else {
                        itemInv.setAmount(itemInv.getAmount() - itemRequire.getAmount());
                        if (itemInv.getAmount() == 0) {
                            itemInv.setType(Material.AIR);
                            iteItemInv.remove();
                        }
                        iteRequirement.remove();
                        continue require;
                    }
                }
            }
        }
        return requirement.isEmpty();
    }

    public static void setGlow(ItemStack item) {
        addVanillaEnchant(Enchantment.DURABILITY, 1, item);
        addItemFlag(item, ItemFlag.HIDE_ENCHANTS);
    }

    @SuppressWarnings("deprecation")
    public static void setSkull(ItemStack item, String name) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(name);
        item.setItemMeta(meta);
    }

    public List<ItemStack> cloneItemStacks(List<ItemStack> list) {
        List<ItemStack> newlist = new ArrayList<>();
        for (ItemStack item : list) {
            newlist.add(item.clone());
        }
        return newlist;
    }

}