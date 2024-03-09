package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPassive implements CommandExecutor {
    public SetPassive(MainPlugin plugin) {
        plugin.getCommand("passive").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p)) {
            commandSender.sendMessage(ChatColor.RED + "This command is for player usage only!");
            return false;
        }
        MainPluginPlayer mpp = Utils.getPluginPlayer(p);
        mpp.invertPassive();
        if(mpp.isPassive) {
            p.sendMessage(ChatColor.GREEN + "Successfully marked you as passive. You can no longer be damaged by other players but can't damage them as well. Run the command again to un-pacify yourself.");
        } else {
            p.sendMessage(ChatColor.GREEN + "Successfully marked you as non-passive.");
        }
        return true;
    }
}
