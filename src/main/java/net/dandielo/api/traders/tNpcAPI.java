package net.dandielo.api.traders;

import net.dandielo.citizens.traders_v3.tNpcManager;

import org.bukkit.inventory.Inventory;

public class tNpcAPI {

	/**
	 * Checks if the given inventory is a trader, banker or aution npc inventory
	 * @param inventory
	 * the inventory that will be checked
	 * @return
	 * true if the inventory was found in the registry
	 */
	public static boolean isTNpcInventory(Inventory inventory)
	{
		return tNpcManager.instance().tNpcInventory(inventory);
	}
}
