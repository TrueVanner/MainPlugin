package me.vannername.mainplugin.listeners;

import jdk.jshell.execution.Util;
import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.commands.AFKAccounts;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.vannername.mainplugin.utils.Utils.*;

public class ImportantListeners implements Listener {
    public ImportantListeners(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        defaultConfigSetterFallback(p);

        MainPlugin.pluginPlayers.put(p.getName(), new MainPluginPlayer(p));

        AFKAccounts.init(p);
//        DiscordLogging.logPlayersChange(plugin);
    }

    // a now redundant code transformed into fallback in case the config.yml default values aren't properly set
    private void defaultConfigSetterFallback(Player p) {
        boolean isFirstTime = false;

        for(String param : new String[]{".senddeathcoords", ".coords.show", ".coords.coords", ".coords.pitch", ".coords.direction" }) {
            if(!config.contains(getPlayerConfigPath(p, param))) {
                isFirstTime = true;
                Utils.setInConfig(Utils.getPlayerConfigPath(p, param), true);
            }
        }
        if(!Utils.playerConfigContains(p, ".passive")) {
            isFirstTime = true;
            Utils.setInConfig(Utils.getPlayerConfigPath(p, "passive"), false);
        }
        if(!Utils.playerConfigContains(p, ".tab_color")) {
            isFirstTime = true;
            Utils.setInConfig(Utils.getPlayerConfigPath(p, ".tab_color"), "WHITE");
        }
        if(isFirstTime) {
            Utils.onlyOnce(() -> p.spigot().sendMessage(new ComponentBuilder()
                    .append("Welcome to the server! Sending death coordinates on death and showing coordinates above hotbar have been enabled by default. To configure or disable them, visit ")
                    .color(ChatColor.GREEN)
                    .append("the plugin's GUI.")
                    .underlined(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("/gui")))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gui"))
                    .create()
            ), "Welcome message", 20L);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Utils.getPluginPlayer(e.getPlayer()).sitting.unsit(); // has to be here, exception otherwise
        MainPlugin.pluginPlayers.remove(e.getPlayer().getName());
    }

    @EventHandler (ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player p) {
            Utils.getPluginPlayer(p.getName()).timeSinceDamage = System.currentTimeMillis();
        }
    }
}
