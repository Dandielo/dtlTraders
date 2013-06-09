package net.dandielo.citizens.traders_v3.traders;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.tNpc;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.wallet.Wallet;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;

public abstract class Trader implements tNpc {
	
	/**
	 * All registered click functions for each trader type
	 */
	private static Map<Class<? extends Trader>, List<Method>> handlers = new HashMap<Class<? extends Trader>, List<Method>>();
	
	/**
	 * Register functions for the specified type
	 * @param clazz
	 * the trader type that will be looked through functions
	 */
	public static void registerHandlers(Class<? extends Trader> clazz)
	{
		//debug info
		Debugger.info("Registering click handlers for trader type: ", clazz.getSimpleName());
		
		List<Method> methods = new ArrayList<Method>();
		for ( Method method : clazz.getMethods() )
			if ( method.isAnnotationPresent(ClickHandler.class) )
				methods.add(method);
		handlers.put(clazz, methods);
	}

	/**
	 * Permissions manager instance
	 */
	protected Perms perms = Perms.perms;
	
	/**
	 * Locale manager instance
	 */
	protected LocaleManager locale = LocaleManager.locale;
	
	/*
	 * Trader const data, nothing of these changes during the transaction
	 */
	protected Settings settings;
	protected Wallet wallet;
	protected Stock stock;
	
	/*
	 * Player related data, includes the player and his base stock that will be used to restore items when switching from buy stock to sell stock, or when closing the inventory when in buy stock
	 */
	protected Player player;
	
	/*
	 * Temporary trader data
	 */
	protected Inventory inventory;
	protected tNpcStatus baseStatus;
	protected tNpcStatus status;
	
	/*
	 * Temporary item data
	 */
	private int lastSlot = -1;
	private StockItem selectedItem = null;
	
	/**
	 * Creates a new ServerTrader type based on the trader and wallet trait. It also assigns a player to the new created trader. 
	 * @param trader
	 * the trader trait that holds all setting for this trader
	 * @param wallet
	 * the wallet trait that is used to complete transactions
	 * @param player
	 * the assigned player
	 */
	public Trader(TraderTrait trader, WalletTrait wallet, Player player)
	{
		//debug info
		Debugger.low("Creating a trader, for: ", player.getName());
		
		//set all constant data
		settings = trader.getSettings();
		status = getDefaultStatus();
		stock = trader.getStock();
		this.wallet = wallet.getWallet();
		this.player = player;
	}
	
	/**
	 * Get all specified settings for this trader,
	 * @return
	 * Settings applied to the trader
	 */
	public Settings getSettings()
	{
		return settings;
	}
	
	/**
	 * @return
	 * the current status for the trader
	 */
	public tNpcStatus getStatus() 
	{
		return status;
	}
	
	/**
	 * @return
	 * the the traders stock
	 */
	public Stock getStock()
	{
		return stock;
	}
	
	/**
	 * @return
	 * the npc that is a trader
	 */
	public NPC getNPC()
	{
		return settings.getNPC();
	}
	
	/**
	 * Sets the new status checking if the new status is a base status, SELL or BUY. 
	 * Checking it is essential for setting the right UI and the "back" button. 
	 * Base status tell the trader to what stock he should go back.
	 * @param newStatus
	 * the new applied status
	 */
	public void parseStatus(tNpcStatus newStatus)
	{
		//set the general status variable
	    status = newStatus;
	    
	    //parse the newStatus, this will determine if the new status is a base status (stock switching)
		baseStatus = tNpcStatus.parseBaseManageStatus(baseStatus, newStatus);
	}
	
	@Override
	public void onManageInventoryClick(InventoryClickEvent e)
	{
		//use the click parser
		inventoryClickHandler(e);
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e)
	{ 
		//use the click parser
		inventoryClickHandler(e);
	}
	
	/**
	 * Handles the inventory click event by using registered click handlers for the given type
	 * @param e
	 */
	private void inventoryClickHandler(InventoryClickEvent e)
	{		
		//debug info
		Debugger.low("Handling click event");
		
		//check if the clicked inventoru=y is the tope or the bottom one
        boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
		
		//get all click handlers for the calling class
		List<Method> methods = handlers.get(getClass());
		for ( Method method : methods )
		{
			//get the handler information
			ClickHandler handler = method.getAnnotation(ClickHandler.class);

		//	//debug info (we don't need so much debug)
		//	Debugger.info("Checking shift click requirement");
			if ( !handler.shift() ? !e.isShiftClick() : true )
			{
			//	//debug info (we don't need so much debug)
			//	Debugger.info("Checking trader status requirement");
				if ( checkStatusWith(handler.status()) && handler.inventory().equals(top) )
				{
					try 
					{
						//debug info
						Debugger.low("Executing method: ", ChatColor.AQUA, method.getName());
						method.invoke(this, e);
					} 
					catch (Exception ex) 
					{
						//debug info
						Debugger.critical("While executing inventory click event");
						Debugger.critical("Exception: ", ex.getClass().getSimpleName());
						Debugger.critical("Method: ", method.getName());
						Debugger.critical("Trader: ", this.getSettings().getNPC().getName(), ", player: ", player.getName());
						Debugger.critical(" ");
						Debugger.critical("Exception message: ", ex.getMessage());
						Debugger.high("Stack trace: ", StringTools.stackTrace(ex.getStackTrace()));
						
						//cancel the event because of the exception!
						e.setCancelled(true);
					}
				}
			}
		}
		//debug info, shows if the event was canceled or not
		Debugger.info("Event cancelled: ", e.isCancelled());
	}
	
