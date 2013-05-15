package net.dandielo.citizens.traders_v3.traders;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;

public abstract class Trader implements tNpc {
	//Click handlers
	private static Map<Class<? extends Trader>, List<Method>> handlers = new HashMap<Class<? extends Trader>, List<Method>>();
	
	public static void registerHandlers(Class<? extends Trader> clazz)
	{
		List<Method> methods = new ArrayList<Method>();
		for ( Method method : clazz.getMethods() )
			if ( method.isAnnotationPresent(ClickHandler.class) )
				methods.add(method);
		handlers.put(clazz, methods);
	}
	
	//temp data
	private int lastSlot = -1;
	private StockItem selectedItem = null;
	
	//the trader class
	protected Settings settings;
	protected Wallet wallet;
	protected Stock stock;
	protected Player player;
	
	protected Inventory inventory;
	protected Status baseStatus;
	protected Status status;
	
	//constructor
	public Trader(TraderTrait trader, WalletTrait wallet, Player player)
	{
		settings = trader.getSettings();
		status = getDefaultStatus();
		stock = trader.getStock();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	//trader getters
	public Settings getSettings()
	{
		return settings;
	}
	
	public Status getStatus() 
	{
		return status;
	}
	
	public void parseStatus(Status newStatus)
	{
	    status = newStatus;
		baseStatus = Status.parseBaseManageStatus(baseStatus, newStatus);
	}
	
	public Stock getStock()
	{
		return stock;
	}
	
	public boolean equals(NPC npc)
	{
		return settings.getNPC().getId() == npc.getId();
	}
	
	//inventory click handler
	@Override
	public void onManageInventoryClick(InventoryClickEvent e)
	{
		inventoryClickParser(e);
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e)
	{ 
		inventoryClickParser(e);
	}
	
	private final void inventoryClickParser(InventoryClickEvent e)
	{
        boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
		
		//get all handlers
		List<Method> methods = handlers.get(getClass());
		for ( Method method : methods )
		{
			ClickHandler handler = method.getAnnotation(ClickHandler.class);
			
			if ( !handler.shift() ? !e.isShiftClick() : true )
			{
				if ( checkStatusWith(handler.status()) && handler.inventory().equals(top) )
				{
					try 
					{
						method.invoke(this, e);
					} 
					catch (Exception ex) 
					{
						DtlTraders.severe("Error when handling click event");
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	/** Transaction methods */
	public boolean sellTransaction()
	{
		if ( wallet.withdraw(player, selectedItem.getPrice()) )
		{
			wallet.deposit(this, selectedItem.getPrice());
			return true;
		}
		return false;
	}
	
	public boolean buyTransaction()
	{
		if ( wallet.withdraw(this, selectedItem.getPrice()) )
		{
			wallet.deposit(player, selectedItem.getPrice());
			return true;
		}
		return false;
	}

	/** Helper methods */
	public void addToInventory()
	{
		player.getInventory().addItem(selectedItem.getItem());
	}
	
	/** 
	 * Selects the item using the slot as search key. It will search in a stock depending on the actual traders status.
	 * The result will be stored and returned.
	 * 
	 * @param slot 
	 *     Search for item at slot
	 * @return 
	 *     Returns the item found or null otherwise    
	 * 
	 * @author dandielo
	 */
	public StockItem selectItem(int slot)
	{
		return (selectedItem = stock.getItem(slot, status.asStock()));
	}

	/** 
	 * Selects the item using a bukkit item to compare with. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @return 
	 *     Returns the item found, or null otherwise
	 * @author dandielo
	 */
	public StockItem selectItem(ItemStack item)
	{
		return stock.getItem(ItemUtils.createStockItem(item), status.asStock());
	}

	/** 
	 * Selects the item using the slot as search key. It will search in a stock depending on the actual traders status.
	 * The result will be checked true if an item was found false otherwise. The item found will be stored.
	 * 
	 * @param slot 
	 *     Search for item at slot
	 * @return 
	 *     Returns true if the item was found, false otherwise   
	 * @author dandielo
	 */
	//TODO This might be not needed
	public boolean selectAndCheckItem(int slot)
	{
		return (selectedItem = stock.getItem(slot, status.asStock())) != null;
	}
	
	/** 
	 * Selects the item using a bukkit item to compare with. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @return 
	 *     Returns true if the item was found, false otherwise   
	 * @author dandielo
	 */
	//TODO This might be not needed
	public boolean selectAndCheckItem(ItemStack item)
	{
		return (selectedItem = stock.getItem(ItemUtils.createStockItem(item), status.asStock())) != null;
	}
	
	/** 
	 * @return returns true if there is a item stored, false otherwise
	 * @author dandielo 
	 */
	public boolean hasSelectedItem()
	{
		return selectedItem != null;
	}
	
	/**
	 * @return 
	 *     Returns the stored item 
	 * @author dandielo 
	 */
	public StockItem getSelectedItem()
	{
		return selectedItem;
	}
	
	/** 
	 * Checks if the currents trader status is present in the given array
	 *
	 * @param stat
	 *     array to check
	 *    
	 * @author dandielo
	 */
	public boolean checkStatusWith(Status[] stat)
	{
		for ( Status s : stat )
			if ( s.equals(status) )
				return true;
		return false;
	}
	
	/** 
	 * Depending on the config value, this function will check if the given slot was clicked 2 times in a row
	 * 
	 * @param slot
	 *     Inventory slot to be checked
	 *     
	 * @author dandielo
	 */
	public boolean handleClick(int slot)
	{
		if ( Settings.dClickEvent() )
			return lastSlot == (lastSlot = slot); 
		else
			return true;
	}

	/** 
	 * Makes a hit-test with the slot given and a item saved in the config file (UI items)
	 * 
	 * @param slot
	 *     slot to test
	 * @param item
	 *     the name of the item, that name is used in the config file
	 * 
	 * @author dandielo
	 */
	public boolean hitTest(int slot, String item)
	{
		return Settings.getUiItems().get(item).equals(inventory.getItem(slot));
	}

	//Trader status enum
	public static enum Status
	{
		SELL, BUY, SELL_AMOUNTS, MANAGE_SELL, MANAGE_BUY, MANAGE_PRICE, MANAGE_AMOUNTS, MANAGE_LIMITS;
		
		public boolean inManagementMode()
		{
			return !( this.equals(SELL) || this.equals(BUY) || this.equals(SELL_AMOUNTS) ); 
		}
		
		public static Status parseBaseManageStatus(Status oldStatus, Status newStatus)
		{
			return newStatus.equals(MANAGE_SELL) || newStatus.equals(MANAGE_BUY) ? newStatus : oldStatus;
		}
		
		public static Status baseManagementStatus(String status)
		{
			if ( MANAGE_SELL.name().toLowerCase().contains(status) )
				return MANAGE_SELL;
			return MANAGE_BUY;
		}
		
		public static Status baseStatus(String status)
		{
			if ( SELL.name().toLowerCase().equals(status) )
				return SELL;
			return BUY;
		}

		public String asStock() {
			return this.equals(BUY) || this.equals(MANAGE_BUY) ? "buy" : "sell";
		}
	}
	
	public Status getDefaultStatus()
	{
		return Status.baseStatus(settings.getStockStart());
	}
	
	public Status getDefaultManagementStatus()
	{
		return Status.baseManagementStatus(settings.getManagerStockStart());
	}
	
	
}
