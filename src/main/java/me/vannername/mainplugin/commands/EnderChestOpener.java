package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestOpener implements CommandExecutor {
    public EnderChestOpener(MainPlugin plugin){
        plugin.getCommand("enderchest").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Command can be performed by players only.");
            return true;
        }
        if (p.getInventory().contains(Material.ENDER_CHEST)) {
            p.openInventory(p.openInventory(p.getEnderChest()));
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "You don't have an ender chest in your inventory.");
        }
        return false;
    }
}