	/**
	 * Called when a player buys an item. 
	 * @return
	 * true if the player has enough money to buy the item
	 */
	protected boolean sellTransaction()
	{
		return sellTransaction(0);	
	}
	
	/**
	 * Called when a player buys an item. 
	 * @return
	 * true if the player has enough money to buy the item
	 */
	protected boolean sellTransaction(int slot)
	{
		if ( wallet.withdraw(player, stock.parsePrice(selectedItem, slot)) )
		{
			wallet.deposit(this, stock.parsePrice(selectedItem, slot));
			return true;
		}
		return false;
	}
	
	/**
	 * Called when a player sells an item.
	 * @return
	 * true if the trader has enough money to payoff the player
	 */
	protected boolean buyTransaction()
	{
		return buyTransaction(1);
	}

	/**
	 * Called when a player sells an item.
	 * @return
	 * true if the trader has enough money to payoff the player
	 */
	protected boolean buyTransaction(int scale)
	{
		if ( wallet.withdraw(this, stock.parsePrice(selectedItem, 0)*scale) )
		{
			wallet.deposit(player, stock.parsePrice(selectedItem, 0)*scale);
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the players inventory resetting all transaction lores.
	 */
	protected void updatePlayerInventory()
	{
		//the inventory that will be reseted
		Inventory inv = player.getInventory();
		
		//save the selectedItem temporary
		StockItem selected = selectedItem;
		
		int i = 0;
		for ( ItemStack item : inv.getContents() )
		{
			if ( selectAndCheckItem(item, "buy") )
			{
				//check if a lore cann be added
				if ( item.getAmount() >= selectedItem.getAmount() )
				{
				    //set the new lore
				    inv.setItem(i, NBTUtils.addLore(NBTUtils.cleanItem(item), selectedItem.getTempLore(status, item.clone())));
				}
				else
					//clean the item from any lore
					inv.setItem(i, NBTUtils.cleanItem(item));
			}			
			//next item please
			i++;
		}
		
		//reassign the last selected item
		selectedItem = selected;
	}
	
	/**
	 * Updates the players inventory resetting all transaction lores.
	 */
	protected void setSpecialBlockPrices()
	{
		//the inventory that will be reseted
		Inventory inv = player.getInventory();
		
		//save the selectedItem temporary
		StockItem selected = selectedItem;
		
		int i = 0;
		for ( ItemStack item : inv.getContents() )
		{
			if ( selectAndCheckNewItem(item) && status.equals(tNpcStatus.MANAGE_PRICE) )
			{
				double value = TGlobalSettings.getBlockValue(item);
				if ( value != 1.0 )
				{
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GOLD + "Block value: " + ChatColor.YELLOW + String.format("%.2f", value) );
					inv.setItem(i, NBTUtils.addLore(NBTUtils.cleanItem(item), lore));
				}
			}
			else
			{
				if ( item != null )
				    inv.setItem(i, NBTUtils.cleanItem(item));
			}
			//next item please
			i++;
		}
		
		//reassign the last selected item
		selectedItem = selected;
	}
	
	/*
	 * Methods that helps us to choose and execute all actions we want
	 */
	
	/**
	 * @return
	 * true if the players inventory has enough place to buy the clicked item
	 */
	protected final boolean inventoryHasPlace()
	{
		return inventoryHasPlace(0);
	}

	/**
	 * @param
	 * slot is used for multiple amounts selection
	 * @return
	 * true if the players inventory has enough place to buy the clicked item
	 */
	protected final boolean inventoryHasPlace(int slot) 
	{
		//debug info
		Debugger.info("Checking players inventory space");
		Debugger.info("Player: ", player.getName(), ", item: ", selectedItem.getItem().getType().name().toLowerCase());
				
		return _inventoryHasPlace(selectedItem.getAmount(slot));
	}

	/**
	 * @param
	 * amount that we want to add to the inventory. If the amount reaches 0 there is place :)
	 * @return
	 * true if the players inventory has enough place to buy the clicked item
	 */
	protected final boolean _inventoryHasPlace(int amount) 
	{
		if ( inventory.firstEmpty() >= 0 && inventory.firstEmpty() < inventory.getSize() )
			return true;
		
		//the players inventory
		PlayerInventory inventory = player.getInventory();
		
		//the amount left to add to the inventory
		int amountLeft = amount;
		
		//get all item stack with the same type
		for ( ItemStack item : inventory.all(selectedItem.getItem().getType()).values() )
		{
			if ( selectedItem.equalsWeak(ItemUtils.createStockItem(item)) )
			{
				if ( item.getAmount() + amountLeft <= item.getMaxStackSize() )
					return true;
				
				if ( item.getAmount() < item.getMaxStackSize() ) {
					amountLeft = ( item.getAmount() + amountLeft ) % item.getMaxStackSize(); 
				}
				
				if ( amountLeft <= 0 )
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds the selected item to the inventory
	 * @return
	 * true if it was added successful 
	 */
	protected final boolean addToInventory() 
	{
		return addToInventory(0);
	}

	/**
	 * Adds the selected item to the inventory
	 * @param slot that holds the amount that will be added (multiple amounts slot)
	 * @return
	 * true if it was added successful 
	 */
	protected final boolean addToInventory(int slot) 
	{
		//debug info
		Debugger.info("Adding item to players inventory");
		Debugger.info("Player: ", player.getName(), ", item: ", selectedItem.getItem().getType().name().toLowerCase());
		
		//adds the item to the "eventInventory" 
		return _addToInventory(selectedItem.getAmount(slot));
	}

	/**
	 * Adds the selected item to the inventory
	 * @param amount that will be added of the selected item to the inventory
	 * @return
	 * true if it was added successful 
	 */
	private boolean _addToInventory(int amount) 
	{
		PlayerInventory inventory = player.getInventory();
		int amountLeft = amount;

		for ( ItemStack item : inventory.all(selectedItem.getItem().getType()).values() ) 
		{
			if ( selectedItem.equalsWeak(ItemUtils.createStockItem(item)) )
			{
				//add amount to an item in the inventory, its done
				if ( item.getAmount() + amountLeft <= item.getMaxStackSize() ) {
					item.setAmount( item.getAmount() + amountLeft );
					return true;
				} 
				
				//add amount to an item in the inventory, but we still got some left
				if ( item.getAmount() < item.getMaxStackSize() ) {
					amountLeft = ( item.getAmount() + amountLeft ) % item.getMaxStackSize(); 
					item.setAmount(item.getMaxStackSize());
				}
					
				//nothing left
				if ( amountLeft <= 0 )
					return true;
			}
		}

		if ( inventory.firstEmpty() < inventory.getSize() 
				&& inventory.firstEmpty() >= 0 ) 
		{
			
			//new stack
			ItemStack is = selectedItem.getItem();
			is.setAmount(amountLeft);
			
			//set the item info the inv
			inventory.setItem(inventory.firstEmpty(), is);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the requested amount from players inventory
	 * @param slot
	 * the slot of the item where it sits in the players inventory
	 */
	protected final void removeFromInventory(int slot) 
	{
		removeFromInventory(slot, 1);
	}

	
	/**
	 * Removes the requested amount from players inventory
	 * @param slot
	 * the slot of the item where it sits in the players inventory
	 * @param scale 
	 * how many times should the amount be scaled that we want to remove
	 */
	protected final void removeFromInventory(int slot, int scale) 
	{
		//debug info
		Debugger.info("Removing item from players inventory");
		Debugger.info("Player: ", player.getName(), ", item: ", selectedItem.getItem().getType().name().toLowerCase());
				
		//removes from the event inventory
		_removeFromInventory(slot, selectedItem.getAmount(0) * scale);
	}	
	
	/**
	 * Removes the requested amount from players inventory
	 * @param slot
	 * the slot of the item where it sits in the players inventory
	 * @param amount
	 * the final amount to remove
	 */
	private void _removeFromInventory(int slot, int amount) 
	{
		Inventory inventory = player.getInventory();
		
		ItemStack item = inventory.getItem(slot);
		
		if ( item.getAmount() > amount )
		{
			item.setAmount(item.getAmount() - amount);
			inventory.setItem(slot, item);
		}
		else
			inventory.setItem(slot, null);
	}

	
	/**
	 * Selects a new item based on the given ItemStack
	 * @param item
	 * item that is going to be converted into StockItem and then selected
	 */
	protected void selectNewItem(ItemStack item)
	{
		selectedItem = item != null ? ItemUtils.createStockItem(item) : null;
	}

	/**
	 * Selects a new item based on the given ItemStack, and checks it if the conversion went good
	 * @param item
	 * item that is going to be converted into StockItem and then selected
	 * @return
	 * true if the item was selected successful
	 */
	protected boolean selectAndCheckNewItem(ItemStack item)
	{
		return (selectedItem = item != null && item.getTypeId() != 0 ? ItemUtils.createStockItem(item) : null) != null;
	}
	
	/**
	 * Clears the current selection
	 */
	protected void clearSelection()
	{
		selectedItem = null;
	}
	
	/** 
	 * Selects the item using the slot as search key. It will search in a stock depending on the actual traders status.
	 * The result will be stored and returned.
	 * 
	 * @param slot 
	 *     Search for item at slot
	 * @return 
	 *     the item found or null otherwise    
	 */
	protected StockItem selectItem(int slot)
	{
		return (selectedItem = stock.getItem(slot, baseStatus.asStock()));
	}

	/** 
	 * Selects the item using a bukkit item to compare with. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @return 
	 *     the item found, or null otherwise
	 */
	protected StockItem selectItem(ItemStack item)
	{
		return stock.getItem(ItemUtils.createStockItem(item), baseStatus.asStock());
	}
	
	/** 
	 * Selects the item using a bukkit item to compare with and the target stock. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @param stock
	 *     the stock to search for
	 * @return 
	 *     the item found, or null otherwise
	 */
	protected StockItem selectItem(ItemStack item, String bStock)
	{
		return stock.getItem(ItemUtils.createStockItem(item), bStock);
	}

	/** 
	 * Checks if the selected item has enough amounts declared to handle the clicked slot request
	 * 
	 * @param slot 
	 *     Search for item at slot
	 * @return 
	 *     true if the item was found, false otherwise   
	 */
	protected boolean checkItemAmount(int slot)
	{
		return selectedItem.getAmounts().size() > slot;
	}
	
	/** 
	 * Selects the item using the slot as search key. It will search in a stock depending on the actual traders status.
	 * The result will be checked true if an item was found false otherwise. The item found will be stored.
	 * 
	 * @param slot 
	 *     Search for item at slot
	 * @return 
	 *     true if the item was found, false otherwise   
	 */
	protected boolean selectAndCheckItem(int slot)
	{
		return (selectedItem = stock.getItem(slot, baseStatus.asStock())) != null;
	}
	
	/** 
	 * Selects the item using a bukkit item to compare with. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @return 
	 *     true if the item was found, false otherwise 
	 */
	protected boolean selectAndCheckItem(ItemStack item)
	{
		return (selectedItem = item != null && item.getTypeId() != 0 ? stock.getItem(ItemUtils.createStockItem(item), baseStatus.asStock()) : null ) != null;
	}
	
	/** 
	 * Selects the item using a bukkit item to compare with. If a similar item is found it will be stored.
	 * 
	 * @param item 
	 *     Item to compare with
	 * @param bStock
	 *     stock to look for the item
	 * @return 
	 *     true if the item was found, false otherwise   
	 */
	protected boolean selectAndCheckItem(ItemStack item, String bStock)
	{
		return (selectedItem = item != null && item.getTypeId() != 0 ? stock.getItem(ItemUtils.createStockItem(item), bStock) : null ) != null;
	}
	
	/** 
	 * @return true if there is a item stored, false otherwise
	 */
	protected boolean hasSelectedItem()
	{
		return selectedItem != null;
	}
	
	/**
	 * @return 
	 *     the stored item 
	 */
	protected StockItem getSelectedItem()
	{
		return selectedItem;
	}
	
	/** 
	 * Checks if the currents trader status is present in the given array
	 *
	 * @param stat
	 *     array to check
	 */
	protected boolean checkStatusWith(tNpcStatus[] stat)
	{
		for ( tNpcStatus s : stat )
			if ( s.equals(status) )
				return true;
		return false;
	}
	
	/** 
	 * Depending on the config value, this function will check if the given slot was clicked 2 times in a row
	 * 
	 * @param slot
	 *     Inventory slot to be checked
	 */
	protected boolean handleClick(int slot)
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
	 */
	protected boolean hitTest(int slot, String item)
	{
		return Settings.getUiItems().get(item).equals(inventory.getItem(slot));
	}
	
	/**
	 * @return
	 * the start stock (status) for the specified trader
	 */
	protected tNpcStatus getDefaultStatus()
	{
		return tNpcStatus.baseStatus(settings.getStockStart());
	}

	/**
	 * @return
	 * the management start stock (status) for the specified trader
	 */
	protected tNpcStatus getDefaultManagementStatus()
	{
		return tNpcStatus.baseManagementStatus(settings.getManagerStockStart());
	}

	/**
	 * @param npc
	 * npc that will be checked
	 * @return
	 * true if the traders NPC is equal to the given NPC
	 */
	public boolean equals(NPC npc)
	{
		return settings.getNPC().getId() == npc.getId();
	}
	
}
