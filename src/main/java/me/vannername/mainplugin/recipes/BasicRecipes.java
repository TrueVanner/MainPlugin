package me.vannername.mainplugin.recipes;

import me.vannername.mainplugin.MainPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class BasicRecipes {
    private final MainPlugin plugin;

    public BasicRecipes(MainPlugin plugin) {
        this.plugin = plugin;
        Bukkit.addRecipe(serverChest());
        Bukkit.addRecipe(enchGoldApple());
        Bukkit.addRecipe(fireworkPower4());
        Bukkit.addRecipe(fireworkPower5());
        for(int i = 1; i <= 9; i++) {
            if(i != 2) {
                Bukkit.addRecipe(bowToSticks(i));
                Bukkit.addRecipe(crossbowToSticks(i));
            }
            Bukkit.addRecipe(saddleToLeather(i));
        }
        Material[] logs = new Material[]{
                Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
                Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG,
                Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM};
        Material[] woods = new Material[]{
                Material.OAK_WOOD, Material.BIRCH_WOOD, Material.SPRUCE_WOOD,
                Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.DARK_OAK_WOOD, Material.MANGROVE_WOOD,
                Material.CHERRY_WOOD};

        for(Material log : logs) {
            Bukkit.addRecipe(woodToChests(log));
        }
        for(Material wood : woods) {
            Bukkit.addRecipe(woodToChests(wood));
        }
    }

    public ShapedRecipe serverChest() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        NamespacedKey key = new NamespacedKey(plugin, "ender_chest");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Server Chest");
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("EGE", "GCG", "EGE");
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('C', Material.ENDER_CHEST);
        return recipe;
    }

    public ShapedRecipe enchGoldApple() {
        ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("TTT", "TET", "TTT");
        recipe.setIngredient('T', Material.GOLD_BLOCK);
        recipe.setIngredient('E', Material.APPLE);
        return recipe;
    }

    public ShapelessRecipe fireworkPower4() {
        ItemStack res = new ItemStack(Material.FIREWORK_ROCKET);
        res.setAmount(3);
        FireworkMeta meta = (FireworkMeta) res.getItemMeta();
        meta.setPower(4);
        res.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "firework_rocket"), res);
        recipe.addIngredient(Material.PAPER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        return recipe;
    }

    public ShapelessRecipe fireworkPower5() {
        ItemStack res = new ItemStack(Material.FIREWORK_ROCKET);
        res.setAmount(3);
        FireworkMeta meta = (FireworkMeta) res.getItemMeta();
        meta.setPower(5);
        res.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "firework_rocket2"), res);
        recipe.addIngredient(Material.PAPER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        recipe.addIngredient(Material.GUNPOWDER);
        return recipe;
    }

    /* public ShapedRecipe pacifier() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Pacifier");
        meta.setLore(Arrays.asList("While in inventory:"," - Can't directly damage other players", " - Other players can't directly damage you"));
        meta.setCustomModelData(999);
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(plugin, "paper");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(" F ", "FPF", " F ");
        recipe.setIngredient('F', Material.POPPY);
        recipe.setIngredient('P', Material.PAPER);
        return recipe;
    } */

    public ShapelessRecipe bowToSticks(int count) {
        ItemStack res = new ItemStack(Material.STICK, 2*count);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, count+"_bow_to_sticks"), res);
        for(int i = 0; i < count; i++) {
            recipe.addIngredient(Material.BOW);
        }
        return recipe;
    }
    public ShapelessRecipe crossbowToSticks(int count) {
        ItemStack res = new ItemStack(Material.STICK, 2*count);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, count+"_crossbow_to_sticks"), res);
        for(int i = 0; i < count; i++) {
            recipe.addIngredient(Material.CROSSBOW);
        }
        return recipe;
    }

    public ShapelessRecipe saddleToLeather(int count) {
        ItemStack res = new ItemStack(Material.LEATHER, count);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, count+"_saddle_to_leather"), res);
        for(int i = 0; i < count; i++) {
            recipe.addIngredient(Material.SADDLE);
        }

        return recipe;
    }

    public ShapedRecipe woodToChests(Material wood) {
        ItemStack item = new ItemStack(Material.CHEST, 4);
        NamespacedKey key = new NamespacedKey(plugin, "chest_from_" + wood.toString().toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("WWW", "W W", "WWW");
        recipe.setIngredient('W', wood);
        return recipe;
    }
}
