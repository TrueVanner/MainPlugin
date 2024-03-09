package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.gui.coords.GUI_CONF_HOTBAR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_COLOR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_TYPE;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_COORDS;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_LETTERS;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.vannername.mainplugin.utils.Utils.plugin;


public class GUI_MAIN {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 3 * 9;


    public static void init() {
        inv_name = ChatColor.AQUA +"MainPlugin GUI";
        inv = Bukkit.createInventory(null, inv_rows);

        GUI_CONF_HOTBAR.init();
        GUI_CONF_COLOR.init();
        GUI_LETTERS.init();
        GUI_COORDS.init();
        GUI_CONF_TYPE.init();
        GUI_TP.init();
        GUI_CONF_COORDS.init();
        GUI_SKIPDAYNIGHT.init();
        GUI_TABCOLOR.init();
    }

    public static Inventory GUI(Player p) {
        return GUI(Utils.getPluginPlayer(p));
    }
    public static Inventory GUI(MainPluginPlayer mpp) {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        Utils.createItem(inv, Material.COMPASS, 10, ChatColor.AQUA+"Change hotbar coordinates display", ChatColor.RESET+"Change what data is displayed above your hotbar");
        Utils.createItem(inv, Material.RED_DYE, 11, ChatColor.AQUA+"Change server color", ChatColor.RESET+"Change your color in tab list and chat messages");
        Utils.createItem(inv, Material.FIREWORK_STAR, 12, ChatColor.AQUA+"/skipnight", ChatColor.RESET+"Configure night skipping");
        Utils.createItem(inv, Material.SUNFLOWER, 13, ChatColor.AQUA+"/skipday", ChatColor.RESET+"Configure day skipping");
        Utils.createItem(inv, Material.ENDER_CHEST, 14, ChatColor.AQUA+"/enderchest", ChatColor.RESET+"Open your ender chest (is possible)");
        Utils.createItem(inv, Material.ENDER_CHEST, 15, ChatColor.AQUA+"/serverchest", ChatColor.RESET+"Open your server chest (if possible)");
        Utils.createItem(inv, Material.COMPASS, 16, ChatColor.AQUA + "Teleport to spawn", ChatColor.RESET + "Teleport to your spawn point in exchange for XP!");

        if(mpp.isInAir()) {
            Utils.createItem(inv, Material.BARRIER, 8, ChatColor.RED + "You can't sit in the air!");
        } else if(mpp.isAFK()) {
            Utils.createItem(inv, Material.BARRIER, 8, ChatColor.RED + "Can't get up if you're AFK!");
        } else {
            ItemStack i = new ItemStack(Material.PAPER);
            ItemMeta meta = i.getItemMeta();

            if(!mpp.isSitting()) {
                meta.setCustomModelData(10001);
                i.setItemMeta(meta);
                Utils.createCustomItem(inv, i, 8, ChatColor.LIGHT_PURPLE+"Sit");
            } else {
                meta.setCustomModelData(10002);
                i.setItemMeta(meta);
                Utils.createCustomItem(inv, i, 8, ChatColor.LIGHT_PURPLE+"Get up");
            }
        }

        if(mpp.isInAir())
            Utils.createItem(inv, Material.BARRIER, 26, ChatColor.RED+"You can't go AFK in the air!");
        else if(mpp.takenDamageRecently())
            Utils.createItem(inv, Material.BARRIER, 26, ChatColor.RED+"You can't go AFK right after taking damage!");
        else {
            if(mpp.isAFK())
                Utils.createItem(inv, Material.RED_WOOL, 26, ChatColor.RED+"Leave AFK");
            else
                Utils.createItem(inv, Material.GREEN_WOOL, 26, ChatColor.GREEN+"Go AFK");
        }

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        MainPluginPlayer mpp = Utils.getPluginPlayer(p);

        if (clicked.getItemMeta().getDisplayName().contains("Change hotbar coordinates display")) {
            p.openInventory(GUI_CONF_COORDS.GUI(p));
        }
        if (clicked.getItemMeta().getDisplayName().contains("Change server color")) {
            p.openInventory(GUI_TABCOLOR.GUI());
        }
        if (clicked.getItemMeta().getDisplayName().contains("/skipnight")) {
            p.openInventory(GUI_SKIPDAYNIGHT.GUI(false));
        }
        if (clicked.getItemMeta().getDisplayName().contains("/skipday")) {
            p.openInventory(GUI_SKIPDAYNIGHT.GUI(true));
        }
        else if (clicked.getItemMeta().getDisplayName().contains("/enderchest")) {
            p.closeInventory();
            p.performCommand("enderchest");
        }
        else if (clicked.getItemMeta().getDisplayName().contains("/serverchest")) {
            p.closeInventory();
            p.performCommand("serverchest");
        }
        else if (clicked.getItemMeta().getDisplayName().contains("Teleport to spawn")) {
            p.openInventory(GUI_TP.GUI(p));
        }
        else if (clicked.getItemMeta().getDisplayName().contains("Leave AFK")) {
            p.performCommand("afk stop");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.openInventory(GUI_MAIN.GUI(mpp)), 2L);
        }
        else if (clicked.getItemMeta().getDisplayName().contains("Go AFK")) {
            p.performCommand("afk start");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.openInventory(GUI_MAIN.GUI(mpp)), 7L);
        }
        else if (clicked.getItemMeta().getDisplayName().contains("Sit")) {
            mpp.sitting.sit(false);
            p.openInventory(GUI_MAIN.GUI(mpp));
        }
        else if (clicked.getItemMeta().getDisplayName().contains("Get up")) {
            mpp.sitting.unsit();
            p.openInventory(GUI_MAIN.GUI(mpp));
        }
    }
}
