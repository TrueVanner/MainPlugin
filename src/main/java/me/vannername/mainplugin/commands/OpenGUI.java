package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.gui.GUI_MAIN;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenGUI implements CommandExecutor {
    public OpenGUI(MainPlugin plugin){
        plugin.getCommand("gui").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }
        p.openInventory(GUI_MAIN.GUI(p));
        return false;
    }
}
