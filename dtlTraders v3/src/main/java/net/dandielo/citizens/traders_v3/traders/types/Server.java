package net.dandielo.citizens.traders_v3.traders.types;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
	public void onLeftClick() {
		if ( status.inManagementMode() )
			status = getDefaultStatus();
		else
			status = getDefaultManagementStatus();
	}

	@Override
	public void onRightClick()
	{
		if ( status.inManagementMode() )
			inventory = stock.getManagementInventory(status);
		else
			inventory = stock.getInventory(status);
		player.openInventory(inventory);
	}

	@ClickHandler(status = {Status.SELL, Status.BUY}, inventory = InventoryType.TRADER)
	public void uiChanges(InventoryClickEvent e)
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
	
	@ClickHandler(status = {Status.SELL}, inventory = InventoryType.TRADER)
	public void sellTopClick(InventoryClickEvent e)
	{
		int slot = e.getSlot();
		if ( stock.isUiSlot(slot) ) return;

		if ( e.isLeftClick() )
		{
			if ( stock.getItem(slot, "sell") != null )
			if ( handleClick(e.getRawSlot()) )
			{
				player.sendMessage("L" + stock.getItem(slot, "sell").<Double>getData("p").toString());
			}
			else
			{
				player.sendMessage("L" + stock.getItem(slot, "sell").toString());
			}
		}
		else
		{
			if ( stock.getItem(slot, "sell") != null )
			if ( handleClick(e.getRawSlot()) )
			{
				player.sendMessage("R" + stock.getItem(slot, "sell").<Double>getData("p").toString());
			}
			else
			{
				player.sendMessage("R" + stock.getItem(slot, "sell").toString());
			}
		}
		e.setCancelled(true);
	}
	
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

	@Override
	public void onManageInventoryClick(InventoryClickEvent e) {
	}

}
