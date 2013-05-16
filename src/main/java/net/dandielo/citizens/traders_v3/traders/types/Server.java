package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.TraderType;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

@TraderType(name="server", author="dandielo")
public class Server extends Trader {

	public Server(TraderTrait trader, WalletTrait wallet, Player player) {
		super(trader, wallet, player);
	}

	@Override
	public void onLeftClick()
	{
		//debug info
		Debugger.info(this.getClass().getSimpleName(), " Trader left click event, by: ", player.getName());
		
		if ( status.inManagementMode() )
		{
			locale.sendMessage(player, "trader-managermode-disabled", "npc", getNPC().getName());
			status = getDefaultStatus();
		}
		else
		{
			locale.sendMessage(player, "trader-managermode-enabled", "npc", getNPC().getName());
			parseStatus(getDefaultManagementStatus());
		}
	}

	@Override
	public void onRightClick()
	{
		//debug info
		Debugger.info(this.getClass().getSimpleName(), " Trader right click event, by: ", player.getName());
		
		if ( status.inManagementMode() )
			inventory = stock.getManagementInventory(baseStatus, status);
		else
			inventory = stock.getInventory(status);
		player.openInventory(inventory);
	}

	@ClickHandler(status = {Status.SELL, Status.BUY, Status.SELL_AMOUNTS}, inventory = InventoryType.TRADER)
	public void generalUI(InventoryClickEvent e)
	{
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			if ( hitTest(slot, "buy") )
			{
				status = Status.BUY;
			}
			else
			if ( hitTest(slot, "sell") )
			{
				status = Status.SELL;
			}
			else
			if ( hitTest(slot, "back") )
			{
				status = Status.SELL;
			}
			stock.setInventory(inventory, getStatus());
		}
		e.setCancelled(true);
	}
	
	@ClickHandler(
	status = {Status.MANAGE_SELL, Status.MANAGE_BUY, Status.MANAGE_AMOUNTS, Status.MANAGE_PRICE, Status.MANAGE_LIMITS}, 
	inventory = InventoryType.TRADER)
	public void manageUI(InventoryClickEvent e)
	{		
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) )
		{
			if ( hitTest(slot, "buy") )
			{
				parseStatus(Status.MANAGE_BUY);
			}
			else
			if ( hitTest(slot, "sell") )
			{
				parseStatus(Status.MANAGE_SELL);
			}
			else
			if ( hitTest(slot, "back") )
			{
					parseStatus(baseStatus);
			}
			else
			if ( hitTest(slot, "price") )
			{
				parseStatus(Status.MANAGE_PRICE);
			}
			else
			if ( hitTest(slot, "limit") )
			{
				parseStatus(Status.MANAGE_LIMITS);
			}
			stock.setManagementInventory(inventory, baseStatus, status);
		}
		e.setCancelled(true);
	}
	
	@ClickHandler(status = {Status.SELL}, inventory = InventoryType.TRADER)
	public void sellItems(InventoryClickEvent e)
	{
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) ) return;

		if ( e.isLeftClick() )
		{
			if ( selectAndCheckItem(slot) )
			{
				if ( getSelectedItem().hasMultipleAmounts() )
				{
					status = Status.SELL_AMOUNTS;
					stock.setAmountsInventory(inventory, getSelectedItem());
				}
				else
				if ( handleClick(e.getRawSlot()) )
				{
					if ( !inventoryHasPlace() )
					{
						
						//temp
						Debugger.low("Player has no space to buy this item");
					}
					else
					if ( !sellTransaction() )
					{
						
						//temp
						Debugger.low("Player has no space to buy this item");
					}
					else
					{
						addToInventory();
						
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(getSelectedItem().getPrice()));
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
						
						//temp
						Debugger.low("Player has no space to buy this item");
					}
					else
					if ( !sellTransaction() )
					{
						
						//temp
						Debugger.low("Player has no space to buy this item");
					}
					else
					{
						addToInventory();
						
						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(getSelectedItem().getPrice()));
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
		e.setCancelled(true);
	}
	
	@ClickHandler(status = {Status.SELL_AMOUNTS}, inventory = InventoryType.TRADER)
	public void sellAmountsItems(InventoryClickEvent e)
	{
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) ) return;

		if ( checkItemAmount(slot) )
		{
			if ( handleClick(e.getRawSlot()) )
			{
				if ( !inventoryHasPlace(slot) )
				{

					//temp
					Debugger.low("Player has no space to buy this item");
				}
				else
					if ( !sellTransaction(slot) )
					{

						//temp
						Debugger.low("Player has no space to buy this item");
					}
					else
					{
						addToInventory(slot);

						locale.sendMessage(player, "trader-transaction-success", "trader", getNPC().getName(),
								"player", player.getName(), "action", "#bought", "item", getSelectedItem().getName(),
								"amount", String.valueOf(getSelectedItem().getAmount()), "price", String.valueOf(stock.parsePrice(getSelectedItem(), slot)));
					}
			}
			else
			{
				//informations about the item some1 wants to buy
				locale.sendMessage(player, "trader-transaction-item",
						"item", getSelectedItem().getName(), "amount", String.valueOf(getSelectedItem().getAmount()), 
						"price", String.valueOf(stock.parsePrice(getSelectedItem(), slot)));
			}
		}
		e.setCancelled(true);
	}
	
	@ClickHandler(status = {Status.SELL, Status.BUY}, inventory = InventoryType.PLAYER)
	public void buyItems(InventoryClickEvent e)
	{
	//	int slot = e.getSlot();
		if ( e.isLeftClick() )
		{
		/*	if ( stock.getItem(slot, "sell") != null )
			if ( handleClick(e.getRawSlot()) )
			{
				player.sendMessage("L" + stock.getItem(slot, "sell").<Double>getData("p").toString());
			}
			else
			{
				player.sendMessage("L" + stock.getItem(slot, "sell").toString());
			}*/
		}
		else
		{
		/*	if ( stock.getItem(slot, "sell") != null )
			if ( handleClick(e.getRawSlot()) )
			{
				player.sendMessage("R" + stock.getItem(slot, "sell").<Double>getData("p").toString());
			}
			else
			{
				player.sendMessage("R" + stock.getItem(slot, "sell").toString());
			}*/
		}
		e.setCancelled(true);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//shift handler
	@ClickHandler(status = {Status.SELL, Status.BUY, Status.SELL_AMOUNTS}, shift = true, inventory = InventoryType.TRADER)
	public void topShift(InventoryClickEvent e)
	{
		if ( e.isShiftClick() )
		    e.setCancelled(true);
	}
	
	@ClickHandler(status = {Status.SELL, Status.BUY, Status.SELL_AMOUNTS}, shift = true, inventory = InventoryType.PLAYER)
	public void botShift(InventoryClickEvent e)
	{
		if ( e.isShiftClick() )
		    e.setCancelled(true);
	}
	
	@ClickHandler(status = {Status.SELL, Status.BUY, Status.SELL_AMOUNTS, Status.MANAGE_SELL, Status.MANAGE_BUY, Status.MANAGE_AMOUNTS, Status.MANAGE_PRICE, Status.MANAGE_LIMITS}, shift = true, inventory = InventoryType.TRADER)
	public void topDebug(InventoryClickEvent e)
	{
		//debug info
		Debugger.info("Inventory click, by: ", player.getName(), ", status: ", status.name().toLowerCase());
		Debugger.info("slot: ", e.getSlot(), ", left: ", e.isLeftClick(), ", shift: ", e.isShiftClick());
	}
	
	@ClickHandler(status = {Status.SELL, Status.BUY, Status.SELL_AMOUNTS, Status.MANAGE_SELL, Status.MANAGE_BUY, Status.MANAGE_AMOUNTS, Status.MANAGE_PRICE, Status.MANAGE_LIMITS}, shift = true, inventory = InventoryType.PLAYER)
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
				status = Status.BUY;
			}
			else
			if ( hitTest(slot, "sell") )
			{
				status = Status.SELL;
			}
			else
			if ( hitTest(slot, "back") )
			{
				status = Status.SELL;
			}
			stock.setInventory(inventory, getStatus());
		}
		else
		{
			if ( status.equals(Status.SELL) )
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
			if ( status.equals(Status.SELL_AMOUNTS) )
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
			if ( status.equals(Status.BUY) )
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
