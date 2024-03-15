package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static me.vannername.mainplugin.utils.Utils.config;

public class ServerNotes implements CommandExecutor, TabCompleter {

    public ServerNotes(MainPlugin plugin) {
        plugin.getCommand("servernotes").setExecutor(this);
    }
    List<String> colors = List.of("red","green","aqua","dark_aqua","blue","dark_blue","yellow","gold","dark_purple","light_purple");
    List<String> styles = List.of("bold","italic","underline","strikethrough");

    private String checkAndDetectCoordsShortcut(String text, CommandSender sender) throws Exception {
        if(text.contains("%c")) {
            if(sender instanceof Player p) {
                Location l = p.getLocation();
                return text.replace("%c", "%1$d %2$d %3$d".formatted(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
            } else throw new Exception();
        } else {
            return text;
        }
    }

    // very not pretty but does the job
    private String checkOperation(String s, String type) throws Exception {
        switch(type) {
            case "color" -> {
                if(colors.contains(s)) return s;
                else if(s.contains("none")) return "none";
                else throw new Exception();
            }
            case "style" -> {
                if(styles.contains(s)) return s;
                else if(s.contains("none")) return "none";
                else throw new Exception();
            }
            default -> { return ""; }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Map<?, ?>> notes = config.getMapList("server-notes");

        if (strings.length == 0) {
            Bukkit.dispatchCommand(commandSender, "svn list");
            return true;
        }

        switch (strings[0]) {
            case "add" -> {
                HashMap<String, String> entry = new HashMap<>();
                StringBuilder text = new StringBuilder();

                if (strings.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "You didn't write anything.");
                    return false;
                }
                for (int i = 3; i < strings.length; i++) {
                    text.append(strings[i]).append(" ");
                }

                if(strings[1].equals("~") && strings[2].equals("~") && strings[3].equals("~")) {
                    commandSender.sendMessage(ChatColor.RED + "You haven't made any changes.");
                    return false;
                }

                try {
                    entry.put("text", checkAndDetectCoordsShortcut(text.toString().trim(), commandSender));
                } catch (Exception e) {
                    commandSender.sendMessage(ChatColor.RED + "Usage of coords macros from console is forbidden.");
                    return false;
                }
                try {
                    entry.put("color", checkOperation(strings[1], "color").toUpperCase());
                    entry.put("style", checkOperation(strings[2], "style").toUpperCase());
                } catch (Exception e) {
                    commandSender.sendMessage(ChatColor.RED +"Wrong color/style.");
                    return false;
                }

                notes.add(entry);
                commandSender.sendMessage(ChatColor.GREEN + "Note created successfully!");
            }

            case "redact" -> {

                if (strings.length < 4) {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /servernotes redact <index of line to edit> <new color> <new text style> <new text>");
                }

                StringBuilder text = new StringBuilder();
                for (int i = 4; i < strings.length; i++) {
                    text.append(strings[i]).append(" ");
                }

                try {
                    int index = Integer.parseInt(strings[1]) - 1;
                    Map<?, ?> noteAtIndex = notes.get(index);
                    HashMap<String, String> entry = new HashMap<>();
                    try {
                        entry.put("text", strings[4].equals("~") ? (String) noteAtIndex.get("text")
                                : checkAndDetectCoordsShortcut(text.toString().trim(), commandSender));
                    } catch (Exception e) {
                        commandSender.sendMessage(ChatColor.RED + "Usage of coords macros from console is forbidden.");
                        return false;
                    }
                    try {
                        entry.put("color", strings[2].equals("~") ? (String) noteAtIndex.get("color") : checkOperation(strings[2], "color").toUpperCase());
                        entry.put("style", strings[3].equals("~") ? (String) noteAtIndex.get("style") : checkOperation(strings[3], "style").toUpperCase());
                    } catch (Exception e) {
                        commandSender.sendMessage(ChatColor.RED +"Wrong color/style.");
                        return false;
                    }

                    notes.set(index, entry);
                    commandSender.sendMessage(ChatColor.GREEN + "Note edited successfully!");

                } catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "Please input number as index!");
                } catch (IndexOutOfBoundsException e) {
                    commandSender.sendMessage("This line doesn't exist.");
                }
            }

            case "list" -> {
                if (notes.isEmpty()) {
                    commandSender.sendMessage(ChatColor.AQUA + "No notes yet. Wanna change that?");
                    return false;
                }
                int index = 1;

                commandSender.sendMessage("\n");
                commandSender.sendMessage(ChatColor.GOLD + "------------------SERVER NOTES--------------------");
                for (Map<?, ?> map : notes) {
                    ChatColor color = ChatColor.WHITE;
                    String style = "";

                    if (!map.get("color").equals("NONE")) color = ChatColor.of((String) map.get("color"));
                    if (!map.get("style").equals("NONE")) style = (String) map.get("style");

                    sendWithPossibleCoordinates(commandSender, index, (String) map.get("text"), color, style);
                    index++;
                }
            }

            case "remove" -> {
                try {
                    notes.remove(Integer.parseInt(strings[1]) - 1);
                    commandSender.sendMessage(ChatColor.GREEN + "Note removed successfully.");
                } catch (IndexOutOfBoundsException e) {
                    commandSender.sendMessage(ChatColor.RED + "This line doesn't exist");
                }
            }



            case "fullreset" -> {
                // lol
                if (strings.length == 1 || !strings[1].equals("please?")) {
                    commandSender.sendMessage(ChatColor.RED + "Wrong reset password.");
                    return false;
                }

                notes.clear();
                commandSender.sendMessage(ChatColor.MAGIC + "dhsjh  " + ChatColor.GOLD + "All server notes have been successfully removed" + ChatColor.WHITE + ChatColor.MAGIC + "  dhsjh");
            }

            default -> {
                commandSender.sendMessage(ChatColor.RED + "/servernotes or /svn <add/redact/remove/list> [color] [text style] [text to add]");
                return false;
            }
        }

        Utils.setInConfig("server-notes", notes);
        return true;
    }

