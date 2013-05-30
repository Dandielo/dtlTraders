package net.dandielo.citizens.traders_v3.traders.types;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderClickEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

@tNpcType(name="server", author="dandielo")
public class Server extends Trader {
	/**
	 * Permissions manager instance
	 */
	Perms perms = Perms.perms;
	

	public Server(TraderTrait trader, WalletTrait wallet, Player player) {
		super(trader, wallet, player);
	}

	@Override
	public void onLeftClick(ItemStack itemInHand)
    {
		//send a event
		TraderClickEvent e = (TraderClickEvent) new TraderClickEvent(this, player, !TGlobalSettings.mmRightToggle(), true).callEvent();
		
		//check settings
		if ( e.isManagerToggling() ) return;

		//check permission
		if ( !perms.has(player, "dtl.trader.manage") ) return;
		
		//if air every item in hand is valid
		ItemStack itemToToggle = TGlobalSettings.mmItemToggle();
		if ( itemInHand != null && !itemToToggle.getType().equals(Material.AIR) )
		{
			//if id are different then cancel the event
			if ( itemToToggle.getTypeId() != itemInHand.getTypeId() ) return;
		}
		
		toggleManageMode("left");
	}

	@Override
	public boolean onRightClick(ItemStack itemInHand)
	{
		
		//right click toggling is enabled, handle it and check permission
		if ( TGlobalSettings.mmRightToggle() && perms.has(player, "dtl.trader.manage") )
		{
			//if air then chane to stick item
			ItemStack itemToToggle = TGlobalSettings.mmItemToggle();
			if ( itemToToggle.getType().equals(Material.AIR) )
				itemToToggle.setType(Material.STICK);

			//if id's in hand and for toggling are the same manage the mode change
			if ( itemInHand != null && itemToToggle.getTypeId() == itemInHand.getTypeId() ) 
			{
				//send a event when manager mode toggling
				TraderClickEvent e = (TraderClickEvent) new TraderClickEvent(this, player, true, false).callEvent();
				
				//we can still stop mmToggling
				if ( e.isManagerToggling() )
				{
					toggleManageMode("right");
					
					//stop event execution
					return false;
				}
			}
			else
			{
				//maybe we can do this bit more nicer?
				
				//send a event withoug toggling
				new TraderClickEvent(this, player, false, false).callEvent();
			}
		}
		else
		{
			//send a event withoug toggling
			new TraderClickEvent(this, player, false, false).callEvent();
		}
		
			
		//debug info
		Debugger.info(this.getClass().getSimpleName(), " Trader right click event, by: ", player.getName());
		
		if ( status.inManagementMode() )
			inventory = stock.getManagementInventory(baseStatus, status);
		else
			inventory = stock.getInventory(status);
		parseStatus(status);
		
		updateInventory();
		
		//register the inventory as a traderInventory
		tNpcManager.instance().registerOpenedInventory(player, inventory);
		
		//open the traders inventory
		player.openInventory(inventory);
		
		return true;
	}
	
