package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;

public class Navigate implements CommandExecutor, Listener, TabCompleter {
    private final MainPlugin plugin;

    public Navigate(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("navigate").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Command can be performed by players only.");
            return true;
        }

        MainPluginPlayer.Navigating pn = Utils.getPluginPlayer(p).navigating;

        if (!strings[0].equals("stop")) {
            try {
                if(strings.length == 2) strings = new String[]{ strings[0], "~", strings[1] };

                Location l = p.getLocation();

                if (strings[0].startsWith("~"))
                    strings[0] = strings[0].length() > 1 ? String.valueOf(l.getBlockX() + Integer.parseInt(strings[0].substring(1))) : String.valueOf(l.getBlockX());

                if (strings[1].startsWith("~"))
                    strings[1] = strings[1].length() > 1 ? String.valueOf(l.getBlockY() + Integer.parseInt(strings[1].substring(1))) : String.valueOf(l.getBlockY());

                if (strings[2].startsWith("~"))
                    strings[2] = strings[2].length() > 1 ? String.valueOf(l.getBlockZ() + Integer.parseInt(strings[2].substring(1))) : String.valueOf(l.getBlockZ());

                pn.startNavigation(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), strings.length >= 4 && strings[3].equalsIgnoreCase("direct"));
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED+"/navigate (/nav) [x, y, z] <direct?>");
                return false;
            }
        } else pn.stopNavigation();

        return true;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Utils.onlyOnce(() -> {
            Utils.getPluginPlayer(e.getPlayer()).navigating.stopNavigation();
        }, "Stop navigation", plugin, 20L);
    }

    @EventHandler
    public void onCompassRemoveAttempt(PlayerDropItemEvent e) {
        try {
            ItemStack i = e.getItemDrop().getItemStack();
            if (i.getType().equals(Material.COMPASS) && Objects.requireNonNull(i.getItemMeta()).getCustomModelData() == 0) {
                e.setCancelled(true);
            }
        } catch (NullPointerException ignored) {}
    }

    List<String> s1 = new ArrayList<>(Arrays.asList("stop"));
    List<String> sFinal;
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();
        s1.addAll(ServerNotes.getAllNotesCoords());

        if (strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], s1, sFinal);
        }
        if(strings.length == 4) {
            return StringUtil.copyPartialMatches(strings[3], Collections.singleton("direct"), sFinal);
        }
        return sFinal;
    }
}