    public static List<String> getAllNotesCoords() {
        List<Map<?, ?>> notes = config.getMapList("server-notes");
        List<String> res = new ArrayList<>();
        notes.forEach(map -> {
            Matcher matcher = Pattern.compile("(-*[0-9]+ -*[0-9]+ -*[0-9]+)").matcher((String) map.get("text"));
            if(matcher.find())
                res.add(matcher.group(1));
        });
        return res;
    }

    // Below lies the result of several tens of hours of work, a piece of code carefully crafted to
    // fulfill its purpose. However, because it was crafted to just work, it might be suboptimal.
    // If you happen to find this code and wish to optimise it, be my guest. I'm sure some parts can
    // be improved to make the thing better. But be warned - it might lead to many wasted hours for
    // a feature that will barely be used. Also detects only 1st set of coordinates as of now.
    private static void sendWithPossibleCoordinates(CommandSender p, int lineIndex, String toSend, ChatColor color, String style) {
        Matcher matcher = Pattern.compile("(-*[0-9]+ -*[0-9]+ -*[0-9]+)").matcher(toSend);

        if(matcher.find() && p instanceof Player) {
            ComponentBuilder temp = new ComponentBuilder().append("Line " + lineIndex + ": ").color(ChatColor.GRAY).append("").reset();
            String coords = matcher.group(1);
            String[] _toSend = toSend.split(coords);

            if(_toSend.length != 0) {
                if(!toSend.endsWith(coords)) {
                    if(_toSend.length != 1) {
                        Utils.appendStyle(temp.append(_toSend[0]).color(color), style);
                        _toSend[0] = _toSend[1];
                    }
                    p.spigot().sendMessage(Utils.appendStyle(Utils.addNavLinkToComponent(temp, coords, color, style).append("").reset().color(color), style).append(_toSend[0]).create());
                } else
                    p.spigot().sendMessage(Utils.addNavLinkToComponent(Utils.appendStyle(temp.append(_toSend[0]).color(color), style), coords, color, style).create());
            } else {
                p.spigot().sendMessage(Utils.addNavLinkToComponent(temp, coords, color, style).create());
            }

        } else
            p.sendMessage(ChatColor.GRAY + "Line " + lineIndex + ": " + color + (style.isEmpty() ? "" : ChatColor.of(style)) + toSend);
    }

    List<String> s1 = List.of("add","redact","remove","list");
    List<String> s2;
    List<String> sFinal = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        int size = config.getMapList("server-notes").size();
        s2 = Collections.singletonList(size == 0 ? "" : String.valueOf(size));
        sFinal = new ArrayList<>();

        if(strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], s1, sFinal);
        }
        if(strings.length == 2 && (strings[0].equals("remove") || strings[0].equals("redact"))) {
            return StringUtil.copyPartialMatches(strings[1], s2, sFinal);
        }
        if(strings.length == 2 && strings[0].equals("add")) {
            return Stream.concat(Stream.of( ">none<"), StringUtil.copyPartialMatches(strings[1], colors, sFinal).stream()).toList();
        }
        if(strings.length == 3 && strings[0].equals("redact")) {
            return Stream.concat(Stream.of(">none<"), StringUtil.copyPartialMatches(strings[2], colors, sFinal).stream()).toList();
    }
        // to remember: index in startsWith is different, that's why we need several ifs
        if(strings.length == 3 && strings[0].equals("add")) {
            return Stream.concat(Stream.of(">none<"), StringUtil.copyPartialMatches(strings[2], styles, sFinal).stream()).toList();
        }
        if(strings.length == 4 && strings[0].equals("redact")) {
            return Stream.concat(Stream.of(">none<"), StringUtil.copyPartialMatches(strings[3], styles, sFinal).stream()).toList();
        }
        return sFinal;
    }
}
