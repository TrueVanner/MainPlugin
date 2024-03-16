package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.logging.Level;

import static me.vannername.mainplugin.utils.Utils.config;
import static me.vannername.mainplugin.utils.Utils.isOnline;

public class Bug implements CommandExecutor, TabCompleter {
    private final MainPlugin plugin;
    public static int opTime;
    private static Player admin; // this feels extremely silly.
    public Bug(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("bug").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        List<Map<?, ?>> reports = config.getMapList("bugreports");

        if(strings.length >= 1) {
            switch (strings[0]) {
                case "report" -> {
                    StringBuilder name = new StringBuilder();
                    StringBuilder descr = new StringBuilder();

                    boolean nameEnded = false;
                    if (strings[1].startsWith("\"")) {
                        for (int i = 1; i < strings.length; i++) {
                            String part = strings[i];
                            if (!nameEnded) {
                                name.append(part).append(" ");
                            } else {
                                descr.append(part).append(" ");
                            }
                            if (part.endsWith("\"")) nameEnded = true;
                        }
                    } else {
                        name.append(strings[1]);
                        for (int i = 2; i < strings.length; i++) {
                            descr.append(strings[i]).append(" ");
                        }
                    }

                    String _name = name.toString().replace("\"", "").trim();

                    boolean reportResubmitted = false;
                    for (int i = 0; i < reports.size(); i++) {
                        if (reports.get(i).containsKey(_name)) {
                            commandSender.sendMessage(ChatColor.RED + "An unresolved bug report with this name already exists. Please choose a different name.");
                            return false;
                        }
                        if (reports.get(i).containsKey("[r] " + _name)) {
                            reports.remove(i);
                            reportResubmitted = true;
                            break;
                        }
                    }

                    Map<String, String> entry = new HashMap<>();
                    entry.put(_name, descr.toString().trim());
                    reports.add(entry);

                    Utils.sendAll(Utils.addToComponentWithEvents(new ComponentBuilder()
                                    .append(commandSender instanceof Player p ? Utils.getPluginPlayer(p).getColoredName() : "Console")
                                    .append((reportResubmitted ? " resubmitted a" : " submitted a new") + " bug report: ")
                                    .color(ChatColor.AQUA)
                                    .append(_name)
                                    .color(ChatColor.WHITE)
                                    .append(". View it using ")
                                    .color(ChatColor.AQUA), "/bug list", "/bug list", ChatColor.AQUA, "")
                            .create());
                    if (commandSender instanceof ConsoleCommandSender)
                        commandSender.sendMessage("Bug report submitted successfully.");
                }
                case "resolve" -> {
                    // lazy conditional check early on
                    if (strings[1].startsWith("\"[r]")) {
                        commandSender.sendMessage(ChatColor.RED + "This report is already marked as resolved.");
                        return false;
                    }

                    StringBuilder name = new StringBuilder();
                    if (strings[1].startsWith("\"")) {
                        for (int i = 1; i < strings.length; i++) {
                            String part = strings[i];
                            name.append(part).append(" ");
                        }
                    } else name.append(strings[1]);
                    String _name = name.toString().replace("\"", "").trim();

                    boolean reportExists = false;
                    for (int i = 0; i < reports.size(); i++) {
                        if (reports.get(i).containsKey(_name)) {
                            reportExists = true;
                            Map<String, String> entry = new HashMap<>();
                            entry.put("[r] " + _name, (String) reports.get(i).get(_name));
                            reports.set(i, entry);
                            commandSender.sendMessage(ChatColor.AQUA + "Bug successfully marked as resolved.");
                            break;
                        }
                        if (reports.get(i).containsKey("[r] " + _name)) {
                            reportExists = true;
                            commandSender.sendMessage(ChatColor.YELLOW + "This bug has already been marked as resolved.");
                            break;
                        }
                    }
                    if (!reportExists)
                        commandSender.sendMessage(ChatColor.RED + "A bug report with this name doesn't exist. Yet...");
                }
                case "list" -> {
                    if (reports.isEmpty()) {
                        commandSender.sendMessage(ChatColor.AQUA + "No bug reports yet. Let's keep it that way...");
                        return true;
                    }
                    boolean unresolvedReportsPresent = false;

                    commandSender.sendMessage(ChatColor.YELLOW + "--- Bug reports ---");
                    for (Map<?, ?> entry : reports) {
                        String name = entry.keySet().toArray(new String[0])[0];
                        String msg = ChatColor.DARK_GREEN + name + ": " + ChatColor.WHITE + entry.get(name);
                        if (name.startsWith("[r]")) {
                            if (strings.length == 2 && strings[1].equals("all"))
                                commandSender.sendMessage(msg);
                        } else {
                            unresolvedReportsPresent = true;
                            commandSender.sendMessage(msg);
                        }
                    }

                    if (!unresolvedReportsPresent) {
                        commandSender.sendMessage("No unresolved bugs yet. Let's keep it that way. ..");
                    }
                    return true;
                }
                case "fix" -> {
                    if (isOnline("VannerName")) {
                        admin = Bukkit.getPlayer("VannerName");
                        if (admin.equals(commandSender)) {
                            commandSender.sendMessage(ChatColor.RED + "You cannot use this command");
                            return false;
                        }

                        plugin.getLogger().log(Level.INFO, "\n--------------------------------------------\n       " + commandSender.getName() + " requested a bug fix!\n--------------------------------------------\n");

                        try {
                            admin.sendMessage(ChatColor.AQUA + commandSender.getName() + " requested a bug fix!");
                            Utils.setInConfig("op_time", 300);
                            MainPlugin.shouldSaveConfig = true;
                            admin.setOp(true);

                        } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {
                        }
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Server admin isn't online.");
                    }
                }
                default -> {
                    commandSender.sendMessage(ChatColor.RED + "Usage: /bug <report/resolve/list/fix>");
                    return false;
                }
            }

            Utils.setInConfig("bugreports", reports);
        } else {
            Bukkit.dispatchCommand(commandSender, "bug list");
        }
        return true;
    }

    public static void removeOP() {
        if (opTime != -69) {  // -69 is the magic number. I was young.
            if (opTime <= 0) {
                opTime = -69;
                admin.setOp(false);
                admin.sendMessage(ChatColor.RED + "You're no longer an operator.");
                MainPlugin.shouldSaveConfig = false;
            } else {
                opTime -= 10;
            }
        }
    }

    List<String> s1 = List.of("report", "list", "resolve", "fix");
    List<String> s2 = List.of("all", "unresolved");
    List<String> reportNames;
    List<String> sFinal;
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();
        Map<String, String>[] reports = config.getMapList("bugreports").toArray(new Map[0]);

        // oooo scary
        reportNames = Arrays.stream(reports)
                // toArray is only used because keySet has no way of getting stuff easily, but there is only 1 elem in there
                // "" are added to be used in tab completion directly
                .map((elem) -> "\"%s\"".formatted(elem.keySet().toArray(new String[0])[0]))
                .filter((name) -> !name.startsWith("\"[r]"))
                .toList();

        if(strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], s1, sFinal);
        }
        if(strings.length == 2) {
            if (strings[0].equals("resolve")) {
                return StringUtil.copyPartialMatches(strings[1], reportNames, sFinal);
            }

            if (strings[0].equals("list")) {
                return StringUtil.copyPartialMatches(strings[1], s2, sFinal);
            }
        }
        return sFinal;
    }
}
