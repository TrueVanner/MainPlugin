package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemFrameChanges implements Listener {

    public ItemFrameChanges(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void invisibleItemFrames(EntitySpawnEvent e) {
        if (e.getEntity() instanceof ItemFrame) {
            for (Entity n : e.getEntity().getNearbyEntities(6, 6, 6)) {
                if (n instanceof Player) {
                    try {
                        ItemStack item = ((Player) n).getInventory().getItemInOffHand();
                        if (item.getType() == Material.NAME_TAG)
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("inv")) {
                                ((ItemFrame) (e.getEntity())).setVisible(false);
                                // ((ItemFrame) (e.getEntity())).setFixed(true);
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

    public static void tempGlowOnInvisible(Player p, Plugin plugin) {
        for (Entity e : p.getNearbyEntities(6, 4, 6)) {
            if ((e instanceof ItemFrame && !((ItemFrame) e).isVisible() && e.getTicksLived() > 100) || (e instanceof ArmorStand && !((ArmorStand) e).isVisible())) {
                ItemStack item = p.getInventory().getItemInOffHand();
                if (item.getType() == Material.NAME_TAG) {
                    try {
                        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("inv")) {
                            if (e instanceof ItemFrame) ((ItemFrame) e).setVisible(true);
                            e.setGlowing(true);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                if (e instanceof ItemFrame) ((ItemFrame) e).setVisible(false);
                                e.setGlowing(false);
                            }, 19L);
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
    }
}
