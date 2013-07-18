package net.dandielo.citizens.traders_v3.core.events.trader;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;

/**
 * Event called after a traders was right or left clicked. 
 * This event contains the trader that was clicked, and the Clicker (Player). 
 * Information about the click, and if the click was toggling manager mode or was just a normal click.
 * This event is not called when a player does not have permission to use a trader
 * @author dandielo
 *
 */
public class TraderClickEvent extends TraderEvent {
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
	 * Custom Event
	 */
	protected boolean mmToggling;
	protected boolean leftClick; 
	
	/**
	 * The end TraderClick event
	 * @param npc
	 * the trader that takes part
	 * @param player
	 * the player that takes part
	 * @param mmToggling
	 * if the event was toggling manager mode
	 * @param leftClick
	 * if the event was a left click
	 */
	public TraderClickEvent(Trader npc, Player player, boolean mmToggling, boolean leftClick)
	{
		super(npc, player);
		this.mmToggling = mmToggling;
		this.leftClick = leftClick;
	}

	/**
	 * @return
	 * If the trader was left clicked
	 */
	public boolean isLeftClick()
	{
		return leftClick;
	}
	
	/**
	 * @return
	 * If the trader was right clicked
	 */
	public boolean isRightClick()
	{
		return !leftClick;
	}
	
	/**
	 * @return
	 * If the click event will toggle the manager mode
	 */
	public boolean isManagerToggling()
	{
		return mmToggling;
	}
	
	/**
	 * Sets if the event should toggle the manager mode instead of opening the trader
	 * @param toggle
	 * set true if you want to toggle the manager mode
	 */
	public void setManagerToggling(boolean toggle)
	{
		mmToggling = toggle;
	}
}
