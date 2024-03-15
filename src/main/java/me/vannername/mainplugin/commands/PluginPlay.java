package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class PluginPlay implements CommandExecutor, TabCompleter {
    private final MainPlugin plugin;

    List<String> soundIDs = List.of("kys", "77+33");

    public PluginPlay(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("pluginplay").setExecutor(this);
    }
    private String idToName(String ID) throws NullPointerException {
        return switch (ID) {
            case "kys" -> "custom.motivationalspeech";
            case "77+33" -> "custom.77_33";
            default -> throw new NullPointerException();
        };
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 3 || strings.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /pplay [soundID] <global?>");
            return false;
        }
        if (commandSender instanceof Player p) {
            if(strings[0].contains("stop")) {
                for (String ID : soundIDs) {
                    p.stopSound(idToName(ID));
                }
            } else {
                try {
                    boolean global = strings.length >= 2 && !strings[1].equalsIgnoreCase("false");
                    int volume = 1;
                    try {
                        volume = (int) (Integer.parseInt(strings[2]) / 100.0);
                    } catch (NullPointerException | NumberFormatException ignored) {}
                    String toPlay = idToName(strings[0]);
                    if (global) {
                        for (Player pl : plugin.getServer().getOnlinePlayers()) {
                            pl.playSound(p, toPlay, volume, 1);
                        }
                    } else {
                        p.playSound(p, toPlay, volume, 1);
                    }
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "Bad sound/cutscene ID");
                    return false;
                }
            }
            return true;
        } else {
            commandSender.sendMessage(ChatColor.RED + "This command is for player usage only!");
            return false;
        }
    }
    List<String> s2 = List.of("true","false");
    List<String> s3 = List.of("100", "50", "5");
    List<String> sFinal;

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();

        if(strings.length == 1) {
            return Stream.concat(Stream.of(">stop<"), StringUtil.copyPartialMatches(strings[0], soundIDs, sFinal).stream()).toList();
        }
        if(strings.length == 2) {
            return StringUtil.copyPartialMatches(strings[1], s2, sFinal);
        }
        if(strings.length == 3) {
            return StringUtil.copyPartialMatches(strings[2], s3, sFinal);
        }

        return sFinal;
    }

}
