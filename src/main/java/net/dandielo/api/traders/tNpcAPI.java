package net.dandielo.api.traders;

import net.dandielo.citizens.traders_v3.tNpcManager;

import org.bukkit.entity.Player;

public class tNpcAPI {
	private static tNpcManager manager = tNpcManager.instance(); 

	/**
	 * Checks if the given inventory is a trader, banker or auction npc inventory
	 * @param player
	 * the player that will be checked if he has opened a trader inventory
	 * @return
	 * true if the inventory was found in the registry
	 */
	public static boolean isTNpcInventory(Player player)
	{
		return manager.tNpcInventoryOpened(player);
	}
}
