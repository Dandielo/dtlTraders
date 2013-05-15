package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Stock implements InventoryHolder {
	protected final int size;
	protected final String name;
	
	protected final Map<String, List<StockItem>> stock = new HashMap<String, List<StockItem>>();
	
	protected Stock(String name, int size)
	{
        if( size <= 0 || size > 6 ){
        	throw new IllegalArgumentException("Size must be between 1 and 6");}
        
		this.name = name;
		this.size = size;
        
        stock.put("sell", new ArrayList<StockItem>());
        stock.put("buy", new ArrayList<StockItem>());
	}
	
	//Stockitem operations
	public StockItem getItem(int slot, String stock)
	{
		return this.stock.get(stock).get(slot);
	}
	
	//inventory size
	public final int getFinalInventorySize()
	{
		return size*9;
	}

	public boolean isUiSlot(int slot)
	{
		return slot < getFinalInventorySize() && slot >= getFinalInventorySize() - 3;
	}
	
	public void load(DataKey data) {
	}

	public void save(DataKey data) {
	}

	public abstract Inventory getInventory(Status status);
	public abstract Inventory getManagementInventory(Status status);
	public abstract void setInventory(Inventory inventory, Status status);
	public abstract void setManagementInventory(Inventory inventory, Status status);
	
}
