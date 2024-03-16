package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.stream.Stream;

import static me.vannername.mainplugin.utils.Utils.plugin;

public class ItemFrameChanges implements Listener {
    public ItemFrameChanges(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        for(Player p : plugin.getServer().getOnlinePlayers()) {
            clearGlowingSigns(p);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                tempGlowOnItemFrames(p);
            }
        }, 0L, 5L);
    }

    @EventHandler
    public void makeInvTagLookCool(PrepareAnvilEvent e) {
        try {
            ItemStack result = e.getResult();

            if (result.getType() == Material.NAME_TAG && result.hasItemMeta()) {
                ItemMeta meta = result.getItemMeta();
                if (meta.hasDisplayName() && meta.getDisplayName().equals("inv")) {
                    meta.setDisplayName(ChatColor.RESET + "");
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    result.setItemMeta(meta);
                    e.setResult(result);
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void invisibleItemFrames(EntitySpawnEvent e) {
        if (e.getEntity() instanceof ItemFrame itf) {
            for (Entity ent : e.getEntity().getNearbyEntities(6, 6, 6)) {
                if (ent instanceof Player p) {
                    try {
                        if (isInvTag(p.getInventory().getItemInOffHand())) {
                            itf.setVisible(false);
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
    }

    @EventHandler
    public void shiftToChangeItemFrame(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof ItemFrame) {
            if (((ItemFrame) e.getRightClicked()).getItem().getType() != Material.AIR) {
                if (!e.getPlayer().isSneaking()) e.setCancelled(true);
            }
        }
    }

    public boolean isInvTag(ItemStack item) {
        if(item.getType() == Material.NAME_TAG) {
            try {
                return item.getItemMeta().isUnbreakable();
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    private ArrayList<ItemFrame> oldToGlow = new ArrayList<>();
    private final ArrayList<ItemFrame> toGlow = new ArrayList<>();
    public void detectInvItemFrames(Player p) {
        if(isInvTag(p.getInventory().getItemInOffHand())) {
            for (Entity e : p.getNearbyEntities(15, 7, 15)) {
                if (e instanceof ItemFrame itf && (!itf.isVisible() || (itf.isVisible() && itf.isGlowing()))) {
                    toGlow.add(itf);
                }
            }
        }
    }

    public void tempGlowOnItemFrames(Player p) {
        detectInvItemFrames(p);
        // loops below don't run if the lists are empty, minimal load
        for (ItemFrame itf : toGlow) {
            itf.setVisible(true);
            itf.setGlowing(true);
        }
        for(ItemFrame itf : oldToGlow) {
            if(!toGlow.contains(itf)) {
                itf.setGlowing(false);
                itf.setVisible(false);
            }
        }
        oldToGlow = new ArrayList<>(toGlow);
        toGlow.clear();
    }

    public static void clearGlowingSigns(Player p) {
//        for(Player p : onlinePlayers) {
            for (Entity e : p.getNearbyEntities(50, 15, 50)) {
                if(e instanceof ItemFrame itf && e.isGlowing()) {
                    itf.setGlowing(false);
                    itf.setVisible(false);
                }
            }
//        }
    }
}
