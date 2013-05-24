package net.dandielo.citizens.traders_v3;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface tNpc {
	
	public void onLeftClick(ItemStack itemInHand);
	public boolean onRightClick(ItemStack itemInHand);
	
    public void onInventoryClick(InventoryClickEvent e);
    public void onManageInventoryClick(InventoryClickEvent e);
}
