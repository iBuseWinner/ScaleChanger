package ru.fennec.free.scaleChanger.scaleChanger.common.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityPreChangeScaleEvent extends Event implements Cancellable {
    private CommandSender actor;
    private Entity target;
    private double scale;
    private boolean cancelled;

    private static final HandlerList handlerList = new HandlerList();

    public EntityPreChangeScaleEvent(CommandSender actor, Entity target, double scale) {
        this.actor = actor;
        this.target = target;
        this.scale = scale;
        this.cancelled = false;
    }

    public CommandSender getActor() {
        return actor;
    }

    public Entity getTarget() {
        return target;
    }

    public double getScale() {
        return scale;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
