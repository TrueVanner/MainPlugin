package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.gui.coords.GUI_CONF_HOTBAR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_TYPE;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.vannername.mainplugin.utils.Utils.config;

public class GUI_CONF_COORDS {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Configure coordinate display";
        inv = Bukkit.createInventory(null, inv_rows);
    }
    private static boolean isParamOn(Player p, String param) {
        return config.getBoolean(Utils.getPlayerConfigPath(p, param));
    }

    public static Inventory GUI(Player p) {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        Utils.createItem(inv, Material.CRAFTING_TABLE, 1, ChatColor.AQUA+"Hotbar coordinates", ChatColor.DARK_PURPLE+"Configure hotbar coordinates settings");
        Utils.createItem(inv, Material.valueOf((isParamOn(p, ".senddeathcoords") ? "GREEN" : "RED") + "_CONCRETE"), 4, ChatColor.AQUA+"Send death coordinates?");
        Utils.createItem(inv, Material.BOOKSHELF, 7, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }

    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getItemMeta().getDisplayName().contains("Hotbar coordinates")) {
            p.openInventory(GUI_CONF_HOTBAR.GUI());
        }
        if (clicked.getItemMeta().getDisplayName().contains("Send death coordinates?")) {
            MainPluginPlayer mpp = Utils.getPluginPlayer(p);
            mpp.setInConfig(".senddeathcoords", !isParamOn(p, ".senddeathcoords"));
            p.openInventory(GUI_CONF_COORDS.GUI(p));
        }
        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back")) {
            p.openInventory(GUI_MAIN.GUI(p));
        }
    }
}

