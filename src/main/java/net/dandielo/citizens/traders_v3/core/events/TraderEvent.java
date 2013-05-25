package net.dandielo.citizens.traders_v3.core.events;

import net.dandielo.citizens.traders_v3.traders.Trader;

import org.bukkit.entity.Player;

/**
 * Base for each trader related event
 * @author dandielo
 *
 */
public class TraderEvent extends tEvent {

	/**
	 * A trader event base
	 * @param npc
	 * the trader that takes part in the event
	 * @param player
	 * the player that takes part in the event
	 */
	protected TraderEvent(Trader npc, Player player)
	{
		super(npc, player);
	}
	
	/**
	 * Returns the participating trader
	 */
	public Trader getTrader()
	{
		//this cast will be always valid
		return (Trader) npc;
	}
	
}
