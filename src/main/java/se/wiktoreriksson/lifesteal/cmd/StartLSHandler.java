package se.wiktoreriksson.lifesteal.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.wiktoreriksson.lifesteal.LifeSteal;

import java.util.List;

public class StartLSHandler implements TabExecutor, Listener {
    private boolean noDmg = false;

    public boolean noDmg() {
        return noDmg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("You are not OP.");
        } else {
            noDmg = true;
            Bukkit.getScheduler().runTaskLater(LifeSteal.plugin, () -> noDmg = false, 24*60*60*20);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(EntityDamageByEntityEvent edbee) {
        if (edbee.getEntity() instanceof Player && noDmg) {
            if (edbee.getDamager() instanceof Player)
                edbee.setCancelled(true);
            else if (edbee.getDamager() instanceof Projectile) {
                if (((Projectile)edbee.getDamager()).getShooter() instanceof Player)
                    edbee.setCancelled(true);
            }
        }
    }
}
