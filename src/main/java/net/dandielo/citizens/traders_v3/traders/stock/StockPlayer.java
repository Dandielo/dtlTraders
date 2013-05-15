package net.dandielo.citizens.traders_v3.traders.stock;

import net.dandielo.citizens.traders_v3.traders.Trader.Status;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StockPlayer extends Stock {
    private Player player;
	
	public StockPlayer(String name, int size, Player player) {
		super(name, size);
		this.player = player;
	}

	@Override
	public Inventory getInventory() {
		return Bukkit.createInventory(this, getFinalInventorySize(), name);
	}

	@Override
	public Inventory getInventory(Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory getManagementInventory(Status baseStatus, Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventory(Inventory inventory, Status status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setManagementInventory(Inventory inventory, Status baseStatus, Status status) {
		// TODO Auto-generated method stub
		
	}

}
