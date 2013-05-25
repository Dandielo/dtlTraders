package net.dandielo.citizens.traders_v3.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;

/**
 * tNpc event base
 * @author dandielo
 *
 */
public abstract class tEvent extends Event {
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
	
	/**
	 * Call the event
	 * @return 
	 */
	public tEvent callEvent()
	{
		DtlTraders.getInstance().getServer().getPluginManager().callEvent(this);
		return this;
	}
}
