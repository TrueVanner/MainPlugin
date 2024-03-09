package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class MyReload implements CommandExecutor, Listener {
    private final MainPlugin plugin;

    public MyReload(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("myreload").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

//    @EventHandler
//    public void onReloadCommand(ServerCommandEvent e) {
//        if(e.getCommand().startsWith("reload")) {
//            e.getSender().sendMessage("This command is banned. Please use /myreload instead.");
//            e.setCancelled(true);
//        }
//    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        plugin.getConfig().set("no_discord_notifications", true);
        plugin.saveConfig();
        plugin.getServer().reload();

        return true;
    }
}
