package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Ping implements CommandExecutor, TabCompleter {
    public Ping(MainPlugin plugin) {
        plugin.getCommand("ping").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p)) {
            commandSender.sendMessage(ChatColor.RED + "This command is for player usage only!");
            return false;
        }

        MainPluginPlayer toDisplay;
        if(strings.length == 1 && Utils.isOnline(strings[0])) {
            toDisplay = Utils.getPluginPlayer(strings[0]);
        } else {
            toDisplay = Utils.getPluginPlayer(p);
        }

        Utils.sendAll(toDisplay.pingRecording.generatePingString());
        return true;
    }

    List<String> playerNames;
    List<String> sFinal;

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();
        playerNames = MainPlugin.pluginPlayers.keySet().stream().filter(playerName -> !playerName.equals(commandSender.getName())).toList();

        if(strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], playerNames, sFinal);
        }
        return sFinal;
    }
}
