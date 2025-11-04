package com.github.fauxle.cpt;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassPlayerTracking extends JavaPlugin {

    private Map<UUID, UUID> compassPlayerTrackingTargets;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.compassPlayerTrackingTargets = new HashMap<>();
        this.getServer()
                .getScheduler()
                .runTaskTimer(
                        this,
                        () -> {
                            if (compassPlayerTrackingTargets.isEmpty()) return;
                            Map<UUID, Player> lookupTable = new HashMap<>();
                            for (Player p : getServer().getOnlinePlayers()) {
                                lookupTable.put(p.getUniqueId(), p);
                            }
                            Set<UUID> cleanup = new HashSet<>();
                            for (Map.Entry<UUID, UUID> en :
                                    compassPlayerTrackingTargets.entrySet()) {
                                Player chaser = lookupTable.get(en.getKey());
                                Player target = lookupTable.get(en.getValue());
                                if (chaser != null && target != null) {
                                    chaser.setCompassTarget(target.getLocation());
                                } else {
                                    cleanup.add(en.getKey());
                                }
                            }
                            if (!cleanup.isEmpty()) {
                                for (UUID uuid : cleanup) compassPlayerTrackingTargets.remove(uuid);
                                cleanup.clear();
                            }
                            lookupTable.clear();
                        },
                        20,
                        TimeUnit.SECONDS.toSeconds(
                                        getConfig().getInt("compass-auto-update-seconds", 30))
                                * 20);
    }

    public void setCompassPlayerTrackingTarget(UUID chaser, UUID target) {
        if (target == null) {
            this.compassPlayerTrackingTargets.remove(chaser);
        } else {
            this.compassPlayerTrackingTargets.put(chaser, target);
        }
    }
}
