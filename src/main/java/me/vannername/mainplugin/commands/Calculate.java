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
import org.mariuszgromada.math.mxparser.Expression;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class Calculate implements CommandExecutor, TabCompleter {
    public Calculate(MainPlugin plugin) {
        plugin.getCommand("calculate").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        String eq = "";
        for(int i = 0; i < strings.length; i++) {
            eq += strings[0] + " ";
        }
        Expression expr = new Expression(eq);
        try {
            commandSender.sendMessage(ChatColor.AQUA + "Result: " + ChatColor.WHITE + String.format("%.4f", expr.calculate()));
            return true;
        } catch (Exception e) {
            commandSender.sendMessage(ChatColor.RED + "Something went wrong!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
