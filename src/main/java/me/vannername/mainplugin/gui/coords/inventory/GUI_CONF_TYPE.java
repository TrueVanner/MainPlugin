package me.vannername.mainplugin.gui.coords.inventory;

import me.vannername.mainplugin.gui.coords.GUI_CONF_HOTBAR;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GUI_CONF_TYPE {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 3 * 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Hotbar coordinates data";
        inv = Bukkit.createInventory(null, inv_rows);
    }

    private static boolean isParamOn(MainPluginPlayer mpp, String param) {
        return mpp.getFromConfig(".coords" + param, true);
    }

    public static Inventory GUI(Player p) {
        return GUI(Utils.getPluginPlayer(p));
    }
    public static Inventory GUI(MainPluginPlayer mpp) {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        Utils.createItem(inv, Material.valueOf((isParamOn(mpp, ".show") ? "GREEN" : "RED") + "_CONCRETE"), 10, ChatColor.AQUA+"Visible?", ChatColor.DARK_PURPLE + "Display location data above hotbar?");
        Utils.createItem(inv, Material.valueOf((isParamOn(mpp, ".coords") ? "GREEN" : "RED") + "_CONCRETE"), 12, ChatColor.AQUA+"Show coordinates?", ChatColor.DARK_PURPLE+"Display your current location in coordinates?");
        Utils.createItem(inv, Material.valueOf((isParamOn(mpp, ".pitch") ? "GREEN" : "RED") + "_CONCRETE"), 14, ChatColor.AQUA+"Show pitch?", ChatColor.DARK_PURPLE+"Display your current tilt angle?");
        Utils.createItem(inv, Material.valueOf((isParamOn(mpp, ".direction") ? "GREEN" : "RED") + "_CONCRETE"), 16, ChatColor.AQUA+"Show direction?", ChatColor.DARK_PURPLE+"Display your direction in bearings?");
        Utils.createItem(inv, Material.BOOKSHELF, 26, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        MainPluginPlayer mpp = Utils.getPluginPlayer(p);
        boolean back = false;
        String param = "";
        if (clicked.getItemMeta().getDisplayName().contains("Visible")) {
            param = ".show";
        }
        if (clicked.getItemMeta().getDisplayName().contains("coordinates")) {
            param = ".coords";
        }
        if (clicked.getItemMeta().getDisplayName().contains("pitch")) {
            param = ".pitch";
        }
        if (clicked.getItemMeta().getDisplayName().contains("direction")) {
            param = ".direction";
        }
        if (clicked.getItemMeta().getDisplayName().contains("Back")) {
            p.openInventory(GUI_CONF_HOTBAR.GUI());
            back = true;
        }

        if(!back) {
            mpp.setInConfig(".coords." + param, !isParamOn(mpp, param));
            mpp.displayCoordinates.loadParams();
            p.openInventory(GUI_CONF_TYPE.GUI(mpp));
        } else {
            p.openInventory(GUI_CONF_HOTBAR.GUI());
        }
    }
}
