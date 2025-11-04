package com.github.fauxle.cpt;

import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final CompassPlayerTracking plugin;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.COMPASS) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        Player p = event.getPlayer();

        Location ourLocation = p.getLocation();

        Player target =
                p.getWorld().getPlayers().stream()
                        .filter(o -> o != p)
                        .filter(o -> o.getLocation().distanceSquared(ourLocation) > 100)
                        .min(
                                Comparator.comparingDouble(
                                        o -> ourLocation.distanceSquared(o.getLocation())))
                        .orElse(null);

        CompassTrackPlayerEvent trackPlayerEvent = new CompassTrackPlayerEvent(p, target);
        plugin.getServer().getPluginManager().callEvent(trackPlayerEvent);

        if (trackPlayerEvent.isCancelled()) {
            return;
        } else {
            target = trackPlayerEvent.getTarget();
        }

        if (target == null) {
            p.sendMessage(ChatColor.YELLOW + "Could not find any valid targets");
            plugin.setCompassPlayerTrackingTarget(p.getUniqueId(), null);
        } else {
            p.sendMessage(ChatColor.YELLOW + "Compass pointing at " + target.getName());
            p.setCompassTarget(target.getLocation());
            plugin.setCompassPlayerTrackingTarget(p.getUniqueId(), target.getUniqueId());
        }
    }
}
