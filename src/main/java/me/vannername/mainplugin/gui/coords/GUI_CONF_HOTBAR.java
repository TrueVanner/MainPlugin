package me.vannername.mainplugin.gui.coords;

import me.vannername.mainplugin.gui.GUI_CONF_COORDS;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_COLOR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_TYPE;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GUI_CONF_HOTBAR {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Configure hotbar coordinates";
        inv = Bukkit.createInventory(null, inv_rows);
    }
    public static Inventory GUI() {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        Utils.createItem(inv, Material.YELLOW_WOOL, 1, ChatColor.AQUA + "Change color");
        Utils.createItem(inv, Material.OAK_SIGN, 4, ChatColor.AQUA + "Change what is displayed");
        Utils.createItem(inv, Material.BOOKSHELF, 7, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getItemMeta().getDisplayName().contains("Change color")) {
            p.openInventory(GUI_CONF_COLOR.GUI());
        }
        if (clicked.getItemMeta().getDisplayName().contains("Change what is displayed")) {
            p.openInventory(GUI_CONF_TYPE.GUI(p));
        }
        if (clicked.getItemMeta().getDisplayName().contains("Back")) {
            p.openInventory(GUI_CONF_COORDS.GUI(p));
        }
    }
}
