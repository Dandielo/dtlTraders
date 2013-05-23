package net.dandielo.citizens.traders_v3;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
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
	/**
	 * Permissions manager instance
	 */
	Perms perms = Perms.perms;
	
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
		Trader trader = manager.getTraderRelation(e.getWhoClicked());

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
		Trader trader = manager.getTraderRelation(e.getPlayer());
		if ( trader != null && !trader.getStatus().inManagementMode() )
		{			
			//remove the relation
			manager.removeRelation((Player) e.getPlayer());
		}
	}

	//npc events
	@EventHandler
	public void npcLeftClickEvent(NPCLeftClickEvent e)
	{
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;
		
		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") ) return;

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader = null;
		try 
		{

		    if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
				if ( !trader.equals(e.getNPC()) )
				{
					manager.removeRelation(e.getClicker());
					trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
					manager.registerRelation(e.getClicker(), trader);
				}
			}

			trader.onLeftClick(e.getClicker().getItemInHand());
			
			if ( !trader.getStatus().inManagementMode() )
				manager.removeRelation(e.getClicker());
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
		//check trait
		if ( !e.getNPC().hasTrait(TraderTrait.class) ) return;

		//check permission
		if ( !perms.has(e.getClicker(), "dtl.trader.use") ) return;
		

		TraderTrait traderTrait = e.getNPC().getTrait(TraderTrait.class);
		Trader trader = null;
		try 
		{
			if ( !manager.inRelation(e.getClicker()) )
			{
				trader = (Trader) tNpcManager.create_tNpc(e.getNPC(), traderTrait.getType(), e.getClicker());
				manager.registerRelation(e.getClicker(), trader);
			}
			else
			{
				trader = manager.getTraderRelation(e.getClicker());
			}

			if ( !trader.onRightClick(e.getClicker().getItemInHand()) )
				//check the mode 
				if ( !trader.getStatus().inManagementMode() )
					manager.removeRelation(e.getClicker());
			
			
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
