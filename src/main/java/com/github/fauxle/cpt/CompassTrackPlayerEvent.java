package com.github.fauxle.cpt;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
@Setter
public class CompassTrackPlayerEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player target;

    public CompassTrackPlayerEvent(Player who, Player target) {
        super(who);
        this.target = target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
