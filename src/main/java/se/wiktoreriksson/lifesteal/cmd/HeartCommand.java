package se.wiktoreriksson.lifesteal.cmd;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.wiktoreriksson.lifesteal.LifeSteal;

public class HeartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player executor = (Player) sender;
            if (executor.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() > 2) {
                if (executor.getInventory().addItem(LifeSteal.plugin.getHeart()).isEmpty()) {
                    executor.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(executor.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2);
                    executor.sendMessage("You converted the heart into an item form. Use it carefully when needed.");
                } else executor.sendMessage("Inventory full...");
            } else executor.sendMessage("Yeah, no. I'm not gonna kill you to give you a heart item... Get some more hearts first.");
        } else sender.sendMessage("Hmm... you're not a player..?");
        return true;
    }
}
