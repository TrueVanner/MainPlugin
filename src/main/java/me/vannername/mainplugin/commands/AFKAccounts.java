package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AFKAccounts implements CommandExecutor, Listener {

    public AFKAccounts(MainPlugin plugin) {
        plugin.getCommand("moveafk").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static boolean isAFKAccount(String playerName) {
        return playerName.contains("_AFK");
    }
    public static boolean isAFKAccount(Player p) {
        return p.getName().contains("_AFK");
    }

    public static void init(Player p) {
        if (isAFKAccount(p.getName())) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, -1, 255, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 255, false, false));
            p.setGameMode(GameMode.ADVENTURE);
            p.setInvulnerable(true);
            p.setSleepingIgnored(false);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            return false;
        }

        try {
            if (!Bukkit.getPlayer(p.getName() + "_AFK").teleport(p)) {
                p.sendMessage(ChatColor.RED + "Something went wrong!");
                return false;
            }
        } catch (NullPointerException e) {
            p.sendMessage(ChatColor.RED + "Your AFK account isn't online");
            return false;
        }
        return true;
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getName().contains("_AFK")) {
            if (e.getTo().getChunk() != e.getPlayer().getWorld().getChunkAt(e.getFrom())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the borders of your chunk!");
            }
        }
    }
}
