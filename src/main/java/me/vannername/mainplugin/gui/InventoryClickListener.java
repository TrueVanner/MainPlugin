package me.vannername.mainplugin.gui;

import me.vannername.mainplugin.MainPlugin;
import me.vannername.mainplugin.gui.coords.GUI_CONF_HOTBAR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_COLOR;
import me.vannername.mainplugin.gui.coords.inventory.GUI_CONF_TYPE;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_COORDS;
import me.vannername.mainplugin.gui.coords.inventory.color.GUI_LETTERS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    public InventoryClickListener(MainPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        if (title.equals(GUI_CONF_HOTBAR.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_CONF_HOTBAR.inv_name)) {
                GUI_CONF_HOTBAR.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_CONF_COLOR.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_CONF_COLOR.inv_name)) {
                GUI_CONF_COLOR.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_LETTERS.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_LETTERS.inv_name)) {
                GUI_LETTERS.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_COORDS.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_COORDS.inv_name)) {
                GUI_COORDS.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_CONF_TYPE.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_CONF_TYPE.inv_name)) {
                GUI_CONF_TYPE.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_MAIN.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_MAIN.inv_name)) {
                GUI_MAIN.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_TP.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_TP.inv_name)) {
                GUI_TP.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }

        if (title.equals(GUI_CONF_COORDS.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_CONF_COORDS.inv_name)) {
                GUI_CONF_COORDS.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_SKIPDAYNIGHT.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_SKIPDAYNIGHT.inv_name)) {
                GUI_SKIPDAYNIGHT.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
        if (title.equals(GUI_TABCOLOR.inv_name)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (title.equals(GUI_TABCOLOR.inv_name)) {
                GUI_TABCOLOR.clicked(p, e.getSlot(), e.getCurrentItem(), e.getInventory());
            }
        }
    }
}
