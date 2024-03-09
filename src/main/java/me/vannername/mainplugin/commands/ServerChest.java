package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ServerChest implements CommandExecutor, Listener {
    public static Inventory inv;
    public static ItemStack requiredItem;

    public ServerChest(MainPlugin plugin) {
        plugin.getCommand("serverchest").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        inv = Bukkit.createInventory(null, InventoryType.ENDER_CHEST);

        requiredItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = requiredItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Server Chest");
        requiredItem.setItemMeta(meta);
    }

    Inventory toReturn = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, "Server Chest");

    public Inventory chest() {
        if (toReturn.getViewers().isEmpty()) {
            toReturn.setContents(inv.getContents());
            return toReturn;
        } else {
            throw new Error("The server chest is being viewed by another player! Wait for a bit before entering it");
        }
    }

    @EventHandler
    public void onExit(InventoryCloseEvent e) {
        inv.setContents(toReturn.getContents());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Command can be performed by players only.");
            return true;
        }

        if (p.getInventory().containsAtLeast(requiredItem, 1)) {
            try {
                p.openInventory(chest());
            } catch (Error err) {
                p.sendMessage(ChatColor.RED + err.getMessage());
            }
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "You don't have a " + ChatColor.GOLD + "Server Chest" + ChatColor.RED + " yet! Craft it first to gain access to the global server storage!");
            return false;
        }
    }
}
