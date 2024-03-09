package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.commands.Bug;
import me.vannername.mainplugin.commands.Teleport;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.vannername.mainplugin.utils.Utils.plugin;

public class OtherListeners implements Listener {
    public OtherListeners(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // needed to function in lambdas.
    Player player;
    String msg;
    @EventHandler
    public void pingPlayers(AsyncPlayerChatEvent e) {

        if (!e.getMessage().startsWith("/")) {
            if (e.getMessage().contains("@")) {
                Utils.onlyOnce(() -> {
                    msg = e.getMessage();
                    player = null;
                    if (msg.indexOf("@") != msg.lastIndexOf("@")) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.RED + "Your message wasn't sent because it contained more than one ping");
                        return;
                    }
                    String name = msg.substring(msg.indexOf("@") + 1).split(" ")[0];
                    if (name.equals("everyone")) {
                        e.setMessage(msg.replaceFirst("@", "").replaceFirst(name, String.valueOf(ChatColor.BLUE) + ChatColor.BOLD + ChatColor.ITALIC + "everyone" + ChatColor.RESET));
                        for (Player p : e.getPlayer().getServer().getOnlinePlayers()) {
                            if (!p.getName().contains("_AFK")) {
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.9F);
                            }
                        }
                    } else {
                        try {
                            if (name.length() > 5) player = Bukkit.getPlayer(name);
                            if (player != null) {
                                e.setMessage(msg.replaceFirst("@", "").replaceFirst(name, Utils.getPluginPlayer(player).getColoredName()));
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.9F);
                        } catch (NullPointerException ignored) {
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(ChatColor.RED + "Your message wasn't send because the player mentioned in your pings is offline or does not exist");
                        }
                    }
                }, "Pinging", plugin, 40L);
            }
        }
    }

    @EventHandler
    public void onKYS(AsyncPlayerChatEvent e) {
        if (!e.getMessage().startsWith("/")) {
            if(e.getMessage().equalsIgnoreCase("kys")) {
                if(Utils.randomChance(20)) {
                    e.getPlayer().getServer().getOnlinePlayers().forEach(p -> {
                        // only works if the server contains a resource pack with a sound tagged like this
                        p.playSound(p, "custom.motivationalspeech", 1, 1);
                    });
                }
            }
        }
    }

    @EventHandler
    public void onLuckyFireworkDamage(EntityDamageEvent e) {
        if(Teleport.justLuckyTeleported) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Teleport.justLuckyTeleported = false;
                }, 40L);
            }
        }
//        if (e.getDamager() instanceof Firework) {
//            Utils.sendAll("detected2");
//            Firework fw = (Firework) e.getDamager();
//            Utils.sendAll(fw.getMetadata("nodamage"));
//            if (fw.hasMetadata("nodamage")) {
//                e.setCancelled(true);
//            }
//        }
    }

    private Location findSolidBlock(Location origin, int[] range) {
        // range: x, y, z
        // -x/2 to x/2, 0 to y, -z/2 to z/2
        for(int y = origin.getBlockY(); y < origin.getBlockY() + range[1]; y++) {
            for(int x = origin.getBlockX() - range[0]/2; x < origin.getBlockX() + range[0]/2; x++) {
                for(int z = origin.getBlockZ() - range[2]/2; z < origin.getBlockZ() + range[2]/2; z++) {
                    Block block = origin.getWorld().getBlockAt(x, y, z);
                    if(!block.isEmpty()) return adjustHeight(block.getLocation());
                }
            }
        }
        return origin.add(0, range[1], 0);
    }

    private Location adjustHeight(Location origin) {
        while(!origin.getBlock().isEmpty()) {
            origin = origin.add(0, 1, 0);
        }
        return origin;
    }

    // protect the player who has high ping and fell into the void
    @EventHandler
    public void onVoidDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player p && e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            MainPluginPlayer mpp = Utils.getPluginPlayer(p);
            if(mpp.protectedFromHighPing()) {
                e.setCancelled(true);
                p.teleport(p.getLocation().add(0, 50, 0)); // to make sure checks aren't re-run again if player
                // falls into the void faster than the server can calculate position
                p.teleport(findSolidBlock(p.getLocation(), new int[]{10, 50, 10}));
                mpp.afking.startAFK(true);
                p.sendMessage(ChatColor.YELLOW + "Warning: teleported you higher up because you took void damage while under high ping protection.");
            }
        }
    }

    // makes each player drop 20% of their total XP on death instead of just 100.
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDroppedExp(e.getDroppedExp() < 100 ? e.getDroppedExp() : (int) (Utils.getPluginPlayer(e.getEntity()).getTotalXP() * 0.2));
    }

    @EventHandler
    public void coordsInChat(AsyncPlayerChatEvent e) {
        Utils.onlyOnce(() -> {
//            Pattern pt = Pattern.compile("(-*[0-9]+ (?:-*[0-9]+ )*-*[0-9]+)"); // detects 2-3 coordinates and no ~'s
            Pattern pt = Pattern.compile("((?:~?-?[0-9]+|~) (?:(?:~?-?[0-9]+|~) )?(?:~?-?[0-9]+|~))"); // detects 2-3 coordinates but accepts ~'s and ~n's.
            Matcher matcher = pt.matcher(e.getMessage());
            String name = Utils.getPluginPlayer(e.getPlayer()).getColoredName();

            if(matcher.find()) {
                e.setCancelled(true);

                String coords = matcher.group(1);
                String[] _msg = e.getMessage().split(coords);

                // holy fuck am i actually building the message from fucking scratch
                ComponentBuilder res = new ComponentBuilder().append("<").append(name).append("> ").reset();

                // looks like I'm splitting the message around coordinates and handle it differently based on that?
                if(_msg.length != 0) {
                    if(!e.getMessage().endsWith(coords)) {
                        if(_msg.length != 1) {
                            res.append(_msg[0]);
                            _msg[0] = _msg[1];
                        }
                        Utils.sendAll(Utils.addNavLinkToComponent(res, coords, ChatColor.WHITE, "").append("").reset().append(_msg[0]).create());
                    } else
                        Utils.sendAll(Utils.addNavLinkToComponent(res.append(_msg[0]), coords, ChatColor.WHITE, "").create());
                } else
                    Utils.sendAll(Utils.addNavLinkToComponent(res, coords, ChatColor.WHITE, "").create());
                plugin.getLogger().log(Level.FINE, "<" + name + ChatColor.RESET + "> " + e.getMessage());
            }
        }, "Edit player message", plugin, 20L);
    }
}