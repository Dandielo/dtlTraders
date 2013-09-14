package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderClickEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent.TransactionResult;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.TGlobalSettings;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.flags.NoStack;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;

@tNpcType(name="server", author="dandielo")
public class Server extends Trader {
	

	public Server(TraderTrait trader, WalletTrait wallet, Player player) {
		super(trader, wallet, player);
	}

	@Override
	public void onLeftClick(ItemStack itemInHand)
    {
		//send a event
		TraderClickEvent e = (TraderClickEvent) new TraderClickEvent(this, player, !TGlobalSettings.mmRightToggle(), true).callEvent();
		
		//check settings
		if ( !e.isManagerToggling() ) return;

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
		dB.info(this.getClass().getSimpleName(), " Trader right click event, by: ", player.getName());
		
		if ( status.inManagementMode() )
			inventory = stock.getManagementInventory(baseStatus, status);
		else
			inventory = stock.getInventory(status);
		parseStatus(status);
		
		updatePlayerInventory();
		
		//register the inventory as a traderInventory
		tNpcManager.instance().registerOpenedInventory(player, inventory);
		
		//open the traders inventory
		player.openInventory(inventory);
		
		return true;
	}
	
	public void toggleManageMode(String clickEvent)
	{
		//debug info
		dB.info(this.getClass().getSimpleName(), " Trader ", clickEvent, " click event, by: ", player.getName());
		
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
		dB.info("General UI checking");
		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			//debug info
			dB.info("Hit tests");
			
			if ( hitTest(slot, "buy") )
			{
				//debug low
				dB.low("Buy stock hit test");
				
				//send message
				locale.sendMessage(player, "trader-stock-toggled", "stock", "#stock-buy");
				
				//change status
				parseStatus(tNpcStatus.BUY);
			}
			else
			if ( hitTest(slot, "sell") )
			{
				//debug low
				dB.low("Sell stock hit test");
				
				//send message
				locale.sendMessage(player, "trader-stock-toggled", "stock", "#stock-sell");
				
				//change status
				parseStatus(tNpcStatus.SELL);
			}
			else
			if ( hitTest(slot, "back") )
			{
				//debug low
				dB.low("Babck to stock hit test");
				
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
			
			//TODO add lores to special blocks
			setSpecialBlockPrices();
			
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
					
					//send event
					transactionEvent(TransactionResult.INVENTORY_FULL);
				}
				else
				if ( !sellTransaction(slot) )
				{
					//send message
					locale.sendMessage(player, "trader-transaction-failed-player-money");
					
					//send event
					transactionEvent(TransactionResult.PLAYER_LACKS_MONEY);
				}
				else
				{
					//send event
					if ( transactionEvent(TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv() )
					    addToInventory(slot);

					//send message
					locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
							"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
							"amount", String.valueOf(getSelectedItem().getAmount(slot)), "price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount(slot))).replace(',', '.'));
					
					//update inventory - lore
					updatePlayerInventory();
				}
			}
			else
			{
				//informations about the item some1 wants to buy
				locale.sendMessage(player, "trader-transaction-item",
						"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount(slot)), 
						"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount(slot))).replace(',', '.'));
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
						
						//send event
						transactionEvent(TransactionResult.INVENTORY_FULL);
					}
					else
					if ( !sellTransaction() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-player-money");
						
						//send event
						transactionEvent(TransactionResult.PLAYER_LACKS_MONEY);
					}
					else
					{
						//send event
						if ( transactionEvent(TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv() )
						    addToInventory();

								    
						//send message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'));
						
						//update inventory - lore
						updatePlayerInventory();
					}
				}
				else
				{
					//informations about the item some1 wants to buy
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'));
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
						
						//send event
						transactionEvent(TransactionResult.INVENTORY_FULL);
					}
					else
					if ( !sellTransaction() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-player-money");
						
						//send event
						transactionEvent(TransactionResult.PLAYER_LACKS_MONEY);
					}
					else
					{
						//send event
						if ( transactionEvent(TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv() )
						    addToInventory();
						
						//send message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), 
								"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'));
						
						//update inventory - lore
						updatePlayerInventory();
					}
				}
				else
				{
					//informations about the item some1 wants to buy
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'));
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
		//Check the item in cursor if its marked
		
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
						
						//send event
						transactionEvent(TransactionResult.TRADER_LACKS_MONEY);
					}
					else
					{
						//send event
						transactionEvent(TransactionResult.SUCCESS_PLAYER_SELL);
						
						//remove the amount from inventory
						removeFromInventory(slot, scale);

						//send the transaction success message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#sold", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()*scale), 
								"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())*scale).replace(',', '.'));
						
						//update the inventory lore
						updatePlayerInventory();
					}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())*scale).replace(',', '.'));
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
						
						//send event
						transactionEvent(TransactionResult.TRADER_LACKS_MONEY);
					}
					else
					{
						//send event
						transactionEvent(TransactionResult.SUCCESS_PLAYER_SELL);
						
						//remove the amount from inventory
						removeFromInventory(slot);
						
						//send the transaction success message
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#sold", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), 
								"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())).replace(',', '.'));

						//update the inventory lore
						updatePlayerInventory();
					}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())).replace(',', '.'));
				}
			}
		}
	}
	
	
	
	/* manager mode handlers */
	@ClickHandler(status={tNpcStatus.MANAGE_UNLOCKED}, inventory=InventoryType.TRADER)
	public void setStock(InventoryClickEvent e)
	{
		dB.info("Unlocked stock click event");
	}
	
	@ClickHandler(status={tNpcStatus.MANAGE_UNLOCKED}, inventory=InventoryType.PLAYER)
	public void getStock(InventoryClickEvent e)
	{
		dB.info("Unlocked stock click event");
	}

	@ClickHandler(status={tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY}, inventory=InventoryType.TRADER, shift = true)
	public void itemsAttribs(InventoryClickEvent e)
	{
		//debug info
		dB.info("Item managing click event");
		
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
				else //if it's a no shift rightclick
				{
					if ( getSelectedItem().hasFlag(NoStack.class) )
						getSelectedItem().removeFlag(NoStack.class);
					else
						getSelectedItem().addFlag(".nostack");
					
					locale.sendMessage(player, "key-change", 
							"key", "#stack-disable", 
							"value", String.valueOf(getSelectedItem().hasFlag(NoStack.class)));
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
		dB.info("Price managing click event");
		
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
		dB.info("Inventory click, by: ", player.getName(), ", status: ", status.name().toLowerCase());
		dB.info("slot: ", e.getSlot(), ", left: ", e.isLeftClick(), ", shift: ", e.isShiftClick());
	}
	
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS, tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_AMOUNTS, tNpcStatus.MANAGE_PRICE, tNpcStatus.MANAGE_LIMIT}, shift = true, inventory = InventoryType.PLAYER)
	public void botDebug(InventoryClickEvent e)
	{
		//debug info
		dB.info("Inventory click, by: ", player.getName(), ", status: ", status.name().toLowerCase());
		dB.info("slot: ", e.getSlot(), ", left: ", e.isLeftClick(), ", shift: ", e.isShiftClick());
	}

	
	@SuppressWarnings("deprecation")
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.PLAYER)
	public void __last(InventoryClickEvent e)
	{
		//Temporary fix for Touchscreen issue!
		if ( e.isCancelled() )
		{
			e.setCurrentItem(e.getCurrentItem());
			//This should be fixed soon! 
			((Player)e.getWhoClicked()).updateInventory();
		}
	}
}
