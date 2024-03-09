package me.vannername.mainplugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveAFKEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String playerName;

    public PlayerLeaveAFKEvent(String playerName) {
        this.playerName = playerName;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
