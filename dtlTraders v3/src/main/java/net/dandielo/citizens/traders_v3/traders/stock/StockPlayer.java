package net.dandielo.citizens.traders_v3.traders.stock;

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
		Inventory inventory = Bukkit.createInventory(this, getFinalInventorySize(), name);
		return inventory;
	}

}
