package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
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
            result += res[i] == 0 ? "" : res[i] + (i == 0 ? "h" : (i == 1 ? "m" : "s")) + ", ";
        }
        return result.substring(0, result.length() - 2);
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
            switch(strings[0]) {
                case "cancel" -> {
                    shutdown = -69;
                    commandSender.sendMessage(ChatColor.GREEN + "Disabled shutdown.");
                }
                case "status" -> {
                    if(shutdown == -69) {
                        commandSender.sendMessage(ChatColor.AQUA + "Shutdown hasn't been initiated.");
                    } else {
                        commandSender.sendMessage(Utils.colorSegment("Time until shutdown: %c seconds.",
                                List.of(ChatColor.AQUA, ChatColor.YELLOW), shutdown));
                    }
                }
                default -> {
                    // modes: s = in seconds, m = minutes, h = hours, t = until a specific time in HH:MM (deprecated)
                    String mode;
                    // to make number checks without changing shutdown value directly if a bad input happens
                    int shutdownCandidate;
                    if(strings[0].contains(":")) {
                        mode = "t";
                        String[] timeArr = strings[0].split(":");
                        LocalTime toDisable = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]));

                        shutdownCandidate = (int) (toDisable.getLong(ChronoField.SECOND_OF_DAY) - LocalTime.now().getLong(ChronoField.SECOND_OF_DAY));

                        if (shutdownCandidate <= 0) {
                            if(shutdownCandidate <= -600) { // make sure that tiny past values are not accepted (likely user's mistake)
                                shutdownCandidate += 86400; // if the time is before shutdown, add 1 day in seconds to it for correct offset.
                            } else {
                                commandSender.sendMessage(ChatColor.RED + "This time has already passed.");
                                return false;
                            }
                        }
                    } else {
                        try {
                            shutdownCandidate = Integer.parseInt(strings[0].substring(0, strings[0].length() - 1)); // number without mode
                            mode = strings[0].substring(strings[0].length() - 1);
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(ChatColor.RED + "Bad shutdown value.");
                            commandSender.sendMessage(errorMessage);
                            return false;
                        }
                    }

                    switch (mode) {
                        case "s":
                        case "t":
                            break;
                        case "h":
                            shutdownCandidate *= 60;
                        case "m":
                            shutdownCandidate *= 60;
                            break;
                        default:
                            commandSender.sendMessage("Wrong mode");
                            commandSender.sendMessage(errorMessage);
                            return false;
                    }

                    if(shutdownCandidate >= 86400) {
                        commandSender.sendMessage(ChatColor.RED + "Bad time.");
                        commandSender.sendMessage(errorMessage);
                        return false;
                    }

                    shutdown = shutdownCandidate;

//            if (strings[strings.length - 1].equals("full")) {
//                try {
//                    Runtime.getRuntime().exec("shutdown -s -t " + (shutdown + 60));
//                } catch (IOException e) {
//                    commandSender.sendMessage(e.getMessage());
//                }
//            }

                    String result1;
                    String result2;

                    if(mode == "t") {
                        result1 = Utils.colorSegment("Server will be shut down at %c!",
                                List.of(ChatColor.AQUA, ChatColor.LIGHT_PURPLE), strings[0]);
                        result2 = ChatColor.AQUA + "(in %s)".formatted(resText());
                    } else {
                        result1 = Utils.colorSegment("Server will be shut down in %c!",
                                List.of(ChatColor.AQUA, ChatColor.LIGHT_PURPLE), resText());
                        result2 = ChatColor.AQUA + "(at %s)".formatted(LocalTime.now().plusSeconds(shutdown).toString().substring(0, 5));
                    }
                    plugin.getServer().getOnlinePlayers().forEach((Player p) -> p.sendTitle(result1, result2, 10, 60, 10));
                    if(commandSender instanceof ConsoleCommandSender) {
                        commandSender.sendMessage(Utils.colorSegment("Successfully set shutdown in %c seconds.",
                                List.of(ChatColor.GREEN, ChatColor.YELLOW), shutdown));
                    }
                    MainPlugin.shouldSaveConfig = true;
                }
            }
            return true;
        } else {
            commandSender.sendMessage(errorMessage);
            return false;
        }
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

    List<String> s1 = List.of("30m", "2h", "00:00", "cancel", "status");
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

