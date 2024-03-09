package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GetCoords implements CommandExecutor {
    public GetCoords(MainPlugin plugin) {
        plugin.getCommand("getcoords").setExecutor(this);
    }

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!(commandSender instanceof Player p)) {
                commandSender.sendMessage("Command can be performed by players only.");
                return true;
            }

            StringBuilder text = new StringBuilder();
            for (String str : strings) {
                text.append(str).append(" ");
            }

            Utils.sendAll(Utils.addNavLinkToComponent(new ComponentBuilder()
                    .append("Player ")
                    .color(ChatColor.DARK_GREEN)
                    .append(Utils.getPluginPlayer(p).getColoredName())
                    .append(" shared their location")
                    .color(ChatColor.DARK_GREEN)
                    .append((text.isEmpty() ? ": " : (" (\"" + text.toString().trim()) + "\"): "))
                    .color(ChatColor.DARK_GREEN),
                    p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ(), ChatColor.GRAY, "").create());
            return true;
        }

}
