package se.wiktoreriksson.lifesteal;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import se.wiktoreriksson.lifesteal.cmd.HeartCommand;
import se.wiktoreriksson.lifesteal.cmd.StartLSHandler;

import java.util.Arrays;

/**
 * Main class of the LifeSteal plugin. Contains event handling and crafting setup.
 * @author Wii
 */
public final class LifeSteal extends JavaPlugin implements Listener {
    /**
     * An org.bukkit.inventory.ItemStack describing a Nether Star with the name "§4Heart". It is the item form of a heart, representing 2 HP.
     */
    private ItemStack heart;

    /**
     * This plugin, set during #onEnable().
     */
    public static LifeSteal plugin;

    /**
     * Getter for ItemStack heart
     * @return ItemStack heart
     */
    public ItemStack getHeart() {
        return heart;
    }

    private StartLSHandler lsh;

    /**
     * The enable method of LifeSteal. Overridden from org.bukkit.plugin.java.JavaPlugin
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        lsh = new StartLSHandler();

        Bukkit.getPluginCommand("getheart").setExecutor(new HeartCommand());
        Bukkit.getPluginCommand("startls").setExecutor(lsh);
        Bukkit.getPluginManager().registerEvents(lsh,this);
        Bukkit.getPluginManager().registerEvents(this,this);


        heart = new ItemStack(Material.NETHER_STAR);
        ItemMeta im = heart.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED+"Heart");
        im.setLore(Arrays.asList(
                ChatColor.RED+"Crafted with the riches of 1000 men.",
                ChatColor.RED+"Grants you an extra heart."
        ));
        heart.setItemMeta(im);
        ShapedRecipe heartCraft = new ShapedRecipe(new NamespacedKey(this,"heart"), heart);
        heartCraft.shape("OTO", "DND", "OTO");
        heartCraft.setIngredient('O',Material.OBSIDIAN);
        heartCraft.setIngredient('T',Material.TOTEM_OF_UNDYING);
        heartCraft.setIngredient('D',Material.DIAMOND_BLOCK);
        heartCraft.setIngredient('N',Material.NETHERITE_INGOT);
        Bukkit.addRecipe(heartCraft);
    }

    /**
     * Empty disable method. Overridden from org.bukkit.plugin.java.JavaPlugin
     */
    @Override
    public void onDisable() {}

    /**
     * Event handler for a death. Used to remove/add hearts due to killing.
     * @param pde org.bukkit.event.entity.PlayerDeathEvent passed by Bukkit
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(@NotNull PlayerDeathEvent pde) {
        if (lsh.noDmg()) return;
        Player killer = pde.getEntity().getKiller();
        if (killer != null) { //Did they die by a player?
            AttributeInstance playerh = pde.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
            AttributeInstance killerh = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (playerh.getBaseValue() > 2 && killerh.getBaseValue() < 40) { // Do not execute for 1 hearted people.
                playerh.setBaseValue(playerh.getBaseValue()-2);
                killerh.setBaseValue(killerh.getBaseValue()+2);
            }
        }
    }
    /**
     * Event handler for a right click. Used to detect usage of heart.
     * @param pie org.bukkit.event.player.PlayerInteractEvent passed by Bukkit
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClick(@NotNull PlayerInteractEvent pie) {
        ItemStack mainHand = pie.getPlayer().getInventory().getItemInMainHand();
        if (mainHand.isSimilar(heart) && pie.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() < 40) {
            if (pie.getAction() == Action.RIGHT_CLICK_BLOCK || pie.getAction() == Action.RIGHT_CLICK_AIR) {
                mainHand.setAmount(mainHand.getAmount() - 1);
                pie.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(pie.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);
                pie.getPlayer().sendMessage(ChatColor.RED+"You have recieved a heart.");
                pie.getPlayer().playSound(pie.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            }
        }
    }
}
