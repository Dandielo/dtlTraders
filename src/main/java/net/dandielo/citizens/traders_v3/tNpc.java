package net.dandielo.citizens.traders_v3;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface tNpc {
	
	public void onLeftClick();
	public void onRightClick();
	
    public void onInventoryClick(InventoryClickEvent e);
    public void onManageInventoryClick(InventoryClickEvent e);
	
}
