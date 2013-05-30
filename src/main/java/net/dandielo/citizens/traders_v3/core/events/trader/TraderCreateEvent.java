package net.dandielo.citizens.traders_v3.core.events.trader;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;

public class TraderCreateEvent extends TraderEvent
{
	/**
	 * Handlers
	 */
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	/*
	 * Custom event
	 */	
	public TraderCreateEvent(Trader npc, Player player)
	{
		super(npc, player);
	}
}
