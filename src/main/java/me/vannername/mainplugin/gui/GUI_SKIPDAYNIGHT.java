package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.commands.DayNightSkipper;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI_SKIPDAYNIGHT {
    public static Inventory inv;
    public static String inv_name = ChatColor.AQUA + "Configure day skipping";
    public static int inv_rows = 9;

    public static void init() {
        inv = Bukkit.createInventory(null, inv_rows);
    }
    public static Inventory GUI(boolean mode) {
        if(!mode) inv_name = ChatColor.AQUA + "Configure night skipping";
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);
        ChatColor color = mode ? ChatColor.GRAY : ChatColor.YELLOW;
        String name = Utils.timeOfDayString(mode);

        Utils.createItem(inv, Material.BARRIER, 0, ChatColor.AQUA+"Cancel " + name + " skipping");
        Utils.createItem(inv, Material.CLOCK, 2, color + "Skip 5 "+ name + "s");
        Utils.createItem(inv, Material.CLOCK, 3, color + "Infinitely skip "+ name + "s");
        Utils.createItem(inv, Material.CLOCK, 4, color + "Force-skip a "+ name);
        Utils.createItem(inv, Material.BOOK, 6, ChatColor.YELLOW + "Status: " + DayNightSkipper.getStatusString(mode));
        Utils.createItem(inv, Material.BOOKSHELF,8, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        String name = Utils.timeOfDayString(clicked.getItemMeta().getDisplayName().contains("day"));

        if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Back")) {
            p.openInventory(GUI_MAIN.GUI(p));
        } else if(!clicked.getItemMeta().getDisplayName().contains("View")) {
            if (clicked.getItemMeta().getDisplayName().contains("Cancel")) {
                p.performCommand("skip" + name + " 0");
            }
            if (clicked.getItemMeta().getDisplayName().contains("5")) {
                p.performCommand("skip" + name + " 5");
            }
            if (clicked.getItemMeta().getDisplayName().contains("Inf")) {
                p.performCommand("skip" + name + " inf");
            }
            if (clicked.getItemMeta().getDisplayName().contains("Force")) {
                p.performCommand("skip" + name + " force");
            }
            p.closeInventory();
        }
    }
}
