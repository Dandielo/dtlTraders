package net.dandielo.citizens.traders_v3;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.Debugger;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class tNpcListener implements Listener {
	private static tNpcListener instance = new tNpcListener();
	
	public static tNpcListener instance()
	{
		return instance;
	}
	
	//class definition
	tNpcManager manager = tNpcManager.instance();

	public tNpcListener()
	{
	}
	
	//general events
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e)
	{
		Trader trader = manager.getTransactionTrader((Player)e.getWhoClicked());

		if ( trader != null )
		{
			if ( trader.getStatus().inManagementMode() )
				trader.onManageInventoryClick(e);
			else
			    trader.onInventoryClick(e);
		}
	}
	
	@EventHandler
	public void inventoryOpenEvent(InventoryOpenEvent e)
	{
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e)
	{
	}

	//npc events
	@EventHandler
	public void npcLeftClickEvent(NPCLeftClickEvent e)
	{
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;
		
		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader = null;
		try 
		{
		    if ( !manager.inTransaction(e.getClicker()) )
			{
				trader = tNpcManager.createTarder(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.openTransaction(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTransactionTrader(e.getClicker());
				if ( !trader.equals(e.getNPC()) )
				{
					manager.closeTransaction(e.getClicker());
					trader = tNpcManager.createTarder(e.getNPC(), traderTrait.getType(), e.getClicker());
					manager.openTransaction(e.getClicker(), trader);
				}
			}
			
			trader.onLeftClick();
			
			if ( !trader.getStatus().inManagementMode() )
				manager.closeTransaction(e.getClicker());
		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			Debugger.critical("Trader type was not found, type: ", traderTrait.getType());
			Debugger.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			Debugger.critical("Trader type is invalid, type: ", traderTrait.getType());
			Debugger.critical("Contact the dev to fix this!");
		}
	}

	@EventHandler
	public void npcRightClickEvent(NPCRightClickEvent e) 
	{
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader = null;
		try 
		{
			if ( !manager.inTransaction(e.getClicker()) )
			{
				trader = tNpcManager.createTarder(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.openTransaction(e.getClicker(), trader);
			}
			else
				trader = manager.getTransactionTrader(e.getClicker());
			
			trader.onRightClick();
		}
		catch (TraderTypeNotFoundException e1) 
		{
			//debug critical
			Debugger.critical("Trader type was not found, type: ", traderTrait.getType());
			Debugger.critical("Did you changed the save file?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			//debug critical
			Debugger.critical("Trader type is invalid, type: ", traderTrait.getType());
			Debugger.critical("Contact the dev to fix this!");
		}
	}

}
