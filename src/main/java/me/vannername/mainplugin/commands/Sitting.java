package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Sitting implements CommandExecutor, Listener {
    private final MainPlugin plugin;
    public Sitting(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("sit").setExecutor(this);
        plugin.getCommand("dissapig").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equals("sit")) {
            if (commandSender instanceof Player p) {
                Utils.getPluginPlayer(p).sitting.sit(false);
                return true;
            } else
                commandSender.sendMessage(ChatColor.RED + "This command is for players only");
            return false;
        } else {
            List<Entity> brokenPigs = plugin.getServer().selectEntities(plugin.getServer().getConsoleSender(), "@e[name=\"temp_seat\",type=pig]");

            if(!brokenPigs.isEmpty()) {
                commandSender.sendMessage(brokenPigs.size() + " pigs found.");

                for(Entity e : brokenPigs) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, false, false, false));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, e::remove, 20L);
                }

                Utils.sendAll(ChatColor.GREEN + "All bugged seats were removed!");
            } else {
                commandSender.sendMessage(ChatColor.RED + "No pigs were found.");
            }
            return true;
        }
    }

    @EventHandler
    public void unsit(VehicleExitEvent e) {
        if(e.getExited() instanceof Player p) {
            MainPluginPlayer mpp = Utils.getPluginPlayer(p);
            // isSitting() doesn't work because the player will have already left the vehicle during the check
            if(e.getVehicle() instanceof Pig pig && pig.getCustomName().equals("temp_seat")) {
                e.setCancelled(true);
                if (!mpp.isAFK()) {
                    e.getVehicle().remove();
                    p.teleport(p.getLocation().add(0, 0.85, 0));
                    mpp.sitting.isSitting = false;
                }
            }
        }
    }
}
