package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calculate implements CommandExecutor, TabCompleter {
    static boolean confirmedLicence = false; // i hate this so much.
    public Calculate(MainPlugin plugin) {
        plugin.getCommand("calculate").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!confirmedLicence) {
            confirmedLicence = License.iConfirmNonCommercialUse("TrueVanner");
        }

        String eq = "";
        for(int i = 0; i < strings.length; i++) {
            eq += strings[i];
        }
        if (commandSender instanceof Player p) {
            if(eq.equals("77+33")) {
                p.getServer().getOnlinePlayers().forEach(pl -> {
                    pl.playSound(p, "custom.calc", 1, 1);
                });
            }
            if(eq.equals("9+10")) {
                commandSender.spigot().sendMessage(new ComponentBuilder()
                        .append("Result: ")
                        .color(ChatColor.AQUA)
                        .append("21")
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/watch?v=vBNR_4YTOm0"))
                        .create());
                return true;
            }
        }

        try {
            Expression expr = new Expression(eq);
            Double result = expr.calculate();
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(4);
            commandSender.sendMessage(ChatColor.AQUA + "Result: " + ChatColor.WHITE + df.format(result));

            return true;
        } catch (Exception e) {
            commandSender.sendMessage(ChatColor.RED + "Something went wrong!");
            return false;
        }
    }

    // to remove default tabcomplete (does there exist another way?)
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
