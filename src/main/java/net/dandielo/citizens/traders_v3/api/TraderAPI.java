package net.dandielo.citizens.traders_v3.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.Trader.Status;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;

public class TraderAPI {
	
	//Core related
	public void registerTraderType(Class<? extends Trader> type)
	{
		
	}
	
	//trader type related
	public <T extends Trader> T createTrader(Location loc, Class<T> type)
	{
		return null;
	}
	
	public void removeTrader(String name)
	{
	}
	
	public void removeTrader(int npcID)
	{
	}
	
	public void toggleStatus(Player player, Trader trader, Status status)
	{
		
	}
	
	public void openTrader(Player player, Trader trader)
	{
		
	}

	public void closeTrader(Player player, Trader trader)
	{
		
	}
	
	//transaction related
	public boolean sellItem(Player player, Trader trader, StockItem item)
	{
		return false;
	}

	public boolean buyItem(Player player, Trader trader, StockItem item)
	{
		return false;
	}
	
	//Stock related
	public void removeItem(Trader trader, String stock, ItemStack item)
	{
		
	}
	public void removeItem(Trader trader, String stock, StockItem item)
	{
		
	}
	public void removeItem(Trader trader, String stock, String item)
	{
		
	}
	
	public void addItem(Trader trader, String stock, ItemStack item)
	{
		
	}
	public void addItem(Trader trader, String stock, StockItem item)
	{
		
	}
	public void addItem(Trader trader, String stock, String item)
	{
		
	}
	
	public boolean hasItem(Trader trader, String stock, ItemStack item)
	{
		return false;
	}
	public boolean hasItem(Trader trader, String stock, StockItem item)
	{
		return false;
	}
	public boolean hasItem(Trader trader, String stock, String item)
	{
		return false;
	}
	
	public StockItem getItem(Trader trader, String stock, ItemStack item)
	{
		return null;
	}
	public StockItem getItem(Trader trader, String stock, StockItem item)
	{
		return null;
	}
	public StockItem getItem(Trader trader, String stock, String item)
	{
		return null;
	}
}
