package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.commands.Teleport;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI_TP {

    public static Inventory inv;
    public static String inv_name;
    public static int inv_rows = 9;

    public static void init() {
        inv_name = ChatColor.AQUA + "Make a decision";
        inv = Bukkit.createInventory(null, inv_rows);
    }

    public static Inventory GUI(Player p) {
        MainPluginPlayer mpp = Utils.getPluginPlayer(p);
        Teleport.TPData tpData = Teleport.computeTPData(p);
        Utils.MeetsConditions result = Teleport.checkTPConditions(mpp, tpData.cost);
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inv_name);

        if(!result.value) {
            Utils.createItem(inv, Material.BARRIER, 1, result.expl);
            Utils.createItem(inv, Material.BARRIER, 4, result.expl);
        } else {
            int totalXP = mpp.getTotalXP();

            String distanceText = ChatColor.AQUA + "Distance to your spawn point: ";
            if (tpData.isToWorldSpawn) {
                distanceText = ChatColor.RED + "Your spawn location is not set in this world, so you will be sent to spawn.\n";
            }
            distanceText += ChatColor.GREEN + String.format("%.2f", tpData.dist / 1000) + " km";

            String xpText = ChatColor.AQUA + "Your current total XP: " + ChatColor.GREEN + totalXP + " points";
            String costText = ChatColor.AQUA + "Teleport cost: " + ChatColor.GREEN + (int) tpData.cost + " points";
            String costEvalText = ChatColor.DARK_AQUA + "Cost evaluation: ";

            double frac = (tpData.cost / totalXP) * 100;

            if (frac > 1)
                costEvalText += ChatColor.GREEN + "Around " + (int) frac + "% of your XP will be deducted";
            else
                costEvalText += ChatColor.GREEN + "Around " + String.format("%.3f", frac) + "% of your XP will be deducted";

            int xpAfter = (int) (totalXP - tpData.cost);
            int levelAfter = Utils.XPtoLevel(xpAfter);
            int fracToNew = (int) (((double) xpAfter / Utils.levelToXP(levelAfter + 1, 0)) * 100);
            String afterTP = ChatColor.AQUA + "After teleporting you will have: " + ChatColor.GREEN + levelAfter +
                    ChatColor.AQUA + " levels + " + ChatColor.GREEN + fracToNew + ChatColor.AQUA + "%";

            Utils.createItem(inv, Material.ENDER_EYE, 1, ChatColor.RESET + "Info", distanceText, xpText, costText, costEvalText, afterTP);
            Utils.createItem(inv, Material.WHITE_BED, 4, ChatColor.GREEN + "Click to teleport");
        }

        Utils.createItem(inv, Material.BOOKSHELF, 7, ChatColor.RED + "Back");

        toReturn.setContents(inv.getContents());
        return toReturn;
    }
    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getType() == Material.WHITE_BED) {
            p.closeInventory();
            Teleport.teleport(p);
        }
        else if (clicked.getType() == Material.BOOKSHELF) p.openInventory(GUI_MAIN.GUI(p));
    }
}
