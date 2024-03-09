package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ScheduleStop implements CommandExecutor, TabCompleter {
    private final MainPlugin plugin;

    public ScheduleStop(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("schedulestop").setExecutor(this);
    }

    private static final int[] res = new int[]{ 0, 0, 0 }; // h-m-s
    public static int shutdown;
    private static char mode = 0;

    /* String correctString(int num, char mode) {
        String res = "";
        if(num <= 0) return res;

        switch (mode) {
            case 'h' -> res = " hour" + (num > 1 ? "s " : " ");
            case 'm' -> res = " min" + (num > 1 ? "s" : " ");
            case 's' -> res = " second" + (num > 1 ? "s." : ".");
        }

        return ChatColor.LIGHT_PURPLE + String.valueOf(num) + ChatColor.AQUA + res;
    } */

    String resText() {
        res[2] = shutdown;
        res[1] = 0;
        res[0] = 0;
        if (shutdown >= 60 && shutdown < 3600) {
            res[1] = shutdown / 60;
            res[2] = shutdown % 60;
        }
        if (shutdown >= 3600) {
            res[0] = shutdown / 3600;
            res[1] = (shutdown % 3600) / 60;
            res[2] = (shutdown % 3600) % 60;
        }

        String result = "";
        for(int i = 0; i < 3; i++) {
            // sorry.
            result += res[i] == 0 ? "" : res[i] + (i == 0 ? 's' : (i == 1 ? 'm' : 'h'));
        }
        return result.substring(0, result.length() - 2);
//        return "%1$sh, %2$sm, %3$ss".formatted(res[0], res[1], res[2]);
//        if(res[2] != 0) {
//            if(res[0] != 0 && res[1] != 0) {
//                return baseAll.formatted(args);
//            } else {
//                return baseWithSeconds.formatted(args);
//            }
//        } else {
//            return baseWithoutSeconds.formatted(args);
//        }
//        return ((res[0] != 0 ?  : "") + (res[1] != 0 ? (", " + correctString(res[1], 'm')) : "") + (res[2] != 0 ? (" and around " + correctString(res[2], 's')) : "")).trim() + '!';
    }

    String errorMessage = ChatColor.RED + "/schedulestop (/sstop) [relative time with h/m/s (2h, only 1 value - 2h 30m -> 150m), relative time (00:00), or \"cancel\"]";
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//            try {
//                Runtime.getRuntime().exec("sudo shutdown -c");
//            } catch (IOException e) {
//                commandSender.sendMessage(e.getMessage());
//            }


        if (strings.length == 1) {
            if(strings[0].equalsIgnoreCase("cancel")) {
                shutdown = -69;
                commandSender.sendMessage(ChatColor.GREEN + "Disabled shutdown.");
                return true;
            }

            try {
                shutdown = Integer.parseInt(strings[0].substring(0, strings[0].length() - 1)); // number without mode
            } catch (NumberFormatException e) {
                if (strings[0].contains(":")) {
                    mode = 't';
                    String[] arr = strings[0].split(":");
                    LocalTime toDisable = LocalTime.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                    shutdown = (int) (toDisable.getLong(ChronoField.SECOND_OF_DAY) - LocalTime.now().getLong(ChronoField.SECOND_OF_DAY));

                    if (shutdown <= 0) {
                        shutdown += 86400; // if the time is before shutdown, add 1 day in seconds to it for correct offset.
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Bad shutdown value.");
                    commandSender.sendMessage(errorMessage);
                    return false;
                }
            }

            // modes: s = in seconds, m = minutes, h = hours, t = until a specific time in HH:MM
            mode = mode == 0 ? strings[0].charAt(strings[0].length() - 1) : mode; // an attempt to remove the need for using 't' as a tag for abs time messages
            switch (mode) {
                case 's':
                case 't':
                    break;
                case 'h':
                    shutdown *= 60;
                case 'm':
                    shutdown *= 60;
                    break;
                default:
                    commandSender.sendMessage("Wrong mode");
                    commandSender.sendMessage(errorMessage);
                    return false;
            }

            if(shutdown >= 86400) {
                commandSender.sendMessage(ChatColor.RED + "Bad time.");
                commandSender.sendMessage(errorMessage);
                return false;
            }

//            if (strings[strings.length - 1].equals("full")) {
//                try {
//                    Runtime.getRuntime().exec("shutdown -s -t " + (shutdown + 60));
//                } catch (IOException e) {
//                    commandSender.sendMessage(e.getMessage());
//                }
//            }

            String result1;
            String result2;

            if(mode == 't') {
                result1 = ChatColor.AQUA + "Server will be shut down at " + ChatColor.LIGHT_PURPLE + "%s!".formatted(strings[0].split("t")[0]);
                result2 = ChatColor.AQUA + "(in %s)".formatted(resText());
            } else {
                result1 = ChatColor.AQUA + "Server will be shut down in %s!".formatted(resText());
                result2 = "";
            }
            plugin.getServer().getOnlinePlayers().forEach((Player p) -> p.sendTitle(result1, result2, 10, 60, 10));

        } else {
            commandSender.sendMessage(errorMessage);
        }
        return true;
    }

    public static void progressShutdown() { // server shutdown notifications
        if (shutdown != -69) {
            if (shutdown <= 0) {
                shutdown = -69;
                Bukkit.broadcastMessage(ChatColor.AQUA + "Shutting down...");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Utils.plugin, () -> Utils.plugin.getServer().shutdown(), 15L);
            } else {
                shutdown -= 10;
                if ((shutdown < 910 && shutdown >= 900)
                        || (shutdown < 610 && shutdown >= 600)
                        || (shutdown < 310 && shutdown >= 300)
                        || (shutdown < 70 && shutdown >= 60)) {
                    String base = ChatColor.AQUA + "The server will be shut down in %d minute(s)!";
                    Utils.sendAll(base.formatted(shutdown / 60));
                }
            }
        }
    }

    List<String> s1 = List.of("30m", "2h", "00:00", "cancel");
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

