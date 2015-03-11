package net.dandielo.citizens.traders_v3;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Basic interface for each tNpc type. Might change in future versions.
 * @author dandielo
 *
 */
public interface TradingEntity {

	/**
	 * Allows simply to lock and save all unsaved changes to a tNpc on inventory close events
	 */
	public void lockAndSave();
	
	/**
	 * Handles a left click on the entity
	 * @param itemInHand
	 * the item hold when left clicking
	 * @return
	 * true if the click was successful handled
	 */
	public void onLeftClick(ItemStack itemInHand);
	
	/**
	 * Handles a right click on the entity
	 * @param itemInHand
	 * the item hold when right clicking
	 * @return
	 * true if the click was successful handled
	 */
	public boolean onRightClick(ItemStack itemInHand);

	/**
	 * Handles the inventory click event the for normal mode
	 * @param e
	 * the inventory click event that will be handled
	 */
    public void onInventoryClick(InventoryClickEvent e); 
	
	/**
	 * Handles the inventory click event for the manager mode (same as for the normal mode) 
	 * @param e
	 * the inventory click event that will be handled
	 */
    public void onManageInventoryClick(InventoryClickEvent e);
    
    /**
     * @return
     * the current entity traging status
     */
    public TEntityStatus getStatus();
}
