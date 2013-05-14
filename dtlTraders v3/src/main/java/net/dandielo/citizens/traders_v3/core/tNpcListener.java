package net.dandielo.citizens.traders_v3.core;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class tNpcListener implements Listener {

	tNpcManager manager = tNpcManager.instance();

	//general events
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent e)
	{
	}

	//npc events
	@EventHandler
	public void npcClickEvent(NPCClickEvent e)
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
			DtlTraders.severe("Trader type does not exists, did you removed an extension?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			DtlTraders.severe("Trader type is invalid, is this type up to date?");
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
			DtlTraders.severe("Trader type does not exists, did you removed an extension?");
		} 
		catch (InvalidTraderTypeException e1) 
		{
			DtlTraders.severe("Trader type is invalid, is this type up to date?");
		}
	}

}
