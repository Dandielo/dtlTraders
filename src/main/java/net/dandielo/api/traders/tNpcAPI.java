package net.dandielo.api.traders;

import java.util.Collection;
import java.util.Set;

import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.tNpcManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
		return tNpcManager.instance().tNpcInventoryOpened(player);
	}
	
	/*
	 * Test methods
	 */
	public static Collection<Inventory> getInventories()
	{
		return tNpcManager.instance().getInventories();
	}
	
	public static boolean inRelation(Player player)
	{
		return manager.inRelation(player);
	}
	
	public static tNpc getRelation(Player player)
	{
		return manager.getRelation(player.getName(), tNpc.class);
	}
}
