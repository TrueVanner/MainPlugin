package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class DamageCanceling implements Listener {
    public DamageCanceling(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static ArrayList<EntityType> hostiles = new ArrayList<>();
    public static ArrayList<EntityType> badHostiles = new ArrayList<>();
    public void loadHostile() {
        hostiles.add(EntityType.BLAZE);
        hostiles.add(EntityType.ENDERMITE);
        hostiles.add(EntityType.ENDERMAN);
        hostiles.add(EntityType.SLIME);
        hostiles.add(EntityType.ZOMBIE);
        hostiles.add(EntityType.PIGLIN_BRUTE);
        hostiles.add(EntityType.PIGLIN);
        hostiles.add(EntityType.SKELETON);
        hostiles.add(EntityType.BAT);
        hostiles.add(EntityType.SPIDER);
        hostiles.add(EntityType.CAVE_SPIDER);
        hostiles.add(EntityType.ZOMBIFIED_PIGLIN);
        badHostiles.add(EntityType.EVOKER);
        hostiles.add(EntityType.VINDICATOR);
        hostiles.add(EntityType.PILLAGER);
        badHostiles.add(EntityType.RAVAGER);
        hostiles.add(EntityType.VEX);
        hostiles.add(EntityType.GUARDIAN);
        badHostiles.add(EntityType.ELDER_GUARDIAN);
        hostiles.add(EntityType.SHULKER);
        hostiles.add(EntityType.HUSK);
        hostiles.add(EntityType.STRAY);
        hostiles.add(EntityType.PHANTOM);
        hostiles.add(EntityType.CREEPER);
        hostiles.add(EntityType.GHAST);
        hostiles.add(EntityType.MAGMA_CUBE);
        hostiles.add(EntityType.SILVERFISH);
        hostiles.add(EntityType.ZOMBIE_VILLAGER);
        hostiles.add(EntityType.DROWNED);
        hostiles.add(EntityType.WITHER_SKELETON);
        hostiles.add(EntityType.WITCH);
        hostiles.add(EntityType.HOGLIN);
        hostiles.add(EntityType.ZOGLIN);
        badHostiles.add(EntityType.WARDEN);
        badHostiles.add(EntityType.ENDER_DRAGON);
        badHostiles.add(EntityType.WITHER);
        hostiles.add(EntityType.FIREBALL);
        hostiles.add(EntityType.BREEZE);
        hostiles.add(EntityType.SHULKER_BULLET);
        hostiles.addAll(badHostiles);
    }

    private void cancelDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);

        Player damager = (Player) e.getDamager();
        damager.stopSound(Sound.ENTITY_GENERIC_HURT);

        if(e.getEntity() instanceof Player damaged) {
            damaged.stopSound(Sound.ENTITY_GENERIC_HURT);
        }
        damager.getWorld().spawnParticle(Particle.HEART, e.getEntity().getLocation().add(-0.5 + Math.random(), e.getEntity().getHeight() - 0.25 + Math.random() * 0.5, -0.5 + Math.random()), 1);
    }

    /*@EventHandler
    public void test(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            ((Player) e.getEntity()).getInventory().forEach(i -> {
                try {
                    if (Objects.requireNonNull(i.getItemMeta()).getCustomModelData() == 999) {
                        e.getEntity().getNearbyEntities(1.5, 1, 1.5).forEach(entity -> {
                            if (entity instanceof Player) {
                                e.setCancelled(true);
                                cancelDamage2((Player) e.getEntity());
                                return;
                            }
                        });
                    }
                } catch (NullPointerException | IllegalStateException ignored) {
                }
            });
        }
    }*/

    @EventHandler
    public void damageDealingLimitations(EntityDamageByEntityEvent e) {
        if (hostiles.isEmpty()) loadHostile();

        if (e.getDamager() instanceof Player damager) {
            if (damager.isSneaking() && !hostiles.contains(e.getEntityType())) {
                cancelDamage(e);

            } else if (e.getEntity() instanceof Player damaged) {
                if (Utils.getPluginPlayer(damager).isPassive || Utils.getPluginPlayer(damaged).isPassive) {
                    cancelDamage(e);
                }
            }
        }
        // works on tridents because Trident extends AbstractArrow for some reason. Wild shit
        if (e.getDamager() instanceof AbstractArrow arrow &&
                arrow.getShooter() instanceof Player damager && e.getEntity() instanceof Player damaged) {
            if (Utils.getPluginPlayer(damager).isPassive || Utils.getPluginPlayer(damaged).isPassive) {
                cancelDamage(e);
            }
        }
    }
}