	public void toggleManageMode(String clickEvent)
	{
		//debug info
		Debugger.info(this.getClass().getSimpleName(), " Trader ", clickEvent, " click event, by: ", player.getName());
		
		if ( status.inManagementMode() )
		{
			locale.sendMessage(player, "trader-managermode-disabled", "npc", getNPC().getName());
			parseStatus(getDefaultStatus());
		}
		else
		{
			locale.sendMessage(player, "trader-managermode-enabled", "npc", getNPC().getName());
			parseStatus(getDefaultManagementStatus());
		}
	}

	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.TRADER)
	public void generalUI(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("General UI checking");
		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			//debug info
			Debugger.info("Hit tests");
			
			if ( hitTest(slot, "buy") )
			{
				//debug low
				Debugger.low("Buy stock hit test");
				
				//send message
				locale.sendMessage(player, "trader-stock-toggled", "stock", "#stock-buy");
				
				//change status
				parseStatus(tNpcStatus.BUY);
			}
			else
			if ( hitTest(slot, "sell") )
			{
				//debug low
				Debugger.low("Sell stock hit test");
				
				//send message
				locale.sendMessage(player, "trader-stock-toggled", "stock", "#stock-sell");
				
				//change status
				parseStatus(tNpcStatus.SELL);
			}
			else
			if ( hitTest(slot, "back") )
			{
				//debug low
				Debugger.low("Babck to stock hit test");
				
				//send message
				locale.sendMessage(player, "trader-stock-back");
				
				//change status
				parseStatus(tNpcStatus.SELL);
			}
			stock.setInventory(inventory, getStatus());
		}
		e.setCancelled(true);
	}
	
	@SuppressWarnings("static-access")
	@ClickHandler(
	status = {tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_UNLOCKED, tNpcStatus.MANAGE_AMOUNTS, tNpcStatus.MANAGE_PRICE, tNpcStatus.MANAGE_LIMIT}, 
	inventory = InventoryType.TRADER)
	public void manageUI(InventoryClickEvent e)
	{		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			if ( hitTest(slot, "buy") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#stock-buy");
				
				//change status
				parseStatus(tNpcStatus.MANAGE_BUY);
			}
			else
			if ( hitTest(slot, "sell") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#stock-sell");
				
				//change status
				parseStatus(tNpcStatus.MANAGE_SELL);
			}
			else
			if ( hitTest(slot, "back") )
			{
				//if its backing from amounts managing save those amounts
				if ( status.equals(tNpcStatus.MANAGE_AMOUNTS) )
					stock.saveNewAmounts(inventory, getSelectedItem());

				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#stock");
				
				//change status
				parseStatus(baseStatus);
			}
			else
			if ( hitTest(slot, "price") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#price");
				
				//change status
				parseStatus(tNpcStatus.MANAGE_PRICE);
			}
			else
			if ( hitTest(slot, "lock") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-stock-locked");
				
				//change status
				parseStatus(baseStatus);
				saveItemsUpponLocking();
			}
			else
			if ( hitTest(slot, "unlock") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-stock-unlocked");
				
				//change status
				parseStatus(tNpcStatus.MANAGE_UNLOCKED);
			}
			else
			if ( hitTest(slot, "limit") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#limit");
				
				//change status
				parseStatus(tNpcStatus.MANAGE_LIMIT);
			}
			stock.setManagementInventory(inventory, baseStatus, status);
			e.setCancelled(true);
		}
	}
	
	@ClickHandler(status = {tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.TRADER)
	public void sellAmountsItems(InventoryClickEvent e)
	{
		e.setCancelled(true);
		//check permission
		if ( !perms.has(player, "dtl.trader.sell") ) return;
		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) ) return;

		if ( checkItemAmount(slot) )
		{
			if ( handleClick(e.getRawSlot()) )
			{
				if ( !inventoryHasPlace(slot) )
				{
					//send message
					locale.sendMessage(player, "trader-transaction-failed-inventory");
				}
				else
				if ( !sellTransaction(slot) )
				{
					//send message
					locale.sendMessage(player, "trader-transaction-failed-player-money");
				}
				else
				{
					addToInventory(slot);

					//send message
					locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
							"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
							"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.format("%.2f", stock.parsePrice(getSelectedItem(), slot)).replace(',', '.'));
					
					//update inventory - lore
					updateInventory();
				}
			}
			else
			{
				//informations about the item some1 wants to buy
				locale.sendMessage(player, "trader-transaction-item",
						"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
						"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), slot)).replace(',', '.'));
			}
		}
	}
	
	@ClickHandler(status = {tNpcStatus.SELL}, inventory = InventoryType.TRADER)
	public void sellItems(InventoryClickEvent e)
	{
		e.setCancelled(true);
		//check permission
		if ( !perms.has(player, "dtl.trader.sell") ) return;
		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) ) return;

		if ( e.isLeftClick() )
		{
			if ( selectAndCheckItem(slot) )
			{
				if ( getSelectedItem().hasMultipleAmounts() )
				{
					//send message
					locale.sendMessage(player, "trader-stock-toggled", "stock", "#stock-amounts");
					
					//change status
					status = tNpcStatus.SELL_AMOUNTS;
					stock.setAmountsInventory(inventory, status, getSelectedItem());
				}
				else
				if ( handleClick(e.getRawSlot()) )
				{
					if ( !inventoryHasPlace() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-inventory");
					}
					else
					if ( !sellTransaction() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-player-money");
					}
					else
					{
						addToInventory();
						
						//send message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(getSelectedItem().getPrice()));
						
						//update inventory - lore
						updateInventory();
					}
				}
				else
				{
					//informations about the item some1 wants to buy
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.valueOf(getSelectedItem().getPrice()));
				}
			}
		}
		else
		{
			if ( selectAndCheckItem(slot) )
			{
				if ( handleClick(e.getRawSlot()) )
				{
					if ( !inventoryHasPlace() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-inventory");
					}
					else
					if ( !sellTransaction() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-player-money");
					}
					else
					{
						addToInventory();
						
						//send message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(getSelectedItem().getPrice()));
						
						//update inventory - lore
						updateInventory();
					}
				}
				else
				{
					//informations about the item some1 wants to buy
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.valueOf(getSelectedItem().getPrice()));
				}
			}
		}
	}

	@ClickHandler(status = {tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.PLAYER)
	public void sellAmountsSec(InventoryClickEvent e)
	{
		e.setCancelled(true);
	}
	
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY}, inventory = InventoryType.PLAYER)
	public void buyItems(InventoryClickEvent e)
	{
		e.setCancelled(true);
		//check permission
		if ( !perms.has(player, "dtl.trader.buy") ) return;
		
		clearSelection();
		int slot = e.getSlot();
		if ( e.isLeftClick() )
		{
			if ( selectAndCheckItem(e.getCurrentItem(), "buy") )
			{
				int scale = e.getCurrentItem().getAmount() / getSelectedItem().getAmount();
				if ( scale == 0 ) return;
				
				if ( handleClick(e.getRawSlot()) )
				{
					if ( !buyTransaction(scale) )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-trader-money", "npc", settings.getNPC().getName());
					}
					else
					{
						//remove the amount from inventory
						removeFromInventory(slot, scale);

						//send the transaction success message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#sold", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()*scale), "price", String.valueOf(stock.parsePrice(getSelectedItem(), 0)*scale));
						
						//update the inventory lore
						updateInventory();
					}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.valueOf(stock.parsePrice(getSelectedItem(), 0)));
				}
			}
		}
		else
		{
			if ( selectAndCheckItem(e.getCurrentItem(), "buy") )
			{
				int scale = e.getCurrentItem().getAmount() / getSelectedItem().getAmount();
				if ( scale == 0 ) return;
				
				if ( handleClick(e.getRawSlot()) )
				{
					if ( !buyTransaction() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-trader-money", "npc", settings.getNPC().getName());
					}
					else
					{
						//remove the amount from inventory
						removeFromInventory(slot);
						
						//send the transaction success message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#sold", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(stock.parsePrice(getSelectedItem(), 0)));

						//update the inventory lore
						updateInventory();
					}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.valueOf(stock.parsePrice(getSelectedItem(), 0)));
				}
			}
		}
	}
	
	
	
	/* manager mode handlers */
	@ClickHandler(status={tNpcStatus.MANAGE_UNLOCKED}, inventory=InventoryType.TRADER)
	public void setStock(InventoryClickEvent e)
	{
		Debugger.info("Unlocked stock click event");
	}
	
	@ClickHandler(status={tNpcStatus.MANAGE_UNLOCKED}, inventory=InventoryType.PLAYER)
	public void getStock(InventoryClickEvent e)
	{
		Debugger.info("Unlocked stock click event");
	}

	@ClickHandler(status={tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY}, inventory=InventoryType.TRADER, shift = true)
	public void itemsAttribs(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("Item managing click event");
		
		//select the item that should have the price changed
		if ( selectAndCheckItem(e.getSlot()) )
		{
			if ( e.isShiftClick() )
			{
				if ( e.isLeftClick() )
				{
					stock.setAmountsInventory(inventory, status, getSelectedItem());
					parseStatus(tNpcStatus.MANAGE_AMOUNTS);
				}
				else //if it's a shift rightclick
				{
					
				}
			}
			else //no shift click
			{
				if ( e.isLeftClick() )
				{
					if ( getSelectedItem().hasFlag(StackPrice.class) )
						getSelectedItem().removeFlag(StackPrice.class);
					else
						getSelectedItem().addFlag(".sp");
					
					locale.sendMessage(player, "key-change", 
							"key", "#stack-price", 
							"value", String.valueOf(getSelectedItem().hasFlag(StackPrice.class)));
				}
				else //if it's a shift rightclick
				{
					
				}
			}
		}
		e.setCancelled(true);
	}
	
	@ClickHandler(status={tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY}, inventory=InventoryType.PLAYER)
	public void itemsForStock(InventoryClickEvent e)
	{
	}
	
	/**
	 * Price managing for manager stock, this allows you to change prices for all items in your traders stock
	 * @param e
	 */
	@ClickHandler(status={tNpcStatus.MANAGE_PRICE}, inventory=InventoryType.TRADER)
	public void managePrices(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("Price managing click event");
		
		//select the item that should have the price changed
		if ( selectAndCheckItem(e.getSlot()) )
		{
			//show the current price in chat, if cursor is AIR
			if ( e.getCursor().getTypeId() == 0 )
			{
				//sends the message
				locale.sendMessage(player, "key-value", 
						"key", "#price", "value", getSelectedItem().getPriceFormated());
			}
			else
			{
				//adds the price attribute to the item
				Price price = getSelectedItem().getPriceAttr();
				
				//adds value to the current price
				if ( e.isLeftClick() )
				{
					//increases the price using specialBlockValue*cursorAmount
					price.increase(Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());
					
					//sends a message
					locale.sendMessage(player, "key-change", 
							"key", "#price", "value", getSelectedItem().getPriceFormated());
				}
				else
				//remove value from the current price
				if ( e.isRightClick() )
				{
					//decreases the price using specialBlockValue*cursorAmount
					price.decrease(Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());
					
					//sends a message
					locale.sendMessage(player, "key-change", 
							"key", "#price", "value", getSelectedItem().getPriceFormated());
				}
			}
		}
		e.setCancelled(true);
	}
	
	public void saveItemsUpponLocking()
	{
		//debug normal
		Debugger.normal("Clearing the stock to set it with new items");
		
		List<StockItem> oldItems = stock.getStock(baseStatus.asStock());
		Debugger.low("Old stock size: ", oldItems.size());
		
		stock.clearStock(baseStatus.asStock());
		Debugger.low("Old stock size after clearing: ", oldItems.size());
				
		int slot = 0;
		//save each item until stockSize() - uiSlots() are reached
		for ( ItemStack bItem : inventory.getContents() )
		{
			//check if the given item is not null
			if ( bItem != null && !stock.isUiSlot(slot) )
			{
				//to stock item
				StockItem sItem = ItemUtils.createStockItem(bItem);
				
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
	
	
	
	
	
	
	
	
	
	
	//shift handler
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_SELL}, shift = true, inventory = InventoryType.TRADER)
	public void topShift(InventoryClickEvent e)
	{
		if ( e.isShiftClick() )
		    e.setCancelled(true);
	}
	
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_SELL}, shift = true, inventory = InventoryType.PLAYER)
	public void botShift(InventoryClickEvent e)
	{
		if ( e.isShiftClick() )
		    e.setCancelled(true);
	}
	
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_AMOUNTS, tNpcStatus.MANAGE_PRICE, tNpcStatus.MANAGE_LIMIT}, shift = true, inventory = InventoryType.TRADER)
	public void topDebug(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("Inventory click, by: ", player.getName(), ", status: ", status.name().toLowerCase());
		Debugger.info("slot: ", e.getSlot(), ", left: ", e.isLeftClick(), ", shift: ", e.isShiftClick());
	}
	
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_AMOUNTS, tNpcStatus.MANAGE_PRICE, tNpcStatus.MANAGE_LIMIT}, shift = true, inventory = InventoryType.PLAYER)
	public void botDebug(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("Inventory click, by: ", player.getName(), ", status: ", status.name().toLowerCase());
		Debugger.info("slot: ", e.getSlot(), ", left: ", e.isLeftClick(), ", shift: ", e.isShiftClick());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	@Override
	public void onInventoryClick(InventoryClickEvent e) { 
		boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();

		if ( top )
			topClick(e);
		else
			bottomClick(e);
	}*/

	/*
	public void topClick(InventoryClickEvent e)
	{
		
		if ( e.isShiftClick() )
		{
			e.setCancelled(true);
			return;
		}
			
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			if ( hitTest(slot, "buy") )
			{
				status = tNpcStatus.BUY;
			}
			else
			if ( hitTest(slot, "sell") )
			{
				status = tNpcStatus.SELL;
			}
			else
			if ( hitTest(slot, "back") )
			{
				status = tNpcStatus.SELL;
			}
			stock.setInventory(inventory, getStatus());
		}
		else
		{
			if ( status.equals(tNpcStatus.SELL) )
			{
				if ( e.isRightClick() )
				{
					if ( handleClick(e.getRawSlot()) )
					{
						player.sendMessage(stock.getItem(slot, "sell").<Double>getData("p").toString());
					}
					else
					{
						player.sendMessage(stock.getItem(slot, "sell").toString());
					}
				}
				else
				{
					if ( handleClick(e.getRawSlot()) )
					{
						player.sendMessage(stock.getItem(slot, "sell").<Double>getData("p").toString());
					}
					else
					{
						player.sendMessage(stock.getItem(slot, "sell").toString());
					}
				}
			}
			else
			if ( status.equals(tNpcStatus.SELL_AMOUNTS) )
			{
				if ( handleClick(e.getRawSlot()) )
				{
					player.sendMessage(stock.getItem(slot, "sell").<Double>getData("p").toString());
				}
				else
				{
					player.sendMessage(stock.getItem(slot, "sell").toString());
				}
			}
			else
			if ( status.equals(tNpcStatus.BUY) )
			{
				if ( e.isRightClick() )
				{
					if ( handleClick(e.getRawSlot()) )
					{
						
					}
					else
					{
						
					}
				}
				else
				{
					if ( handleClick(e.getRawSlot()) )
					{
						
					}
					else
					{
						
					}
				}
			}
		}
		e.setCancelled(true);
	}

	public void bottomClick(InventoryClickEvent e)
	{

	}*/

}
