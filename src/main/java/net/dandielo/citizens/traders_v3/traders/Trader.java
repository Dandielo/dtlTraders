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
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent.TransactionResult;
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
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;

@SuppressWarnings("deprecation")
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
		dB.info("Registering click handlers for trader type: ", clazz.getSimpleName());
		
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
		dB.low("Creating a trader, for: ", player.getName());
		
		//set all constant data
		settings = trader.getSettings();
		status = getDefaultStatus();
		stock = trader.getStock().toPlayerStock(player);
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
	 * the player that talks with the trader
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * @return
	 * the npc that is a trader
	 */
	public NPC getNPC()
	{
		return settings.getNPC();
	}
	
	public TraderTransactionEvent transactionEvent(TransactionResult result)
	{
		return (TraderTransactionEvent) new TraderTransactionEvent(this, player, selectedItem, TransactionResult.INVENTORY_FULL).callEvent();
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
		dB.low("Handling click event");
		
		//check if the clicked inventoru=y is the tope or the bottom one
        boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
		
		//get all click handlers for the calling class
		List<Method> methods = handlers.get(getClass());
		for ( Method method : methods )
		{
			//get the handler information
			ClickHandler handler = method.getAnnotation(ClickHandler.class);

			if ( !handler.shift() ? !e.isShiftClick() : true )
			{
				if ( checkStatusWith(handler.status()) && handler.inventory().equals(top) )
				{
					try 
					{
						//debug info
						dB.low("Executing method: ", ChatColor.AQUA, method.getName());
						method.invoke(this, e);
					} 
					catch (Exception ex) 
					{
						//debug info
						dB.critical("While executing inventory click event");
						dB.critical("Exception: ", ex.getClass().getSimpleName());
						dB.critical("Method: ", method.getName());
						dB.critical("Trader: ", this.getSettings().getNPC().getName(), ", player: ", player.getName());
						dB.critical(" ");
						dB.critical("Exception message: ", ex.getMessage());
						dB.high("Stack trace: ", StringTools.stackTrace(ex.getStackTrace()));
						
						//cancel the event because of the exception!
						e.setCancelled(true);
					}
				}
			}
		}
		//debug info, shows if the event was canceled or not
		dB.info("Event cancelled: ", e.isCancelled());
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
		if ( wallet.withdraw(player, stock.parsePrice(selectedItem, "sell", selectedItem.getAmount(slot))) )
		{
			wallet.deposit(this, stock.parsePrice(selectedItem, "sell", selectedItem.getAmount(slot)));
			
			//Trader log
			//TraderStats.traderLog(this, "buy", selectedItem, selectedItem.getAmount());
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
		if ( wallet.withdraw(this, stock.parsePrice(selectedItem, "buy", selectedItem.getAmount())*scale) )
		{
			wallet.deposit(player, stock.parsePrice(selectedItem, "buy", selectedItem.getAmount())*scale);
			
			//Trader log
			//TraderStats.traderLog(this, "sell", selectedItem, scale);
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
				    inv.setItem(i, NBTUtils.addLore(NBTUtils.cleanItem(item), Price.loreRequest(stock.parsePrice(selectedItem, "buy", item.getAmount()), new ArrayList<String>(), baseStatus)));
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
	public void setSpecialBlockPrices()
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
	
	public void saveItemsUpponLocking()
	{
		//debug normal
		dB.normal("Clearing the stock to set it with new items");
		
		List<StockItem> oldItems = stock.getStock(baseStatus.asStock());
		dB.low("Old stock size: ", oldItems.size());
		
		stock.clearStock(baseStatus.asStock());
		dB.low("Old stock size after clearing: ", oldItems.size());
				
		int slot = 0;
		//save each item until stockSize() - uiSlots() are reached
		for ( ItemStack bItem : inventory.getContents() )
		{
			//check if the given item is not null
			if ( bItem != null && !stock.isUiSlot(slot) )
			{
				//to stock item
				StockItem sItem = ItemUtils.createAbstractStockItem(bItem);
				
				StockItem matchedItem = null;
				//match old items to persist item data
				for ( StockItem item : oldItems )
					if ( matchedItem == null && item.equalsStrong(sItem) )
						matchedItem = item; 
				
				if ( matchedItem != null ) 
				{ 
					//update just its slot 
					matchedItem.setSlot(slot); 

					//add to the new stock 
					stock.addItem(matchedItem, baseStatus.asStock()); 
				} 
				else 
				{ 
					//set the items new slot 
					sItem.setSlot(slot); 

					//add to stock 
					stock.addItem(sItem, baseStatus.asStock()); 
				}
			}
			
			++slot;
		}
	}

	
	/**
	 * Function to lock and save the traders inventory on inventory closing
	 */
	public void lockAndSave()
	{
		//send message
		locale.sendMessage(player, "trader-managermode-stock-locked");
		
		//change status
		parseStatus(baseStatus);
		saveItemsUpponLocking();
	}
	
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
		dB.info("Checking players inventory space");
		dB.info("Player: ", player.getName(), ", item: ", selectedItem.getItem(false).getType().name().toLowerCase());
				
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
		for ( ItemStack item : inventory.all(selectedItem.getItem(false).getType()).values() )
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
		dB.info("Adding item to players inventory");
		dB.info("Player: ", player.getName(), ", item: ", selectedItem.getItem(false).getType().name().toLowerCase());
		
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

		for ( ItemStack item : inventory.all(selectedItem.getItem(false).getType()).values() ) 
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
			ItemStack is = selectedItem.getItem(true);
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
		dB.info("Removing item from players inventory");
		dB.info("Player: ", player.getName(), ", item: ", selectedItem.getItem(false).getType().name().toLowerCase());
				
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
		return (selectedItem = item != null && item.getTypeId() != 0 ? stock.getItem(ItemUtils.createAbstractStockItem(item), bStock) : null ) != null;
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
