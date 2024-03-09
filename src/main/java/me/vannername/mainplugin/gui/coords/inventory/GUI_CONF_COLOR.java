package me.vannername.mainplugin.gui.coords.inventory;

import me.vannername.mainplugin.gui.coords.GUI_CONF_HOTBAR;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_COORDS;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_LETTERS;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GUI_CONF_COLOR {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Color settings";
        inv = Bukkit.createInventory(null, inv_rows);
    }
    public static Inventory GUI() {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);
        Utils.createItem(inv, Material.BOOK, 1, ChatColor.AQUA + "Change text color", ChatColor.DARK_PURPLE+"Change the color of coordinate letters and text");
        Utils.createItem(inv, Material.COMPASS, 4, ChatColor.AQUA + "Change coordinates color", ChatColor.DARK_PURPLE+"Change the color of the coordinates");
        Utils.createItem(inv, Material.BOOKSHELF, 7, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getItemMeta().getDisplayName().contains("Change text color")) {
            p.openInventory(GUI_LETTERS.GUI());
        }
        if (clicked.getItemMeta().getDisplayName().contains("Change coordinates color")) {
            p.openInventory(GUI_COORDS.GUI());
        }
        if (clicked.getItemMeta().getDisplayName().contains(ChatColor.RED + "Back")) {
            p.openInventory(GUI_CONF_HOTBAR.GUI());
        }
    }
}
