package me.vannername.mainplugin;

import me.vannername.mainplugin.commands.*;
import me.vannername.mainplugin.gui.GUI_MAIN;
import me.vannername.mainplugin.gui.InventoryClickListener;
import me.vannername.mainplugin.listeners.*;
import me.vannername.mainplugin.recipes.BasicRecipes;
import me.vannername.mainplugin.recipes.RecoloringRecipes;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

import static me.vannername.mainplugin.utils.Utils.particleBorder;

public final class MainPlugin extends JavaPlugin {
    public static HashMap<String, MainPluginPlayer> pluginPlayers = new HashMap<>();
    @Override
    public void onEnable() {
        saveDefaultConfig();

        // INIT MAINPLUGINPLAYERS
        for (Player p : getServer().getOnlinePlayers()) {
            pluginPlayers.put(p.getName(), new MainPluginPlayer(p));
            AFKAccounts.init(p);
        }

        // BORDER
        final int borderOverworld = getConfig().getInt("border_overworld");
        final int borderNether = getConfig().getInt("border_nether");
        if(borderOverworld > 0 || borderNether > 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for (Player p : getServer().getOnlinePlayers()) {
                    if(borderOverworld > 0) {
                        particleBorder(p, getServer().getWorld("world"), borderOverworld);
                    }
                    if(borderNether > 0) {
                        particleBorder(p, getServer().getWorld("world_nether"), borderNether);
                    }
                }
            }, 0, 1L);
        }

        // EVERY 1s
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : getServer().getOnlinePlayers()) {
                ItemFrameChanges.tempGlowOnInvisible(p, this);
            }

            DayNightSkipper.skipDayNight(this);
        }, 0L, 20L);

        // EVERY 10s
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bug.removeOP();
            ScheduleStop.progressShutdown();
        }, 0L, 10*20L);

//        new AFKSetter(this);
        new AFKAccounts(this);
        new EnderChestOpener(this);
        new GetCoords(this);
        new InventoryClickListener(this);
        new MyReload(this);
        new MyRestart(this);
        new OpenGUI(this);
        new ServerChest(this);
        new SetPassive(this);
        new Teleport(this);
//        new ServerNotes(this);
//        new DayNightSkipper(this);
//        new Navigate(this);
//        new Bug(this);
//        new ScheduleStop(this);
//        new Ping(this);

        new ImportantListeners(this);
        new DamageCanceling(this);
        new DeathMessage(this);
        new ItemFrameChanges(this);
        new Sitting(this);
        new OtherListeners(this);

        new BasicRecipes(this);
        new RecoloringRecipes(this);

        getCommand("afk").setTabCompleter(new AFKSetter(this));
        getCommand("skipnight").setTabCompleter(new DayNightSkipper(this));
        getCommand("skipday").setTabCompleter(new DayNightSkipper(this));
        getCommand("servernotes").setTabCompleter(new ServerNotes(this));
        getCommand("navigate").setTabCompleter(new Navigate(this));
        getCommand("bug").setTabCompleter(new Bug(this));
        getCommand("schedulestop").setTabCompleter(new ScheduleStop(this));
        getCommand("ping").setTabCompleter(new Ping(this));
        getCommand("calculate").setTabCompleter(new Calculate(this));

        GUI_MAIN.init();

        // LOAD CRITICAL VALS FROM CONFIG
        ScheduleStop.shutdown = getConfig().getInt("shutdown");
        Bug.opTime = getConfig().getInt("op_time");

//        getConfig().set("no_discord_notifications", true);
        saveConfig();
    }
    @Override
    public void onDisable() {
        getConfig().set("shutdown", ScheduleStop.shutdown);
        getConfig().set("op_time", Bug.opTime);
        saveConfig();
    }
}
