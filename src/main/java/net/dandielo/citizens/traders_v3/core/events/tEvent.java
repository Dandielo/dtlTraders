package net.dandielo.citizens.traders_v3.core.events;

import org.bukkit.entity.Player;

import net.dandielo.citizens.traders_v3.tNpc;

/**
 * tNpc event base
 * @author dandielo
 *
 */
public abstract class tEvent {
	/**
	 * The tNpc that takes part in the event
	 */
	protected final tNpc npc;
    
    /**
     * The player that takes part in the event
     */
    protected final Player player;
    
    /**
     * Set all required variables of a tEvent 
     * @param npc
     * @param player
     */
	protected tEvent(tNpc npc, Player player)
	{
		this.npc = npc;
		this.player = player;
	}
	
	/**
	 * Returns the player that participates in the event with the given tNpc character
	 * @return
	 */
	public Player getParticipate()
	{
		return player;
	}
}
