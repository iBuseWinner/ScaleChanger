package ru.fennec.free.scaleChanger.scaleChanger.common.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityChangedScaleEvent extends Event {
    private CommandSender actor;
    private Entity target;
    private double scale;

    private static final HandlerList handlerList = new HandlerList();

    public EntityChangedScaleEvent(CommandSender actor, Entity target, double scale) {
        this.actor = actor;
        this.target = target;
        this.scale = scale;
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
}
