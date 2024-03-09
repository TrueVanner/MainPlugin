package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GUI_TABCOLOR {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 3 * 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Change personal server color";
        inv = Bukkit.createInventory(null, inv_rows);
    }

    public static Inventory GUI() {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        Utils.createItem(inv, Material.WHITE_WOOL, 0, ChatColor.WHITE+"White (default)");
        Utils.createItem(inv, Material.RED_WOOL, 1, ChatColor.RED+"Red");
        Utils.createItem(inv, Material.RED_WOOL, 2, ChatColor.DARK_RED+"Dark red");
        Utils.createItem(inv, Material.ORANGE_WOOL, 3, ChatColor.GOLD+"Gold");
        Utils.createItem(inv, Material.YELLOW_WOOL, 4, ChatColor.YELLOW+"Yellow");
        Utils.createItem(inv, Material.GREEN_WOOL, 5, ChatColor.DARK_GREEN+"Dark green");
        Utils.createItem(inv, Material.LIME_WOOL, 6, ChatColor.GREEN+"Green");
        Utils.createItem(inv, Material.LIGHT_BLUE_WOOL, 7, ChatColor.AQUA+"Aqua");
        Utils.createItem(inv, Material.CYAN_WOOL, 8, ChatColor.DARK_AQUA+"Dark aqua");
        Utils.createItem(inv, Material.BLUE_WOOL, 9, ChatColor.BLUE+"Blue");
        Utils.createItem(inv, Material.BLUE_WOOL, 10, ChatColor.DARK_BLUE+"Dark blue");
        Utils.createItem(inv, Material.MAGENTA_WOOL, 11, ChatColor.LIGHT_PURPLE+"Light purple");
        Utils.createItem(inv, Material.PURPLE_WOOL, 12, ChatColor.DARK_PURPLE+"Dark purple");
        Utils.createItem(inv, Material.LIGHT_GRAY_WOOL, 13, ChatColor.GRAY+"Gray");

        Utils.createItem(inv, Material.BOOKSHELF, 26, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        MainPluginPlayer mpp = Utils.getPluginPlayer(p);

        boolean back = false;
        String color = "RESET";

        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED+"Red")) {
            color = "RED";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED+"Dark red")) {
            color = "DARK_RED";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD+"Gold")) {
            color = "GOLD";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW+"Yellow")) {
            color = "YELLOW";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN+"Dark green")) {
            color = "DARK_GREEN";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN+"Green")) {
            color = "GREEN";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA+"Aqua")) {
            color = "AQUA";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_AQUA+"Dark aqua")) {
            color = "DARK_AQUA";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE+"Blue")) {
            color = "BLUE";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_BLUE+"Dark blue")) {
            color = "DARK_BLUE";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE+"Light purple")) {
            color = "LIGHT_PURPLE";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE+"Dark purple")) {
            color = "DARK_PURPLE";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY+"Gray")) {
            color = "GRAY";
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_GRAY+"Dark gray")) {
            color = "DARK_GRAY";
        }

        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back")) {
            p.openInventory(GUI_MAIN.GUI(mpp));
            back = true;
        }
        if (!back) {
            mpp.setInConfig(".tab_color", color);
            mpp.loadTabColor();
            p.closeInventory();
            p.sendMessage(ChatColor.AQUA + "Server color saved.");
        }
    }
}
