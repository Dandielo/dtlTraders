package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcStatus;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.bukkit.Econ.econ;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderClickEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderOpenEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent.TransactionResult;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.NBTUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
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
		TraderClickEvent e = (TraderClickEvent) new TraderClickEvent(this, player, !GlobalSettings.mmRightToggle(), true).callEvent();

		//check settings
		if ( !e.isManagerToggling() ) return;

		//check permission
		if ( !perms.has(player, "dtl.trader.manage") ) return;

		//if air every item in hand is valid
		ItemStack itemToToggle = GlobalSettings.mmItemToggle();
		if ( itemInHand != null && !itemToToggle.getType().equals(Material.AIR) )
		{
			//if id are different then cancel the event
			if ( !itemToToggle.getType().equals(itemInHand.getType()) ) return;
		}

		toggleManageMode("left");
	}

	@Override
	public boolean onRightClick(ItemStack itemInHand)
	{
		dB.info("-------------------------------------");
		dB.info("Trader right click");
		dB.info("-------------------------------------");
		
		//right click toggling is enabled, handle it and check permission
		if ( GlobalSettings.mmRightToggle() && perms.has(player, "dtl.trader.manage") )
		{
			//if air then chane to stick item
			ItemStack itemToToggle = GlobalSettings.mmItemToggle();
			if ( itemToToggle.getType().equals(Material.AIR) )
				itemToToggle.setType(Material.STICK);

			//if id's in hand and for toggling are the same manage the mode change
			if ( itemInHand != null && itemToToggle.getType().equals(itemInHand.getType()) ) 
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

				//send a event without toggling
				new TraderClickEvent(this, player, false, false).callEvent();
			}
		}
		else
		{
			//send a event without toggling
			new TraderClickEvent(this, player, false, false).callEvent();
		}

		//debug info
		dB.info(this.getClass().getSimpleName(), " Trader right click event, by: ", player.getName());

		//update all limits
		limits.refreshAll();

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

		TraderOpenEvent event = new TraderOpenEvent(this, player);
		event.callEvent();

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
			//Update the inventory on EACH UI CLICK
			stock.setInventory(inventory, getStatus());
		}
		e.setCancelled(true);
	}

	@ClickHandler(
	status = {tNpcStatus.MANAGE_SELL, tNpcStatus.MANAGE_BUY, tNpcStatus.MANAGE_UNLOCKED, tNpcStatus.MANAGE_AMOUNTS, tNpcStatus.MANAGE_PRICE, tNpcStatus.MANAGE_LIMIT, tNpcStatus.MANAGE_PLIMIT}, 
	inventory = InventoryType.TRADER)
	@SuppressWarnings("static-access")
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
			if ( hitTest(slot, "plimit") )
			{
				//send message
				locale.sendMessage(player, "trader-managermode-toggled", "mode", "#plimit");

				//change status
				parseStatus(tNpcStatus.MANAGE_PLIMIT);
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
			setSpecialBlockValues();
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
				if ( !checkSellLimits(slot) )
				{
					//send message
					locale.sendMessage(player, "trader-transaction-failed-limit-reached");

					//send event
					transactionEvent(TransactionResult.LIMIT_REACHED);
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
							"amount", String.valueOf(getSelectedItem().getAmount(slot))/*, "price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount(slot))).replace(',', '.')*/);

					//update limits
					updateSellLimits(slot);

					//update inventory - lore
					updatePlayerInventory();
				}
			}
			else
			{
				//informations about the item some1 wants to buy
				locale.sendMessage(player, "trader-transaction-item",
						"price", String.format("%.2f", econ.getBalance() - stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount(slot))).replace(',', '.'), "action", "#buy", "item", getSelectedItem().getName()/*, 
						"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount(slot))).replace(',', '.')*/);
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
							if ( !checkSellLimits() )
							{
								//send message
								locale.sendMessage(player, "trader-transaction-failed-limit-reached");

								//send event
								transactionEvent(TransactionResult.LIMIT_REACHED);
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
											"amount", String.valueOf(getSelectedItem().getAmount())/*, "price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.')*/);

									//update limits
									updateSellLimits();

									//update inventory - lore
									updatePlayerInventory();
								}
					}
					else
					{
						//informations about the item some1 wants to buy
						locale.sendMessage(player, "trader-transaction-item",
								"price", String.format("%.2f", econ.getBalance() - stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'), "action", "#buy", "item", getSelectedItem().getName()/*, 
								"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.')*/);
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
						if ( !checkSellLimits() )
						{
							//send message
							locale.sendMessage(player, "trader-transaction-failed-limit-reached");

							//send event
							transactionEvent(TransactionResult.LIMIT_REACHED);
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
										"amount", String.valueOf(getSelectedItem().getAmount())/*, 
										"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.')*/);

								//update limits
								updateSellLimits();

								//update inventory - lore
								updatePlayerInventory();
							}
				}
				else
				{
					//informations about the item some1 wants to buy
					locale.sendMessage(player, "trader-transaction-item",
							"price", String.format("%.2f", econ.getBalance() - stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.'), "action", "#buy", "item", getSelectedItem().getName()/*, 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "sell", getSelectedItem().getAmount())).replace(',', '.')*/);
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
					if ( !checkBuyLimits(scale) )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-limit-reached");

						//send event
						transactionEvent(TransactionResult.LIMIT_REACHED);
					}
					else
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
									"amount", String.valueOf(getSelectedItem().getAmount()*scale)/*, 
									"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())*scale).replace(',', '.')*/);

							updateBuyLimits(scale);

							//update the inventory lore
							updatePlayerInventory();
						}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"price", String.format("%.2f", econ.getBalance() + stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())*scale).replace(',', '.'), "action", "#sell", "item", getSelectedItem().getName()/*, 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())*scale).replace(',', '.')*/);
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
					if ( !checkBuyLimits() )
					{
						//send message
						locale.sendMessage(player, "trader-transaction-failed-limit-reached");

						//send event
						transactionEvent(TransactionResult.LIMIT_REACHED);
					}
					else
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
									"amount", String.valueOf(getSelectedItem().getAmount())/*, 
									"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())).replace(',', '.')*/);

							//update limits
							updateBuyLimits();

							//update the inventory lore
							updatePlayerInventory();
						}
				}
				else
				{
					//send the information message
					locale.sendMessage(player, "trader-transaction-item",
							"price", String.format("%.2f", econ.getBalance() + stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())).replace(',', '.'), "action", "#sell", "item", getSelectedItem().getName()/*, 
							"price", String.format("%.2f", stock.parsePrice(getSelectedItem(), "buy", getSelectedItem().getAmount())).replace(',', '.')*/);
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
	public void itemAttribs(InventoryClickEvent e)
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

					locale.sendMessage(player, "trader-managermode-toggled", "mode", "#amount");
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
							"value", locale.getKeyword(String.valueOf(getSelectedItem().hasFlag(StackPrice.class))));
				}
				else //if it's a no shift rightclick
				{
					/* Unused feedback confuses players
					if ( getSelectedItem().hasFlag(NoStack.class) )
						getSelectedItem().removeFlag(NoStack.class);
					else
						getSelectedItem().addFlag(".nostack");

					locale.sendMessage(player, "key-change", 
							"key", "#stack-disable", 
							"value", locale.getKeyword(String.valueOf(getSelectedItem().hasFlag(NoStack.class)));*/
				}

				// Update the item with new Price

				/* Do not show the price in the general Management Tab! It's not to show prices
				 * Ok it might be quite anoying to always check prices entering the price management
				 * But every tab has its own purpose dont mix it!
				 * 
                StockItem item = getSelectedItem();
                ItemStack itemStack = item.getItem(false);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setLore(Price.loreRequest(stock.parsePrice(item, status.asStock(), item.getAmount()), item.getTempLore(status, itemStack.clone()), status));
                itemStack.setItemMeta(meta); 
                e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));*/
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
			//get the selected item
			StockItem item = getSelectedItem();

			//show the current price in chat, if cursor is AIR
			if ( e.getCursor().getType().equals(Material.AIR) )
			{
				//sends the message
				locale.sendMessage(player, "key-value", 
						"key", "#price", "value", item.getPriceFormated());
			}
			else
			{
				//adds the price attribute to the item
				Price price = item.getPriceAttr();

				//adds value to the current price
				if ( e.isLeftClick() )
				{
					//increases the price using specialBlockValue*cursorAmount
					price.increase(Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

					//sends a message
					locale.sendMessage(player, "key-change", 
							"key", "#price", "value", item.getPriceFormated());
				}
				else
					//remove value from the current price
					if ( e.isRightClick() )
					{
						//decreases the price using specialBlockValue*cursorAmount
						price.decrease(Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

						//sends a message
						locale.sendMessage(player, "key-change", 
								"key", "#price", "value", item.getPriceFormated());
					}

				//Get a clean item and it's meta
	            ItemStack itemStack = item.getItem(false, item.getTempLore(status));
	            
	            //replace the item with that one in the inventory
	            e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));
			}
		}
		e.setCancelled(true);
	}

	/**
	 * Limit managing for manager stock, this allows you to change limits for all items in your traders stock
	 * @param e
	 */
	@ClickHandler(status={tNpcStatus.MANAGE_LIMIT}, inventory=InventoryType.TRADER, shift = true)
	public void manageLimits(InventoryClickEvent e)
	{
		//debug info
		dB.info("Limit managing click event");

		//select the item that should have the price changed
		if ( selectAndCheckItem(e.getSlot()) )
		{
			//get the selected item
			StockItem item = getSelectedItem();

			//get the limit attribute
			if (!item.hasAttr(Limit.class))
				item.addAttr("l", this.settings.getNPC().getId() + "@" + baseStatus.asStock() + ":" + item.getSlot() + "/0/0s");
			Limit limit = item.getAttr(Limit.class);

			//show the current price in chat, if cursor is AIR
			if ( e.getCursor().getType().equals(Material.AIR) )
			{
				//sends the message
				locale.sendMessage(player, "key-value", 
						"key", "#limit", "value", limit.getLimit() != 0 ? String.valueOf(limit.getLimit()) : "none");
				locale.sendMessage(player, "key-value", 
						"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout()));
			}
			else
			{
				//change the limit
				if ( !e.isShiftClick() )
				{
					//adds value to the current price
					if ( e.isLeftClick() )
					{
						//increases the price using specialBlockValue*cursorAmount
						limit.increaseLimit((int)Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

						//sends a message
						locale.sendMessage(player, "key-change", 
								"key", "#limit", "value", limit.getLimit() != 0 ? String.valueOf(limit.getLimit()) : "none");
					}
					else
						//remove value from the current price
						if ( e.isRightClick() )
						{
							//decreases the price using specialBlockValue*cursorAmount
							limit.decreaseLimit((int)Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

							//sends a message
							locale.sendMessage(player, "key-change", 
									"key", "#limit", "value", limit.getLimit() != 0 ? String.valueOf(limit.getLimit()) : "none");
						}
				}
				//change the timeout
				else
				{
					//adds value to the current price
					if ( e.isLeftClick() )
					{
						//increases the price using specialBlockValue*cursorAmount
						limit.increaseTimeout(Settings.getBlockTimeoutValue(e.getCursor())*e.getCursor().getAmount());

						//sends a message
						locale.sendMessage(player, "key-change", 
								"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout()));
					}
					else
						//remove value from the current price
						if ( e.isRightClick() )
						{
							//decreases the price using specialBlockValue*cursorAmount
							limit.decreaseTimeout(Settings.getBlockTimeoutValue(e.getCursor())*e.getCursor().getAmount());

							//sends a message
							locale.sendMessage(player, "key-change", 
									"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout()));
						}
				}

				//Get a clean item and it's meta
				ItemStack itemStack = item.getItem(false, item.getTempLore(status));
				//replace the item with that one in the inventory
				e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));
			}

			//remove the attribute if not needed
			if (limit.getLimit() == 0 && limit.getPlayerLimit() == 0)
			{
				item.removeAttr(Limit.class);
			}
		}
		e.setCancelled(true);
	}

	/**
	 * Limit managing for manager stock, this allows you to change limits for all items in your traders stock
	 * @param e
	 */
	@ClickHandler(status={tNpcStatus.MANAGE_PLIMIT}, inventory=InventoryType.TRADER, shift = true)
	public void managePlayerLimits(InventoryClickEvent e)
	{
		//debug info
		dB.info("Limit managing click event");

		//select the item that should have the price changed
		if ( selectAndCheckItem(e.getSlot()) )
		{
			//get the selected item
			StockItem item = getSelectedItem();

			//get the limit attribute
			if (!item.hasAttr(Limit.class))
				item.addAttr("l", this.settings.getNPC().getId() + "@" + baseStatus.asStock() + ":" + item.getSlot() + "/0/0s");
			Limit limit = item.getAttr(Limit.class);

			//show the current price in chat, if cursor is AIR
			if ( e.getCursor().getType().equals(Material.AIR) )
			{
				//sends the message
				locale.sendMessage(player, "key-value", 
						"key", "#limit", "value", limit.getPlayerLimit() != 0 ? String.valueOf(limit.getPlayerLimit()) : "none");
				locale.sendMessage(player, "key-value", 
						"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout()));
			}
			else
			{
				//change the limit
				if ( !e.isShiftClick() )
				{
					//adds value to the current price
					if ( e.isLeftClick() )
					{
						//increases the price using specialBlockValue*cursorAmount
						limit.increasePlayerLimit((int)Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

						//sends a message
						locale.sendMessage(player, "key-change", 
								"key", "#limit", "value", limit.getPlayerLimit() != 0 ? String.valueOf(limit.getPlayerLimit()) : "none");
					}
					else
						//remove value from the current price
						if ( e.isRightClick() )
						{
							//decreases the price using specialBlockValue*cursorAmount
							limit.decreasePlayerLimit((int)Settings.getBlockValue(e.getCursor())*e.getCursor().getAmount());

							//sends a message
							locale.sendMessage(player, "key-change", 
									"key", "#limit", "value", limit.getPlayerLimit() != 0 ? String.valueOf(limit.getPlayerLimit()) : "none");
						}
				}
				//change the timeout
				else
				{
					//adds value to the current price
					if ( e.isLeftClick() )
					{
						//increases the price using specialBlockValue*cursorAmount
						limit.increasePlayerTimeout(Settings.getBlockTimeoutValue(e.getCursor())*e.getCursor().getAmount());

						//sends a message
						locale.sendMessage(player, "key-change", 
								"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout()));
					}
					else
						//remove value from the current price
						if ( e.isRightClick() )
						{
							//decreases the price using specialBlockValue*cursorAmount
							limit.decreasePlayerTimeout(Settings.getBlockTimeoutValue(e.getCursor())*e.getCursor().getAmount());

							//sends a message
							locale.sendMessage(player, "key-change", 
									"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout()));
						}
				}

				//Get a clean item and it's meta
				ItemStack itemStack = item.getItem(false, item.getTempLore(status));

				//replace the item with that one in the inventory
				e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));


				//remove the attribute if not needed
				if (limit.getLimit() == 0 && limit.getPlayerLimit() == 0)
				{
					item.removeAttr(Limit.class);
				}
			}
			e.setCancelled(true);
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

	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.TRADER)
	public void __topUpdate(InventoryClickEvent e)
	{
		limits.refreshAll();
		if (status.equals(tNpcStatus.SELL_AMOUNTS))
			stock.setAmountsInventory(inventory, status, getSelectedItem());
		else
			stock.setInventory(inventory, status);
	}
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.PLAYER)
	public void __bottomUpdate(InventoryClickEvent e)
	{
		limits.refreshAll();
		if (status.equals(tNpcStatus.SELL_AMOUNTS))
			stock.setAmountsInventory(inventory, status, getSelectedItem());
		else
			stock.setInventory(inventory, status);
	}

	@SuppressWarnings("deprecation")
	@ClickHandler(status = {tNpcStatus.SELL, tNpcStatus.BUY, tNpcStatus.SELL_AMOUNTS}, inventory = InventoryType.PLAYER)
	public void __last(InventoryClickEvent e)
	{
		//Temporary fix for Touchscreen issue!
		if ( e.isCancelled() )
		{
			//e.setCurrentItem(null);
			//e.setCurrentItem(e.getCurrentItem());
			//This should be fixed soon! 
			//e.getWhoClicked().
			((Player)e.getWhoClicked()).updateInventory();

			//update the trader inventory too
		}
	}
}
