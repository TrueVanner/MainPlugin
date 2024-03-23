package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessage implements Listener {
    private final MainPlugin plugin;

    public DeathMessage(MainPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Utils.onlyOnce(() -> {
            Player p = e.getEntity();

            if (Utils.getPluginPlayer(p).getFromConfig(".senddeathcoords", true)) {
                String coords = p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ();
                p.spigot().sendMessage(Utils.addNavLinkToComponent(
                        new ComponentBuilder().append("You died at: ").color(ChatColor.DARK_RED), coords, ChatColor.GRAY, "").create()
                );

                if (Utils.randomChance(15)) {
                    String dm = e.getDeathMessage();
                    String res = getJokeString(dm, p);

                    if (!res.isEmpty()) Bukkit.broadcastMessage(res);
                }
            }
        }, "Death message", 60L);
    }

    private String getJokeString(String dm, Player p) {
        if (dm == null) return "";
        String res = "";
        if (dm.contains("by Setredid") || dm.contains("was killed trying to hurt Setredid"))
            res = "Setredid won this PVP duel!";
        else if (dm.contains("by Vannername") || dm.contains("was killed trying to hurt Vannername"))
            res = "Vannername won this PVP duel!";
        else if (dm.contains("experienced kinetic energy"))
            res = "oof";
        else if (dm.contains("was blown up by Creeper"))
            res = "CREEPER? AWW MAN";
//                    else if (dm.contains("fell from a high place") || dm.contains("hit the ground too hard"))
//                        res = p.getName() + ": Well, off to visit your mother! Woop!";
        else if (dm.contains("fell out of the world"))
            res = p.getName() + ": aaaaaaaaaaAAAAAAAAAAAAAAAAAAAAA" + ChatColor.BOLD + "AAAAAAAAAAAAAAAAA";
        else if (dm.contains("was skewered by a falling stalactite"))
            res = p.getName() + ": Wait, those can fall?";
        else if (dm.contains("lava") || dm.contains("suffocated in a wall"))
            res = "F";
        return res;
    }
}
