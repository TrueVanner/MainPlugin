package me.vannername.mainplugin.utils;

import me.vannername.mainplugin.commands.AFKAccounts;
import me.vannername.mainplugin.events.PlayerEnterAFKEvent;
import me.vannername.mainplugin.events.PlayerLeaveAFKEvent;
import me.vannername.mainplugin.listeners.DamageCanceling;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static me.vannername.mainplugin.utils.Utils.*;

public class MainPluginPlayer {
    public Player p;
    public AFKing afking;
    public Navigating navigating;
    public final DisplayCoordinates displayCoordinates;
    public final Sitting sitting;
    public final PingRecording pingRecording;
    public boolean isPassive;
    public long timeSinceDamage = -1;

    public ChatColor color;
    public ArrayList<Integer> pingLog = new ArrayList<>(Arrays.asList(0, 0, 0));

    public MainPluginPlayer(Player p) {
        this.p = p;
        if(!AFKAccounts.isAFKAccount(p)) {
            this.afking = new AFKing();
            this.navigating = new Navigating();
        }
        this.displayCoordinates = new DisplayCoordinates();
        this.sitting = new Sitting();
        this.pingRecording = new PingRecording();
        this.isPassive = isPassive();
        loadTabColor();
    }
    public String getColoredName() {
        return color + p.getName() + ChatColor.RESET;
    }
    public void loadTabColor() {
        try {
            color = ChatColor.of(getFromConfig(".tab_color", ""));
        } catch (NullPointerException e) {
            color = ChatColor.WHITE;
        }
        p.setDisplayName(color + p.getName() + ChatColor.RESET);
        p.setPlayerListName(color + p.getName() + ChatColor.RESET);
    }

    public void setHotbarCoordinatesColor(String type, String color) {
        String property = null;
        switch (type) {
            case "LC" -> property = ".letter_color";
            case "CC" -> property = ".coords_color";
        }
        setInConfig(property, color);
        displayCoordinates.loadColors();
    }

    public int getTotalXP() {
        return Utils.levelToXP(p.getLevel(), p.getExp());
    }

    public boolean takenDamageRecently() {
        if(timeSinceDamage != -1) {
            return (System.currentTimeMillis() - timeSinceDamage) / 1000 < 5;
        }
        return false;
    }

    public boolean badHostilesNearby() {
        for(Entity e : p.getNearbyEntities(50, 10, 50)) {
            if(DamageCanceling.badHostiles.contains(e.getType())) return true;
        }
        return false;
    }

    public boolean isInAir() {
        return p.getLocation().getBlock().getType() == Material.AIR && p.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR;
    }
    public boolean isSitting() {
        return sitting.isSitting;
    }

    public boolean isAFK() {
        return afking.isAFK;
    }

    public boolean protectedFromHighPing() {
        return pingRecording.isProtected;
    }

