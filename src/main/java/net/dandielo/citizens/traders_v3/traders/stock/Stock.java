package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern.Type;
import net.dandielo.citizens.traders_v3.traders.patterns.types.ItemPattern;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Stock implements InventoryHolder {
	protected Map<String, List<StockItem>> stock = new HashMap<String, List<StockItem>>();
	protected Settings settings;
	
	protected Stock(Settings settings)
	{
		//debug info
		int size = settings.getStockSize();
		dB.info("Creating stock with name: ", settings.getStockName(), ", size: ", size);
		
        if( size <= 0 || size > 6 ){
        	throw new IllegalArgumentException("Size must be between 1 and 6");}
        
        this.settings = settings;
        
        stock.put("sell", new ArrayList<StockItem>());
        stock.put("buy", new ArrayList<StockItem>());
	}

	//stock changing
	public StockPlayer toPlayerStock(Player player)
	{
		dB.high("Creating player specific stock");
		StockPlayer stock = new StockPlayer(settings, player);
		stock.stock = this.stock;

		dB.high("Creating player specific stock");
		//remove all pattern items
		Iterator<StockItem> it = stock.stock.get("sell").iterator();
		while(it.hasNext()) if ( it.next().hasAttribute(PatternItem.class) ) it.remove(); 
		it = stock.stock.get("buy").iterator();
		while(it.hasNext()) if ( it.next().hasAttribute(PatternItem.class) ) it.remove(); 
		dB.high("Creating player specific stock");

		//update stock with pattern items
		if ( settings.getPatterns() != null && !settings.getPatterns().isEmpty() )
		{
			dB.high("Loaded all needed patterns");
			List<Pattern> patterns = PatternManager.getPatterns(settings.getPatterns());
			
			if ( !patterns.isEmpty() )
			{
				for ( Pattern pattern : patterns )
				{
					dB.high("Check permission for pattern: " + pattern.getName());
					if ( pattern.getType().equals(Type.ITEM) && 
							Perms.hasPerm(player, "dtl.trader.patterns." + pattern.getName()) )
					{
						dB.high("Update stock with pattern: " + pattern.getName());
						((ItemPattern)pattern).updateStock(stock.stock.get("sell"), "sell", player);
						((ItemPattern)pattern).updateStock(stock.stock.get("buy"), "buy", player);
					}
				}
			}
		}
		
		return stock;
	}

	//inventory size
	public final int getFinalInventorySize()
	{
		return settings.getStockSize()*9;
	}

	public boolean isUiSlot(int slot)
	{
		return slot < getFinalInventorySize() && slot >= getFinalInventorySize() - 4;
	}
	
	/**
	 * Overrides the old list with a new one, that way all old items will persist opon clearing
	 * @param stock
	 */
	public void clearStock(String stock)
	{
		this.stock.put(stock, new ArrayList<StockItem>());
	}
	
	public void load(DataKey data) {
	}

	public void save(DataKey data) {
	}

	/*item operations*/
	public StockItem getItem(int slot, String stock)
	{
		StockItem resultItem = null;
		for ( StockItem stockItem : this.stock.get(stock) )
			if ( stockItem.getSlot() == slot )
				resultItem = stockItem;
		return resultItem;
	}
	
	public StockItem getItem(StockItem item, String stock)
	{
		for (StockItem sItem : this.stock.get(stock))
		{
			if (sItem.similar(item))
			{
				return sItem;
			}
		}
		return null;
	}

	/**
	 * For the given item changes the items amounts attribute setting one or more new amounts to it, depending on them items the inventory has.
	 * @param inventory
	 * inventory to check for item amounts
	 * @param si
	 * stock item to apply this amounts to
	 */
	public static void saveNewAmounts(Inventory inventory, StockItem si) {
		si.getAmounts().clear();
		for ( ItemStack is : inventory.getContents() ) 
			if ( is != null ) 
				si.addAmount(is.getAmount());
		
		if ( si.getAmounts().size() > 1 )
			si.getAmounts().remove(si.getAmounts().size()-1);
	}
	
	public abstract void addItem(StockItem item, String stock);
	public abstract void removeItem(StockItem item, String stock);
	
	/*abstract methods*/
	public abstract Inventory getInventory(TEntityStatus status);
	public abstract Inventory getManagementInventory(TEntityStatus baseStatus, TEntityStatus status);
	public abstract void setInventory(Inventory inventory, TEntityStatus status);
	public abstract void setAmountsInventory(Inventory inventory, TEntityStatus status, StockItem item);
	public abstract void setManagementInventory(Inventory inventory, TEntityStatus baseStatus, TEntityStatus status);
	
	//oposite stock
	public static String opositeStock(String stock)
	{
		return stock.equals("sell") ? "buy" : "sell"; 
	}

	public List<StockItem> getStock(String stock)
	{
		return this.stock.get(stock);
	}
}
