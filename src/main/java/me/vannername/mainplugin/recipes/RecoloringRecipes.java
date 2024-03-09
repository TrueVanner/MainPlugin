package me.vannername.mainplugin.recipes;

import me.vannername.mainplugin.MainPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import static me.vannername.mainplugin.listeners.ConcreteRecoloring.colors;

public class RecoloringRecipes {
    public MainPlugin plugin;

    public RecoloringRecipes(MainPlugin plugin) {
        this.plugin = plugin;
        createConcreteCrafts();
        createWoolCrafts();
        createBedCrafts();
    }

    void createConcreteCrafts() {

        for (String color : colors) {
            if(!color.equals("light_gray")) {
                // recoloring concrete block
                ItemStack concrete = new ItemStack(Material.valueOf(color.toUpperCase() + "_CONCRETE"), 4);
                ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, color + "_concrete_from_default_concrete"), concrete);

                for (int i = 0; i < 4; i++) {
                    recipe.addIngredient(Material.LIGHT_GRAY_CONCRETE);
                }

                recipe.addIngredient(Material.valueOf(color.toUpperCase() + "_DYE"));
                Bukkit.addRecipe(recipe);

                // recoloring concrete powder
                ItemStack powder = new ItemStack(Material.valueOf(color.toUpperCase() + "_CONCRETE_POWDER"), 4);
                ShapelessRecipe recipe1 = new ShapelessRecipe(new NamespacedKey(plugin, color + "_concrete_powder_from_default_concrete_powder"), powder);

                for (int i = 0; i < 4; i++) {
                    recipe1.addIngredient(Material.LIGHT_GRAY_CONCRETE_POWDER);
                }

                recipe1.addIngredient(Material.valueOf(color.toUpperCase() + "_DYE"));
                Bukkit.addRecipe(recipe1);
            }
        }

        // craft gray concrete powder without dyes
        ItemStack res = new ItemStack(Material.LIGHT_GRAY_CONCRETE_POWDER, 8);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "default_concrete_powder"), res);

        for(int i = 0; i < 4; i++) {
            recipe.addIngredient(Material.SAND);
            recipe.addIngredient(Material.GRAVEL);
        }

        Bukkit.addRecipe(recipe);
    }

    void createWoolCrafts() {

        // recipes for any wool "otherColor" to be colored into "color"
        for(String color : colors) {
            for(String otherColor : colors) {
                if (!(otherColor.equals(color) || otherColor.equals("white"))) {
                    ItemStack wool = new ItemStack(Material.valueOf(color.toUpperCase() + "_WOOL"), 1);
                    ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, color + "_wool_from_" + otherColor + "_wool"), wool);

                    recipe.addIngredient(Material.valueOf(otherColor.toUpperCase() + "_WOOL"));
                    recipe.addIngredient(Material.valueOf(color.toUpperCase() + "_DYE"));
                    Bukkit.addRecipe(recipe);
                }
            }


            // crafting wool from carpets of any color
            ItemStack wool = new ItemStack(Material.valueOf(color.toUpperCase() + "_WOOL"), 2);
            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, color + "_wool_from_" + color + "_carpets"), wool);

            for(int i = 0; i < 3; i++) {
                recipe.addIngredient(Material.valueOf(color.toUpperCase() + "_CARPET"));
            }

            Bukkit.addRecipe(recipe);
        }
    }

    void createBedCrafts() {

        // recipes for any bed "otherColor" to be colored into "color"
        for(String color : colors) {
            for (String otherColor : colors) {
                if (!(otherColor.equals(color) || otherColor.equals("white"))) {
                    ItemStack bed = new ItemStack(Material.valueOf(color.toUpperCase() + "_BED"), 1);
                    ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, color + "_bed_from_" + otherColor + "_bed"), bed);

                    recipe.addIngredient(Material.valueOf(otherColor.toUpperCase() + "_BED"));
                    recipe.addIngredient(Material.valueOf(color.toUpperCase() + "_DYE"));
                    Bukkit.addRecipe(recipe);
                }
            }
        }
    }
}
