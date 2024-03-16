package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.vannername.mainplugin.utils.Utils.config;

public class DayNightSkipper implements CommandExecutor, Listener, TabCompleter {
    private final MainPlugin plugin;
    public static int nightsToSkip = 0;
    public static int daysToSkip = 0;
    public static boolean skipDayForce = false;
    public static boolean skipNightForce = false;

    public DayNightSkipper(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("skipnight").setExecutor(this);
        plugin.getCommand("skipday").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        nightsToSkip = config.getInt("nights_to_skip");
        daysToSkip = config.getInt("days_to_skip");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::skipDayNight, 0L, 20L);
    }

    private boolean _main(CommandSender commandSender, FileConfiguration config, String param, boolean mode) {
        String nameMain = Utils.timeOfDayString(mode);
        String nameOther = Utils.timeOfDayString(!mode);
        String cNameOther = Utils.cTimeOfDayString(!mode);
        String name = commandSender instanceof Player p ? Utils.getPluginPlayer(p).getColoredName() : ChatColor.BOLD + "Console";

        try {
            int toSkip = param.equals("inf") ? 100 : Integer.parseInt(param);

            if(config.getInt(nameOther + "s_to_skip") > 0) {
                commandSender.sendMessage(ChatColor.RED + String.format("%3$s skipping has already been set up. Use /skip%2$s 0 to disable %2$s skipping or use /skip%1$s force to skip this %1$s.", nameMain, nameOther, cNameOther));
                return false;
            }
            
            if(toSkip <= 0) {
                Utils.sendAll(ChatColor.AQUA + nameMain + " skipping has been disabled!");
            } else {
                String base;
                if(toSkip >= 100) {
                    base = ChatColor.AQUA + "By request of %1$s " + ChatColor.AQUA + "%2$ss will be skipped forever! (until someone uses /skip%2$s 0)";
                } else {
                    base = ChatColor.AQUA + "By request of %1$s " + ChatColor.LIGHT_PURPLE + toSkip + ChatColor.AQUA + " %2$s(s) will be skipped!";
                }
                Utils.sendAll(base.formatted(name, nameMain));
            }

            if(mode) daysToSkip = toSkip; else nightsToSkip = toSkip;
            Utils.setInConfig(Utils.timeOfDayString(mode) + "s_to_skip", toSkip);

        } catch (NumberFormatException e) {
            if (param.equalsIgnoreCase("force")) {
                if(mode) skipDayForce = true; else skipNightForce = true;
                commandSender.getServer().getWorld("world").setTime(mode ? 13184 : 0);
                Utils.sendAll(name + ChatColor.AQUA + " temporarily set " + nameOther + ".");
            }
            else if (param.equalsIgnoreCase("status")) {
                commandSender.sendMessage(getStatusString(mode));
            }
            else commandSender.sendMessage(ChatColor.RED + "/skip" + nameMain + " [number/\"status\"/\"force\"]");
        }
        return true;
    }

    public static String getStatusString(boolean mode) {
        String name = Utils.cTimeOfDayString(mode);
        if((mode && daysToSkip == 0) || (!mode && nightsToSkip == 0)) {
            return ChatColor.AQUA + name + " skipping hasn't been set up yet.";
        } else if ((mode && daysToSkip >= 100) || (!mode && nightsToSkip >= 100)) {
            return ChatColor.AQUA + name + "s are being skipped indefinitely.";
        } else {
            return (ChatColor.LIGHT_PURPLE + "%1$s" + ChatColor.AQUA + " more %2$s(s) are planned be skipped.").formatted(mode ? daysToSkip : nightsToSkip, name);
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 1) {
            commandSender.sendMessage(ChatColor.RED + "/" + s + " [*number*/inf/force]");
            return false;
        }
        return _main(commandSender, config, strings[0], command.getName().contains("day"));
    }

    public void skipDayNight() {
        World w = Objects.requireNonNull(plugin.getServer().getWorld("world"));

        if(!plugin.getServer().getOnlinePlayers().isEmpty()) {
            if (w.getTime() >= 12517 && w.getTime() < 24000) {
                skipNightForce = false;
                if (nightsToSkip > 0 && !skipDayForce) {
                    w.setTime(0);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Night skipped successfully!");
                    if(nightsToSkip < 100) {
                        nightsToSkip--;
                        Utils.sendAll(ChatColor.LIGHT_PURPLE + "" + nightsToSkip + ChatColor.AQUA + " more nights remaining");
                        Utils.setInConfig("nights_to_skip", nightsToSkip);
                    }
                }
            } else {
                skipDayForce = false;
                if (daysToSkip > 0 && !skipNightForce) {
                    w.setTime(13184);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Day skipped successfully!");
                    if(daysToSkip < 100) {
                        daysToSkip--;
                        Utils.sendAll(ChatColor.LIGHT_PURPLE + "" + daysToSkip + ChatColor.AQUA + " more days remaining");
                        Utils.setInConfig("days_to_skip", daysToSkip);
                    }
                }
            }
        }
    }

    List<String> s1 = List.of("5", "15", "inf", "force", "status");
    List<String> sFinal;

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();

        if(strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], s1, sFinal);
        }
        return sFinal;
    }
}
