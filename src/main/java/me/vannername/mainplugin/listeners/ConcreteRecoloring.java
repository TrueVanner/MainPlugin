package me.vannername.mainplugin.listeners;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ConcreteRecoloring implements Listener {

    public ConcreteRecoloring(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

//        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
//            Bukkit.clearRecipes();
//            Bukkit.resetRecipes();
//
////            Bukkit.getPlayer("VannerName").getDiscoveredRecipes().forEach(recipe -> {
////                if(recipe.getNamespace().equals("MainPlugin")) Bukkit.removeRecipe(recipe);
////            });
//        }, 10L);
    }

    public static String[] colors = new String[] {
            "white", "red", "pink", "orange", "yellow", "green", "lime", "light_blue",
            "cyan", "blue", "magenta", "purple", "brown", "gray", "black", "light_gray"
    };

    boolean checkConcrete(ItemStack toCheck) {
        for(String color : colors) {
            if(!color.equals("light_gray")) {
                if (toCheck.getType() == Material.valueOf(color.toUpperCase() + "_CONCRETE"))
                    return true;
            }
        }
        return false;
    }

    @EventHandler
    public void rightClickOnCauldron(PlayerInteractEvent e) {
        try {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.WATER_CAULDRON) {

                PlayerInventory inv = e.getPlayer().getInventory();

                if (checkConcrete(inv.getItemInMainHand())) {
                    e.setCancelled(true);
                    Levelled data = (Levelled) e.getClickedBlock().getBlockData();

                    if (data.getLevel() == 1)
                        e.getClickedBlock().setType(Material.CAULDRON);
                    else {
                        data.setLevel(data.getLevel() - 1);
                        e.getClickedBlock().setBlockData(data);
                    }
                    inv.setItemInMainHand(new ItemStack(Material.LIGHT_GRAY_CONCRETE, inv.getItemInMainHand().getAmount()));
                }

            }
        } catch (NullPointerException ignored) {}
    }
}
