package net.dandielo.citizens.traders_v3.traders.stock;

import net.dandielo.citizens.traders_v3.traders.Trader.Status;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StockPlayer extends Stock {
    @SuppressWarnings("unused")
	private Player player;
	
	public StockPlayer(String name, int size, Player player) {
		super(null);
		this.player = player;
	}

	@Override
	public Inventory getInventory() {
		return Bukkit.createInventory(this, getFinalInventorySize(), "");
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

	@Override
	public void setAmountsInventory(Inventory inventory, Status status, StockItem item) {
		// TODO Auto-generated method stub
		
	}

	/* price methods */
	@Override
	public double parsePrice(StockItem item, int slot) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addItem(StockItem item, String stock) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItem(StockItem item, String stock) {
		// TODO Auto-generated method stub
		
	}
}
