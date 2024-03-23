package me.vannername.mainplugin.commands;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.MainPluginPlayer;
import me.vannername.mainplugin.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.SculkShrieker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Teleport implements CommandExecutor {

    public static class TPData {
        public Location from;
        public Location to;
        public boolean isToWorldSpawn;
        public double dist;
        public double cost; // could be int but is double because of future non-int divisions

        public TPData(Location from, Location to, boolean isToWorldSpawn, double dist, double cost) {
            this.from = from;
            this.to = to;
            this.isToWorldSpawn = isToWorldSpawn;
            this.dist = dist;
            this.cost = cost;
        }
    }
    public Teleport(MainPlugin plugin){
        plugin.getCommand("home").setExecutor(this);
    }

//    public static int totalXP, XPAmount;
//    public static boolean toBed = false;
//    public static double dist;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage("Command can be performed by players only.");
            return false;
        }

        MainPluginPlayer mpp = Utils.getPluginPlayer(p);
        TPData tpData = computeTPData(p);

        // a small work-around to prevent unreasonable teleports
        Utils.MeetsConditions result = checkTPConditions(mpp, tpData);
        if(!result.value) {
            p.sendMessage(result.expl);
        } else {
            teleport(p, tpData);
        }

        return true;
    }

    private static double calculateDistCoef(double dist) {
        // OLD VERSION
        // if (dist < 1000) return Math.pow(2, -0.0069*dist + 4.2) + 13.37;
        // return Math.pow(1.0000069, dist) + 13.37;


        // NEW VERSION
        if(dist < 3000) return Math.pow(1.25, -dist/500) + 0.25;
        else return (2 * Math.pow(dist + 1.05, 2)) / Math.pow(10, 9) + 0.49413; // was +0.5 before, reduced to remove gap
    }

    public static TPData computeTPData(Player p) {
        Location from = p.getLocation();
        Location to = p.getRespawnLocation();
        boolean isToWorldSpawn = false;
        double dist;

        if (to == null || from.getWorld() != to.getWorld()) {
            isToWorldSpawn = true;
            to = p.getWorld().getSpawnLocation();
        }

        dist = from.distance(to);

        // OLD VERSION
        // xpAmount = (int) Math.ceil(0.01337 * dist * calculateDistCoef(dist)) - 10;

        // NEV VERSION
        double cost = Math.round(0.1 * dist * calculateDistCoef(dist));
        return new TPData(from, to, isToWorldSpawn, dist, cost);
    }

    // boolean + explanation why
    public static Utils.MeetsConditions checkTPConditions(MainPluginPlayer mpp, TPData tpData) {
        if(mpp.takenDamageRecently()) {
            return new Utils.MeetsConditions("Can't teleport right after taking damage!");
        }
        if(mpp.isAFK()) {
            return new Utils.MeetsConditions( "Can't teleport while AFK!");
        }
        if(mpp.isInAir()) {
            return new Utils.MeetsConditions("Can't teleport while in the air!");
        }
        if(mpp.badHostilesNearby()) {
            return new Utils.MeetsConditions("You can't go to bed now, there are monsters nearby!");
        }
        if(mpp.hasTriggerredWarden()) {
            return new Utils.MeetsConditions("It's already too late...");
        }
        if(mpp.getTotalXP() < tpData.cost) {
            return new Utils.MeetsConditions("Not enough XP! Required: %1$d, you have: %2$d".formatted((int) tpData.cost, mpp.getTotalXP()));
        }
        if(mpp.getTotalXP() == 0) {
            return new Utils.MeetsConditions("Not enough XP!");
        }
        if(AFKAccounts.isAFKAccount(mpp.p)) {
            return new Utils.MeetsConditions("AFK accounts can't teleport.");
        }
        if(tpData.to.distance(tpData.from) <= 10) {
            return new Utils.MeetsConditions("You are already there!");
        }

        return new Utils.MeetsConditions();
    }

    public static boolean justLuckyTeleported = false;
    public static void teleport(Player p, TPData tpData) {
        p.spawnParticle(Particle.PORTAL, p.getLocation(), 1000,1, 1, 1, 1);

        if(tpData.isToWorldSpawn) {
            p.sendMessage(ChatColor.RED + "Your spawn location is not set in this world, so you were sent to spawn.");
        }
        p.teleport(tpData.to);
        p.giveExp((int) -tpData.cost);
        p.spawnParticle(Particle.PORTAL, p.getLocation(), 1000,1, 1, 1, 1);

        if(Utils.randomChance(1)) {
            Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation().add(0, 0, 0), EntityType.FIREWORK);
            fw.setVelocity(new Vector(0, 0.1, 0));

            FireworkMeta meta = fw.getFireworkMeta();

            meta.addEffects(
                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.RED, Color.BLUE, Color.GREEN, Color.FUCHSIA, Color.YELLOW, Color.WHITE)
                            .flicker(true)
                            .trail(true)
                            .withFade(Color.WHITE)
                            .build(),
                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.STAR)
                            .withColor(Color.YELLOW, Color.FUCHSIA, Color.AQUA)
                            .flicker(true)
                            .trail(true)
                            .withFade(Color.WHITE)
                            .build());
            meta.setPower(1);
            fw.setFireworkMeta(meta);
            justLuckyTeleported = true;

            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.6F, 1.0F);
            Bukkit.broadcastMessage(ChatColor.AQUA + p.getName() + ChatColor.GOLD + " is lucky today!");
        }

        p.sendMessage(ChatColor.GREEN+"You're here!");
    }

    public static void teleport(Player p) {
        teleport(p, computeTPData(p));
    }
}