    public boolean isPassive() {
        try {
            return getFromConfig(".passive", true);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void invertPassive() {
        if(config.contains(getConfigPath(".passive"))) {
            setInConfig(".passive", !getFromConfig(".passive", true));
            isPassive = !isPassive;
        } else {
            setInConfig(".passive", true);
            isPassive = true;
        }
    }

    public void setInConfig(String property, Object value) {
        Utils.setInConfig(getConfigPath(property), value);
    }

    private String getConfigPath(String property) {
        return "Users." + p.getName() + property;
    }

    public <T> T getFromConfig(String property, T type) {
        String path = getConfigPath(property);
        if(config.contains(path)) {
            return (T) config.get(path);
        } else throw new NullPointerException("No property found");
    }

    public class AFKing {
        private int AFKRequestTaskID = -1;

        public boolean isAFK = false;

        public AFKing() {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::keepAFK, 0L, 20L);
        }

        public MeetsConditions checkCanAFK(boolean force) {
            if(force) return new MeetsConditions();

            if(isAFK) {
                return new MeetsConditions("You're already AFK.");
            }

            if(AFKAccounts.isAFKAccount(p)) {
                return new MeetsConditions("AFK accounts can't go AFK.");
            }

            if(isInAir()) {
                return new MeetsConditions("Can't go AFK in the air!");
            }

            if (takenDamageRecently()) {
                return new MeetsConditions("Can't go AFK right after taking damage");
            }

            if(badHostilesNearby()) {
                return new MeetsConditions("You can't rest now, there are monsters nearby!");
            }

            return new MeetsConditions();
        }

        public void startAFK(boolean force) {
            MeetsConditions result = checkCanAFK(force);
            if(!result.value) {
                p.sendMessage(result.expl);
                return;
            }

            isAFK = true;

            sitting.unsit();
            Location loc = /* sitting.isSitting ? p.getLocation().add(0, 0.55, 0) : */p.getLocation();

            p.setGameMode(GameMode.SPECTATOR);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                p.setGameMode(GameMode.SURVIVAL);

                p.teleport(loc);
                p.setInvulnerable(true);

//                if (!isAFK) {
                p.setPlayerListName(ChatColor.RED + p.getName() + " (AFK) " + ChatColor.RESET);
                p.setDisplayName(ChatColor.RED + p.getName() + " (AFK)" + ChatColor.RESET);
//                }
            }, 5L);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> sitting.sit(force), 7L);

            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 255, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 255, false, false));


            Bukkit.getPluginManager().callEvent(new PlayerEnterAFKEvent(p.getName()));
            Bukkit.broadcastMessage(getColoredName() + ChatColor.GREEN +  " went AFK.");
            p.sendTitle(ChatColor.GOLD + "You went AFK!","",10,20,10);
        }

        public void endAFK() {
            p.setInvulnerable(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            p.removePotionEffect(PotionEffectType.WEAKNESS);

            p.setDisplayName(getColoredName());
            p.setPlayerListName(getColoredName());

//        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,() -> Sitting.unsit(p),5L); // to make sure that the player unsits for sure
            sitting.unsit();
            p.teleport(p.getLocation().add(0, 0.3, 0));

            isAFK = false;
            Bukkit.broadcastMessage(getColoredName() + ChatColor.GREEN + " returned from AFK.");
            Bukkit.getPluginManager().callEvent(new PlayerLeaveAFKEvent(p.getName()));
            p.sendTitle(ChatColor.GREEN + "Have fun!","",10,20,10);
        }

        public void keepAFK() {
            if (isAFK) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 255, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 60, 255, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 255, false, false));
            } else if (p.isInvulnerable()) {
                // break AFK
                p.setInvulnerable(false);
                p.leaveVehicle();
            }
        }

        public void handleAFKRequest() {
            if (AFKRequestTaskID == -1) {
                // using raw ComponentBuilder instead of Utils one because action is not SUGGEST_COMMAND
                p.spigot().sendMessage(
                        new ComponentBuilder()
                                .append("Player ")
                                .color(ChatColor.YELLOW)
                                .append(getColoredName())
                                .append(" wants to force you into AFK. ")
                                .color(ChatColor.YELLOW)
                                .append("Not okay with this? You have 10 seconds to reply.")
                                .underlined(true)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Decline AFK request")))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afk decline"))
                                .create()
                );
                AFKRequestTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    startAFK(true);
                    AFKRequestTaskID = -1;
                }, 10 * 20L);
            } else {
                p.sendMessage(ChatColor.RED + "A pending AFK request has already been sent to the player.");
            }
        }

        public void denyAFKRequest() {
            if(AFKRequestTaskID != -1) {
                Bukkit.getScheduler().cancelTask(AFKRequestTaskID);
                AFKRequestTaskID = -1;
                p.sendMessage(ChatColor.GREEN + "Successfully declined AFK request.");
            } else {
                p.sendMessage(ChatColor.RED + "No AFK requests to decline.");
            }
        }

        public static boolean isAFK(Player p) {
            return p.getDisplayName().contains("(AFK)");
        }
        public static boolean isAFK(String playerDisplayName) {
            return playerDisplayName.contains("(AFK)");
        }
    }

    public class Navigating {
        public Location to;
        public boolean isNavigating;
        public boolean direct;

        public Navigating() {
            loadData();
        }

        private void loadData() {
            isNavigating = isNavigating();
            if (isNavigating) {
                String pathBase = getConfigPath(".navigating");
                to = new Location(p.getWorld(), config.getDouble(pathBase + ".navX"), config.getDouble(pathBase + ".navY"), config.getDouble(pathBase + ".navZ"));
            }
        }

        private void saveData() {
            String pathBase = ".navigating";
            config.set(getConfigPath(pathBase + ".navigating"), isNavigating);
            config.set(getConfigPath(pathBase + ".navX"), to.getBlockX());
            config.set(getConfigPath(pathBase + ".navY"), to.getBlockY());
            config.set(getConfigPath(pathBase + ".navZ"), to.getBlockZ());
            plugin.saveConfig();
        }

        public boolean isNavigating() {
            try {
                return getFromConfig(".navigating.navigating", true);
            } catch (NullPointerException e) {
                return false;
            }
        }
        public Location whereTo() { return to; }

        public void startNavigation(int x, int y, int z, boolean direct) {
            to = new Location(p.getWorld(), x, y, z);
            if (to.distance(p.getLocation()) <= 5 && !direct) {
                p.sendMessage(ChatColor.RED + "You're already there!");
                return;
            }

            isNavigating = true;
            this.direct = direct;
            saveData();

            removeCompass();

            p.sendMessage(ChatColor.AQUA + "Distance to location: " + ChatColor.GREEN + (int) Math.round(p.getLocation().distance(to)) + ChatColor.AQUA + " blocks");
            setCompass();

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::checkReachedDest, 0L, 2L);
        }

        public void stopNavigation() {
            if(isNavigating) {
                isNavigating = false;
                saveData();
                p.sendMessage(ChatColor.AQUA + "Navigation aborted.");
                removeCompass();
            }
        }

        public void checkReachedDest() {
            if (isNavigating) {
                if (whereTo().distance(p.getLocation()) < (direct ? 1 : 5)) {
                    stopNavigation();
                }
            }
        }

        public BaseComponent[] getNavText(ChatColor LC, ChatColor CC) {
            Location l = p.getLocation();
            int x = Math.abs(l.getBlockX() - to.getBlockX());
            int y = Math.abs(l.getBlockY() - to.getBlockY());
            int z = Math.abs(l.getBlockZ() - to.getBlockZ());

            char elev;
            if (l.getBlockY() > to.getBlockY()) {
                elev = '▼';
            } else if (l.getBlockY() < to.getBlockY()) {
                elev = '▲';
            } else {
                elev = '•';
            }

            return new ComponentBuilder()
                    .append("Blocks left = X: ").color(LC)
                    .append("" + x).color(CC)
                    .append(" Y: ").color(LC)
                    .append("" + y).color(CC)
                    .append(" Z: ").color(LC)
                    .append("" + z).color(CC)
                    .append(" (").color(LC)
                    .append("" + elev).color(CC)
                    .append(")").color(LC).create();
        }

        private boolean isNavCompass(ItemStack item) { return item.getType() == Material.COMPASS && Objects.requireNonNull(item.getItemMeta()).getCustomModelData() == 0; }

        private void setCompass() {
            ItemStack inOffHand = p.getInventory().getItemInOffHand();

            try {
                if (!isNavCompass(inOffHand)) {
                    if (p.getInventory().firstEmpty() != -1)
                        p.getInventory().addItem(inOffHand);
                    else
                        p.getWorld().dropItem(p.getLocation(), inOffHand);
                }
            } catch (NullPointerException | IllegalStateException ignored) {
                p.getWorld().dropItem(p.getLocation(), inOffHand);
            } catch (IllegalArgumentException ignored) {}

            p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            p.getInventory().setItemInOffHand(new ItemStack(Material.COMPASS));

            CompassMeta meta = (CompassMeta) p.getInventory().getItemInOffHand().getItemMeta();
            meta.setLodestoneTracked(false);
            meta.setLodestone(to);
            meta.setDisplayName(ChatColor.DARK_PURPLE + "(Temporary compass. Used for navigation.)");
            meta.setCustomModelData(0);
            p.getInventory().getItemInOffHand().setItemMeta(meta);
        }


        // remark: while the code will indeed run more times than intended
        // every time navigation is stopped, if it's not repeated then it won't
        // detect and remove compasses stacked together.
        private void removeCompass() {
            try {
                if (isNavCompass(p.getInventory().getItemInOffHand())) {
                    p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                }
            } catch (NullPointerException | IllegalStateException ignored) {}

            for (ItemStack i : p.getInventory().getContents()) {
                try {
                    if (isNavCompass(i)) {
                        p.getInventory().remove(i);
                    }
                } catch (NullPointerException | IllegalStateException ignored) {}
            }
        }
    }

    public class DisplayCoordinates {
        ChatColor LC;
        ChatColor CC;
        boolean shouldDisplay;
        boolean showCoords;
        boolean showPitch;
        boolean showDirection;

        public DisplayCoordinates() {
            loadColors();
            loadParams();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::display, 0L, 1L);
        }

        public void loadColors() {
            try {
                LC = ChatColor.of(getFromConfig(".letter_color", ""));
                CC = ChatColor.of(getFromConfig(".coords_color", ""));
            } catch (NullPointerException e) {
                LC = ChatColor.GREEN;
                CC = ChatColor.YELLOW;
            }
        }

        public void loadParams() {
            String pathBase = getConfigPath(".coords");
            this.shouldDisplay = config.getBoolean(pathBase + ".show");
            this.showCoords = config.getBoolean(pathBase + ".coords");
            this.showPitch = config.getBoolean(pathBase + ".pitch");
            this.showDirection = config.getBoolean(pathBase + ".direction");
        }
        public String getPitchText(float pitch) {
            // wanted to do something cool, it failed, decided not to change
            // anything and keep it a useless function
            return String.valueOf(Math.floor(pitch)).replace(".0", "") + "°";
        }
        public void display() {

            if (shouldDisplay) {
                if (!AFKing.isAFK(p)) {
                    ComponentBuilder toSend = new ComponentBuilder();

                    if (navigating.isNavigating) {
                        toSend.append(navigating.getNavText(LC, CC));
                    }
                    else if (showCoords) {
                        toSend.append("X: ").color(LC)
                                .append("" + p.getLocation().getBlockX()).color(CC)
                                .append(" Y: ").color(LC)
                                .append("" + p.getLocation().getBlockY()).color(CC)
                                .append(" Z: ").color(LC)
                                .append("" + p.getLocation().getBlockZ()).color(CC);
                    }

                    if (showPitch) {
                        toSend.append(" Pitch: ")
                                .color(LC)
                                .append(getPitchText(p.getLocation().getPitch()))
                                .color(CC);
                    }

                    if (showDirection) {
                        toSend.append(" Direction: ")
                                .color(LC)
                                .append(Utils.direction(p.getLocation().getYaw()))
                                .color(CC);
                    }

                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, toSend.create());
                }
            }
        }
    }

    public class Sitting {
        public boolean isSitting;

        public Sitting() {
            this.isSitting = isSitting(p);
        }

        public MeetsConditions canSit(boolean force) {
            if(force) return new MeetsConditions();

            if(isInAir()) {
                return new MeetsConditions("You can't sit in the air!");
            }
            if(isSitting) {
                return new MeetsConditions("You're already sitting!");
            }

            return new MeetsConditions();
        }

        public void sit(boolean force) {
            MeetsConditions result = canSit(force);
            if(!result.value) {
                p.sendMessage(result.expl);
                return;
            }

            if(isSitting && force) {
                unsit();
            }

            LivingEntity pig = (LivingEntity) p.getWorld().spawnEntity(p.getLocation().add(0, -0.9, 0), EntityType.PIG);

            pig.setInvisible(true);
            pig.setGravity(false);
            pig.setAI(false);
            pig.setCollidable(false);
            pig.setInvulnerable(true);
            pig.setSilent(true);
            pig.setCustomName("temp_seat");
            pig.setCustomNameVisible(false);
            pig.setMaxHealth(2);

            pig.addPassenger(p);

            isSitting = true;
        }

        public void unsit() {
            if(isSitting(p)) {
                p.getVehicle().remove();
                p.teleport(p.getLocation().add(0, 0.55, 0));
                isSitting = false;
            }
        }

        public static boolean isSitting(Player p) {
            try {
                Entity vehicle = p.getVehicle();
                return (vehicle instanceof Pig pig && pig.getCustomName().equals("temp_seat"));
            } catch (Exception e) {
                return false;
            }
        }
    }

    public class PingRecording {
        public static final int HIGH_PING_BOUNDARY = 500;

        public boolean isProtected = false;
        private int protectionTaskID = -1;
        private int stopProtectionTaskID = -2; // -2 = no stop protection needed, -1 = stop protection might be needed but wasn't yet defined

        private boolean forceProtectionOnJoin = false;
        public PingRecording() {
            try {
                forceProtectionOnJoin = getFromConfig("high_ping", true);
            } catch (NullPointerException ignored) {}

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                int currentPing = p.getPing();
                if(currentPing > 10) {
                    pingLog.set(2, pingLog.get(1));
                    pingLog.set(1, pingLog.get(0));
                    pingLog.set(0, currentPing);

                    if ((pingLog.stream().filter(integer -> integer > HIGH_PING_BOUNDARY).count() >= 2) || forceProtectionOnJoin) {
                        if(stopProtectionTaskID == -2) { // if stop protection wasn't needed
                            protectionTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::protect, 0L, 5*20L);
                            stopProtectionTaskID = -1; // change stop protection to "might be needed"
                            isProtected = true;
                            Utils.setInConfig("high_ping", true);
                            // only send the message again if protection stop hasn't yet been activated
                            if(!forceProtectionOnJoin) {
                                Utils.sendAll(ChatColor.YELLOW + "Warning: " + getColoredName() + ChatColor.YELLOW + "'s ping is too high! The player will receive additional resistance.");
                                Utils.sendAll(generatePingString());
                            }
                            forceProtectionOnJoin = false;
                        } else if (stopProtectionTaskID == -1) {
                            Bukkit.getScheduler().cancelTask(stopProtectionTaskID);
                        }
                    }

                    // order of ifs changed to reduce number of ops per check
                    if(stopProtectionTaskID == -1) {
                        if (pingLog.stream().allMatch(integer -> integer < HIGH_PING_BOUNDARY)) {
                            stopProtectionTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::endProtection, 120*20L);
                        }
                    }
                }
            }, 0L, 5*20L);
        }

        public void protect() {
            int maxPing = Collections.max(pingLog);
            int value = 0;
            if(maxPing > 900 && maxPing <= 1200) {
                value = 1;
            } else if(maxPing > 1200 && maxPing <= 1600) {
                value = 2;
            } else if(maxPing > 1600 && maxPing <= 2000) {
                value = 3;
            } else if(maxPing > 2000) value = 4;

            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, value));
        }

        public void endProtection() {
            isProtected = false;
            Utils.setInConfig("high_ping", false);
            Bukkit.getScheduler().cancelTask(protectionTaskID);
            protectionTaskID = -1;
            stopProtectionTaskID = -2;
            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            Utils.sendAll(getColoredName() + ChatColor.GREEN + "'s ping has improved. Resistance will be removed.");
        }

        private String pingWithColor() {
            String res = "";
            for(Integer i : pingLog) {
                // 0-200 = green, 200-bound (500) = gold, 500+ = red
                ChatColor color = i < 200 ? ChatColor.GREEN : (i < HIGH_PING_BOUNDARY ? ChatColor.GOLD : ChatColor.RED);
                res += color + "" + i + ChatColor.YELLOW + ", ";
            }
            return res.substring(0, res.length() - 2);
        }

        public String generatePingString() {
            return ChatColor.YELLOW + "Ping of player " + getColoredName() + ChatColor.YELLOW + " for the past 5s, 10s, 15s: " + pingWithColor();
        }
    }
}
