package me.vannername.mainplugin.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static me.vannername.mainplugin.MainPlugin.pluginPlayers;

public class Utils {
    public static final Plugin plugin = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MainPlugin"));
    public static final FileConfiguration config = plugin.getConfig();

    public static void sendAll(Object toSend) {
        if(toSend instanceof BaseComponent[])
            Bukkit.spigot().broadcast((BaseComponent[]) toSend);
        else
            Bukkit.broadcastMessage(String.valueOf(toSend));
    }

    // different name to differentiate testing messages from general messages
    public void debug(Object toSend) {
        Utils.sendAll(toSend);
    }

    public static ComponentBuilder appendStyle(ComponentBuilder base, String style) {
        switch (style) {
            case "BOLD" -> base.bold(true);
            case "ITALIC" -> base.italic(true);
            case "STRIKETHROUGH" -> base.strikethrough(true);
            case "UNDERLINED" -> base.underlined(true);
        }
        return base;
    }
    // might no longer be needed with new coords detection
    private static String f(String coords) {
        if(coords.split(" ").length == 2)
            return coords.replace(" ", " ~ ");
        else return coords;
    }

    public static ComponentBuilder addToComponentWithEvents(ComponentBuilder base, String text, String hoverClickText, ChatColor color, String style) {
        return Utils.appendStyle(base.append(text).color(color), style)
                .underlined(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverClickText)))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, hoverClickText));
    }
    public static ComponentBuilder addNavLinkToComponent(ComponentBuilder base, String coords, ChatColor color, String style) {
//        return Utils.addToComponentWithEvents(base, coords,"/navigate " + f(coords), color, style);
        return Utils.addToComponentWithEvents(base, coords,"/navigate " + coords, color, style);
    }

    public static ItemStack createItem(Inventory inv, Material material, int invSlot, String displayName, String... loreString) {
        ItemStack item;
        List<String> lore = new ArrayList<>();
        item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        Collections.addAll(lore, loreString);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(invSlot, item);
        return item;
    }

    public static void createCustomItem(Inventory inv, ItemStack item, int invSlot, String displayName, String... loreString) {
        List<String> lore = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        Collections.addAll(lore, loreString);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(invSlot, item);
    }

    public static boolean randomChance(int chance) {
        return Math.floor(Math.random() * 100) + 1 <= chance;
    }

    public static String direction (float degree) {
        if (degree > 157.5 || degree <= -157.5)
            return "N";
        else if (degree > -157.5 && degree <= -112.5)
            return "NE";
        else if (degree > -112.5 && degree <= -67.5)
            return "E";
        else if (degree > -67.5 && degree <= -22.5)
            return "SE";
        else if (degree > -22.5 && degree < 0 || degree <= 22.5)
            return "S";
        else if (degree > 22.5 && degree <= 67.5)
            return "SW";
        else if (degree > 67.5 && degree <= 112.5)
            return "W";
        else if (degree > 112.5 && degree <= 157.5)
            return "NW";
        else return "";
    }
    private static final ArrayList<Object> currentlyRunning = new ArrayList<>();
    public static void onlyOnce(Runnable r, Object uniqueID, Plugin plugin, long delay) {
        if (!currentlyRunning.contains(uniqueID)) {
            currentlyRunning.add(uniqueID);
            r.run();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> currentlyRunning.remove(uniqueID), delay);
        }
    }

    public static String timeOfDayString(boolean mode) { return mode ? "day" : "night"; }

    public static String cTimeOfDayString(boolean mode) { return mode ? "Day" : "Night"; }

    public static boolean isOnline(String playerName) {
        return pluginPlayers.containsKey(playerName);
    }

    public static MainPluginPlayer getPluginPlayer(String playerName) throws NullPointerException {
        if(!isOnline(playerName)) {
            throw new NullPointerException("Player is not online");
        }
        return pluginPlayers.get(playerName);
    }

    public static MainPluginPlayer getPluginPlayer(Player p) {
        return getPluginPlayer(p.getName());
    }

    public static String getPlayerConfigPath(Player p, String property) {
        return "Users." + p.getName() + property;
    }
    public static String getPlayerConfigPath(String playerName, String property) {
        return "Users." + playerName + property;
    }
    public static void setInConfig(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }

    public static boolean playerConfigContains(Player p, String property) {
        return config.contains(getPlayerConfigPath(p, property));
    }
    // very small class to simplify transmission of condition check results
    public static class MeetsConditions {
        public boolean value;
        public String expl;
        public MeetsConditions() {
            this.value = true;
        }
        public MeetsConditions(boolean value, String expl) {
            this.value = value;
            this.expl = org.bukkit.ChatColor.RED + expl;
        }
    }

    // creates a wall of particles on the specified border (+x, -x, +z, -z)
    public static void particleBorder(Player p, World w, int border) {
        if(p.getWorld() == w) {
            // i need two ifs to create two separate walls of particles
            if (Math.abs(p.getLocation().getBlockX() - border) < 30) { // check of the player is close enough
                Utils.onlyOnce(() -> p.sendMessage(org.bukkit.ChatColor.YELLOW + "Attention: you are approaching the last loaded chunks. Loading new chunks will be much slower and will make the server run slower for all players. Proceed with caution."), 3721, plugin, 1200L);
                for (int y = -2; y < 8; y++) { // create particle wall on relative y from -2 to 8
                    for (int z = -5; z <= 5; z++) { // create particle wall on relative y from -5 to 5
                        Location l = p.getLocation().add(0, y, z).getBlock().getLocation(); // get exact block location
                        l.setX(p.getLocation().getBlockX() > 0 ? border : -border);
                        if (Math.abs(l.getBlockZ()) <= border) { // the wall stops at the border
                            p.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                        }
                    }
                }
            }

            if (Math.abs(p.getLocation().getBlockZ() - border) < 30) {
                Utils.onlyOnce(() -> p.sendMessage(org.bukkit.ChatColor.YELLOW + "Attention: you are approaching the last loaded chunks. Loading new chunks will be much slower and will make the server run slower for all players. Proceed with caution."), 3721, plugin, 1200L);
                for (int y = -2; y < 8; y++) {
                    for (int x = -5; x <= 5; x++) {
                        Location l = p.getLocation().add(x, y, 0).getBlock().getLocation();
                        l.setZ(p.getLocation().getBlockZ() > 0 ? border : -border);
                        if (Math.abs(l.getBlockX()) <= border) {
                            p.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                        }
                    }
                }
            }
        }
    }

    public static int levelToXP(int lvl, double exp) {
        int xp = lvl * lvl + 6 * lvl;
        int n = 2 * lvl + 7;
        if (lvl > 16) {
            xp = (int) (2.5 * lvl * lvl - 40.5 * lvl + 360);
            n = 5 * lvl - 38;
        }
        if (lvl > 31) {
            xp = (int) (4.5 * lvl * lvl - 162.5 * lvl + 2220);
            n = 9 * lvl - 158;
        }
        return (int) Math.round(xp + exp * n);
    }

    public static int XPtoLevel(double total) {
        int lvl;
        if(total < 352) {
            lvl = (int) (Math.sqrt(total + 9) - 3);
        } else if(total < 1507) {
            lvl = (int) (8.1 + Math.sqrt(0.4 * (total - (double) 7839 / 40)));
        } else {
            lvl = (int) ((double) 325 / 18 + ((double) 2 / 9) * (total - ((double) 54215 / 72)));
        }
        return lvl;
    }
}
