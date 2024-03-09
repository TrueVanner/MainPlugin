package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AFKSetter implements CommandExecutor, TabCompleter, Listener {
    private final MainPlugin plugin;

    public AFKSetter(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("afk").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Command can be performed by players only.");
            return false;
        }

        MainPluginPlayer.AFKing pAFK = Utils.getPluginPlayer(p).afking;

        if (strings.length >= 1) {
            switch (strings[0]) {
                case "start" -> pAFK.startAFK(false);
                case "stop" -> pAFK.endAFK();
                case "decline" -> pAFK.denyAFKRequest();
                default -> {
//                    if(strings[0].equals(p.getName())) {
//                        p.sendMessage(ChatColor.RED + "...Why?");
//                        return false;
//                    }

                    if(Utils.isOnline(strings[0])) {
                        MainPluginPlayer.AFKing toAFK = Utils.getPluginPlayer(strings[0]).afking;
                        if (toAFK.isAFK) {
                            p.sendMessage(ChatColor.RED + "The player is already AFK!");
                            return false;
                        }
                        toAFK.handleAFKRequest();
                    } else {
                        p.sendMessage(ChatColor.RED + "Usage: /afk [start/stop/*online player's name*]");
                    }
                }
            }
        } else {
            if (pAFK.isAFK) {
                pAFK.endAFK();
            } else
                pAFK.startAFK(false);
        }

        return true;
    }

    // eventually we all grow older... and more boring, apparently.
//    String[] not = {"Nope.", "Nahhhh bro", "Dude stopppp", "Not today!", "U sure u wanna do dis?", "Never gonna give up telling you to stop...", "Bip bop. Stop acting wrong. Bip bop."};
    String[] not = { "Nope.", "Forbidden.", "Please stop.", "Not today.", "You really shouldn't...", "Stop it!", "Bip bop. This action has been cancelled." };

    void warn(Cancellable c, Player p) {
        if (MainPluginPlayer.AFKing.isAFK(p)) {
            c.setCancelled(true);
            if(!p.isInWater()) { // literally only to stop spamming when player unsits in water
                Utils.onlyOnce(() -> {
                    p.sendMessage(ChatColor.ITALIC + not[(int) (Math.random() * not.length)]);
                }, "AFK warn message", plugin, 40L);
            }
        }
    }

//    @EventHandler
//    public void onMilk(PlayerItemConsumeEvent e) {
//        if (e.getItem().getType() == Material.MILK_BUCKET
//                || e.getItem().getType() == Material.GOLDEN_APPLE
//                || e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE
//                || e.getItem().getType() == Material.POTION) {
//            warn(e, e.getPlayer());
//        }
//    }

    @EventHandler
    public void onLiquidPlacement(PlayerInteractEvent e) {
        if(e.hasItem()) {
            Material type = e.getItem().getType();
            if (type.toString().contains("BUCKET") || type.toString().contains("POTION") || type.isEdible())
                warn(e, e.getPlayer());
        }
    }
    @EventHandler
    public void onEntityInteraction(PlayerInteractEntityEvent e) {
        warn(e, e.getPlayer());
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        warn(e, e.getPlayer());
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        warn(e, e.getPlayer());
    }
    @EventHandler
    public void onBowShooting(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            warn(e, (Player) e.getEntity());
        }
    }
    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        warn(e, e.getPlayer());
    }
    @EventHandler
    public void onLeash(PlayerLeashEntityEvent e) {
        warn(e, e.getPlayer());
    }
    @EventHandler
    public void onUnsitting(VehicleExitEvent e) {
        if (e.getExited() instanceof Player p && e.getVehicle() instanceof Pig pig && pig.getName().equals("temp_seat")) {
            warn(e, p);
        }
    }

    List<String> s1 = List.of("start","stop");
    List<String> sFinal;

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String lbl, String[] strings) {
        sFinal = new ArrayList<>();
        // easier this way
        Stream<String> playerNames = MainPlugin.pluginPlayers.keySet().stream().filter(playerName -> !playerName.equals(commandSender.getName()));

        new ArrayList<>();
        if(strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], Stream.concat(s1.stream(), playerNames).toList(), sFinal);
        }
        return sFinal;
    }
}