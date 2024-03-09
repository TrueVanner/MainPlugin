package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class MyRestart implements CommandExecutor, Listener {
    private final MainPlugin plugin;

    public MyRestart(MainPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("myrestart").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


//    @EventHandler
//    public void onReloadCommand(ServerCommandEvent e) {
//        if(e.getCommand().startsWith("restart")) {
//            e.getSender().sendMessage("This command is banned. Please use /myrestart instead.");
//            e.setCancelled(true);
//        }
//    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        plugin.getConfig().set("no_discord_notifications", true);
        plugin.saveConfig();
        commandSender.getServer().dispatchCommand(commandSender, "restart");

        return true;
    }
}
