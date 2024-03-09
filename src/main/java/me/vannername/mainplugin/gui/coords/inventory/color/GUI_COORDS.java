package me.vannername.mainplugin.gui.coords.inventory.color;

import me.vannername.mainplugin.gui.GUI_CONF_COORDS;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_COLOR;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GUI_COORDS {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 3 * 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Configure coordinates color";
        inv = Bukkit.createInventory(null, inv_rows);
    }
    public static Inventory GUI() {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);
        Utils.createItem(inv, Material.RED_CONCRETE, 9, ChatColor.RED+"Red", ChatColor.DARK_PURPLE+"Changes the color of coordinates to red");
        Utils.createItem(inv, Material.GOLD_BLOCK, 10, ChatColor.GOLD+"Gold", ChatColor.DARK_PURPLE+"Changes the color of coordinates to gold");
        Utils.createItem(inv, Material.YELLOW_CONCRETE, 11, ChatColor.YELLOW+"Yellow", ChatColor.DARK_PURPLE+"Changes the color of coordinates to yellow");
        Utils.createItem(inv, Material.GREEN_CONCRETE, 12, ChatColor.GREEN+"Green (default)", ChatColor.DARK_PURPLE+"Changes the color of coordinates to green");
        Utils.createItem(inv, Material.LIGHT_BLUE_CONCRETE, 13, ChatColor.AQUA+"Aqua", ChatColor.DARK_PURPLE+"Changes the color of coordinates to aqua");
        Utils.createItem(inv, Material.BLUE_CONCRETE, 14, ChatColor.BLUE+"Blue", ChatColor.DARK_PURPLE+"Changes the color of coordinates to blue");
        Utils.createItem(inv, Material.PURPLE_CONCRETE, 15, ChatColor.DARK_PURPLE+"Purple", ChatColor.DARK_PURPLE+"Changes the color of coordinates to purple");
        Utils.createItem(inv, Material.WHITE_CONCRETE, 16, ChatColor.RESET+"White", ChatColor.DARK_PURPLE+"Changes the color of coordinates to white");
        Utils.createItem(inv, Material.MUSIC_DISC_11, 17, ChatColor.BLACK+"Black",ChatColor.DARK_PURPLE+"Black.");
        Utils.createItem(inv, Material.BOOKSHELF, 26, ChatColor.RED+"Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        boolean back = false;
        String color = "WHITE";

        if (clicked.getItemMeta().getDisplayName().contains("Red")) {
            color = "RED";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Gold")) {
            color = "GOLD";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Yellow")) {
            color = "YELLOW";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Green (default)")) {
            color = "GREEN";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Aqua")) {
            color = "AQUA";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Blue")) {
            color = "BLUE";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Purple")) {
            color = "DARK_PURPLE";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Black")) {
            color = "BLACK";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Back")) {
            back = true;
        }

        if(!back) {
            Utils.getPluginPlayer(p).setHotbarCoordinatesColor("CC", color);
            p.sendMessage(ChatColor.of(color) + "Color changed!");
        } else {
            p.openInventory(GUI_CONF_COLOR.GUI());
        }
    }
}
